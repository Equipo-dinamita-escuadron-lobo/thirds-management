package com.thirdsmanagement.thirds.unit.domain.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.domain.enums.ImportStatus;

/**
 * Tests unitarios para el enum ImportStatus (estado de importación)
 */
class ImportStatusUnitTest {

    @Test
    @DisplayName("Debe retornar true para estados finalizados")
    void testIsFinished_WithFinishedStates() {
        // Act
        boolean completedFinished = ImportStatus.COMPLETED.isFinished();
        boolean completedWithErrorsFinished = ImportStatus.COMPLETED_WITH_ERRORS.isFinished();
        boolean failedFinished = ImportStatus.FAILED.isFinished();

        // Assert
        assertTrue(completedFinished);
        assertTrue(completedWithErrorsFinished);
        assertTrue(failedFinished);
    }

    @Test
    @DisplayName("Debe retornar false para estados no finalizados")
    void testIsFinished_WithUnfinishedStates() {
        // Act
        boolean pendingFinished = ImportStatus.PENDING.isFinished();
        boolean processingFinished = ImportStatus.PROCESSING.isFinished();

        // Assert
        assertFalse(pendingFinished);
        assertFalse(processingFinished);
    }

    @Test
    @DisplayName("Debe retornar true para estados con registros exitosos")
    void testHasSuccessfulRecords_WithSuccessfulStates() {
        // Act
        boolean completedHasSuccess = ImportStatus.COMPLETED.hasSuccessfulRecords();
        boolean completedWithErrorsHasSuccess = ImportStatus.COMPLETED_WITH_ERRORS.hasSuccessfulRecords();

        // Assert
        assertTrue(completedHasSuccess);
        assertTrue(completedWithErrorsHasSuccess);
    }

    @Test
    @DisplayName("Debe retornar false para estados sin registros exitosos")
    void testHasSuccessfulRecords_WithUnsuccessfulStates() {
        // Act
        boolean pendingHasSuccess = ImportStatus.PENDING.hasSuccessfulRecords();
        boolean processingHasSuccess = ImportStatus.PROCESSING.hasSuccessfulRecords();
        boolean failedHasSuccess = ImportStatus.FAILED.hasSuccessfulRecords();

        // Assert
        assertFalse(pendingHasSuccess);
        assertFalse(processingHasSuccess);
        assertFalse(failedHasSuccess);
    }

    @Test
    @DisplayName("Debe retornar true para estados con errores")
    void testHasErrors_WithErrorStates() {
        // Act
        boolean completedWithErrorsHasErrors = ImportStatus.COMPLETED_WITH_ERRORS.hasErrors();
        boolean failedHasErrors = ImportStatus.FAILED.hasErrors();

        // Assert
        assertTrue(completedWithErrorsHasErrors);
        assertTrue(failedHasErrors);
    }

    @Test
    @DisplayName("Debe retornar false para estados sin errores")
    void testHasErrors_WithNoErrorStates() {
        // Act
        boolean pendingHasErrors = ImportStatus.PENDING.hasErrors();
        boolean processingHasErrors = ImportStatus.PROCESSING.hasErrors();
        boolean completedHasErrors = ImportStatus.COMPLETED.hasErrors();

        // Assert
        assertFalse(pendingHasErrors);
        assertFalse(processingHasErrors);
        assertFalse(completedHasErrors);
    }

    @Test
    @DisplayName("Debe tener valores correctos de descripción")
    void testGetDescription() {
        // Act
        String pendingDesc = ImportStatus.PENDING.getDescription();
        String processingDesc = ImportStatus.PROCESSING.getDescription();
        String completedDesc = ImportStatus.COMPLETED.getDescription();
        String completedWithErrorsDesc = ImportStatus.COMPLETED_WITH_ERRORS.getDescription();
        String failedDesc = ImportStatus.FAILED.getDescription();

        // Assert
        assertEquals("Pendiente", pendingDesc);
        assertEquals("Procesando", processingDesc);
        assertEquals("Completado", completedDesc);
        assertEquals("Completado con Errores", completedWithErrorsDesc);
        assertEquals("Fallido", failedDesc);
    }

    @Test
    @DisplayName("Debe tener exactamente cinco valores de enum")
    void testEnumValues() {
        // Act
        ImportStatus[] values = ImportStatus.values();

        // Assert
        assertEquals(5, values.length);
        assertEquals(ImportStatus.PENDING, values[0]);
        assertEquals(ImportStatus.PROCESSING, values[1]);
        assertEquals(ImportStatus.COMPLETED, values[2]);
        assertEquals(ImportStatus.COMPLETED_WITH_ERRORS, values[3]);
        assertEquals(ImportStatus.FAILED, values[4]);
    }

    @Test
    @DisplayName("Debe validar comportamiento correcto de COMPLETED_WITH_ERRORS")
    void testCompletedWithErrors_Behavior() {
        // Arrange
        ImportStatus status = ImportStatus.COMPLETED_WITH_ERRORS;

        // Act
        boolean isFinished = status.isFinished();
        boolean hasSuccessfulRecords = status.hasSuccessfulRecords();
        boolean hasErrors = status.hasErrors();

        // Assert
        assertTrue(isFinished, "Debe estar finalizado");
        assertTrue(hasSuccessfulRecords, "Debe tener registros exitosos");
        assertTrue(hasErrors, "Debe tener errores");
    }

    @Test
    @DisplayName("Debe validar comportamiento correcto de COMPLETED")
    void testCompleted_Behavior() {
        // Arrange
        ImportStatus status = ImportStatus.COMPLETED;

        // Act
        boolean isFinished = status.isFinished();
        boolean hasSuccessfulRecords = status.hasSuccessfulRecords();
        boolean hasErrors = status.hasErrors();

        // Assert
        assertTrue(isFinished, "Debe estar finalizado");
        assertTrue(hasSuccessfulRecords, "Debe tener registros exitosos");
        assertFalse(hasErrors, "No debe tener errores");
    }

    @Test
    @DisplayName("Debe validar comportamiento correcto de FAILED")
    void testFailed_Behavior() {
        // Arrange
        ImportStatus status = ImportStatus.FAILED;

        // Act
        boolean isFinished = status.isFinished();
        boolean hasSuccessfulRecords = status.hasSuccessfulRecords();
        boolean hasErrors = status.hasErrors();

        // Assert
        assertTrue(isFinished, "Debe estar finalizado");
        assertFalse(hasSuccessfulRecords, "No debe tener registros exitosos");
        assertTrue(hasErrors, "Debe tener errores");
    }

    @Test
    @DisplayName("Debe validar comportamiento correcto de PROCESSING")
    void testProcessing_Behavior() {
        // Arrange
        ImportStatus status = ImportStatus.PROCESSING;

        // Act
        boolean isFinished = status.isFinished();
        boolean hasSuccessfulRecords = status.hasSuccessfulRecords();
        boolean hasErrors = status.hasErrors();

        // Assert
        assertFalse(isFinished, "No debe estar finalizado");
        assertFalse(hasSuccessfulRecords, "No debe tener registros exitosos");
        assertFalse(hasErrors, "No debe tener errores");
    }
}
