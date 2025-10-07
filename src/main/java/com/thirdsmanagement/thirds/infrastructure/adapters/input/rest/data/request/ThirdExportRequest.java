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
     * Lista de IDs específicos de terceros a exportar.
     * Si está vacía o es null, se aplican los otros filtros.
     */
    private List<Long> thirdIds;

    // ===== CAMPOS OPCIONALES INDIVIDUALES =====
    
    /**
     * Incluir columna de género en la exportación.
     */
    @Builder.Default
    private Boolean includeGender = false;

    /**
     * Incluir columna de país en la exportación.
     */
    @Builder.Default
    private Boolean includeCountry = false;

    /**
     * Incluir columna de departamento en la exportación.
     */
    @Builder.Default
    private Boolean includeState = false;

    /**
     * Incluir columna de ciudad en la exportación.
     */
    @Builder.Default
    private Boolean includeCity = false;
}
