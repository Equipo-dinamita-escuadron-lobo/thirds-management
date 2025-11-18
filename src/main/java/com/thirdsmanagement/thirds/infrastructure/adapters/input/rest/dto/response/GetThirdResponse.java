package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response;

import java.util.Set;

import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @brief DTO de respuesta para consulta de tercero específico
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetThirdResponse {
    private Long thId;
    private Long entId;

    private TypeId typeId;

    private Set<ThirdType> thirdTypes;

    private ePersonType personType;
    private String names;
    private String lastNames;
    private String socialReason;
    private eThirdGender gender;
    private Long idNumber;
    private Long verificationNumber;
    private Boolean state;
    private String country;
    private String province;
    private String city;
    private String address;
    private String phoneNumber;
    private String email;
    private Integer usageCount;
}
