package com.thirdsmanagement.thirds.unit.application.service.importExport;

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
import com.thirdsmanagement.thirds.application.service.importExport.DuplicateDetectionService;
import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;
import com.thirdsmanagement.thirds.domain.model.ThirdExcelData;

/**
 * Tests unitarios para DuplicateDetectionService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DuplicateDetectionServiceUnitTest {

    @Mock
    private ThirdOutputPort thirdOutputPort;

    @InjectMocks
    private DuplicateDetectionService duplicateDetectionService;

    private String entId;
    private ThirdExcelData record1;
    private ThirdExcelData record2;
    private ThirdExcelData record3;

    @BeforeEach
    void setUp() {
        entId = "ENT001";

        record1 = ThirdExcelData.builder()
                .rowNumber(2)
                .entId(entId)
                .idNumber(123456789L)
                .typeIdName("CC")
                .names("Juan")
                .lastNames("Pérez")
                .build();

        record2 = ThirdExcelData.builder()
                .rowNumber(3)
                .entId(entId)
                .idNumber(987654321L)
                .typeIdName("CC")
                .names("María")
                .lastNames("García")
                .build();

        record3 = ThirdExcelData.builder()
                .rowNumber(4)
                .entId(entId)
                .idNumber(123456789L)
                .typeIdName("CC")
                .names("Pedro")
                .lastNames("López")
                .build();
    }

    // ========== detectDuplicates Tests ==========

    @Test
    @DisplayName("Debe detectar duplicados con todos los registros únicos")
    void testDetectDuplicates_WithUniqueRecords() {
        // Arrange
        List<ThirdExcelData> thirdsData = Arrays.asList(record1, record2);
        when(thirdOutputPort.findExistingIdNumbers(anySet(), eq(entId))).thenReturn(Collections.emptySet());

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, false);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalAnalyzed());
        assertEquals(2, result.getUniqueCount());
        assertEquals(0, result.getDuplicateCount());
        assertEquals(2, result.getUniqueRecords().size());
        assertTrue(result.getErrors().isEmpty());
        verify(thirdOutputPort).findExistingIdNumbers(anySet(), eq(entId));
    }

    @Test
    @DisplayName("Debe detectar duplicados internos en el Excel")
    void testDetectDuplicates_WithInternalDuplicates() {
        // Arrange
        List<ThirdExcelData> thirdsData = Arrays.asList(record1, record3);
        when(thirdOutputPort.findExistingIdNumbers(anySet(), eq(entId))).thenReturn(Collections.emptySet());

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, false);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalAnalyzed());
        assertEquals(1, result.getUniqueCount());
        assertEquals(1, result.getDuplicateCount());
        assertEquals(1, result.getUniqueRecords().size());
        assertEquals(1, result.getErrors().size());
        assertEquals("INTERNAL_DUPLICATE_FOUND", result.getErrors().get(0).getErrorCode());
        assertEquals(4, result.getErrors().get(0).getRowNumber());
        assertTrue(result.getErrors().get(0).getErrorMessage().contains("fila 2"));
    }

    @Test
    @DisplayName("Debe omitir duplicados internos cuando skipDuplicates es true")
    void testDetectDuplicates_WithInternalDuplicatesSkipped() {
        // Arrange
        List<ThirdExcelData> thirdsData = Arrays.asList(record1, record3);
        when(thirdOutputPort.findExistingIdNumbers(anySet(), eq(entId))).thenReturn(Collections.emptySet());

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, true);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalAnalyzed());
        assertEquals(1, result.getUniqueCount());
        assertEquals(1, result.getDuplicateCount());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Debe detectar duplicados con la base de datos")
    void testDetectDuplicates_WithDatabaseDuplicates() {
        // Arrange
        List<ThirdExcelData> thirdsData = Arrays.asList(record1, record2);
        Set<Long> existingIds = new HashSet<>(Arrays.asList(123456789L));
        when(thirdOutputPort.findExistingIdNumbers(anySet(), eq(entId))).thenReturn(existingIds);

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, false);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalAnalyzed());
        assertEquals(1, result.getUniqueCount());
        assertEquals(1, result.getDuplicateCount());
        assertEquals(1, result.getUniqueRecords().size());
        assertEquals(987654321L, result.getUniqueRecords().get(0).getIdNumber());
        assertEquals(1, result.getErrors().size());
        assertEquals("DATABASE_DUPLICATE_FOUND", result.getErrors().get(0).getErrorCode());
        assertEquals(2, result.getErrors().get(0).getRowNumber());
    }

    @Test
    @DisplayName("Debe omitir duplicados de base de datos cuando skipDuplicates es true")
    void testDetectDuplicates_WithDatabaseDuplicatesSkipped() {
        // Arrange
        List<ThirdExcelData> thirdsData = Arrays.asList(record1, record2);
        Set<Long> existingIds = new HashSet<>(Arrays.asList(123456789L));
        when(thirdOutputPort.findExistingIdNumbers(anySet(), eq(entId))).thenReturn(existingIds);

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, true);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalAnalyzed());
        assertEquals(1, result.getUniqueCount());
        assertEquals(1, result.getDuplicateCount());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Debe detectar duplicados internos y de base de datos simultáneamente")
    void testDetectDuplicates_WithBothInternalAndDatabaseDuplicates() {
        // Arrange
        ThirdExcelData record4 = ThirdExcelData.builder()
                .rowNumber(5)
                .entId(entId)
                .idNumber(111222333L)
                .typeIdName("CC")
                .names("Ana")
                .lastNames("Martínez")
                .build();

        List<ThirdExcelData> thirdsData = Arrays.asList(record1, record3, record4);
        Set<Long> existingIds = new HashSet<>(Arrays.asList(111222333L));
        when(thirdOutputPort.findExistingIdNumbers(anySet(), eq(entId))).thenReturn(existingIds);

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, false);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalAnalyzed());
        assertEquals(1, result.getUniqueCount());
        assertEquals(2, result.getDuplicateCount());
        assertEquals(1, result.getUniqueRecords().size());
        assertEquals(123456789L, result.getUniqueRecords().get(0).getIdNumber());
        assertEquals(2, result.getErrors().size());
    }

    @Test
    @DisplayName("Debe manejar lista vacía correctamente")
    void testDetectDuplicates_WithEmptyList() {
        // Arrange
        List<ThirdExcelData> thirdsData = Collections.emptyList();

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, false);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalAnalyzed());
        assertEquals(0, result.getUniqueCount());
        assertEquals(0, result.getDuplicateCount());
        assertTrue(result.getUniqueRecords().isEmpty());
        assertTrue(result.getErrors().isEmpty());
        verify(thirdOutputPort, never()).findExistingIdNumbers(anySet(), anyString());
    }

    @Test
    @DisplayName("Debe ignorar registros con idNumber null en detección interna")
    void testDetectDuplicates_WithNullIdNumber() {
        // Arrange
        ThirdExcelData recordWithNullId = ThirdExcelData.builder()
                .rowNumber(2)
                .entId(entId)
                .idNumber(null)
                .typeIdName("CC")
                .names("Juan")
                .build();

        List<ThirdExcelData> thirdsData = Arrays.asList(recordWithNullId, record1);
        when(thirdOutputPort.findExistingIdNumbers(anySet(), eq(entId))).thenReturn(Collections.emptySet());

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, false);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalAnalyzed());
        assertEquals(1, result.getUniqueCount());
        assertEquals(1, result.getDuplicateCount());
        assertEquals(1, result.getUniqueRecords().size());
        assertEquals(123456789L, result.getUniqueRecords().get(0).getIdNumber());
    }

    @Test
    @DisplayName("Debe detectar múltiples duplicados internos del mismo registro")
    void testDetectDuplicates_WithMultipleInternalDuplicates() {
        // Arrange
        ThirdExcelData record4 = ThirdExcelData.builder()
                .rowNumber(5)
                .entId(entId)
                .idNumber(123456789L)
                .typeIdName("CC")
                .names("Luis")
                .build();

        List<ThirdExcelData> thirdsData = Arrays.asList(record1, record3, record4);
        when(thirdOutputPort.findExistingIdNumbers(anySet(), eq(entId))).thenReturn(Collections.emptySet());

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, false);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalAnalyzed());
        assertEquals(1, result.getUniqueCount());
        assertEquals(2, result.getDuplicateCount());
        assertEquals(2, result.getErrors().size());
        assertTrue(result.getErrors().stream()
                .allMatch(e -> e.getErrorCode().equals("INTERNAL_DUPLICATE_FOUND")));
    }

    @Test
    @DisplayName("Debe incluir número de columna en errores de duplicado interno")
    void testDetectDuplicates_InternalDuplicateErrorFormat() {
        // Arrange
        List<ThirdExcelData> thirdsData = Arrays.asList(record1, record3);
        when(thirdOutputPort.findExistingIdNumbers(anySet(), eq(entId))).thenReturn(Collections.emptySet());

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals("Número Identificación", result.getErrors().get(0).getColumnName());
        assertEquals("123456789", result.getErrors().get(0).getFieldValue());
        assertEquals(ImportErrorType.DUPLICATE_ERROR, result.getErrors().get(0).getErrorType());
    }

    @Test
    @DisplayName("Debe incluir información correcta en errores de duplicado de base de datos")
    void testDetectDuplicates_DatabaseDuplicateErrorFormat() {
        // Arrange
        List<ThirdExcelData> thirdsData = Arrays.asList(record1);
        Set<Long> existingIds = new HashSet<>(Arrays.asList(123456789L));
        when(thirdOutputPort.findExistingIdNumbers(anySet(), eq(entId))).thenReturn(existingIds);

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getErrors().size());
        assertEquals("Número Identificación", result.getErrors().get(0).getColumnName());
        assertEquals("123456789", result.getErrors().get(0).getFieldValue());
        assertEquals("El tercero ya existe en la base de datos.", result.getErrors().get(0).getErrorMessage());
        assertEquals(ImportErrorType.DUPLICATE_ERROR, result.getErrors().get(0).getErrorType());
    }

    @Test
    @DisplayName("Debe usar solo registros únicos para verificación de base de datos")
    void testDetectDuplicates_OnlyUniqueRecordsCheckedAgainstDatabase() {
        // Arrange
        List<ThirdExcelData> thirdsData = Arrays.asList(record1, record3, record2);
        when(thirdOutputPort.findExistingIdNumbers(anySet(), eq(entId))).thenReturn(Collections.emptySet());

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, false);

        // Assert
        assertEquals(2, result.getUniqueCount());
        verify(thirdOutputPort).findExistingIdNumbers(argThat(set -> 
            set.size() == 2 && set.contains(123456789L) && set.contains(987654321L)
        ), eq(entId));
    }

    @Test
    @DisplayName("Debe preservar el orden de registros únicos")
    void testDetectDuplicates_PreservesOrderOfUniqueRecords() {
        // Arrange
        List<ThirdExcelData> thirdsData = Arrays.asList(record2, record1);
        when(thirdOutputPort.findExistingIdNumbers(anySet(), eq(entId))).thenReturn(Collections.emptySet());

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, false);

        // Assert
        assertEquals(2, result.getUniqueRecords().size());
        assertEquals(987654321L, result.getUniqueRecords().get(0).getIdNumber());
        assertEquals(123456789L, result.getUniqueRecords().get(1).getIdNumber());
    }

    @Test
    @DisplayName("Debe manejar correctamente cuando todos son duplicados de base de datos")
    void testDetectDuplicates_AllDatabaseDuplicates() {
        // Arrange
        List<ThirdExcelData> thirdsData = Arrays.asList(record1, record2);
        Set<Long> existingIds = new HashSet<>(Arrays.asList(123456789L, 987654321L));
        when(thirdOutputPort.findExistingIdNumbers(anySet(), eq(entId))).thenReturn(existingIds);

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, false);

        // Assert
        assertEquals(2, result.getTotalAnalyzed());
        assertEquals(0, result.getUniqueCount());
        assertEquals(2, result.getDuplicateCount());
        assertTrue(result.getUniqueRecords().isEmpty());
        assertEquals(2, result.getErrors().size());
    }

    @Test
    @DisplayName("Debe crear clave de duplicado usando entId e idNumber")
    void testDetectDuplicates_DuplicateKeyConsidersEntId() {
        // Arrange
        ThirdExcelData recordDifferentEnt = ThirdExcelData.builder()
                .rowNumber(3)
                .entId("ENT002")
                .idNumber(123456789L)
                .typeIdName("CC")
                .names("María")
                .build();

        List<ThirdExcelData> thirdsData = Arrays.asList(record1, recordDifferentEnt);
        when(thirdOutputPort.findExistingIdNumbers(anySet(), eq(entId))).thenReturn(Collections.emptySet());

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, false);

        // Assert
        assertEquals(2, result.getUniqueCount());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Debe no llamar a base de datos cuando no hay registros únicos después de detectar internos")
    void testDetectDuplicates_NoDbCallWhenNoUniqueRecords() {
        // Arrange
        ThirdExcelData recordAllNull = ThirdExcelData.builder()
                .rowNumber(2)
                .entId(entId)
                .idNumber(null)
                .typeIdName("CC")
                .build();

        List<ThirdExcelData> thirdsData = Arrays.asList(recordAllNull);

        // Act
        DuplicateDetectionService.DuplicateDetectionResult result = 
            duplicateDetectionService.detectDuplicates(thirdsData, entId, false);

        // Assert
        verify(thirdOutputPort, never()).findExistingIdNumbers(anySet(), anyString());
        assertEquals(0, result.getUniqueCount());
    }
}
