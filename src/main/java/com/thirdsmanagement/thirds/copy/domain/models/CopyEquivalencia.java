package com.thirdsmanagement.thirds.copy.domain.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Modelo de dominio para equivalencia de ID entre empresa origen y destino.
 */
@Getter
@Builder
@EqualsAndHashCode
public class CopyEquivalencia {

    /** Nombre de la tabla (ej: "thirds", "third_type", "type_id") */
    private String tabla;

    /** ID antiguo en la empresa origen */
    private String idViejo;

    /** ID nuevo en la empresa destino */
    private String idNuevo;
}
