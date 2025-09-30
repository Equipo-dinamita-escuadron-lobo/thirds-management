package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción específica para errores durante la importación de terceros.
 * Extiende BaseBusinessException para mantener consistencia con el manejo de errores del dominio.
 */
public class ThirdImportException extends BaseBusinessException {

    /**
     * Constructor con mensaje y código de error.
     * 
     * @param errorCode código específico del error
     * @param message mensaje descriptivo del error
     */
    public ThirdImportException(ThirdsErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * Constructor con mensaje, código de error y causa.
     * 
     * @param errorCode código específico del error
     * @param message mensaje descriptivo del error
     * @param cause causa raíz del error
     */
    public ThirdImportException(ThirdsErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    /**
     * Constructor de conveniencia para errores de archivo Excel inválido.
     * 
     * @param fileName nombre del archivo que causó el error
     * @param details detalles específicos del error
     * @return nueva instancia de ThirdImportException
     */
    public static ThirdImportException forInvalidExcelFile(String fileName, String details) {
        return new ThirdImportException(
            ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
            String.format("El archivo Excel '%s' tiene formato inválido: %s", fileName, details)
        );
    }

    /**
     * Constructor de conveniencia para errores de procesamiento en lotes.
     * 
     * @param batchNumber número del lote que falló
     * @param details detalles específicos del error
     * @return nueva instancia de ThirdImportException
     */
    public static ThirdImportException forBatchProcessingError(int batchNumber, String details) {
        return new ThirdImportException(
            ThirdsErrorCode.THIRD_EXPORT_ERROR, // Reutilizamos este código para errores de procesamiento
            String.format("Error procesando lote #%d: %s", batchNumber, details)
        );
    }

    /**
     * Constructor de conveniencia para errores de archivo vacío.
     * 
     * @param fileName nombre del archivo vacío
     * @return nueva instancia de ThirdImportException
     */
    public static ThirdImportException forEmptyFile(String fileName) {
        return new ThirdImportException(
            ThirdsErrorCode.THIRD_EXPORT_NO_DATA,
            String.format("El archivo '%s' está vacío o no contiene datos válidos para importar", fileName)
        );
    }

    /**
     * Constructor de conveniencia para errores de tamaño de archivo.
     * 
     * @param fileName nombre del archivo
     * @param actualSize tamaño actual del archivo
     * @param maxSize tamaño máximo permitido
     * @return nueva instancia de ThirdImportException
     */
    public static ThirdImportException forFileSizeExceeded(String fileName, long actualSize, long maxSize) {
        return new ThirdImportException(
            ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
            String.format("El archivo '%s' excede el tamaño máximo permitido. Actual: %d bytes, Máximo: %d bytes", 
                fileName, actualSize, maxSize)
        );
    }
}
