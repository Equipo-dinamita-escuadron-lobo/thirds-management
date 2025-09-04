package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción lanzada cuando los datos del tercero son inválidos.
 */
public class ThirdInvalidDataException extends BaseBusinessException {

    public ThirdInvalidDataException(String message) {
        super(ThirdsErrorCode.INVALID_THIRD_DATA, message);
    }
}
