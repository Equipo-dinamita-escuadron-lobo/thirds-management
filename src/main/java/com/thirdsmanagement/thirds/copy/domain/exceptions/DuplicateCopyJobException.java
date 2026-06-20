package com.thirdsmanagement.thirds.copy.domain.exceptions;

/**
 * Excepción de dominio lanzada cuando se intenta operar sobre un proceso de copia que no existe
 * o cuando se detecta un estado inconsistente en el log de idempotencia.
 */
public class DuplicateCopyJobException extends RuntimeException {

    /**
     * Crea la excepción con el ID de proceso y la fase afectada.
     *
     * @param idProceso identificador UUID del proceso
     * @param fase      número de fase del proceso
     */
    public DuplicateCopyJobException(String idProceso, int fase) {
        super("No se encontró el proceso de copia con idProceso=" + idProceso + " y fase=" + fase);
    }

    /**
     * Crea la excepción con solo el ID de proceso.
     *
     * @param idProceso identificador UUID del proceso
     */
    public DuplicateCopyJobException(String idProceso) {
        super("No se encontró el proceso de copia con idProceso=" + idProceso);
    }
}
