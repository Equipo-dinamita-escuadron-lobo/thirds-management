package com.thirdsmanagement.thirds.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Builder
@Getter
@Setter
@EqualsAndHashCode(of = { "entId", "thirdTypeId" })
@ToString(of = { "thirdTypeId", "thirdTypeName" })
@AllArgsConstructor
@NoArgsConstructor
public class ThirdType {

    @NotBlank(message = "El ID de la empresa no puede estar vacío")
    @Size(max = 50, message = "El ID de la empresa no puede exceder los 50 caracteres")
    private String entId;

    @NotNull(message = "El ID del tipo de tercero es obligatorio")
    private Long thirdTypeId;

    @NotBlank(message = "El nombre del tipo de tercero no puede estar vacío")
    @Size(max = 100, message = "El nombre del tipo de tercero no puede exceder los 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras y espacios")
    private String thirdTypeName;

    @Builder.Default
    private Boolean status = true;

    public boolean isClientType() {
        return "Cliente".equalsIgnoreCase(thirdTypeName) ||
                "CLIENTE".equalsIgnoreCase(thirdTypeName);
    }

    public boolean isSupplierType() {
        return "Proveedor".equalsIgnoreCase(thirdTypeName) ||
                "PROVEEDOR".equalsIgnoreCase(thirdTypeName);
    }

    public boolean isEmployeeType() {
        return "Empleado".equalsIgnoreCase(thirdTypeName) ||
                "EMPLEADO".equalsIgnoreCase(thirdTypeName);
    }

    public String getNormalizedName() {
        if (thirdTypeName == null || thirdTypeName.trim().isEmpty()) {
            return null;
        }

        String trimmed = thirdTypeName.trim();
        if (trimmed.length() == 1) {
            return trimmed.toUpperCase();
        }

        return trimmed.substring(0, 1).toUpperCase() +
                trimmed.substring(1).toLowerCase();
    }
}
