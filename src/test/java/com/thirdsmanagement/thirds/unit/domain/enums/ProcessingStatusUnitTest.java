package com.thirdsmanagement.thirds.unit.domain.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.domain.enums.ProcessingStatus;

/**
 * Tests unitarios para el enum ProcessingStatus (estado de procesamiento)
 */
class ProcessingStatusUnitTest {

    @Test
    @DisplayName("Debe retornar true solo para SUCCESS")
    void testIsSuccessful_WithSuccessfulStatus() {
        // Act
        boolean successSuccessful = ProcessingStatus.SUCCESS.isSuccessful();
        boolean duplicateSuccessful = ProcessingStatus.DUPLICATE_SKIPPED.isSuccessful();
        boolean failedSuccessful = ProcessingStatus.FAILED.isSuccessful();
        boolean skippedSuccessful = ProcessingStatus.SKIPPED.isSuccessful();

        // Assert
        assertTrue(successSuccessful);
        assertFalse(duplicateSuccessful);
        assertFalse(failedSuccessful);
        assertFalse(skippedSuccessful);
    }

    @Test
    @DisplayName("Debe retornar true para estados omitidos")
    void testWasSkipped_WithSkippedStatuses() {
        // Act
        boolean duplicateSkipped = ProcessingStatus.DUPLICATE_SKIPPED.wasSkipped();
        boolean skippedSkipped = ProcessingStatus.SKIPPED.wasSkipped();

        // Assert
        assertTrue(duplicateSkipped);
        assertTrue(skippedSkipped);
    }

    @Test
    @DisplayName("Debe retornar false para estados no omitidos")
    void testWasSkipped_WithNonSkippedStatuses() {
        // Act
        boolean successSkipped = ProcessingStatus.SUCCESS.wasSkipped();
        boolean failedSkipped = ProcessingStatus.FAILED.wasSkipped();

        // Assert
        assertFalse(successSkipped);
        assertFalse(failedSkipped);
    }

    @Test
    @DisplayName("Debe retornar true solo para FAILED")
    void testIsFailed_WithFailedStatus() {
        // Act
        boolean failedFailed = ProcessingStatus.FAILED.isFailed();
        boolean successFailed = ProcessingStatus.SUCCESS.isFailed();
        boolean duplicateFailed = ProcessingStatus.DUPLICATE_SKIPPED.isFailed();
        boolean skippedFailed = ProcessingStatus.SKIPPED.isFailed();

        // Assert
        assertTrue(failedFailed);
        assertFalse(successFailed);
        assertFalse(duplicateFailed);
        assertFalse(skippedFailed);
    }

    @Test
    @DisplayName("Debe retornar true solo para DUPLICATE_SKIPPED")
    void testIsDuplicate_WithDuplicateStatus() {
        // Act
        boolean duplicateIsDuplicate = ProcessingStatus.DUPLICATE_SKIPPED.isDuplicate();
        boolean successIsDuplicate = ProcessingStatus.SUCCESS.isDuplicate();
        boolean failedIsDuplicate = ProcessingStatus.FAILED.isDuplicate();
        boolean skippedIsDuplicate = ProcessingStatus.SKIPPED.isDuplicate();

        // Assert
        assertTrue(duplicateIsDuplicate);
        assertFalse(successIsDuplicate);
        assertFalse(failedIsDuplicate);
        assertFalse(skippedIsDuplicate);
    }

    @Test
    @DisplayName("Debe tener valores correctos de descripción")
    void testGetDescription() {
        // Act
        String successDesc = ProcessingStatus.SUCCESS.getDescription();
        String duplicateDesc = ProcessingStatus.DUPLICATE_SKIPPED.getDescription();
        String failedDesc = ProcessingStatus.FAILED.getDescription();
        String skippedDesc = ProcessingStatus.SKIPPED.getDescription();

        // Assert
        assertEquals("Éxito", successDesc);
        assertEquals("Duplicado Omitido", duplicateDesc);
        assertEquals("Fallido", failedDesc);
        assertEquals("Omitido", skippedDesc);
    }

    @Test
    @DisplayName("Debe tener exactamente cuatro valores de enum")
    void testEnumValues() {
        // Act
        ProcessingStatus[] values = ProcessingStatus.values();

        // Assert
        assertEquals(4, values.length);
        assertEquals(ProcessingStatus.SUCCESS, values[0]);
        assertEquals(ProcessingStatus.DUPLICATE_SKIPPED, values[1]);
        assertEquals(ProcessingStatus.FAILED, values[2]);
        assertEquals(ProcessingStatus.SKIPPED, values[3]);
    }

