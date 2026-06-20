package com.thirdsmanagement.thirds.copy.application.input;

/**
 * Puerto de entrada para limpiar los registros de log de un proceso de copia de terceros.
 */
public interface ICleanupThirdsCopyPort {

    /**
     * Elimina los registros de log asociados a un proceso de copia.
     *
     * @param idProceso identificador UUID del proceso a limpiar
     */
    void limpiar(String idProceso);
}
