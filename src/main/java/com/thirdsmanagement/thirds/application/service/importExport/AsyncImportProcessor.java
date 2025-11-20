package com.thirdsmanagement.thirds.application.service.importExport;

import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;
import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdImportException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdsErrorCode;
import com.thirdsmanagement.thirds.domain.model.ImportErrorDetail;
import com.thirdsmanagement.thirds.domain.model.ThirdExcelData;
import com.thirdsmanagement.thirds.application.service.third.BatchProcessor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @brief Servicio dedicado exclusivamente al procesamiento asíncrono de importaciones
 * 
 * Esta clase está separada de ImportThirdService para evitar problemas de self-invocation
 * con @Async. Spring requiere que los métodos @Async se llamen desde otra clase para
 * que el proxy AOP funcione correctamente.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncImportProcessor {

    private final ExcelParsingService excelParsingService;
    private final BatchValidationService batchValidationService;
    private final DuplicateDetectionService duplicateDetectionService;
    private final BatchProcessor batchProcessor;
    private final ImportJobTracker jobTracker;
    
    private static final boolean SKIP_DUPLICATES = true;
    private static final boolean CONTINUE_ON_ERROR = true;
    private static final int MAX_BATCH_SIZE = 1000;

    /**
     * @brief Procesa la importación de forma asíncrona con tracking de estado
     * 
     * Este método se ejecuta en un thread separado del pool de @Async configurado en AsyncConfig.
     * 
     * @param fileBytes contenido del archivo Excel en bytes
     * @param entId identificador de la entidad
     * @param fileName nombre del archivo
     * @param jobId identificador único del job
     */
    @Async
    public void processImportAsync(byte[] fileBytes, String entId, String fileName, String jobId) {
        long startTime = System.currentTimeMillis();
        
        log.info("JobId {}: Iniciando procesamiento ASÍNCRONO en thread: {}", 
                jobId, Thread.currentThread().getName());
        
        // Map para almacenar tiempos de cada fase
        java.util.Map<String, Long> phaseTimes = new java.util.LinkedHashMap<>();
        
        try {
            jobTracker.updateJobStatus(jobId, ImportStatus.PROCESSING);
            jobTracker.updateProgress(jobId, 10);
            
            // 1. PARSEO: Extraer datos del Excel desde bytes
            log.info("JobId {}: Fase 1 - Parsing del archivo Excel", jobId);
            long phase1Start = System.currentTimeMillis();
            ExcelParsingService.ExcelParsingResult parsingResult = 
                    excelParsingService.parseExcelFileFromBytes(fileBytes, entId);
            long phase1Time = System.currentTimeMillis() - phase1Start;
            phaseTimes.put("1. Parsing Excel", phase1Time);
            log.info("JobId {}: Fase 1 completada en {} ms", jobId, phase1Time);
            jobTracker.updateProgress(jobId, 20);

            if (parsingResult.getThirdsData().isEmpty()) {
                handleAsyncError(jobId, "No se encontraron datos válidos para importar", 
                        parsingResult.getErrors());
                return;
            }

            // 2. VALIDACIÓN: Validar datos en lote con cache optimizado
            log.info("JobId {}: Fase 2 - Validación de {} registros", jobId, parsingResult.getTotalRows());
            long phase2Start = System.currentTimeMillis();
            BatchValidationService.BatchValidationResult validationResult = 
                    batchValidationService.validateBatch(parsingResult.getThirdsData(), entId, 
                            parsingResult.getColumnMap());
            long phase2Time = System.currentTimeMillis() - phase2Start;
            phaseTimes.put("2. Validación", phase2Time);
            log.info("JobId {}: Fase 2 completada en {} ms", jobId, phase2Time);
            jobTracker.updateProgress(jobId, 40);

            // 3. DETECCIÓN DE DUPLICADOS: Filtrar duplicados existentes
            log.info("JobId {}: Fase 3 - Detección de duplicados", jobId);
            long phase3Start = System.currentTimeMillis();
            DuplicateDetectionService.DuplicateDetectionResult duplicateResult = 
                    duplicateDetectionService.detectDuplicates(validationResult.getValidRecords(), 
                            entId, SKIP_DUPLICATES);
            long phase3Time = System.currentTimeMillis() - phase3Start;
            phaseTimes.put("3. Detección Duplicados", phase3Time);
            log.info("JobId {}: Fase 3 completada en {} ms", jobId, phase3Time);
            jobTracker.updateProgress(jobId, 60);

            // 4. PROCESAMIENTO: Procesar registros únicos en lotes transaccionales
            log.info("JobId {}: Fase 4 - Procesamiento de {} registros únicos", jobId, 
                    duplicateResult.getUniqueRecords().size());
            long phase4Start = System.currentTimeMillis();
            BatchProcessingResult processingResult = processInBatches(duplicateResult.getUniqueRecords(), jobId);
            long phase4Time = System.currentTimeMillis() - phase4Start;
            phaseTimes.put("4. Procesamiento Lotes", phase4Time);
            log.info("JobId {}: Fase 4 completada en {} ms", jobId, phase4Time);
            jobTracker.updateProgress(jobId, 90);

            // 5. RESPUESTA: Consolidar y almacenar resultados finales
            log.info("JobId {}: Fase 5 - Consolidación de resultados", jobId);
            long phase5Start = System.currentTimeMillis();
            buildFinalAsyncResponse(jobId, entId, fileName, parsingResult, validationResult,
                    duplicateResult, processingResult);
            long phase5Time = System.currentTimeMillis() - phase5Start;
            phaseTimes.put("5. Consolidación", phase5Time);
            log.info("JobId {}: Fase 5 completada en {} ms", jobId, phase5Time);
            
            jobTracker.updateProgress(jobId, 100);
            
            // Calcular tiempo total
            long totalTime = System.currentTimeMillis() - startTime;
            
            // Imprimir tabla de tiempos
            printPhaseTimesTable(jobId, phaseTimes, totalTime, parsingResult.getTotalRows());
            
            log.info("JobId {}: Importación completada exitosamente en {} ms", jobId, totalTime);

        } catch (Exception e) {
            log.error("JobId {}: Error crítico durante la importación asíncrona", jobId, e);
            handleAsyncCriticalError(jobId, e);
        }
    }
    
    /**
     * @brief Imprime una tabla formateada con los tiempos de cada fase
     * @param jobId identificador del job
     * @param phaseTimes mapa con los tiempos de cada fase
     * @param totalTime tiempo total de procesamiento
     * @param totalRecords total de registros procesados
     */
    private void printPhaseTimesTable(String jobId, java.util.Map<String, Long> phaseTimes, 
                                      long totalTime, int totalRecords) {
        StringBuilder table = new StringBuilder("\n");
        table.append("╔════════════════════════════════════════════════════════════════════════╗\n");
        table.append(String.format("║  RESUMEN DE TIEMPOS - JobId: %-40s ║\n", jobId));
        table.append("╠════════════════════════════════════════════════════════════════════════╣\n");
        table.append("║  Fase                          │ Tiempo (ms) │ Tiempo (s) │ Porcentaje ║\n");
        table.append("╠════════════════════════════════════════════════════════════════════════╣\n");
        
        for (java.util.Map.Entry<String, Long> entry : phaseTimes.entrySet()) {
            String phaseName = entry.getKey();
            long phaseTime = entry.getValue();
            double seconds = phaseTime / 1000.0;
            double percentage = (phaseTime * 100.0) / totalTime;
            
            table.append(String.format("║  %-30s│ %,11d │ %10.2f │   %6.2f%% ║\n",
                    phaseName, phaseTime, seconds, percentage));
        }
        
        table.append("╠════════════════════════════════════════════════════════════════════════╣\n");
        table.append(String.format("║  TOTAL                         │ %,11d │ %10.2f │  100.00%% ║\n",
                totalTime, totalTime / 1000.0));
        table.append("╠════════════════════════════════════════════════════════════════════════╣\n");
        
        // Calcular métricas de rendimiento
        double recordsPerSecond = (totalRecords * 1000.0) / totalTime;
        double msPerRecord = totalTime / (double) totalRecords;
        
        table.append(String.format("║  Total Registros: %-15d                                   ║\n", totalRecords));
        table.append(String.format("║  Rendimiento: %,.2f registros/seg                                ║\n", recordsPerSecond));
        table.append(String.format("║  Tiempo por registro: %.2f ms                                    ║\n", msPerRecord));
        table.append("╚════════════════════════════════════════════════════════════════════════╝");
        
        log.info("JobId {}: {}", jobId, table.toString());
    }

    /**
     * @brief Procesar registros únicos en lotes optimizados con tracking de progreso
     */
    private BatchProcessingResult processInBatches(List<ThirdExcelData> uniqueRecords, String jobId) {
        if (uniqueRecords.isEmpty()) {
            return new BatchProcessingResult(0, 0, 0);
        }

        long cacheLoadStart = System.currentTimeMillis();
        String entId = uniqueRecords.get(0).getEntId();
        BatchValidationService.ReferenceDataCache cache = batchValidationService.preloadReferenceData(entId);
        long cacheLoadTime = System.currentTimeMillis() - cacheLoadStart;
        log.info("JobId {}: Cache de referencia precargado en {} ms", jobId, cacheLoadTime);

        List<List<ThirdExcelData>> batches = createBatches(uniqueRecords, MAX_BATCH_SIZE);
        log.info("JobId {}: Dividido en {} lotes de hasta {} registros", jobId, batches.size(), MAX_BATCH_SIZE);

        AtomicInteger totalSuccess = new AtomicInteger(0);
        AtomicInteger totalFailure = new AtomicInteger(0);
        AtomicInteger totalSkipped = new AtomicInteger(0);
        long totalBatchProcessingTime = 0;

        int totalBatches = batches.size();
        for (int i = 0; i < totalBatches; i++) {
            List<ThirdExcelData> batch = batches.get(i);

            try {
                long batchStart = System.currentTimeMillis();
                BatchProcessor.BatchProcessingResult batchResult = batchProcessor.processBatch(
                        batch, cache, CONTINUE_ON_ERROR);
                long batchTime = System.currentTimeMillis() - batchStart;
                totalBatchProcessingTime += batchTime;

                totalSuccess.addAndGet(batchResult.getSuccessCount());
                totalFailure.addAndGet(batchResult.getFailureCount());
                totalSkipped.addAndGet(batchResult.getSkippedCount());

                int batchProgress = 60 + (30 * (i + 1) / totalBatches);
                jobTracker.updateProgress(jobId, batchProgress);
                
                // Log detallado cada 10 lotes o en lotes lentos (>2 segundos)
                if ((i + 1) % 10 == 0 || batchTime > 2000) {
                    double recordsPerSec = (batch.size() * 1000.0) / batchTime;
                    log.info("JobId {}: Lote {}/{} - {} ms ({} reg/seg) - Exitosos: {}, Fallidos: {}, Omitidos: {}",
                            jobId, i + 1, totalBatches, batchTime, String.format("%.2f", recordsPerSec),
                            batchResult.getSuccessCount(), batchResult.getFailureCount(), 
                            batchResult.getSkippedCount());
                } else {
                    log.debug("JobId {}: Lote {}/{} procesado en {} ms - Exitosos: {}, Fallidos: {}, Omitidos: {}",
                            jobId, i + 1, totalBatches, batchTime, batchResult.getSuccessCount(),
                            batchResult.getFailureCount(), batchResult.getSkippedCount());
                }

            } catch (Exception e) {
                throw new ThirdImportException(ThirdsErrorCode.THIRD_EXPORT_ERROR,
                        "Error crítico en lote " + (i + 1) + ": " + e.getMessage(), e);
            }
        }

        // Resumen de rendimiento de la Fase 4
        double avgTimePerBatch = totalBatchProcessingTime / (double) totalBatches;
        double totalRecordsProcessed = totalSuccess.get() + totalFailure.get() + totalSkipped.get();
        double avgTimePerRecord = totalBatchProcessingTime / totalRecordsProcessed;
        
        log.info("JobId {}: ⏱️ DESGLOSE FASE 4 - Carga Cache: {} ms, Procesamiento Lotes: {} ms", 
                jobId, cacheLoadTime, totalBatchProcessingTime);
        log.info("JobId {}: 📊 MÉTRICAS FASE 4 - Promedio por lote: {:.2f} ms, Promedio por registro: {:.2f} ms",
                jobId, avgTimePerBatch, avgTimePerRecord);

        return new BatchProcessingResult(totalSuccess.get(), totalFailure.get(), totalSkipped.get());
    }

    private void buildFinalAsyncResponse(String jobId, String entId, String fileName,
            ExcelParsingService.ExcelParsingResult parsingResult,
            BatchValidationService.BatchValidationResult validationResult,
            DuplicateDetectionService.DuplicateDetectionResult duplicateResult,
            BatchProcessingResult processingResult) {

        List<ImportErrorDetail> allErrors = new ArrayList<>();
        allErrors.addAll(parsingResult.getErrors());
        allErrors.addAll(validationResult.getErrors());
        allErrors.addAll(duplicateResult.getErrors());

        int duplicatesFromDetection = validationResult.getValidCount() - duplicateResult.getUniqueCount();
        int duplicatesFromProcessing = processingResult.getSkippedCount();
        int totalDuplicatesSkipped = duplicatesFromDetection + duplicatesFromProcessing;

        int validationFailures = calculateUniqueFailedRecords(validationResult.getErrors());
        int processingFailures = processingResult.getFailureCount();
        int totalFailures = validationFailures + processingFailures;

        jobTracker.updateJobMetrics(jobId, 
                parsingResult.getTotalRows(),
                processingResult.getSuccessCount(),
                totalFailures,
                totalDuplicatesSkipped);
        
        jobTracker.addErrors(jobId, allErrors);

        ImportStatus finalStatus;
        if (totalFailures > 0 && processingResult.getSuccessCount() > 0) {
            finalStatus = ImportStatus.COMPLETED_WITH_ERRORS;
        } else if (processingResult.getSuccessCount() == 0) {
            finalStatus = ImportStatus.FAILED;
        } else {
            finalStatus = ImportStatus.COMPLETED;
        }

        jobTracker.updateJobStatus(jobId, finalStatus);
    }

    private void handleAsyncError(String jobId, String errorMessage, List<ImportErrorDetail> errors) {
        log.error("JobId {}: {}", jobId, errorMessage);
        jobTracker.updateJobMetrics(jobId, 0, 0, 0, 0);
        jobTracker.addErrors(jobId, errors);
        jobTracker.updateJobStatus(jobId, ImportStatus.FAILED);
    }

    private void handleAsyncCriticalError(String jobId, Exception e) {
        List<ImportErrorDetail> systemErrors = List.of(
                ImportErrorDetail.builder()
                        .errorCode("SYSTEM_ERROR")
                        .errorMessage("Error del sistema durante la importación: " + e.getMessage())
                        .errorType(ImportErrorType.SYSTEM_ERROR)
                        .build());

        jobTracker.addErrors(jobId, systemErrors);
        jobTracker.updateJobStatus(jobId, ImportStatus.FAILED);
    }

    private <T> List<List<T>> createBatches(List<T> list, int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }

    private int calculateUniqueFailedRecords(List<ImportErrorDetail> errors) {
        return (int) errors.stream()
                .mapToInt(ImportErrorDetail::getRowNumber)
                .distinct()
                .count();
    }

    @Getter
    @AllArgsConstructor
    private static class BatchProcessingResult {
        private final int successCount;
        private final int failureCount;
        private final int skippedCount;
    }
}

