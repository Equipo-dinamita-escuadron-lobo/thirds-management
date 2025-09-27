package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.*;
import com.thirdsmanagement.thirds.domain.utils.ImportConstants;
import com.thirdsmanagement.thirds.domain.utils.ValidationUtils;
import com.thirdsmanagement.thirds.domain.utils.ErrorMappingUtils;
import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ImportErrorDetail;
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
                log.error("Error validando registro en fila {}: {}", excelData.getRowNumber(), e.getMessage());
                errors.add(ImportErrorDetail.builder()
                        .rowNumber(excelData.getRowNumber())
                        .errorCode("VALIDATION_SYSTEM_ERROR")
                        .errorMessage("Error del sistema validando registro: " + e.getMessage())
                        .errorType(ImportErrorDetail.ErrorType.SYSTEM_ERROR)
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
    private ReferenceDataCache preloadReferenceData(String entId) {

        // Cargar tipos de identificación
        Map<String, TypeId> typeIds = idOutputPort.getAllTypeIds(entId).stream()
                .filter(typeId -> typeId.getStatus() != null && typeId.getStatus())
                .collect(Collectors.toMap(
                        typeId -> typeId.getTypeIdname().toUpperCase(),
                        typeId -> typeId,
                        (existing, replacement) -> existing));

        // Cargar tipos de tercero
        Map<String, ThirdType> thirdTypes = idOutputPort.getALLThirdTypes(entId).stream()
                .filter(thirdType -> thirdType.getStatus() != null && thirdType.getStatus())
                .collect(Collectors.toMap(
                        thirdType -> thirdType.getThirdTypeName().toUpperCase(),
                        thirdType -> thirdType,
                        (existing, replacement) -> existing));

        // Cargar geografía
        Map<String, Country> countries = geographyOutputPort.getAllActiveCountries().stream()
                .collect(Collectors.toMap(
                        country -> country.getCountryName().toUpperCase(),
                        country -> country,
                        (existing, replacement) -> existing));

        Map<String, State> states = geographyOutputPort.getStatesByCountry("COL").stream()
                .collect(Collectors.toMap(
                        state -> state.getStateName().toUpperCase(),
                        state -> state,
                        (existing, replacement) -> existing));

        Map<String, City> cities = new HashMap<>();
        for (State state : states.values()) {
            geographyOutputPort.getCitiesByState(state.getStateCode(), "COL").forEach(city -> {
                String key = city.getCityName().toUpperCase() + "_" + state.getStateCode();
                cities.put(key, city);
            });
        }

        // Crear objeto inmutable con todos los datos
        ReferenceDataCache cache = new ReferenceDataCache(typeIds, thirdTypes, countries, states, cities);

        return cache;
    }

    /**
     * Valida un registro individual usando los datos pre-cargados.
     */
    private ValidationResult validateSingleRecord(ThirdExcelData excelData, ReferenceDataCache cache,
            Map<String, Integer> columnMap) {
        List<ImportErrorDetail> errors = new ArrayList<>();

        // 1. Validaciones básicas de formato y campos requeridos
        validateBasicFields(excelData, errors, columnMap);

        // 2. Validaciones de referencias (tipos, geografía)
        validateReferences(excelData, errors, cache, columnMap);

        // 3. Validaciones de reglas de negocio (solo si no hay errores básicos)
        if (errors.isEmpty()) {
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
        // Validar campos requeridos usando métodos utilitarios
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

        // Validar formatos usando ValidationUtils centralizado
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
     * Valida datos geográficos con completitud y existencia.
     */
    private void validateGeography(ThirdExcelData excelData, List<ImportErrorDetail> errors, ReferenceDataCache cache,
            Map<String, Integer> columnMap) {
        boolean hasCountry = ValidationUtils.hasContent(excelData.getCountryName());
        boolean hasState = ValidationUtils.hasContent(excelData.getStateName());
        boolean hasCity = ValidationUtils.hasContent(excelData.getCityName());

        // Validar completitud geográfica
        if ((hasState || hasCity) && !hasCountry) {
            errors.add(ErrorMappingUtils.createError(excelData.getRowNumber(), "País", null,
                    ImportConstants.ErrorCodes.MISSING_COUNTRY_FOR_GEOGRAPHY,
                    "El país es obligatorio cuando se especifica departamento o ciudad",
                    ImportErrorType.VALIDATION_ERROR, columnMap));
            return;
        }

        if (hasCity && !hasState) {
            errors.add(ErrorMappingUtils.createError(excelData.getRowNumber(), "Departamento", null,
                    ImportConstants.ErrorCodes.MISSING_STATE_FOR_CITY,
                    "El departamento es obligatorio cuando se especifica ciudad",
                    ImportErrorType.VALIDATION_ERROR, columnMap));
            return;
        }

        // Validar existencia usando encapsulación
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
     */
    private void validateBusinessRules(ThirdExcelData excelData, List<ImportErrorDetail> errors,
            ReferenceDataCache cache, Map<String, Integer> columnMap) {
        try {

            Third third = convertToThird(excelData, cache);

            thirdValidationService.validatePersonTypeConsistency(third);
            thirdValidationService.validateTypeIdPersonTypeCompatibility(third);
            thirdValidationService.validateNitFormat(third);

        } catch (Exception e) {
            errors.add(ErrorMappingUtils.createBusinessRuleError(excelData, e, columnMap));
        }
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
    private static class ReferenceDataCache {
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

        // Métodos de acceso con encapsulación correcta
        boolean hasTypeId(String name) {
            return name != null && typeIds.containsKey(name.toUpperCase());
        }

        TypeId getTypeId(String name) {
            return name != null ? typeIds.get(name.toUpperCase()) : null;
        }

        boolean hasThirdType(String name) {
            return name != null && thirdTypes.containsKey(name.toUpperCase());
        }

        ThirdType getThirdType(String name) {
            return name != null ? thirdTypes.get(name.toUpperCase()) : null;
        }

        boolean hasCountry(String name) {
            return name != null && countries.containsKey(name.toUpperCase());
        }

        boolean hasState(String name) {
            return name != null && states.containsKey(name.toUpperCase());
        }

        State getState(String name) {
            return name != null ? states.get(name.toUpperCase()) : null;
        }

        boolean hasCity(String cityName, String stateCode) {
            if (cityName == null || stateCode == null)
                return false;
            String key = cityName.toUpperCase() + "_" + stateCode;
            return cities.containsKey(key);
        }
    }

    /**
     * Clase que representa el resultado de validación de un registro individual.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ValidationResult {
        private boolean valid;
        private List<ImportErrorDetail> errors;
    }

    /**
     * Clase que representa el resultado de validación de un lote completo.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class BatchValidationResult {
        private List<ThirdExcelData> validRecords;
        private List<ImportErrorDetail> errors;
        private int totalProcessed;
        private int validCount;
        private int errorCount;
    }
}