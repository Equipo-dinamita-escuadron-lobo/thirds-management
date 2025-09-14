package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción específica para errores durante la exportación de terceros.
 */
public class ThirdExportException extends BaseBusinessException {

    public ThirdExportException(ThirdsErrorCode errorCode) {
        super(errorCode);
    }

    public ThirdExportException(ThirdsErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public ThirdExportException(ThirdsErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }
}
