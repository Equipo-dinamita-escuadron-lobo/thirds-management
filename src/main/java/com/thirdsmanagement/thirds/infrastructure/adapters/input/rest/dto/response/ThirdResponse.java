package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @brief DTO de respuesta para información básica de tercero
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThirdResponse {

    private Long id;

    private String name;

    private String description;
}
