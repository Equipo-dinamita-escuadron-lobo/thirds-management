package com.thirdsmanagement.thirds.unit.application.service.importExport;

import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.application.service.importExport.AsyncExportProcessor;
import com.thirdsmanagement.thirds.application.service.importExport.ExcelValidationService;
import com.thirdsmanagement.thirds.application.service.importExport.ExportJobTracker;
import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdExportRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AsyncExportProcessor
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AsyncExportProcessorUnitTest {

    @Mock
    private ThirdOutputPort thirdOutputPort;

    @Mock
    private ExportJobTracker jobTracker;

    @Mock
    private ExcelValidationService excelValidationService;

    private AsyncExportProcessor asyncExportProcessor;

    private ThirdExportRequest exportRequest;
    private String jobId;
    private List<Third> sampleThirds;

    @BeforeEach
    void setUp() {
        asyncExportProcessor = new AsyncExportProcessor(thirdOutputPort, jobTracker, excelValidationService);

        // Configurar datos de prueba
        exportRequest = new ThirdExportRequest();
        exportRequest.setEntId("TEST_ENT");
        jobId = "test-job-123";

        // Crear thirds de prueba
        sampleThirds = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Third third = Third.builder()
                    .thId((long) i)
                    .entId("TEST_ENT")
                    .build();
            sampleThirds.add(third);
        }
    }

    // ===============================
    // ESCENARIOS DE ÉXITO
    // ===============================

    @Test
    @DisplayName("Debe procesar exportación exitosamente con datos activos")
    void testProcessExportAsync_WithActiveData_Success() throws Exception {
        // Arrange
        exportRequest.setStatus(true);
        Page<Third> page = new PageImpl<>(sampleThirds, PageRequest.of(0, 5000), sampleThirds.size());

        when(thirdOutputPort.getAllThirdsByStateForExport("TEST_ENT", true, PageRequest.of(0, 5000)))
            .thenReturn(page);
        when(thirdOutputPort.getAllThirdsByStateForExport("TEST_ENT", true, PageRequest.of(1, 5000)))
            .thenReturn(Page.empty());

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.PROCESSING);
        verify(jobTracker).updateProgress(jobId, 10);
        verify(jobTracker).updateTotalRecords(jobId, 3);
        verify(jobTracker).updateProgress(jobId, 50);
        verify(jobTracker).updateProgress(jobId, 90);
        verify(jobTracker).setFileData(eq(jobId), any(byte[].class));
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.COMPLETED);
        verify(jobTracker).updateProgress(jobId, 100);
        verify(excelValidationService).createReferenceDataSheet(any(), eq("TEST_ENT"));
    }

    @Test
    @DisplayName("Debe procesar exportación exitosamente con datos inactivos")
    void testProcessExportAsync_WithInactiveData_Success() throws Exception {
        // Arrange
        exportRequest.setStatus(false); // Inactivos
        Page<Third> page = new PageImpl<>(sampleThirds, PageRequest.of(0, 5000), sampleThirds.size());

        when(thirdOutputPort.getAllThirdsByStateForExport("TEST_ENT", false, PageRequest.of(0, 5000)))
            .thenReturn(page);
        when(thirdOutputPort.getAllThirdsByStateForExport("TEST_ENT", false, PageRequest.of(1, 5000)))
            .thenReturn(Page.empty());

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.PROCESSING);
        verify(thirdOutputPort).getAllThirdsByStateForExport("TEST_ENT", false, PageRequest.of(0, 5000));
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.COMPLETED);
    }

    @Test
    @DisplayName("Debe procesar exportación exitosamente sin filtro de estado")
    void testProcessExportAsync_WithoutStatusFilter_Success() throws Exception {
        // Arrange
        exportRequest.setStatus(null); // Sin filtro
        Page<Third> page = new PageImpl<>(sampleThirds, PageRequest.of(0, 5000), sampleThirds.size());

        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000)))
            .thenReturn(page);
        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(1, 5000)))
            .thenReturn(Page.empty());

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert
        verify(thirdOutputPort).getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000));
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.COMPLETED);
    }

    @Test
    @DisplayName("Debe procesar exportación con paginación múltiple")
    void testProcessExportAsync_WithMultiplePages_Success() throws Exception {
        // Arrange - Crear datos que llenen completamente las páginas
        List<Third> page1Data = new ArrayList<>();
        for (int i = 1; i <= 5000; i++) {
            page1Data.add(Third.builder().thId((long) i).entId("TEST_ENT").build());
        }

        List<Third> page2Data = new ArrayList<>();
        for (int i = 5001; i <= 10000; i++) {
            page2Data.add(Third.builder().thId((long) i).entId("TEST_ENT").build());
        }

        List<Third> page3Data = List.of(Third.builder().thId(10001L).entId("TEST_ENT").build());

        // Total = 10001 elementos, así que hay 3 páginas
        Page<Third> page1 = new PageImpl<>(page1Data, PageRequest.of(0, 5000), 10001);
        Page<Third> page2 = new PageImpl<>(page2Data, PageRequest.of(1, 5000), 10001);
        Page<Third> page3 = new PageImpl<>(page3Data, PageRequest.of(2, 5000), 10001);

        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000))).thenReturn(page1);
        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(1, 5000))).thenReturn(page2);
        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(2, 5000))).thenReturn(page3);
        // No es necesario mockear página 3 porque hasNext() = false en página 2

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert - Solo se llaman las páginas que tienen datos
        verify(thirdOutputPort).getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000));
        verify(thirdOutputPort).getAllThirdsForExport("TEST_ENT", PageRequest.of(1, 5000));
        verify(thirdOutputPort).getAllThirdsForExport("TEST_ENT", PageRequest.of(2, 5000));
        verify(thirdOutputPort, never()).getAllThirdsForExport("TEST_ENT", PageRequest.of(3, 5000));
        verify(jobTracker).updateTotalRecords(jobId, 10001); // 5000 + 5000 + 1
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.COMPLETED);
    }

    @Test
    @DisplayName("Debe procesar exportación con una sola página de datos")
    void testProcessExportAsync_WithSinglePage_Success() throws Exception {
        // Arrange
        Page<Third> page = new PageImpl<>(sampleThirds, PageRequest.of(0, 5000), sampleThirds.size());

        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000)))
            .thenReturn(page);
            
        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert - Solo se llama a la página 0
        verify(thirdOutputPort, times(1)).getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000));
        verify(thirdOutputPort, never()).getAllThirdsForExport("TEST_ENT", PageRequest.of(1, 5000));
        verify(jobTracker).updateTotalRecords(jobId, 3);
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.COMPLETED);
    }

    // ===============================
    // ESCENARIOS SIN DATOS
    // ===============================

    @Test
    @DisplayName("Debe manejar exportación sin datos (estado activo)")
    void testProcessExportAsync_WithNoActiveData() throws Exception {
        // Arrange
        exportRequest.setStatus(true);
        Page<Third> emptyPage = Page.empty();

        when(thirdOutputPort.getAllThirdsByStateForExport("TEST_ENT", true, PageRequest.of(0, 5000)))
            .thenReturn(emptyPage);

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.PROCESSING);
        verify(jobTracker).updateProgress(jobId, 10);
        verify(jobTracker).updateTotalRecords(jobId, 0);
        verify(jobTracker).updateProgress(jobId, 50);
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.FAILED);
        verify(jobTracker).setErrorMessage(jobId, "No hay terceros activos para exportar");
        verify(jobTracker).updateProgress(jobId, 100);

        // Verificar que no se genera Excel
        verifyNoInteractions(excelValidationService);
        verify(jobTracker, never()).setFileData(anyString(), any(byte[].class));
    }

    @Test
    @DisplayName("Debe manejar exportación sin datos (estado inactivo)")
    void testProcessExportAsync_WithNoInactiveData() throws Exception {
        // Arrange
        exportRequest.setStatus(false);
        Page<Third> emptyPage = Page.empty();

        when(thirdOutputPort.getAllThirdsByStateForExport("TEST_ENT", false, PageRequest.of(0, 5000)))
            .thenReturn(emptyPage);

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert
        verify(jobTracker).setErrorMessage(jobId, "No hay terceros inactivos para exportar");
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.FAILED);
    }

    @Test
    @DisplayName("Debe manejar exportación sin datos (sin filtro de estado)")
    void testProcessExportAsync_WithNoData_NoStatusFilter() throws Exception {
        // Arrange
        exportRequest.setStatus(null);
        Page<Third> emptyPage = Page.empty();

        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000)))
            .thenReturn(emptyPage);

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert
        verify(jobTracker).setErrorMessage(jobId, "No hay terceros para exportar");
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.FAILED);
    }

    @Test
    @DisplayName("Debe manejar exportación con lista nula de datos")
    void testProcessExportAsync_WithNullDataList() throws Exception {
        // Arrange
        exportRequest.setStatus(null);

        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000)))
            .thenReturn(null);

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert
        verify(jobTracker).setErrorMessage(jobId, "No hay terceros para exportar");
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.FAILED);
    }

    // ===============================
    // ESCENARIOS DE ERROR
    // ===============================

    @Test
    @DisplayName("Debe manejar error en consulta de datos")
    void testProcessExportAsync_WithDataQueryError() throws Exception {
        // Arrange
        RuntimeException dbError = new RuntimeException("Database connection failed");

        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000)))
            .thenThrow(dbError);

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.PROCESSING);
        verify(jobTracker).updateProgress(jobId, 10);
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.FAILED);
        verify(jobTracker).setErrorMessage(jobId, "Database connection failed");
        verify(jobTracker).updateProgress(jobId, 100);
    }

    @Test
    @DisplayName("Debe manejar error en generación de Excel")
    void testProcessExportAsync_WithExcelGenerationError() throws Exception {
        // Arrange
        Page<Third> page = new PageImpl<>(sampleThirds, PageRequest.of(0, 5000), sampleThirds.size());

        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000)))
            .thenReturn(page);
        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(1, 5000)))
            .thenReturn(Page.empty());

        // Simular error creando un ExcelValidationService que lance excepción
        doThrow(new RuntimeException("Excel validation failed"))
            .when(excelValidationService).createReferenceDataSheet(any(), anyString());

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.FAILED);
        verify(jobTracker).setErrorMessage(jobId, "Excel validation failed");
    }

    @Test
    @DisplayName("Debe manejar error con mensaje nulo")
    void testProcessExportAsync_WithNullErrorMessage() throws Exception {
        // Arrange
        RuntimeException nullMessageError = new RuntimeException((String) null);

        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000)))
            .thenThrow(nullMessageError);

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert
        verify(jobTracker).setErrorMessage(jobId, "Error desconocido durante la exportación");
    }

    @Test
    @DisplayName("Debe manejar error en almacenamiento de archivo")
    void testProcessExportAsync_WithFileStorageError() throws Exception {
        // Arrange
        Page<Third> page = new PageImpl<>(sampleThirds, PageRequest.of(0, 5000), sampleThirds.size());
        RuntimeException storageError = new RuntimeException("Error al almacenar archivo en memoria");

        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000)))
            .thenReturn(page);
        // No es necesario mockear página 1 porque hasNext() = false

        // Simular error en almacenamiento de archivo
        doThrow(storageError).when(jobTracker).setFileData(anyString(), any(byte[].class));

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Esperar a que termine el procesamiento asíncrono
        Thread.sleep(500); // Dar tiempo al hilo asíncrono para completar

        // Assert
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.FAILED);
        verify(jobTracker).setErrorMessage(jobId, "Error al almacenar archivo en memoria");
    }

    // ===============================
    // ESCENARIOS DE VALIDACIÓN
    // ===============================

    @Test
    @DisplayName("Debe validar que se actualiza el progreso correctamente")
    void testProcessExportAsync_ProgressUpdates() throws Exception {
        // Arrange
        Page<Third> page = new PageImpl<>(sampleThirds, PageRequest.of(0, 5000), sampleThirds.size());

        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000)))
            .thenReturn(page);
        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(1, 5000)))
            .thenReturn(Page.empty());

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert
        verify(jobTracker).updateProgress(jobId, 10);
        verify(jobTracker).updateProgress(jobId, 50);
        verify(jobTracker).updateProgress(jobId, 90);
        verify(jobTracker).updateProgress(jobId, 100);
    }

    @Test
    @DisplayName("Debe verificar que el archivo generado no esté vacío")
    void testProcessExportAsync_FileDataNotEmpty() throws Exception {
        // Arrange
        Page<Third> page = new PageImpl<>(sampleThirds, PageRequest.of(0, 5000), sampleThirds.size());

        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000)))
            .thenReturn(page);
        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(1, 5000)))
            .thenReturn(Page.empty());

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert
        verify(jobTracker).setFileData(eq(jobId), argThat(bytes -> bytes != null && bytes.length > 0));
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.COMPLETED);
    }

    @Test
    @DisplayName("Debe crear hoja de datos de referencia en el Excel")
    void testProcessExportAsync_CreatesReferenceDataSheet() throws Exception {
        // Arrange
        Page<Third> page = new PageImpl<>(sampleThirds, PageRequest.of(0, 5000), sampleThirds.size());

        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(0, 5000)))
            .thenReturn(page);
        when(thirdOutputPort.getAllThirdsForExport("TEST_ENT", PageRequest.of(1, 5000)))
            .thenReturn(Page.empty());

        // Act
        asyncExportProcessor.processExportAsync(exportRequest, jobId);

        // Assert
        verify(excelValidationService).createReferenceDataSheet(any(), eq("TEST_ENT"));
        verify(jobTracker).updateJobStatus(jobId, ImportStatus.COMPLETED);
    }
}
