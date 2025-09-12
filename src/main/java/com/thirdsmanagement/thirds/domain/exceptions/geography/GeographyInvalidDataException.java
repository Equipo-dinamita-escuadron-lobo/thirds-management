package com.thirdsmanagement.thirds.domain.exceptions.geography;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción que se lanza cuando los datos geográficos son inválidos.
 */
public class GeographyInvalidDataException extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public GeographyInvalidDataException() {
        super(GeographyErrorCode.GEOGRAPHY_HIERARCHY_VIOLATION);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public GeographyInvalidDataException(String customMessage) {
        super(GeographyErrorCode.GEOGRAPHY_HIERARCHY_VIOLATION, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public GeographyInvalidDataException(String customMessage, Throwable cause) {
        super(GeographyErrorCode.GEOGRAPHY_HIERARCHY_VIOLATION, customMessage, cause);
    }
}
