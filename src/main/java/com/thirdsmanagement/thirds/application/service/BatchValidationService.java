package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.*;
import com.thirdsmanagement.thirds.domain.utils.ImportConstants;
import com.thirdsmanagement.thirds.domain.utils.ValidationUtils;
import com.thirdsmanagement.thirds.domain.utils.ErrorMappingUtils;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ImportErrorDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio especializado en validaciones en lotes para importación de terceros.
 * Optimiza las validaciones pre-cargando datos de referencia y procesando en lotes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchValidationService {

    private final IdOutputPort idOutputPort;
    private final GeographyOutputPort geographyOutputPort;
    private final ThirdValidationService thirdValidationService;

    // Caches para datos de referencia (se cargan una vez por lote)
    private Map<String, TypeId> typeIdsCache;
    private Map<String, ThirdType> thirdTypesCache;
    private Map<String, Country> countriesCache;
    private Map<String, State> statesCache;
    private Map<String, City> citiesCache;

    /**
     * Valida un lote de datos de terceros de Excel.
     * 
     * @param thirdsData lista de datos de terceros a validar
     * @param entId identificador de la entidad
     * @param columnMap mapa de columnas del Excel para incluir números de columna en errores
     * @return resultado de validación con errores y advertencias
     */
    public BatchValidationResult validateBatch(List<ThirdExcelData> thirdsData, String entId, Map<String, Integer> columnMap) {
        List<ImportErrorDetail> errors = new ArrayList<>();
        List<ThirdExcelData> validRecords = new ArrayList<>();

        // Pre-cargar datos de referencia
        preloadReferenceData(entId);

        log.info("Iniciando validación de lote con {} registros para entidad {}", thirdsData.size(), entId);

            for (ThirdExcelData excelData : thirdsData) {
            try {
                ValidationResult result = validateSingleRecord(excelData, entId, columnMap);
                
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

        log.info("Validación completada. Válidos: {}, Errores: {}", 
                validRecords.size(), errors.size());

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
     */
    private void preloadReferenceData(String entId) {
        log.debug("Pre-cargando datos de referencia para entidad {}", entId);

        // Cargar tipos de identificación
        typeIdsCache = idOutputPort.getAllTypeIds(entId).stream()
                .filter(typeId -> typeId.getStatus() != null && typeId.getStatus())
                .collect(Collectors.toMap(
                        typeId -> typeId.getTypeIdname().toUpperCase(),
                        typeId -> typeId,
                        (existing, replacement) -> existing
                ));

        // Cargar tipos de tercero
        thirdTypesCache = idOutputPort.getALLThirdTypes(entId).stream()
                .filter(thirdType -> thirdType.getStatus() != null && thirdType.getStatus())
                .collect(Collectors.toMap(
                        thirdType -> thirdType.getThirdTypeName().toUpperCase(),
                        thirdType -> thirdType,
                        (existing, replacement) -> existing
                ));

        // Cargar países
        countriesCache = geographyOutputPort.getAllActiveCountries().stream()
                .collect(Collectors.toMap(
                        country -> country.getCountryName().toUpperCase(),
                        country -> country,
                        (existing, replacement) -> existing
                ));

        // Cargar estados (solo de Colombia por ahora)
        statesCache = geographyOutputPort.getStatesByCountry("COL").stream()
                .collect(Collectors.toMap(
                        state -> state.getStateName().toUpperCase(),
                        state -> state,
                        (existing, replacement) -> existing
                ));

        // Cargar ciudades de Colombia
        citiesCache = new HashMap<>();
        for (State state : statesCache.values()) {
            geographyOutputPort.getCitiesByState(state.getStateCode(), "COL").forEach(city -> {
                String key = city.getCityName().toUpperCase() + "_" + state.getStateCode();
                citiesCache.put(key, city);
            });
        }

        log.debug("Datos pre-cargados: {} tipos ID, {} tipos tercero, {} países, {} estados, {} ciudades",
                typeIdsCache.size(), thirdTypesCache.size(), countriesCache.size(), 
                statesCache.size(), citiesCache.size());
    }

    /**
     * Valida un registro individual usando los datos pre-cargados.
     */
    private ValidationResult validateSingleRecord(ThirdExcelData excelData, String entId, Map<String, Integer> columnMap) {
        List<ImportErrorDetail> errors = new ArrayList<>();

        // 1. Validaciones básicas de formato y campos requeridos
        validateBasicFields(excelData, errors, columnMap);

        // 2. Validaciones de referencias (tipos, geografía)
        validateReferences(excelData, errors, columnMap);

        // 3. Validaciones de reglas de negocio
        if (errors.isEmpty()) {
            validateBusinessRules(excelData, errors, columnMap);
        }

        boolean isValid = errors.isEmpty();

        return ValidationResult.builder()
                .valid(isValid)
                .errors(errors)
                .build();
    }

    /**
     * Valida campos básicos y formato.
     */
    private void validateBasicFields(ThirdExcelData excelData, List<ImportErrorDetail> errors, Map<String, Integer> columnMap) {
        int rowNumber = excelData.getRowNumber();

        // Validar campos requeridos
        if (excelData.getTypeIdName() == null || excelData.getTypeIdName().trim().isEmpty()) {
            errors.add(createError(rowNumber, "Tipo Identificación", null, 
                    ImportConstants.ErrorCodes.REQUIRED_FIELD_MISSING, "El tipo de identificación es obligatorio",
                    ImportErrorDetail.ErrorType.VALIDATION_ERROR, columnMap));
        }

        if (excelData.getIdNumber() == null) {
            errors.add(createError(rowNumber, "Número Identificación", null,
                    ImportConstants.ErrorCodes.REQUIRED_FIELD_MISSING, "El número de identificación es obligatorio",
                    ImportErrorDetail.ErrorType.VALIDATION_ERROR, columnMap));
        }

        if (excelData.getPersonType() == null) {
            errors.add(createError(rowNumber, "Tipo Persona", null,
                    ImportConstants.ErrorCodes.REQUIRED_FIELD_MISSING, "El tipo de persona es obligatorio",
                    ImportErrorDetail.ErrorType.VALIDATION_ERROR, columnMap));
        }

        // Validar email si está presente
        if (excelData.getEmail() != null && !excelData.getEmail().trim().isEmpty()) {
            if (!ValidationUtils.isValidEmail(excelData.getEmail())) {
                errors.add(createError(rowNumber, "Email", excelData.getEmail(),
                        ImportConstants.ErrorCodes.INVALID_EMAIL_FORMAT, "El formato del correo electrónico no es válido",
                        ImportErrorDetail.ErrorType.FORMAT_ERROR, columnMap));
            }
        }

        // Validar teléfono si está presente
        if (excelData.getPhoneNumber() != null && !excelData.getPhoneNumber().trim().isEmpty()) {
            if (!ValidationUtils.isValidPhoneNumber(excelData.getPhoneNumber())) {
                errors.add(createError(rowNumber, "Teléfono", excelData.getPhoneNumber(),
                        ImportConstants.ErrorCodes.INVALID_PHONE_FORMAT, "El formato del número de teléfono no es válido",
                        ImportErrorDetail.ErrorType.FORMAT_ERROR, columnMap));
            }
        }
    }

    /**
     * Valida referencias a datos maestros.
     */
    private void validateReferences(ThirdExcelData excelData, List<ImportErrorDetail> errors, Map<String, Integer> columnMap) {
        int rowNumber = excelData.getRowNumber();

        // Validar tipo de identificación
        if (excelData.getTypeIdName() != null) {
            String typeIdKey = excelData.getTypeIdName().toUpperCase();
            if (!typeIdsCache.containsKey(typeIdKey)) {
                errors.add(createError(rowNumber, "Tipo Identificación", excelData.getTypeIdName(),
                        "INVALID_TYPE_ID_REFERENCE", 
                        "El tipo de identificación '" + excelData.getTypeIdName() + "' no existe o está inactivo",
                        ImportErrorDetail.ErrorType.REFERENCE_ERROR, columnMap));
            }
        }

        // Validar tipos de tercero
        if (excelData.getThirdTypesNames() != null && !excelData.getThirdTypesNames().isEmpty()) {
            for (String typeName : excelData.getThirdTypesNames()) {
                String typeKey = typeName.toUpperCase();
                if (!thirdTypesCache.containsKey(typeKey)) {
                    errors.add(createError(rowNumber, "Tipos de Tercero", typeName,
                            "INVALID_THIRD_TYPE_REFERENCE",
                            "El tipo de tercero '" + typeName + "' no existe o está inactivo",
                            ImportErrorDetail.ErrorType.REFERENCE_ERROR, columnMap));
                }
            }
        }

        // Validar geografía
        validateGeography(excelData, errors, columnMap);
    }

    /**
     * Valida datos geográficos.
     */
    private void validateGeography(ThirdExcelData excelData, List<ImportErrorDetail> errors, Map<String, Integer> columnMap) {
        int rowNumber = excelData.getRowNumber();

        // Validar completitud geográfica: si se proporciona uno, se deben proporcionar todos
        boolean hasCountry = excelData.getCountryName() != null && !excelData.getCountryName().trim().isEmpty();
        boolean hasState = excelData.getStateName() != null && !excelData.getStateName().trim().isEmpty();
        boolean hasCity = excelData.getCityName() != null && !excelData.getCityName().trim().isEmpty();

        // Si hay ciudad o estado, debe haber país
        if ((hasState || hasCity) && !hasCountry) {
            errors.add(createError(rowNumber, "País", null,
                    "MISSING_COUNTRY_FOR_GEOGRAPHY",
                    "El país es obligatorio cuando se especifica departamento o ciudad",
                    ImportErrorDetail.ErrorType.VALIDATION_ERROR, columnMap));
            return;
        }

        // Si hay ciudad, debe haber estado
        if (hasCity && !hasState) {
            errors.add(createError(rowNumber, "Departamento", null,
                    "MISSING_STATE_FOR_CITY",
                    "El departamento es obligatorio cuando se especifica ciudad",
                    ImportErrorDetail.ErrorType.VALIDATION_ERROR, columnMap));
            return;
        }

        // Validar país
        if (hasCountry) {
            String countryKey = excelData.getCountryName().toUpperCase();
            if (!countriesCache.containsKey(countryKey)) {
                errors.add(createError(rowNumber, "País", excelData.getCountryName(),
                        "INVALID_COUNTRY_REFERENCE",
                        "El país '" + excelData.getCountryName() + "' no existe o está inactivo",
                        ImportErrorDetail.ErrorType.REFERENCE_ERROR, columnMap));
                return; // No validar estado/ciudad si el país es inválido
            }
        }

        // Validar estado/departamento
        if (hasState) {
            String stateKey = excelData.getStateName().toUpperCase();
            if (!statesCache.containsKey(stateKey)) {
                errors.add(createError(rowNumber, "Departamento", excelData.getStateName(),
                        "INVALID_STATE_REFERENCE",
                        "El departamento '" + excelData.getStateName() + "' no existe o está inactivo",
                        ImportErrorDetail.ErrorType.REFERENCE_ERROR, columnMap));
                return; // No validar ciudad si el estado es inválido
            }

            // Validar ciudad
            if (hasCity) {
                State state = statesCache.get(stateKey);
                String cityKey = excelData.getCityName().toUpperCase() + "_" + state.getStateCode();
                if (!citiesCache.containsKey(cityKey)) {
                    errors.add(createError(rowNumber, "Ciudad", excelData.getCityName(),
                            "INVALID_CITY_REFERENCE",
                            "La ciudad '" + excelData.getCityName() + "' no existe en el departamento '" + excelData.getStateName() + "'",
                            ImportErrorDetail.ErrorType.REFERENCE_ERROR, columnMap));
                }
            }
        }
    }

    /**
     * Valida reglas de negocio reutilizando el servicio existente.
     */
    private void validateBusinessRules(ThirdExcelData excelData, List<ImportErrorDetail> errors, Map<String, Integer> columnMap) {
        try {
            // Convertir a Third para validar con el servicio existente
            Third third = convertToThird(excelData);
            
            // Usar las validaciones existentes del dominio
            thirdValidationService.validatePersonTypeConsistency(third);
            thirdValidationService.validateTypeIdPersonTypeCompatibility(third);
            thirdValidationService.validateNitFormat(third);
            
        } catch (Exception e) {
            // Detectar el campo específico según el mensaje de error
            String fieldName = ValidationUtils.detectFieldFromBusinessRuleError(e.getMessage());
            String fieldValue = ErrorMappingUtils.getFieldValueFromExcelData(excelData, fieldName);
            Integer columnNumber = columnMap != null ? columnMap.get(fieldName) : null;
            
            errors.add(ImportErrorDetail.builder()
                    .rowNumber(excelData.getRowNumber())
                    .columnNumber(columnNumber != null ? columnNumber + 1 : null) // Empezar en 1, no en 0
                    .columnName(fieldName)
                    .fieldValue(fieldValue)
                    .errorCode("BUSINESS_RULE_VIOLATION")
                    .errorMessage("Violación de regla de negocio: " + e.getMessage())
                    .errorType(ImportErrorDetail.ErrorType.BUSINESS_RULE_ERROR)
                    .build());
        }
    }

    /**
     * Convierte ThirdExcelData a Third para validaciones.
     */
    private Third convertToThird(ThirdExcelData excelData) {
        // Obtener TypeId desde cache
        TypeId typeId = null;
        if (excelData.getTypeIdName() != null) {
            typeId = typeIdsCache.get(excelData.getTypeIdName().toUpperCase());
        }

        // Obtener ThirdTypes desde cache
        Set<ThirdType> thirdTypes = new HashSet<>();
        if (excelData.getThirdTypesNames() != null) {
            for (String typeName : excelData.getThirdTypesNames()) {
                ThirdType thirdType = thirdTypesCache.get(typeName.toUpperCase());
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
     * Utilitario para crear errores de manera consistente.
     * Incluye automáticamente el número de columna si está disponible en el mapa.
     */
    private ImportErrorDetail createError(int rowNumber, String columnName, String fieldValue,
                                        String errorCode, String errorMessage, 
                                        ImportErrorDetail.ErrorType errorType, Map<String, Integer> columnMap) {
        Integer columnNumber = columnMap != null ? columnMap.get(columnName) : null;
        return ImportErrorDetail.builder()
                .rowNumber(rowNumber)
                .columnNumber(columnNumber != null ? columnNumber + 1 : null)
                .columnName(columnName)
                .fieldValue(fieldValue)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .errorType(errorType)
                .build();
    }

    /**
     * Detecta el campo específico basado en el mensaje de error de reglas de negocio.
     */
    private String detectFieldFromBusinessRuleError(String errorMessage) {
        if (errorMessage == null) return "Reglas Negocio";
        
        String lowerMessage = errorMessage.toLowerCase();
        if (lowerMessage.contains("nit")) {
            return "Número Identificación";
        } else if (lowerMessage.contains("tipo de persona") || lowerMessage.contains("persona natural") || lowerMessage.contains("persona jurídica")) {
            return "Tipo Persona";
        } else if (lowerMessage.contains("tipo de identificación") || lowerMessage.contains("identificación")) {
            return "Tipo Identificación";
        } else if (lowerMessage.contains("nombre")) {
            return "Nombres";
        } else if (lowerMessage.contains("razón social")) {
            return "Razón Social";
        } else {
            return "Reglas Negocio"; // Campo genérico si no se puede detectar
        }
    }

    /**
     * Obtiene el valor del campo específico desde ThirdExcelData.
     */
    // Métodos de validación movidos a ValidationUtils y ErrorMappingUtils para reutilización

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
