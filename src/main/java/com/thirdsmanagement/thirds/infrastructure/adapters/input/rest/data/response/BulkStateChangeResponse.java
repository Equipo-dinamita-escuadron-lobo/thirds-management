package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para operaciones de cambio de estado masivo.
 * Contiene información sobre la cantidad de registros actualizados.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkStateChangeResponse {
    
    /**
     * Cantidad de terceros actualizados.
     */
    private int updatedCount;
    
    /**
     * Nuevo estado aplicado (true para activo, false para inactivo).
     */
    private Boolean newState;
    
    /**
     * Mensaje descriptivo de la operación.
     */
    private String message;
}
