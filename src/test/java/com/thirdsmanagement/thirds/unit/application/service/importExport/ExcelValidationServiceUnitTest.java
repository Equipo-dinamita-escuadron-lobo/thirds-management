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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.service.importExport.ExcelValidationService;
import com.thirdsmanagement.thirds.domain.model.*;

/**
 * Tests unitarios para ExcelValidationService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExcelValidationServiceUnitTest {

    @Mock
    private IdOutputPort idOutputPort;

    @Mock
    private GeographyOutputPort geographyOutputPort;

    @InjectMocks
    private ExcelValidationService excelValidationService;

    private String entId;
    private Workbook workbook;
    private Sheet sheet;

    @BeforeEach
    void setUp() {
        entId = "ENT001";
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Test");
    }

    // ========== getTypeIdOptions Tests ==========

    @Test
    @DisplayName("Debe obtener tipos de identificación activos")
    void testGetTypeIdOptions_WithActiveTypes() {
        // Arrange
        List<TypeId> typeIds = Arrays.asList(
            createTypeId("CC", true),
            createTypeId("NIT", true),
            createTypeId("CE", false)
        );
        when(idOutputPort.getAllTypeIds(entId)).thenReturn(typeIds);

        // Act
        List<String> result = excelValidationService.getTypeIdOptions(entId);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("CC"));
        assertTrue(result.contains("NIT"));
        assertFalse(result.contains("CE"));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay tipos activos")
    void testGetTypeIdOptions_WithNoActiveTypes() {
        // Arrange
        List<TypeId> typeIds = Arrays.asList(
            createTypeId("CC", false),
            createTypeId("NIT", false)
        );
        when(idOutputPort.getAllTypeIds(entId)).thenReturn(typeIds);

        // Act
        List<String> result = excelValidationService.getTypeIdOptions(entId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe filtrar tipos con status null")
    void testGetTypeIdOptions_WithNullStatus() {
        // Arrange
        List<TypeId> typeIds = Arrays.asList(
            createTypeId("CC", true),
            createTypeIdWithNullStatus("NIT")
        );
        when(idOutputPort.getAllTypeIds(entId)).thenReturn(typeIds);

        // Act
        List<String> result = excelValidationService.getTypeIdOptions(entId);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains("CC"));
    }

    // ========== getThirdTypeOptions Tests ==========

    @Test
    @DisplayName("Debe obtener tipos de tercero activos")
    void testGetThirdTypeOptions_WithActiveTypes() {
        // Arrange
        List<ThirdType> thirdTypes = Arrays.asList(
            createThirdType("Cliente", true),
            createThirdType("Proveedor", true),
            createThirdType("Empleado", false)
        );
        when(idOutputPort.getALLThirdTypes(entId)).thenReturn(thirdTypes);

        // Act
        List<String> result = excelValidationService.getThirdTypeOptions(entId);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("Cliente"));
        assertTrue(result.contains("Proveedor"));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay tipos de tercero activos")
    void testGetThirdTypeOptions_WithNoActiveTypes() {
        // Arrange
        when(idOutputPort.getALLThirdTypes(entId)).thenReturn(Collections.emptyList());

        // Act
        List<String> result = excelValidationService.getThirdTypeOptions(entId);

        // Assert
        assertTrue(result.isEmpty());
    }

    // ========== getPersonTypeOptions Tests ==========

    @Test
    @DisplayName("Debe retornar opciones de tipo de persona")
    void testGetPersonTypeOptions_ReturnsCorrectOptions() {
        // Act
        List<String> result = excelValidationService.getPersonTypeOptions();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("NATURAL"));
        assertTrue(result.contains("JURIDICA"));
    }

    // ========== getGenderOptions Tests ==========

    @Test
    @DisplayName("Debe retornar opciones de género")
    void testGetGenderOptions_ReturnsCorrectOptions() {
        // Act
        List<String> result = excelValidationService.getGenderOptions();

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.contains("MASCULINO"));
        assertTrue(result.contains("FEMENINO"));
        assertTrue(result.contains("OTRO"));
    }

    // ========== getStatusOptions Tests ==========

    @Test
    @DisplayName("Debe retornar opciones de estado")
    void testGetStatusOptions_ReturnsCorrectOptions() {
        // Act
        List<String> result = excelValidationService.getStatusOptions();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("ACTIVO"));
        assertTrue(result.contains("INACTIVO"));
    }

    // ========== getCountryOptions Tests ==========

    @Test
    @DisplayName("Debe obtener países activos")
    void testGetCountryOptions_WithActiveCountries() {
        // Arrange
        List<Country> countries = Arrays.asList(
            createCountry("COL", "Colombia"),
            createCountry("USA", "Estados Unidos")
        );
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(countries);

        // Act
        List<String> result = excelValidationService.getCountryOptions();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("Colombia"));
        assertTrue(result.contains("Estados Unidos"));
    }

    // ========== getColombianStates Tests ==========

    @Test
    @DisplayName("Debe obtener departamentos colombianos")
    void testGetColombianStates_ReturnsStates() {
        // Arrange
        List<State> states = Arrays.asList(
            createState("ANT", "Antioquia"),
            createState("CUN", "Cundinamarca")
        );
        when(geographyOutputPort.getStatesByCountry("COL")).thenReturn(states);

        // Act
        List<String> result = excelValidationService.getColombianStates();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("Antioquia"));
        assertTrue(result.contains("Cundinamarca"));
    }

    // ========== getCountryCodeByName Tests ==========

    @Test
    @DisplayName("Debe obtener código de país por nombre")
    void testGetCountryCodeByName_WithValidName() {
        // Arrange
        List<Country> countries = Arrays.asList(
            createCountry("COL", "Colombia")
        );
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(countries);

        // Act
        String result = excelValidationService.getCountryCodeByName("Colombia");

        // Assert
        assertEquals("COL", result);
    }

    @Test
    @DisplayName("Debe retornar null cuando el país no existe")
    void testGetCountryCodeByName_WithInvalidName() {
        // Arrange
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(Collections.emptyList());

        // Act
        String result = excelValidationService.getCountryCodeByName("PaisInexistente");

        // Assert
        assertNull(result);
    }

    // ========== getStatesByCountryName Tests ==========

    @Test
    @DisplayName("Debe obtener departamentos por nombre de país")
    void testGetStatesByCountryName_WithValidCountry() {
        // Arrange
        List<Country> countries = Arrays.asList(createCountry("COL", "Colombia"));
        List<State> states = Arrays.asList(
            createState("ANT", "Antioquia"),
            createState("CUN", "Cundinamarca")
        );
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(countries);
        when(geographyOutputPort.getStatesByCountry("COL")).thenReturn(states);

        // Act
        List<String> result = excelValidationService.getStatesByCountryName("Colombia");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains("Antioquia"));
        assertTrue(result.contains("Cundinamarca"));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando el país no existe")
    void testGetStatesByCountryName_WithInvalidCountry() {
        // Arrange
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(Collections.emptyList());

        // Act
        List<String> result = excelValidationService.getStatesByCountryName("PaisInexistente");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe filtrar estados con nombres vacíos")
    void testGetStatesByCountryName_FiltersEmptyNames() {
        // Arrange
        List<Country> countries = Arrays.asList(createCountry("COL", "Colombia"));
        List<State> states = Arrays.asList(
            createState("ANT", "Antioquia"),
            createState("XXX", ""),
            createState("YYY", null)
        );
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(countries);
        when(geographyOutputPort.getStatesByCountry("COL")).thenReturn(states);

        // Act
        List<String> result = excelValidationService.getStatesByCountryName("Colombia");

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains("Antioquia"));
    }

    @Test
    @DisplayName("Debe ordenar departamentos alfabéticamente")
    void testGetStatesByCountryName_ReturnsSortedList() {
        // Arrange
        List<Country> countries = Arrays.asList(createCountry("COL", "Colombia"));
        List<State> states = Arrays.asList(
            createState("CUN", "Cundinamarca"),
            createState("ANT", "Antioquia"),
            createState("VAL", "Valle del Cauca")
        );
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(countries);
        when(geographyOutputPort.getStatesByCountry("COL")).thenReturn(states);

        // Act
        List<String> result = excelValidationService.getStatesByCountryName("Colombia");

        // Assert
        assertEquals("Antioquia", result.get(0));
        assertEquals("Cundinamarca", result.get(1));
        assertEquals("Valle del Cauca", result.get(2));
    }

    // ========== applyDropdownValidation Tests ==========

    @Test
    @DisplayName("Debe aplicar validación de lista desplegable")
    void testApplyDropdownValidation_WithValidOptions() {
        // Arrange
        List<String> options = Arrays.asList("Opción1", "Opción2");

        // Act
        excelValidationService.applyDropdownValidation(sheet, 0, 1, 10, options, "Error");

        // Assert
        assertNotNull(sheet.getDataValidations());
        assertFalse(sheet.getDataValidations().isEmpty());
    }

    @Test
    @DisplayName("Debe no aplicar validación cuando la lista está vacía")
    void testApplyDropdownValidation_WithEmptyOptions() {
        // Arrange
        List<String> options = Collections.emptyList();

        // Act
        excelValidationService.applyDropdownValidation(sheet, 0, 1, 10, options, "Error");

        // Assert
        assertTrue(sheet.getDataValidations().isEmpty());
    }

    @Test
    @DisplayName("Debe no aplicar validación cuando la lista es null")
    void testApplyDropdownValidation_WithNullOptions() {
        // Act
        excelValidationService.applyDropdownValidation(sheet, 0, 1, 10, null, "Error");

        // Assert
        assertTrue(sheet.getDataValidations().isEmpty());
    }

    // ========== applyNumericRangeValidation Tests ==========

    @Test
    @DisplayName("Debe aplicar validación de rango numérico")
    void testApplyNumericRangeValidation_WithValidRange() {
        // Act
        excelValidationService.applyNumericRangeValidation(sheet, 0, 1, 10, 0, 9, "Campo", "Error");

        // Assert
        assertNotNull(sheet.getDataValidations());
        assertFalse(sheet.getDataValidations().isEmpty());
    }

    // ========== applyThirdValidations Tests ==========

    @Test
    @DisplayName("Debe aplicar validaciones básicas de tercero sin género")
    void testApplyThirdValidations_WithoutGender() {
        // Arrange
        when(idOutputPort.getAllTypeIds(entId)).thenReturn(Arrays.asList(createTypeId("CC", true)));

        // Act
        excelValidationService.applyThirdValidations(sheet, entId, 1, 10, -1, 7);

        // Assert
        assertNotNull(sheet.getDataValidations());
        assertFalse(sheet.getDataValidations().isEmpty());
        verify(idOutputPort).getAllTypeIds(entId);
    }

    @Test
    @DisplayName("Debe aplicar validaciones básicas de tercero con género")
    void testApplyThirdValidations_WithGender() {
        // Arrange
        when(idOutputPort.getAllTypeIds(entId)).thenReturn(Arrays.asList(createTypeId("CC", true)));

        // Act
        excelValidationService.applyThirdValidations(sheet, entId, 1, 10, 7, 8);

        // Assert
        assertNotNull(sheet.getDataValidations());
        assertFalse(sheet.getDataValidations().isEmpty());
    }

    // ========== applyThirdValidationsWithTypes Tests ==========

    @Test
    @DisplayName("Debe aplicar validaciones de tercero con tipos")
    void testApplyThirdValidationsWithTypes_WithAllValidations() {
        // Arrange
        when(idOutputPort.getAllTypeIds(entId)).thenReturn(Arrays.asList(createTypeId("CC", true)));
        when(idOutputPort.getALLThirdTypes(entId)).thenReturn(Arrays.asList(createThirdType("Cliente", true)));

        // Act
        excelValidationService.applyThirdValidationsWithTypes(sheet, entId, 1, 10, -1, 7, 8);

        // Assert
        assertNotNull(sheet.getDataValidations());
        assertFalse(sheet.getDataValidations().isEmpty());
        verify(idOutputPort).getALLThirdTypes(entId);
    }

    // ========== applyGeographyValidations Tests ==========

    @Test
    @DisplayName("Debe aplicar validaciones geográficas cuando todas las columnas existen")
    void testApplyGeographyValidations_WithAllColumns() {
        // Arrange
        Sheet refSheet = workbook.createSheet("Datos_Referencia");
        createReferenceData(refSheet);

        // Act
        excelValidationService.applyGeographyValidations(sheet, 1, 10, 0, 1, 2);

        // Assert
        assertNotNull(sheet.getDataValidations());
    }

    @Test
    @DisplayName("Debe no aplicar validación de país cuando índice es negativo")
    void testApplyGeographyValidations_WithoutCountryColumn() {
        // Act
        excelValidationService.applyGeographyValidations(sheet, 1, 10, -1, -1, -1);

        // Assert
        assertTrue(sheet.getDataValidations().isEmpty());
    }

    @Test
    @DisplayName("Debe aplicar solo validación de país cuando otras columnas no existen")
    void testApplyGeographyValidations_OnlyCountry() {
        // Arrange
        Sheet refSheet = workbook.createSheet("Datos_Referencia");
        createReferenceData(refSheet);

        // Act
        excelValidationService.applyGeographyValidations(sheet, 1, 10, 0, -1, -1);

        // Assert
        assertFalse(sheet.getDataValidations().isEmpty());
    }

    // ========== createReferenceDataSheet Tests ==========

    @Test
    @DisplayName("Debe crear hoja de datos de referencia")
    void testCreateReferenceDataSheet_CreatesSheet() {
        // Arrange
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(
            Arrays.asList(createCountry("COL", "Colombia"))
        );
        when(geographyOutputPort.getStatesByCountry("COL")).thenReturn(
            Arrays.asList(createState("ANT", "Antioquia"))
        );
        when(geographyOutputPort.getCitiesByState("ANT", "COL")).thenReturn(
            Arrays.asList(createCity("MED", "Medellín"))
        );
        when(idOutputPort.getAllTypeIds(entId)).thenReturn(
            Arrays.asList(createTypeId("CC", true))
        );
        when(idOutputPort.getALLThirdTypes(entId)).thenReturn(
            Arrays.asList(createThirdType("Cliente", true))
        );

        // Act
        excelValidationService.createReferenceDataSheet(workbook, entId);

        // Assert
        assertNotNull(workbook.getSheet("Datos_Referencia"));
        assertTrue(workbook.isSheetHidden(workbook.getSheetIndex("Datos_Referencia")));
    }

    @Test
    @DisplayName("Debe llenar encabezados en hoja de referencia")
    void testCreateReferenceDataSheet_FillsHeaders() {
        // Arrange
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(Collections.emptyList());
        when(geographyOutputPort.getStatesByCountry(anyString())).thenReturn(Collections.emptyList());
        when(idOutputPort.getAllTypeIds(entId)).thenReturn(Collections.emptyList());
        when(idOutputPort.getALLThirdTypes(entId)).thenReturn(Collections.emptyList());

        // Act
        excelValidationService.createReferenceDataSheet(workbook, entId);

        // Assert
        Sheet refSheet = workbook.getSheet("Datos_Referencia");
        Row headerRow = refSheet.getRow(0);
        assertNotNull(headerRow);
        assertEquals("Países", headerRow.getCell(0).getStringCellValue());
        assertEquals("Departamentos", headerRow.getCell(1).getStringCellValue());
        assertEquals("Tipos_ID", headerRow.getCell(2).getStringCellValue());
    }

    // ========== applyMultiSelectValidation Tests ==========

    @Test
    @DisplayName("Debe aplicar validación multi-selección")
    void testApplyMultiSelectValidation_WithValidOptions() {
        // Arrange
        List<String> options = Arrays.asList("Cliente", "Proveedor", "Empleado");

        // Act
        excelValidationService.applyMultiSelectValidation(sheet, 0, 1, 10, options, "Tipos", "Opciones: ");

        // Assert
        assertNotNull(sheet.getDataValidations());
        assertFalse(sheet.getDataValidations().isEmpty());
    }

    @Test
    @DisplayName("Debe no aplicar validación multi-selección con lista vacía")
    void testApplyMultiSelectValidation_WithEmptyOptions() {
        // Arrange
        List<String> options = Collections.emptyList();

        // Act
        excelValidationService.applyMultiSelectValidation(sheet, 0, 1, 10, options, "Tipos", "Opciones: ");

        // Assert
        assertTrue(sheet.getDataValidations().isEmpty());
    }

    @Test
    @DisplayName("Debe truncar mensaje largo en validación multi-selección")
    void testApplyMultiSelectValidation_WithLongMessage() {
        // Arrange
        List<String> options = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            options.add("OpcionMuyLarga" + i);
        }

        // Act
        excelValidationService.applyMultiSelectValidation(sheet, 0, 1, 10, options, "Tipos", "Opciones: ");

        // Assert
        assertNotNull(sheet.getDataValidations());
    }

    // ========== Helper Methods ==========

    private TypeId createTypeId(String typeId, boolean status) {
        return TypeId.builder()
                .typeId(typeId)
                .status(status)
                .build();
    }

    private TypeId createTypeIdWithNullStatus(String typeId) {
        return TypeId.builder()
                .typeId(typeId)
                .status(null)
                .build();
    }

    private ThirdType createThirdType(String name, boolean status) {
        return ThirdType.builder()
                .thirdTypeName(name)
                .status(status)
                .build();
    }

    private Country createCountry(String code, String name) {
        return Country.builder()
                .countryCode(code)
                .countryName(name)
                .build();
    }

    private State createState(String code, String name) {
        return State.builder()
                .stateCode(code)
                .stateName(name)
                .build();
    }

    private City createCity(String code, String name) {
        return City.builder()
                .cityCode(code)
                .cityName(name)
                .build();
    }

    private void createReferenceData(Sheet refSheet) {
        Row headerRow = refSheet.createRow(0);
        headerRow.createCell(0).setCellValue("Países");
        
        Row dataRow = refSheet.createRow(1);
        dataRow.createCell(0).setCellValue("Colombia");
    }
}
