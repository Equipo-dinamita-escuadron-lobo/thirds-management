package com.thirdsmanagement.thirds.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThirdType {
    private Long entId;
    private long thirdTypeId;
    private String thirdTypeName;
}
