package com.thirdsmanagement.thirds.copy.application.output;

import com.thirdsmanagement.thirds.copy.domain.models.CopyJobLog;

import java.util.Optional;

/**
 * Puerto de salida para persistir y consultar el log de idempotencia de copia.
 */
public interface ICopyJobLogRepositoryPort {

    /**
     * Persiste o actualiza un registro de log de copia.
     *
     * @param log log a persistir
     * @return log persistido con ID asignado
     */
    CopyJobLog guardar(CopyJobLog log);

    /**
     * Busca un log de copia por proceso y número de fase.
     *
     * @param idProceso identificador UUID del proceso
     * @param fase      número de fase
     * @return log encontrado o vacío
     */
    Optional<CopyJobLog> buscarPorIdProcesoYFase(String idProceso, int fase);

    /**
     * Busca el log más reciente de un proceso (para status/cancel).
     *
     * @param idProceso identificador UUID del proceso
     * @return log más reciente o vacío
     */
    Optional<CopyJobLog> buscarPorIdProceso(String idProceso);

    /**
     * Elimina todos los registros de log de un proceso.
     *
     * @param idProceso identificador UUID del proceso
     */
    void eliminarPorIdProceso(String idProceso);
}
