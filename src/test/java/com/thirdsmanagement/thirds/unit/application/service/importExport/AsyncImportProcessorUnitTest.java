package com.thirdsmanagement.thirds.unit.application.service.importExport;

import com.thirdsmanagement.thirds.application.service.importExport.AsyncImportProcessor;
import com.thirdsmanagement.thirds.application.service.third.BatchProcessor;
import com.thirdsmanagement.thirds.application.service.importExport.BatchValidationService;
import com.thirdsmanagement.thirds.application.service.importExport.DuplicateDetectionService;
import com.thirdsmanagement.thirds.application.service.importExport.ExcelParsingService;
import com.thirdsmanagement.thirds.application.service.importExport.ImportJobTracker;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;
import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import com.thirdsmanagement.thirds.domain.model.ImportErrorDetail;
import com.thirdsmanagement.thirds.domain.model.ThirdExcelData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios completos para AsyncImportProcessor
 * Cubre todas las combinatorias posibles de escenarios de importación asíncrona
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AsyncImportProcessorUnitTest {

    @Mock
    private ExcelParsingService excelParsingService;

    @Mock
    private BatchValidationService batchValidationService;

    @Mock
    private DuplicateDetectionService duplicateDetectionService;

    @Mock
    private BatchProcessor batchProcessor;

    @Mock
    private ImportJobTracker jobTracker;

    private AsyncImportProcessor asyncImportProcessor;

    private byte[] fileBytes;
    private String entId;
    private String fileName;
    private String jobId;
    private List<ThirdExcelData> sampleThirdsData;
    private Map<String, Integer> columnMap;

    @BeforeEach
    void setUp() {
        asyncImportProcessor = new AsyncImportProcessor(
            excelParsingService, batchValidationService, duplicateDetectionService,
            batchProcessor, jobTracker
        );

        fileBytes = "dummy file content".getBytes();
        entId = "TEST_ENT";
        fileName = "test_import.xlsx";
        jobId = "test-job-123";

        // Crear datos de prueba
        sampleThirdsData = Arrays.asList(
            ThirdExcelData.builder()
                .entId(entId)
                .names("Juan")
                .lastNames("Pérez")
                .personType(ePersonType.Natural)
                .build(),
            ThirdExcelData.builder()
                .entId(entId)
                .socialReason("Empresa S.A.")
                .personType(ePersonType.Juridica)
                .build()
        );

        columnMap = new HashMap<>();
        columnMap.put("Nombres", 0);
        columnMap.put("Apellidos", 1);
        columnMap.put("Tipo Persona", 2);
    }

    // ===============================
    // ESCENARIOS DE ÉXITO COMPLETO
    // ===============================

    @Test
    @DisplayName("Debe procesar importación exitosamente sin errores ni duplicados")
    void testProcessImportAsync_SuccessComplete() throws Exception {
        // Arrange
        ExcelParsingService.ExcelParsingResult parsingResult = mock(ExcelParsingService.ExcelParsingResult.class);
        when(parsingResult.getThirdsData()).thenReturn(sampleThirdsData);
        when(parsingResult.getErrors()).thenReturn(Collections.emptyList());
        when(parsingResult.getColumnMap()).thenReturn(columnMap);
        when(parsingResult.getTotalRows()).thenReturn(2);

        BatchValidationService.BatchValidationResult validationResult = mock(BatchValidationService.BatchValidationResult.class);
        when(validationResult.getValidRecords()).thenReturn(sampleThirdsData);
        when(validationResult.getErrors()).thenReturn(Collections.emptyList());
        when(validationResult.getValidCount()).thenReturn(2);

        DuplicateDetectionService.DuplicateDetectionResult duplicateResult = mock(DuplicateDetectionService.DuplicateDetectionResult.class);
        when(duplicateResult.getUniqueRecords()).thenReturn(sampleThirdsData);
        when(duplicateResult.getErrors()).thenReturn(Collections.emptyList());
        when(duplicateResult.getUniqueCount()).thenReturn(2);

        BatchProcessor.BatchProcessingResult batchResult = mock(BatchProcessor.BatchProcessingResult.class);
        when(batchResult.getSuccessCount()).thenReturn(2);
        when(batchResult.getFailureCount()).thenReturn(0);
        when(batchResult.getSkippedCount()).thenReturn(0);

        BatchValidationService.ReferenceDataCache cache = mock(BatchValidationService.ReferenceDataCache.class);

        when(excelParsingService.parseExcelFileFromBytes(fileBytes, entId)).thenReturn(parsingResult);
        when(batchValidationService.validateBatch(sampleThirdsData, entId, columnMap)).thenReturn(validationResult);
        when(batchValidationService.preloadReferenceData(entId)).thenReturn(cache);
        when(duplicateDetectionService.detectDuplicates(sampleThirdsData, entId, true)).thenReturn(duplicateResult);
        when(batchProcessor.processBatch(any(), any(), eq(true))).thenReturn(batchResult);

        // Act
        asyncImportProcessor.processImportAsync(fileBytes, entId, fileName, jobId);

        // Assert
        verify(jobTracker, timeout(2000)).updateJobStatus(jobId, ImportStatus.PROCESSING);
        verify(jobTracker, timeout(2000)).updateProgress(jobId, 10);
        verify(jobTracker, timeout(2000)).updateProgress(jobId, 20);
        verify(jobTracker, timeout(2000)).updateProgress(jobId, 40);
        verify(jobTracker, timeout(2000)).updateProgress(jobId, 60);
        verify(jobTracker, timeout(2000).times(2)).updateProgress(jobId, 90);
        verify(jobTracker, timeout(2000)).updateProgress(jobId, 100);
        verify(jobTracker, timeout(2000)).updateJobMetrics(jobId, 2, 2, 0, 0);
        verify(jobTracker, timeout(2000)).updateJobStatus(jobId, ImportStatus.COMPLETED);
        verify(batchValidationService, timeout(2000)).preloadReferenceData(entId);
    }

    @Test
    @DisplayName("Debe procesar importación con algunos errores (COMPLETED_WITH_ERRORS)")
    void testProcessImportAsync_SuccessWithErrors() throws Exception {
        // Arrange
        ExcelParsingService.ExcelParsingResult parsingResult = mock(ExcelParsingService.ExcelParsingResult.class);
        when(parsingResult.getThirdsData()).thenReturn(sampleThirdsData);
        when(parsingResult.getErrors()).thenReturn(Collections.emptyList());
        when(parsingResult.getColumnMap()).thenReturn(columnMap);
        when(parsingResult.getTotalRows()).thenReturn(3);

        BatchValidationService.BatchValidationResult validationResult = mock(BatchValidationService.BatchValidationResult.class);
        when(validationResult.getValidRecords()).thenReturn(sampleThirdsData);
        when(validationResult.getErrors()).thenReturn(Arrays.asList(createMockError(3, "Error de validación")));
        when(validationResult.getValidCount()).thenReturn(2);

        DuplicateDetectionService.DuplicateDetectionResult duplicateResult = mock(DuplicateDetectionService.DuplicateDetectionResult.class);
        when(duplicateResult.getUniqueRecords()).thenReturn(Arrays.asList(sampleThirdsData.get(0)));
        when(duplicateResult.getErrors()).thenReturn(Collections.emptyList());
        when(duplicateResult.getUniqueCount()).thenReturn(1);

        BatchProcessor.BatchProcessingResult batchResult = mock(BatchProcessor.BatchProcessingResult.class);
        when(batchResult.getSuccessCount()).thenReturn(1);
        when(batchResult.getFailureCount()).thenReturn(0);
        when(batchResult.getSkippedCount()).thenReturn(0);

        BatchValidationService.ReferenceDataCache cache = mock(BatchValidationService.ReferenceDataCache.class);

        when(excelParsingService.parseExcelFileFromBytes(fileBytes, entId)).thenReturn(parsingResult);
        when(batchValidationService.validateBatch(sampleThirdsData, entId, columnMap)).thenReturn(validationResult);
        when(batchValidationService.preloadReferenceData(entId)).thenReturn(cache);
        when(duplicateDetectionService.detectDuplicates(sampleThirdsData, entId, true)).thenReturn(duplicateResult);
        when(batchProcessor.processBatch(any(), any(), eq(true))).thenReturn(batchResult);

        // Act
        asyncImportProcessor.processImportAsync(fileBytes, entId, fileName, jobId);
        

        // Assert
        verify(jobTracker, timeout(2000)).updateJobStatus(jobId, ImportStatus.COMPLETED_WITH_ERRORS);
        verify(jobTracker, timeout(2000)).updateJobMetrics(jobId, 3, 1, 1, 1);
        verify(batchValidationService, timeout(2000)).preloadReferenceData(entId);
    }

    @Test
    @DisplayName("Debe procesar importación con duplicados detectados")
    void testProcessImportAsync_WithDuplicates() throws Exception {
        // Arrange
        ExcelParsingService.ExcelParsingResult parsingResult = mock(ExcelParsingService.ExcelParsingResult.class);
        when(parsingResult.getThirdsData()).thenReturn(sampleThirdsData);
        when(parsingResult.getErrors()).thenReturn(Collections.emptyList());
        when(parsingResult.getColumnMap()).thenReturn(columnMap);
        when(parsingResult.getTotalRows()).thenReturn(2);

        BatchValidationService.BatchValidationResult validationResult = mock(BatchValidationService.BatchValidationResult.class);
        when(validationResult.getValidRecords()).thenReturn(sampleThirdsData);
        when(validationResult.getErrors()).thenReturn(Collections.emptyList());
        when(validationResult.getValidCount()).thenReturn(2);

        DuplicateDetectionService.DuplicateDetectionResult duplicateResult = mock(DuplicateDetectionService.DuplicateDetectionResult.class);
        when(duplicateResult.getUniqueRecords()).thenReturn(Arrays.asList(sampleThirdsData.get(0))); // Solo 1 único
        when(duplicateResult.getErrors()).thenReturn(Collections.emptyList());
        when(duplicateResult.getUniqueCount()).thenReturn(1);

        BatchProcessor.BatchProcessingResult batchResult = mock(BatchProcessor.BatchProcessingResult.class);
        when(batchResult.getSuccessCount()).thenReturn(1);
        when(batchResult.getFailureCount()).thenReturn(0);
        when(batchResult.getSkippedCount()).thenReturn(0);

        when(excelParsingService.parseExcelFileFromBytes(fileBytes, entId)).thenReturn(parsingResult);
        when(batchValidationService.validateBatch(sampleThirdsData, entId, columnMap)).thenReturn(validationResult);
        when(duplicateDetectionService.detectDuplicates(sampleThirdsData, entId, true)).thenReturn(duplicateResult);
        when(batchProcessor.processBatch(any(), any(), eq(true))).thenReturn(batchResult);

        // Act
        asyncImportProcessor.processImportAsync(fileBytes, entId, fileName, jobId);
        

        // Assert
        verify(jobTracker, timeout(2000)).updateJobMetrics(jobId, 2, 1, 0, 1); // 1 duplicado omitido
        verify(jobTracker, timeout(2000)).updateJobStatus(jobId, ImportStatus.COMPLETED);
    }

    // ===============================
    // ESCENARIOS SIN DATOS
    // ===============================

    @Test
    @DisplayName("Debe manejar importación sin datos válidos")
    void testProcessImportAsync_NoValidData() throws Exception {
        // Arrange
        ExcelParsingService.ExcelParsingResult parsingResult = mock(ExcelParsingService.ExcelParsingResult.class);
        when(parsingResult.getThirdsData()).thenReturn(Collections.emptyList());
        when(parsingResult.getErrors()).thenReturn(Arrays.asList(createMockError(1, "No data found")));
        when(parsingResult.getColumnMap()).thenReturn(columnMap);
        when(parsingResult.getTotalRows()).thenReturn(0);

        when(excelParsingService.parseExcelFileFromBytes(fileBytes, entId)).thenReturn(parsingResult);

        // Act
        asyncImportProcessor.processImportAsync(fileBytes, entId, fileName, jobId);
        

        // Assert
        verify(jobTracker, timeout(2000)).updateJobStatus(jobId, ImportStatus.FAILED);
        verify(jobTracker, timeout(2000)).updateJobMetrics(jobId, 0, 0, 0, 0);
        verify(jobTracker, timeout(2000)).addErrors(eq(jobId), anyList());
        verify(jobTracker, never()).updateJobStatus(jobId, ImportStatus.COMPLETED);
    }

    @Test
    @DisplayName("Debe manejar importación completamente fallida")
    void testProcessImportAsync_CompleteFailure() throws Exception {
        // Arrange
        ExcelParsingService.ExcelParsingResult parsingResult = mock(ExcelParsingService.ExcelParsingResult.class);
        when(parsingResult.getThirdsData()).thenReturn(sampleThirdsData);
        when(parsingResult.getErrors()).thenReturn(Collections.emptyList());
        when(parsingResult.getColumnMap()).thenReturn(columnMap);
        when(parsingResult.getTotalRows()).thenReturn(2);

        BatchValidationService.BatchValidationResult validationResult = mock(BatchValidationService.BatchValidationResult.class);
        when(validationResult.getValidRecords()).thenReturn(sampleThirdsData);
        when(validationResult.getErrors()).thenReturn(Collections.emptyList());
        when(validationResult.getValidCount()).thenReturn(2);

        DuplicateDetectionService.DuplicateDetectionResult duplicateResult = mock(DuplicateDetectionService.DuplicateDetectionResult.class);
        when(duplicateResult.getUniqueRecords()).thenReturn(sampleThirdsData);
        when(duplicateResult.getErrors()).thenReturn(Collections.emptyList());
        when(duplicateResult.getUniqueCount()).thenReturn(2);

        BatchProcessor.BatchProcessingResult batchResult = mock(BatchProcessor.BatchProcessingResult.class);
        when(batchResult.getSuccessCount()).thenReturn(0);
        when(batchResult.getFailureCount()).thenReturn(2);
        when(batchResult.getSkippedCount()).thenReturn(0);

        when(excelParsingService.parseExcelFileFromBytes(fileBytes, entId)).thenReturn(parsingResult);
        when(batchValidationService.validateBatch(sampleThirdsData, entId, columnMap)).thenReturn(validationResult);
        when(duplicateDetectionService.detectDuplicates(sampleThirdsData, entId, true)).thenReturn(duplicateResult);
        when(batchProcessor.processBatch(any(), any(), eq(true))).thenReturn(batchResult);

        // Act
        asyncImportProcessor.processImportAsync(fileBytes, entId, fileName, jobId);
        

        // Assert
        verify(jobTracker, timeout(2000)).updateJobStatus(jobId, ImportStatus.FAILED);
        verify(jobTracker, timeout(2000)).updateJobMetrics(jobId, 2, 0, 2, 0);
    }

    // ===============================
    // ESCENARIOS DE ERROR
    // ===============================

    @Test
    @DisplayName("Debe manejar error en parsing de Excel")
    void testProcessImportAsync_ParsingError() throws Exception {
        // Arrange
        RuntimeException parsingError = new RuntimeException("Error parsing Excel file");

        when(excelParsingService.parseExcelFileFromBytes(fileBytes, entId))
            .thenThrow(parsingError);

        // Act
        asyncImportProcessor.processImportAsync(fileBytes, entId, fileName, jobId);
        

        // Assert
        verify(jobTracker, timeout(2000)).updateJobStatus(jobId, ImportStatus.PROCESSING);
        verify(jobTracker, timeout(2000)).updateProgress(jobId, 10);
        verify(jobTracker, timeout(2000)).addErrors(eq(jobId), anyList());
        verify(jobTracker, timeout(2000)).updateJobStatus(jobId, ImportStatus.FAILED);
    }

    @Test
    @DisplayName("Debe manejar error en validación por lotes")
    void testProcessImportAsync_ValidationError() throws Exception {
        // Arrange
        ExcelParsingService.ExcelParsingResult parsingResult = mock(ExcelParsingService.ExcelParsingResult.class);
        when(parsingResult.getThirdsData()).thenReturn(sampleThirdsData);
        when(parsingResult.getErrors()).thenReturn(Collections.emptyList());
        when(parsingResult.getColumnMap()).thenReturn(columnMap);
        when(parsingResult.getTotalRows()).thenReturn(2);

        RuntimeException validationError = new RuntimeException("Error en validación por lotes");

        when(excelParsingService.parseExcelFileFromBytes(fileBytes, entId)).thenReturn(parsingResult);
        when(batchValidationService.validateBatch(sampleThirdsData, entId, columnMap))
            .thenThrow(validationError);

        // Act
        asyncImportProcessor.processImportAsync(fileBytes, entId, fileName, jobId);
        

        // Assert
        verify(jobTracker, timeout(2000)).updateJobStatus(jobId, ImportStatus.FAILED);
        verify(jobTracker, timeout(2000)).addErrors(eq(jobId), anyList());
    }

    @Test
    @DisplayName("Debe manejar error en detección de duplicados")
    void testProcessImportAsync_DuplicateDetectionError() throws Exception {
        // Arrange
        ExcelParsingService.ExcelParsingResult parsingResult = mock(ExcelParsingService.ExcelParsingResult.class);
        when(parsingResult.getThirdsData()).thenReturn(sampleThirdsData);
        when(parsingResult.getErrors()).thenReturn(Collections.emptyList());
        when(parsingResult.getColumnMap()).thenReturn(columnMap);
        when(parsingResult.getTotalRows()).thenReturn(2);

        BatchValidationService.BatchValidationResult validationResult = mock(BatchValidationService.BatchValidationResult.class);
        when(validationResult.getValidRecords()).thenReturn(sampleThirdsData);
        when(validationResult.getErrors()).thenReturn(Collections.emptyList());
        when(validationResult.getValidCount()).thenReturn(2);

        RuntimeException duplicateError = new RuntimeException("Error en detección de duplicados");

        when(excelParsingService.parseExcelFileFromBytes(fileBytes, entId)).thenReturn(parsingResult);
        when(batchValidationService.validateBatch(sampleThirdsData, entId, columnMap)).thenReturn(validationResult);
        when(duplicateDetectionService.detectDuplicates(sampleThirdsData, entId, true))
            .thenThrow(duplicateError);

        // Act
        asyncImportProcessor.processImportAsync(fileBytes, entId, fileName, jobId);
        

        // Assert
        verify(jobTracker, timeout(2000)).updateJobStatus(jobId, ImportStatus.FAILED);
        verify(jobTracker, timeout(2000)).addErrors(eq(jobId), anyList());
    }

    @Test
    @DisplayName("Debe manejar error en procesamiento por lotes")
    void testProcessImportAsync_BatchProcessingError() throws Exception {
        // Arrange
        ExcelParsingService.ExcelParsingResult parsingResult = mock(ExcelParsingService.ExcelParsingResult.class);
        when(parsingResult.getThirdsData()).thenReturn(sampleThirdsData);
        when(parsingResult.getErrors()).thenReturn(Collections.emptyList());
        when(parsingResult.getColumnMap()).thenReturn(columnMap);
        when(parsingResult.getTotalRows()).thenReturn(2);

        BatchValidationService.BatchValidationResult validationResult = mock(BatchValidationService.BatchValidationResult.class);
        when(validationResult.getValidRecords()).thenReturn(sampleThirdsData);
        when(validationResult.getErrors()).thenReturn(Collections.emptyList());
        when(validationResult.getValidCount()).thenReturn(2);

        DuplicateDetectionService.DuplicateDetectionResult duplicateResult = mock(DuplicateDetectionService.DuplicateDetectionResult.class);
        when(duplicateResult.getUniqueRecords()).thenReturn(sampleThirdsData);
        when(duplicateResult.getErrors()).thenReturn(Collections.emptyList());
        when(duplicateResult.getUniqueCount()).thenReturn(2);

        RuntimeException batchError = new RuntimeException("Error en procesamiento por lotes");

        when(excelParsingService.parseExcelFileFromBytes(fileBytes, entId)).thenReturn(parsingResult);
        when(batchValidationService.validateBatch(sampleThirdsData, entId, columnMap)).thenReturn(validationResult);
        when(duplicateDetectionService.detectDuplicates(sampleThirdsData, entId, true)).thenReturn(duplicateResult);
        when(batchProcessor.processBatch(any(), any(), eq(true))).thenThrow(batchError);

        // Act
        asyncImportProcessor.processImportAsync(fileBytes, entId, fileName, jobId);
        

        // Assert
        verify(jobTracker, timeout(2000)).updateJobStatus(jobId, ImportStatus.FAILED);
        verify(jobTracker, timeout(2000)).addErrors(eq(jobId), anyList());
    }

    @Test
    @DisplayName("Debe manejar error crítico del sistema")
    void testProcessImportAsync_CriticalSystemError() throws Exception {
        // Arrange
        RuntimeException criticalError = new RuntimeException("Error crítico del sistema");

        when(excelParsingService.parseExcelFileFromBytes(fileBytes, entId))
            .thenThrow(criticalError);

        // Act
        asyncImportProcessor.processImportAsync(fileBytes, entId, fileName, jobId);
        

        // Assert
        verify(jobTracker, timeout(2000)).updateJobStatus(jobId, ImportStatus.FAILED);
        verify(jobTracker, timeout(2000)).addErrors(eq(jobId), anyList());
    }

    // ===============================
    // ESCENARIOS DE PROCESAMIENTO POR LOTES
    // ===============================

    @Test
    @DisplayName("Debe procesar múltiples lotes correctamente")
    void testProcessImportAsync_MultipleBatches() throws Exception {
        // Arrange - Crear datos para múltiples lotes (más de 1000 registros)
        List<ThirdExcelData> largeDataSet = new ArrayList<>();
        for (int i = 0; i < 2500; i++) { // 3 lotes de ~833 registros cada uno
            largeDataSet.add(ThirdExcelData.builder()
                .entId(entId)
                .names("Name" + i)
                .lastNames("LastName" + i)
                .personType(ePersonType.Natural)
                .build());
        }

        ExcelParsingService.ExcelParsingResult parsingResult = mock(ExcelParsingService.ExcelParsingResult.class);
        when(parsingResult.getThirdsData()).thenReturn(largeDataSet);
        when(parsingResult.getErrors()).thenReturn(Collections.emptyList());
        when(parsingResult.getColumnMap()).thenReturn(columnMap);
        when(parsingResult.getTotalRows()).thenReturn(2500);

        BatchValidationService.BatchValidationResult validationResult = mock(BatchValidationService.BatchValidationResult.class);
        when(validationResult.getValidRecords()).thenReturn(largeDataSet);
        when(validationResult.getErrors()).thenReturn(Collections.emptyList());
        when(validationResult.getValidCount()).thenReturn(2500);

        DuplicateDetectionService.DuplicateDetectionResult duplicateResult = mock(DuplicateDetectionService.DuplicateDetectionResult.class);
        when(duplicateResult.getUniqueRecords()).thenReturn(largeDataSet);
        when(duplicateResult.getErrors()).thenReturn(Collections.emptyList());
        when(duplicateResult.getUniqueCount()).thenReturn(2500);

        BatchProcessor.BatchProcessingResult batchResult = mock(BatchProcessor.BatchProcessingResult.class);
        when(batchResult.getSuccessCount()).thenReturn(833); // Éxito por lote
        when(batchResult.getFailureCount()).thenReturn(0);
        when(batchResult.getSkippedCount()).thenReturn(0);

        when(excelParsingService.parseExcelFileFromBytes(fileBytes, entId)).thenReturn(parsingResult);
        when(batchValidationService.validateBatch(largeDataSet, entId, columnMap)).thenReturn(validationResult);
        when(duplicateDetectionService.detectDuplicates(largeDataSet, entId, true)).thenReturn(duplicateResult);
        when(batchProcessor.processBatch(any(), any(), eq(true))).thenReturn(batchResult);

        // Act
        asyncImportProcessor.processImportAsync(fileBytes, entId, fileName, jobId);
        

        // Assert - Debería procesar 3 lotes
        verify(batchProcessor, timeout(2000).times(3)).processBatch(any(), any(), eq(true));
        verify(jobTracker, timeout(2000)).updateJobMetrics(jobId, 2500, 2499, 0, 0); // 3 * 833 = 2499
        verify(jobTracker, timeout(2000)).updateJobStatus(jobId, ImportStatus.COMPLETED);
    }

    @Test
    @DisplayName("Debe manejar lote vacío correctamente")
    void testProcessImportAsync_EmptyBatch() throws Exception {
        // Arrange
        ExcelParsingService.ExcelParsingResult parsingResult = mock(ExcelParsingService.ExcelParsingResult.class);
        when(parsingResult.getThirdsData()).thenReturn(sampleThirdsData);
        when(parsingResult.getErrors()).thenReturn(Collections.emptyList());
        when(parsingResult.getColumnMap()).thenReturn(columnMap);
        when(parsingResult.getTotalRows()).thenReturn(2);

        BatchValidationService.BatchValidationResult validationResult = mock(BatchValidationService.BatchValidationResult.class);
        when(validationResult.getValidRecords()).thenReturn(sampleThirdsData);
        when(validationResult.getErrors()).thenReturn(Collections.emptyList());
        when(validationResult.getValidCount()).thenReturn(2);

        DuplicateDetectionService.DuplicateDetectionResult duplicateResult = mock(DuplicateDetectionService.DuplicateDetectionResult.class);
        when(duplicateResult.getUniqueRecords()).thenReturn(Collections.emptyList()); // Sin registros únicos
        when(duplicateResult.getErrors()).thenReturn(Collections.emptyList());
        when(duplicateResult.getUniqueCount()).thenReturn(0);

        when(excelParsingService.parseExcelFileFromBytes(fileBytes, entId)).thenReturn(parsingResult);
        when(batchValidationService.validateBatch(sampleThirdsData, entId, columnMap)).thenReturn(validationResult);
        when(duplicateDetectionService.detectDuplicates(sampleThirdsData, entId, true)).thenReturn(duplicateResult);

        // Act
        asyncImportProcessor.processImportAsync(fileBytes, entId, fileName, jobId);
        

        // Assert
        verify(batchProcessor, never()).processBatch(any(), any(), anyBoolean());
        verify(jobTracker, timeout(2000)).updateJobMetrics(jobId, 2, 0, 0, 2);
        verify(jobTracker, timeout(2000)).updateJobStatus(jobId, ImportStatus.FAILED);
    }

    // ===============================
    // ESCENARIOS DE VALIDACIÓN
    // ===============================

    @Test
    @DisplayName("Debe validar que se actualiza el progreso correctamente durante importación")
    void testProcessImportAsync_ProgressUpdates() throws Exception {
        // Arrange
        ExcelParsingService.ExcelParsingResult parsingResult = mock(ExcelParsingService.ExcelParsingResult.class);
        when(parsingResult.getThirdsData()).thenReturn(sampleThirdsData);
        when(parsingResult.getErrors()).thenReturn(Collections.emptyList());
        when(parsingResult.getColumnMap()).thenReturn(columnMap);
        when(parsingResult.getTotalRows()).thenReturn(2);

        BatchValidationService.BatchValidationResult validationResult = mock(BatchValidationService.BatchValidationResult.class);
        when(validationResult.getValidRecords()).thenReturn(sampleThirdsData);
        when(validationResult.getErrors()).thenReturn(Collections.emptyList());
        when(validationResult.getValidCount()).thenReturn(2);

        DuplicateDetectionService.DuplicateDetectionResult duplicateResult = mock(DuplicateDetectionService.DuplicateDetectionResult.class);
        when(duplicateResult.getUniqueRecords()).thenReturn(sampleThirdsData);
        when(duplicateResult.getErrors()).thenReturn(Collections.emptyList());
        when(duplicateResult.getUniqueCount()).thenReturn(2);

        BatchProcessor.BatchProcessingResult batchResult = mock(BatchProcessor.BatchProcessingResult.class);
        when(batchResult.getSuccessCount()).thenReturn(2);
        when(batchResult.getFailureCount()).thenReturn(0);
        when(batchResult.getSkippedCount()).thenReturn(0);

        when(excelParsingService.parseExcelFileFromBytes(fileBytes, entId)).thenReturn(parsingResult);
        when(batchValidationService.validateBatch(sampleThirdsData, entId, columnMap)).thenReturn(validationResult);
        when(duplicateDetectionService.detectDuplicates(sampleThirdsData, entId, true)).thenReturn(duplicateResult);
        when(batchProcessor.processBatch(any(), any(), eq(true))).thenReturn(batchResult);

        // Act
        asyncImportProcessor.processImportAsync(fileBytes, entId, fileName, jobId);
        

        // Assert - Verificar orden de actualizaciones de progreso
        verify(jobTracker, timeout(2000)).updateProgress(jobId, 10);
        verify(jobTracker, timeout(2000)).updateProgress(jobId, 20);
        verify(jobTracker, timeout(2000)).updateProgress(jobId, 40);
        verify(jobTracker, timeout(2000)).updateProgress(jobId, 60);
        verify(jobTracker, times(2)).updateProgress(jobId, 90); // Una del batch + una final
        verify(jobTracker, timeout(2000)).updateProgress(jobId, 100);
    }

    // ===============================
    // MÉTODOS DE UTILIDAD
    // ===============================

    private ImportErrorDetail createMockError(int rowNumber, String message) {
        return ImportErrorDetail.builder()
            .rowNumber(rowNumber)
            .errorCode("TEST_ERROR")
            .errorMessage(message)
            .errorType(ImportErrorType.VALIDATION_ERROR)
            .build();
    }
}
