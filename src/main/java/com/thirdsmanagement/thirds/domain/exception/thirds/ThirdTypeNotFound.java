package com.thirdsmanagement.thirds.domain.exception.thirds;

import com.thirdsmanagement.thirds.domain.exception.BaseBusinessException;

/**
 * Excepción que se lanza cuando no se encuentra un tipo de tercero.
 */
public class ThirdTypeNotFound extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public ThirdTypeNotFound() {
        super(ThirdsErrorCode.THIRD_TYPE_NOT_FOUND);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public ThirdTypeNotFound(String customMessage) {
        super(ThirdsErrorCode.THIRD_TYPE_NOT_FOUND, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public ThirdTypeNotFound(String customMessage, Throwable cause) {
        super(ThirdsErrorCode.THIRD_TYPE_NOT_FOUND, customMessage, cause);
    }
}
