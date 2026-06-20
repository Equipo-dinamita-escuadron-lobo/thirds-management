package com.thirdsmanagement.thirds.copy.application.input;

import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyStatusResponseDto;

/**
 * Puerto de entrada para consultar el estado de un proceso de copia de terceros.
 */
public interface IGetThirdsCopyStatusPort {

    /**
     * Obtiene el estado actual de un proceso de copia.
     *
     * @param idProceso identificador UUID del proceso
     * @return estado del proceso
     */
    CopyStatusResponseDto obtenerEstado(String idProceso);
}
