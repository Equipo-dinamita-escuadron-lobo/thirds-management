package com.thirdsmanagement.thirds.copy.domain.models;

import com.thirdsmanagement.thirds.copy.domain.enums.CopyEstado;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Modelo de dominio para el log de idempotencia del proceso de copia.
 */
@Getter
@Setter
@Builder
public class CopyJobLog {

    /** Identificador de proceso (UUID del orquestador) */
    private UUID idProceso;

    /** Número de fase (1, 2, etc.) */
    private int fase;

    /** Nombre del módulo ('thirds') */
    private String modulo;

    /** Estado actual del proceso */
    private CopyEstado estado;

    /** Cantidad de registros copiados */
    @Builder.Default
    private int registrosProcesados = 0;

    /** Cantidad de equivalencias generadas */
    @Builder.Default
    private int equivalenciasGeneradas = 0;

    /** Advertencias no fatales durante la copia */
    private List<String> advertencias;

    /** Mensaje descriptivo del resultado */
    private String mensaje;

    /** Fecha y hora de inicio del proceso */
    private Instant fechaInicio;

    /** Fecha y hora de finalización del proceso */
    private Instant fechaFin;
}
