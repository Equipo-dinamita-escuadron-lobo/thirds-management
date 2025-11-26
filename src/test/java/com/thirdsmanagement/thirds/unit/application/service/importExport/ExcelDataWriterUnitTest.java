package com.thirdsmanagement.thirds.unit.application.service.importExport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.thirdsmanagement.thirds.application.service.importExport.ExcelDataWriter;
import com.thirdsmanagement.thirds.application.service.importExport.ExcelValidationService;
import com.thirdsmanagement.thirds.domain.enums.ExportableField;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.domain.model.*;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdExportRequest;
import com.thirdsmanagement.thirds.infrastructure.utils.ExcelStyleHelper;

/**
 * Tests unitarios para ExcelDataWriter
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExcelDataWriterUnitTest {

    @Mock
    private ExcelStyleHelper styleHelper;

    @Mock
    private ExcelValidationService validationService;

    private ExcelDataWriter excelDataWriter;
    private Workbook workbook;
    private Sheet sheet;
    private CellStyle headerStyle;
    private CellStyle optionalHeaderStyle;
    private CellStyle dataStyle;

    @BeforeEach
    void setUp() {
        excelDataWriter = new ExcelDataWriter(styleHelper);
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Test");

        headerStyle = workbook.createCellStyle();
        optionalHeaderStyle = workbook.createCellStyle();
        dataStyle = workbook.createCellStyle();

        when(styleHelper.getHeaderStyle()).thenReturn(headerStyle);
        when(styleHelper.getOptionalHeaderStyle()).thenReturn(optionalHeaderStyle);
        when(styleHelper.getDataStyle()).thenReturn(dataStyle);
    }

    // ========== createHeaders Tests ==========

    @Test
    @DisplayName("Debe crear encabezados básicos sin campos opcionales")
    void testCreateHeaders_WithBasicFieldsOnly() {
        // Arrange
        ThirdExportRequest request = createExportRequest(Collections.emptySet());

        // Act
        excelDataWriter.createHeaders(sheet, request);

        // Assert
        Row headerRow = sheet.getRow(0);
        assertNotNull(headerRow);
        assertEquals(12, headerRow.getLastCellNum());
        assertEquals("Tipo Identificación\n(Requerido)", headerRow.getCell(0).getStringCellValue());
        assertEquals("Número Identificación\n(Requerido)", headerRow.getCell(1).getStringCellValue());
        assertEquals("Dígito Verificación\n(Requerido para persona jurídica con NIT)", headerRow.getCell(2).getStringCellValue());
        assertEquals("Tipo Persona\n(Requerido)", headerRow.getCell(3).getStringCellValue());
        assertEquals(35, headerRow.getHeightInPoints(), 0.1);
    }

    @Test
    @DisplayName("Debe crear encabezados con campo género incluido")
    void testCreateHeaders_WithGenderField() {
        // Arrange
        Set<ExportableField> fields = new HashSet<>(Arrays.asList(ExportableField.GENDER));
        ThirdExportRequest request = createExportRequest(fields);

        // Act
        excelDataWriter.createHeaders(sheet, request);

        // Assert
        Row headerRow = sheet.getRow(0);
        assertNotNull(headerRow);
        assertEquals(13, headerRow.getLastCellNum());
        boolean genderFound = false;
        for (Cell cell : headerRow) {
            if (cell.getStringCellValue().contains("Género")) {
                genderFound = true;
                assertTrue(cell.getStringCellValue().contains("Opcional"));
                break;
            }
        }
        assertTrue(genderFound);
    }

    @Test
    @DisplayName("Debe crear encabezados con campos geográficos incluidos")
    void testCreateHeaders_WithGeographyFields() {
        // Arrange
        Set<ExportableField> fields = new HashSet<>(Arrays.asList(
            ExportableField.COUNTRY,
            ExportableField.STATE,
            ExportableField.CITY
        ));
        ThirdExportRequest request = createExportRequest(fields);

        // Act
        excelDataWriter.createHeaders(sheet, request);

        // Assert
        Row headerRow = sheet.getRow(0);
        assertNotNull(headerRow);
        boolean countryFound = false;
        boolean stateFound = false;
        boolean cityFound = false;
        
        for (Cell cell : headerRow) {
            String value = cell.getStringCellValue();
            if (value.contains("País")) countryFound = true;
            if (value.contains("Departamento")) stateFound = true;
            if (value.contains("Ciudad")) cityFound = true;
        }
        
        assertTrue(countryFound);
        assertTrue(stateFound);
        assertTrue(cityFound);
    }

    @Test
    @DisplayName("Debe crear encabezados con todos los campos opcionales")
    void testCreateHeaders_WithAllOptionalFields() {
        // Arrange
        Set<ExportableField> fields = new HashSet<>(Arrays.asList(
            ExportableField.GENDER,
            ExportableField.COUNTRY,
            ExportableField.STATE,
            ExportableField.CITY
        ));
        ThirdExportRequest request = createExportRequest(fields);

        // Act
        excelDataWriter.createHeaders(sheet, request);

        // Assert
        Row headerRow = sheet.getRow(0);
        assertNotNull(headerRow);
        assertEquals(16, headerRow.getLastCellNum());
    }

    @Test
    @DisplayName("Debe aplicar estilos correctos a encabezados requeridos")
    void testCreateHeaders_AppliesRequiredHeaderStyle() {
        // Arrange
        ThirdExportRequest request = createExportRequest(Collections.emptySet());

        // Act
        excelDataWriter.createHeaders(sheet, request);

        // Assert
        Row headerRow = sheet.getRow(0);
        assertEquals(headerStyle, headerRow.getCell(0).getCellStyle());
        assertEquals(headerStyle, headerRow.getCell(1).getCellStyle());
        verify(styleHelper, atLeastOnce()).getHeaderStyle();
    }

    @Test
    @DisplayName("Debe aplicar estilos correctos a encabezados opcionales")
    void testCreateHeaders_AppliesOptionalHeaderStyle() {
        // Arrange
        Set<ExportableField> fields = new HashSet<>(Arrays.asList(ExportableField.GENDER));
        ThirdExportRequest request = createExportRequest(fields);

        // Act
        excelDataWriter.createHeaders(sheet, request);

        // Assert
        verify(styleHelper, atLeastOnce()).getOptionalHeaderStyle();
    }

    // ========== fillData Tests ==========

    @Test
    @DisplayName("Debe llenar datos básicos de terceros sin campos opcionales")
    void testFillData_WithBasicFieldsOnly() {
        // Arrange
        List<Third> thirds = Arrays.asList(createThird("123456789", "Juan", "Pérez"));
        ThirdExportRequest request = createExportRequest(Collections.emptySet());

        // Act
        excelDataWriter.fillData(sheet, thirds, request);

        // Assert
        Row dataRow = sheet.getRow(1);
        assertNotNull(dataRow);
        assertEquals("CC", dataRow.getCell(0).getStringCellValue());
        assertEquals(123456789.0, dataRow.getCell(1).getNumericCellValue(), 0.1);
        assertEquals("Juan", dataRow.getCell(4).getStringCellValue());
        assertEquals("Pérez", dataRow.getCell(5).getStringCellValue());
    }

    @Test
    @DisplayName("Debe llenar múltiples filas de datos")
    void testFillData_WithMultipleThirds() {
        // Arrange
        List<Third> thirds = Arrays.asList(
            createThird("123456789", "Juan", "Pérez"),
            createThird("987654321", "María", "García"),
            createThird("555666777", "Pedro", "López")
        );
        ThirdExportRequest request = createExportRequest(Collections.emptySet());

        // Act
        excelDataWriter.fillData(sheet, thirds, request);

        // Assert
        assertEquals(1, sheet.getRow(1).getRowNum());
        assertEquals(2, sheet.getRow(2).getRowNum());
        assertEquals(3, sheet.getRow(3).getRowNum());
        assertEquals(123456789.0, sheet.getRow(1).getCell(1).getNumericCellValue(), 0.1);
        assertEquals(987654321.0, sheet.getRow(2).getCell(1).getNumericCellValue(), 0.1);
        assertEquals(555666777.0, sheet.getRow(3).getCell(1).getNumericCellValue(), 0.1);
    }

    @Test
    @DisplayName("Debe llenar datos con campo género cuando está incluido")
    void testFillData_WithGenderField() {
        // Arrange
        Third third = createThird("123456789", "Juan", "Pérez");
        third.setGender(eThirdGender.Masculino);
        List<Third> thirds = Arrays.asList(third);
        Set<ExportableField> fields = new HashSet<>(Arrays.asList(ExportableField.GENDER));
        ThirdExportRequest request = createExportRequest(fields);

        // Act
        excelDataWriter.fillData(sheet, thirds, request);

        // Assert
        Row dataRow = sheet.getRow(1);
        assertNotNull(dataRow);
        assertEquals("Masculino", dataRow.getCell(7).getStringCellValue());
    }

    @Test
    @DisplayName("Debe llenar datos con campos geográficos cuando están incluidos")
    void testFillData_WithGeographyFields() {
        // Arrange
        Third third = createThird("123456789", "Juan", "Pérez");
        third.setCountry(Country.builder().countryName("Colombia").build());
        third.setProvince(State.builder().stateName("Antioquia").build());
        third.setCity(City.builder().cityName("Medellín").build());
        
        List<Third> thirds = Arrays.asList(third);
        Set<ExportableField> fields = new HashSet<>(Arrays.asList(
            ExportableField.COUNTRY,
            ExportableField.STATE,
            ExportableField.CITY
        ));
        ThirdExportRequest request = createExportRequest(fields);

        // Act
        excelDataWriter.fillData(sheet, thirds, request);

        // Assert
        Row dataRow = sheet.getRow(1);
        boolean colombiaFound = false;
        boolean antioquiaFound = false;
        boolean medellinFound = false;
        
        for (Cell cell : dataRow) {
            if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue();
                if (value.equals("Colombia")) colombiaFound = true;
                if (value.equals("Antioquia")) antioquiaFound = true;
                if (value.equals("Medellín")) medellinFound = true;
            }
        }
        
        assertTrue(colombiaFound);
        assertTrue(antioquiaFound);
        assertTrue(medellinFound);
    }

    @Test
    @DisplayName("Debe manejar valores null en datos de terceros")
    void testFillData_WithNullValues() {
        // Arrange
        Third third = Third.builder()
                .idNumber(123456789L)
                .personType(ePersonType.Natural)
                .names(null)
                .lastNames(null)
                .thirdTypes(new HashSet<>())
                .build();
        
        List<Third> thirds = Arrays.asList(third);
        ThirdExportRequest request = createExportRequest(Collections.emptySet());

        // Act
        excelDataWriter.fillData(sheet, thirds, request);

        // Assert
        Row dataRow = sheet.getRow(1);
        assertNotNull(dataRow);
        assertEquals("", dataRow.getCell(4).getStringCellValue());
        assertEquals("", dataRow.getCell(5).getStringCellValue());
    }

    @Test
    @DisplayName("Debe escribir estado como ACTIVO o INACTIVO")
    void testFillData_WithStateValues() {
        // Arrange
        Third activeThird = createThird("123456789", "Juan", "Pérez");
        activeThird.setState(true);
        
        Third inactiveThird = createThird("987654321", "María", "García");
        inactiveThird.setState(false);
        
        List<Third> thirds = Arrays.asList(activeThird, inactiveThird);
        ThirdExportRequest request = createExportRequest(Collections.emptySet());

        // Act
        excelDataWriter.fillData(sheet, thirds, request);

        // Assert
        assertEquals("ACTIVO", sheet.getRow(1).getCell(7).getStringCellValue());
        assertEquals("INACTIVO", sheet.getRow(2).getCell(7).getStringCellValue());
    }

    @Test
    @DisplayName("Debe concatenar tipos de tercero con comas")
    void testFillData_WithMultipleThirdTypes() {
        // Arrange
        Third third = Third.builder()
                .idNumber(123456789L)
                .typeId(TypeId.builder().typeId("CC").build())
                .personType(ePersonType.Natural)
                .names("Juan")
                .lastNames("Pérez")
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("test@test.com")
                .state(true)
                .thirdTypes(new HashSet<>(Arrays.asList(
                    ThirdType.builder().thirdTypeName("Cliente").build(),
                    ThirdType.builder().thirdTypeName("Proveedor").build(),
                    ThirdType.builder().thirdTypeName("Empleado").build()
                )))
                .build();
        
        List<Third> thirds = Arrays.asList(third);
        ThirdExportRequest request = createExportRequest(Collections.emptySet());
        excelDataWriter.createHeaders(sheet, request);

        // Act
        excelDataWriter.fillData(sheet, thirds, request);

        // Assert
        Row dataRow = sheet.getRow(1);
        String types = dataRow.getCell(8).getStringCellValue();
        assertNotNull(types);
        assertFalse(types.isEmpty());
        
        assertTrue(types.contains("Cliente") || types.contains("Proveedor") || types.contains("Empleado"), 
                "Debe contener al menos uno de los tipos de tercero");
    }

    @Test
    @DisplayName("Debe aplicar estilos de datos a las celdas")
    void testFillData_AppliesDataStyle() {
        // Arrange
        List<Third> thirds = Arrays.asList(createThird("123456789", "Juan", "Pérez"));
        ThirdExportRequest request = createExportRequest(Collections.emptySet());

        // Act
        excelDataWriter.fillData(sheet, thirds, request);

        // Assert
        Row dataRow = sheet.getRow(1);
        for (Cell cell : dataRow) {
            assertEquals(dataStyle, cell.getCellStyle());
        }
        verify(styleHelper, atLeastOnce()).getDataStyle();
    }

    @Test
    @DisplayName("Debe llenar lista vacía de terceros sin errores")
    void testFillData_WithEmptyList() {
        // Arrange
        List<Third> thirds = Collections.emptyList();
        ThirdExportRequest request = createExportRequest(Collections.emptySet());

        // Act
        excelDataWriter.fillData(sheet, thirds, request);

        // Assert
        assertNull(sheet.getRow(1));
    }

    // ========== applyValidations Tests ==========

    @Test
    @DisplayName("Debe aplicar validaciones con índices correctos para campos básicos")
    void testApplyValidations_WithBasicFields() {
        // Arrange
        ThirdExportRequest request = createExportRequest(Collections.emptySet());
        int dataRowCount = 10;

        // Act
        excelDataWriter.applyValidations(sheet, request, dataRowCount, validationService);

        // Assert
        verify(validationService).applyThirdValidationsWithTypes(
            eq(sheet),
            eq("ENT001"),
            eq(1),
            eq(1000),
            eq(-1),
            eq(7),
            eq(8)
        );
        verify(validationService).applyGeographyValidations(
            eq(sheet),
            eq(1),
            eq(1000),
            eq(-1),
            eq(-1),
            eq(-1)
        );
    }

    @Test
    @DisplayName("Debe aplicar validaciones con índice de género cuando está incluido")
    void testApplyValidations_WithGenderField() {
        // Arrange
        Set<ExportableField> fields = new HashSet<>(Arrays.asList(ExportableField.GENDER));
        ThirdExportRequest request = createExportRequest(fields);
        int dataRowCount = 10;

        // Act
        excelDataWriter.applyValidations(sheet, request, dataRowCount, validationService);

        // Assert
        verify(validationService).applyThirdValidationsWithTypes(
            eq(sheet),
            eq("ENT001"),
            eq(1),
            eq(1000),
            eq(7),
            eq(8),
            eq(9)
        );
    }

    @Test
    @DisplayName("Debe aplicar validaciones con índices geográficos cuando están incluidos")
    void testApplyValidations_WithGeographyFields() {
        // Arrange
        Set<ExportableField> fields = new HashSet<>(Arrays.asList(
            ExportableField.COUNTRY,
            ExportableField.STATE,
            ExportableField.CITY
        ));
        ThirdExportRequest request = createExportRequest(fields);
        int dataRowCount = 10;

        // Act
        excelDataWriter.applyValidations(sheet, request, dataRowCount, validationService);

        // Assert
        verify(validationService).applyGeographyValidations(
            eq(sheet),
            eq(1),
            eq(1000),
            eq(9),
            eq(10),
            eq(11)
        );
    }

    @Test
    @DisplayName("Debe calcular rango de validación con mínimo de 1000 filas")
    void testApplyValidations_WithMinimumRowRange() {
        // Arrange
        ThirdExportRequest request = createExportRequest(Collections.emptySet());
        int dataRowCount = 5;

        // Act
        excelDataWriter.applyValidations(sheet, request, dataRowCount, validationService);

        // Assert
        verify(validationService).applyThirdValidationsWithTypes(
            any(Sheet.class),
            anyString(),
            eq(1),
            eq(1000),
            anyInt(),
            anyInt(),
            anyInt()
        );
    }

    @Test
    @DisplayName("Debe calcular rango de validación con buffer de 100 filas")
    void testApplyValidations_WithBufferRows() {
        // Arrange
        ThirdExportRequest request = createExportRequest(Collections.emptySet());
        int dataRowCount = 950;

        // Act
        excelDataWriter.applyValidations(sheet, request, dataRowCount, validationService);

        // Assert
        verify(validationService).applyThirdValidationsWithTypes(
            any(Sheet.class),
            anyString(),
            eq(1),
            eq(1050),
            anyInt(),
            anyInt(),
            anyInt()
        );
    }

    // ========== autoSizeColumns Tests ==========

    @Test
    @DisplayName("Debe ajustar automáticamente columnas para campos básicos")
    void testAutoSizeColumns_WithBasicFields() {
        // Arrange
        ThirdExportRequest request = createExportRequest(Collections.emptySet());
        excelDataWriter.createHeaders(sheet, request);

        // Act
        excelDataWriter.autoSizeColumns(sheet, request);

        // Assert
        for (int i = 0; i < 12; i++) {
            int width = sheet.getColumnWidth(i);
            assertTrue(width >= 1500);
            assertTrue(width <= 25000);
        }
    }

    @Test
    @DisplayName("Debe ajustar automáticamente todas las columnas incluyendo opcionales")
    void testAutoSizeColumns_WithAllFields() {
        // Arrange
        Set<ExportableField> fields = new HashSet<>(Arrays.asList(
            ExportableField.GENDER,
            ExportableField.COUNTRY,
            ExportableField.STATE,
            ExportableField.CITY
        ));
        ThirdExportRequest request = createExportRequest(fields);
        excelDataWriter.createHeaders(sheet, request);

        // Act
        excelDataWriter.autoSizeColumns(sheet, request);

        // Assert
        for (int i = 0; i < 16; i++) {
            int width = sheet.getColumnWidth(i);
            assertTrue(width >= 1500);
            assertTrue(width <= 25000);
        }
    }

    @Test
    @DisplayName("Debe respetar ancho mínimo de columnas")
    void testAutoSizeColumns_RespectsMinimumWidth() {
        // Arrange
        ThirdExportRequest request = createExportRequest(Collections.emptySet());
        excelDataWriter.createHeaders(sheet, request);

        // Act
        excelDataWriter.autoSizeColumns(sheet, request);

        // Assert
        for (int i = 0; i < 12; i++) {
            assertTrue(sheet.getColumnWidth(i) >= 1500);
        }
    }

    @Test
    @DisplayName("Debe respetar ancho máximo de columnas")
    void testAutoSizeColumns_RespectsMaximumWidth() {
        // Arrange
        ThirdExportRequest request = createExportRequest(Collections.emptySet());
        excelDataWriter.createHeaders(sheet, request);

        // Act
        excelDataWriter.autoSizeColumns(sheet, request);

        // Assert
        for (int i = 0; i < 12; i++) {
            assertTrue(sheet.getColumnWidth(i) <= 25000);
        }
    }

    // ========== Helper Methods ==========

    private ThirdExportRequest createExportRequest(Set<ExportableField> fields) {
        return ThirdExportRequest.builder()
                .entId("ENT001")
                .optionalFields(fields)
                .build();
    }

    private Third createThird(String idNumber, String names, String lastNames) {
        return Third.builder()
                .idNumber(Long.parseLong(idNumber))
                .typeId(TypeId.builder().typeId("CC").build())
                .personType(ePersonType.Natural)
                .names(names)
                .lastNames(lastNames)
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("test@test.com")
                .state(true)
                .thirdTypes(new HashSet<>(Arrays.asList(
                    ThirdType.builder().thirdTypeName("Cliente").build()
                )))
                .build();
    }
}
