package com.thirdsmanagement.thirds.domain.enums;

import lombok.Getter;

/**
 * Tipos de errores que pueden ocurrir durante la importación de terceros.
 * Clasifica los errores para facilitar su manejo y presentación al usuario.
 */
@Getter
public enum ImportErrorType {
    
    /**
     * Error en la validación de formato o contenido de campos.
     * Ejemplos: campo requerido faltante, formato de email inválido.
     */
    VALIDATION_ERROR("Error de Validación"),
    
    /**
     * Error en el formato de datos dentro del archivo.
     * Ejemplos: tipo de dato incorrecto, formato de fecha inválido.
     */
    FORMAT_ERROR("Error de Formato"),
    
    /**
     * Error de referencia a datos maestros inexistentes.
     * Ejemplos: tipo de identificación no existe, país no encontrado.
     */
    REFERENCE_ERROR("Error de Referencia"),
    
    /**
     * Error en la aplicación de reglas de negocio.
     * Ejemplos: incompatibilidad tipo ID con tipo persona, NIT inválido.
     */
    BUSINESS_RULE_ERROR("Error de Regla de Negocio"),
    
    /**
     * Error por registro duplicado interno o con la base de datos.
     * Ejemplos: número de identificación ya existe.
     */
    DUPLICATE_ERROR("Error de Duplicado"),
    
    /**
     * Error del sistema durante el procesamiento.
     * Ejemplos: error de base de datos, error de conexión.
     */
    SYSTEM_ERROR("Error del Sistema");

    private final String description;

    ImportErrorType(String description) {
        this.description = description;
    }

    /**
     * Verifica si el error es de tipo recuperable.
     * Los errores recuperables pueden ser corregidos por el usuario.
     * 
     * @return true si el error es recuperable
     */
    public boolean isRecoverable() {
        return this != SYSTEM_ERROR;
    }

    /**
     * Verifica si el error requiere acción del usuario.
     * 
     * @return true si requiere corrección por parte del usuario
     */
    public boolean requiresUserAction() {
        return this == VALIDATION_ERROR || 
               this == FORMAT_ERROR || 
               this == REFERENCE_ERROR || 
               this == BUSINESS_RULE_ERROR ||
               this == DUPLICATE_ERROR;
    }

    /**
     * Obtiene la prioridad del error para ordenamiento.
     * Menor número = mayor prioridad.
     * 
     * @return nivel de prioridad (1-6)
     */
    public int getPriority() {
        return switch (this) {
            case SYSTEM_ERROR -> 1;
            case BUSINESS_RULE_ERROR -> 2;
            case REFERENCE_ERROR -> 3;
            case VALIDATION_ERROR -> 4;
            case FORMAT_ERROR -> 5;
            case DUPLICATE_ERROR -> 6;
        };
    }
}
