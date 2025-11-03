package com.thirdsmanagement.thirds.application.service.third;

import com.thirdsmanagement.thirds.application.service.importExport.BatchValidationService;
import com.thirdsmanagement.thirds.application.service.importExport.DataConverter;
import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;
import com.thirdsmanagement.thirds.domain.model.ImportErrorDetail;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdExcelData;
import com.thirdsmanagement.thirds.domain.utils.ValidationUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Procesador de lotes optimizado para importación masiva.
 * Maneja transacciones por lote (no por registro) para mejor performance.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchProcessor {

    private final CreateThirdService createThirdService;
    private final DataConverter dataConverter;

    /**
     * Procesa un lote completo de registros en una sola transacción.
     * OPTIMIZACIÓN: Una transacción por lote, no por registro.
     */
    @Transactional
    public BatchProcessingResult processBatch(List<ThirdExcelData> batch, 
                                             BatchValidationService.ReferenceDataCache cache,
                                             boolean continueOnError) {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger skippedCount = new AtomicInteger(0);
        List<ImportErrorDetail> errors = new ArrayList<>();


        for (ThirdExcelData excelData : batch) {
            try {
                ProcessingResult result = processRecord(excelData, cache);
                
                switch (result.getStatus()) {
                    case SUCCESS:
                        successCount.incrementAndGet();
                        break;
                    case DUPLICATE_SKIPPED:
                        skippedCount.incrementAndGet();
                        break;
                    case FAILED:
                        if (continueOnError) {
                            failureCount.incrementAndGet();
                            errors.add(createProcessingError(excelData, result.getErrorMessage()));
                        } else {
                            // En modo "parar en error", lanzar excepción para rollback
                            throw new RuntimeException("Error en registro fila " + excelData.getRowNumber() + ": " + result.getErrorMessage());
                        }
                        break;
                    case SKIPPED:
                        skippedCount.incrementAndGet();
                        if (result.getErrorMessage() != null) {
                            errors.add(createProcessingError(excelData, result.getErrorMessage()));
                        }
                        break;
                }

            } catch (Exception e) {
                if (continueOnError) {
                    failureCount.incrementAndGet();
                    errors.add(createProcessingError(excelData, e.getMessage()));
                } else {
                    // Re-lanzar para rollback del lote completo
                    throw e;
                }
            }
        }


        return BatchProcessingResult.builder()
                .successCount(successCount.get())
                .failureCount(failureCount.get())
                .skippedCount(skippedCount.get())
                .errors(errors)
                .build();
    }

    /**
     * Procesa un registro individual usando el cache pre-cargado.
     */
    private ProcessingResult processRecord(ThirdExcelData excelData, BatchValidationService.ReferenceDataCache cache) {
        try {
            // Convertir usando cache (evita consultas N+1)
            Third third = dataConverter.convertWithCache(excelData, cache);
            
            if (third == null) {
                return ProcessingResult.skipped("Registro omitido por datos incompletos");
            }

            // Obtener códigos geográficos resueltos
            DataConverter.GeographyData geography = dataConverter.getGeographyData(excelData, cache);

            // Crear tercero - el servicio maneja duplicados internamente
            createThirdService.createThird(third, 
                    geography.getCountryCode(), geography.getStateCode(), geography.getCityCode());
            
            return ProcessingResult.success();

        } catch (Exception e) {
            // Clasificar el tipo de error
            if (ValidationUtils.isDuplicateError(e.getMessage())) {
                return ProcessingResult.duplicateSkipped();
            }
            if (ValidationUtils.isGeographyError(e.getMessage())) {
                return ProcessingResult.skipped("Error geográfico: " + e.getMessage());
            }
            
            return ProcessingResult.failed(e.getMessage());
        }
    }

    /**
     * Crea un error de procesamiento estructurado.
     */
    private ImportErrorDetail createProcessingError(ThirdExcelData excelData, String message) {
        return ImportErrorDetail.builder()
                .rowNumber(excelData.getRowNumber())
                .errorCode("PROCESSING_ERROR")
                .errorMessage("Error al procesar: " + message)
                .errorType(ImportErrorType.SYSTEM_ERROR)
                .build();
    }

    /**
     * Resultado del procesamiento de un lote.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchProcessingResult {
        private int successCount;
        private int failureCount;
        private int skippedCount;
        private List<ImportErrorDetail> errors;
    }

    /**
     * Resultado del procesamiento de un registro individual.
     */
    @Data
    @AllArgsConstructor
    private static class ProcessingResult {
        private ProcessingStatus status;
        private String errorMessage;

        public static ProcessingResult success() {
            return new ProcessingResult(ProcessingStatus.SUCCESS, null);
        }

        public static ProcessingResult duplicateSkipped() {
            return new ProcessingResult(ProcessingStatus.DUPLICATE_SKIPPED, null);
        }

        public static ProcessingResult failed(String errorMessage) {
            return new ProcessingResult(ProcessingStatus.FAILED, errorMessage);
        }

        public static ProcessingResult skipped(String reason) {
            return new ProcessingResult(ProcessingStatus.SKIPPED, reason);
        }
    }

    /**
     * Estados de procesamiento de un registro.
     */
    private enum ProcessingStatus {
        SUCCESS, DUPLICATE_SKIPPED, FAILED, SKIPPED
    }
}
