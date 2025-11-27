package com.thirdsmanagement.thirds.unit.application.service.importExport;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.application.service.importExport.ImportJobTracker;
import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;
import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import com.thirdsmanagement.thirds.domain.model.ImportErrorDetail;
import com.thirdsmanagement.thirds.domain.model.ImportJobStatus;

/**
 * Tests unitarios para ImportJobTracker
 */
class ImportJobTrackerUnitTest {

    private ImportJobTracker importJobTracker;
    private String entId;
    private String fileName;

    @BeforeEach
    void setUp() {
        importJobTracker = new ImportJobTracker();
        entId = "ENT001";
        fileName = "terceros_import.xlsx";
    }

    // ========== createJob Tests ==========

    @Test
    @DisplayName("Debe crear un nuevo job con UUID único")
    void testCreateJob_GeneratesUniqueJobId() {
        // Act
        String jobId = importJobTracker.createJob(entId, fileName);

        // Assert
        assertNotNull(jobId);
        assertFalse(jobId.isEmpty());
    }

    @Test
    @DisplayName("Debe crear un job con estado PENDING")
    void testCreateJob_WithPendingStatus() {
        // Act
        String jobId = importJobTracker.createJob(entId, fileName);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(ImportStatus.PENDING, jobStatus.get().getStatus());
    }

    @Test
    @DisplayName("Debe inicializar el progreso en 0")
    void testCreateJob_WithZeroProgress() {
        // Act
        String jobId = importJobTracker.createJob(entId, fileName);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(0, jobStatus.get().getProgress());
    }

    @Test
    @DisplayName("Debe inicializar todas las métricas en 0")
    void testCreateJob_InitializesMetricsToZero() {
        // Act
        String jobId = importJobTracker.createJob(entId, fileName);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(0, jobStatus.get().getTotalRecords());
        assertEquals(0, jobStatus.get().getSuccessfulImports());
        assertEquals(0, jobStatus.get().getFailedImports());
        assertEquals(0, jobStatus.get().getDuplicatesSkipped());
    }

    @Test
    @DisplayName("Debe almacenar entId y fileName correctamente")
    void testCreateJob_StoresEntIdAndFileName() {
        // Act
        String jobId = importJobTracker.createJob(entId, fileName);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(entId, jobStatus.get().getEntId());
        assertEquals(fileName, jobStatus.get().getFileName());
    }

    @Test
    @DisplayName("Debe establecer la hora de inicio al crear el job")
    void testCreateJob_SetsStartTime() {
        // Act
        String jobId = importJobTracker.createJob(entId, fileName);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertNotNull(jobStatus.get().getStartTime());
    }

    @Test
    @DisplayName("Debe generar IDs únicos para múltiples jobs")
    void testCreateJob_GeneratesUniqueIdsForMultipleJobs() {
        // Act
        String jobId1 = importJobTracker.createJob(entId, "file1.xlsx");
        String jobId2 = importJobTracker.createJob(entId, "file2.xlsx");
        String jobId3 = importJobTracker.createJob(entId, "file3.xlsx");

        // Assert
        assertNotEquals(jobId1, jobId2);
        assertNotEquals(jobId2, jobId3);
        assertNotEquals(jobId1, jobId3);
    }

    @Test
    @DisplayName("Debe incrementar el contador de jobs activos")
    void testCreateJob_IncrementsActiveJobsCount() {
        // Arrange
        int initialCount = importJobTracker.getActiveJobsCount();

        // Act
        importJobTracker.createJob(entId, fileName);

        // Assert
        assertEquals(initialCount + 1, importJobTracker.getActiveJobsCount());
    }

    // ========== updateJobStatus Tests ==========

