package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @brief DTO de respuesta para cambio de estado de tercero individual
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeThirdStateResponse {
    private Boolean result;
}
