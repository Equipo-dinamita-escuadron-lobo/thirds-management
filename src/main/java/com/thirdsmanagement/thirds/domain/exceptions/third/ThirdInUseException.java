package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * @brief Excepción lanzada cuando se intenta modificar o eliminar un tercero en uso
 *
 * Esta excepción se utiliza cuando un tercero tiene un contador de uso mayor a cero,
 * indicando que tiene movimientos contables asociados y no puede ser modificado o eliminado.
 */
public class ThirdInUseException extends BaseBusinessException {

    public ThirdInUseException() {
        super(ThirdsErrorCode.THIRD_IN_USE);
    }

    public ThirdInUseException(String customMessage) {
        super(ThirdsErrorCode.THIRD_IN_USE, customMessage);
    }
}
