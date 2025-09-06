package com.thirdsmanagement.thirds.domain.model;


public enum ePersonType {

    Natural("NATURAL", "Persona Natural"),

    Juridica("JURIDICA", "Persona Jurídica");

    private final String code;
    private final String description;

    ePersonType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isNatural() {
        return this == Natural;
    }

    public boolean isJuridica() {
        return this == Juridica;
    }

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
