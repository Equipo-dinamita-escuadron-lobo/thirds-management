package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para las peticiones de actualización de tipo de tercero.
 * Contiene los datos necesarios para actualizar un tipo de tercero existente.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdTypeUpdateRequest {

    /**
     * ID del tipo de tercero a actualizar.
     */
    @NotNull(message = "El ID del tipo de tercero es obligatorio")
    private Long thirdTypeId;

    /**
     * Nombre del tipo de tercero.
     */
    @NotBlank(message = "El nombre del tipo de tercero es obligatorio")
    private String thirdTypeName;

    /**
     * ID de la entidad a la que pertenece el tipo de tercero.
     */
    @NotBlank(message = "El ID de la entidad es obligatorio")
    private String entId;

    /**
     * Estado del tipo de tercero.
     * true = activo, false = inactivo
     */
    @Builder.Default
    private Boolean status = true;
}
