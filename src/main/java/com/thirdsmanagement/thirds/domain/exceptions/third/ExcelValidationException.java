package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción específica para errores durante la aplicación de validaciones en Excel.
 */
public class ExcelValidationException extends BaseBusinessException {

    public ExcelValidationException(ThirdsErrorCode errorCode) {
        super(errorCode);
    }

    public ExcelValidationException(ThirdsErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public ExcelValidationException(ThirdsErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }
}
