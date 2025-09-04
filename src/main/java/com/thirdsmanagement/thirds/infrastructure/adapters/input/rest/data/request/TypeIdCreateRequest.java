package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa la petición de creación de un tipo de identificación.
 * Contiene la información necesaria para crear un nuevo tipo de identificación en el sistema.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeIdCreateRequest {

    /**
     * Identificador de la empresa a la que pertenece el tipo de identificación.
     * Campo obligatorio para segmentar los tipos por empresa.
     */
    @NotNull(message = "El ID de la empresa no puede estar vacío")
    private String entId;

    /**
     * Código único del tipo de identificación (ej: CC, NIT, CE).
     * Será normalizado automáticamente antes del guardado.
     */
    @NotNull(message = "El código del tipo de identificación no puede estar vacío")
    @Size(min = 2, max = 10, message = "El código debe tener entre 2 y 10 caracteres")
    private String typeId;

    /**
     * Nombre descriptivo del tipo de identificación.
     * Campo obligatorio que describe el tipo (ej: Cédula de Ciudadanía).
     */
    @NotNull(message = "El nombre del tipo de identificación no puede estar vacío")
    private String typeIdname;
}
