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
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa la petición de actualización de un tercero.
 * Incluye validación geográfica con códigos de país, estado y ciudad.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThirdUpdateRequest {
    
    @NotNull(message = "Third ID cannot be empty")
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
    
    @NotBlank(message = "Country code cannot be empty")
    @Size(max = 3, message = "Country code cannot exceed 3 characters")
    private String countryCode;

    @NotBlank(message = "State code cannot be empty") 
    @Size(max = 10, message = "State code cannot exceed 10 characters")
    private String stateCode;

    @NotBlank(message = "City code cannot be empty")
    @Size(max = 10, message = "City code cannot exceed 10 characters") 
    private String cityCode;

    @NotBlank(message = "Address cannot be empty")
    private String address;

    @NotBlank(message = "Phone number cannot be empty")
    private String phoneNumber; 

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email; 

    private LocalDate creationDate;
    private LocalDate updateDate;
}
