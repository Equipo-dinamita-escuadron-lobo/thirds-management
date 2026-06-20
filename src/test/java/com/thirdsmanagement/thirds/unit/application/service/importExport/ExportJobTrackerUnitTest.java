package com.thirdsmanagement.thirds.unit.application.service.importExport;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.application.service.importExport.ExportJobTracker;
import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import com.thirdsmanagement.thirds.domain.model.ExportJobStatus;

/**
 * Tests unitarios para ExportJobTracker
 */
class ExportJobTrackerUnitTest {

    private ExportJobTracker exportJobTracker;
    private String entId;
    private String fileName;

    @BeforeEach
    void setUp() {
        exportJobTracker = new ExportJobTracker();
        entId = "ENT001";
        fileName = "terceros_export.xlsx";
    }

    // ========== createJob Tests ==========

    @Test
    @DisplayName("Debe crear un nuevo trabajo de exportación con UUID único")
    void testCreateJob_GeneratesUniqueJobId() {
        // Act
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Assert
        assertNotNull(jobId);
        assertFalse(jobId.isEmpty());
    }

    @Test
    @DisplayName("Debe crear un trabajo con estado PENDING")
    void testCreateJob_WithPendingStatus() {
        // Act
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(ImportStatus.PENDING, jobStatus.get().getStatus());
    }

    @Test
    @DisplayName("Debe inicializar el progreso en 0")
    void testCreateJob_WithZeroProgress() {
        // Act
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(0, jobStatus.get().getProgress());
    }

    @Test
    @DisplayName("Debe almacenar el entId y fileName correctamente")
    void testCreateJob_StoresEntIdAndFileName() {
        // Act
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(entId, jobStatus.get().getEntId());
        assertEquals(fileName, jobStatus.get().getFileName());
    }

    @Test
    @DisplayName("Debe establecer la hora de inicio al crear el trabajo")
    void testCreateJob_SetsStartTime() {
        // Act
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertNotNull(jobStatus.get().getStartTime());
    }

    @Test
    @DisplayName("Debe generar IDs únicos para diferentes trabajos")
    void testCreateJob_GeneratesUniqueIdsForMultipleJobs() {
        // Act
        String jobId1 = exportJobTracker.createJob(entId, "file1.xlsx");
        String jobId2 = exportJobTracker.createJob(entId, "file2.xlsx");
        String jobId3 = exportJobTracker.createJob(entId, "file3.xlsx");

        // Assert
        assertNotEquals(jobId1, jobId2);
        assertNotEquals(jobId2, jobId3);
        assertNotEquals(jobId1, jobId3);
    }

    // ========== getJobStatus Tests ==========

    @Test
    @DisplayName("Debe obtener el estado de un trabajo existente")
    void testGetJobStatus_WithExistingJob() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Act
        Optional<ExportJobStatus> result = exportJobTracker.getJobStatus(jobId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(jobId, result.get().getJobId());
    }

    @Test
    @DisplayName("Debe retornar Optional vacío para trabajo inexistente")
    void testGetJobStatus_WithNonExistentJob() {
        // Act
        Optional<ExportJobStatus> result = exportJobTracker.getJobStatus("non-existent-id");

        // Assert
        assertFalse(result.isPresent());
    }

    // ========== updateJobStatus Tests ==========

