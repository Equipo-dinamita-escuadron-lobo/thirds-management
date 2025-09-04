package com.thirdsmanagement.thirds.domain.exceptions.typeId;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción que se lanza cuando los datos del tipo de identificación son inválidos.
 */
public class TypeIdInvalidDataException extends BaseBusinessException {

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public TypeIdInvalidDataException(String customMessage) {
        super(TypeIdErrorCode.TYPE_ID_INVALID_DATA, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public TypeIdInvalidDataException(String customMessage, Throwable cause) {
        super(TypeIdErrorCode.TYPE_ID_INVALID_DATA, customMessage, cause);
    }
}
