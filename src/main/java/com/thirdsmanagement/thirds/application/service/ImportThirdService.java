package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.ImportThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdImportException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdsErrorCode;
import com.thirdsmanagement.thirds.domain.model.*;
import com.thirdsmanagement.thirds.domain.utils.StringNormalizer;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdImportRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ImportErrorDetail;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdImportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Servicio principal para la importación masiva de terceros desde Excel.
 * Orquesta todo el proceso: parseo, validación, detección de duplicados y persistencia.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImportThirdService implements ImportThirdUseCase {

    private static final String IMPORT_ID_PREFIX = "IMP";

    private final ExcelParsingService excelParsingService;
    private final BatchValidationService batchValidationService;
    private final DuplicateDetectionService duplicateDetectionService;
    private final CreateThirdService createThirdService;
    private final IdOutputPort idOutputPort;
    private final GeographyOutputPort geographyOutputPort;

    /**
     * Importa terceros masivamente desde un archivo Excel.
     */
    @Override
    public ThirdImportResponse importThirdsFromExcel(ThirdImportRequest importRequest) {
        String importId = generateImportId();
        LocalDateTime startTime = LocalDateTime.now();
        
        log.info("Iniciando importación {} para entidad {} - archivo: {}", 
                importId, importRequest.getEntId(), importRequest.getFileName());

        try {
            // 1. Parsear archivo Excel
            ExcelParsingService.ExcelParsingResult parsingResult = excelParsingService.parseExcelFile(
                    importRequest.getExcelFile(), importRequest.getEntId());

            if (parsingResult.getThirdsData().isEmpty()) {
                return createFailedResponse(importId, importRequest, startTime, 
                        "No se encontraron datos válidos para importar", parsingResult.getErrors());
            }

            // 2. Validaciones en lotes
            BatchValidationService.BatchValidationResult validationResult = batchValidationService.validateBatch(
                    parsingResult.getThirdsData(), importRequest.getEntId(), parsingResult.getColumnMap());

            // 3. Detección de duplicados - omitir duplicados automáticamente para usabilidad
            DuplicateDetectionService.DuplicateDetectionResult duplicateResult = duplicateDetectionService.detectDuplicates(
                    validationResult.getValidRecords(), importRequest.getEntId(), 
                    true); // skipDuplicates = true (omitir duplicados existentes)

            // Consolidar errores
            List<ImportErrorDetail> allErrors = new ArrayList<>();
            allErrors.addAll(parsingResult.getErrors());
            allErrors.addAll(validationResult.getErrors());
            allErrors.addAll(duplicateResult.getErrors());


            // 5. Procesar importación en lotes si hay registros válidos
            ImportProcessingResult processingResult = new ImportProcessingResult();
            if (!duplicateResult.getUniqueRecords().isEmpty()) {
                processingResult = processImportInBatches(duplicateResult.getUniqueRecords(), 
                        importRequest, allErrors);
            }

            // 4. Calcular duplicados omitidos (detección previa + procesamiento)
            int duplicatesFromDetection = validationResult.getValidCount() - duplicateResult.getUniqueCount();
            int duplicatesFromProcessing = processingResult.getSkippedCount(); // Esto incluye los duplicados detectados durante el procesamiento
            int duplicatesSkipped = duplicatesFromDetection + duplicatesFromProcessing;
            
            // 5. Generar respuesta final
            return createSuccessResponse(importId, importRequest, startTime, 
                    parsingResult.getTotalRows(), processingResult, allErrors, duplicatesSkipped, duplicatesFromProcessing);

        } catch (Exception e) {
            log.error("Error durante importación {}: {}", importId, e.getMessage(), e);
            
            List<ImportErrorDetail> systemErrors = List.of(
                ImportErrorDetail.builder()
                        .errorCode("SYSTEM_ERROR")
                        .errorMessage("Error del sistema durante la importación: " + e.getMessage())
                        .errorType(ImportErrorDetail.ErrorType.SYSTEM_ERROR)
                        .build()
            );
            
            return createFailedResponse(importId, importRequest, startTime, e.getMessage(), systemErrors);
        }
    }


    /**
     * Procesa la importación en lotes para optimizar rendimiento.
     */
    private ImportProcessingResult processImportInBatches(List<ThirdExcelData> uniqueRecords, 
                                                        ThirdImportRequest importRequest,
                                                        List<ImportErrorDetail> errors) {
        
        // Usar un batch size grande para archivos masivos sin límite específico
        int batchSize = Math.min(uniqueRecords.size(), 1000); // Máximo 1000 por lote
        List<List<ThirdExcelData>> batches = createBatches(uniqueRecords, batchSize);
        
        log.info("Procesando {} registros en {} lotes de tamaño {}", 
                uniqueRecords.size(), batches.size(), batchSize);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger skippedCount = new AtomicInteger(0);

        // Procesar lotes secuencialmente para mantener consistencia transaccional
        for (int i = 0; i < batches.size(); i++) {
            List<ThirdExcelData> batch = batches.get(i);
            log.debug("Procesando lote {}/{} con {} registros", i + 1, batches.size(), batch.size());

            try {
                // Procesar lote sin continuar en errores
                BatchProcessingResult batchResult = processBatch(batch, importRequest.getEntId(), 
                        false); // detenerse en errores
                
                successCount.addAndGet(batchResult.getSuccessCount());
                failureCount.addAndGet(batchResult.getFailureCount());
                skippedCount.addAndGet(batchResult.getSkippedCount());
                
                errors.addAll(batchResult.getErrors());
                
                // Si hay errores en el lote, detener la importación completa
                if (batchResult.getFailureCount() > 0) {
                    log.error("Errores encontrados en lote {}/{}. Deteniendo importación.", i + 1, batches.size());
                    break;
                }
                
                log.debug("Lote {}/{} completado - Éxitos: {}, Fallos: {}, Omitidos: {}", 
                        i + 1, batches.size(), batchResult.getSuccessCount(), 
                        batchResult.getFailureCount(), batchResult.getSkippedCount());

            } catch (Exception e) {
                log.error("Error crítico en lote {}/{}: {}", i + 1, batches.size(), e.getMessage(), e);
                throw new ThirdImportException(ThirdsErrorCode.THIRD_EXPORT_ERROR, 
                        "Error crítico en lote " + (i + 1) + ": " + e.getMessage(), e);
            }
        }

        return ImportProcessingResult.builder()
                .successCount(successCount.get())
                .failureCount(failureCount.get())
                .skippedCount(skippedCount.get())
                .build();
    }

    /**
     * Procesa un lote individual de registros.
     */
    private BatchProcessingResult processBatch(List<ThirdExcelData> batch, String entId, boolean continueOnError) {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger skippedCount = new AtomicInteger(0);
        
        List<ImportErrorDetail> batchErrors = new ArrayList<>();

        for (ThirdExcelData excelData : batch) {
            try {
                // Procesar cada registro en su propia transacción para evitar rollback-only
                ProcessingResult result = processSingleRecord(excelData, entId);
                
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
                            batchErrors.add(createProcessingError(excelData, result.getErrorMessage()));
                        } else {
                            throw new RuntimeException(result.getErrorMessage());
                        }
                        break;
                    case SKIPPED:
                        skippedCount.incrementAndGet();
                        batchErrors.add(createProcessingError(excelData, result.getErrorMessage()));
                        break;
                }
                
            } catch (Exception e) {
                if (continueOnError) {
                    failureCount.incrementAndGet();
                    batchErrors.add(createProcessingError(excelData, e.getMessage()));
                } else {
                    throw e;
                }
            }
        }

        return BatchProcessingResult.builder()
                .successCount(successCount.get())
                .failureCount(failureCount.get())
                .skippedCount(skippedCount.get())
                .errors(batchErrors)
                .build();
    }

    /**
     * Procesa un único registro manejando excepciones de duplicados.
     */
    private ProcessingResult processSingleRecord(ThirdExcelData excelData, String entId) {
        try {
            Third convertedThird = convertExcelDataToThird(excelData);
            
            if (convertedThird == null) {
                return ProcessingResult.skipped("Registro omitido por datos incompletos");
            }
            
            // Crear en nueva transacción independiente
            Third createdThird = createThirdInNewTransaction(convertedThird, excelData);
            
            if (createdThird != null) {
                return ProcessingResult.success();
            } else {
                // createThirdInNewTransaction retornó null = duplicado detectado
                return ProcessingResult.duplicateSkipped();
            }
            
        } catch (Exception e) {
            // Otros errores no manejados
            return ProcessingResult.failed(e.getMessage());
        }
    }

    /**
     * Crea un tercero en una nueva transacción independiente.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private Third createThirdInNewTransaction(Third third, ThirdExcelData excelData) {
        try {
            // Resolver geografía
            Object[] geography = resolveGeography(excelData);
            String countryCode = geography[0] != null ? ((Country) geography[0]).getCountryCode() : null;
            String stateCode = geography[1] != null ? ((State) geography[1]).getStateCode() : null;
            String cityCode = geography[2] != null ? ((City) geography[2]).getCityCode() : null;
            
            // Usar el servicio existente de creación
            return createThirdService.createThird(third, countryCode, stateCode, cityCode);
            
        } catch (Exception e) {
            // Si es duplicado, retornar null para manejarlo en el método padre
            if (isDuplicateError(e)) {
                return null; // Señal de duplicado
            }
            // Re-lanzar otros errores
            throw e;
        }
    }

    /**
     * Convierte datos de Excel a entidad Third del dominio.
     */
    private Third convertExcelDataToThird(ThirdExcelData excelData) {
        if (!excelData.hasRequiredFields()) {
            return null;
        }

        // Obtener TypeId
        TypeId typeId = getTypeIdByName(excelData.getTypeIdName(), excelData.getEntId());
        if (typeId == null) {
            return null;
        }

        // Obtener ThirdTypes
        Set<ThirdType> thirdTypes = getThirdTypesByNames(excelData.getThirdTypesNames(), excelData.getEntId());

        return Third.builder()
                .entId(excelData.getEntId())
                .typeId(typeId)
                .thirdTypes(thirdTypes)
                .personType(excelData.getPersonType())
                .names(excelData.getNames() != null ? StringNormalizer.normalizePreservingCase(excelData.getNames()) : null)
                .lastNames(excelData.getLastNames() != null ? StringNormalizer.normalizePreservingCase(excelData.getLastNames()) : null)
                .socialReason(excelData.getSocialReason() != null ? StringNormalizer.normalizePreservingCase(excelData.getSocialReason()) : null)
                .gender(excelData.getGender())
                .idNumber(excelData.getIdNumber())
                .verificationNumber(excelData.getVerificationNumber())
                .state(excelData.getState() != null ? excelData.getState() : true)
                .address(excelData.getAddress())
                .phoneNumber(excelData.getPhoneNumber())
                .email(excelData.getEmail())
                .build();
    }

    /**
     * Resuelve la geografía desde los nombres en Excel.
     */
    private Object[] resolveGeography(ThirdExcelData excelData) {
        Country country = null;
        State state = null;
        City city = null;

        // Resolver país
        if (excelData.getCountryName() != null && !excelData.getCountryName().trim().isEmpty()) {
            country = geographyOutputPort.getAllActiveCountries().stream()
                    .filter(c -> c.getCountryName().equalsIgnoreCase(excelData.getCountryName().trim()))
                    .findFirst()
                    .orElse(null);
        }

        // Resolver estado (solo para Colombia por ahora)
        if (country != null && "COL".equals(country.getCountryCode()) && 
            excelData.getStateName() != null && !excelData.getStateName().trim().isEmpty()) {
            
            state = geographyOutputPort.getStatesByCountry("COL").stream()
                    .filter(s -> s.getStateName().equalsIgnoreCase(excelData.getStateName().trim()))
                    .findFirst()
                    .orElse(null);
        }

        // Resolver ciudad
        if (state != null && excelData.getCityName() != null && !excelData.getCityName().trim().isEmpty()) {
            city = geographyOutputPort.getCitiesByState(state.getStateCode(), "COL").stream()
                    .filter(c -> c.getCityName().equalsIgnoreCase(excelData.getCityName().trim()))
                    .findFirst()
                    .orElse(null);
        }

        return new Object[]{country, state, city};
    }

    /**
     * Obtiene TypeId por nombre.
     */
    private TypeId getTypeIdByName(String typeIdName, String entId) {
        if (typeIdName == null || typeIdName.trim().isEmpty()) {
            return null;
        }

        return idOutputPort.getAllTypeIds(entId).stream()
                .filter(typeId -> typeId.getStatus() != null && typeId.getStatus())
                .filter(typeId -> typeId.getTypeIdname().equalsIgnoreCase(typeIdName.trim()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene ThirdTypes por nombres.
     */
    private Set<ThirdType> getThirdTypesByNames(Set<String> typeNames, String entId) {
        if (typeNames == null || typeNames.isEmpty()) {
            return new HashSet<>();
        }

        List<ThirdType> allTypes = idOutputPort.getALLThirdTypes(entId);
        
        return typeNames.stream()
                .map(name -> allTypes.stream()
                        .filter(type -> type.getStatus() != null && type.getStatus())
                        .filter(type -> type.getThirdTypeName().equalsIgnoreCase(name.trim()))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Divide una lista en lotes del tamaño especificado.
     */
    private <T> List<List<T>> createBatches(List<T> list, int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return batches;
    }

    /**
     * Genera un ID único para la importación.
     */
    private String generateImportId() {
        return IMPORT_ID_PREFIX + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Crea una respuesta de error para importación fallida.
     */
    private ThirdImportResponse createFailedResponse(String importId, ThirdImportRequest request, 
                                                   LocalDateTime startTime, String errorMessage, 
                                                   List<ImportErrorDetail> errors) {
        LocalDateTime endTime = LocalDateTime.now();
        
        return ThirdImportResponse.builder()
                .importId(importId)
                .entId(request.getEntId())
                .fileName(request.getFileName())
                .startTime(startTime)
                .endTime(endTime)
                .durationMs(java.time.Duration.between(startTime, endTime).toMillis())
                .status(ThirdImportResponse.ImportStatus.FAILED)
                .totalRecords(0)
                .successfulImports(0)
                .failedImports(0)
                .skippedRecords(0)
                .duplicatesSkipped(0)
                .errors(errors)
                .build();
    }


    /**
     * Crea una respuesta de éxito para importación completada.
     */
    private ThirdImportResponse createSuccessResponse(String importId, ThirdImportRequest request, 
                                                    LocalDateTime startTime, int totalRecords,
                                                    ImportProcessingResult processingResult,
                                                    List<ImportErrorDetail> errors, int duplicatesSkipped, int duplicatesFromProcessing) {
        LocalDateTime endTime = LocalDateTime.now();
        
        // Determinar estado simple basado en éxitos y fallos
        ThirdImportResponse.ImportStatus status;
        if (processingResult.getFailureCount() == 0 && errors.isEmpty()) {
            status = ThirdImportResponse.ImportStatus.COMPLETED;
        } else if (processingResult.getSuccessCount() > 0) {
            status = ThirdImportResponse.ImportStatus.COMPLETED_WITH_ERRORS;
        } else {
            status = ThirdImportResponse.ImportStatus.FAILED;
        }

        return ThirdImportResponse.builder()
                .importId(importId)
                .entId(request.getEntId())
                .fileName(request.getFileName())
                .startTime(startTime)
                .endTime(endTime)
                .durationMs(java.time.Duration.between(startTime, endTime).toMillis())
                .status(status)
                .totalRecords(totalRecords)
                .successfulImports(processingResult.getSuccessCount())
                .failedImports(processingResult.getFailureCount())
                .skippedRecords(processingResult.getSkippedCount() - duplicatesFromProcessing) // Solo omitidos por otros motivos (no duplicados)
                .duplicatesSkipped(duplicatesSkipped)
                .errors(errors)
                .build();
    }

    /**
     * Crea un error de procesamiento.
     */
    private ImportErrorDetail createProcessingError(ThirdExcelData excelData, String message) {
        return ImportErrorDetail.builder()
                .rowNumber(excelData.getRowNumber())
                .errorCode("PROCESSING_ERROR")
                .errorMessage(message)
                .errorType(ImportErrorDetail.ErrorType.SYSTEM_ERROR)
                .build();
    }

    /**
     * Verifica si la excepción es un error de duplicado.
     */
    private boolean isDuplicateError(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            return false;
        }
        
        // Verificar patrones comunes de errores de duplicado
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("ya existe") || 
               lowerMessage.contains("duplicate") || 
               lowerMessage.contains("duplicado") ||
               lowerMessage.contains("unique constraint") ||
               lowerMessage.contains("número de identificación") && lowerMessage.contains("existe");
    }

    /**
     * Clase para encapsular el resultado del procesamiento de importación.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class ImportProcessingResult {
        private int successCount;
        private int failureCount;
        private int skippedCount;
    }

    /**
     * Clase para encapsular el resultado del procesamiento de un lote.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class BatchProcessingResult {
        private int successCount;
        private int failureCount;
        private int skippedCount;
        private List<ImportErrorDetail> errors;
    }

    /**
     * Resultado del procesamiento de un registro individual.
     */
    @lombok.Data
    @lombok.AllArgsConstructor
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
     * Estados posibles del procesamiento de un registro.
     */
    private enum ProcessingStatus {
        SUCCESS,
        DUPLICATE_SKIPPED,
        FAILED,
        SKIPPED
    }
}
