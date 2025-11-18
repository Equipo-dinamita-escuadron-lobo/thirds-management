package com.thirdsmanagement.thirds.application.service.importExport;

import com.thirdsmanagement.thirds.application.ports.input.ImportThirdUseCase;
import com.thirdsmanagement.thirds.application.service.third.BatchProcessor;
import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdImportException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdsErrorCode;
import com.thirdsmanagement.thirds.domain.model.ImportErrorDetail;
import com.thirdsmanagement.thirds.domain.model.ThirdExcelData;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdImportRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ThirdImportResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @brief Servicio principal para importación masiva de terceros desde Excel
 *
 * Orquesta el proceso completo de importación con validación por lotes,
 * detección de duplicados y procesamiento transaccional optimizado.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImportThirdService implements ImportThirdUseCase {

    private final ExcelParsingService excelParsingService;
    private final BatchValidationService batchValidationService;
    private final DuplicateDetectionService duplicateDetectionService;
    private final BatchProcessor batchProcessor;
    private final ImportResponseBuilder responseBuilder;
    
    private static final boolean SKIP_DUPLICATES = true; 
    private static final boolean CONTINUE_ON_ERROR = true; 
    private static final int MAX_BATCH_SIZE = 100; 

    
    @Override
    public ThirdImportResponse importThirdsFromExcel(ThirdImportRequest importRequest) {
        try {
            // 1. PARSEO: Extraer datos del Excel
            ExcelParsingService.ExcelParsingResult parsingResult = parseExcelFile(importRequest);

            if (parsingResult.getThirdsData().isEmpty()) {
                return responseBuilder.buildFailedResponse(importRequest,
                        "No se encontraron datos válidos para importar", parsingResult.getErrors());
            }

            // 2. VALIDACIÓN: Validar datos en lote con cache optimizado
            BatchValidationService.BatchValidationResult validationResult = validateData(parsingResult, importRequest);

            // 3. DETECCIÓN DE DUPLICADOS: Filtrar duplicados existentes
            DuplicateDetectionService.DuplicateDetectionResult duplicateResult = detectDuplicates(validationResult,
                    importRequest);

            // 4. PROCESAMIENTO: Procesar registros únicos en lotes transaccionales
            BatchProcessingResult processingResult = processInBatches(duplicateResult.getUniqueRecords());

            // 5. RESPUESTA: Construir respuesta final consolidada
            return buildFinalResponse(importRequest, parsingResult, validationResult,
                    duplicateResult, processingResult);

        } catch (Exception e) {
            return handleCriticalError(importRequest, e);
        }
    }

    /**
     * @brief Parsear archivo Excel
     * @param importRequest Solicitud de importación con el archivo Excel
     * @return Resultado del parsing con datos extraídos y mapa de columnas
     */
    private ExcelParsingService.ExcelParsingResult parseExcelFile(ThirdImportRequest importRequest) {
        return excelParsingService.parseExcelFile(importRequest.getExcelFile(), importRequest.getEntId());
    }

    /**
     * @brief Validar datos con cache optimizado
     * @param parsingResult Resultado del parsing con datos extraídos
     * @param importRequest Solicitud de importación con contexto
     * @return Resultado de validación con registros válidos y errores
     */
    private BatchValidationService.BatchValidationResult validateData(
            ExcelParsingService.ExcelParsingResult parsingResult,
            ThirdImportRequest importRequest) {
        return batchValidationService.validateBatch(
                parsingResult.getThirdsData(),
                importRequest.getEntId(),
                parsingResult.getColumnMap());
    }

    /**
     * @brief Detectar y filtrar duplicados
     * @param validationResult Resultado de validación con registros válidos
     * @param importRequest Solicitud de importación con configuración
     * @return Resultado de detección con registros únicos y duplicados encontrados
     */
    private DuplicateDetectionService.DuplicateDetectionResult detectDuplicates(
            BatchValidationService.BatchValidationResult validationResult,
            ThirdImportRequest importRequest) {
        return duplicateDetectionService.detectDuplicates(
                validationResult.getValidRecords(),
                importRequest.getEntId(),
                SKIP_DUPLICATES);
    }

    /**
     * @brief Paso 4: Procesar registros únicos en lotes optimizados
     * @param uniqueRecords Lista de registros únicos a procesar
     * @return Resultado consolidado del procesamiento por lotes
     */
    private BatchProcessingResult processInBatches(List<ThirdExcelData> uniqueRecords) {
        if (uniqueRecords.isEmpty()) {
            return new BatchProcessingResult(0, 0, 0);
        }

        String entId = uniqueRecords.get(0).getEntId();
        BatchValidationService.ReferenceDataCache cache = batchValidationService.preloadReferenceData(entId);

        // Dividir en lotes y procesar
        List<List<ThirdExcelData>> batches = createBatches(uniqueRecords, MAX_BATCH_SIZE);

        AtomicInteger totalSuccess = new AtomicInteger(0);
        AtomicInteger totalFailure = new AtomicInteger(0);
        AtomicInteger totalSkipped = new AtomicInteger(0);

        for (int i = 0; i < batches.size(); i++) {
            List<ThirdExcelData> batch = batches.get(i);

            try {
                BatchProcessor.BatchProcessingResult batchResult = batchProcessor.processBatch(
                        batch, cache, CONTINUE_ON_ERROR);

                totalSuccess.addAndGet(batchResult.getSuccessCount());
                totalFailure.addAndGet(batchResult.getFailureCount());
                totalSkipped.addAndGet(batchResult.getSkippedCount());

                // Si hay errores y no continuamos en errores, parar
                if (batchResult.getFailureCount() > 0 && !CONTINUE_ON_ERROR) {
                    break;
                }

            } catch (Exception e) {
                throw new ThirdImportException(ThirdsErrorCode.THIRD_EXPORT_ERROR,
                        "Error crítico en lote " + (i + 1) + ": " + e.getMessage(), e);
            }
        }

        return new BatchProcessingResult(totalSuccess.get(), totalFailure.get(), totalSkipped.get());
    }

    /**
     * @brief Paso 5: Construir respuesta final consolidada
     * @param importRequest Solicitud original de importación
     * @param parsingResult Resultado del parsing del archivo
     * @param validationResult Resultado de validación por lotes
     * @param duplicateResult Resultado de detección de duplicados
     * @param processingResult Resultado del procesamiento por lotes
     * @return Respuesta final consolidada con métricas y errores
     */
    private ThirdImportResponse buildFinalResponse(ThirdImportRequest importRequest,
            ExcelParsingService.ExcelParsingResult parsingResult,
            BatchValidationService.BatchValidationResult validationResult,
            DuplicateDetectionService.DuplicateDetectionResult duplicateResult,
            BatchProcessingResult processingResult) {

        // Consolidar todos los errores
        List<ImportErrorDetail> allErrors = new ArrayList<>();
        allErrors.addAll(parsingResult.getErrors());
        allErrors.addAll(validationResult.getErrors());
        allErrors.addAll(duplicateResult.getErrors());

        // Calcular duplicados omitidos
        int duplicatesFromDetection = validationResult.getValidCount() - duplicateResult.getUniqueCount();
        int duplicatesFromProcessing = processingResult.getSkippedCount();
        int totalDuplicatesSkipped = duplicatesFromDetection + duplicatesFromProcessing;

        // Calcular fallos totales: registros únicos con errores de validación + errores de procesamiento
        int validationFailures = calculateUniqueFailedRecords(validationResult.getErrors());
        int processingFailures = processingResult.getFailureCount();
        int totalFailures = validationFailures + processingFailures;

        // Crear métricas consolidadas
        ImportResponseBuilder.ImportMetrics metrics = new ImportResponseBuilder.ImportMetrics(
                parsingResult.getTotalRows(),
                processingResult.getSuccessCount(),
                totalFailures,
                totalDuplicatesSkipped);

        return responseBuilder.buildSuccessResponse(importRequest, metrics, allErrors);
    }

    /**
     * @brief Maneja errores críticos del sistema
     * @param importRequest Solicitud de importación que falló
     * @param e Excepción crítica que ocurrió
     * @return Respuesta de error con detalles del fallo del sistema
     */
    private ThirdImportResponse handleCriticalError(ThirdImportRequest importRequest, Exception e) {
        List<ImportErrorDetail> systemErrors = List.of(
                ImportErrorDetail.builder()
                        .errorCode("SYSTEM_ERROR")
                        .errorMessage("Error del sistema durante la importación: " + e.getMessage())
                        .errorType(ImportErrorType.SYSTEM_ERROR)
                        .build());

        return responseBuilder.buildFailedResponse(importRequest, e.getMessage(), systemErrors);
    }

    /**
     * @brief Divide lista en lotes del tamaño especificado
     * @param <T> Tipo de elementos en la lista
     * @param list Lista original a dividir
     * @param batchSize Tamaño máximo de cada lote
     * @return Lista de lotes con elementos distribuidos
     */
    private <T> List<List<T>> createBatches(List<T> list, int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }

    /**
     * @brief Calcula el número de registros únicos que tienen errores de validación
     *
     * Un registro puede tener múltiples errores, pero solo cuenta como 1 fallo
     * para evitar duplicar el conteo de registros fallidos.
     *
     * @param errors Lista de errores de importación
     * @return Número de registros únicos que fallaron
     */
    private int calculateUniqueFailedRecords(List<ImportErrorDetail> errors) {
        return (int) errors.stream()
                .mapToInt(ImportErrorDetail::getRowNumber)
                .distinct()
                .count();
    }

    /**
     * @brief Clase interna para resultado de procesamiento en lotes
     *
     * Contiene métricas consolidadas del procesamiento de lotes:
     * registros procesados exitosamente, fallidos y omitidos.
     */
    @Getter
    @AllArgsConstructor
    private static class BatchProcessingResult {
        private final int successCount;
        private final int failureCount;
        private final int skippedCount;
    }
}
