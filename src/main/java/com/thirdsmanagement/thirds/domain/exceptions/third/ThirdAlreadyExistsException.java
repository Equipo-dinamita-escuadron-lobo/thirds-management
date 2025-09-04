package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción lanzada cuando se intenta crear un tercero que ya existe.
 */
public class ThirdAlreadyExistsException extends BaseBusinessException {

    public ThirdAlreadyExistsException(String idNumber) {
        super(ThirdsErrorCode.THIRD_ALREADY_EXISTS, "Ya existe un tercero con el número de identificación: " + idNumber);
    }
}