    @Test
    @DisplayName("Debe validar comportamiento correcto de SUCCESS")
    void testSuccess_Behavior() {
        // Arrange
        ProcessingStatus status = ProcessingStatus.SUCCESS;

        // Act
        boolean isSuccessful = status.isSuccessful();
        boolean wasSkipped = status.wasSkipped();
        boolean isFailed = status.isFailed();
        boolean isDuplicate = status.isDuplicate();

        // Assert
        assertTrue(isSuccessful, "Debe ser exitoso");
        assertFalse(wasSkipped, "No debe estar omitido");
        assertFalse(isFailed, "No debe haber fallado");
        assertFalse(isDuplicate, "No debe ser duplicado");
    }

    @Test
    @DisplayName("Debe validar comportamiento correcto de DUPLICATE_SKIPPED")
    void testDuplicateSkipped_Behavior() {
        // Arrange
        ProcessingStatus status = ProcessingStatus.DUPLICATE_SKIPPED;

        // Act
        boolean isSuccessful = status.isSuccessful();
        boolean wasSkipped = status.wasSkipped();
        boolean isFailed = status.isFailed();
        boolean isDuplicate = status.isDuplicate();

        // Assert
        assertFalse(isSuccessful, "No debe ser exitoso");
        assertTrue(wasSkipped, "Debe estar omitido");
        assertFalse(isFailed, "No debe haber fallado");
        assertTrue(isDuplicate, "Debe ser duplicado");
    }

    @Test
    @DisplayName("Debe validar comportamiento correcto de FAILED")
    void testFailed_Behavior() {
        // Arrange
        ProcessingStatus status = ProcessingStatus.FAILED;

        // Act
        boolean isSuccessful = status.isSuccessful();
        boolean wasSkipped = status.wasSkipped();
        boolean isFailed = status.isFailed();
        boolean isDuplicate = status.isDuplicate();

        // Assert
        assertFalse(isSuccessful, "No debe ser exitoso");
        assertFalse(wasSkipped, "No debe estar omitido");
        assertTrue(isFailed, "Debe haber fallado");
        assertFalse(isDuplicate, "No debe ser duplicado");
    }

    @Test
    @DisplayName("Debe validar comportamiento correcto de SKIPPED")
    void testSkipped_Behavior() {
        // Arrange
        ProcessingStatus status = ProcessingStatus.SKIPPED;

        // Act
        boolean isSuccessful = status.isSuccessful();
        boolean wasSkipped = status.wasSkipped();
        boolean isFailed = status.isFailed();
        boolean isDuplicate = status.isDuplicate();

        // Assert
        assertFalse(isSuccessful, "No debe ser exitoso");
        assertTrue(wasSkipped, "Debe estar omitido");
        assertFalse(isFailed, "No debe haber fallado");
        assertFalse(isDuplicate, "No debe ser duplicado");
    }

    @Test
    @DisplayName("Debe diferenciar correctamente DUPLICATE_SKIPPED de SKIPPED")
    void testDuplicateVsSkipped_Distinction() {
        // Act
        boolean duplicateWasSkipped = ProcessingStatus.DUPLICATE_SKIPPED.wasSkipped();
        boolean skippedWasSkipped = ProcessingStatus.SKIPPED.wasSkipped();
        boolean duplicateIsDuplicate = ProcessingStatus.DUPLICATE_SKIPPED.isDuplicate();
        boolean skippedIsDuplicate = ProcessingStatus.SKIPPED.isDuplicate();

        // Assert
        assertTrue(duplicateWasSkipped, "DUPLICATE_SKIPPED debe estar omitido");
        assertTrue(skippedWasSkipped, "SKIPPED debe estar omitido");
        assertTrue(duplicateIsDuplicate, "DUPLICATE_SKIPPED debe ser duplicado");
        assertFalse(skippedIsDuplicate, "SKIPPED no debe ser duplicado");
    }

    @Test
    @DisplayName("Debe identificar correctamente estados de error")
    void testErrorStates_Identification() {
        // Arrange
        ProcessingStatus[] allStatuses = ProcessingStatus.values();

        // Act
        int failedCount = 0;
        for (ProcessingStatus status : allStatuses) {
            if (status.isFailed()) {
                failedCount++;
            }
        }

        // Assert
        assertEquals(1, failedCount, "Debe haber exactamente un estado fallido");
        for (ProcessingStatus status : allStatuses) {
            if (status.isFailed()) {
                assertEquals(ProcessingStatus.FAILED, status, "Solo FAILED debe ser considerado fallido");
            }
        }
    }
}