    @Test
    @DisplayName("Debe actualizar el estado a PROCESSING")
    void testUpdateJobStatus_ToProcessing() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);

        // Act
        importJobTracker.updateJobStatus(jobId, ImportStatus.PROCESSING);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(ImportStatus.PROCESSING, jobStatus.get().getStatus());
    }

    @Test
    @DisplayName("Debe actualizar el estado a COMPLETED y establecer endTime")
    void testUpdateJobStatus_ToCompletedSetsEndTime() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);

        // Act
        importJobTracker.updateJobStatus(jobId, ImportStatus.COMPLETED);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(ImportStatus.COMPLETED, jobStatus.get().getStatus());
        assertNotNull(jobStatus.get().getEndTime());
        assertEquals(100, jobStatus.get().getProgress());
    }

    @Test
    @DisplayName("Debe actualizar el estado a FAILED y establecer endTime")
    void testUpdateJobStatus_ToFailedSetsEndTime() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);

        // Act
        importJobTracker.updateJobStatus(jobId, ImportStatus.FAILED);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(ImportStatus.FAILED, jobStatus.get().getStatus());
        assertNotNull(jobStatus.get().getEndTime());
        assertEquals(100, jobStatus.get().getProgress());
    }

    @Test
    @DisplayName("Debe establecer progreso a 100 cuando el estado es finalizado")
    void testUpdateJobStatus_SetsProgressTo100WhenFinished() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);
        importJobTracker.updateProgress(jobId, 50);

        // Act
        importJobTracker.updateJobStatus(jobId, ImportStatus.COMPLETED);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(100, jobStatus.get().getProgress());
    }

    @Test
    @DisplayName("Debe no hacer nada si el job no existe al actualizar estado")
    void testUpdateJobStatus_WithNonExistentJob() {
        // Act & Assert
        assertDoesNotThrow(() -> importJobTracker.updateJobStatus("non-existent-id", ImportStatus.COMPLETED));
    }

    @Test
    @DisplayName("Debe no establecer endTime para estado PROCESSING")
    void testUpdateJobStatus_ProcessingDoesNotSetEndTime() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);

        // Act
        importJobTracker.updateJobStatus(jobId, ImportStatus.PROCESSING);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertNull(jobStatus.get().getEndTime());
    }

    // ========== updateJobMetrics Tests ==========

    @Test
    @DisplayName("Debe actualizar todas las métricas del job")
    void testUpdateJobMetrics_UpdatesAllMetrics() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);

        // Act
        importJobTracker.updateJobMetrics(jobId, 1000, 900, 50, 50);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(1000, jobStatus.get().getTotalRecords());
        assertEquals(900, jobStatus.get().getSuccessfulImports());
        assertEquals(50, jobStatus.get().getFailedImports());
        assertEquals(50, jobStatus.get().getDuplicatesSkipped());
    }

    @Test
    @DisplayName("Debe actualizar métricas con valores cero")
    void testUpdateJobMetrics_WithZeroValues() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);

        // Act
        importJobTracker.updateJobMetrics(jobId, 0, 0, 0, 0);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(0, jobStatus.get().getTotalRecords());
        assertEquals(0, jobStatus.get().getSuccessfulImports());
    }

    @Test
    @DisplayName("Debe no hacer nada si el job no existe al actualizar métricas")
    void testUpdateJobMetrics_WithNonExistentJob() {
        // Act & Assert
        assertDoesNotThrow(() -> importJobTracker.updateJobMetrics("non-existent-id", 100, 50, 25, 25));
    }

    @Test
    @DisplayName("Debe actualizar métricas múltiples veces")
    void testUpdateJobMetrics_MultipleTimes() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);

        // Act
        importJobTracker.updateJobMetrics(jobId, 500, 400, 50, 50);
        importJobTracker.updateJobMetrics(jobId, 1000, 900, 50, 50);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(1000, jobStatus.get().getTotalRecords());
        assertEquals(900, jobStatus.get().getSuccessfulImports());
    }

    // ========== updateProgress Tests ==========

    @Test
    @DisplayName("Debe actualizar el progreso a 50%")
    void testUpdateProgress_ToFiftyPercent() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);

        // Act
        importJobTracker.updateProgress(jobId, 50);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(50, jobStatus.get().getProgress());
    }

    @Test
    @DisplayName("Debe limitar el progreso máximo a 100")
    void testUpdateProgress_LimitsToMaximum100() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);

        // Act
        importJobTracker.updateProgress(jobId, 150);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(100, jobStatus.get().getProgress());
    }

    @Test
    @DisplayName("Debe limitar el progreso mínimo a 0")
    void testUpdateProgress_LimitsToMinimum0() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);

        // Act
        importJobTracker.updateProgress(jobId, -10);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(0, jobStatus.get().getProgress());
    }

    @Test
    @DisplayName("Debe no hacer nada si el job no existe al actualizar progreso")
    void testUpdateProgress_WithNonExistentJob() {
        // Act & Assert
        assertDoesNotThrow(() -> importJobTracker.updateProgress("non-existent-id", 50));
    }

    @Test
    @DisplayName("Debe actualizar el progreso múltiples veces")
    void testUpdateProgress_MultipleTimes() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);

        // Act
        importJobTracker.updateProgress(jobId, 25);
        importJobTracker.updateProgress(jobId, 50);
        importJobTracker.updateProgress(jobId, 75);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(75, jobStatus.get().getProgress());
    }

    // ========== addErrors Tests ==========

    @Test
    @DisplayName("Debe agregar errores al job")
    void testAddErrors_AddsErrorsToJob() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);
        List<ImportErrorDetail> errors = createErrorList();

        // Act
        importJobTracker.addErrors(jobId, errors);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertNotNull(jobStatus.get().getErrors());
        assertEquals(2, jobStatus.get().getErrors().size());
    }

    @Test
    @DisplayName("Debe agregar errores a una lista existente")
    void testAddErrors_AppendsToExistingErrors() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);
        List<ImportErrorDetail> firstErrors = createErrorList();
        List<ImportErrorDetail> secondErrors = createAdditionalErrorList();

        // Act
        importJobTracker.addErrors(jobId, firstErrors);
        importJobTracker.addErrors(jobId, secondErrors);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(4, jobStatus.get().getErrors().size());
    }

    @Test
    @DisplayName("Debe no hacer nada si el job no existe al agregar errores")
    void testAddErrors_WithNonExistentJob() {
        // Arrange
        List<ImportErrorDetail> errors = createErrorList();

        // Act & Assert
        assertDoesNotThrow(() -> importJobTracker.addErrors("non-existent-id", errors));
    }

    @Test
    @DisplayName("Debe manejar lista vacía de errores")
    void testAddErrors_WithEmptyList() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);
        List<ImportErrorDetail> emptyErrors = Collections.emptyList();

        // Act
        importJobTracker.addErrors(jobId, emptyErrors);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
    }

    // ========== getJobStatus Tests ==========

    @Test
    @DisplayName("Debe obtener el estado de un job existente")
    void testGetJobStatus_WithExistingJob() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);

        // Act
        Optional<ImportJobStatus> result = importJobTracker.getJobStatus(jobId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(jobId, result.get().getJobId());
    }

    @Test
    @DisplayName("Debe retornar Optional vacío para job inexistente")
    void testGetJobStatus_WithNonExistentJob() {
        // Act
        Optional<ImportJobStatus> result = importJobTracker.getJobStatus("non-existent-id");

        // Assert
        assertFalse(result.isPresent());
    }

    // ========== removeJob Tests ==========

    @Test
    @DisplayName("Debe eliminar un job existente")
    void testRemoveJob_WithExistingJob() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);

        // Act
        importJobTracker.removeJob(jobId);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertFalse(jobStatus.isPresent());
    }

    @Test
    @DisplayName("Debe no hacer nada al eliminar un job inexistente")
    void testRemoveJob_WithNonExistentJob() {
        // Act & Assert
        assertDoesNotThrow(() -> importJobTracker.removeJob("non-existent-id"));
    }

    @Test
    @DisplayName("Debe decrementar el contador de jobs activos")
    void testRemoveJob_DecrementsActiveJobsCount() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);
        int countAfterCreate = importJobTracker.getActiveJobsCount();

        // Act
        importJobTracker.removeJob(jobId);

        // Assert
        assertEquals(countAfterCreate - 1, importJobTracker.getActiveJobsCount());
    }

    // ========== getActiveJobsCount Tests ==========

    @Test
    @DisplayName("Debe retornar 0 cuando no hay jobs")
    void testGetActiveJobsCount_WithNoJobs() {
        // Arrange
        ImportJobTracker freshTracker = new ImportJobTracker();

        // Act
        int count = freshTracker.getActiveJobsCount();

        // Assert
        assertEquals(0, count);
    }

    @Test
    @DisplayName("Debe retornar el número correcto de jobs activos")
    void testGetActiveJobsCount_WithMultipleJobs() {
        // Arrange
        importJobTracker.createJob("ENT001", "file1.xlsx");
        importJobTracker.createJob("ENT002", "file2.xlsx");
        importJobTracker.createJob("ENT003", "file3.xlsx");

        // Act
        int count = importJobTracker.getActiveJobsCount();

        // Assert
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Debe actualizar el contador después de eliminar jobs")
    void testGetActiveJobsCount_AfterRemovingJobs() {
        // Arrange
        String jobId1 = importJobTracker.createJob("ENT001", "file1.xlsx");
        String jobId2 = importJobTracker.createJob("ENT002", "file2.xlsx");
        importJobTracker.createJob("ENT003", "file3.xlsx");

        // Act
        importJobTracker.removeJob(jobId1);
        importJobTracker.removeJob(jobId2);
        int count = importJobTracker.getActiveJobsCount();

        // Assert
        assertEquals(1, count);
    }

    // ========== Integration Scenario Tests ==========

    @Test
    @DisplayName("Debe manejar el ciclo completo de un job exitoso")
    void testCompleteJobLifecycle_Success() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);

        // Act
        importJobTracker.updateJobStatus(jobId, ImportStatus.PROCESSING);
        importJobTracker.updateProgress(jobId, 25);
        importJobTracker.updateJobMetrics(jobId, 100, 90, 5, 5);
        importJobTracker.updateProgress(jobId, 50);
        importJobTracker.updateProgress(jobId, 100);
        importJobTracker.updateJobStatus(jobId, ImportStatus.COMPLETED);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(ImportStatus.COMPLETED, jobStatus.get().getStatus());
        assertEquals(100, jobStatus.get().getTotalRecords());
        assertEquals(90, jobStatus.get().getSuccessfulImports());
        assertEquals(100, jobStatus.get().getProgress());
        assertNotNull(jobStatus.get().getEndTime());
    }

    @Test
    @DisplayName("Debe manejar el ciclo completo de un job fallido con errores")
    void testCompleteJobLifecycle_FailureWithErrors() {
        // Arrange
        String jobId = importJobTracker.createJob(entId, fileName);
        List<ImportErrorDetail> errors = createErrorList();

        // Act
        importJobTracker.updateJobStatus(jobId, ImportStatus.PROCESSING);
        importJobTracker.updateProgress(jobId, 30);
        importJobTracker.updateJobMetrics(jobId, 100, 50, 50, 0);
        importJobTracker.addErrors(jobId, errors);
        importJobTracker.updateJobStatus(jobId, ImportStatus.FAILED);

        // Assert
        Optional<ImportJobStatus> jobStatus = importJobTracker.getJobStatus(jobId);
        assertTrue(jobStatus.isPresent());
        assertEquals(ImportStatus.FAILED, jobStatus.get().getStatus());
        assertEquals(50, jobStatus.get().getFailedImports());
        assertEquals(2, jobStatus.get().getErrors().size());
        assertNotNull(jobStatus.get().getEndTime());
    }

    @Test
    @DisplayName("Debe manejar múltiples jobs concurrentes")
    void testMultipleConcurrentJobs() {
        // Act
        String jobId1 = importJobTracker.createJob("ENT001", "file1.xlsx");
        String jobId2 = importJobTracker.createJob("ENT002", "file2.xlsx");
        String jobId3 = importJobTracker.createJob("ENT003", "file3.xlsx");

        importJobTracker.updateJobStatus(jobId1, ImportStatus.PROCESSING);
        importJobTracker.updateJobStatus(jobId2, ImportStatus.COMPLETED);
        importJobTracker.updateProgress(jobId3, 50);

        // Assert
        Optional<ImportJobStatus> job1 = importJobTracker.getJobStatus(jobId1);
        Optional<ImportJobStatus> job2 = importJobTracker.getJobStatus(jobId2);
        Optional<ImportJobStatus> job3 = importJobTracker.getJobStatus(jobId3);

        assertTrue(job1.isPresent());
        assertTrue(job2.isPresent());
        assertTrue(job3.isPresent());

        assertEquals(ImportStatus.PROCESSING, job1.get().getStatus());
        assertEquals(ImportStatus.COMPLETED, job2.get().getStatus());
        assertEquals(50, job3.get().getProgress());
        assertEquals(3, importJobTracker.getActiveJobsCount());
    }

    @Test
    @DisplayName("Debe actualizar correctamente un job específico sin afectar otros")
    void testUpdateSpecificJob_DoesNotAffectOthers() {
        // Arrange
        String jobId1 = importJobTracker.createJob("ENT001", "file1.xlsx");
        String jobId2 = importJobTracker.createJob("ENT002", "file2.xlsx");

        // Act
        importJobTracker.updateProgress(jobId1, 75);
        importJobTracker.updateJobMetrics(jobId1, 100, 75, 25, 0);
        importJobTracker.updateJobStatus(jobId1, ImportStatus.COMPLETED);

        // Assert
        Optional<ImportJobStatus> job1 = importJobTracker.getJobStatus(jobId1);
        Optional<ImportJobStatus> job2 = importJobTracker.getJobStatus(jobId2);

        assertTrue(job1.isPresent());
        assertTrue(job2.isPresent());

        assertEquals(100, job1.get().getTotalRecords());
        assertEquals(ImportStatus.COMPLETED, job1.get().getStatus());

        assertEquals(0, job2.get().getTotalRecords());
        assertEquals(ImportStatus.PENDING, job2.get().getStatus());
    }

    @Test
    @DisplayName("Debe poder crear nuevo job después de eliminar uno")
    void testCreateJob_AfterRemovingJob() {
        // Arrange
        String jobId1 = importJobTracker.createJob(entId, fileName);
        importJobTracker.removeJob(jobId1);

        // Act
        String jobId2 = importJobTracker.createJob(entId, fileName);

        // Assert
        Optional<ImportJobStatus> job1 = importJobTracker.getJobStatus(jobId1);
        Optional<ImportJobStatus> job2 = importJobTracker.getJobStatus(jobId2);

        assertFalse(job1.isPresent());
        assertTrue(job2.isPresent());
    }

    // ========== Helper Methods ==========

    private List<ImportErrorDetail> createErrorList() {
        List<ImportErrorDetail> errors = new ArrayList<>();
        errors.add(createError(1, ImportErrorType.VALIDATION_ERROR, "Error validación"));
        errors.add(createError(2, ImportErrorType.DUPLICATE_ERROR, "Registro duplicado"));
        return errors;
    }

    private List<ImportErrorDetail> createAdditionalErrorList() {
        List<ImportErrorDetail> errors = new ArrayList<>();
        errors.add(createError(3, ImportErrorType.REFERENCE_ERROR, "Campo requerido faltante"));
        errors.add(createError(4, ImportErrorType.FORMAT_ERROR, "Formato inválido"));
        return errors;
    }

    private ImportErrorDetail createError(int row, ImportErrorType errorType, String message) {
        return ImportErrorDetail.builder()
                .rowNumber(row)
                .errorType(errorType)
                .errorMessage(message)
                .build();
    }
}
