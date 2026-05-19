package com.thirdsmanagement.thirds.copy.application.input;

import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyPhaseRequestDto;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyPhaseResponseDto;

/**
 * Puerto de entrada para ejecutar una fase del proceso de copia de terceros.
 */
public interface IExecuteThirdsCopyPhasePort {

    /**
     * Ejecuta la copia de terceros de entOrigen a entDestino.
     *
     * @param request datos del proceso de copia
     * @return resultado de la ejecución con estado y equivalencias generadas
     */
    CopyPhaseResponseDto ejecutar(CopyPhaseRequestDto request);
}
