package com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO de respuesta al ejecutar una fase del proceso de copia de terceros.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopyPhaseResponseDto {

    /** Estado resultante del proceso (COMPLETADO, FALLIDO, etc.) */
    private String estado;

    /** Cantidad total de registros procesados */
    private int registrosProcesados;

    /** Cantidad de equivalencias de IDs generadas */
    private int equivalenciasGeneradas;

    /** Mensaje descriptivo del resultado */
    private String mensaje;

    /** Lista de advertencias no fatales (FKs no encontradas, etc.) */
    private List<String> advertencias;

    /** Equivalencias generadas para que el orquestador las propague a fases siguientes */
    private List<CopyEquivalenciaDto> equivalencias;
}
