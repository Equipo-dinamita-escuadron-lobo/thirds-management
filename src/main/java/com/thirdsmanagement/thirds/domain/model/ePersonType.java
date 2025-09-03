package com.thirdsmanagement.thirds.domain.model;

/**
 * Enumeración que define los tipos de persona en el sistema.
 * Un tercero puede ser clasificado como persona natural o jurídica.
 *
 * @author Sistema de Gestión de Terceros
 * @version 1.0
 * @since 2024
 */
public enum ePersonType {

    /**
     * Persona Natural: Representa a individuos con capacidad jurídica
     * para realizar actos civiles. Ejemplos: empleados, clientes particulares.
     */
    Natural("NATURAL", "Persona Natural"),

    /**
     * Persona Jurídica: Representa a entidades con personalidad jurídica
     * propia. Ejemplos: empresas, fundaciones, corporaciones.
     */
    Juridica("JURIDICA", "Persona Jurídica");

    private final String code;
    private final String description;

    ePersonType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Obtiene el código del tipo de persona.
     *
     * @return código en mayúsculas
     */
    public String getCode() {
        return code;
    }

    /**
     * Obtiene la descripción del tipo de persona.
     *
     * @return descripción completa
     */
    public String getDescription() {
        return description;
    }

    /**
     * Verifica si el tipo de persona es natural.
     *
     * @return true si es persona natural
     */
    public boolean isNatural() {
        return this == Natural;
    }

    /**
     * Verifica si el tipo de persona es jurídica.
     *
     * @return true si es persona jurídica
     */
    public boolean isJuridica() {
        return this == Juridica;
    }

    /**
     * Obtiene el tipo de persona a partir de su código.
     *
     * @param code código del tipo de persona
     * @return tipo de persona correspondiente, o null si no existe
     */
    public static ePersonType fromCode(String code) {
        if (code == null) {
            return null;
        }

        for (ePersonType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Obtiene el tipo de persona a partir de su descripción.
     *
     * @param description descripción del tipo de persona
     * @return tipo de persona correspondiente, o null si no existe
     */
    public static ePersonType fromDescription(String description) {
        if (description == null) {
            return null;
        }

        for (ePersonType type : values()) {
            if (type.description.equalsIgnoreCase(description)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return description;
    }
}
