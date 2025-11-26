package com.thirdsmanagement.thirds.unit.application.service.third;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.application.service.importExport.BatchValidationService;
import com.thirdsmanagement.thirds.application.service.importExport.DataConverter;
import com.thirdsmanagement.thirds.application.service.third.BatchProcessor;
import com.thirdsmanagement.thirds.application.service.third.CreateThirdService;
import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdExcelData;
import com.thirdsmanagement.thirds.domain.model.TypeId;

/**
 * Tests unitarios para BatchProcessor
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BatchProcessorUnitTest {

    @Mock
    private CreateThirdService createThirdService;

    @Mock
    private DataConverter dataConverter;

    @Mock
    private ThirdOutputPort thirdOutputPort;

    @InjectMocks
    private BatchProcessor batchProcessor;

    private BatchValidationService.ReferenceDataCache cache;
    private List<ThirdExcelData> batch;

    @BeforeEach
    void setUp() {
        cache = createMockCache();
        batch = new ArrayList<>();
    }

    // ========== processBatch Tests - Casos Exitosos ==========

    @Test
    @DisplayName("Debe procesar un lote con un registro exitoso")
    void testProcessBatch_WithSingleSuccessfulRecord() {
        // Arrange
        ThirdExcelData excelData = createThirdExcelData(1);
        batch.add(excelData);

        Third convertedThird = createThird();
        Third preparedThird = createThird();
        DataConverter.GeographyData geography = createGeographyData();

        when(dataConverter.convertWithCache(excelData, cache)).thenReturn(convertedThird);
        when(dataConverter.getGeographyData(excelData, cache)).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(convertedThird, "CO", "05", "05001")).thenReturn(preparedThird);
        when(thirdOutputPort.saveAllThirds(anyList())).thenReturn(Collections.singletonList(preparedThird));

        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, true);

        // Assert
        assertEquals(1, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getSkippedCount());
        assertTrue(result.getErrors().isEmpty());
        verify(thirdOutputPort).saveAllThirds(argThat(list -> list.size() == 1));
    }

    @Test
    @DisplayName("Debe procesar un lote con múltiples registros exitosos")
    void testProcessBatch_WithMultipleSuccessfulRecords() {
        // Arrange
        for (int i = 1; i <= 5; i++) {
            batch.add(createThirdExcelData(i));
        }

        Third convertedThird = createThird();
        Third preparedThird = createThird();
        DataConverter.GeographyData geography = createGeographyData();

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache))).thenReturn(convertedThird);
        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(any(Third.class), anyString(), anyString(), anyString())).thenReturn(preparedThird);
        when(thirdOutputPort.saveAllThirds(anyList())).thenReturn(Arrays.asList(preparedThird, preparedThird, preparedThird, preparedThird, preparedThird));

        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, true);

        // Assert
        assertEquals(5, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getSkippedCount());
        assertTrue(result.getErrors().isEmpty());
        verify(thirdOutputPort).saveAllThirds(argThat(list -> list.size() == 5));
    }

    @Test
    @DisplayName("Debe invocar saveAllThirds con la lista correcta de terceros")
    void testProcessBatch_InvokesSaveAllThirdsCorrectly() {
        // Arrange
        batch.add(createThirdExcelData(1));
        batch.add(createThirdExcelData(2));

        Third convertedThird = createThird();
        Third preparedThird = createThird();
        DataConverter.GeographyData geography = createGeographyData();

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache))).thenReturn(convertedThird);
        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(any(Third.class), anyString(), anyString(), anyString())).thenReturn(preparedThird);
        when(thirdOutputPort.saveAllThirds(anyList())).thenReturn(Arrays.asList(preparedThird, preparedThird));

        // Act
        batchProcessor.processBatch(batch, cache, true);

        // Assert
        verify(thirdOutputPort, times(1)).saveAllThirds(anyList());
        verify(createThirdService, times(2)).prepareThirdForBatchSave(any(Third.class), eq("CO"), eq("05"), eq("05001"));
    }

    // ========== processBatch Tests - Casos con Errores ==========

    @Test
    @DisplayName("Debe continuar procesando cuando continueOnError es true")
    void testProcessBatch_ContinuesOnError_WhenFlagIsTrue() {
        // Arrange
        batch.add(createThirdExcelData(1));
        batch.add(createThirdExcelData(2));
        batch.add(createThirdExcelData(3));

        Third convertedThird = createThird();
        Third preparedThird = createThird();
        DataConverter.GeographyData geography = createGeographyData();

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache)))
                .thenReturn(convertedThird)
                .thenThrow(new RuntimeException("Error en fila 2"))
                .thenReturn(convertedThird);
        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(any(Third.class), anyString(), anyString(), anyString())).thenReturn(preparedThird);
        when(thirdOutputPort.saveAllThirds(anyList())).thenReturn(Arrays.asList(preparedThird, preparedThird));

        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, true);

        // Assert
        assertEquals(2, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(1, result.getErrors().size());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando continueOnError es false y hay error")
    void testProcessBatch_ThrowsException_WhenFlagIsFalse() {
        // Arrange
        batch.add(createThirdExcelData(1));

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache)))
                .thenThrow(new RuntimeException("Error crítico"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            batchProcessor.processBatch(batch, cache, false);
        });

        verify(thirdOutputPort, never()).saveAllThirds(anyList());
    }

    @Test
    @DisplayName("Debe agregar errores a la lista cuando hay fallos")
    void testProcessBatch_AddsErrorsToList() {
        // Arrange
        batch.add(createThirdExcelData(1));
        batch.add(createThirdExcelData(2));

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache)))
                .thenThrow(new RuntimeException("Error en registro 1"))
                .thenThrow(new RuntimeException("Error en registro 2"));

        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, true);

        // Assert
        assertEquals(0, result.getSuccessCount());
        assertEquals(2, result.getFailureCount());
        assertEquals(2, result.getErrors().size());
        assertEquals(1, result.getErrors().get(0).getRowNumber());
        assertEquals(2, result.getErrors().get(1).getRowNumber());
    }

    @Test
    @DisplayName("Debe manejar error en saveAllThirds cuando continueOnError es true")
    void testProcessBatch_HandlesSaveAllThirdsError_WithContinueOnError() {
        // Arrange
        batch.add(createThirdExcelData(1));
        batch.add(createThirdExcelData(2));

        Third convertedThird = createThird();
        Third preparedThird = createThird();
        DataConverter.GeographyData geography = createGeographyData();

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache))).thenReturn(convertedThird);
        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(any(Third.class), anyString(), anyString(), anyString())).thenReturn(preparedThird);
        when(thirdOutputPort.saveAllThirds(anyList())).thenThrow(new RuntimeException("Error en base de datos"));

        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, true);

        // Assert
        assertEquals(0, result.getSuccessCount());
        assertEquals(2, result.getFailureCount());
        assertEquals(2, result.getErrors().size());
        assertTrue(result.getErrors().get(0).getErrorMessage().contains("Error al guardar lote"));
        assertEquals(ImportErrorType.SYSTEM_ERROR, result.getErrors().get(0).getErrorType());
    }

    @Test
    @DisplayName("Debe lanzar excepción en saveAllThirds cuando continueOnError es false")
    void testProcessBatch_ThrowsSaveAllThirdsError_WhenFlagIsFalse() {
        // Arrange
        batch.add(createThirdExcelData(1));

        Third convertedThird = createThird();
        Third preparedThird = createThird();
        DataConverter.GeographyData geography = createGeographyData();

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache))).thenReturn(convertedThird);
        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(any(Third.class), anyString(), anyString(), anyString())).thenReturn(preparedThird);
        when(thirdOutputPort.saveAllThirds(anyList())).thenThrow(new RuntimeException("Error crítico en BD"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            batchProcessor.processBatch(batch, cache, false);
        });
    }

    // ========== processBatch Tests - Casos de Registros Omitidos ==========

    @Test
    @DisplayName("Debe omitir registros cuando convertWithCache retorna null")
    void testProcessBatch_SkipsRecords_WhenConvertReturnsNull() {
        // Arrange
        batch.add(createThirdExcelData(1));
        batch.add(createThirdExcelData(2));

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache))).thenReturn(null);

        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, true);

        // Assert
        assertEquals(0, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(2, result.getSkippedCount());
        verify(thirdOutputPort, never()).saveAllThirds(anyList());
    }

    @Test
    @DisplayName("Debe contar correctamente registros omitidos por duplicados")
    void testProcessBatch_CountsDuplicatesCorrectly() {
        // Arrange
        batch.add(createThirdExcelData(1));
        batch.add(createThirdExcelData(2));

        DataConverter.GeographyData geography = createGeographyData();

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache))).thenReturn(createThird());
        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(any(Third.class), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("El tercero con ID 123456789 ya existe"));

        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, true);

        // Assert
        assertEquals(0, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(2, result.getSkippedCount());
    }

    @Test
    @DisplayName("Debe contar correctamente registros omitidos por errores geográficos")
    void testProcessBatch_CountsGeographyErrorsAsSkipped() {
        // Arrange
        batch.add(createThirdExcelData(1));

        DataConverter.GeographyData geography = createGeographyData();

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache))).thenReturn(createThird());
        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(any(Third.class), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("El código del país es obligatorio"));

        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, true);

        // Assert
        assertEquals(0, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(1, result.getSkippedCount());
        assertEquals(1, result.getErrors().size());
    }

    @Test
    @DisplayName("Debe no guardar nada cuando todos los registros son omitidos")
    void testProcessBatch_SavesNothing_WhenAllRecordsSkipped() {
        // Arrange
        batch.add(createThirdExcelData(1));
        batch.add(createThirdExcelData(2));
        batch.add(createThirdExcelData(3));

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache))).thenReturn(null);

        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, true);

        // Assert
        assertEquals(0, result.getSuccessCount());
        assertEquals(3, result.getSkippedCount());
        verify(thirdOutputPort, never()).saveAllThirds(anyList());
    }

    // ========== processBatch Tests - Casos Mixtos ==========

    @Test
    @DisplayName("Debe procesar correctamente un lote con registros exitosos, fallidos y omitidos")
    void testProcessBatch_WithMixedResults() {
        // Arrange
        batch.add(createThirdExcelData(1)); // Exitoso
        batch.add(createThirdExcelData(2)); // Omitido (null)
        batch.add(createThirdExcelData(3)); // Fallido
        batch.add(createThirdExcelData(4)); // Exitoso

        Third convertedThird = createThird();
        Third preparedThird = createThird();
        DataConverter.GeographyData geography = createGeographyData();

        when(dataConverter.convertWithCache(eq(batch.get(0)), eq(cache))).thenReturn(convertedThird);
        when(dataConverter.convertWithCache(eq(batch.get(1)), eq(cache))).thenReturn(null);
        when(dataConverter.convertWithCache(eq(batch.get(2)), eq(cache))).thenThrow(new RuntimeException("Error de validación"));
        when(dataConverter.convertWithCache(eq(batch.get(3)), eq(cache))).thenReturn(convertedThird);

        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(any(Third.class), anyString(), anyString(), anyString())).thenReturn(preparedThird);
        when(thirdOutputPort.saveAllThirds(anyList())).thenReturn(Arrays.asList(preparedThird, preparedThird));

        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, true);

        // Assert
        assertEquals(2, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(1, result.getSkippedCount());
        assertEquals(2, result.getErrors().size());
    }

    @Test
    @DisplayName("Debe procesar lote completo cuando todos los registros son exitosos")
    void testProcessBatch_AllSuccessful() {
        // Arrange
        for (int i = 1; i <= 10; i++) {
            batch.add(createThirdExcelData(i));
        }

        Third convertedThird = createThird();
        Third preparedThird = createThird();
        DataConverter.GeographyData geography = createGeographyData();
        List<Third> savedThirds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            savedThirds.add(preparedThird);
        }

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache))).thenReturn(convertedThird);
        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(any(Third.class), anyString(), anyString(), anyString())).thenReturn(preparedThird);
        when(thirdOutputPort.saveAllThirds(anyList())).thenReturn(savedThirds);

        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, true);

        // Assert
        assertEquals(10, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getSkippedCount());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Debe manejar lote con algunos registros que fallan en prepareThirdForBatchSave")
    void testProcessBatch_WithPrepareFailures() {
        // Arrange
        batch.add(createThirdExcelData(1));
        batch.add(createThirdExcelData(2));
        batch.add(createThirdExcelData(3));

        Third convertedThird = createThird();
        Third preparedThird = createThird();
        DataConverter.GeographyData geography = createGeographyData();

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache))).thenReturn(convertedThird);
        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(any(Third.class), anyString(), anyString(), anyString()))
                .thenReturn(preparedThird)
                .thenThrow(new RuntimeException("Validación falló"))
                .thenReturn(preparedThird);
        when(thirdOutputPort.saveAllThirds(anyList())).thenReturn(Arrays.asList(preparedThird, preparedThird));

        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, true);

        // Assert
        assertEquals(2, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(1, result.getErrors().size());
    }

    // ========== processBatch Tests - Validación de Parámetros ==========

    @Test
    @DisplayName("Debe procesar lote vacío sin errores")
    void testProcessBatch_WithEmptyBatch() {
        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, true);

        // Assert
        assertEquals(0, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getSkippedCount());
        assertTrue(result.getErrors().isEmpty());
        verify(thirdOutputPort, never()).saveAllThirds(anyList());
    }

    @Test
    @DisplayName("Debe usar el cache proporcionado para conversiones")
    void testProcessBatch_UsesCacheForConversions() {
        // Arrange
        batch.add(createThirdExcelData(1));

        Third convertedThird = createThird();
        Third preparedThird = createThird();
        DataConverter.GeographyData geography = createGeographyData();

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache))).thenReturn(convertedThird);
        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(any(Third.class), anyString(), anyString(), anyString())).thenReturn(preparedThird);
        when(thirdOutputPort.saveAllThirds(anyList())).thenReturn(Collections.singletonList(preparedThird));

        // Act
        batchProcessor.processBatch(batch, cache, true);

        // Assert
        verify(dataConverter).convertWithCache(any(ThirdExcelData.class), eq(cache));
        verify(dataConverter).getGeographyData(any(ThirdExcelData.class), eq(cache));
    }

    @Test
    @DisplayName("Debe pasar códigos geográficos correctos a prepareThirdForBatchSave")
    void testProcessBatch_PassesCorrectGeographyCodes() {
        // Arrange
        batch.add(createThirdExcelData(1));

        Third convertedThird = createThird();
        Third preparedThird = createThird();
        DataConverter.GeographyData geography = new DataConverter.GeographyData("US", "CA", "LA001");

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache))).thenReturn(convertedThird);
        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(any(Third.class), eq("US"), eq("CA"), eq("LA001"))).thenReturn(preparedThird);
        when(thirdOutputPort.saveAllThirds(anyList())).thenReturn(Collections.singletonList(preparedThird));

        // Act
        batchProcessor.processBatch(batch, cache, true);

        // Assert
        verify(createThirdService).prepareThirdForBatchSave(any(Third.class), eq("US"), eq("CA"), eq("LA001"));
    }

    // ========== BatchProcessingResult Tests ==========

    @Test
    @DisplayName("Debe crear resultado con constructor builder")
    void testBatchProcessingResult_BuilderConstruction() {
        // Act
        BatchProcessor.BatchProcessingResult result = BatchProcessor.BatchProcessingResult.builder()
                .successCount(10)
                .failureCount(2)
                .skippedCount(3)
                .errors(new ArrayList<>())
                .build();

        // Assert
        assertEquals(10, result.getSuccessCount());
        assertEquals(2, result.getFailureCount());
        assertEquals(3, result.getSkippedCount());
        assertNotNull(result.getErrors());
    }

    @Test
    @DisplayName("Debe crear resultado con constructor sin argumentos")
    void testBatchProcessingResult_NoArgsConstruction() {
        // Act
        BatchProcessor.BatchProcessingResult result = new BatchProcessor.BatchProcessingResult();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getSkippedCount());
    }

    @Test
    @DisplayName("Debe permitir modificar valores del resultado")
    void testBatchProcessingResult_SettersWork() {
        // Arrange
        BatchProcessor.BatchProcessingResult result = new BatchProcessor.BatchProcessingResult();

        // Act
        result.setSuccessCount(5);
        result.setFailureCount(1);
        result.setSkippedCount(2);
        result.setErrors(new ArrayList<>());

        // Assert
        assertEquals(5, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(2, result.getSkippedCount());
        assertNotNull(result.getErrors());
    }

    // ========== Integration Scenario Tests ==========

    @Test
    @DisplayName("Debe manejar escenario completo de procesamiento exitoso")
    void testProcessBatch_CompleteSuccessScenario() {
        // Arrange
        batch.add(createThirdExcelData(1));
        batch.add(createThirdExcelData(2));
        batch.add(createThirdExcelData(3));

        Third convertedThird = createThird();
        Third preparedThird = createThird();
        DataConverter.GeographyData geography = createGeographyData();

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache))).thenReturn(convertedThird);
        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(any(Third.class), anyString(), anyString(), anyString())).thenReturn(preparedThird);
        when(thirdOutputPort.saveAllThirds(anyList())).thenReturn(Arrays.asList(preparedThird, preparedThird, preparedThird));

        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, false);

        // Assert
        assertEquals(3, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getSkippedCount());
        assertTrue(result.getErrors().isEmpty());
        verify(dataConverter, times(3)).convertWithCache(any(ThirdExcelData.class), eq(cache));
        verify(thirdOutputPort, times(1)).saveAllThirds(anyList());
    }

    @Test
    @DisplayName("Debe manejar escenario completo con errores recuperables")
    void testProcessBatch_CompleteScenarioWithRecoverableErrors() {
        // Arrange
        batch.add(createThirdExcelData(1));
        batch.add(createThirdExcelData(2));
        batch.add(createThirdExcelData(3));
        batch.add(createThirdExcelData(4));

        Third convertedThird = createThird();
        Third preparedThird = createThird();
        DataConverter.GeographyData geography = createGeographyData();

        when(dataConverter.convertWithCache(any(ThirdExcelData.class), eq(cache)))
                .thenReturn(convertedThird)
                .thenReturn(null)
                .thenThrow(new RuntimeException("Error de validación"))
                .thenReturn(convertedThird);
        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);
        when(createThirdService.prepareThirdForBatchSave(any(Third.class), anyString(), anyString(), anyString())).thenReturn(preparedThird);
        when(thirdOutputPort.saveAllThirds(anyList())).thenReturn(Arrays.asList(preparedThird, preparedThird));

        // Act
        BatchProcessor.BatchProcessingResult result = batchProcessor.processBatch(batch, cache, true);

        // Assert
        assertEquals(2, result.getSuccessCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(1, result.getSkippedCount());
        assertEquals(2, result.getErrors().size());
    }

    @Test
    @DisplayName("Debe realizar rollback cuando hay error y continueOnError es false")
    void testProcessBatch_RollbackOnErrorWhenFlagIsFalse() {
        // Arrange
        batch.add(createThirdExcelData(1));
        batch.add(createThirdExcelData(2));

        Third convertedThird = createThird();
        DataConverter.GeographyData geography = createGeographyData();

        when(dataConverter.convertWithCache(eq(batch.get(0)), eq(cache))).thenReturn(convertedThird);
        when(dataConverter.convertWithCache(eq(batch.get(1)), eq(cache))).thenThrow(new RuntimeException("Error crítico"));
        when(dataConverter.getGeographyData(any(ThirdExcelData.class), eq(cache))).thenReturn(geography);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            batchProcessor.processBatch(batch, cache, false);
        });

        verify(thirdOutputPort, never()).saveAllThirds(anyList());
    }

    // ========== Helper Methods ==========

    private ThirdExcelData createThirdExcelData(int rowNumber) {
        return ThirdExcelData.builder()
                .rowNumber(rowNumber)
                .personType(ePersonType.Natural)
                .typeIdName("CC")
                .idNumber(100000L + rowNumber)
                .names("Juan")
                .lastNames("Pérez")
                .countryName("Colombia")
                .stateName("Antioquia")
                .cityName("Medellín")
                .address("Calle " + rowNumber)
                .email("test" + rowNumber + "@mail.com")
                .phoneNumber("300000000" + rowNumber)
                .build();
    }

    private Third createThird() {
        return Third.builder()
                .idNumber(123456789L)
                .personType(ePersonType.Natural)
                .names("Juan")
                .lastNames("Pérez")
                .typeId(TypeId.builder().typeIdname("CC").build())
                .build();
    }

    private DataConverter.GeographyData createGeographyData() {
        return new DataConverter.GeographyData("CO", "05", "05001");
    }

    private BatchValidationService.ReferenceDataCache createMockCache() {
        try {
            var constructor = BatchValidationService.ReferenceDataCache.class.getDeclaredConstructor(
                    Map.class, Map.class, Map.class, Map.class, Map.class);
            constructor.setAccessible(true);
            return constructor.newInstance(
                    new HashMap<>(),
                    new HashMap<>(),
                    new HashMap<>(),
                    new HashMap<>(),
                    new HashMap<>());
        } catch (Exception e) {
            throw new RuntimeException("Error creating ReferenceDataCache", e);
        }
    }
}
