package com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO de request para ejecutar una fase del proceso de copia de terceros.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopyPhaseRequestDto {

    /** Identificador UUID del proceso coordinado por el orquestador */
    private UUID idProceso;

    /** Número de fase de copia (1, 2, etc.) */
    private int fase;

    /** ID de la empresa origen */
    private String entOrigen;

    /** ID de la empresa destino */
    private String entDestino;

    /** Fecha de corte para el snapshot (excluye registros más nuevos) */
    private Instant snapshotCorte;

    /** Equivalencias de IDs de fases anteriores (puede ser vacío para THIRDS) */
    private List<CopyEquivalenciaDto> equivalenciasPrev;
}
