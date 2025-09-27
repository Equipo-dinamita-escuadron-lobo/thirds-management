package com.thirdsmanagement.thirds.domain.utils;

/**
 * Constantes centralizadas para funcionalidades de importación.
 * Centraliza todos los valores constantes utilizados en el proceso de importación
 * para facilitar mantenimiento y reutilización.
 */
public final class ImportConstants {

    private ImportConstants() {
        // Clase utilitaria - constructor privado
    }

    // ===== CONFIGURACIÓN DE ARCHIVOS =====
    
    /**
     * Tamaño máximo permitido para archivos de importación (10MB).
     */
    public static final int MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    /**
     * Extensiones de archivo soportadas para importación.
     */
    public static final String[] SUPPORTED_EXTENSIONS = {".xlsx", ".xls"};
    
    /**
     * Tipo MIME para archivos Excel XLSX.
     */
    public static final String EXCEL_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    // ===== ENCABEZADOS DE EXCEL =====
    
    /**
     * Encabezados básicos requeridos en orden fijo para importación de terceros.
     */
    public static final String[] BASIC_REQUIRED_HEADERS = {
        "Tipo Identificación", 
        "Número Identificación", 
        "Dígito Verificación", 
        "Tipo Persona", 
        "Nombres", 
        "Apellidos", 
        "Razón Social", 
        "Género", 
        "Estado",
        "Dirección",
        "Teléfono",
        "Email"
    };
    
    /**
     * Encabezados opcionales que pueden aparecer en el Excel.
     */
    public static final class OptionalHeaders {
        public static final String TYPES = "Tipos de Tercero";
        public static final String COUNTRY = "País";
        public static final String STATE = "Departamento";  
        public static final String CITY = "Ciudad";
        public static final String ADDRESS = "Dirección";
        public static final String PHONE = "Teléfono";
        public static final String EMAIL = "Email";
        
        private OptionalHeaders() {}
    }

    // ===== CONFIGURACIÓN DE PROCESAMIENTO =====
    
    /**
     * Prefijo para generar IDs únicos de importación.
     */
    public static final String IMPORT_ID_PREFIX = "IMP";
    
    /**
     * Tamaño de lote por defecto para procesamiento.
     */
    public static final int DEFAULT_BATCH_SIZE = 1000;
    
    /**
     * Número máximo de errores permitidos antes de detener importación.
     */
    public static final int MAX_ERRORS_THRESHOLD = 100;

    // ===== MENSAJES DE ERROR COMUNES =====
    
    /**
     * Mensajes de error estándar para importación.
     */
    public static final class ErrorMessages {
        public static final String FILE_EMPTY = "El archivo está vacío";
        public static final String FILE_TOO_LARGE = "El archivo excede el tamaño máximo permitido";
        public static final String INVALID_FILE_FORMAT = "Formato de archivo no soportado";
        public static final String MISSING_REQUIRED_HEADERS = "Faltan encabezados requeridos";
        public static final String NO_DATA_FOUND = "No se encontraron datos válidos para importar";
        public static final String SYSTEM_ERROR = "Error del sistema durante la importación";
        
        private ErrorMessages() {}
    }

    // ===== CÓDIGOS DE ERROR =====
    
    /**
     * Códigos de error estandarizados para importación.
     */
    public static final class ErrorCodes {
        public static final String REQUIRED_FIELD_MISSING = "REQUIRED_FIELD_MISSING";
        public static final String INVALID_EMAIL_FORMAT = "INVALID_EMAIL_FORMAT";
        public static final String INVALID_PHONE_FORMAT = "INVALID_PHONE_FORMAT";
        public static final String INVALID_TYPE_ID_REFERENCE = "INVALID_TYPE_ID_REFERENCE";
        public static final String INVALID_COUNTRY_REFERENCE = "INVALID_COUNTRY_REFERENCE";
        public static final String INVALID_STATE_REFERENCE = "INVALID_STATE_REFERENCE";
        public static final String INVALID_CITY_REFERENCE = "INVALID_CITY_REFERENCE";
        public static final String BUSINESS_RULE_VIOLATION = "BUSINESS_RULE_VIOLATION";
        public static final String DUPLICATE_RECORD = "DUPLICATE_RECORD";
        public static final String MISSING_COUNTRY_FOR_GEOGRAPHY = "MISSING_COUNTRY_FOR_GEOGRAPHY";
        public static final String MISSING_STATE_FOR_CITY = "MISSING_STATE_FOR_CITY";
        public static final String SYSTEM_ERROR = "SYSTEM_ERROR";
        
        private ErrorCodes() {}
    }

    // ===== PATRONES DE VALIDACIÓN =====
    
    /**
     * Patrones regex para validaciones comunes.
     */
    public static final class ValidationPatterns {
        public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        public static final String PHONE_PATTERN = "^[+]?[0-9]{7,15}$";
        public static final String NIT_START_PATTERN = "^[89].*";
        
        private ValidationPatterns() {}
    }

    // ===== VALORES POR DEFECTO =====
    
    /**
     * Valores por defecto para configuraciones.
     */
    public static final class Defaults {
        public static final boolean SKIP_DUPLICATES = true;
        public static final boolean CONTINUE_ON_ERROR = false;
        public static final int COLUMN_START_INDEX = 1; // Las columnas empiezan en 1, no en 0
        
        private Defaults() {}
    }
}
