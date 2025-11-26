package com.thirdsmanagement.thirds.unit.application.service.importExport;

import com.thirdsmanagement.thirds.application.service.importExport.BatchValidationService;
import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.service.third.ThirdValidationService;
import com.thirdsmanagement.thirds.domain.model.*;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios completos para BatchValidationService
 * Cubre todas las combinatorias posibles de escenarios de validación por lotes
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BatchValidationServiceUnitTest {

    @Mock
    private IdOutputPort idOutputPort;

    @Mock
    private GeographyOutputPort geographyOutputPort;

    @Mock
    private ThirdValidationService thirdValidationService;

    private BatchValidationService batchValidationService;

    private Map<String, Integer> columnMap;
    private List<ThirdExcelData> sampleData;

    @BeforeEach
    void setUp() {
        batchValidationService = new BatchValidationService(
            idOutputPort, geographyOutputPort, thirdValidationService
        );

        columnMap = new HashMap<>();
        columnMap.put("Tipo Identificación", 0);
        columnMap.put("Número Identificación", 1);
        columnMap.put("Tipo Persona", 2);
        columnMap.put("Nombres", 3);
        columnMap.put("Apellidos", 4);
        columnMap.put("Razón Social", 5);
        columnMap.put("Dirección", 6);
        columnMap.put("Teléfono", 7);
        columnMap.put("Email", 8);
        columnMap.put("Tipos de Tercero", 9);
        columnMap.put("País", 10);
        columnMap.put("Departamento", 11);
        columnMap.put("Ciudad", 12);

        sampleData = new ArrayList<>();
    }

    // ===============================
    // ESCENARIOS DE VALIDACIÓN EXITOSA
    // ===============================

    @Test
    @DisplayName("Debe validar lote exitosamente con persona natural completa")
    void testValidateBatch_SuccessNaturalPerson() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData naturalPerson = createValidNaturalPersonData();
        sampleData.add(naturalPerson);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().size() == 1);
        assertTrue(result.getErrors().isEmpty());
        assertEquals(1, result.getValidCount());
        assertEquals(0, result.getErrorCount());
        assertEquals(1, result.getTotalProcessed());
    }

    @Test
    @DisplayName("Debe validar lote exitosamente con persona jurídica completa")
    void testValidateBatch_SuccessLegalEntity() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData legalEntity = createValidLegalEntityData();
        sampleData.add(legalEntity);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().size() == 1);
        assertTrue(result.getErrors().isEmpty());
        assertEquals(1, result.getValidCount());
        assertEquals(0, result.getErrorCount());
        assertEquals(1, result.getTotalProcessed());
    }

    @Test
    @DisplayName("Debe validar lote con múltiples registros válidos")
    void testValidateBatch_SuccessMultipleRecords() {
        // Arrange
        setupMockReferenceData();
        sampleData.add(createValidNaturalPersonData());
        sampleData.add(createValidLegalEntityData());

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertEquals(2, result.getValidRecords().size());
        assertTrue(result.getErrors().isEmpty());
        assertEquals(2, result.getValidCount());
        assertEquals(0, result.getErrorCount());
        assertEquals(2, result.getTotalProcessed());
    }

    // ===============================
    // ESCENARIOS CON ERRORES DE CAMPOS REQUERIDOS
    // ===============================

    @Test
    @DisplayName("Debe detectar errores en campos requeridos comunes")
    void testValidateBatch_ErrorRequiredCommonFields() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData incompleteData = ThirdExcelData.builder()
            .rowNumber(1)
            .entId("TEST_ENT")
            .personType(ePersonType.Natural)
            .thirdTypesNames(new HashSet<>(Arrays.asList("Cliente")))
            // Faltan: typeIdName, idNumber, address, phoneNumber, email
            .build();
        sampleData.add(incompleteData);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertEquals(5, result.getErrors().size()); // 5 campos requeridos faltan
        assertEquals(0, result.getValidCount());
        assertEquals(5, result.getErrorCount());
        assertEquals(1, result.getTotalProcessed());
    }

    @Test
    @DisplayName("Debe detectar errores en campos específicos de persona natural")
    void testValidateBatch_ErrorNaturalPersonSpecificFields() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData incompleteNatural = ThirdExcelData.builder()
            .rowNumber(1)
            .entId("TEST_ENT")
            .typeIdName("CC")
            .idNumber(123456789L)
            .personType(ePersonType.Natural)
            .address("Calle 123")
            .phoneNumber("3001234567")
            .email("test@test.com")
            .thirdTypesNames(new HashSet<>(Arrays.asList("Cliente")))
            // Faltan: names, lastNames
            .build();
        sampleData.add(incompleteNatural);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertEquals(2, result.getErrors().size()); // names y lastNames faltan
        assertTrue(result.getErrors().stream().allMatch(e ->
            e.getErrorCode().equals("REQUIRED_FIELD_MISSING")));
    }

    @Test
    @DisplayName("Debe detectar errores en campos específicos de persona jurídica")
    void testValidateBatch_ErrorLegalEntitySpecificFields() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData incompleteLegal = ThirdExcelData.builder()
            .rowNumber(1)
            .entId("TEST_ENT")
            .typeIdName("NIT")
            .idNumber(901234567L)
            .personType(ePersonType.Juridica)
            .address("Carrera 456")
            .phoneNumber("3019876543")
            .email("empresa@test.com")
            .thirdTypesNames(new HashSet<>(Arrays.asList("Proveedor")))
            // Falta: socialReason
            .build();
        sampleData.add(incompleteLegal);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertEquals(1, result.getErrors().size());
        assertEquals("Razón Social", result.getErrors().get(0).getColumnName());
        assertEquals("REQUIRED_FIELD_MISSING", result.getErrors().get(0).getErrorCode());
    }

    // ===============================
    // ESCENARIOS CON ERRORES DE FORMATO
    // ===============================

    @Test
    @DisplayName("Debe detectar errores de formato de email")
    void testValidateBatch_ErrorEmailFormat() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData invalidEmail = createValidNaturalPersonData();
        invalidEmail.setEmail("invalid-email-format");
        sampleData.add(invalidEmail);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertEquals(1, result.getErrors().size());
        assertEquals("INVALID_EMAIL_FORMAT", result.getErrors().get(0).getErrorCode());
        assertEquals("Email", result.getErrors().get(0).getColumnName());
    }

    @Test
    @DisplayName("Debe detectar errores de formato de teléfono")
    void testValidateBatch_ErrorPhoneFormat() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData invalidPhone = createValidNaturalPersonData();
        invalidPhone.setPhoneNumber("123-456"); // Formato inválido
        sampleData.add(invalidPhone);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertEquals(1, result.getErrors().size());
        assertEquals("INVALID_PHONE_FORMAT", result.getErrors().get(0).getErrorCode());
    }

    @Test
    @DisplayName("Debe detectar errores de formato de dígito de verificación")
    void testValidateBatch_ErrorVerificationDigitFormat() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData invalidDigit = createValidNaturalPersonData();
        invalidDigit.setVerificationNumber(15L); // Fuera del rango 0-9
        sampleData.add(invalidDigit);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertEquals(1, result.getErrors().size());
        assertEquals("INVALID_VERIFICATION_DIGIT_FORMAT", result.getErrors().get(0).getErrorCode());
    }

    // ===============================
    // ESCENARIOS CON ERRORES DE REFERENCIAS
    // ===============================

    @Test
    @DisplayName("Debe detectar error de tipo de identificación inexistente")
    void testValidateBatch_ErrorInvalidTypeId() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData invalidTypeId = createValidNaturalPersonData();
        invalidTypeId.setTypeIdName("INVALID_TYPE"); // No existe en cache
        sampleData.add(invalidTypeId);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertEquals(1, result.getErrors().size());
        assertEquals("INVALID_REFERENCE", result.getErrors().get(0).getErrorCode());
        assertEquals("Tipo Identificación", result.getErrors().get(0).getColumnName());
    }

    @Test
    @DisplayName("Debe detectar error de tipo de tercero inexistente")
    void testValidateBatch_ErrorInvalidThirdType() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData invalidThirdType = createValidNaturalPersonData();
        invalidThirdType.setThirdTypesNames(new HashSet<>(Arrays.asList("INVALID_TYPE")));
        sampleData.add(invalidThirdType);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertEquals(1, result.getErrors().size());
        assertEquals("INVALID_REFERENCE", result.getErrors().get(0).getErrorCode());
        assertEquals("Tipos de Tercero", result.getErrors().get(0).getColumnName());
    }

    @Test
    @DisplayName("Debe detectar error de país inexistente")
    void testValidateBatch_ErrorInvalidCountry() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData invalidCountry = createValidNaturalPersonData();
        invalidCountry.setCountryName("INVALID_COUNTRY");
        sampleData.add(invalidCountry);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertEquals(1, result.getErrors().size());
        assertEquals("INVALID_REFERENCE", result.getErrors().get(0).getErrorCode());
        assertEquals("País", result.getErrors().get(0).getColumnName());
    }

    @Test
    @DisplayName("Debe detectar error de departamento inexistente")
    void testValidateBatch_ErrorInvalidState() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData invalidState = createValidNaturalPersonData();
        invalidState.setStateName("INVALID_STATE");
        sampleData.add(invalidState);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertEquals(1, result.getErrors().size());
        assertEquals("INVALID_REFERENCE", result.getErrors().get(0).getErrorCode());
        assertEquals("Departamento", result.getErrors().get(0).getColumnName());
    }

    @Test
    @DisplayName("Debe detectar error de ciudad inexistente")
    void testValidateBatch_ErrorInvalidCity() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData invalidCity = createValidNaturalPersonData();
        invalidCity.setCityName("INVALID_CITY");
        sampleData.add(invalidCity);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertEquals(1, result.getErrors().size());
        assertEquals("INVALID_REFERENCE", result.getErrors().get(0).getErrorCode());
        assertEquals("Ciudad", result.getErrors().get(0).getColumnName());
    }

    // ===============================
    // ESCENARIOS CON ERRORES DE REGLAS DE NEGOCIO
    // ===============================

    @Test
    @DisplayName("Debe detectar error de consistencia de tipo de persona")
    void testValidateBatch_ErrorPersonTypeConsistency() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData inconsistentPerson = createValidNaturalPersonData();
        inconsistentPerson.setPersonType(ePersonType.Natural);
        inconsistentPerson.setNames(null); // Inconsistente: persona natural sin nombres
        sampleData.add(inconsistentPerson);

        doThrow(new RuntimeException("Tipo de persona inconsistente"))
            .when(thirdValidationService).validatePersonTypeConsistency(any());

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertEquals(2, result.getErrors().size()); // 1 por campo faltante + 1 por regla negocio
        assertTrue(result.getErrors().stream().anyMatch(e ->
            e.getErrorCode().equals("BUSINESS_RULE_VIOLATION")));
    }

    @Test
    @DisplayName("Debe detectar error de compatibilidad TypeId-PersonType")
    void testValidateBatch_ErrorTypeIdCompatibility() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData incompatibleType = createValidNaturalPersonData();
        sampleData.add(incompatibleType);

        doThrow(new RuntimeException("TypeId incompatible con PersonType"))
            .when(thirdValidationService).validateTypeIdPersonTypeCompatibility(any());

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertTrue(result.getErrors().stream().anyMatch(e ->
            e.getErrorCode().equals("BUSINESS_RULE_VIOLATION")));
    }

    @Test
    @DisplayName("Debe detectar error de formato NIT")
    void testValidateBatch_ErrorNitFormat() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData invalidNit = createValidLegalEntityData();
        invalidNit.setIdNumber(123456789L); // NIT inválido (no empieza con 8 o 9)
        sampleData.add(invalidNit);

        doThrow(new RuntimeException("Formato NIT inválido"))
            .when(thirdValidationService).validateNitFormat(any());

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertTrue(result.getErrors().stream().anyMatch(e ->
            e.getErrorCode().equals("BUSINESS_RULE_VIOLATION")));
    }

    @Test
    @DisplayName("Debe detectar error de dígito de verificación")
    void testValidateBatch_ErrorVerificationDigit() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData invalidVerification = createValidNaturalPersonData();
        sampleData.add(invalidVerification);

        doThrow(new RuntimeException("Dígito de verificación inválido"))
            .when(thirdValidationService).validateVerificationDigit(any());

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertTrue(result.getErrors().stream().anyMatch(e ->
            e.getErrorCode().equals("BUSINESS_RULE_VIOLATION")));
    }

    // ===============================
    // ESCENARIOS MIXTOS
    // ===============================

    @Test
    @DisplayName("Debe manejar lote mixto con registros válidos e inválidos")
    void testValidateBatch_MixedValidInvalidRecords() {
        // Arrange
        setupMockReferenceData();

        // Registro válido
        sampleData.add(createValidNaturalPersonData());

        ThirdExcelData invalidRecord = createValidNaturalPersonData();
        invalidRecord.setEmail(null);
        invalidRecord.setRowNumber(2);
        sampleData.add(invalidRecord);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertEquals(1, result.getValidRecords().size());
        assertEquals(1, result.getErrors().size());
        assertEquals(1, result.getValidCount());
        assertEquals(1, result.getErrorCount());
        assertEquals(2, result.getTotalProcessed());
    }

    @Test
    @DisplayName("Debe manejar error crítico durante validación")
    void testValidateBatch_CriticalErrorDuringValidation() {
        // Arrange
        setupMockReferenceData();
        ThirdExcelData problemData = createValidNaturalPersonData();

        // Simular error crítico en el proceso de validación
        doThrow(new RuntimeException("Error crítico del sistema"))
            .when(thirdValidationService).validatePersonTypeConsistency(any());

        sampleData.add(problemData);

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            sampleData, "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertTrue(result.getErrors().stream().anyMatch(e ->
            e.getErrorCode().equals("VALIDATION_SYSTEM_ERROR")));
    }

    @Test
    @DisplayName("Debe manejar lote vacío")
    void testValidateBatch_EmptyBatch() {
        // Arrange
        setupMockReferenceData();

        // Act
        BatchValidationService.BatchValidationResult result = batchValidationService.validateBatch(
            Collections.emptyList(), "TEST_ENT", columnMap);

        // Assert
        assertTrue(result.getValidRecords().isEmpty());
        assertTrue(result.getErrors().isEmpty());
        assertEquals(0, result.getValidCount());
        assertEquals(0, result.getErrorCount());
        assertEquals(0, result.getTotalProcessed());
    }

    // ===============================
    // TESTS DE PRELOAD REFERENCE DATA
    // ===============================

    @Test
    @DisplayName("Debe precargar datos de referencia correctamente")
    void testPreloadReferenceData_Success() {
        // Arrange
        TypeId typeId = TypeId.builder().typeId("CC").build();
        ThirdType thirdType = ThirdType.builder().thirdTypeName("Cliente").build();
        Country country = Country.builder().countryName("Colombia").build();
        State state = State.builder().stateName("Cundinamarca").stateCode("CUN").build();
        City city = City.builder().cityName("Bogotá").build();

        when(idOutputPort.getAllTypeIds("TEST_ENT")).thenReturn(Arrays.asList(typeId));
        when(idOutputPort.getALLThirdTypes("TEST_ENT")).thenReturn(Arrays.asList(thirdType));
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(Arrays.asList(country));
        when(geographyOutputPort.getStatesByCountry("COL")).thenReturn(Arrays.asList(state));
        when(geographyOutputPort.getCitiesByState("CUN", "COL")).thenReturn(Arrays.asList(city));

        // Act
        BatchValidationService.ReferenceDataCache cache = batchValidationService.preloadReferenceData("TEST_ENT");

        // Assert
        assertNotNull(cache);
    }

    // ===============================
    // TESTS DE REFERENCE DATA CACHE
    // ===============================

    @Test
    @DisplayName("Debe precargar y usar cache de tipos de identificación")
    void testReferenceDataCache_TypeIdLookup() {
        // Arrange
        TypeId typeId = TypeId.builder().typeId("CC").build();
        when(idOutputPort.getAllTypeIds("TEST_ENT")).thenReturn(Arrays.asList(typeId));
        when(idOutputPort.getALLThirdTypes("TEST_ENT")).thenReturn(Collections.emptyList());
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(Collections.emptyList());

        // Act
        BatchValidationService.ReferenceDataCache cache = batchValidationService.preloadReferenceData("TEST_ENT");

        // Assert 
        assertNotNull(cache);
    }

    // ===============================
    // MÉTODOS DE UTILIDAD
    // ===============================

    private void setupMockReferenceData() {
        // Setup TypeIds
        TypeId ccTypeId = TypeId.builder().typeId("CC").build();
        TypeId nitTypeId = TypeId.builder().typeId("NIT").build();
        when(idOutputPort.getAllTypeIds("TEST_ENT")).thenReturn(Arrays.asList(ccTypeId, nitTypeId));

        // Setup ThirdTypes
        ThirdType clienteType = ThirdType.builder().thirdTypeName("Cliente").build();
        ThirdType proveedorType = ThirdType.builder().thirdTypeName("Proveedor").build();
        when(idOutputPort.getALLThirdTypes("TEST_ENT")).thenReturn(Arrays.asList(clienteType, proveedorType));

        // Setup Geography
        Country colombia = Country.builder().countryName("Colombia").build();
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(Arrays.asList(colombia));

        State cundinamarca = State.builder().stateName("Cundinamarca").stateCode("CUN").build();
        when(geographyOutputPort.getStatesByCountry("COL")).thenReturn(Arrays.asList(cundinamarca));

        City bogota = City.builder().cityName("Bogotá").build();
        when(geographyOutputPort.getCitiesByState("CUN", "COL")).thenReturn(Arrays.asList(bogota));
    }

    private ThirdExcelData createValidNaturalPersonData() {
        return ThirdExcelData.builder()
            .rowNumber(1)
            .entId("TEST_ENT")
            .typeIdName("CC")
            .idNumber(123456789L)
            .verificationNumber(5L)
            .personType(ePersonType.Natural)
            .names("Juan")
            .lastNames("Pérez")
            .address("Calle 123")
            .phoneNumber("3001234567")
            .email("juan@test.com")
            .thirdTypesNames(new HashSet<>(Arrays.asList("Cliente")))
            .countryName("Colombia")
            .stateName("Cundinamarca")
            .cityName("Bogotá")
            .build();
    }

    private ThirdExcelData createValidLegalEntityData() {
        return ThirdExcelData.builder()
            .rowNumber(1)
            .entId("TEST_ENT")
            .typeIdName("NIT")
            .idNumber(901234567L)
            .verificationNumber(8L)
            .personType(ePersonType.Juridica)
            .socialReason("Empresa S.A.")
            .address("Carrera 456")
            .phoneNumber("3019876543")
            .email("empresa@test.com")
            .thirdTypesNames(new HashSet<>(Arrays.asList("Proveedor")))
            .countryName("Colombia")
            .stateName("Cundinamarca")
            .cityName("Bogotá")
            .build();
    }
}
