package com.thirdsmanagement.thirds.domain.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @Size(max = 500, message = "La ruta del RUT no puede exceder los 500 caracteres")
    private String rutPath;

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

    @Size(max = 500, message = "La ruta de la foto no puede exceder los 500 caracteres")
    private String photoPath;

    @Size(max = 100, message = "El país no puede exceder los 100 caracteres")
    private String country;

    @Size(max = 100, message = "La provincia no puede exceder los 100 caracteres")
    private String province;

    @Size(max = 100, message = "La ciudad no puede exceder los 100 caracteres")
    private String city;

    @Size(max = 300, message = "La dirección no puede exceder los 300 caracteres")
    private String address;

    @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{7,20}$", message = "El formato del teléfono no es válido")
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String phoneNumber;

    @Email(message = "El formato del correo electrónico no es válido")
    @Size(max = 150, message = "El correo electrónico no puede exceder los 150 caracteres")
    private String email;

    @PastOrPresent(message = "La fecha de creación debe ser en el pasado o presente")
    private LocalDate creationDate;
    @PastOrPresent(message = "La fecha de actualización debe ser en el pasado o presente")
    private LocalDate updateDate;

    @JsonIgnore
    public boolean isActive() {
        return Boolean.TRUE.equals(state);
    }

    public void activate() {
        this.state = true;
        this.updateDate = LocalDate.now();
    }

    public void deactivate() {
        this.state = false;
        this.updateDate = LocalDate.now();
    }

    public boolean isLegalEntity() {
        return ePersonType.Juridica.equals(personType);
    }

    public boolean isNaturalPerson() {
        return ePersonType.Natural.equals(personType);
    }

    public void markAsUpdated() {
        this.updateDate = LocalDate.now();
    }
}