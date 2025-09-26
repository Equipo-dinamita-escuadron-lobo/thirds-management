package com.thirdsmanagement.thirds.domain.interfaces;

import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ImportErrorDetail;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interfaz genérica para orquestadores de importación.
 * Define el contrato principal para procesos de importación masiva
 * permitiendo reutilización en diferentes módulos del sistema.
 * 
 * @param <TRequest> tipo de solicitud de importación
 * @param <TResponse> tipo de respuesta de importación
 * @param <TData> tipo de datos a importar
 */
public interface ImportOrchestrator<TRequest, TResponse, TData> {

    /**
     * Resultado del procesamiento de importación.
     * 
     * @param <TData> tipo de datos procesados
     */
    interface ImportProcessingResult<TData> {
        int getSuccessCount();
        int getFailureCount();
        int getSkippedCount();
        List<ImportErrorDetail> getErrors();
        List<TData> getProcessedData();
    }

    /**
     * Ejecuta el proceso completo de importación.
     * 
     * @param request solicitud de importación con archivo y parámetros
     * @return respuesta con resultados y estadísticas
     */
    TResponse executeImport(TRequest request);

    /**
     * Parsea el archivo Excel y extrae los datos.
     * 
     * @param file archivo Excel a parsear
     * @param entityId identificador de la entidad
     * @return resultado del parseo
     */
    ExcelParsingStrategy.ExcelParsingResult<TData> parseExcelFile(MultipartFile file, String entityId);

    /**
     * Valida los datos parseados aplicando todas las reglas.
     * 
     * @param data datos a validar
     * @param entityId identificador de la entidad
     * @param columnMap mapa de columnas
     * @return resultado de la validación
     */
    BatchValidationStrategy.BatchValidationResult<TData> validateData(List<TData> data, String entityId, 
                                                                      java.util.Map<String, Integer> columnMap);

    /**
     * Detecta y maneja duplicados en los datos.
     * 
     * @param data datos válidos a verificar
     * @param entityId identificador de la entidad
     * @param skipDuplicates indica si omitir duplicados
     * @return resultado de detección de duplicados
     */
    DuplicateDetectionStrategy.DuplicateDetectionResult<TData> handleDuplicates(List<TData> data, String entityId, 
                                                                                boolean skipDuplicates);

    /**
     * Procesa los datos únicos y los persiste.
     * 
     * @param data datos únicos a procesar
     * @param request solicitud original
     * @return resultado del procesamiento
     */
    ImportProcessingResult<TData> processData(List<TData> data, TRequest request);

    /**
     * Crea la respuesta final con estadísticas y errores.
     * 
     * @param importId identificador único de la importación
     * @param request solicitud original
     * @param processingResult resultado del procesamiento
     * @param allErrors todos los errores acumulados
     * @param status estado final de la importación
     * @return respuesta estructurada
     */
    TResponse createResponse(String importId, TRequest request, ImportProcessingResult<TData> processingResult,
                           List<ImportErrorDetail> allErrors, ImportStatus status);

    /**
     * Genera un identificador único para la importación.
     * 
     * @return identificador único
     */
    default String generateImportId() {
        return com.thirdsmanagement.thirds.domain.utils.ExcelUtils.generateImportId();
    }
}
