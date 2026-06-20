package com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de respuesta al cancelar un proceso de copia.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopyCancelResponseDto {

    /** Identificador UUID del proceso cancelado */
    private String idProceso;

    /** Estado resultante (CANCELADO) */
    private String estado;

    /** Mensaje descriptivo */
    private String mensaje;
}
