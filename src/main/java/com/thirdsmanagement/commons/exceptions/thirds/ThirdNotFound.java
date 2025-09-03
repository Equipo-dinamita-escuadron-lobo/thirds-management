package com.thirdsmanagement.commons.exceptions.thirds;

import com.thirdsmanagement.commons.exceptions.BaseBusinessException;

/**
 * Excepción que se lanza cuando no se encuentra un tercero.
 */
public class ThirdNotFound extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public ThirdNotFound() {
        super(ThirdsErrorCode.THIRD_NOT_FOUND);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public ThirdNotFound(String customMessage) {
        super(ThirdsErrorCode.THIRD_NOT_FOUND, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public ThirdNotFound(String customMessage, Throwable cause) {
        super(ThirdsErrorCode.THIRD_NOT_FOUND, customMessage, cause);
    }
}
