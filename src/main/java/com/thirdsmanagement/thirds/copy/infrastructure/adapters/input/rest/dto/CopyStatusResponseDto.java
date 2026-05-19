package com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de respuesta al consultar el estado de un proceso de copia.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopyStatusResponseDto {

    /** Identificador UUID del proceso */
    private String idProceso;

    /** Estado actual del proceso */
    private String estado;

    /** Número de fase */
    private int fase;

    /** Módulo */
    private String modulo;

    /** Registros procesados */
    private int registrosProcesados;

    /** Equivalencias generadas */
    private int equivalenciasGeneradas;
}
