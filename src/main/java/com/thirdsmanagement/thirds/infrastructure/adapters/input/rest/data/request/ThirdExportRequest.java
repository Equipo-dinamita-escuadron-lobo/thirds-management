package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para solicitudes de exportación de terceros.
 * Permite filtrar los terceros que se van a exportar.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdExportRequest {

    /**
     * Identificador de la entidad.
     */
    @NotBlank(message = "El identificador de entidad es obligatorio")
    private String entId;

    /**
     * Estado de los terceros a exportar (true para activos, false para inactivos).
     * Si es null, se exportan todos independientemente del estado.
     */
    private Boolean status;

    /**
     * ID específico de tipo de tercero para filtrar.
     * Si es null, se incluyen todos los tipos.
     */
    private Long thirdTypeId;

    /**
     * Lista de IDs específicos de terceros a exportar.
     * Si está vacía o es null, se aplican los otros filtros.
     */
    private List<Long> thirdIds;

    /**
     * Incluir información de tipos asociados en la exportación.
     */
    @Builder.Default
    private Boolean includeTypes = true;

    /**
     * Incluir información de ciudades en la exportación.
     */
    @Builder.Default
    private Boolean includeCities = true;
}
