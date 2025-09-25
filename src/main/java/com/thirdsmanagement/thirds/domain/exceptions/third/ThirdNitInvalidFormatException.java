package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción lanzada cuando el formato del NIT no es válido para personas jurídicas.
 * El NIT debe empezar por 8 o 9 según las regulaciones colombianas.
 */
public class ThirdNitInvalidFormatException extends BaseBusinessException {

    private static final String DEFAULT_MESSAGE = "El NIT debe empezar por 8 o 9 para personas jurídicas";

    /**
     * Constructor con número de identificación específico.
     * 
     * @param nitNumber el número de NIT que no cumple con el formato
     */
    public ThirdNitInvalidFormatException(String nitNumber) {
        super(
            ThirdsErrorCode.THIRD_NIT_INVALID_FORMAT,
            String.format("El NIT '%s' no es válido. Debe empezar por 8 o 9 para personas jurídicas", nitNumber)
        );
    }

    /**
     * Constructor con mensaje por defecto.
     */
    public ThirdNitInvalidFormatException() {
        super(ThirdsErrorCode.THIRD_NIT_INVALID_FORMAT, DEFAULT_MESSAGE);
    }

    /**
     * Constructor con mensaje personalizado.
     * 
     * @param message mensaje personalizado de error
     */
    public ThirdNitInvalidFormatException(String message, Throwable cause) {
        super(ThirdsErrorCode.THIRD_NIT_INVALID_FORMAT, message, cause);
    }
}
