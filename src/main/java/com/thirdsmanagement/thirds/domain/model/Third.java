package com.thirdsmanagement.thirds.domain.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @brief Modelo de dominio que representa un tercero en el sistema
 *
 * Entidad principal que representa a personas naturales o jurídicas (terceros)
 * con información completa de identificación, contacto, ubicación geográfica
 * y categorización por tipos (cliente, proveedor, empleado, etc.).
 */
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = {"entId", "idNumber", "typeId"})
@ToString(of = {"entId", "idNumber", "names", "lastNames", "socialReason"})
@AllArgsConstructor
@NoArgsConstructor
public class Third {

    private Long thId;

    @NotBlank(message = "El ID de la empresa no puede estar vacío")
    @Size(max = 50, message = "El ID de la empresa no puede exceder los 50 caracteres")
    private String entId;

    @NotNull(message = "El tipo de identificación es obligatorio")
    private TypeId typeId;

    @Builder.Default
    @NotNull(message = "Los tipos de tercero no pueden ser null")
    private Set<ThirdType> thirdTypes = new HashSet<>();

    @NotNull(message = "El tipo de persona es obligatorio")
    private ePersonType personType;

    @Size(max = 100, message = "Los nombres no pueden exceder los 100 caracteres")
    private String names;
    @Size(max = 100, message = "Los apellidos no pueden exceder los 100 caracteres")
    private String lastNames;
    @Size(max = 200, message = "La razón social no puede exceder los 200 caracteres")
    private String socialReason;
    private eThirdGender gender;

    @NotNull(message = "El número de identificación es obligatorio")
    private Long idNumber;
    private Long verificationNumber;


    @NotNull(message = "El estado del tercero es obligatorio")
    @Builder.Default
    private Boolean state = true;

    private Country country;
    private State province;
    private City city;

    @Size(max = 300, message = "La dirección no puede exceder los 300 caracteres")
    private String address;

    @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{7,20}$", message = "El formato del teléfono no es válido")
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String phoneNumber;

    @Email(message = "El formato del correo electrónico no es válido")
    @Size(max = 150, message = "El correo electrónico no puede exceder los 150 caracteres")
    private String email;

    @JsonIgnore
    public boolean isActive() {
        return Boolean.TRUE.equals(state);
    }

    public void activate() {
        this.state = true;
    }

    public void deactivate() {
        this.state = false;
    }

    public boolean isLegalEntity() {
        return ePersonType.Juridica.equals(personType);
    }

    public boolean isNaturalPerson() {
        return ePersonType.Natural.equals(personType);
    }
}