package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción unificada para errores de validación de archivos.
 * Centraliza el manejo de errores relacionados con carga de archivos.
 */
public class FileValidationException extends BaseBusinessException {

    public FileValidationException(ThirdsErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public FileValidationException(ThirdsErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public static FileValidationException forNullFile() {
        return new FileValidationException(
            ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
            "El archivo no puede ser null"
        );
    }

    public static FileValidationException forEmptyFile(String fileName) {
        return new FileValidationException(
            ThirdsErrorCode.THIRD_EXPORT_NO_DATA,
            String.format("El archivo '%s' está vacío", fileName)
        );
    }

    public static FileValidationException forSizeExceeded(String fileName, long actualSize, long maxSize) {
        return new FileValidationException(
            ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
            String.format("El archivo '%s' excede el tamaño máximo permitido. Actual: %d bytes, Máximo: %d bytes", 
                fileName, actualSize, maxSize)
        );
    }

    public static FileValidationException forInvalidExtension(String fileName, String[] supportedExtensions) {
        return new FileValidationException(
            ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
            String.format("El archivo '%s' tiene una extensión no válida. Extensiones soportadas: %s", 
                fileName, String.join(", ", supportedExtensions))
        );
    }

    public static FileValidationException forInvalidMimeType(String fileName, String actualMimeType, String[] supportedMimeTypes) {
        return new FileValidationException(
            ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
            String.format("El archivo '%s' tiene un tipo MIME no válido (%s). Tipos soportados: %s", 
                fileName, actualMimeType, String.join(", ", supportedMimeTypes))
        );
    }
}
