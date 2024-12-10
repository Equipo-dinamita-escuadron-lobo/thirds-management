package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.identifiers;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * Clase que representa la clave primaria compuesta de la tabla thirds_and_type.
 * Contiene el identificador de un tercero y el identificador de un tipo de tercero.
 */
@Data
@Embeddable
public class ThirdsAndTypeId implements Serializable{
    private Long thId;
    private Long ttId;
}
