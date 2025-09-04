package com.thirdsmanagement.thirds.domain.exceptions.typeId;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción que se lanza cuando no se encuentra un tipo de identificación.
 */
public class TypeIdNotFound extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public TypeIdNotFound() {
        super(TypeIdErrorCode.TYPE_ID_NOT_FOUND);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public TypeIdNotFound(String customMessage) {
        super(TypeIdErrorCode.TYPE_ID_NOT_FOUND, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public TypeIdNotFound(String customMessage, Throwable cause) {
        super(TypeIdErrorCode.TYPE_ID_NOT_FOUND, customMessage, cause);
    }
}
