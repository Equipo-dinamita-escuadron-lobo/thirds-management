package com.thirdsmanagement.thirds.unit.application.service.importExport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.application.service.importExport.*;
import com.thirdsmanagement.thirds.domain.enums.ExportableField;
import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdExportException;
import com.thirdsmanagement.thirds.domain.model.*;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdExportRequest;
import com.thirdsmanagement.thirds.infrastructure.utils.ExcelFileNameGenerator;

/**
 * Tests unitarios para ExportThirdService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExportThirdServiceUnitTest {

    @Mock
    private ThirdOutputPort thirdOutputPort;

    @Mock
    private ExcelValidationService excelValidationService;

    @Mock
    private ExportJobTracker exportJobTracker;

    @Mock
    private AsyncExportProcessor asyncExportProcessor;

    @Mock
    private ExcelFileNameGenerator fileNameGenerator;

    @InjectMocks
    private ExportThirdService exportThirdService;

    private String entId;
    private String companyName;
    private String jobId;
    private List<Third> thirds;
    private ThirdExportRequest exportRequest;

    @BeforeEach
    void setUp() {
        entId = "ENT001";
        companyName = "Empresa Test";
        jobId = "job-12345";
        thirds = createThirdsList();
        exportRequest = createExportRequest();
    }

    // ========== exportThirdTemplateWithValidations Tests ==========

    @Test
    @DisplayName("Debe exportar plantilla con validaciones exitosamente")
    void testExportThirdTemplateWithValidations_Success() {
        // Arrange
        doNothing().when(excelValidationService).createReferenceDataSheet(any(Workbook.class), eq(entId));

        // Act
        Resource result = exportThirdService.exportThirdTemplateWithValidations(entId);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        verify(excelValidationService).createReferenceDataSheet(any(Workbook.class), eq(entId));
    }

    @Test
    @DisplayName("Debe lanzar ThirdExportException cuando falla la generación de plantilla")
    void testExportThirdTemplateWithValidations_ThrowsException() {
        // Arrange
        doThrow(new RuntimeException("Error creando referencia"))
            .when(excelValidationService).createReferenceDataSheet(any(Workbook.class), eq(entId));

        // Act & Assert
        assertThrows(ThirdExportException.class, () -> {
            exportThirdService.exportThirdTemplateWithValidations(entId);
        });
    }

    // ========== exportThirdsWithValidations Tests ==========

    @Test
    @DisplayName("Debe exportar terceros con validaciones exitosamente")
    void testExportThirdsWithValidations_Success() {
        // Arrange
        Page<Third> page = new PageImpl<>(thirds);
        when(thirdOutputPort.getAllThirdsForExport(eq(entId), any(Pageable.class))).thenReturn(page);
        doNothing().when(excelValidationService).createReferenceDataSheet(any(Workbook.class), eq(entId));

        // Act
        Resource result = exportThirdService.exportThirdsWithValidations(exportRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        verify(thirdOutputPort).getAllThirdsForExport(eq(entId), any(Pageable.class));
    }

    @Test
    @DisplayName("Debe lanzar ThirdExportException cuando no hay datos para exportar")
    void testExportThirdsWithValidations_NoData() {
        // Arrange
        Page<Third> emptyPage = new PageImpl<>(Collections.emptyList());
        when(thirdOutputPort.getAllThirdsForExport(eq(entId), any(Pageable.class))).thenReturn(emptyPage);

        // Act & Assert
        ThirdExportException exception = assertThrows(ThirdExportException.class, () -> {
            exportThirdService.exportThirdsWithValidations(exportRequest);
        });

        assertNotNull(exception.getErrorCode());
    }

    @Test
    @DisplayName("Debe exportar terceros filtrados por estado activo")
    void testExportThirdsWithValidations_WithActiveStatusFilter() {
        // Arrange
        exportRequest.setStatus(true);
        Page<Third> page = new PageImpl<>(thirds);
        when(thirdOutputPort.getAllThirdsByStateForExport(eq(entId), eq(true), any(Pageable.class)))
            .thenReturn(page);
        doNothing().when(excelValidationService).createReferenceDataSheet(any(Workbook.class), eq(entId));

        // Act
        Resource result = exportThirdService.exportThirdsWithValidations(exportRequest);

        // Assert
        assertNotNull(result);
        verify(thirdOutputPort).getAllThirdsByStateForExport(eq(entId), eq(true), any(Pageable.class));
    }

    @Test
    @DisplayName("Debe exportar terceros filtrados por estado inactivo")
    void testExportThirdsWithValidations_WithInactiveStatusFilter() {
        // Arrange
        exportRequest.setStatus(false);
        Page<Third> page = new PageImpl<>(thirds);
        when(thirdOutputPort.getAllThirdsByStateForExport(eq(entId), eq(false), any(Pageable.class)))
            .thenReturn(page);
        doNothing().when(excelValidationService).createReferenceDataSheet(any(Workbook.class), eq(entId));

        // Act
        Resource result = exportThirdService.exportThirdsWithValidations(exportRequest);

        // Assert
        assertNotNull(result);
        verify(thirdOutputPort).getAllThirdsByStateForExport(eq(entId), eq(false), any(Pageable.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción específica cuando no hay datos activos")
    void testExportThirdsWithValidations_NoActiveData() {
        // Arrange
        exportRequest.setStatus(true);
        Page<Third> emptyPage = new PageImpl<>(Collections.emptyList());
        when(thirdOutputPort.getAllThirdsByStateForExport(eq(entId), eq(true), any(Pageable.class)))
            .thenReturn(emptyPage);

        // Act & Assert
        assertThrows(ThirdExportException.class, () -> {
            exportThirdService.exportThirdsWithValidations(exportRequest);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepción específica cuando no hay datos inactivos")
    void testExportThirdsWithValidations_NoInactiveData() {
        // Arrange
        exportRequest.setStatus(false);
        Page<Third> emptyPage = new PageImpl<>(Collections.emptyList());
        when(thirdOutputPort.getAllThirdsByStateForExport(eq(entId), eq(false), any(Pageable.class)))
            .thenReturn(emptyPage);

        // Act & Assert
        assertThrows(ThirdExportException.class, () -> {
            exportThirdService.exportThirdsWithValidations(exportRequest);
        });
    }

    @Test
    @DisplayName("Debe procesar múltiples páginas de terceros")
    void testExportThirdsWithValidations_WithMultiplePages() {
        // Arrange
        List<Third> firstPageThirds = createThirdsList();
        List<Third> secondPageThirds = createThirdsList();
        
        Page<Third> firstPage = new PageImpl<>(firstPageThirds, Pageable.ofSize(5000), 10000);
        Page<Third> secondPage = new PageImpl<>(secondPageThirds, Pageable.ofSize(5000).next(), 10000);
        Page<Third> emptyPage = Page.empty();

        when(thirdOutputPort.getAllThirdsForExport(eq(entId), any(Pageable.class)))
            .thenReturn(firstPage)
            .thenReturn(secondPage)
            .thenReturn(emptyPage);
        doNothing().when(excelValidationService).createReferenceDataSheet(any(Workbook.class), eq(entId));

        // Act
        Resource result = exportThirdService.exportThirdsWithValidations(exportRequest);

        // Assert
        assertNotNull(result);
        verify(thirdOutputPort, atLeast(2)).getAllThirdsForExport(eq(entId), any(Pageable.class));
    }

    // ========== exportThirdsAsync Tests ==========

    @Test
    @DisplayName("Debe iniciar exportación asíncrona exitosamente")
    void testExportThirdsAsync_Success() {
        // Arrange
        String fileName = "terceros_export.xlsx";
        when(fileNameGenerator.generateExportFileName(entId, companyName, null)).thenReturn(fileName);
        when(exportJobTracker.createJob(entId, fileName)).thenReturn(jobId);
        doNothing().when(asyncExportProcessor).processExportAsync(exportRequest, jobId);

        // Act
        String result = exportThirdService.exportThirdsAsync(exportRequest);

        // Assert
        assertEquals(jobId, result);
        verify(fileNameGenerator).generateExportFileName(entId, companyName, null);
        verify(exportJobTracker).createJob(entId, fileName);
        verify(asyncExportProcessor).processExportAsync(exportRequest, jobId);
    }

    @Test
    @DisplayName("Debe generar nombre de archivo con estado activo")
    void testExportThirdsAsync_WithActiveStatus() {
        // Arrange
        exportRequest.setStatus(true);
        String fileName = "terceros_activos.xlsx";
        when(fileNameGenerator.generateExportFileName(entId, companyName, true)).thenReturn(fileName);
        when(exportJobTracker.createJob(entId, fileName)).thenReturn(jobId);

        // Act
        String result = exportThirdService.exportThirdsAsync(exportRequest);

        // Assert
        assertEquals(jobId, result);
        verify(fileNameGenerator).generateExportFileName(entId, companyName, true);
    }

    @Test
    @DisplayName("Debe lanzar ThirdExportException cuando falla la creación del job")
    void testExportThirdsAsync_JobCreationFails() {
        // Arrange
        String fileName = "terceros_export.xlsx";
        when(fileNameGenerator.generateExportFileName(entId, companyName, null)).thenReturn(fileName);
        when(exportJobTracker.createJob(entId, fileName)).thenThrow(new RuntimeException("Error creando job"));

        // Act & Assert
        assertThrows(ThirdExportException.class, () -> {
            exportThirdService.exportThirdsAsync(exportRequest);
        });
    }

    @Test
    @DisplayName("Debe retornar jobId inmediatamente sin esperar procesamiento")
    void testExportThirdsAsync_ReturnsImmediately() {
        // Arrange
        String fileName = "terceros_export.xlsx";
        when(fileNameGenerator.generateExportFileName(entId, companyName, null)).thenReturn(fileName);
        when(exportJobTracker.createJob(entId, fileName)).thenReturn(jobId);

        // Act
        String result = exportThirdService.exportThirdsAsync(exportRequest);

        // Assert
        assertNotNull(result);
        assertEquals(jobId, result);
        verify(asyncExportProcessor).processExportAsync(exportRequest, jobId);
    }

    // ========== getExportStatus Tests ==========

    @Test
    @DisplayName("Debe obtener el estado de un job existente")
    void testGetExportStatus_WithExistingJob() {
        // Arrange
        ExportJobStatus jobStatus = createJobStatus(jobId, ImportStatus.PROCESSING);
        when(exportJobTracker.getJobStatus(jobId)).thenReturn(Optional.of(jobStatus));

        // Act
        Optional<ExportJobStatus> result = exportThirdService.getExportStatus(jobId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(jobId, result.get().getJobId());
        assertEquals(ImportStatus.PROCESSING, result.get().getStatus());
    }

    @Test
    @DisplayName("Debe retornar Optional vacío para job inexistente")
    void testGetExportStatus_WithNonExistentJob() {
        // Arrange
        when(exportJobTracker.getJobStatus(jobId)).thenReturn(Optional.empty());

        // Act
        Optional<ExportJobStatus> result = exportThirdService.getExportStatus(jobId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Debe obtener estado COMPLETED de job finalizado")
    void testGetExportStatus_WithCompletedJob() {
        // Arrange
        ExportJobStatus jobStatus = createJobStatus(jobId, ImportStatus.COMPLETED);
        when(exportJobTracker.getJobStatus(jobId)).thenReturn(Optional.of(jobStatus));

        // Act
        Optional<ExportJobStatus> result = exportThirdService.getExportStatus(jobId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ImportStatus.COMPLETED, result.get().getStatus());
    }

    @Test
    @DisplayName("Debe delegar la consulta al exportJobTracker")
    void testGetExportStatus_DelegatesToTracker() {
        // Arrange
        when(exportJobTracker.getJobStatus(jobId)).thenReturn(Optional.empty());

        // Act
        exportThirdService.getExportStatus(jobId);

        // Assert
        verify(exportJobTracker, times(1)).getJobStatus(jobId);
    }

    // ========== Integration Scenarios ==========

    @Test
    @DisplayName("Debe ejecutar flujo completo de exportación síncrona")
    void testCompleteExportFlow_Synchronous() {
        // Arrange
        Page<Third> page = new PageImpl<>(thirds);
        when(thirdOutputPort.getAllThirdsForExport(eq(entId), any(Pageable.class))).thenReturn(page);
        doNothing().when(excelValidationService).createReferenceDataSheet(any(Workbook.class), eq(entId));

        // Act
        Resource result = exportThirdService.exportThirdsWithValidations(exportRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
        verify(thirdOutputPort).getAllThirdsForExport(eq(entId), any(Pageable.class));
        verify(excelValidationService).createReferenceDataSheet(any(Workbook.class), eq(entId));
    }

    @Test
    @DisplayName("Debe ejecutar flujo completo de exportación asíncrona y consulta de estado")
    void testCompleteExportFlow_AsynchronousWithStatusCheck() {
        // Arrange
        String fileName = "terceros_export.xlsx";
        ExportJobStatus jobStatus = createJobStatus(jobId, ImportStatus.PROCESSING);
        
        when(fileNameGenerator.generateExportFileName(entId, companyName, null)).thenReturn(fileName);
        when(exportJobTracker.createJob(entId, fileName)).thenReturn(jobId);
        when(exportJobTracker.getJobStatus(jobId)).thenReturn(Optional.of(jobStatus));

        // Act
        String createdJobId = exportThirdService.exportThirdsAsync(exportRequest);
        Optional<ExportJobStatus> status = exportThirdService.getExportStatus(createdJobId);

        // Assert
        assertEquals(jobId, createdJobId);
        assertTrue(status.isPresent());
        assertEquals(ImportStatus.PROCESSING, status.get().getStatus());
    }

    @Test
    @DisplayName("Debe manejar exportación con campos opcionales incluidos")
    void testExportWithOptionalFields_AllIncluded() {
        // Arrange
        exportRequest.setOptionalFields(Set.of(
            ExportableField.GENDER,
            ExportableField.COUNTRY,
            ExportableField.STATE,
            ExportableField.CITY
        ));
        Page<Third> page = new PageImpl<>(thirds);
        when(thirdOutputPort.getAllThirdsForExport(eq(entId), any(Pageable.class))).thenReturn(page);
        doNothing().when(excelValidationService).createReferenceDataSheet(any(Workbook.class), eq(entId));

        // Act
        Resource result = exportThirdService.exportThirdsWithValidations(exportRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
    }

    @Test
    @DisplayName("Debe manejar exportación sin campos opcionales")
    void testExportWithOptionalFields_NoneIncluded() {
        // Arrange
        exportRequest.setOptionalFields(Collections.emptySet());
        Page<Third> page = new PageImpl<>(thirds);
        when(thirdOutputPort.getAllThirdsForExport(eq(entId), any(Pageable.class))).thenReturn(page);
        doNothing().when(excelValidationService).createReferenceDataSheet(any(Workbook.class), eq(entId));

        // Act
        Resource result = exportThirdService.exportThirdsWithValidations(exportRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
    }

    @Test
    @DisplayName("Debe manejar exportación con solo campo género incluido")
    void testExportWithOptionalFields_OnlyGender() {
        // Arrange
        exportRequest.setOptionalFields(Set.of(ExportableField.GENDER));
        Page<Third> page = new PageImpl<>(thirds);
        when(thirdOutputPort.getAllThirdsForExport(eq(entId), any(Pageable.class))).thenReturn(page);
        doNothing().when(excelValidationService).createReferenceDataSheet(any(Workbook.class), eq(entId));

        // Act
        Resource result = exportThirdService.exportThirdsWithValidations(exportRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
    }

    @Test
    @DisplayName("Debe manejar exportación con solo campos geográficos incluidos")
    void testExportWithOptionalFields_OnlyGeography() {
        // Arrange
        exportRequest.setOptionalFields(Set.of(
            ExportableField.COUNTRY,
            ExportableField.STATE,
            ExportableField.CITY
        ));
        Page<Third> page = new PageImpl<>(thirds);
        when(thirdOutputPort.getAllThirdsForExport(eq(entId), any(Pageable.class))).thenReturn(page);
        doNothing().when(excelValidationService).createReferenceDataSheet(any(Workbook.class), eq(entId));

        // Act
        Resource result = exportThirdService.exportThirdsWithValidations(exportRequest);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
    }

    @Test
    @DisplayName("Debe re-lanzar ThirdExportException sin modificar")
    void testExportThirdsWithValidations_RethrowsBusinessException() {
        // Arrange
        Page<Third> emptyPage = new PageImpl<>(Collections.emptyList());
        when(thirdOutputPort.getAllThirdsForExport(eq(entId), any(Pageable.class))).thenReturn(emptyPage);

        // Act & Assert
        ThirdExportException exception = assertThrows(ThirdExportException.class, () -> {
            exportThirdService.exportThirdsWithValidations(exportRequest);
        });

        assertNotNull(exception.getErrorCode());
    }

    @Test
    @DisplayName("Debe envolver excepciones genéricas en ThirdExportException")
    void testExportThirdsWithValidations_WrapsGenericExceptions() {
        // Arrange
        when(thirdOutputPort.getAllThirdsForExport(eq(entId), any(Pageable.class)))
            .thenThrow(new RuntimeException("Error inesperado"));

        // Act & Assert
        ThirdExportException exception = assertThrows(ThirdExportException.class, () -> {
            exportThirdService.exportThirdsWithValidations(exportRequest);
        });

        assertTrue(exception.getMessage().contains("Error al generar archivo de exportación"));
    }

    // ========== Helper Methods ==========

    private List<Third> createThirdsList() {
        List<Third> list = new ArrayList<>();
        list.add(createThird("123456789", "Juan", "Pérez"));
        list.add(createThird("987654321", "María", "García"));
        return list;
    }

    private Third createThird(String idNumberStr, String names, String lastNames) {
        return Third.builder()
                .idNumber(Long.parseLong(idNumberStr))
                .names(names)
                .lastNames(lastNames)
                .personType(ePersonType.Natural)
                .state(true)
                .typeId(createTypeId())
                .thirdTypes(createThirdTypes())
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("test@example.com")
                .build();
    }

    private TypeId createTypeId() {
        return TypeId.builder()
                .typeId("CC")
                .build();
    }

    private Set<ThirdType> createThirdTypes() {
        Set<ThirdType> types = new HashSet<>();
        types.add(ThirdType.builder().thirdTypeName("Cliente").build());
        return types;
    }

    private ThirdExportRequest createExportRequest() {
        return ThirdExportRequest.builder()
                .entId(entId)
                .companyName(companyName)
                .optionalFields(Collections.emptySet())
                .build();
    }

    private ExportJobStatus createJobStatus(String jobId, ImportStatus status) {
        return ExportJobStatus.builder()
                .jobId(jobId)
                .entId(entId)
                .fileName("test.xlsx")
                .status(status)
                .build();
    }
}
