package com.thirdsmanagement.thirds.domain.model;
import com.thirdsmanagement.thirds.domain.utils.StringNormalizer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Builder
@Getter
@Setter
@EqualsAndHashCode(of = { "id" })
@ToString(of = { "id", "typeId", "typeIdname", "classification" })
@AllArgsConstructor
@NoArgsConstructor
public class TypeId {

    private Long id;

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

    @NotNull(message = "La clasificación de persona no puede estar vacía")
    private PersonClassification classification;

    /**
     * Verifica si este tipo de identificación es válido para personas naturales.
     * Utiliza la clasificación configurada en lugar de validaciones hardcodeadas.
     * 
     * @return true si es válido para personas naturales
     */
    public boolean isValidForNaturalPerson() {
        return classification != null && classification.isValidForNaturalPerson();
    }

    /**
     * Verifica si este tipo de identificación es válido para personas jurídicas.
     * Utiliza la clasificación configurada en lugar de validaciones hardcodeadas.
     * 
     * @return true si es válido para personas jurídicas
     */
    public boolean isValidForLegalEntity() {
        return classification != null && classification.isValidForLegalEntity();
    }

    public String getNormalizedTypeId() {
        return StringNormalizer.normalizeCode(typeId);
    }
}
