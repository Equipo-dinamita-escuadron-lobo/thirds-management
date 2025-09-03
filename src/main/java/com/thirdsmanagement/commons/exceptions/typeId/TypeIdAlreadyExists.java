package com.thirdsmanagement.commons.exceptions.typeId;

import com.thirdsmanagement.commons.exceptions.BaseBusinessException;
import com.thirdsmanagement.commons.exceptions.thirds.ThirdsErrorCode;

/**
 * Excepción que se lanza cuando ya existe un tipo de identificación.
 */
public class TypeIdAlreadyExists extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public TypeIdAlreadyExists() {
        super(ThirdsErrorCode.TYPE_ID_ALREADY_EXISTS);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public TypeIdAlreadyExists(String customMessage) {
        super(ThirdsErrorCode.TYPE_ID_ALREADY_EXISTS, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public TypeIdAlreadyExists(String customMessage, Throwable cause) {
        super(ThirdsErrorCode.TYPE_ID_ALREADY_EXISTS, customMessage, cause);
    }
}
