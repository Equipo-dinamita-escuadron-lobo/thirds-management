package com.thirdsmanagement.thirds.application.service.importExport;

import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import com.thirdsmanagement.thirds.domain.model.ImportErrorDetail;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdImportRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ThirdImportResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @brief Builder especializado en la construcción de respuestas de importación
 *
 * Centraliza la lógica de determinación de estados y cálculos de métricas
 * para proporcionar respuestas consistentes en operaciones de importación.
 */
@Slf4j
@Service
public class ImportResponseBuilder {

    /**
     * @brief Construye respuesta de importación exitosa o parcialmente exitosa
     * @param request Solicitud original de importación
     * @param metrics Métricas consolidadas de la importación
     * @param allErrors Lista completa de errores encontrados
     * @return Respuesta estructurada con estado y métricas finales
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
     * @brief Construye respuesta de importación fallida
     * @param request Solicitud original de importación
     * @param errorMessage Mensaje descriptivo del error crítico
     * @param errors Lista de errores específicos encontrados
     * @return Respuesta con estado FAILED y métricas en cero
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
     * @brief Determina el estado final de la importación basado en métricas
     * @param metrics Métricas consolidadas del proceso de importación
     * @param allErrors Lista completa de errores encontrados
     * @return Estado final: COMPLETED, COMPLETED_WITH_ERRORS, o FAILED
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
     * @brief Clase para encapsular métricas de importación
     *
     * Contiene todas las métricas relevantes del proceso de importación:
     * total de registros, exitosos, fallidos y duplicados omitidos.
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
