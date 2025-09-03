package com.thirdsmanagement.commons.exceptions.thirds;

import com.thirdsmanagement.commons.exceptions.BaseBusinessException;

/**
 * Excepción que se lanza cuando no se ha podido cambiar el estado de un tercero.
 */
public class ThirdStateNotChanged extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public ThirdStateNotChanged() {
        super(ThirdsErrorCode.THIRD_STATE_NOT_CHANGED);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public ThirdStateNotChanged(String customMessage) {
        super(ThirdsErrorCode.THIRD_STATE_NOT_CHANGED, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public ThirdStateNotChanged(String customMessage, Throwable cause) {
        super(ThirdsErrorCode.THIRD_STATE_NOT_CHANGED, customMessage, cause);
    }
}
