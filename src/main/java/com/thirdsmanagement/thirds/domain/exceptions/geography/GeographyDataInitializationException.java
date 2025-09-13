package com.thirdsmanagement.thirds.domain.exceptions.geography;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción que se lanza cuando falla la inicialización de datos geográficos.
 * Esta excepción se utiliza específicamente durante el proceso de carga inicial
 * de países, estados y ciudades en el sistema.
 */
public class GeographyDataInitializationException extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public GeographyDataInitializationException() {
        super(GeographyErrorCode.GEOGRAPHY_DATA_INITIALIZATION_FAILED);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public GeographyDataInitializationException(String customMessage) {
        super(GeographyErrorCode.GEOGRAPHY_DATA_INITIALIZATION_FAILED, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public GeographyDataInitializationException(String customMessage, Throwable cause) {
        super(GeographyErrorCode.GEOGRAPHY_DATA_INITIALIZATION_FAILED, customMessage, cause);
    }

    /**
     * Constructor con causa únicamente.
     * @param cause causa del error
     */
    public GeographyDataInitializationException(Throwable cause) {
        super(GeographyErrorCode.GEOGRAPHY_DATA_INITIALIZATION_FAILED, 
              "Error en la inicialización de datos geográficos: " + cause.getMessage(), cause);
    }
}
