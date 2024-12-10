package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response;

import java.time.LocalDate;
import java.util.Set;

import com.thirdsmanagement.thirds.domain.model.ePersonType;
import com.thirdsmanagement.thirds.domain.model.eThirdGender;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa la respuesta de la operación de obtención de un tercero.
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

    private String rutPath; 
    private ePersonType personType; 
    private String names; 
    private String lastNames; 
    private String socialReason; 
    private eThirdGender gender;
    private Long idNumber;
    private Long verificationNumber; 
    private Boolean state;
    private String photoPath;
    private String country;
    private String province;
    private String city; 
    private String address;
    private String phoneNumber; 
    private String email; 

    private LocalDate creationDate;
    private LocalDate updateDate;
}
