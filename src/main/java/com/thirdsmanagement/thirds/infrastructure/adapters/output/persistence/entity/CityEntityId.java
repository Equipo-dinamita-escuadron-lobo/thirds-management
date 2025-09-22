package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase de clave compuesta para CityEntity.
 * Representa la clave primaria compuesta por cityCode, stateCode y countryCode.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CityEntityId implements Serializable {
    
    private String cityCode;
    private String stateCode;
    private String countryCode;
}
