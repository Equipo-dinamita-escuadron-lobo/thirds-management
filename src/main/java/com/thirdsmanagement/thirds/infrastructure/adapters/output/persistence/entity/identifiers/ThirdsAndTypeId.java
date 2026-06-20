package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.identifiers;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * @brief Clave primaria compuesta @Embeddable para thirds_and_types
 */
@Data
@Embeddable
public class ThirdsAndTypeId implements Serializable{
    private Long thId;
    private Long ttId;
}
