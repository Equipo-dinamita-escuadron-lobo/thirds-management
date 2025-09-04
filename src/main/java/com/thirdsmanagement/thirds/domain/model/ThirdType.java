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

/**
 * Entidad de dominio que representa un tipo de tercero.
 * Define las diferentes categorías o roles que puede tener un tercero en el sistema.
 *
 * Ejemplos: Cliente, Proveedor, Empleado, Socio, etc.
 *
 * Un tercero puede tener múltiples tipos asignados, permitiendo flexibilidad
 * en la categorización de las entidades.
 */
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = {"entId", "thirdTypeId"})
@ToString(of = {"thirdTypeId", "thirdTypeName"})
@AllArgsConstructor
@NoArgsConstructor
public class ThirdType {

    /**
     * Identificador de la empresa a la que pertenece el tipo de tercero.
     * Permite segmentar los tipos de tercero por empresa.
     */
    @NotBlank(message = "El ID de la empresa no puede estar vacío")
    @Size(max = 50, message = "El ID de la empresa no puede exceder los 50 caracteres")
    private String entId;

    /**
     * Identificador único del tipo de tercero dentro de la empresa.
     */
    @NotNull(message = "El ID del tipo de tercero es obligatorio")
    private Long thirdTypeId;

    /**
     * Nombre descriptivo del tipo de tercero.
     * Ejemplo: "Cliente", "Proveedor", "Empleado", "Socio".
     */
    @NotBlank(message = "El nombre del tipo de tercero no puede estar vacío")
    @Size(max = 100, message = "El nombre del tipo de tercero no puede exceder los 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras y espacios")
    private String thirdTypeName;

    /**
     * Estado del tipo de tercero.
     * true = activo, false = inactivo
     */
    @Builder.Default
    private Boolean status = true;

    /**
     * Verifica si el tipo de tercero es de tipo cliente.
     *
     * @return true si es un tipo cliente
     */
    public boolean isClientType() {
        return "Cliente".equalsIgnoreCase(thirdTypeName) ||
               "CLIENTE".equalsIgnoreCase(thirdTypeName);
    }

    /**
     * Verifica si el tipo de tercero es de tipo proveedor.
     *
     * @return true si es un tipo proveedor
     */
    public boolean isSupplierType() {
        return "Proveedor".equalsIgnoreCase(thirdTypeName) ||
               "PROVEEDOR".equalsIgnoreCase(thirdTypeName);
    }

    /**
     * Verifica si el tipo de tercero es de tipo empleado.
     *
     * @return true si es un tipo empleado
     */
    public boolean isEmployeeType() {
        return "Empleado".equalsIgnoreCase(thirdTypeName) ||
               "EMPLEADO".equalsIgnoreCase(thirdTypeName);
    }

    /**
     * Obtiene el nombre del tipo de tercero normalizado (primera letra mayúscula).
     *
     * @return nombre normalizado
     */
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
