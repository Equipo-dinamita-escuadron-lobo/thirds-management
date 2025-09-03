package com.thirdsmanagement.thirds.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entidad de dominio que representa un tipo de identificación.
 * Define los diferentes tipos de documentos de identidad válidos en el sistema.
 *
 * Ejemplos: CC (Cédula de Ciudadanía), NIT (Número de Identificación Tributaria),
 * CE (Cédula de Extranjería), etc.
 */
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = {"entId", "typeId"})
@ToString(of = {"typeId", "typeIdname"})
@AllArgsConstructor
@NoArgsConstructor
public class TypeId {

    /**
     * Identificador de la empresa a la que pertenece el tipo de identificación.
     * Permite segmentar los tipos de identificación por empresa.
     */
    @NotBlank(message = "El ID de la empresa no puede estar vacío")
    @Size(max = 50, message = "El ID de la empresa no puede exceder los 50 caracteres")
    private String entId;

    /**
     * Código único del tipo de identificación.
     * Debe ser un código alfanumérico sin espacios.
     */
    @NotBlank(message = "El código del tipo de identificación no puede estar vacío")
    @Pattern(regexp = "^[A-Z0-9]{2,10}$", message = "El código debe contener solo letras mayúsculas y números, entre 2 y 10 caracteres")
    @Size(max = 10, message = "El código del tipo de identificación no puede exceder los 10 caracteres")
    private String typeId;

    /**
     * Nombre descriptivo del tipo de identificación.
     * Ejemplo: "Cédula de Ciudadanía", "Número de Identificación Tributaria".
     */
    @NotBlank(message = "El nombre del tipo de identificación no puede estar vacío")
    @Size(max = 100, message = "El nombre del tipo de identificación no puede exceder los 100 caracteres")
    private String typeIdname;

    /**
     * Verifica si el tipo de identificación es válido para personas naturales.
     *
     * @return true si es aplicable para personas naturales
     */
    public boolean isValidForNaturalPerson() {
        return "CC".equalsIgnoreCase(typeId) ||
               "CE".equalsIgnoreCase(typeId) ||
               "PA".equalsIgnoreCase(typeId) ||
               "TI".equalsIgnoreCase(typeId);
    }

    /**
     * Verifica si el tipo de identificación es válido para personas jurídicas.
     *
     * @return true si es aplicable para personas jurídicas
     */
    public boolean isValidForLegalEntity() {
        return "NIT".equalsIgnoreCase(typeId) ||
               "RU".equalsIgnoreCase(typeId);
    }

    /**
     * Obtiene una representación en mayúsculas del código del tipo de identificación.
     *
     * @return código en mayúsculas
     */
    public String getNormalizedTypeId() {
        return typeId != null ? typeId.toUpperCase() : null;
    }
}
