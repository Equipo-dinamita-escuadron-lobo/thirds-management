package com.thirdsmanagement.thirds.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Modelo que representa los datos de un tercero parseados desde Excel.
 * Contiene los datos en formato raw antes de ser convertidos a entidades de dominio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdExcelData {

    /**
     * Número de fila en el Excel donde se encuentra este registro.
     */
    private Integer rowNumber;

    /**
     * Identificador de la entidad.
     */
    private String entId;

    /**
     * Nombre del tipo de identificación (ej: "CC", "NIT", "CE").
     */
    private String typeIdName;

    /**
     * Número de identificación.
     */
    private Long idNumber;

    /**
     * Dígito de verificación.
     */
    private Long verificationNumber;

    /**
     * Tipo de persona (Natural o Jurídica).
     */
    private ePersonType personType;

    /**
     * Nombres (para persona natural).
     */
    private String names;

    /**
     * Apellidos (para persona natural).
     */
    private String lastNames;

    /**
     * Razón social (para persona jurídica).
     */
    private String socialReason;

    /**
     * Género.
     */
    private eThirdGender gender;

    /**
     * Estado del tercero (activo/inactivo).
     */
    private Boolean state;

    /**
     * Nombre del país.
     */
    private String countryName;

    /**
     * Nombre del estado/departamento.
     */
    private String stateName;

    /**
     * Nombre de la ciudad.
     */
    private String cityName;

    /**
     * Dirección.
     */
    private String address;

    /**
     * Número de teléfono.
     */
    private String phoneNumber;

    /**
     * Correo electrónico.
     */
    private String email;

    /**
     * Nombres de los tipos de tercero (ej: "Cliente", "Proveedor").
     */
    private Set<String> thirdTypesNames;

    /**
     * Verifica si los datos básicos están presentes para validación.
     */
    public boolean hasRequiredFields() {
        return typeIdName != null && !typeIdName.trim().isEmpty()
                && idNumber != null
                && personType != null
                && address != null && !address.trim().isEmpty()
                && phoneNumber != null && !phoneNumber.trim().isEmpty()
                && email != null && !email.trim().isEmpty();
    }

    /**
     * Verifica si es una persona natural con datos completos.
     */
    public boolean isValidNaturalPerson() {
        return personType == ePersonType.Natural
                && names != null && !names.trim().isEmpty()
                && lastNames != null && !lastNames.trim().isEmpty()
                && gender != null
                && (socialReason == null || socialReason.trim().isEmpty());
    }

    /**
     * Verifica si es una persona jurídica con datos completos.
     */
    public boolean isValidLegalEntity() {
        return personType == ePersonType.Juridica
                && socialReason != null && !socialReason.trim().isEmpty()
                && (names == null || names.trim().isEmpty())
                && (lastNames == null || lastNames.trim().isEmpty())
                && gender == null;
    }

    /**
     * Obtiene una representación String del registro para logging y errores.
     */
    public String toLogString() {
        return String.format("Fila %d: %s %s - %s %s",
                rowNumber,
                typeIdName != null ? typeIdName : "N/A",
                idNumber != null ? idNumber : "N/A",
                personType != null ? personType : "N/A",
                personType == ePersonType.Natural 
                    ? (names + " " + lastNames) 
                    : socialReason);
    }
}
