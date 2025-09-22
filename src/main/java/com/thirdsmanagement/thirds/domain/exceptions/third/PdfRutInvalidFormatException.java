package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción lanzada cuando el archivo PDF no tiene el formato válido de RUT de la DIAN.
 */
public class PdfRutInvalidFormatException extends BaseBusinessException {

    public PdfRutInvalidFormatException() {
        super(ThirdsErrorCode.PDF_RUT_INVALID_FORMAT);
    }

    public PdfRutInvalidFormatException(String customMessage) {
        super(ThirdsErrorCode.PDF_RUT_INVALID_FORMAT, customMessage);
    }

    public PdfRutInvalidFormatException(String customMessage, Throwable cause) {
        super(ThirdsErrorCode.PDF_RUT_INVALID_FORMAT, customMessage, cause);
    }
}
