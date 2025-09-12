package com.thirdsmanagement.thirds.domain.exceptions.geography;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción que se lanza cuando no se encuentra un estado/departamento o está inactivo.
 */
public class StateNotFoundException extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public StateNotFoundException() {
        super(GeographyErrorCode.STATE_NOT_FOUND);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public StateNotFoundException(String customMessage) {
        super(GeographyErrorCode.STATE_NOT_FOUND, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public StateNotFoundException(String customMessage, Throwable cause) {
        super(GeographyErrorCode.STATE_NOT_FOUND, customMessage, cause);
    }
}
