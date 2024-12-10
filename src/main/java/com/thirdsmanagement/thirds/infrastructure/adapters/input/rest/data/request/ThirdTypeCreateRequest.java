package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa la petición de creación de un tipo de tercero.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThirdTypeCreateRequest {
    @NotNull(message = "Enterprise ID not be empty") 
    private String entId;

    private long thirdTypeId;

    @NotNull(message = "Enterprise ThirdTypeName not be empty") 
    private String thirdTypeName;
    
}
