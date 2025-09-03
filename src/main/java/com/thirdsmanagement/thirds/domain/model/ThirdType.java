package com.thirdsmanagement.thirds.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tipo de tercero.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThirdType {
    private String entId;
    private Long thirdTypeId;
    private String thirdTypeName;
}
