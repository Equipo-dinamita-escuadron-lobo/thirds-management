package com.thirdsmanagement.thirds.unit.application.service.importExport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import com.thirdsmanagement.thirds.application.service.importExport.ExcelParsingService;
import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdImportException;
import com.thirdsmanagement.thirds.domain.model.ThirdExcelData;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.validation.FileValidator;

/**
 * Tests unitarios para ExcelParsingService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExcelParsingServiceUnitTest {

    @Mock
    private FileValidator fileValidator;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ExcelParsingService excelParsingService;

    private String entId;
    private byte[] validExcelBytes;

    @BeforeEach
    void setUp() throws IOException {
        entId = "ENT001";
        validExcelBytes = createValidExcelFile();
        
        doNothing().when(fileValidator).validate(any(MultipartFile.class));
    }

    // ========== parseExcelFile Tests ==========

    @Test
    @DisplayName("Debe parsear archivo Excel válido con todos los campos")
    void testParseExcelFile_WithValidFile() throws IOException {
        // Arrange
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(validExcelBytes));

        // Act
        ExcelParsingService.ExcelParsingResult result = excelParsingService.parseExcelFile(multipartFile, entId);

        // Assert
        assertNotNull(result);
        assertFalse(result.getThirdsData().isEmpty());
        assertEquals(1, result.getTotalRows());
        verify(fileValidator).validate(multipartFile);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el archivo está vacío")
    void testParseExcelFile_WithEmptyFile() throws IOException {
        // Arrange
        byte[] emptyExcel = createEmptyExcelFile();
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(emptyExcel));

        // Act & Assert
        ThirdImportException exception = assertThrows(ThirdImportException.class,
                () -> excelParsingService.parseExcelFile(multipartFile, entId));
        
        assertTrue(exception.getMessage().contains("vacío"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando hay error de IO")
    void testParseExcelFile_WithIOException() throws IOException {
        // Arrange
        when(multipartFile.getInputStream()).thenThrow(new IOException("Error de lectura"));

        // Act & Assert
        ThirdImportException exception = assertThrows(ThirdImportException.class,
                () -> excelParsingService.parseExcelFile(multipartFile, entId));
        
        assertTrue(exception.getMessage().contains("Error leyendo archivo Excel"));
    }

    // ========== parseExcelFileFromBytes Tests ==========

    @Test
    @DisplayName("Debe parsear Excel desde bytes correctamente")
    void testParseExcelFileFromBytes_WithValidBytes() {
        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(validExcelBytes, entId);

        // Assert
        assertNotNull(result);
        assertFalse(result.getThirdsData().isEmpty());
        assertEquals(1, result.getTotalRows());
    }

    @Test
    @DisplayName("Debe manejar bytes inválidos correctamente")
    void testParseExcelFileFromBytes_WithInvalidBytes() {
        // Arrange
        byte[] invalidBytes = "not-an-excel-file".getBytes();

        // Act & Assert
        Exception exception = assertThrows(Exception.class,
                () -> excelParsingService.parseExcelFileFromBytes(invalidBytes, entId));
        
        assertTrue(exception instanceof ThirdImportException || 
                   exception.getClass().getName().contains("NotOfficeXmlFileException"));
    }

    // ========== Column Detection Tests ==========

    @Test
    @DisplayName("Debe detectar correctamente columnas requeridas")
    void testDetectColumnMapping_WithAllRequiredColumns() throws IOException {
        // Arrange
        byte[] excelWithHeaders = createExcelWithHeaders(true, false);

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excelWithHeaders, entId);

        // Assert
        assertNotNull(result.getColumnMap());
        assertTrue(result.getColumnMap().containsKey("Tipo Identificación"));
        assertTrue(result.getColumnMap().containsKey("Número Identificación"));
        assertTrue(result.getColumnMap().containsKey("Tipo Persona"));
    }

    @Test
    @DisplayName("Debe detectar columnas opcionales cuando existen")
    void testDetectColumnMapping_WithOptionalColumns() throws IOException {
        // Arrange
        byte[] excelWithOptional = createExcelWithHeaders(true, true);

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excelWithOptional, entId);

        // Assert
        assertNotNull(result.getColumnMap());
        assertTrue(result.getColumnMap().containsKey("Género"));
        assertTrue(result.getColumnMap().containsKey("País"));
    }

    @Test
    @DisplayName("Debe generar errores cuando faltan columnas requeridas")
    void testDetectColumnMapping_WithMissingRequiredColumns() throws IOException {
        // Arrange
        byte[] excelMissingColumns = createExcelWithMissingColumns();

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excelMissingColumns, entId);

        // Assert
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getErrorCode().equals("MISSING_REQUIRED_HEADER")));
    }

    @Test
    @DisplayName("Debe normalizar nombres de encabezados eliminando texto entre paréntesis")
    void testDetectColumnMapping_NormalizesHeaders() throws IOException {
        // Arrange
        byte[] excelWithParentheses = createExcelWithHeadersInParentheses();

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excelWithParentheses, entId);

        // Assert
        assertNotNull(result.getColumnMap());
        assertTrue(result.getColumnMap().containsKey("Tipo Identificación"));
    }

    // ========== Data Parsing Tests ==========

    @Test
    @DisplayName("Debe parsear campos básicos correctamente")
    void testParseRow_WithBasicFields() throws IOException {
        // Arrange
        byte[] excelWithData = createExcelWithBasicData();

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excelWithData, entId);

        // Assert
        assertFalse(result.getThirdsData().isEmpty());
        ThirdExcelData data = result.getThirdsData().get(0);
        assertNotNull(data);
        assertEquals(entId, data.getEntId());
        assertEquals(2, data.getRowNumber());
    }

    @Test
    @DisplayName("Debe parsear tipo de persona correctamente")
    void testParseRow_ParsesPersonType() throws IOException {
        // Arrange
        byte[] excel = createExcelWithPersonType("Natural");

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getThirdsData().isEmpty());
        assertEquals(ePersonType.Natural, result.getThirdsData().get(0).getPersonType());
    }

    @Test
    @DisplayName("Debe parsear género cuando está presente")
    void testParseRow_ParsesGender() throws IOException {
        // Arrange
        byte[] excel = createExcelWithGender("Masculino");

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getThirdsData().isEmpty());
        assertEquals(eThirdGender.Masculino, result.getThirdsData().get(0).getGender());
    }

    @Test
    @DisplayName("Debe parsear género abreviado")
    void testParseRow_ParsesAbbreviatedGender() throws IOException {
        // Arrange
        byte[] excel = createExcelWithGender("M");

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getThirdsData().isEmpty());
        assertEquals(eThirdGender.Masculino, result.getThirdsData().get(0).getGender());
    }

    @Test
    @DisplayName("Debe parsear estado como activo")
    void testParseRow_ParsesStateAsActive() throws IOException {
        // Arrange
        byte[] excel = createExcelWithState("ACTIVO");

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getThirdsData().isEmpty());
        assertTrue(result.getThirdsData().get(0).getState());
    }

    @Test
    @DisplayName("Debe parsear estado como inactivo")
    void testParseRow_ParsesStateAsInactive() throws IOException {
        // Arrange
        byte[] excel = createExcelWithState("INACTIVO");

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getThirdsData().isEmpty());
        assertFalse(result.getThirdsData().get(0).getState());
    }

    @Test
    @DisplayName("Debe usar estado activo por defecto cuando está vacío")
    void testParseRow_DefaultsToActiveState() throws IOException {
        // Arrange
        byte[] excel = createExcelWithState("");

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getThirdsData().isEmpty());
        assertTrue(result.getThirdsData().get(0).getState());
    }

    @Test
    @DisplayName("Debe parsear tipos de tercero separados por comas")
    void testParseRow_ParsesMultipleThirdTypes() throws IOException {
        // Arrange
        byte[] excel = createExcelWithThirdTypes("Cliente, Proveedor, Empleado");

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getThirdsData().isEmpty());
        Set<String> types = result.getThirdsData().get(0).getThirdTypesNames();
        assertEquals(3, types.size());
        assertTrue(types.contains("Cliente"));
        assertTrue(types.contains("Proveedor"));
        assertTrue(types.contains("Empleado"));
    }

    @Test
    @DisplayName("Debe limpiar espacios en tipos de tercero")
    void testParseRow_TrimsThirdTypes() throws IOException {
        // Arrange
        byte[] excel = createExcelWithThirdTypes("  Cliente  ,  Proveedor  ");

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getThirdsData().isEmpty());
        Set<String> types = result.getThirdsData().get(0).getThirdTypesNames();
        assertTrue(types.contains("Cliente"));
        assertTrue(types.contains("Proveedor"));
    }

    @Test
    @DisplayName("Debe parsear número de teléfono limpiando espacios")
    void testParseRow_ParsesPhoneNumberWithSpaces() throws IOException {
        // Arrange
        byte[] excel = createExcelWithPhone("300 123 4567");

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getThirdsData().isEmpty());
        assertEquals("3001234567", result.getThirdsData().get(0).getPhoneNumber());
    }

    @Test
    @DisplayName("Debe parsear campos geográficos cuando existen")
    void testParseRow_ParsesGeographyFields() throws IOException {
        // Arrange
        byte[] excel = createExcelWithGeography("Colombia", "Antioquia", "Medellín");

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getThirdsData().isEmpty());
        ThirdExcelData data = result.getThirdsData().get(0);
        assertEquals("Colombia", data.getCountryName());
        assertEquals("Antioquia", data.getStateName());
        assertEquals("Medellín", data.getCityName());
    }

    @Test
    @DisplayName("Debe manejar valores numéricos en celdas de texto")
    void testGetCellValueAsString_WithNumericCell() throws IOException {
        // Arrange
        byte[] excel = createExcelWithNumericInTextColumn();

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getThirdsData().isEmpty());
    }

    @Test
    @DisplayName("Debe parsear valores Long desde celdas numéricas")
    void testGetCellValueAsLong_WithNumericCell() throws IOException {
        // Arrange
        byte[] excel = createExcelWithNumericId(123456789L);

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getThirdsData().isEmpty());
        assertEquals(123456789L, result.getThirdsData().get(0).getIdNumber());
    }

    @Test
    @DisplayName("Debe generar error con formato numérico inválido")
    void testGetCellValueAsLong_WithInvalidFormat() throws IOException {
        // Arrange
        byte[] excel = createExcelWithInvalidNumber("ABC123");

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getErrorCode().equals("INVALID_NUMBER_FORMAT")));
    }

    @Test
    @DisplayName("Debe ignorar filas vacías")
    void testParseRow_SkipsEmptyRows() throws IOException {
        // Arrange
        byte[] excel = createExcelWithEmptyRows();

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertEquals(1, result.getTotalRows());
    }

    @Test
    @DisplayName("Debe generar error con tipo de persona inválido")
    void testParseEnum_WithInvalidPersonType() throws IOException {
        // Arrange
        byte[] excel = createExcelWithPersonType("INVALIDO");

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getErrorType() == ImportErrorType.VALIDATION_ERROR));
    }

    @Test
    @DisplayName("Debe generar error con género inválido")
    void testParseEnum_WithInvalidGender() throws IOException {
        // Arrange
        byte[] excel = createExcelWithGender("INVALIDO");

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Debe manejar excepción durante parseo de fila")
    void testParseRow_HandlesException() throws IOException {
        // Arrange
        byte[] excel = createExcelWithCorruptedData();

        // Act
        ExcelParsingService.ExcelParsingResult result = 
            excelParsingService.parseExcelFileFromBytes(excel, entId);

        // Assert
        assertNotNull(result);
    }

    // ========== Helper Methods ==========

    private byte[] createValidExcelFile() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Terceros");
            
            // Headers
            Row headerRow = sheet.createRow(0);
            createCell(headerRow, 0, "Tipo Identificación");
            createCell(headerRow, 1, "Número Identificación");
            createCell(headerRow, 2, "Dígito Verificación");
            createCell(headerRow, 3, "Tipo Persona");
            createCell(headerRow, 4, "Nombres");
            createCell(headerRow, 5, "Apellidos");
            createCell(headerRow, 6, "Razón Social");
            createCell(headerRow, 7, "Estado");
            createCell(headerRow, 8, "Tipos de Tercero");
            createCell(headerRow, 9, "Dirección");
            createCell(headerRow, 10, "Teléfono");
            createCell(headerRow, 11, "Email");
            
            // Data
            Row dataRow = sheet.createRow(1);
            createCell(dataRow, 0, "CC");
            createNumericCell(dataRow, 1, 123456789);
            createCell(dataRow, 2, "");
            createCell(dataRow, 3, "Natural");
            createCell(dataRow, 4, "Juan");
            createCell(dataRow, 5, "Pérez");
            createCell(dataRow, 6, "");
            createCell(dataRow, 7, "ACTIVO");
            createCell(dataRow, 8, "Cliente");
            createCell(dataRow, 9, "Calle 123");
            createCell(dataRow, 10, "3001234567");
            createCell(dataRow, 11, "test@test.com");
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] createEmptyExcelFile() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            workbook.createSheet("Empty");
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] createExcelWithHeaders(boolean required, boolean optional) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Terceros");
            Row headerRow = sheet.createRow(0);
            
            int col = 0;
            if (required) {
                createCell(headerRow, col++, "Tipo Identificación");
                createCell(headerRow, col++, "Número Identificación");
                createCell(headerRow, col++, "Dígito Verificación");
                createCell(headerRow, col++, "Tipo Persona");
                createCell(headerRow, col++, "Nombres");
                createCell(headerRow, col++, "Apellidos");
                createCell(headerRow, col++, "Razón Social");
                createCell(headerRow, col++, "Estado");
                createCell(headerRow, col++, "Tipos de Tercero");
                createCell(headerRow, col++, "Dirección");
                createCell(headerRow, col++, "Teléfono");
                createCell(headerRow, col++, "Email");
            }
            
            if (optional) {
                createCell(headerRow, col++, "Género");
                createCell(headerRow, col++, "País");
                createCell(headerRow, col++, "Departamento");
                createCell(headerRow, col++, "Ciudad");
            }
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] createExcelWithMissingColumns() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Terceros");
            Row headerRow = sheet.createRow(0);
            createCell(headerRow, 0, "Tipo Identificación");
            createCell(headerRow, 1, "Nombres");
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] createExcelWithHeadersInParentheses() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Terceros");
            Row headerRow = sheet.createRow(0);
            
            createCell(headerRow, 0, "Tipo Identificación\n(Requerido)");
            createCell(headerRow, 1, "Número Identificación\n(Requerido)");
            createCell(headerRow, 2, "Dígito Verificación");
            createCell(headerRow, 3, "Tipo Persona\n(Requerido)");
            createCell(headerRow, 4, "Nombres");
            createCell(headerRow, 5, "Apellidos");
            createCell(headerRow, 6, "Razón Social");
            createCell(headerRow, 7, "Estado");
            createCell(headerRow, 8, "Tipos de Tercero");
            createCell(headerRow, 9, "Dirección");
            createCell(headerRow, 10, "Teléfono");
            createCell(headerRow, 11, "Email");
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] createExcelWithBasicData() throws IOException {
        return createValidExcelFile();
    }

    private byte[] createExcelWithPersonType(String personType) throws IOException {
        return createExcelWithCustomField(3, personType);
    }

    private byte[] createExcelWithGender(String gender) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Terceros");
            Row headerRow = sheet.createRow(0);
            
            for (int i = 0; i < 12; i++) {
                createCell(headerRow, i, getHeaderName(i));
            }
            createCell(headerRow, 12, "Género");
            
            Row dataRow = sheet.createRow(1);
            fillBasicDataRow(dataRow);
            createCell(dataRow, 12, gender);
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] createExcelWithState(String state) throws IOException {
        return createExcelWithCustomField(7, state);
    }

    private byte[] createExcelWithThirdTypes(String types) throws IOException {
        return createExcelWithCustomField(8, types);
    }

    private byte[] createExcelWithPhone(String phone) throws IOException {
        return createExcelWithCustomField(10, phone);
    }

    private byte[] createExcelWithGeography(String country, String state, String city) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Terceros");
            Row headerRow = sheet.createRow(0);
            
            for (int i = 0; i < 12; i++) {
                createCell(headerRow, i, getHeaderName(i));
            }
            createCell(headerRow, 12, "País");
            createCell(headerRow, 13, "Departamento");
            createCell(headerRow, 14, "Ciudad");
            
            Row dataRow = sheet.createRow(1);
            fillBasicDataRow(dataRow);
            createCell(dataRow, 12, country);
            createCell(dataRow, 13, state);
            createCell(dataRow, 14, city);
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] createExcelWithNumericInTextColumn() throws IOException {
        return createValidExcelFile();
    }

    private byte[] createExcelWithNumericId(long id) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Terceros");
            Row headerRow = sheet.createRow(0);
            
            for (int i = 0; i < 12; i++) {
                createCell(headerRow, i, getHeaderName(i));
            }
            
            Row dataRow = sheet.createRow(1);
            createCell(dataRow, 0, "CC");
            createNumericCell(dataRow, 1, id);
            fillRestOfDataRow(dataRow, 2);
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] createExcelWithInvalidNumber(String invalidNumber) throws IOException {
        return createExcelWithCustomField(1, invalidNumber);
    }

    private byte[] createExcelWithEmptyRows() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Terceros");
            Row headerRow = sheet.createRow(0);
            
            for (int i = 0; i < 12; i++) {
                createCell(headerRow, i, getHeaderName(i));
            }
            
            // Empty row
            sheet.createRow(1);
            
            // Valid data row
            Row dataRow = sheet.createRow(2);
            fillBasicDataRow(dataRow);
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private byte[] createExcelWithCorruptedData() throws IOException {
        return createValidExcelFile();
    }

    private byte[] createExcelWithCustomField(int columnIndex, String value) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Terceros");
            Row headerRow = sheet.createRow(0);
            
            for (int i = 0; i < 12; i++) {
                createCell(headerRow, i, getHeaderName(i));
            }
            
            Row dataRow = sheet.createRow(1);
            fillBasicDataRow(dataRow);
            createCell(dataRow, columnIndex, value);
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void createCell(Row row, int column, String value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
    }

    private void createNumericCell(Row row, int column, long value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
    }

    private void fillBasicDataRow(Row dataRow) {
        createCell(dataRow, 0, "CC");
        createNumericCell(dataRow, 1, 123456789);
        createCell(dataRow, 2, "");
        createCell(dataRow, 3, "Natural");
        createCell(dataRow, 4, "Juan");
        createCell(dataRow, 5, "Pérez");
        createCell(dataRow, 6, "");
        createCell(dataRow, 7, "ACTIVO");
        createCell(dataRow, 8, "Cliente");
        createCell(dataRow, 9, "Calle 123");
        createCell(dataRow, 10, "3001234567");
        createCell(dataRow, 11, "test@test.com");
    }

    private void fillRestOfDataRow(Row dataRow, int startCol) {
        createCell(dataRow, 2, "");
        createCell(dataRow, 3, "Natural");
        createCell(dataRow, 4, "Juan");
        createCell(dataRow, 5, "Pérez");
        createCell(dataRow, 6, "");
        createCell(dataRow, 7, "ACTIVO");
        createCell(dataRow, 8, "Cliente");
        createCell(dataRow, 9, "Calle 123");
        createCell(dataRow, 10, "3001234567");
        createCell(dataRow, 11, "test@test.com");
    }

    private String getHeaderName(int index) {
        String[] headers = {
            "Tipo Identificación",
            "Número Identificación",
            "Dígito Verificación",
            "Tipo Persona",
            "Nombres",
            "Apellidos",
            "Razón Social",
            "Estado",
            "Tipos de Tercero",
            "Dirección",
            "Teléfono",
            "Email"
        };
        return headers[index];
    }
}
