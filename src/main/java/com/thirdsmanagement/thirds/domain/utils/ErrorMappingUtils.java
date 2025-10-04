package com.thirdsmanagement.thirds.domain.utils;

import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;
import com.thirdsmanagement.thirds.domain.model.ImportErrorDetail;
import com.thirdsmanagement.thirds.domain.model.ThirdExcelData;

import java.util.Map;

/**
 * Utilidades para mapeo y creación consistente de errores de importación.
 * Centraliza la lógica de creación de errores para mantener consistencia
 * en toda la aplicación.
 */
public final class ErrorMappingUtils {

    private ErrorMappingUtils() {
        // Clase utilitaria - constructor privado
    }

    // ===== CREACIÓN DE ERRORES =====

    /**
     * Crea un error de importación con toda la información disponible.
     * 
     * @param rowNumber    número de fila donde ocurrió el error
     * @param columnName   nombre de la columna donde ocurrió el error
     * @param fieldValue   valor del campo que causó el error
     * @param errorCode    código del error para categorización
     * @param errorMessage mensaje descriptivo del error
     * @param errorType    tipo de error para clasificación
     * @param columnMap    mapa de columnas para obtener el número de columna
     * @return error de importación estructurado
     */
    public static ImportErrorDetail createError(int rowNumber, String columnName, String fieldValue,
            String errorCode, String errorMessage,
            ImportErrorType errorType, Map<String, Integer> columnMap) {
        Integer columnNumber = null;
        if (columnMap != null && columnName != null) {
            columnNumber = columnMap.get(columnName);
            if (columnNumber != null) {
                columnNumber = columnNumber + ImportConstants.Defaults.COLUMN_START_INDEX; // Empezar en 1, no en 0
            }
        }

        return ImportErrorDetail.builder()
                .rowNumber(rowNumber)
                .columnNumber(columnNumber)
                .columnName(columnName)
                .fieldValue(fieldValue)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .errorType(errorType)
                .build();
    }

    /**
     * Crea un error de validación de campo requerido.
     * 
     * @param rowNumber  número de fila
     * @param columnName nombre de la columna
     * @param columnMap  mapa de columnas
     * @return error de campo requerido
     */
    public static ImportErrorDetail createRequiredFieldError(int rowNumber, String columnName,
            Map<String, Integer> columnMap) {
        return createError(rowNumber, columnName, null,
                ImportConstants.ErrorCodes.REQUIRED_FIELD_MISSING,
                "El campo " + columnName + " es obligatorio",
                ImportErrorType.VALIDATION_ERROR,
                columnMap);
    }

    /**
     * Crea un error de formato de email inválido.
     * 
     * @param rowNumber  número de fila
     * @param emailValue valor del email inválido
     * @param columnMap  mapa de columnas
     * @return error de formato de email
     */
    public static ImportErrorDetail createInvalidEmailError(int rowNumber, String emailValue,
            Map<String, Integer> columnMap) {
        return createError(rowNumber, ImportConstants.EMAIL_COLUMN, emailValue,
                ImportConstants.ErrorCodes.INVALID_EMAIL_FORMAT,
                "El formato del correo electrónico no es válido",
                ImportErrorType.FORMAT_ERROR,
                columnMap);
    }

    /**
     * Crea un error de formato de teléfono inválido.
     * 
     * @param rowNumber  número de fila
     * @param phoneValue valor del teléfono inválido
     * @param columnMap  mapa de columnas
     * @return error de formato de teléfono
     */
    public static ImportErrorDetail createInvalidPhoneError(int rowNumber, String phoneValue,
            Map<String, Integer> columnMap) {
        return createError(rowNumber, ImportConstants.PHONE_COLUMN, phoneValue,
                ImportConstants.ErrorCodes.INVALID_PHONE_FORMAT,
                "El formato del número de teléfono no es válido",
                ImportErrorType.FORMAT_ERROR,
                columnMap);
    }

    /**
     * Crea un error de referencia inválida para datos maestros.
     * 
     * @param rowNumber     número de fila
     * @param columnName    nombre de la columna
     * @param fieldValue    valor que no existe en referencia
     * @param referenceType tipo de referencia (ej: "tipo de identificación")
     * @param columnMap     mapa de columnas
     * @return error de referencia inválida
     */
    public static ImportErrorDetail createInvalidReferenceError(int rowNumber, String columnName,
            String fieldValue, String referenceType,
            Map<String, Integer> columnMap) {
        String errorCode = "INVALID_" + referenceType.toUpperCase().replace(" ", "_") + "_REFERENCE";
        String errorMessage = String.format("%s '%s' no existe o está inactivo", referenceType, fieldValue);

        return createError(rowNumber, columnName, fieldValue,
                errorCode, errorMessage,
                ImportErrorType.REFERENCE_ERROR,
                columnMap);
    }

    /**
     * Crea un error de regla de negocio con detección automática de campo.
     * 
     * @param excelData datos del registro que causó el error
     * @param exception excepción que contiene el mensaje de error
     * @param columnMap mapa de columnas
     * @return error de regla de negocio
     */
    public static ImportErrorDetail createBusinessRuleError(ThirdExcelData excelData, Exception exception,
            Map<String, Integer> columnMap) {
        String fieldName = ValidationUtils.detectFieldFromBusinessRuleError(exception.getMessage());
        String fieldValue = getFieldValueFromExcelData(excelData, fieldName);

        return createError(excelData.getRowNumber(), fieldName, fieldValue,
                ImportConstants.ErrorCodes.BUSINESS_RULE_VIOLATION,
                "Violación de regla de negocio: " + exception.getMessage(),
                ImportErrorType.BUSINESS_RULE_ERROR,
                columnMap);
    }

