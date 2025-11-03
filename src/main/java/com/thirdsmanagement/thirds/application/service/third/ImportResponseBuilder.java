package com.thirdsmanagement.thirds.application.service.third;

import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import com.thirdsmanagement.thirds.domain.model.ImportErrorDetail;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdImportRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdImportResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Builder especializado en la construcción de respuestas de importación.
 * Centraliza la lógica de determinación de estados y cálculos de métricas.
 */
@Slf4j
@Service
public class ImportResponseBuilder {

    /**
     * Construye respuesta de importación exitosa o parcialmente exitosa.
     */
    public ThirdImportResponse buildSuccessResponse(ThirdImportRequest request,
            ImportMetrics metrics,
            List<ImportErrorDetail> allErrors) {

        ImportStatus status = determineImportStatus(metrics, allErrors);

        return ThirdImportResponse.builder()
                .entId(request.getEntId())
                .fileName(request.getFileName())
                .status(status)
                .totalRecords(metrics.getTotalRecords())
                .successfulImports(metrics.getSuccessCount())
                .failedImports(metrics.getFailureCount())
                .duplicatesSkipped(metrics.getDuplicatesSkipped())
                .errors(allErrors)
                .build();
    }

    /**
     * Construye respuesta de importación fallida.
     */
    public ThirdImportResponse buildFailedResponse(ThirdImportRequest request,
            String errorMessage,
            List<ImportErrorDetail> errors) {

        return ThirdImportResponse.builder()
                .entId(request.getEntId())
                .fileName(request.getFileName())
                .status(ImportStatus.FAILED)
                .totalRecords(0)
                .successfulImports(0)
                .failedImports(0)
                .duplicatesSkipped(0)
                .errors(errors)
                .build();
    }

    /**
     * Determina el estado final de la importación basado en métricas.
     */
    private ImportStatus determineImportStatus(ImportMetrics metrics, List<ImportErrorDetail> allErrors) {
        boolean hasProcessingFailures = metrics.getFailureCount() > 0;
        boolean hasValidationErrors = !allErrors.isEmpty();
        boolean hasSuccesses = metrics.getSuccessCount() > 0;

        if (!hasProcessingFailures && !hasValidationErrors) {
            return ImportStatus.COMPLETED;
        } else if (hasSuccesses) {
            return ImportStatus.COMPLETED_WITH_ERRORS;
        } else {
            return ImportStatus.FAILED;
        }
    }


    /**
     * Clase para encapsular métricas de importación.
     */
    @Getter
    @AllArgsConstructor
    public static class ImportMetrics {
        private final int totalRecords;
        private final int successCount;
        private final int failureCount;
        private final int duplicatesSkipped;
    }
}
