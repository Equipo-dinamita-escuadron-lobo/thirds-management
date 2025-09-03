package com.thirdsmanagement.thirds.domain.exception.thirds;

import com.thirdsmanagement.thirds.domain.exception.BaseBusinessException;

/**
 * Excepción que se lanza cuando no se encuentra un tipo de identificación.
 */
public class TypeIdNotFound extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public TypeIdNotFound() {
        super(ThirdsErrorCode.TYPE_ID_NOT_FOUND);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public TypeIdNotFound(String customMessage) {
        super(ThirdsErrorCode.TYPE_ID_NOT_FOUND, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public TypeIdNotFound(String customMessage, Throwable cause) {
        super(ThirdsErrorCode.TYPE_ID_NOT_FOUND, customMessage, cause);
    }
}
