package com.thirdsmanagement.thirds.domain.model;

/**
 * Enumeración que define la clasificación de personas para tipos de identificación.
 * Determina si un tipo de identificación aplica para personas naturales, jurídicas o ambas.
 */
public enum PersonClassification {
    
    /**
     * Tipo de identificación válido únicamente para personas naturales.
     * Ejemplos: CC, CE, TI, PA
     */
    NATURAL_PERSON("NATURAL_PERSON", "Persona Natural"),
    
    /**
     * Tipo de identificación válido únicamente para personas jurídicas.
     * Ejemplos: NIT, RUT
     */
    LEGAL_ENTITY("LEGAL_ENTITY", "Persona Jurídica");
    
    private final String code;
    private final String description;
    
    /**
     * Constructor del enum PersonClassification.
     * 
     * @param code código identificador de la clasificación
     * @param description descripción legible de la clasificación
     */
    PersonClassification(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * Obtiene el código de la clasificación.
     * 
     * @return código de la clasificación
     */
    public String getCode() {
        return code;
    }
    
    /**
     * Obtiene la descripción de la clasificación.
     * 
     * @return descripción de la clasificación
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Verifica si la clasificación es válida para personas naturales.
     * 
     * @return true si es válida para personas naturales
     */
    public boolean isValidForNaturalPerson() {
        return this == NATURAL_PERSON;
    }
    
    /**
     * Verifica si la clasificación es válida para personas jurídicas.
     * 
     * @return true si es válida para personas jurídicas
     */
    public boolean isValidForLegalEntity() {
        return this == LEGAL_ENTITY;
    }
    
    /**
     * Obtiene una clasificación por su código de forma segura.
     * Retorna null si el código no es válido, delegando la validación a las capas superiores.
     * 
     * @param code código de la clasificación
     * @return PersonClassification correspondiente al código, o null si no existe
     */
    public static PersonClassification fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        
        for (PersonClassification classification : values()) {
            if (classification.getCode().equalsIgnoreCase(code.trim())) {
                return classification;
            }
        }
        
        return null; // No lanza excepción, retorna null
    }
    
    /**
     * Verifica si un código es válido sin lanzar excepciones.
     * 
     * @param code código a validar
     * @return true si el código es válido, false en caso contrario
     */
    public static boolean isValidCode(String code) {
        return fromCode(code) != null;
    }
}
