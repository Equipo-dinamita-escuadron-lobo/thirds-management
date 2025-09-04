package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.domain.model.ThirdType;

/**
 * Puerto de entrada para el caso de uso de actualización de tipo de tercero.
 * Define el contrato para actualizar un tipo de tercero existente.
 */
public interface UpdateThirdTypeUseCase {

    /**
     * Actualiza un tipo de tercero existente.
     * 
     * @param thirdType El tipo de tercero con los datos actualizados
     * @return El tipo de tercero actualizado
     */
    ThirdType updateThirdType(ThirdType thirdType);
}
