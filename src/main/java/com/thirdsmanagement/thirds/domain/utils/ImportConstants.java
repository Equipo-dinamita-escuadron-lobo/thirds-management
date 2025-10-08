package com.thirdsmanagement.thirds.domain.utils;

/**
 * Constantes centralizadas para funcionalidades de importación.
 * Centraliza todos los valores constantes utilizados en el proceso de importación
 * para facilitar mantenimiento y reutilización.
 */
public final class ImportConstants {

    private ImportConstants() {
        throw new UnsupportedOperationException("ImportConstants es una clase de utilidad y no debe ser instanciada");
    }

    // ===== CONFIGURACIÓN DE ARCHIVOS =====
    
    /**
     * Extensiones de archivo soportadas para importación.
     */
    public static final String[] SUPPORTED_EXTENSIONS = {".xlsx", ".xls"};

    // ===== ENCABEZADOS DE EXCEL =====
    
    /**
     * Encabezados requeridos para importación de terceros.
     * Campos mínimos obligatorios para cualquier tipo de importación.
     */
    public static final String[] REQUIRED_HEADERS = {
        "Tipo Identificación", 
        "Número Identificación", 
        "Dígito Verificación", 
        "Tipo Persona", 
        "Nombres", 
        "Apellidos", 
        "Razón Social", 
        "Estado",
        "Tipos de Tercero",
        "Dirección",
        "Teléfono",
        "Email"
    };
    
    /**
     * Encabezados opcionales para importación de terceros.
     * Estos campos pueden estar presentes o ausentes en el archivo Excel.
     */
    public static final String[] OPTIONAL_HEADERS = {
        "Género",
        "País",
        "Departamento",
        "Ciudad"
    };
    
    
    // ===== NOMBRES DE COLUMNAS =====
    
    /**
     * Nombres de columnas para mapeo directo.
     */
    public static final String TYPES_COLUMN = "Tipos de Tercero";
    public static final String COUNTRY_COLUMN = "País";
    public static final String STATE_COLUMN = "Departamento";  
    public static final String CITY_COLUMN = "Ciudad";
    public static final String ADDRESS_COLUMN = "Dirección";
    public static final String PHONE_COLUMN = "Teléfono";
    public static final String EMAIL_COLUMN = "Email";
    

    // ===== MENSAJES DE ERROR COMUNES =====
    
    /**
     * Mensajes de error estándar para importación.
     */
    public static final class ErrorMessages {
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
        public static final String BUSINESS_RULE_VIOLATION = "BUSINESS_RULE_VIOLATION";
        public static final String DUPLICATE_RECORD = "DUPLICATE_RECORD";
        public static final String MISSING_CITY_FOR_COMPLETE_ADDRESS = "MISSING_CITY_FOR_COMPLETE_ADDRESS";
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
        public static final int COLUMN_START_INDEX = 1;
        
        private Defaults() {}
    }
}
