package com.thirdsmanagement.thirds.domain.enums;

/**
 * Campos opcionales exportables para terceros.
 * Representa los atributos que pueden incluirse opcionalmente en la exportación.
 */
public enum ExportableField {
    GENDER("Género", "gender"),
    COUNTRY("País", "country"),
    STATE("Departamento", "state"),
    CITY("Ciudad", "city");

    private final String displayName;
    private final String fieldName;

    ExportableField(String displayName, String fieldName) {
        this.displayName = displayName;
        this.fieldName = fieldName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFieldName() {
        return fieldName;
    }

    /**
     * Obtiene el ExportableField a partir del nombre del campo.
     * 
     * @param fieldName nombre del campo
     * @return ExportableField correspondiente o null si no existe
     */
    public static ExportableField fromFieldName(String fieldName) {
        if (fieldName == null) {
            return null;
        }
        for (ExportableField field : values()) {
            if (field.fieldName.equalsIgnoreCase(fieldName)) {
                return field;
            }
        }
        return null;
    }
}
