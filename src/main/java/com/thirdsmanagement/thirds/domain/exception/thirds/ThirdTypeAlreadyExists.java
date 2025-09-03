package com.thirdsmanagement.thirds.domain.exception.thirds;

import com.thirdsmanagement.thirds.domain.exception.BaseBusinessException;

/**
 * Excepción que se lanza cuando ya existe un tipo de tercero.
 */
public class ThirdTypeAlreadyExists extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public ThirdTypeAlreadyExists() {
        super(ThirdsErrorCode.THIRD_TYPE_ALREADY_EXISTS);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public ThirdTypeAlreadyExists(String customMessage) {
        super(ThirdsErrorCode.THIRD_TYPE_ALREADY_EXISTS, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public ThirdTypeAlreadyExists(String customMessage, Throwable cause) {
        super(ThirdsErrorCode.THIRD_TYPE_ALREADY_EXISTS, customMessage, cause);
    }
}
