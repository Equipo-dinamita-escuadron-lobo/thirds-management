package com.thirdsmanagement.thirds.unit.infrastructure.adapters.input.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.thirdsmanagement.thirds.application.ports.input.BulkChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.input.DeleteThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ExportThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.GetThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ImportThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ListThirdsUseCase;
import com.thirdsmanagement.thirds.application.ports.output.PdfRUTContentOutput;
import com.thirdsmanagement.thirds.application.service.importExport.PdfRUTService;
import com.thirdsmanagement.thirds.application.service.third.CreateThirdService;
import com.thirdsmanagement.thirds.application.service.third.UpdateThirdService;
import com.thirdsmanagement.thirds.domain.enums.ExportableField;
import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.domain.model.ExportJobStatus;
import com.thirdsmanagement.thirds.domain.model.ImportJobStatus;
import com.thirdsmanagement.thirds.domain.model.PdfRUTContent;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.controller.ThirdRestController;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.BulkStateChangeResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ChangeThirdStateResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.ThirdRestMapper;
import com.thirdsmanagement.thirds.infrastructure.utils.ExcelFileNameGenerator;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ThirdRestControllerUnitTest {

    @Mock
    private ListThirdsUseCase listThirdsUseCase;

    @Mock
    private GetThirdUseCase getThirdUseCase;

    @Mock
    private ChangeThirdStateUseCase changeThirdStateUseCase;

    @Mock
    private BulkChangeThirdStateUseCase bulkChangeThirdStateUseCase;

    @Mock
    private DeleteThirdUseCase deleteThirdUseCase;

    @Mock
    private ExportThirdUseCase exportThirdUseCase;

    @Mock
    private ImportThirdUseCase importThirdUseCase;

    @Mock
    private ThirdRestMapper thirdRestMapper;

    @Mock
    private PdfRUTService pdfRUTService;

    @Mock
    private CreateThirdService createThirdService;

    @Mock
    private UpdateThirdService updateThirdService;

    @Mock
    private ExcelFileNameGenerator fileNameGenerator;

    @InjectMocks
    private ThirdRestController controller;

    private String entId;
    private Third third;
    private ThirdCreateRequest thirdCreateRequest;
    private ThirdUpdateRequest thirdUpdateRequest;
    private ThirdResponse thirdResponse;
    private TypeId typeId;
    private ThirdType thirdType;

    @BeforeEach
    void setUp() {
        entId = "ENT001";

        typeId = TypeId.builder()
                .id(1L)
                .entId(entId)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .status(true)
                .build();

        thirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .entId(entId)
                .thirdTypeName("Cliente")
                .status(true)
                .build();

        third = Third.builder()
                .thId(1L)
                .entId(entId)
                .typeId(typeId)
                .personType(ePersonType.Natural)
                .thirdTypes(new HashSet<>(Set.of(thirdType)))
                .names("Juan")
                .lastNames("Pérez")
                .gender(eThirdGender.Masculino)
                .idNumber(123456789L)
                .state(true)
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("juan.perez@example.com")
                .build();

        thirdCreateRequest = ThirdCreateRequest.builder()
                .entId(entId)
                .typeId(typeId)
                .personType(ePersonType.Natural)
                .thirdTypes(new HashSet<>(Set.of(thirdType)))
                .names("Juan")
                .lastNames("Pérez")
                .gender(eThirdGender.Masculino)
                .idNumber(123456789L)
                .state(true)
                .countryCode("CO")
                .stateCode("11")
                .cityCode("11001")
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("juan.perez@example.com")
                .build();

        thirdUpdateRequest = ThirdUpdateRequest.builder()
                .thId(1L)
                .entId(entId)
                .typeId(typeId)
                .personType(ePersonType.Natural)
                .thirdTypes(new HashSet<>(Set.of(thirdType)))
                .names("Juan Actualizado")
                .lastNames("Pérez")
                .gender(eThirdGender.Masculino)
                .idNumber(123456789L)
                .state(true)
                .countryCode("CO")
                .stateCode("11")
                .cityCode("11001")
                .address("Calle 456")
                .phoneNumber("3001234567")
                .email("juan.perez@example.com")
                .build();

        thirdResponse = ThirdResponse.builder()
                .id(1L)
                .name("Juan Pérez")
                .description("Cliente")
                .build();
    }

    // ==================== createThird ====================

    @Test
    @DisplayName("Debe crear tercero correctamente y retornar CREATED")
    void testCreateThirdCreatesCorrectlyAndReturnsCreated() {
        // Arrange
        when(thirdRestMapper.toThird(thirdCreateRequest)).thenReturn(third);
        when(createThirdService.createThird(third, "CO", "11", "11001")).thenReturn(third);
        when(thirdRestMapper.toThirdCreateResponse(third)).thenReturn(thirdResponse);

        // Act
        ResponseEntity<ThirdResponse> response = controller.createThird(thirdCreateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Juan Pérez", response.getBody().getName());
        verify(thirdRestMapper).toThird(thirdCreateRequest);
        verify(createThirdService).createThird(third, "CO", "11", "11001");
        verify(thirdRestMapper).toThirdCreateResponse(third);
    }

    @Test
    @DisplayName("Debe delegar validación geográfica al servicio de creación")
    void testCreateThirdDelegatesGeographicValidation() {
        // Arrange
        when(thirdRestMapper.toThird(thirdCreateRequest)).thenReturn(third);
        when(createThirdService.createThird(third, "CO", "11", "11001")).thenReturn(third);
        when(thirdRestMapper.toThirdCreateResponse(third)).thenReturn(thirdResponse);

        // Act
        controller.createThird(thirdCreateRequest);

        // Assert
        verify(createThirdService).createThird(third, "CO", "11", "11001");
    }

    @Test
    @DisplayName("Debe propagar excepción cuando falla la creación de tercero")
    void testCreateThirdPropagatesException() {
        // Arrange
        when(thirdRestMapper.toThird(thirdCreateRequest)).thenReturn(third);
        when(createThirdService.createThird(third, "CO", "11", "11001"))
                .thenThrow(new RuntimeException("Error al crear"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.createThird(thirdCreateRequest));
        verify(createThirdService).createThird(third, "CO", "11", "11001");
    }

    // ==================== updateThird ====================

    @Test
    @DisplayName("Debe actualizar tercero correctamente y retornar OK")
    void testUpdateThirdUpdatesCorrectlyAndReturnsOk() {
        // Arrange
        Third updatedThird = Third.builder()
                .thId(1L)
                .entId(entId)
                .names("Juan Actualizado")
                .build();

        when(thirdRestMapper.toThird(thirdUpdateRequest)).thenReturn(updatedThird);
        when(updateThirdService.updateThirdWithGeography(updatedThird, "CO", "11", "11001"))
                .thenReturn(updatedThird);
        when(thirdRestMapper.toThirdCreateResponse(updatedThird)).thenReturn(thirdResponse);

        // Act
        ResponseEntity<ThirdResponse> response = controller.updateThird(thirdUpdateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(updateThirdService).updateThirdWithGeography(updatedThird, "CO", "11", "11001");
    }

    @Test
    @DisplayName("Debe delegar validación geográfica al servicio de actualización")
    void testUpdateThirdDelegatesGeographicValidation() {
        // Arrange
        when(thirdRestMapper.toThird(thirdUpdateRequest)).thenReturn(third);
        when(updateThirdService.updateThirdWithGeography(third, "CO", "11", "11001")).thenReturn(third);
        when(thirdRestMapper.toThirdCreateResponse(third)).thenReturn(thirdResponse);

        // Act
        controller.updateThird(thirdUpdateRequest);

        // Assert
        verify(updateThirdService).updateThirdWithGeography(third, "CO", "11", "11001");
    }

    // ==================== changeThirdState ====================

    @Test
    @DisplayName("Debe cambiar estado de tercero correctamente y retornar respuesta")
    void testChangeThirdStateChangesStateCorrectly() {
        // Arrange
        Long thId = 1L;
        ChangeThirdStateResponse expectedResponse = ChangeThirdStateResponse.builder()
                .result(true)
                .build();

        when(changeThirdStateUseCase.changeThirdState(thId, entId)).thenReturn(true);
        when(thirdRestMapper.toChangeThirdStateResponse(true)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ChangeThirdStateResponse> response = controller.changeThirdState(thId, entId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getResult());
        verify(changeThirdStateUseCase).changeThirdState(thId, entId);
        verify(thirdRestMapper).toChangeThirdStateResponse(true);
    }

    @Test
    @DisplayName("Debe retornar false cuando no se puede cambiar estado")
    void testChangeThirdStateReturnsFalseWhenFails() {
        // Arrange
        Long thId = 1L;
        ChangeThirdStateResponse expectedResponse = ChangeThirdStateResponse.builder()
                .result(false)
                .build();

        when(changeThirdStateUseCase.changeThirdState(thId, entId)).thenReturn(false);
        when(thirdRestMapper.toChangeThirdStateResponse(false)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<ChangeThirdStateResponse> response = controller.changeThirdState(thId, entId);

        // Assert
        assertNotNull(response);
        assertFalse(response.getBody().getResult());
    }

    // ==================== changeAllThirdsState ====================

    @Test
    @DisplayName("Debe cambiar estado de todos los terceros correctamente")
    void testChangeAllThirdsStateChangesAllStates() {
        // Arrange
        Boolean newState = true;
        int updatedCount = 50;

        when(bulkChangeThirdStateUseCase.changeAllThirdsState(entId, newState)).thenReturn(updatedCount);

        // Act
        ResponseEntity<BulkStateChangeResponse> response = controller.changeAllThirdsState(entId, newState);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(50, response.getBody().getUpdatedCount());
        assertEquals(true, response.getBody().getNewState());
        assertTrue(response.getBody().getMessage().contains("50"));
        assertTrue(response.getBody().getMessage().contains("activo"));
        verify(bulkChangeThirdStateUseCase).changeAllThirdsState(entId, newState);
    }

    @Test
    @DisplayName("Debe generar mensaje correcto para cambio masivo a inactivo")
    void testChangeAllThirdsStateGeneratesInactiveMessage() {
        // Arrange
        Boolean newState = false;
        int updatedCount = 25;

        when(bulkChangeThirdStateUseCase.changeAllThirdsState(entId, newState)).thenReturn(updatedCount);

        // Act
        ResponseEntity<BulkStateChangeResponse> response = controller.changeAllThirdsState(entId, newState);

        // Assert
        assertNotNull(response);
        assertTrue(response.getBody().getMessage().contains("25"));
        assertTrue(response.getBody().getMessage().contains("inactivo"));
    }

    // ==================== getThirdById ====================

    @Test
    @DisplayName("Debe retornar tercero por ID correctamente")
    void testGetThirdByIdReturnsThirdCorrectly() {
        // Arrange
        Long thId = 1L;
        when(getThirdUseCase.getThirdById(thId, entId)).thenReturn(third);

        // Act
        ResponseEntity<Third> response = controller.getThirdById(thId, entId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Juan", response.getBody().getNames());
        verify(getThirdUseCase).getThirdById(thId, entId);
    }

    @Test
    @DisplayName("Debe delegar correctamente búsqueda por ID")
    void testGetThirdByIdDelegatesCorrectly() {
        // Arrange
        Long thId = 1L;
        when(getThirdUseCase.getThirdById(thId, entId)).thenReturn(third);

        // Act
        controller.getThirdById(thId, entId);

        // Assert
        verify(getThirdUseCase, times(1)).getThirdById(thId, entId);
    }

    // ==================== existThirdById ====================

    @Test
    @DisplayName("Debe retornar true cuando tercero existe")
    void testExistThirdByIdReturnsTrueWhenExists() {
        // Arrange
        Long idNumber = 123456789L;
        when(getThirdUseCase.existThirdById(idNumber, entId)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = controller.existThirdById(idNumber, entId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(getThirdUseCase).existThirdById(idNumber, entId);
    }

    @Test
    @DisplayName("Debe retornar false cuando tercero no existe")
    void testExistThirdByIdReturnsFalseWhenDoesNotExist() {
        // Arrange
        Long idNumber = 999999999L;
        when(getThirdUseCase.existThirdById(idNumber, entId)).thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = controller.existThirdById(idNumber, entId);

        // Assert
        assertNotNull(response);
        assertFalse(response.getBody());
    }

    // ==================== getThirdsList ====================

    @Test
    @DisplayName("Debe retornar lista paginada de terceros sin filtro")
    void testGetThirdsListWithoutFilterReturnsPage() {
        // Arrange
        List<Third> thirds = Arrays.asList(third);
        Page<Third> page = new PageImpl<>(thirds, PageRequest.of(0, 10), 1);

        when(listThirdsUseCase.countAllThirdsByEntId(entId)).thenReturn(1L);
        when(listThirdsUseCase.getAllThirdsByWithSort(entId, 0, 10, "names", "asc"))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<Third>> response = controller.getThirdsList(
                entId, Optional.of(0), Optional.of(10), "names", "asc", null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(listThirdsUseCase).countAllThirdsByEntId(entId);
        verify(listThirdsUseCase).getAllThirdsByWithSort(entId, 0, 10, "names", "asc");
    }

    @Test
    @DisplayName("Debe retornar lista paginada de terceros con filtro de búsqueda")
    void testGetThirdsListWithSearchFilterReturnsPage() {
        // Arrange
        String search = "Juan";
        List<Third> thirds = Arrays.asList(third);
        Page<Third> page = new PageImpl<>(thirds, PageRequest.of(0, 10), 1);

        when(listThirdsUseCase.countByEntIdAndSearch(entId, search)).thenReturn(1L);
        when(listThirdsUseCase.findByEntIdAndSearch(entId, search, 0, 10, "names", "asc"))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<Third>> response = controller.getThirdsList(
                entId, Optional.of(0), Optional.of(10), "names", "asc", search);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        verify(listThirdsUseCase).countByEntIdAndSearch(entId, search);
        verify(listThirdsUseCase).findByEntIdAndSearch(entId, search, 0, 10, "names", "asc");
    }

    @Test
    @DisplayName("Debe ignorar filtro de búsqueda vacío")
    void testGetThirdsListIgnoresEmptySearchFilter() {
        // Arrange
        String search = "   ";
        List<Third> thirds = Arrays.asList(third);
        Page<Third> page = new PageImpl<>(thirds, PageRequest.of(0, 10), 1);

        when(listThirdsUseCase.countAllThirdsByEntId(entId)).thenReturn(1L);
        when(listThirdsUseCase.getAllThirdsByWithSort(entId, 0, 10, "names", "asc"))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<Third>> response = controller.getThirdsList(
                entId, Optional.of(0), Optional.of(10), "names", "asc", search);

        // Assert
        assertNotNull(response);
        verify(listThirdsUseCase).countAllThirdsByEntId(entId);
        verify(listThirdsUseCase, never()).countByEntIdAndSearch(anyString(), anyString());
    }

    // ==================== getActiveThirds ====================

    @Test
    @DisplayName("Debe retornar lista paginada de terceros activos")
    void testGetActiveThirdsReturnsActivePage() {
        // Arrange
        List<Third> activeThirds = Arrays.asList(third);
        Page<Third> page = new PageImpl<>(activeThirds, PageRequest.of(0, 10), 1);

        when(listThirdsUseCase.countActiveThirdsByEntId(entId)).thenReturn(1L);
        when(listThirdsUseCase.getAllActiveThirdsByWithSort(entId, 0, 10, "names", "asc"))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<Third>> response = controller.getActiveThirds(
                entId, Optional.of(0), Optional.of(10), "names", "asc");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        verify(listThirdsUseCase).countActiveThirdsByEntId(entId);
        verify(listThirdsUseCase).getAllActiveThirdsByWithSort(entId, 0, 10, "names", "asc");
    }

    // ==================== uploadPdf ====================

    @Test
    @DisplayName("Debe procesar PDF de RUT correctamente")
    void testUploadPdfProcessesCorrectly() throws Exception {
        // Arrange
        byte[] pdfContent = "PDF content".getBytes();
        MultipartFile file = new MockMultipartFile("file", "rut.pdf", "application/pdf", pdfContent);
        PdfRUTContentOutput output = mock(PdfRUTContentOutput.class);

        when(pdfRUTService.extractContent(any(PdfRUTContent.class))).thenReturn(output);

        // Act
        ResponseEntity<PdfRUTContentOutput> response = controller.uploadPdf(file);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(pdfRUTService).extractContent(any(PdfRUTContent.class));
    }

    @Test
    @DisplayName("Debe retornar BAD REQUEST cuando archivo PDF está vacío")
    void testUploadPdfReturnsBadRequestWhenFileEmpty() throws Exception {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "rut.pdf", "application/pdf", new byte[0]);

        // Act
        ResponseEntity<PdfRUTContentOutput> response = controller.uploadPdf(file);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(pdfRUTService, never()).extractContent(any(PdfRUTContent.class));
    }

    // ==================== exportThirdTemplate ====================

    @Test
    @DisplayName("Debe exportar plantilla de terceros correctamente")
    void testExportThirdTemplateExportsCorrectly() {
        // Arrange
        ByteArrayResource resource = new ByteArrayResource("template content".getBytes());
        String fileName = "plantilla_terceros_20231126.xlsx";

        when(exportThirdUseCase.exportThirdTemplateWithValidations(entId)).thenReturn(resource);
        when(fileNameGenerator.generateTemplateFileName()).thenReturn(fileName);

        // Act
        ResponseEntity<Resource> response = controller.exportThirdTemplate(entId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentDisposition().toString().contains(fileName));
        verify(exportThirdUseCase).exportThirdTemplateWithValidations(entId);
        verify(fileNameGenerator).generateTemplateFileName();
    }

    // ==================== exportThirdsWithValidationsAsync ====================

    @Test
    @DisplayName("Debe iniciar exportación asíncrona correctamente")
    void testExportThirdsAsyncStartsCorrectly() {
        // Arrange
        String jobId = "job-123";
        Set<ExportableField> fields = Set.of(ExportableField.GENDER);

        when(exportThirdUseCase.exportThirdsAsync(any())).thenReturn(jobId);

        // Act
        ResponseEntity<Map<String, String>> response = controller.exportThirdsWithValidationsAsync(
                entId, "true", "ACME Corp", fields);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(jobId, response.getBody().get("jobId"));
        assertTrue(response.getBody().get("downloadUrl").contains(jobId));
        verify(exportThirdUseCase).exportThirdsAsync(any());
    }

    @Test
    @DisplayName("Debe manejar campos opcionales vacíos en exportación")
    void testExportThirdsAsyncHandlesEmptyOptionalFields() {
        // Arrange
        String jobId = "job-456";

        when(exportThirdUseCase.exportThirdsAsync(any())).thenReturn(jobId);

        // Act
        ResponseEntity<Map<String, String>> response = controller.exportThirdsWithValidationsAsync(
                entId, null, null, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(exportThirdUseCase).exportThirdsAsync(any());
    }

    // ==================== getExportStatus ====================

    @Test
    @DisplayName("Debe retornar estado de exportación correctamente")
    void testGetExportStatusReturnsStatusCorrectly() {
        // Arrange
        String jobId = "job-123";
        ExportJobStatus jobStatus = ExportJobStatus.builder()
                .jobId(jobId)
                .status(ImportStatus.PROCESSING)
                .build();

        when(exportThirdUseCase.getExportStatus(jobId)).thenReturn(Optional.of(jobStatus));

        // Act
        ResponseEntity<ExportJobStatus> response = controller.getExportStatus(jobId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jobId, response.getBody().getJobId());
        verify(exportThirdUseCase).getExportStatus(jobId);
    }

    @Test
    @DisplayName("Debe retornar NOT FOUND cuando job de exportación no existe")
    void testGetExportStatusReturnsNotFoundWhenDoesNotExist() {
        // Arrange
        String jobId = "job-999";
        when(exportThirdUseCase.getExportStatus(jobId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ExportJobStatus> response = controller.getExportStatus(jobId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ==================== downloadExportedFile ====================

    @Test
    @DisplayName("Debe descargar archivo exportado correctamente cuando está completado")
    void testDownloadExportedFileDownloadsCorrectly() {
        // Arrange
        String jobId = "job-123";
        byte[] fileData = "exported data".getBytes();
        ExportJobStatus jobStatus = ExportJobStatus.builder()
                .jobId(jobId)
                .status(ImportStatus.COMPLETED)
                .fileData(fileData)
                .fileName("terceros_export.xlsx")
                .build();

        when(exportThirdUseCase.getExportStatus(jobId)).thenReturn(Optional.of(jobStatus));

        // Act
        ResponseEntity<Resource> response = controller.downloadExportedFile(jobId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentDisposition().toString().contains("terceros_export.xlsx"));
    }

    @Test
    @DisplayName("Debe retornar ACCEPTED cuando exportación aún no está completada")
    void testDownloadExportedFileReturnsAcceptedWhenInProgress() {
        // Arrange
        String jobId = "job-123";
        ExportJobStatus jobStatus = ExportJobStatus.builder()
                .jobId(jobId)
                .status(ImportStatus.PROCESSING)
                .build();

        when(exportThirdUseCase.getExportStatus(jobId)).thenReturn(Optional.of(jobStatus));

        // Act
        ResponseEntity<Resource> response = controller.downloadExportedFile(jobId);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Debe retornar NOT FOUND cuando job de descarga no existe")
    void testDownloadExportedFileReturnsNotFoundWhenDoesNotExist() {
        // Arrange
        String jobId = "job-999";
        when(exportThirdUseCase.getExportStatus(jobId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Resource> response = controller.downloadExportedFile(jobId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ==================== importThirdsFromExcel ====================

    @Test
    @DisplayName("Debe iniciar importación desde Excel correctamente")
    void testImportThirdsFromExcelStartsCorrectly() {
        // Arrange
        byte[] excelContent = "excel content".getBytes();
        MultipartFile file = new MockMultipartFile("file", "terceros.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelContent);
        String jobId = "import-job-123";

        when(importThirdUseCase.importThirdsFromExcel(any())).thenReturn(jobId);

        // Act
        ResponseEntity<Map<String, String>> response = controller.importThirdsFromExcel(entId, file);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(jobId, response.getBody().get("jobId"));
        assertTrue(response.getBody().get("statusUrl").contains(jobId));
        verify(importThirdUseCase).importThirdsFromExcel(any());
    }

    // ==================== getImportStatus ====================

    @Test
    @DisplayName("Debe retornar estado de importación correctamente")
    void testGetImportStatusReturnsStatusCorrectly() {
        // Arrange
        String jobId = "import-job-123";
        ImportJobStatus jobStatus = ImportJobStatus.builder()
                .jobId(jobId)
                .status(ImportStatus.PROCESSING)
                .build();

        when(importThirdUseCase.getImportStatus(jobId)).thenReturn(Optional.of(jobStatus));

        // Act
        ResponseEntity<ImportJobStatus> response = controller.getImportStatus(jobId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(jobId, response.getBody().getJobId());
        verify(importThirdUseCase).getImportStatus(jobId);
    }

    @Test
    @DisplayName("Debe retornar NOT FOUND cuando job de importación no existe")
    void testGetImportStatusReturnsNotFoundWhenDoesNotExist() {
        // Arrange
        String jobId = "import-job-999";
        when(importThirdUseCase.getImportStatus(jobId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ImportJobStatus> response = controller.getImportStatus(jobId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ==================== deleteThird ====================

    @Test
    @DisplayName("Debe eliminar tercero correctamente y retornar true")
    void testDeleteThirdDeletesCorrectlyAndReturnsTrue() {
        // Arrange
        Long thirdId = 1L;
        when(deleteThirdUseCase.deleteThird(thirdId, entId)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = controller.deleteThird(thirdId, entId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(deleteThirdUseCase).deleteThird(thirdId, entId);
    }

    @Test
    @DisplayName("Debe retornar false cuando no se puede eliminar tercero")
    void testDeleteThirdReturnsFalseWhenCannotDelete() {
        // Arrange
        Long thirdId = 1L;
        when(deleteThirdUseCase.deleteThird(thirdId, entId)).thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = controller.deleteThird(thirdId, entId);

        // Assert
        assertNotNull(response);
        assertFalse(response.getBody());
    }

    // ==================== getThirdsByType ====================

    @Test
    @DisplayName("Debe retornar terceros filtrados por tipo correctamente")
    void testGetThirdsByTypeReturnsFilteredThirds() {
        // Arrange
        String thirdTypeName = "Cliente";
        List<Third> thirds = Arrays.asList(third);
        Page<Third> page = new PageImpl<>(thirds, PageRequest.of(0, 10), 1);

        when(listThirdsUseCase.countThirdsByEntIdAndThirdTypeName(entId, thirdTypeName)).thenReturn(1L);
        when(listThirdsUseCase.getThirdsByEntIdAndThirdTypeName(entId, thirdTypeName, 0, 10))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<Third>> response = controller.getThirdsByType(
                entId, thirdTypeName, Optional.of(0), Optional.of(10));

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        verify(listThirdsUseCase).countThirdsByEntIdAndThirdTypeName(entId, thirdTypeName);
        verify(listThirdsUseCase).getThirdsByEntIdAndThirdTypeName(entId, thirdTypeName, 0, 10);
    }

    // ==================== Integración y casos complejos ====================

    @Test
    @DisplayName("Debe procesar múltiples operaciones de forma independiente")
    void testProcessesMultipleIndependentOperations() {
        // Arrange
        when(getThirdUseCase.existThirdById(anyLong(), anyString())).thenReturn(true);
        when(deleteThirdUseCase.deleteThird(anyLong(), anyString())).thenReturn(true);

        // Act
        controller.existThirdById(123L, entId);
        controller.deleteThird(456L, entId);

        // Assert
        verify(getThirdUseCase).existThirdById(123L, entId);
        verify(deleteThirdUseCase).deleteThird(456L, entId);
    }

    @Test
    @DisplayName("Debe manejar correctamente parámetros opcionales vacíos")
    void testHandlesEmptyOptionalParameters() {
        // Arrange
        List<Third> thirds = Arrays.asList(third);
        Page<Third> page = new PageImpl<>(thirds, PageRequest.of(0, 10), 1);

        when(listThirdsUseCase.countAllThirdsByEntId(entId)).thenReturn(1L);
        when(listThirdsUseCase.getAllThirdsByWithSort(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<Third>> response = controller.getThirdsList(
                entId, Optional.empty(), Optional.empty(), "names", "asc", null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Debe retornar status correcto para cada tipo de operación")
    void testReturnsCorrectStatusForEachOperation() {
        // Arrange
        when(thirdRestMapper.toThird(thirdCreateRequest)).thenReturn(third);
        when(createThirdService.createThird(any(), anyString(), anyString(), anyString())).thenReturn(third);
        when(thirdRestMapper.toThirdCreateResponse(third)).thenReturn(thirdResponse);

        when(changeThirdStateUseCase.changeThirdState(anyLong(), anyString())).thenReturn(true);
        when(thirdRestMapper.toChangeThirdStateResponse(true))
                .thenReturn(ChangeThirdStateResponse.builder().result(true).build());

        when(deleteThirdUseCase.deleteThird(anyLong(), anyString())).thenReturn(true);

        // Act
        ResponseEntity<ThirdResponse> createResponse = controller.createThird(thirdCreateRequest);
        ResponseEntity<ChangeThirdStateResponse> changeResponse = controller.changeThirdState(1L, entId);
        ResponseEntity<Boolean> deleteResponse = controller.deleteThird(1L, entId);

        // Assert
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertEquals(HttpStatus.OK, changeResponse.getStatusCode());
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
    }

    @Test
    @DisplayName("Debe procesar correctamente diferentes campos de ordenamiento")
    void testProcessesDifferentSortingFields() {
        // Arrange
        List<Third> thirds = Arrays.asList(third);
        Page<Third> page = new PageImpl<>(thirds, PageRequest.of(0, 10), 1);

        when(listThirdsUseCase.countAllThirdsByEntId(entId)).thenReturn(1L);
        when(listThirdsUseCase.getAllThirdsByWithSort(eq(entId), anyInt(), anyInt(), eq("names"), eq("asc")))
                .thenReturn(page);
        when(listThirdsUseCase.getAllThirdsByWithSort(eq(entId), anyInt(), anyInt(), eq("idNumber"), eq("desc")))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<Third>> responseByName = controller.getThirdsList(
                entId, Optional.of(0), Optional.of(10), "names", "asc", null);
        ResponseEntity<Page<Third>> responseById = controller.getThirdsList(
                entId, Optional.of(0), Optional.of(10), "idNumber", "desc", null);

        // Assert
        assertNotNull(responseByName);
        assertNotNull(responseById);
        verify(listThirdsUseCase).getAllThirdsByWithSort(entId, 0, 10, "names", "asc");
        verify(listThirdsUseCase).getAllThirdsByWithSort(entId, 0, 10, "idNumber", "desc");
    }
}
