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

    Masculino("M", "Masculino"),
    Femenino("F", "Femenino"),
    Otro("O", "Otro/Preferir no decir");

    private final String code;
    private final String description;

    eThirdGender(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMasculino() {
        return this == Masculino;
    }

    public boolean isFemenino() {
        return this == Femenino;
    }

    public boolean isOtro() {
        return this == Otro;
    }

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

    public static boolean isValidCode(String code) {
        return fromCode(code) != null;
    }

    @Override
    public String toString() {
        return description;
    }
}
