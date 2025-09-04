package com.thirdsmanagement.thirds.domain.exceptions.thirdType;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción lanzada cuando se intenta crear o actualizar un tipo de tercero con un nombre que ya existe.
 */
public class ThirdTypeNameAlreadyExistsException extends BaseBusinessException {

    public ThirdTypeNameAlreadyExistsException(String thirdTypeName) {
        super(ThirdTypeErrorCode.THIRD_TYPE_NAME_ALREADY_EXISTS, 
              "Ya existe un tipo de tercero con el nombre '" + thirdTypeName + "'");
    }
}
