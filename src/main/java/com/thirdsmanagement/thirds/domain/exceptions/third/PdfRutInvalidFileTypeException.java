package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción lanzada cuando el archivo no es de tipo PDF válido.
 * Se utiliza para validar que solo se procesen archivos PDF en el servicio de RUT.
 */
public class PdfRutInvalidFileTypeException extends BaseBusinessException {

    public PdfRutInvalidFileTypeException() {
        super(ThirdsErrorCode.PDF_RUT_INVALID_FILE_TYPE);
    }

    public PdfRutInvalidFileTypeException(String customMessage) {
        super(ThirdsErrorCode.PDF_RUT_INVALID_FILE_TYPE, customMessage);
    }

    public PdfRutInvalidFileTypeException(String customMessage, Throwable cause) {
        super(ThirdsErrorCode.PDF_RUT_INVALID_FILE_TYPE, customMessage, cause);
    }
}
