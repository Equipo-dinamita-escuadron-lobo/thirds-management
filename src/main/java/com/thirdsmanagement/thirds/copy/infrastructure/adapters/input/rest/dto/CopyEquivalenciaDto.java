package com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de equivalencia cross-servicio en el request de copia.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopyEquivalenciaDto {

    /** Módulo que generó la equivalencia (ej: "CATALOGUE") */
    private String modulo;

    /** Nombre de la tabla (ej: "account", "tax") */
    private String tabla;

    /** ID en la empresa origen */
    private String idViejo;

    /** ID en la empresa destino */
    private String idNuevo;
}
