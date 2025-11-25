package com.thirdsmanagement.thirds.unit.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import com.thirdsmanagement.thirds.domain.model.ImportErrorDetail;
import com.thirdsmanagement.thirds.domain.model.ImportJobStatus;

/**
 * Tests unitarios para la entidad ImportJobStatus (estado de job de importación)
 */
class ImportJobStatusUnitTest {

    private ImportJobStatus importJobStatus;
    private ImportErrorDetail error1;
    private ImportErrorDetail error2;
    private List<ImportErrorDetail> errorList;

    @BeforeEach
    void setUp() {
        // Arrange
        importJobStatus = new ImportJobStatus();
        importJobStatus.setJobId("job-123");
        importJobStatus.setEntId("EMP001");
        importJobStatus.setFileName("terceros.xlsx");
        importJobStatus.setStatus(ImportStatus.PENDING);
        importJobStatus.setStartTime(LocalDateTime.now().minusMinutes(10));
        importJobStatus.setEndTime(LocalDateTime.now());

        error1 = new ImportErrorDetail();
        error1.setRowNumber(5);
        error1.setColumnName("email");
        error1.setErrorMessage("Formato de email inválido");

        error2 = new ImportErrorDetail();
        error2.setRowNumber(10);
        error2.setColumnName("idNumber");
        error2.setErrorMessage("Número de identificación requerido");

        errorList = Arrays.asList(error1, error2);
    }

    @Test
    @DisplayName("Debe crear estado de job de importación con datos válidos")
    void testImportJobStatusCreation_WithValidData() {
        // Act & Assert
        assertNotNull(importJobStatus);
        assertEquals("job-123", importJobStatus.getJobId());
        assertEquals("EMP001", importJobStatus.getEntId());
        assertEquals("terceros.xlsx", importJobStatus.getFileName());
        assertEquals(ImportStatus.PENDING, importJobStatus.getStatus());
        assertNotNull(importJobStatus.getStartTime());
        assertNotNull(importJobStatus.getEndTime());
    }

    @Test
    @DisplayName("Debe actualizar métricas correctamente")
    void testUpdateMetrics_WithValidData() {
        // Act
        importJobStatus.updateMetrics(100, 85, 10, 5);

        // Assert
        assertEquals(100, importJobStatus.getTotalRecords());
        assertEquals(85, importJobStatus.getSuccessfulImports());
        assertEquals(10, importJobStatus.getFailedImports());
        assertEquals(5, importJobStatus.getDuplicatesSkipped());
    }

    @Test
    @DisplayName("Debe actualizar métricas con valores cero")
    void testUpdateMetrics_WithZeroValues() {
        // Act
        importJobStatus.updateMetrics(0, 0, 0, 0);

        // Assert
        assertEquals(0, importJobStatus.getTotalRecords());
        assertEquals(0, importJobStatus.getSuccessfulImports());
        assertEquals(0, importJobStatus.getFailedImports());
        assertEquals(0, importJobStatus.getDuplicatesSkipped());
    }

    @Test
    @DisplayName("Debe actualizar métricas con números grandes")
    void testUpdateMetrics_WithLargeNumbers() {
        // Act
        importJobStatus.updateMetrics(10000, 9500, 400, 100);

        // Assert
        assertEquals(10000, importJobStatus.getTotalRecords());
        assertEquals(9500, importJobStatus.getSuccessfulImports());
        assertEquals(400, importJobStatus.getFailedImports());
        assertEquals(100, importJobStatus.getDuplicatesSkipped());
    }

    @Test
    @DisplayName("Debe agregar errores a lista vacía")
    void testAddErrors_WhenErrorsListIsNull() {
        // Arrange
        importJobStatus.setErrors(null);

        // Act
        importJobStatus.addErrors(errorList);

        // Assert
        assertNotNull(importJobStatus.getErrors());
        assertEquals(2, importJobStatus.getErrors().size());
        assertTrue(importJobStatus.getErrors().contains(error1));
        assertTrue(importJobStatus.getErrors().contains(error2));
    }

    @Test
    @DisplayName("Debe agregar errores a lista existente")
    void testAddErrors_WhenErrorsListExists() {
        // Arrange
        List<ImportErrorDetail> existingErrors = new ArrayList<>();
        existingErrors.add(new ImportErrorDetail());
        importJobStatus.setErrors(existingErrors);

        // Act
        importJobStatus.addErrors(errorList);

        // Assert
        assertEquals(3, importJobStatus.getErrors().size());
    }

    @Test
    @DisplayName("Debe agregar lista de errores vacía")
    void testAddErrors_WithEmptyList() {
        // Arrange
        importJobStatus.setErrors(new ArrayList<>());
        List<ImportErrorDetail> emptyList = new ArrayList<>();

        // Act
        importJobStatus.addErrors(emptyList);

        // Assert
        assertNotNull(importJobStatus.getErrors());
        assertTrue(importJobStatus.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Debe manejar entrada de lista de errores null")
    void testAddErrors_WithNullInput() {
        // Arrange
        importJobStatus.setErrors(new ArrayList<>());

        // Act
        importJobStatus.addErrors(null);

        // Assert
        assertNotNull(importJobStatus.getErrors());
        assertTrue(importJobStatus.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Debe inicializar con lista de errores por defecto")
    void testImportJobStatusCreation_WithDefaultErrorsList() {
        // Arrange & Act
        ImportJobStatus newJobStatus = new ImportJobStatus();

        // Assert
        assertNotNull(newJobStatus.getErrors());
        assertTrue(newJobStatus.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Debe manejar cálculo de progreso")
    void testProgressCalculation() {
        // Arrange
        importJobStatus.setProgress(75);

        // Act
        int progress = importJobStatus.getProgress();

        // Assert
        assertEquals(75, progress);
    }

    @Test
    @DisplayName("Debe manejar todos los valores de estado de importación")
    void testImportStatusEnumValues() {
        // Arrange & Act & Assert
        for (ImportStatus status : ImportStatus.values()) {
            importJobStatus.setStatus(status);
            assertEquals(status, importJobStatus.getStatus());
        }
    }
}
