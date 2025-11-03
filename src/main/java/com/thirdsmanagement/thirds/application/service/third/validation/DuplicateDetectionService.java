package com.thirdsmanagement.thirds.application.service.third.validation;

import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;
import com.thirdsmanagement.thirds.domain.model.ImportErrorDetail;
import com.thirdsmanagement.thirds.domain.model.ThirdExcelData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio especializado en la detección de duplicados durante la importación.
 * Maneja tanto duplicados internos del Excel como duplicados con la base de
 * datos existente.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DuplicateDetectionService {

    private final ThirdOutputPort thirdOutputPort;

    /**
     * Detecta duplicados en un lote de datos de terceros.
     * 
     * @param thirdsData     lista de datos de terceros a analizar
     * @param entId          identificador de la entidad
     * @param skipDuplicates indica si se deben omitir duplicados o marcar como
     *                       error
     * @return resultado de detección con registros únicos y reportes de duplicados
     */
    public DuplicateDetectionResult detectDuplicates(List<ThirdExcelData> thirdsData, String entId,
            boolean skipDuplicates) {

        List<ThirdExcelData> uniqueRecords = new ArrayList<>();
        List<ImportErrorDetail> errors = new ArrayList<>();

        // 1. Detectar duplicados internos del Excel
        DuplicateAnalysisResult internalDuplicates = detectInternalDuplicates(thirdsData, skipDuplicates);
        uniqueRecords.addAll(internalDuplicates.getUniqueRecords());
        errors.addAll(internalDuplicates.getErrors());

        // 2. Detectar duplicados con la base de datos existente
        if (!uniqueRecords.isEmpty()) {
            DuplicateAnalysisResult dbDuplicates = detectDatabaseDuplicates(uniqueRecords, entId, skipDuplicates);
            uniqueRecords = dbDuplicates.getUniqueRecords();
            errors.addAll(dbDuplicates.getErrors());
        }

        return DuplicateDetectionResult.builder()
                .uniqueRecords(uniqueRecords)
                .errors(errors)
                .totalAnalyzed(thirdsData.size())
                .uniqueCount(uniqueRecords.size())
                .duplicateCount(thirdsData.size() - uniqueRecords.size())
                .build();
    }

    /**
     * Detecta duplicados internos dentro del archivo Excel.
     */
    private DuplicateAnalysisResult detectInternalDuplicates(List<ThirdExcelData> thirdsData, boolean skipDuplicates) {

        List<ThirdExcelData> uniqueRecords = new ArrayList<>();
        List<ImportErrorDetail> errors = new ArrayList<>();

        // Mapa para rastrear registros ya procesados por ID de identificación
        Map<String, ThirdExcelData> processedRecords = new HashMap<>();

        for (ThirdExcelData currentRecord : thirdsData) {
            if (currentRecord.getIdNumber() == null) {
                continue;
            }

            String duplicateKey = createDuplicateKey(currentRecord);
            ThirdExcelData existingRecord = processedRecords.get(duplicateKey);

            if (existingRecord != null) {
                handleInternalDuplicate(currentRecord, existingRecord, skipDuplicates, errors);
            } else {
                processedRecords.put(duplicateKey, currentRecord);
                uniqueRecords.add(currentRecord);
            }
        }

        return DuplicateAnalysisResult.builder()
                .uniqueRecords(uniqueRecords)
                .errors(errors)
                .build();
    }

    /**
     * Detecta duplicados con registros existentes en la base de datos.
     */
    private DuplicateAnalysisResult detectDatabaseDuplicates(List<ThirdExcelData> thirdsData, String entId,
            boolean skipDuplicates) {

        List<ThirdExcelData> uniqueRecords = new ArrayList<>();
        List<ImportErrorDetail> errors = new ArrayList<>();

        // batch: verificar existencia
        Set<Long> idNumbers = thirdsData.stream()
                .map(ThirdExcelData::getIdNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> existingIds = thirdOutputPort.findExistingIdNumbers(idNumbers, entId);

        for (ThirdExcelData currentRecord : thirdsData) {
            if (currentRecord.getIdNumber() == null) {
                continue;
            }

            if (existingIds.contains(currentRecord.getIdNumber())) {
                handleDatabaseDuplicate(currentRecord, skipDuplicates, errors);
            } else {
                uniqueRecords.add(currentRecord);
            }
        }

        return DuplicateAnalysisResult.builder()
                .uniqueRecords(uniqueRecords)
                .errors(errors)
                .build();
    }

    /**
     * Maneja un duplicado interno encontrado.
     */
    private void handleInternalDuplicate(ThirdExcelData currentRecord, ThirdExcelData existingRecord,
            boolean skipDuplicates, List<ImportErrorDetail> errors) {
        if (!skipDuplicates) {
            // Solo crear error si no se deben omitir duplicados
            errors.add(ImportErrorDetail.builder()
                    .rowNumber(currentRecord.getRowNumber())
                    .columnName("Número Identificación")
                    .fieldValue(String.valueOf(currentRecord.getIdNumber()))
                    .errorCode("INTERNAL_DUPLICATE_FOUND")
                    .errorMessage(String.format(
                            "Número de identificación duplicado en archivo. Primera ocurrencia en fila %d.",
                            existingRecord.getRowNumber()))
                    .errorType(ImportErrorType.DUPLICATE_ERROR)
                    .build());
        }
    }

    /**
     * Maneja un duplicado con la base de datos encontrado.
     */
    private void handleDatabaseDuplicate(ThirdExcelData currentRecord, boolean skipDuplicates,
            List<ImportErrorDetail> errors) {
        if (!skipDuplicates) {
            // Solo crear error si no se deben omitir duplicados
            errors.add(ImportErrorDetail.builder()
                    .rowNumber(currentRecord.getRowNumber())
                    .columnName("Número Identificación")
                    .fieldValue(String.valueOf(currentRecord.getIdNumber()))
                    .errorCode("DATABASE_DUPLICATE_FOUND")
                    .errorMessage("El tercero ya existe en la base de datos.")
                    .errorType(ImportErrorType.DUPLICATE_ERROR)
                    .build());
        }
    }

    /**
     * Crea una clave única para identificar duplicados.
     * Considera número de identificación y entidad.
     */
    private String createDuplicateKey(ThirdExcelData record) {
        return record.getEntId() + "_" + record.getIdNumber();
    }

    /**
     * Clase que representa el resultado de análisis de duplicados.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DuplicateAnalysisResult {
        private List<ThirdExcelData> uniqueRecords;
        private List<ImportErrorDetail> errors;
    }

    /**
     * Clase que representa el resultado completo de detección de duplicados.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DuplicateDetectionResult {
        private List<ThirdExcelData> uniqueRecords;
        private List<ImportErrorDetail> errors;
        private int totalAnalyzed;
        private int uniqueCount;
        private int duplicateCount;
    }

}
