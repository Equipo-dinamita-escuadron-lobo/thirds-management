package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.*;
import com.thirdsmanagement.thirds.domain.utils.ValidationUtils;
import com.thirdsmanagement.thirds.domain.utils.ErrorMappingUtils;
import com.thirdsmanagement.thirds.domain.utils.StringNormalizer;
import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio especializado en validaciones en lotes para importación de terceros.
 * Optimiza las validaciones pre-cargando datos de referencia y procesando en
 * lotes. *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchValidationService {

    private final IdOutputPort idOutputPort;
    private final GeographyOutputPort geographyOutputPort;
    private final ThirdValidationService thirdValidationService;

    /**
     * Valida un lote de datos de terceros de Excel.
     * 
     * @param thirdsData lista de datos de terceros a validar
     * @param entId      identificador de la entidad
     * @param columnMap  mapa de columnas del Excel para incluir números de columna
     *                   en errores
     * @return resultado de validación con errores y advertencias
     */
    public BatchValidationResult validateBatch(List<ThirdExcelData> thirdsData, String entId,
            Map<String, Integer> columnMap) {
        List<ImportErrorDetail> errors = new ArrayList<>();
        List<ThirdExcelData> validRecords = new ArrayList<>();

        // Pre-cargar datos de referencia como objetos inmutables locales
        ReferenceDataCache cache = preloadReferenceData(entId);

        for (ThirdExcelData excelData : thirdsData) {
            try {
                ValidationResult result = validateSingleRecord(excelData, cache, columnMap);

                errors.addAll(result.getErrors());

                if (result.isValid()) {
                    validRecords.add(excelData);
                }

            } catch (Exception e) {
                errors.add(ImportErrorDetail.builder()
                        .rowNumber(excelData.getRowNumber())
                        .errorCode("VALIDATION_SYSTEM_ERROR")
                        .errorMessage("Error del sistema validando registro: " + e.getMessage())
                        .errorType(ImportErrorType.SYSTEM_ERROR)
                        .build());
            }
        }

        return BatchValidationResult.builder()
                .validRecords(validRecords)
                .errors(errors)
                .totalProcessed(thirdsData.size())
                .validCount(validRecords.size())
                .errorCount(errors.size())
                .build();
    }

    /**
     * Pre-carga todos los datos de referencia para optimizar validaciones.
     * Retorna un objeto inmutable con todos los datos necesarios.
     */
    public ReferenceDataCache preloadReferenceData(String entId) {

        // Cargar tipos de identificación
        Map<String, TypeId> typeIds = idOutputPort.getAllTypeIds(entId).stream()
                .filter(typeId -> typeId.getStatus() != null && typeId.getStatus())
                .collect(Collectors.toMap(
                        typeId -> StringNormalizer.normalizeCode(typeId.getTypeIdname()),
                        typeId -> typeId,
                        (existing, replacement) -> existing));

        // Cargar tipos de tercero
        Map<String, ThirdType> thirdTypes = idOutputPort.getALLThirdTypes(entId).stream()
                .filter(thirdType -> thirdType.getStatus() != null && thirdType.getStatus())
                .collect(Collectors.toMap(
                        thirdType -> StringNormalizer.normalizeCode(thirdType.getThirdTypeName()),
                        thirdType -> thirdType,
                        (existing, replacement) -> existing));

        // Cargar geografía
        Map<String, Country> countries = geographyOutputPort.getAllActiveCountries().stream()
                .collect(Collectors.toMap(
                        country -> StringNormalizer.normalizeCode(country.getCountryName()),
                        country -> country,
                        (existing, replacement) -> existing));

        Map<String, State> states = geographyOutputPort.getStatesByCountry("COL").stream()
                .collect(Collectors.toMap(
                        state -> StringNormalizer.normalizeCode(state.getStateName()),
                        state -> state,
                        (existing, replacement) -> existing));

        Map<String, City> cities = new HashMap<>();
        for (State state : states.values()) {
            geographyOutputPort.getCitiesByState(state.getStateCode(), "COL").forEach(city -> {
                // construir la clave de búsqueda
                String key = StringNormalizer.normalizeCode(city.getCityName()) + "_" + state.getStateCode();
                cities.put(key, city);
            });
        }

        // Crear objeto inmutable con todos los datos
        ReferenceDataCache cache = new ReferenceDataCache(typeIds, thirdTypes, countries, states, cities);

        return cache;
    }

    /**
     * Valida un registro individual usando los datos pre-cargados.
     * Ejecuta TODAS las validaciones para mostrar todos los errores del registro.
     */
    private ValidationResult validateSingleRecord(ThirdExcelData excelData, ReferenceDataCache cache,
            Map<String, Integer> columnMap) {
        List<ImportErrorDetail> errors = new ArrayList<>();

        // 1. Validaciones básicas de formato y campos requeridos
        validateBasicFields(excelData, errors, columnMap);

        // 2. Validaciones de referencias (tipos, geografía)
        validateReferences(excelData, errors, cache, columnMap);

        // 3. Validaciones de reglas de negocio (SIEMPRE ejecutar para mostrar todos los errores)
        // Solo omitir si faltan datos críticos para construir el objeto Third
        if (canBuildThirdObject(excelData)) {
            validateBusinessRules(excelData, errors, cache, columnMap);
        }

        return ValidationResult.builder()
                .valid(errors.isEmpty())
                .errors(errors)
                .build();
    }

    /**
     * Valida campos básicos y formato.
     */
    private void validateBasicFields(ThirdExcelData excelData, List<ImportErrorDetail> errors,
            Map<String, Integer> columnMap) {
        // Validar campos básicos comunes
        validateCommonRequiredFields(excelData, errors, columnMap);
        
        // Validar campos específicos por tipo de persona
        if (excelData.getPersonType() != null) {
            if (excelData.getPersonType().isNatural()) {
                validateNaturalPersonFields(excelData, errors, columnMap);
            } else if (excelData.getPersonType().isJuridica()) {
                validateLegalEntityFields(excelData, errors, columnMap);
            }
        }
        
        // Validar geografía completa obligatoria
        validateCompleteGeographyRequired(excelData, errors, columnMap);

        // Validar formatos usando ValidationUtils centralizado
        validateFieldFormats(excelData, errors, columnMap);
    }

    /**
     * Valida campos requeridos comunes para todos los tipos de persona.
     */
    private void validateCommonRequiredFields(ThirdExcelData excelData, List<ImportErrorDetail> errors,
            Map<String, Integer> columnMap) {
        if (!ValidationUtils.hasContent(excelData.getTypeIdName())) {
            errors.add(ErrorMappingUtils.createRequiredFieldError(
                    excelData.getRowNumber(), "Tipo Identificación", columnMap));
        }

        if (excelData.getIdNumber() == null) {
            errors.add(ErrorMappingUtils.createRequiredFieldError(
                    excelData.getRowNumber(), "Número Identificación", columnMap));
        }

        if (excelData.getPersonType() == null) {
            errors.add(ErrorMappingUtils.createRequiredFieldError(
                    excelData.getRowNumber(), "Tipo Persona", columnMap));
        }

        // Campos de contacto requeridos para todos
        if (!ValidationUtils.hasContent(excelData.getAddress())) {
            errors.add(ErrorMappingUtils.createRequiredFieldError(
                    excelData.getRowNumber(), "Dirección", columnMap));
        }

        if (!ValidationUtils.hasContent(excelData.getPhoneNumber())) {
            errors.add(ErrorMappingUtils.createRequiredFieldError(
                    excelData.getRowNumber(), "Teléfono", columnMap));
        }

        if (!ValidationUtils.hasContent(excelData.getEmail())) {
            errors.add(ErrorMappingUtils.createRequiredFieldError(
                    excelData.getRowNumber(), "Email", columnMap));
        }

       
        if (excelData.getThirdTypesNames() == null || excelData.getThirdTypesNames().isEmpty()) {
            errors.add(ErrorMappingUtils.createRequiredFieldError(
                    excelData.getRowNumber(), "Tipos de Tercero", columnMap));
        }
    }

    /**
     * Valida campos específicos requeridos para personas naturales.
     */
    private void validateNaturalPersonFields(ThirdExcelData excelData, List<ImportErrorDetail> errors,
            Map<String, Integer> columnMap) {
        if (!ValidationUtils.hasContent(excelData.getNames())) {
            errors.add(ErrorMappingUtils.createRequiredFieldError(
                    excelData.getRowNumber(), "Nombres", columnMap));
        }

        if (!ValidationUtils.hasContent(excelData.getLastNames())) {
            errors.add(ErrorMappingUtils.createRequiredFieldError(
                    excelData.getRowNumber(), "Apellidos", columnMap));
        }

        if (excelData.getGender() == null) {
            errors.add(ErrorMappingUtils.createRequiredFieldError(
                    excelData.getRowNumber(), "Género", columnMap));
        }
    }

    /**
     * Valida campos específicos requeridos para personas jurídicas.
     */
    private void validateLegalEntityFields(ThirdExcelData excelData, List<ImportErrorDetail> errors,
            Map<String, Integer> columnMap) {
        if (!ValidationUtils.hasContent(excelData.getSocialReason())) {
            errors.add(ErrorMappingUtils.createRequiredFieldError(
                    excelData.getRowNumber(), "Razón Social", columnMap));
        }
    }

    /**
     * Valida que la geografía completa sea obligatoria (país, departamento, ciudad).
     */
    private void validateCompleteGeographyRequired(ThirdExcelData excelData, List<ImportErrorDetail> errors,
            Map<String, Integer> columnMap) {
        if (!ValidationUtils.hasContent(excelData.getCountryName())) {
            errors.add(ErrorMappingUtils.createRequiredFieldError(
                    excelData.getRowNumber(), "País", columnMap));
        }

        if (!ValidationUtils.hasContent(excelData.getStateName())) {
            errors.add(ErrorMappingUtils.createRequiredFieldError(
                    excelData.getRowNumber(), "Departamento", columnMap));
        }

        if (!ValidationUtils.hasContent(excelData.getCityName())) {
            errors.add(ErrorMappingUtils.createRequiredFieldError(
                    excelData.getRowNumber(), "Ciudad", columnMap));
        }
    }

    /**
     * Valida formatos de campos.
     */
    private void validateFieldFormats(ThirdExcelData excelData, List<ImportErrorDetail> errors,
            Map<String, Integer> columnMap) {
        if (ValidationUtils.hasContent(excelData.getEmail()) && !ValidationUtils.isValidEmail(excelData.getEmail())) {
            errors.add(ErrorMappingUtils.createInvalidEmailError(
                    excelData.getRowNumber(), excelData.getEmail(), columnMap));
        }

        if (ValidationUtils.hasContent(excelData.getPhoneNumber())
                && !ValidationUtils.isValidPhoneNumber(excelData.getPhoneNumber())) {
            errors.add(ErrorMappingUtils.createInvalidPhoneError(
                    excelData.getRowNumber(), excelData.getPhoneNumber(), columnMap));
        }
    }

    /**
     * Valida referencias a datos maestros.
     */
    private void validateReferences(ThirdExcelData excelData, List<ImportErrorDetail> errors, ReferenceDataCache cache,
            Map<String, Integer> columnMap) {
        // Validar tipo de identificación
        if (ValidationUtils.hasContent(excelData.getTypeIdName())) {
            if (!cache.hasTypeId(excelData.getTypeIdName())) {
                errors.add(ErrorMappingUtils.createInvalidReferenceError(
                        excelData.getRowNumber(),
                        "Tipo Identificación",
                        excelData.getTypeIdName(),
                        "tipo de identificación",
                        columnMap));
            }
        }

        // Validar tipos de tercero
        if (excelData.getThirdTypesNames() != null && !excelData.getThirdTypesNames().isEmpty()) {
            for (String typeName : excelData.getThirdTypesNames()) {
                if (!cache.hasThirdType(typeName)) {
                    errors.add(ErrorMappingUtils.createInvalidReferenceError(
                            excelData.getRowNumber(),
                            "Tipos de Tercero",
                            typeName,
                            "tipo de tercero",
                            columnMap));
                }
            }
        }

        // Validar geografía
        validateGeography(excelData, errors, cache, columnMap);
    }

    /**
     * Valida datos geográficos - jerarquía y existencia.
     * La completitud ya se valida en validateCompleteGeographyRequired().
     */
    private void validateGeography(ThirdExcelData excelData, List<ImportErrorDetail> errors, ReferenceDataCache cache,
            Map<String, Integer> columnMap) {
        boolean hasCountry = ValidationUtils.hasContent(excelData.getCountryName());
        boolean hasState = ValidationUtils.hasContent(excelData.getStateName());
        boolean hasCity = ValidationUtils.hasContent(excelData.getCityName());

        // Solo validar existencia si los campos están presentes
        
        if (hasCountry && !cache.hasCountry(excelData.getCountryName())) {
            errors.add(ErrorMappingUtils.createInvalidReferenceError(
                    excelData.getRowNumber(),
                    "País",
                    excelData.getCountryName(),
                    "país",
                    columnMap));
            return;
        }

        if (hasState && !cache.hasState(excelData.getStateName())) {
            errors.add(ErrorMappingUtils.createInvalidReferenceError(
                    excelData.getRowNumber(),
                    "Departamento",
                    excelData.getStateName(),
                    "departamento",
                    columnMap));
            return;
        }

        if (hasCity) {
            State state = cache.getState(excelData.getStateName());
            if (state != null && !cache.hasCity(excelData.getCityName(), state.getStateCode())) {
                errors.add(ErrorMappingUtils.createInvalidReferenceError(
                        excelData.getRowNumber(),
                        "Ciudad",
                        excelData.getCityName(),
                        "ciudad",
                        columnMap));
            }
        }
    }

    /**
     * Valida reglas de negocio reutilizando el servicio existente.
     * Captura CADA excepción individualmente para mostrar todos los errores.
     */
    private void validateBusinessRules(ThirdExcelData excelData, List<ImportErrorDetail> errors,
            ReferenceDataCache cache, Map<String, Integer> columnMap) {
        try {
            Third third = convertToThird(excelData, cache);

            // Validar consistencia de tipo de persona (nombres/apellidos vs razón social)
            try {
                thirdValidationService.validatePersonTypeConsistency(third);
            } catch (Exception e) {
                errors.add(ErrorMappingUtils.createBusinessRuleError(excelData, e, columnMap));
            }

            // Validar compatibilidad TypeId-PersonType
            try {
                thirdValidationService.validateTypeIdPersonTypeCompatibility(third);
            } catch (Exception e) {
                errors.add(ErrorMappingUtils.createBusinessRuleError(excelData, e, columnMap));
            }

            // Validar formato de NIT para personas jurídicas
            try {
                thirdValidationService.validateNitFormat(third);
            } catch (Exception e) {
                errors.add(ErrorMappingUtils.createBusinessRuleError(excelData, e, columnMap));
            }

            // Validar dígito de verificación según tipo de persona
            try {
                thirdValidationService.validateVerificationDigit(third);
            } catch (Exception e) {
                errors.add(ErrorMappingUtils.createBusinessRuleError(excelData, e, columnMap));
            }

        } catch (Exception e) {
            // Error crítico al construir el objeto Third
            errors.add(ErrorMappingUtils.createBusinessRuleError(excelData, e, columnMap));
        }
    }

    /**
     * Verifica si se puede construir un objeto Third con los datos disponibles.
     * Requiere al menos: personType, typeId y idNumber.
     */
    private boolean canBuildThirdObject(ThirdExcelData excelData) {
        return excelData.getPersonType() != null 
                && ValidationUtils.hasContent(excelData.getTypeIdName())
                && excelData.getIdNumber() != null;
    }

    /**
     * Convierte ThirdExcelData a Third para validaciones.
     */
    private Third convertToThird(ThirdExcelData excelData, ReferenceDataCache cache) {
        // Obtener TypeId usando encapsulación correcta
        TypeId typeId = null;
        if (ValidationUtils.hasContent(excelData.getTypeIdName())) {
            typeId = cache.getTypeId(excelData.getTypeIdName());
        }

        // Obtener ThirdTypes usando encapsulación correcta
        Set<ThirdType> thirdTypes = new HashSet<>();
        if (excelData.getThirdTypesNames() != null) {
            for (String typeName : excelData.getThirdTypesNames()) {
                ThirdType thirdType = cache.getThirdType(typeName);
                if (thirdType != null) {
                    thirdTypes.add(thirdType);
                }
            }
        }

        return Third.builder()
                .entId(excelData.getEntId())
                .typeId(typeId)
                .thirdTypes(thirdTypes)
                .personType(excelData.getPersonType())
                .names(excelData.getNames())
                .lastNames(excelData.getLastNames())
                .socialReason(excelData.getSocialReason())
                .gender(excelData.getGender())
                .idNumber(excelData.getIdNumber())
                .verificationNumber(excelData.getVerificationNumber())
                .state(excelData.getState())
                .address(excelData.getAddress())
                .phoneNumber(excelData.getPhoneNumber())
                .email(excelData.getEmail())
                .build();
    }

    /**
     * Objeto inmutable que encapsula todos los datos de referencia.
     * Simplifica testing y elimina complejidad de ThreadLocal.
     * Aplica encapsulación correcta con métodos de acceso.
     */
    public static class ReferenceDataCache {
        private final Map<String, TypeId> typeIds;
        private final Map<String, ThirdType> thirdTypes;
        private final Map<String, Country> countries;
        private final Map<String, State> states;
        private final Map<String, City> cities;

        // Constructor completo para uso en producción
        ReferenceDataCache(Map<String, TypeId> typeIds, Map<String, ThirdType> thirdTypes,
                Map<String, Country> countries, Map<String, State> states, Map<String, City> cities) {
            // Crear copias inmutables para evitar modificaciones externas
            this.typeIds = Collections.unmodifiableMap(new HashMap<>(typeIds));
            this.thirdTypes = Collections.unmodifiableMap(new HashMap<>(thirdTypes));
            this.countries = Collections.unmodifiableMap(new HashMap<>(countries));
            this.states = Collections.unmodifiableMap(new HashMap<>(states));
            this.cities = Collections.unmodifiableMap(new HashMap<>(cities));
        }

        // Constructor simplificado para testing unitario (usado en tests, no en
        // producción)
        @SuppressWarnings("unused")
        ReferenceDataCache(Map<String, TypeId> typeIds, Map<String, ThirdType> thirdTypes) {
            this(typeIds, thirdTypes, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
        }

        // Métodos de acceso con encapsulación
        boolean hasTypeId(String name) {
            return name != null && typeIds.containsKey(StringNormalizer.normalizeCode(name));
        }

        TypeId getTypeId(String name) {
            return name != null ? typeIds.get(StringNormalizer.normalizeCode(name)) : null;
        }

        boolean hasThirdType(String name) {
            return name != null && thirdTypes.containsKey(StringNormalizer.normalizeCode(name));
        }

        ThirdType getThirdType(String name) {
            return name != null ? thirdTypes.get(StringNormalizer.normalizeCode(name)) : null;
        }

        boolean hasCountry(String name) {
            return name != null && countries.containsKey(StringNormalizer.normalizeCode(name));
        }

        boolean hasState(String name) {
            return name != null && states.containsKey(StringNormalizer.normalizeCode(name));
        }

        State getState(String name) {
            return name != null ? states.get(StringNormalizer.normalizeCode(name)) : null;
        }

        boolean hasCity(String cityName, String stateCode) {
            if (cityName == null || stateCode == null)
                return false;
  
            String key = StringNormalizer.normalizeCode(cityName) + "_" + stateCode;
            return cities.containsKey(key);
        }

        City getCity(String cityName, String stateCode) {
            if (cityName == null || stateCode == null)
                return null;
            String key = StringNormalizer.normalizeCode(cityName) + "_" + stateCode;
            return cities.get(key);
        }
    }

    /**
     * Clase que representa el resultado de validación de un registro individual.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationResult {
        private boolean valid;
        private List<ImportErrorDetail> errors;
    }

    /**
     * Clase que representa el resultado de validación de un lote completo.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchValidationResult {
        private List<ThirdExcelData> validRecords;
        private List<ImportErrorDetail> errors;
        private int totalProcessed;
        private int validCount;
        private int errorCount;
    }
}