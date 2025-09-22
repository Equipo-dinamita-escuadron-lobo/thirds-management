package com.thirdsmanagement.thirds.domain.exceptions.thirdType;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción lanzada cuando los datos del tipo de tercero son inválidos.
 */
public class ThirdTypeInvalidDataException extends BaseBusinessException {

    public ThirdTypeInvalidDataException(String message) {
        super(ThirdTypeErrorCode.THIRD_TYPE_INVALID_DATA, message);
    }
}
