package com.thirdsmanagement.thirds.domain.model;

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

/**
 * Modelo de dominio que representa un estado/departamento.
 * Contiene la información básica de un estado para la jerarquía geográfica.
 */
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = {"stateCode", "countryCode"})
@ToString(of = {"stateCode", "stateName", "countryCode"})
@AllArgsConstructor
@NoArgsConstructor
public class State {

    @NotBlank(message = "El código del estado no puede estar vacío")
    @Size(max = 10, message = "El código del estado no puede exceder los 10 caracteres")
    private String stateCode;

    @NotBlank(message = "El nombre del estado no puede estar vacío")
    @Size(max = 100, message = "El nombre del estado no puede exceder los 100 caracteres")
    private String stateName;

    @NotNull(message = "El país es obligatorio")
    private Country country;

    @NotBlank(message = "El código del país no puede estar vacío")
    @Size(max = 3, message = "El código del país no puede exceder los 3 caracteres")
    private String countryCode;

}
