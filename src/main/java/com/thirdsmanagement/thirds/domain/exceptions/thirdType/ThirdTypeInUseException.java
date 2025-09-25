package com.thirdsmanagement.thirds.domain.exceptions.thirdType;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción lanzada cuando se intenta eliminar un tipo de tercero que está siendo utilizado por terceros existentes.
 */
public class ThirdTypeInUseException extends BaseBusinessException {

    /**
     * Constructor con ID específico del tipo de tercero.
     * @param thirdTypeId el ID del tipo de tercero que está en uso
     */
    public ThirdTypeInUseException(Long thirdTypeId) {
        super(ThirdTypeErrorCode.THIRD_TYPE_IN_USE, 
              "El tipo de tercero con ID '" + thirdTypeId + "' está siendo utilizado por terceros existentes y no puede ser eliminado");
    }

    /**
     * Constructor con nombre específico del tipo de tercero.
     * @param thirdTypeName el nombre del tipo de tercero que está en uso
     */
    public ThirdTypeInUseException(String thirdTypeName) {
        super(ThirdTypeErrorCode.THIRD_TYPE_IN_USE, 
              "El tipo de tercero '" + thirdTypeName + "' está siendo utilizado por terceros existentes y no puede ser eliminado");
    }

    /**
     * Constructor por defecto.
     */
    public ThirdTypeInUseException() {
        super(ThirdTypeErrorCode.THIRD_TYPE_IN_USE);
    }

    /**
     * Constructor con causa.
     * @param cause la causa de la excepción
     */
    public ThirdTypeInUseException(Throwable cause) {
        super(ThirdTypeErrorCode.THIRD_TYPE_IN_USE, ThirdTypeErrorCode.THIRD_TYPE_IN_USE.getMessage(), cause);
    }
}
