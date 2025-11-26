package com.thirdsmanagement.thirds.unit.application.service.importExport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import com.thirdsmanagement.thirds.application.service.importExport.AsyncImportProcessor;
import com.thirdsmanagement.thirds.application.service.importExport.ImportJobTracker;
import com.thirdsmanagement.thirds.application.service.importExport.ImportThirdService;
import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdImportException;
import com.thirdsmanagement.thirds.domain.model.ImportJobStatus;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdImportRequest;

/**
 * Tests unitarios para ImportThirdService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImportThirdServiceUnitTest {

    @Mock
    private ImportJobTracker jobTracker;

    @Mock
    private AsyncImportProcessor asyncImportProcessor;

    @InjectMocks
    private ImportThirdService importThirdService;

    @Mock
    private MultipartFile multipartFile;

    private String entId;
    private String fileName;
    private String jobId;
    private byte[] fileBytes;

    @BeforeEach
    void setUp() {
        entId = "ENT001";
        fileName = "terceros.xlsx";
        jobId = "job-12345";
        fileBytes = new byte[]{1, 2, 3, 4, 5};
    }

    // ========== importThirdsFromExcel Tests ==========

    @Test
    @DisplayName("Debe importar terceros desde Excel exitosamente")
    void testImportThirdsFromExcel_Success() throws IOException {
        // Arrange
        ThirdImportRequest request = createImportRequest();
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(jobTracker.createJob(entId, fileName)).thenReturn(jobId);

        // Act
        String result = importThirdService.importThirdsFromExcel(request);

        // Assert
        assertEquals(jobId, result);
        verify(jobTracker).createJob(entId, fileName);
        verify(multipartFile).getBytes();
        verify(asyncImportProcessor).processImportAsync(fileBytes, entId, fileName, jobId);
    }

    @Test
    @DisplayName("Debe crear el job antes de procesar de forma asíncrona")
    void testImportThirdsFromExcel_CreatesJobBeforeAsyncProcessing() throws IOException {
        // Arrange
        ThirdImportRequest request = createImportRequest();
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(jobTracker.createJob(entId, fileName)).thenReturn(jobId);

        // Act
        importThirdService.importThirdsFromExcel(request);

        // Assert
        verify(jobTracker).createJob(entId, fileName);
        verify(asyncImportProcessor).processImportAsync(eq(fileBytes), eq(entId), eq(fileName), eq(jobId));
    }

    @Test
    @DisplayName("Debe leer el archivo a bytes antes de procesamiento asíncrono")
    void testImportThirdsFromExcel_ReadsFileBytesBeforeAsync() throws IOException {
        // Arrange
        ThirdImportRequest request = createImportRequest();
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(jobTracker.createJob(entId, fileName)).thenReturn(jobId);

        // Act
        importThirdService.importThirdsFromExcel(request);

        // Assert
        verify(multipartFile).getBytes();
        verify(asyncImportProcessor).processImportAsync(fileBytes, entId, fileName, jobId);
    }

    @Test
    @DisplayName("Debe retornar jobId inmediatamente después de crear el trabajo")
    void testImportThirdsFromExcel_ReturnsJobIdImmediately() throws IOException {
        // Arrange
        ThirdImportRequest request = createImportRequest();
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(jobTracker.createJob(entId, fileName)).thenReturn(jobId);

        // Act
        String result = importThirdService.importThirdsFromExcel(request);

        // Assert
        assertNotNull(result);
        assertEquals(jobId, result);
    }

    @Test
    @DisplayName("Debe lanzar ThirdImportException cuando falla la lectura del archivo")
    void testImportThirdsFromExcel_ThrowsExceptionWhenFileReadFails() throws IOException {
        // Arrange
        ThirdImportRequest request = createImportRequest();
        when(multipartFile.getBytes()).thenThrow(new IOException("Error al leer archivo"));
        when(jobTracker.createJob(entId, fileName)).thenReturn(jobId);

        // Act & Assert
        ThirdImportException exception = assertThrows(ThirdImportException.class, () -> {
            importThirdService.importThirdsFromExcel(request);
        });

        assertTrue(exception.getMessage().contains("Error al leer el archivo"));
    }

    @Test
    @DisplayName("Debe lanzar ThirdImportException cuando falla la creación del job")
    void testImportThirdsFromExcel_ThrowsExceptionWhenJobCreationFails() throws IOException {
        // Arrange
        ThirdImportRequest request = createImportRequest();
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(jobTracker.createJob(entId, fileName)).thenThrow(new RuntimeException("Error creando job"));

        // Act & Assert
        assertThrows(ThirdImportException.class, () -> {
            importThirdService.importThirdsFromExcel(request);
        });
    }

    @Test
    @DisplayName("Debe pasar los bytes correctos al procesador asíncrono")
    void testImportThirdsFromExcel_PassesCorrectBytesToAsyncProcessor() throws IOException {
        // Arrange
        byte[] specificBytes = new byte[]{10, 20, 30, 40, 50};
        ThirdImportRequest request = createImportRequest();
        when(multipartFile.getBytes()).thenReturn(specificBytes);
        when(jobTracker.createJob(entId, fileName)).thenReturn(jobId);

        // Act
        importThirdService.importThirdsFromExcel(request);

        // Assert
        verify(asyncImportProcessor).processImportAsync(eq(specificBytes), eq(entId), eq(fileName), eq(jobId));
    }

    @Test
    @DisplayName("Debe manejar archivo con bytes vacíos")
    void testImportThirdsFromExcel_WithEmptyFileBytes() throws IOException {
        // Arrange
        byte[] emptyBytes = new byte[0];
        ThirdImportRequest request = createImportRequest();
        when(multipartFile.getBytes()).thenReturn(emptyBytes);
        when(jobTracker.createJob(entId, fileName)).thenReturn(jobId);

        // Act
        String result = importThirdService.importThirdsFromExcel(request);

        // Assert
        assertEquals(jobId, result);
        verify(asyncImportProcessor).processImportAsync(emptyBytes, entId, fileName, jobId);
    }

    @Test
    @DisplayName("Debe invocar processImportAsync una sola vez")
    void testImportThirdsFromExcel_InvokesAsyncProcessorOnce() throws IOException {
        // Arrange
        ThirdImportRequest request = createImportRequest();
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(jobTracker.createJob(entId, fileName)).thenReturn(jobId);

        // Act
        importThirdService.importThirdsFromExcel(request);

        // Assert
        verify(asyncImportProcessor, times(1)).processImportAsync(any(byte[].class), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe manejar correctamente diferentes nombres de archivo")
    void testImportThirdsFromExcel_WithDifferentFileNames() throws IOException {
        // Arrange
        String customFileName = "importacion_masiva_2024.xlsx";
        ThirdImportRequest request = ThirdImportRequest.builder()
                .entId(entId)
                .fileName(customFileName)
                .excelFile(multipartFile)
                .build();
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(jobTracker.createJob(entId, customFileName)).thenReturn(jobId);

        // Act
        importThirdService.importThirdsFromExcel(request);

        // Assert
        verify(jobTracker).createJob(entId, customFileName);
        verify(asyncImportProcessor).processImportAsync(fileBytes, entId, customFileName, jobId);
    }

    @Test
    @DisplayName("Debe manejar correctamente diferentes entidades")
    void testImportThirdsFromExcel_WithDifferentEntities() throws IOException {
        // Arrange
        String customEntId = "ENT999";
        ThirdImportRequest request = ThirdImportRequest.builder()
                .entId(customEntId)
                .fileName(fileName)
                .excelFile(multipartFile)
                .build();
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(jobTracker.createJob(customEntId, fileName)).thenReturn(jobId);

        // Act
        importThirdService.importThirdsFromExcel(request);

        // Assert
        verify(jobTracker).createJob(customEntId, fileName);
        verify(asyncImportProcessor).processImportAsync(fileBytes, customEntId, fileName, jobId);
    }

    // ========== getImportStatus Tests ==========

    @Test
    @DisplayName("Debe obtener el estado de un job existente")
    void testGetImportStatus_WithExistingJob() {
        // Arrange
        ImportJobStatus jobStatus = createJobStatus(jobId, ImportStatus.PROCESSING);
        when(jobTracker.getJobStatus(jobId)).thenReturn(Optional.of(jobStatus));

        // Act
        Optional<ImportJobStatus> result = importThirdService.getImportStatus(jobId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(jobId, result.get().getJobId());
        assertEquals(ImportStatus.PROCESSING, result.get().getStatus());
        verify(jobTracker).getJobStatus(jobId);
    }

    @Test
    @DisplayName("Debe retornar Optional vacío para job inexistente")
    void testGetImportStatus_WithNonExistentJob() {
        // Arrange
        String nonExistentJobId = "non-existent-job";
        when(jobTracker.getJobStatus(nonExistentJobId)).thenReturn(Optional.empty());

        // Act
        Optional<ImportJobStatus> result = importThirdService.getImportStatus(nonExistentJobId);

        // Assert
        assertFalse(result.isPresent());
        verify(jobTracker).getJobStatus(nonExistentJobId);
    }

    @Test
    @DisplayName("Debe obtener estado PENDING de un job recién creado")
    void testGetImportStatus_WithPendingStatus() {
        // Arrange
        ImportJobStatus jobStatus = createJobStatus(jobId, ImportStatus.PENDING);
        when(jobTracker.getJobStatus(jobId)).thenReturn(Optional.of(jobStatus));

        // Act
        Optional<ImportJobStatus> result = importThirdService.getImportStatus(jobId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ImportStatus.PENDING, result.get().getStatus());
    }

    @Test
    @DisplayName("Debe obtener estado COMPLETED de un job finalizado")
    void testGetImportStatus_WithCompletedStatus() {
        // Arrange
        ImportJobStatus jobStatus = createJobStatus(jobId, ImportStatus.COMPLETED);
        when(jobTracker.getJobStatus(jobId)).thenReturn(Optional.of(jobStatus));

        // Act
        Optional<ImportJobStatus> result = importThirdService.getImportStatus(jobId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ImportStatus.COMPLETED, result.get().getStatus());
    }

    @Test
    @DisplayName("Debe obtener estado FAILED de un job con error")
    void testGetImportStatus_WithFailedStatus() {
        // Arrange
        ImportJobStatus jobStatus = createJobStatus(jobId, ImportStatus.FAILED);
        when(jobTracker.getJobStatus(jobId)).thenReturn(Optional.of(jobStatus));

        // Act
        Optional<ImportJobStatus> result = importThirdService.getImportStatus(jobId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ImportStatus.FAILED, result.get().getStatus());
    }

    @Test
    @DisplayName("Debe delegar la consulta al jobTracker")
    void testGetImportStatus_DelegatesToJobTracker() {
        // Arrange
        when(jobTracker.getJobStatus(jobId)).thenReturn(Optional.empty());

        // Act
        importThirdService.getImportStatus(jobId);

        // Assert
        verify(jobTracker, times(1)).getJobStatus(jobId);
    }

    @Test
    @DisplayName("Debe retornar el mismo Optional que el jobTracker")
    void testGetImportStatus_ReturnsSameOptionalFromTracker() {
        // Arrange
        ImportJobStatus jobStatus = createJobStatus(jobId, ImportStatus.PROCESSING);
        Optional<ImportJobStatus> expectedOptional = Optional.of(jobStatus);
        when(jobTracker.getJobStatus(jobId)).thenReturn(expectedOptional);

        // Act
        Optional<ImportJobStatus> result = importThirdService.getImportStatus(jobId);

        // Assert
        assertEquals(expectedOptional, result);
    }

    @Test
    @DisplayName("Debe obtener estado con progreso del job")
    void testGetImportStatus_WithProgress() {
        // Arrange
        ImportJobStatus jobStatus = createJobStatusWithProgress(jobId, ImportStatus.PROCESSING, 75);
        when(jobTracker.getJobStatus(jobId)).thenReturn(Optional.of(jobStatus));

        // Act
        Optional<ImportJobStatus> result = importThirdService.getImportStatus(jobId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(75, result.get().getProgress());
    }

    @Test
    @DisplayName("Debe obtener estado con total de registros")
    void testGetImportStatus_WithTotalRecords() {
        // Arrange
        ImportJobStatus jobStatus = createJobStatusWithRecords(jobId, ImportStatus.PROCESSING, 1000);
        when(jobTracker.getJobStatus(jobId)).thenReturn(Optional.of(jobStatus));

        // Act
        Optional<ImportJobStatus> result = importThirdService.getImportStatus(jobId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1000, result.get().getTotalRecords());
    }

    // ========== Integration Scenario Tests ==========

    @Test
    @DisplayName("Debe ejecutar flujo completo de importación y consulta de estado")
    void testCompleteImportFlow_ImportAndCheckStatus() throws IOException {
        // Arrange
        ThirdImportRequest request = createImportRequest();
        ImportJobStatus jobStatus = createJobStatus(jobId, ImportStatus.PROCESSING);
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(jobTracker.createJob(entId, fileName)).thenReturn(jobId);
        when(jobTracker.getJobStatus(jobId)).thenReturn(Optional.of(jobStatus));

        // Act
        String createdJobId = importThirdService.importThirdsFromExcel(request);
        Optional<ImportJobStatus> status = importThirdService.getImportStatus(createdJobId);

        // Assert
        assertEquals(jobId, createdJobId);
        assertTrue(status.isPresent());
        assertEquals(ImportStatus.PROCESSING, status.get().getStatus());
    }

    @Test
    @DisplayName("Debe manejar múltiples importaciones simultáneas")
    void testMultipleSimultaneousImports() throws IOException {
        // Arrange
        ThirdImportRequest request1 = createImportRequest();
        ThirdImportRequest request2 = createImportRequestWithCustomData("ENT002", "file2.xlsx");
        
        String jobId1 = "job-1";
        String jobId2 = "job-2";
        
        when(multipartFile.getBytes()).thenReturn(fileBytes);
        when(jobTracker.createJob("ENT001", "terceros.xlsx")).thenReturn(jobId1);
        when(jobTracker.createJob("ENT002", "file2.xlsx")).thenReturn(jobId2);

        // Act
        String result1 = importThirdService.importThirdsFromExcel(request1);
        String result2 = importThirdService.importThirdsFromExcel(request2);

        // Assert
        assertEquals(jobId1, result1);
        assertEquals(jobId2, result2);
        assertNotEquals(result1, result2);
        verify(asyncImportProcessor, times(2)).processImportAsync(any(byte[].class), anyString(), anyString(), anyString());
    }

    // ========== Helper Methods ==========

    private ThirdImportRequest createImportRequest() {
        return ThirdImportRequest.builder()
                .entId(entId)
                .fileName(fileName)
                .excelFile(multipartFile)
                .build();
    }

    private ThirdImportRequest createImportRequestWithCustomData(String customEntId, String customFileName) {
        return ThirdImportRequest.builder()
                .entId(customEntId)
                .fileName(customFileName)
                .excelFile(multipartFile)
                .build();
    }

    private ImportJobStatus createJobStatus(String jobId, ImportStatus status) {
        return ImportJobStatus.builder()
                .jobId(jobId)
                .entId(entId)
                .fileName(fileName)
                .status(status)
                .build();
    }

    private ImportJobStatus createJobStatusWithProgress(String jobId, ImportStatus status, Integer progress) {
        return ImportJobStatus.builder()
                .jobId(jobId)
                .entId(entId)
                .fileName(fileName)
                .status(status)
                .progress(progress)
                .build();
    }

    private ImportJobStatus createJobStatusWithRecords(String jobId, ImportStatus status, Integer totalRecords) {
        return ImportJobStatus.builder()
                .jobId(jobId)
                .entId(entId)
                .fileName(fileName)
                .status(status)
                .totalRecords(totalRecords)
                .build();
    }
}
