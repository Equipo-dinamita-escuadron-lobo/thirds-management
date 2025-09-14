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
    
    @NotNull(message = "El ID del tercero no puede estar vacío")
    private Long thId;

    @NotNull(message = "El ID de la empresa no puede estar vacío") 
    private String entId;

    @JsonDeserialize(using = TypeIdDeserializer.class)
    @NotNull(message = "El tipo de identificación no puede estar vacío") 
    private TypeId typeId;

    @NotNull(message = "El tipo de persona no puede estar vacío")  
    private ePersonType personType; 
    
    @JsonDeserialize(using = ThirdTypeDeserializer.class)
    @NotNull(message = "El tipo de tercero no puede estar vacío") 
    private Set<ThirdType> thirdTypes;

    private String names; 
    private String lastNames; 
    private String socialReason; 
    private eThirdGender gender;
    private Long idNumber;
    private Long verificationNumber; 
    @Builder.Default
    private Boolean state = true;
    
    @NotBlank(message = "El código del país no puede estar vacío")
    @Size(max = 3, message = "El código del país no puede exceder los 3 caracteres")
    private String countryCode;

    @NotBlank(message = "El código del estado no puede estar vacío") 
    @Size(max = 10, message = "El código del estado no puede exceder los 10 caracteres")
    private String stateCode;

    @NotBlank(message = "El código de la ciudad no puede estar vacío")
    @Size(max = 10, message = "El código de la ciudad no puede exceder los 10 caracteres") 
    private String cityCode;

    @NotBlank(message = "La dirección no puede estar vacía")
    private String address;

    @NotBlank(message = "El número de teléfono no puede estar vacío")
    private String phoneNumber; 

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "El correo electrónico debe tener un formato válido")
    private String email; 

    private LocalDate creationDate;
    private LocalDate updateDate;
}
