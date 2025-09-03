package com.thirdsmanagement.thirds.domain.exception.thirds;

import com.thirdsmanagement.thirds.domain.exception.BaseBusinessException;

/**
 * Excepción que se lanza cuando no se encuentran terceros.
 */
public class ThirdsNotFound extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public ThirdsNotFound() {
        super(ThirdsErrorCode.THIRDS_NOT_FOUND);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public ThirdsNotFound(String customMessage) {
        super(ThirdsErrorCode.THIRDS_NOT_FOUND, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public ThirdsNotFound(String customMessage, Throwable cause) {
        super(ThirdsErrorCode.THIRDS_NOT_FOUND, customMessage, cause);
    }
}
