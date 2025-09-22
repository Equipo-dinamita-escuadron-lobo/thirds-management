package com.thirdsmanagement.thirds.domain.exceptions.geography;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción que se lanza cuando no se encuentra un país o está inactivo.
 */
public class CountryNotFoundException extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public CountryNotFoundException() {
        super(GeographyErrorCode.COUNTRY_NOT_FOUND);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public CountryNotFoundException(String customMessage) {
        super(GeographyErrorCode.COUNTRY_NOT_FOUND, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public CountryNotFoundException(String customMessage, Throwable cause) {
        super(GeographyErrorCode.COUNTRY_NOT_FOUND, customMessage, cause);
    }
}
