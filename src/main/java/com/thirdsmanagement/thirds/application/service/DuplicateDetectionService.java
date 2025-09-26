package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.model.ThirdExcelData;
import com.thirdsmanagement.thirds.domain.utils.ImportConstants;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ImportErrorDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio especializado en la detección de duplicados durante la importación.
 * Maneja tanto duplicados internos del Excel como duplicados con la base de datos existente.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DuplicateDetectionService {

    private final ThirdOutputPort thirdOutputPort;

    /**
     * Detecta duplicados en un lote de datos de terceros.
     * 
     * @param thirdsData lista de datos de terceros a analizar
     * @param entId identificador de la entidad
     * @param skipDuplicates indica si se deben omitir duplicados o marcar como error
     * @return resultado de detección con registros únicos y reportes de duplicados
     */
    public DuplicateDetectionResult detectDuplicates(List<ThirdExcelData> thirdsData, String entId, boolean skipDuplicates) {
        log.info("Iniciando detección de duplicados para {} registros en entidad {}", thirdsData.size(), entId);

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

        log.info("Detección completada. Únicos: {}, Errores: {}", 
                uniqueRecords.size(), errors.size());

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
        log.debug("Detectando duplicados internos en {} registros", thirdsData.size());

        List<ThirdExcelData> uniqueRecords = new ArrayList<>();
        List<ImportErrorDetail> errors = new ArrayList<>();

        // Mapa para rastrear registros ya procesados por ID de identificación
        Map<String, ThirdExcelData> processedRecords = new HashMap<>();
        
        for (ThirdExcelData currentRecord : thirdsData) {
            if (currentRecord.getIdNumber() == null) {
                // Ya debería estar validado en pasos anteriores, pero por seguridad
                continue;
            }

            String duplicateKey = createDuplicateKey(currentRecord);
            ThirdExcelData existingRecord = processedRecords.get(duplicateKey);

            if (existingRecord != null) {
                // Encontrado duplicado interno
                handleInternalDuplicate(currentRecord, existingRecord, skipDuplicates, errors);
            } else {
                // Registro único, agregarlo a los procesados
                processedRecords.put(duplicateKey, currentRecord);
                uniqueRecords.add(currentRecord);
            }
        }

        log.debug("Duplicados internos detectados: {} únicos de {} totales", uniqueRecords.size(), thirdsData.size());

        return DuplicateAnalysisResult.builder()
                .uniqueRecords(uniqueRecords)
                .errors(errors)
                .build();
    }

    /**
     * Detecta duplicados con registros existentes en la base de datos.
     */
    private DuplicateAnalysisResult detectDatabaseDuplicates(List<ThirdExcelData> thirdsData, String entId, boolean skipDuplicates) {
        log.debug("Detectando duplicados con base de datos para {} registros", thirdsData.size());

        List<ThirdExcelData> uniqueRecords = new ArrayList<>();
        List<ImportErrorDetail> errors = new ArrayList<>();

        // Optimización: verificar existencia en lotes
        Set<Long> idNumbers = thirdsData.stream()
                .map(ThirdExcelData::getIdNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Verificar cuáles ya existen en la base de datos
        Set<Long> existingIds = new HashSet<>();
        for (Long idNumber : idNumbers) {
            if (thirdOutputPort.existThirdById(idNumber, entId)) {
                existingIds.add(idNumber);
                log.debug("Tercero {} ya existe en la base de datos para entidad {}", idNumber, entId);
            }
        }
        
        log.debug("De {} registros únicos, {} ya existen en la base de datos", idNumbers.size(), existingIds.size());

        // Procesar cada registro
        for (ThirdExcelData currentRecord : thirdsData) {
            if (currentRecord.getIdNumber() == null) {
                continue;
            }

            if (existingIds.contains(currentRecord.getIdNumber())) {
                // Encontrado duplicado con base de datos
                handleDatabaseDuplicate(currentRecord, skipDuplicates, errors);
            } else {
                // Registro único
                uniqueRecords.add(currentRecord);
            }
        }

        log.debug("Duplicados con BD detectados: {} únicos de {} analizados", uniqueRecords.size(), thirdsData.size());

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
                    .errorMessage(String.format("Número de identificación duplicado en archivo. Primera ocurrencia en fila %d.", 
                            existingRecord.getRowNumber()))
                    .errorType(ImportErrorDetail.ErrorType.DUPLICATE_ERROR)
                    .build());
        }
        // Si skipDuplicates = true, simplemente no se agrega a los registros únicos
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
                    .errorType(ImportErrorDetail.ErrorType.DUPLICATE_ERROR)
                    .build());
        }
        // Si skipDuplicates = true, simplemente no se agrega a los registros únicos
    }

    /**
     * Crea una clave única para identificar duplicados.
     * Considera número de identificación y entidad.
     */
    private String createDuplicateKey(ThirdExcelData record) {
        return record.getEntId() + "_" + record.getIdNumber();
    }


    /**
     * Obtiene estadísticas de duplicados para reportes.
     */
    public DuplicateStatistics getDuplicateStatistics(List<ThirdExcelData> thirdsData, String entId) {
        Map<String, List<ThirdExcelData>> duplicateGroups = new HashMap<>();

        // Agrupar por número de identificación
        for (ThirdExcelData record : thirdsData) {
            if (record.getIdNumber() != null) {
                String key = createDuplicateKey(record);
                duplicateGroups.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
            }
        }

        // Contar duplicados por tipo
        int internalDuplicates = 0;
        int potentialDbDuplicates = 0;

        for (List<ThirdExcelData> group : duplicateGroups.values()) {
            if (group.size() > 1) {
                internalDuplicates += group.size() - 1; // El primero no es duplicado
            }
            
            // Verificar si el primero del grupo ya existe en BD
            ThirdExcelData first = group.get(0);
            if (thirdOutputPort.existThirdById(first.getIdNumber(), entId)) {
                potentialDbDuplicates += group.size();
            }
        }

        return DuplicateStatistics.builder()
                .totalRecords(thirdsData.size())
                .internalDuplicates(internalDuplicates)
                .databaseDuplicates(potentialDbDuplicates)
                .uniqueRecords(thirdsData.size() - internalDuplicates)
                .duplicateGroups(duplicateGroups.size())
                .build();
    }

    /**
     * Clase que representa el resultado de análisis de duplicados.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DuplicateAnalysisResult {
        private List<ThirdExcelData> uniqueRecords;
        private List<ImportErrorDetail> errors;
    }

    /**
     * Clase que representa el resultado completo de detección de duplicados.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DuplicateDetectionResult {
        private List<ThirdExcelData> uniqueRecords;
        private List<ImportErrorDetail> errors;
        private int totalAnalyzed;
        private int uniqueCount;
        private int duplicateCount;
    }

    /**
     * Clase que representa estadísticas de duplicados.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DuplicateStatistics {
        private int totalRecords;
        private int internalDuplicates;
        private int databaseDuplicates;
        private int uniqueRecords;
        private int duplicateGroups;
    }
}
