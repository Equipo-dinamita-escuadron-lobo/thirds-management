package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase de clave compuesta para StateEntity.
 * Representa la clave primaria compuesta por stateCode y countryCode.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class StateEntityId implements Serializable {
    
    private String stateCode;
    private String countryCode;
}
