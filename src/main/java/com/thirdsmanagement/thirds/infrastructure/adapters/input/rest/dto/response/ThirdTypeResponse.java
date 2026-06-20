package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @brief DTO de respuesta para información de tipo de tercero
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThirdTypeResponse {
    private Long thirdTypeId;
    private String thirdTypeName;
    private Boolean status;
}