    /**
     * Crea un error de duplicado.
     * 
     * @param rowNumber  número de fila
     * @param fieldValue valor duplicado
     * @param columnName nombre del campo duplicado
     * @param columnMap  mapa de columnas
     * @return error de duplicado
     */
    public static ImportErrorDetail createDuplicateError(int rowNumber, String fieldValue,
            String columnName, Map<String, Integer> columnMap) {
        return createError(rowNumber, columnName, fieldValue,
                ImportConstants.ErrorCodes.DUPLICATE_RECORD,
                "El registro con " + columnName + " '" + fieldValue + "' ya existe",
                ImportErrorType.DUPLICATE_ERROR,
                columnMap);
    }

    /**
     * Crea un error del sistema.
     * 
     * @param errorMessage mensaje del error del sistema
     * @return error del sistema
     */
    public static ImportErrorDetail createSystemError(String errorMessage) {
        return ImportErrorDetail.builder()
                .errorCode(ImportConstants.ErrorCodes.SYSTEM_ERROR)
                .errorMessage(ImportConstants.ErrorMessages.SYSTEM_ERROR + ": " + errorMessage)
                .errorType(ImportErrorType.SYSTEM_ERROR)
                .build();
    }

    /**
     * Crea un error de procesamiento durante la creación del registro.
     * 
     * @param excelData    datos del registro
     * @param errorMessage mensaje del error
     * @return error de procesamiento
     */
    public static ImportErrorDetail createProcessingError(ThirdExcelData excelData, String errorMessage) {
        return ImportErrorDetail.builder()
                .rowNumber(excelData.getRowNumber())
                .errorCode(ImportConstants.ErrorCodes.SYSTEM_ERROR)
                .errorMessage("Error al procesar registro: " + errorMessage)
                .errorType(ImportErrorType.SYSTEM_ERROR)
                .build();
    }

    /**
     * Crea un error de ciudad faltante cuando se especifica departamento.
     * 
     * @param rowNumber  número de fila
     * @param columnMap  mapa de columnas
     * @return error de ciudad faltante
     */
    public static ImportErrorDetail createMissingCityError(int rowNumber, Map<String, Integer> columnMap) {
        return createError(rowNumber, "Ciudad", null,
                ImportConstants.ErrorCodes.MISSING_CITY_FOR_COMPLETE_ADDRESS,
                "La ciudad es obligatoria cuando se especifica departamento",
                ImportErrorType.VALIDATION_ERROR,
                columnMap);
    }

    // ===== GENERACIÓN DE CÓDIGOS DE ERROR =====

    /**
     * Genera un código de error consistente basado en el nombre del campo.
     * Centraliza la lógica de generación para mantener consistencia.
     * 
     * @param prefix prefijo del código (ej: "INVALID", "MISSING")
     * @param fieldName nombre del campo
     * @return código de error normalizado (ej: "INVALID_TIPO_PERSONA")
     */
    public static String generateErrorCode(String prefix, String fieldName) {
        if (prefix == null || fieldName == null) {
            return "UNKNOWN_ERROR";
        }
        
        return prefix.toUpperCase() + "_" + 
               fieldName.toUpperCase()
                       .replace(" ", "_")
                       .replace("Ó", "O")
                       .replace("É", "E")
                       .replace("Í", "I")
                       .replace("Á", "A")
                       .replace("Ú", "U")
                       .replaceAll("[^A-Z0-9_]", "_");
    }

    /**
     * Genera código de error para valores inválidos de campo.
     * 
     * @param fieldName nombre del campo
     * @return código de error para valor inválido
     */
    public static String generateInvalidFieldErrorCode(String fieldName) {
        return generateErrorCode("INVALID", fieldName);
    }

    /**
     * Genera código de error para errores de parseo de campo.
     * 
     * @param fieldName nombre del campo
     * @return código de error para parseo
     */
    public static String generateParsingErrorCode(String fieldName) {
        return generateErrorCode(fieldName, "PARSING_ERROR");
    }

    // ===== UTILIDADES DE MAPEO =====

    /**
     * Obtiene el valor de un campo específico desde los datos de Excel.
     * 
     * @param excelData datos de Excel del registro
     * @param fieldName nombre del campo
     * @return valor del campo o null si no se encuentra
     */
    public static String getFieldValueFromExcelData(ThirdExcelData excelData, String fieldName) {
        if (excelData == null || fieldName == null) {
            return null;
        }

        return switch (fieldName) {
            case "Número Identificación" ->
                excelData.getIdNumber() != null ? excelData.getIdNumber().toString() : null;
            case "Dígito Verificación" ->
                excelData.getVerificationNumber() != null ? excelData.getVerificationNumber().toString() : null;
            case "Tipo Persona" ->
                excelData.getPersonType() != null ? excelData.getPersonType().toString() : null;
            case "Tipo Identificación" -> excelData.getTypeIdName();
            case "Nombres" -> excelData.getNames();
            case "Apellidos" -> excelData.getLastNames();
            case "Razón Social" -> excelData.getSocialReason();
            case "Género" ->
                excelData.getGender() != null ? excelData.getGender().toString() : null;
            case "Email" -> excelData.getEmail();
            case "Teléfono" -> excelData.getPhoneNumber();
            case "País" -> excelData.getCountryName();
            case "Departamento" -> excelData.getStateName();
            case "Ciudad" -> excelData.getCityName();
            case "Dirección" -> excelData.getAddress();
            default -> null;
        };
    }

}