    @Test
    @DisplayName("Debe actualizar el estado a PROCESSING")
    void testUpdateJobStatus_ToProcessing() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Act
        exportJobTracker.updateJobStatus(jobId, ImportStatus.PROCESSING);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(ImportStatus.PROCESSING, jobStatus.get().getStatus());
    }

    @Test
    @DisplayName("Debe actualizar el estado a COMPLETED y establecer endTime")
    void testUpdateJobStatus_ToCompletedSetsEndTime() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Act
        exportJobTracker.updateJobStatus(jobId, ImportStatus.COMPLETED);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(ImportStatus.COMPLETED, jobStatus.get().getStatus());
        assertNotNull(jobStatus.get().getEndTime());
    }

    @Test
    @DisplayName("Debe actualizar el estado a FAILED y establecer endTime")
    void testUpdateJobStatus_ToFailedSetsEndTime() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Act
        exportJobTracker.updateJobStatus(jobId, ImportStatus.FAILED);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(ImportStatus.FAILED, jobStatus.get().getStatus());
        assertNotNull(jobStatus.get().getEndTime());
    }

    @Test
    @DisplayName("Debe no hacer nada si el trabajo no existe al actualizar estado")
    void testUpdateJobStatus_WithNonExistentJob() {
        // Act & Assert
        assertDoesNotThrow(() -> exportJobTracker.updateJobStatus("non-existent-id", ImportStatus.COMPLETED));
    }

    @Test
    @DisplayName("Debe no establecer endTime para estado PROCESSING")
    void testUpdateJobStatus_ProcessingDoesNotSetEndTime() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Act
        exportJobTracker.updateJobStatus(jobId, ImportStatus.PROCESSING);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertNull(jobStatus.get().getEndTime());
    }

    // ========== updateProgress Tests ==========

    @Test
    @DisplayName("Debe actualizar el progreso a 50%")
    void testUpdateProgress_ToFiftyPercent() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Act
        exportJobTracker.updateProgress(jobId, 50);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(50, jobStatus.get().getProgress());
    }

    @Test
    @DisplayName("Debe actualizar el progreso a 100%")
    void testUpdateProgress_ToHundredPercent() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Act
        exportJobTracker.updateProgress(jobId, 100);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(100, jobStatus.get().getProgress());
    }

    @Test
    @DisplayName("Debe no hacer nada si el trabajo no existe al actualizar progreso")
    void testUpdateProgress_WithNonExistentJob() {
        // Act & Assert
        assertDoesNotThrow(() -> exportJobTracker.updateProgress("non-existent-id", 50));
    }

    @Test
    @DisplayName("Debe actualizar el progreso múltiples veces")
    void testUpdateProgress_MultipleTimes() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Act
        exportJobTracker.updateProgress(jobId, 25);
        exportJobTracker.updateProgress(jobId, 50);
        exportJobTracker.updateProgress(jobId, 75);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(75, jobStatus.get().getProgress());
    }

    // ========== updateTotalRecords Tests ==========

    @Test
    @DisplayName("Debe actualizar el total de registros")
    void testUpdateTotalRecords_WithValidValue() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);
        Integer totalRecords = 1000;

        // Act
        exportJobTracker.updateTotalRecords(jobId, totalRecords);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(totalRecords, jobStatus.get().getTotalRecords());
    }

    @Test
    @DisplayName("Debe actualizar el total de registros a cero")
    void testUpdateTotalRecords_WithZero() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Act
        exportJobTracker.updateTotalRecords(jobId, 0);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(0, jobStatus.get().getTotalRecords());
    }

    @Test
    @DisplayName("Debe no hacer nada si el trabajo no existe al actualizar total de registros")
    void testUpdateTotalRecords_WithNonExistentJob() {
        // Act & Assert
        assertDoesNotThrow(() -> exportJobTracker.updateTotalRecords("non-existent-id", 100));
    }

    // ========== setFileData Tests ==========

    @Test
    @DisplayName("Debe almacenar los datos del archivo")
    void testSetFileData_WithValidData() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);
        byte[] fileData = new byte[]{1, 2, 3, 4, 5};

        // Act
        exportJobTracker.setFileData(jobId, fileData);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertArrayEquals(fileData, jobStatus.get().getFileData());
    }

    @Test
    @DisplayName("Debe almacenar datos de archivo vacíos")
    void testSetFileData_WithEmptyData() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);
        byte[] fileData = new byte[0];

        // Act
        exportJobTracker.setFileData(jobId, fileData);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertArrayEquals(fileData, jobStatus.get().getFileData());
    }

    @Test
    @DisplayName("Debe no hacer nada si el trabajo no existe al establecer datos de archivo")
    void testSetFileData_WithNonExistentJob() {
        // Arrange
        byte[] fileData = new byte[]{1, 2, 3};

        // Act & Assert
        assertDoesNotThrow(() -> exportJobTracker.setFileData("non-existent-id", fileData));
    }

    // ========== setErrorMessage Tests ==========

    @Test
    @DisplayName("Debe almacenar un mensaje de error")
    void testSetErrorMessage_WithValidMessage() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);
        String errorMessage = "Error procesando archivo";

        // Act
        exportJobTracker.setErrorMessage(jobId, errorMessage);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(errorMessage, jobStatus.get().getErrorMessage());
    }

    @Test
    @DisplayName("Debe almacenar un mensaje de error vacío")
    void testSetErrorMessage_WithEmptyMessage() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Act
        exportJobTracker.setErrorMessage(jobId, "");

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals("", jobStatus.get().getErrorMessage());
    }

    @Test
    @DisplayName("Debe no hacer nada si el trabajo no existe al establecer mensaje de error")
    void testSetErrorMessage_WithNonExistentJob() {
        // Act & Assert
        assertDoesNotThrow(() -> exportJobTracker.setErrorMessage("non-existent-id", "Error"));
    }

    // ========== removeJob Tests ==========

    @Test
    @DisplayName("Debe eliminar un trabajo existente")
    void testRemoveJob_WithExistingJob() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Act
        exportJobTracker.removeJob(jobId);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertFalse(jobStatus.isPresent());
    }

    @Test
    @DisplayName("Debe no hacer nada al eliminar un trabajo inexistente")
    void testRemoveJob_WithNonExistentJob() {
        // Act & Assert
        assertDoesNotThrow(() -> exportJobTracker.removeJob("non-existent-id"));
    }

    @Test
    @DisplayName("Debe poder crear un nuevo trabajo después de eliminar uno")
    void testRemoveJob_AllowsCreatingNewJobAfterRemoval() {
        // Arrange
        String jobId1 = exportJobTracker.createJob(entId, fileName);
        exportJobTracker.removeJob(jobId1);

        // Act
        String jobId2 = exportJobTracker.createJob(entId, fileName);

        // Assert
        Optional<ExportJobStatus> jobStatus1 = exportJobTracker.getJobStatus(jobId1);
        Optional<ExportJobStatus> jobStatus2 = exportJobTracker.getJobStatus(jobId2);
        assertFalse(jobStatus1.isPresent());
        assertTrue(jobStatus2.isPresent());
    }

    // ========== Integration Scenario Tests ==========

    @Test
    @DisplayName("Debe manejar el ciclo completo de un trabajo exitoso")
    void testCompleteJobLifecycle_Success() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);

        // Act
        exportJobTracker.updateTotalRecords(jobId, 100);
        exportJobTracker.updateJobStatus(jobId, ImportStatus.PROCESSING);
        exportJobTracker.updateProgress(jobId, 50);
        exportJobTracker.updateProgress(jobId, 100);
        exportJobTracker.setFileData(jobId, new byte[]{1, 2, 3});
        exportJobTracker.updateJobStatus(jobId, ImportStatus.COMPLETED);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(ImportStatus.COMPLETED, jobStatus.get().getStatus());
        assertEquals(100, jobStatus.get().getTotalRecords());
        assertEquals(100, jobStatus.get().getProgress());
        assertNotNull(jobStatus.get().getFileData());
        assertNotNull(jobStatus.get().getEndTime());
    }

    @Test
    @DisplayName("Debe manejar el ciclo completo de un trabajo fallido")
    void testCompleteJobLifecycle_Failure() {
        // Arrange
        String jobId = exportJobTracker.createJob(entId, fileName);
        String errorMessage = "Error de conexión a la base de datos";

        // Act
        exportJobTracker.updateJobStatus(jobId, ImportStatus.PROCESSING);
        exportJobTracker.updateProgress(jobId, 30);
        exportJobTracker.setErrorMessage(jobId, errorMessage);
        exportJobTracker.updateJobStatus(jobId, ImportStatus.FAILED);

        // Assert
        Optional<ExportJobStatus> jobStatus = exportJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(ImportStatus.FAILED, jobStatus.get().getStatus());
        assertEquals(errorMessage, jobStatus.get().getErrorMessage());
        assertNotNull(jobStatus.get().getEndTime());
    }

    @Test
    @DisplayName("Debe manejar múltiples trabajos concurrentes")
    void testMultipleConcurrentJobs() {
        // Act
        String jobId1 = exportJobTracker.createJob("ENT001", "file1.xlsx");
        String jobId2 = exportJobTracker.createJob("ENT002", "file2.xlsx");
        String jobId3 = exportJobTracker.createJob("ENT003", "file3.xlsx");

        exportJobTracker.updateJobStatus(jobId1, ImportStatus.PROCESSING);
        exportJobTracker.updateJobStatus(jobId2, ImportStatus.COMPLETED);
        exportJobTracker.updateProgress(jobId3, 50);

        // Assert
        Optional<ExportJobStatus> job1 = exportJobTracker.getJobStatus(jobId1);
        Optional<ExportJobStatus> job2 = exportJobTracker.getJobStatus(jobId2);
        Optional<ExportJobStatus> job3 = exportJobTracker.getJobStatus(jobId3);

        assertTrue(job1.isPresent());
        assertTrue(job2.isPresent());
        assertTrue(job3.isPresent());

        assertEquals(ImportStatus.PROCESSING, job1.get().getStatus());
        assertEquals(ImportStatus.COMPLETED, job2.get().getStatus());
        assertEquals(50, job3.get().getProgress());
    }

    @Test
    @DisplayName("Debe actualizar correctamente un trabajo específico sin afectar otros")
    void testUpdateSpecificJob_DoesNotAffectOthers() {
        // Arrange
        String jobId1 = exportJobTracker.createJob("ENT001", "file1.xlsx");
        String jobId2 = exportJobTracker.createJob("ENT002", "file2.xlsx");

        // Act
        exportJobTracker.updateProgress(jobId1, 75);
        exportJobTracker.updateJobStatus(jobId1, ImportStatus.COMPLETED);

        // Assert
        Optional<ExportJobStatus> job1 = exportJobTracker.getJobStatus(jobId1);
        Optional<ExportJobStatus> job2 = exportJobTracker.getJobStatus(jobId2);

        assertTrue(job1.isPresent());
        assertTrue(job2.isPresent());

        assertEquals(75, job1.get().getProgress());
        assertEquals(ImportStatus.COMPLETED, job1.get().getStatus());

        assertEquals(0, job2.get().getProgress());
        assertEquals(ImportStatus.PENDING, job2.get().getStatus());
    }
}
