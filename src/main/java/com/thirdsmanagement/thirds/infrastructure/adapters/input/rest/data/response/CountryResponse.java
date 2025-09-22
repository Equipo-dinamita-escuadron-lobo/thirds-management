package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de respuesta para información de países.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CountryResponse {
    
    private String countryCode;
    private String countryName;
}
