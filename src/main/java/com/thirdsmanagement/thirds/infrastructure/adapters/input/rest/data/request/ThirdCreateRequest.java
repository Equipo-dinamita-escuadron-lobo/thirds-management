package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request;

import java.time.LocalDate;
import java.util.Set;

import com.thirdsmanagement.thirds.domain.model.ePersonType;
import com.thirdsmanagement.thirds.domain.model.eThirdGender;
import com.thirdsmanagement.thirds.infrastructure.adapters.deserializers.ThirdTypeDeserializer;
import com.thirdsmanagement.thirds.infrastructure.adapters.deserializers.TypeIdDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ThirdCreateRequest {
    
    private Long thId;

    @NotNull(message = "Enterprise ID cannot be empty") 
    private String entId;

    @JsonDeserialize(using = TypeIdDeserializer.class)
    @NotNull(message = "The id type cannot be empty") 
    private TypeId typeId;

    @NotNull(message = "The person type cannot be empty")  
    private ePersonType personType; 
    
    @JsonDeserialize(using = ThirdTypeDeserializer.class)
    @NotNull(message = "The third type cannot be empty") 
    private Set<ThirdType> thirdTypes;

    private String rutPath;
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

    @NotBlank(message = "Address cannot be empty")  // Validación para address
    private String address;

    @NotBlank(message = "Phone number cannot be empty") // Validación para phoneNumber
    private String phoneNumber; 

    @NotBlank(message = "Email cannot be empty") // Validación para email
    @Email(message = "Email should be valid") // Validación adicional para el formato del email
    private String email; 

    private LocalDate creationDate;
    private LocalDate updateDate;
}

