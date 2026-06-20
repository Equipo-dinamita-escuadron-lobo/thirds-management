package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @brief DTO de respuesta para información de estados/departamentos
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StateResponse {

    private String stateCode;
    private String stateName;
    private String countryCode;
}
