package com.thirdsmanagement.thirds.domain.exceptions.thirdType;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción lanzada cuando se intenta crear un tercero con un tipo de tercero que no existe.
 */
public class ThirdTypeForeignKeyViolationException extends BaseBusinessException {

    public ThirdTypeForeignKeyViolationException(String thirdTypeId) {
        super(ThirdTypeErrorCode.THIRD_TYPE_FOREIGN_KEY_VIOLATION,
              "El tipo de tercero con ID '" + thirdTypeId + "' no existe");
    }

    public ThirdTypeForeignKeyViolationException(String thirdTypeId, Throwable cause) {
        super(ThirdTypeErrorCode.THIRD_TYPE_FOREIGN_KEY_VIOLATION,
              "El tipo de tercero con ID '" + thirdTypeId + "' no existe", cause);
    }
}
