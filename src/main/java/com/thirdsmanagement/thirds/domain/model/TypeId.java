package com.thirdsmanagement.thirds.domain.model;

import jakarta.validation.constraints.NotBlank;
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

    
    @NotBlank(message = "El ID de la empresa no puede estar vacío")
    @Size(max = 50, message = "El ID de la empresa no puede exceder los 50 caracteres")
    private String entId;


    @NotBlank(message = "El código del tipo de identificación no puede estar vacío")
    @Size(max = 10, message = "El código del tipo de identificación no puede exceder los 10 caracteres")
    private String typeId;

    @NotBlank(message = "El nombre del tipo de identificación no puede estar vacío")
    @Size(max = 100, message = "El nombre del tipo de identificación no puede exceder los 100 caracteres")
    private String typeIdname;

    @Builder.Default
    private Boolean status = true;

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
     * Obtiene una representación normalizada del código del tipo de identificación.
     *
     * @return código normalizado
     */
    public String getNormalizedTypeId() {
        return com.thirdsmanagement.thirds.domain.utils.StringNormalizer.normalizeCode(typeId);
    }
}
