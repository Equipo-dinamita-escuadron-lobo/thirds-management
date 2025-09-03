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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad de dominio que representa un tercero en el sistema.
 * Un tercero puede ser una persona natural o jurídica que interactúa con la empresa.
 *
 * Esta clase sigue el patrón de entidad de dominio rico, conteniendo tanto
 * datos como comportamiento relacionado con la lógica de negocio.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Third {

    /**
     * Identificador único del tercero.
     */
    private Long thId;

    /**
     * Identificador de la empresa a la que pertenece el tercero.
     * Campo obligatorio para segmentar datos por empresa.
     */
    @NotBlank(message = "El ID de la empresa no puede estar vacío")
    @Size(max = 50, message = "El ID de la empresa no puede exceder los 50 caracteres")
    private String entId;

    /**
     * Tipo de identificación del tercero.
     * Define el tipo de documento de identificación (CC, NIT, etc.).
     */
    @NotNull(message = "El tipo de identificación es obligatorio")
    private TypeId typeId;

    /**
     * Conjunto de tipos de tercero asociados.
     * Un tercero puede tener múltiples roles (cliente, proveedor, etc.).
     */
    @Builder.Default
    @NotNull(message = "Los tipos de tercero no pueden ser null")
    private Set<ThirdType> thirdTypes = new HashSet<>();

    /**
     * Ruta del archivo RUT (Registro Único Tributario) en el sistema de archivos.
     */
    @Size(max = 500, message = "La ruta del RUT no puede exceder los 500 caracteres")
    private String rutPath;

    /**
     * Tipo de persona: Natural o Jurídica.
     */
    @NotNull(message = "El tipo de persona es obligatorio")
    private ePersonType personType;

    /**
     * Nombres del tercero (aplicable para personas naturales).
     */
    @Size(max = 100, message = "Los nombres no pueden exceder los 100 caracteres")
    private String names;

    /**
     * Apellidos del tercero (aplicable para personas naturales).
     */
    @Size(max = 100, message = "Los apellidos no pueden exceder los 100 caracteres")
    private String lastNames;

    /**
     * Razón social del tercero (aplicable para personas jurídicas).
     */
    @Size(max = 200, message = "La razón social no puede exceder los 200 caracteres")
    private String socialReason;

    /**
     * Género del tercero (aplicable para personas naturales).
     */
    private eThirdGender gender;

    /**
     * Número de identificación del tercero.
     */
    @NotNull(message = "El número de identificación es obligatorio")
    private Long idNumber;

    /**
     * Número de verificación del documento de identificación.
     */
    private Long verificationNumber;

    /**
     * Estado del tercero: true (activo) o false (inactivo).
     */
    @NotNull(message = "El estado del tercero es obligatorio")
    @Builder.Default
    private Boolean state = true;

    /**
     * Ruta de la foto del tercero en el sistema de archivos.
     */
    @Size(max = 500, message = "La ruta de la foto no puede exceder los 500 caracteres")
    private String photoPath;

    /**
     * País de residencia del tercero.
     */
    @Size(max = 100, message = "El país no puede exceder los 100 caracteres")
    private String country;

    /**
     * Provincia/Departamento de residencia del tercero.
     */
    @Size(max = 100, message = "La provincia no puede exceder los 100 caracteres")
    private String province;

    /**
     * Ciudad de residencia del tercero.
     */
    @Size(max = 100, message = "La ciudad no puede exceder los 100 caracteres")
    private String city;

    /**
     * Dirección completa del tercero.
     */
    @Size(max = 300, message = "La dirección no puede exceder los 300 caracteres")
    private String address;

    /**
     * Número de teléfono del tercero.
     */
    @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{7,20}$", message = "El formato del teléfono no es válido")
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String phoneNumber;

    /**
     * Correo electrónico del tercero.
     */
    @Email(message = "El formato del correo electrónico no es válido")
    @Size(max = 150, message = "El correo electrónico no puede exceder los 150 caracteres")
    private String email;

    /**
     * Fecha de creación del registro del tercero.
     */
    @PastOrPresent(message = "La fecha de creación debe ser en el pasado o presente")
    private LocalDate creationDate;

    /**
     * Fecha de última actualización del registro del tercero.
     */
    @PastOrPresent(message = "La fecha de actualización debe ser en el pasado o presente")
    private LocalDate updateDate;

    /**
     * Verifica si el tercero está activo en el sistema.
     *
     * @return true si el tercero está activo, false en caso contrario
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(state);
    }

    /**
     * Activa el tercero en el sistema.
     */
    public void activate() {
        this.state = true;
        this.updateDate = LocalDate.now();
    }

    /**
     * Desactiva el tercero en el sistema.
     */
    public void deactivate() {
        this.state = false;
        this.updateDate = LocalDate.now();
    }

    /**
     * Verifica si es una persona jurídica.
     *
     * @return true si es persona jurídica, false en caso contrario
     */
    public boolean isLegalEntity() {
        return ePersonType.Juridica.equals(personType);
    }

    /**
     * Verifica si es una persona natural.
     *
     * @return true si es persona natural, false en caso contrario
     */
    public boolean isNaturalPerson() {
        return ePersonType.Natural.equals(personType);
    }

    /**
     * Actualiza la fecha de modificación a la fecha actual.
     */
    public void markAsUpdated() {
        this.updateDate = LocalDate.now();
    }
}