package com.thirdsmanagement.thirds.domain.model;

/**
 * Enumeración que define los géneros disponibles para terceros en el sistema.
 * Se utiliza principalmente para personas naturales y proporciona opciones
 * inclusivas para diferentes identidades de género.
 *
 * @author Sistema de Gestión de Terceros
 * @version 1.0
 * @since 2024
 */
public enum eThirdGender {

    /**
     * Género masculino: Para personas que se identifican como hombres.
     */
    Masculino("M", "Masculino"),

    /**
     * Género femenino: Para personas que se identifican como mujeres.
     */
    Femenino("F", "Femenino"),

    /**
     * Otro género: Para personas que no se identifican con los géneros
     * tradicionales o prefieren no especificar.
     */
    Otro("O", "Otro/Preferir no decir");

    private final String code;
    private final String description;

    eThirdGender(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Obtiene el código del género.
     *
     * @return código de una letra
     */
    public String getCode() {
        return code;
    }

    /**
     * Obtiene la descripción completa del género.
     *
     * @return descripción completa
     */
    public String getDescription() {
        return description;
    }

    /**
     * Verifica si el género es masculino.
     *
     * @return true si es masculino
     */
    public boolean isMasculino() {
        return this == Masculino;
    }

    /**
     * Verifica si el género es femenino.
     *
     * @return true si es femenino
     */
    public boolean isFemenino() {
        return this == Femenino;
    }

    /**
     * Verifica si el género es otro o no especificado.
     *
     * @return true si es otro género
     */
    public boolean isOtro() {
        return this == Otro;
    }

    /**
     * Obtiene el género a partir de su código.
     *
     * @param code código del género
     * @return género correspondiente, o null si no existe
     */
    public static eThirdGender fromCode(String code) {
        if (code == null) {
            return null;
        }

        for (eThirdGender gender : values()) {
            if (gender.code.equalsIgnoreCase(code)) {
                return gender;
            }
        }
        return null;
    }

    /**
     * Obtiene el género a partir de su descripción.
     *
     * @param description descripción del género
     * @return género correspondiente, o null si no existe
     */
    public static eThirdGender fromDescription(String description) {
        if (description == null) {
            return null;
        }

        for (eThirdGender gender : values()) {
            if (gender.description.equalsIgnoreCase(description)) {
                return gender;
            }
        }
        return null;
    }

    /**
     * Verifica si el código proporcionado es válido.
     *
     * @param code código a validar
     * @return true si el código es válido
     */
    public static boolean isValidCode(String code) {
        return fromCode(code) != null;
    }

    @Override
    public String toString() {
        return description;
    }
}
