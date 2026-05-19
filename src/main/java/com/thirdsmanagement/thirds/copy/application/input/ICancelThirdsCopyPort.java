package com.thirdsmanagement.thirds.copy.application.input;

import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyCancelResponseDto;

/**
 * Puerto de entrada para cancelar un proceso de copia de terceros en curso.
 */
public interface ICancelThirdsCopyPort {

    /**
     * Cancela un proceso de copia en curso.
     *
     * @param idProceso identificador UUID del proceso
     * @return respuesta de cancelación con nuevo estado
     */
    CopyCancelResponseDto cancelar(String idProceso);
}
