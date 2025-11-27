package com.thirdsmanagement.thirds.unit.domain.utils;

import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.domain.model.ImportErrorDetail;
import com.thirdsmanagement.thirds.domain.model.ThirdExcelData;
import com.thirdsmanagement.thirds.domain.utils.ErrorMappingUtils;
import com.thirdsmanagement.thirds.domain.utils.ImportConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para ErrorMappingUtils
 */
class ErrorMappingUtilsUnitTest {

    private Map<String, Integer> columnMap;
    private ThirdExcelData testExcelData;

    @BeforeEach
    void setUp() {
        columnMap = new HashMap<>();
        columnMap.put("NIT", 0);
        columnMap.put("Número Identificación", 0); // Alias para compatibilidad con detección de campos
        columnMap.put("EMAIL", 1);
        columnMap.put("PHONE", 2);

        testExcelData = ThirdExcelData.builder()
                .rowNumber(5)
                .entId("TEST001")
                .typeIdName("CC")
                .idNumber(123456789L)
                .names("Juan Carlos")
                .lastNames("Pérez Gómez")
                .socialReason("Empresa Test S.A.S")
                .gender(eThirdGender.Masculino)
                .email("juan@email.com")
                .phoneNumber("3001234567")
                .countryName("Colombia")
                .stateName("Cauca")
                .cityName("Popayán")
                .address("Calle 5 # 4-70")
                .build();
    }

    @Test
    @DisplayName("Debe crear error genérico correctamente")
    void testCreateError_WithAllParameters() {
        // Act
        ImportErrorDetail error = ErrorMappingUtils.createError(10, "NIT", "123456789",
                "ERR001", "Error de prueba", ImportErrorType.VALIDATION_ERROR, columnMap);

        // Assert
        assertEquals(10, error.getRowNumber());
        assertEquals(1, error.getColumnNumber()); // 0 + 1 (COLUMN_START_INDEX)
        assertEquals("NIT", error.getColumnName());
        assertEquals("123456789", error.getFieldValue());
        assertEquals("ERR001", error.getErrorCode());
        assertEquals("Error de prueba", error.getErrorMessage());
        assertEquals(ImportErrorType.VALIDATION_ERROR, error.getErrorType());
    }

    @Test
    @DisplayName("Debe crear error genérico sin mapa de columnas")
    void testCreateError_WithoutColumnMap() {
        // Act
        ImportErrorDetail error = ErrorMappingUtils.createError(10, "NIT", "123456789",
                "ERR001", "Error de prueba", ImportErrorType.VALIDATION_ERROR, null);

        // Assert
        assertEquals(10, error.getRowNumber());
        assertNull(error.getColumnNumber());
        assertEquals("NIT", error.getColumnName());
        assertEquals("123456789", error.getFieldValue());
        assertEquals("ERR001", error.getErrorCode());
        assertEquals("Error de prueba", error.getErrorMessage());
        assertEquals(ImportErrorType.VALIDATION_ERROR, error.getErrorType());
    }

    @Test
    @DisplayName("Debe crear error de campo requerido correctamente")
    void testCreateRequiredFieldError_WithValidData() {
        // Act
        ImportErrorDetail error = ErrorMappingUtils.createRequiredFieldError(5, "EMAIL", columnMap);

        // Assert
        assertEquals(5, error.getRowNumber());
        assertEquals(2, error.getColumnNumber()); // EMAIL está en columna 1, +1 = 2
        assertEquals("EMAIL", error.getColumnName());
        assertNull(error.getFieldValue());
        assertEquals(ImportConstants.ErrorCodes.REQUIRED_FIELD_MISSING, error.getErrorCode());
        assertEquals("El campo EMAIL es obligatorio", error.getErrorMessage());
        assertEquals(ImportErrorType.VALIDATION_ERROR, error.getErrorType());
    }

    @Test
    @DisplayName("Debe crear error de email inválido correctamente")
    void testCreateInvalidEmailError_WithValidData() {
        // Act
        ImportErrorDetail error = ErrorMappingUtils.createInvalidEmailError(3, "invalid@email", columnMap);

        // Assert
        assertEquals(3, error.getRowNumber());
        assertNull(error.getColumnNumber()); // EMAIL no está en columnMap para este test
        assertEquals(ImportConstants.EMAIL_COLUMN, error.getColumnName());
        assertEquals("invalid@email", error.getFieldValue());
        assertEquals(ImportConstants.ErrorCodes.INVALID_EMAIL_FORMAT, error.getErrorCode());
        assertEquals("El formato del correo electrónico no es válido", error.getErrorMessage());
        assertEquals(ImportErrorType.FORMAT_ERROR, error.getErrorType());
    }

    @Test
    @DisplayName("Debe crear error de teléfono inválido correctamente")
    void testCreateInvalidPhoneError_WithValidData() {
        // Act
        ImportErrorDetail error = ErrorMappingUtils.createInvalidPhoneError(7, "300-123-4567", columnMap);

        // Assert
        assertEquals(7, error.getRowNumber());
        assertNull(error.getColumnNumber()); // PHONE no está en columnMap para este test
        assertEquals(ImportConstants.PHONE_COLUMN, error.getColumnName());
        assertEquals("300-123-4567", error.getFieldValue());
        assertEquals(ImportConstants.ErrorCodes.INVALID_PHONE_FORMAT, error.getErrorCode());
        assertEquals("El formato del número de teléfono no es válido", error.getErrorMessage());
        assertEquals(ImportErrorType.FORMAT_ERROR, error.getErrorType());
    }

    @Test
    @DisplayName("Debe crear error de dígito de verificación inválido correctamente")
    void testCreateInvalidVerificationDigitError_WithValidData() {
        // Act
        ImportErrorDetail error = ErrorMappingUtils.createInvalidVerificationDigitError(2, "ABC", columnMap);

        // Assert
        assertEquals(2, error.getRowNumber());
        assertNull(error.getColumnNumber());
        assertEquals("Dígito Verificación", error.getColumnName());
        assertEquals("ABC", error.getFieldValue());
        assertEquals("INVALID_VERIFICATION_DIGIT_FORMAT", error.getErrorCode());
        assertEquals("El dígito de verificación debe ser un solo dígito (0-9)", error.getErrorMessage());
        assertEquals(ImportErrorType.FORMAT_ERROR, error.getErrorType());
    }

    @Test
    @DisplayName("Debe crear error de longitud NIT inválida correctamente")
    void testCreateInvalidNitLengthError_WithValidData() {
        // Act
        ImportErrorDetail error = ErrorMappingUtils.createInvalidNitLengthError(4, "12345678", columnMap);

        // Assert
        assertEquals(4, error.getRowNumber());
        assertEquals(1, error.getColumnNumber()); // Número Identificación está en columna 0, +1 = 1
        assertEquals("Número Identificación", error.getColumnName());
        assertEquals("12345678", error.getFieldValue());
        assertEquals("INVALID_NIT_LENGTH", error.getErrorCode());
        assertEquals("El NIT debe tener exactamente 9 dígitos", error.getErrorMessage());
        assertEquals(ImportErrorType.FORMAT_ERROR, error.getErrorType());
    }

    @Test
    @DisplayName("Debe crear error de referencia inválida correctamente")
    void testCreateInvalidReferenceError_WithValidData() {
        // Act
        ImportErrorDetail error = ErrorMappingUtils.createInvalidReferenceError(6, "CITY", "Bogotá", "Ciudad", columnMap);

        // Assert
        assertEquals(6, error.getRowNumber());
        assertNull(error.getColumnNumber());
        assertEquals("CITY", error.getColumnName());
        assertEquals("Bogotá", error.getFieldValue());
        assertEquals("INVALID_CIUDAD_REFERENCE", error.getErrorCode());
        assertEquals("Ciudad 'Bogotá' no existe o está inactivo", error.getErrorMessage());
        assertEquals(ImportErrorType.REFERENCE_ERROR, error.getErrorType());
    }

    @Test
    @DisplayName("Debe crear error de regla de negocio correctamente")
    void testCreateBusinessRuleError_WithValidData() {
        // Arrange
        Exception exception = new IllegalArgumentException("NIT: Error de regla de negocio");

        // Act
        ImportErrorDetail error = ErrorMappingUtils.createBusinessRuleError(testExcelData, exception, columnMap);

        // Assert
        assertEquals(5, error.getRowNumber());
        assertEquals(1, error.getColumnNumber()); // Número Identificación está en columna 0, +1 = 1
        assertEquals("Número Identificación", error.getColumnName()); // Detectado desde el mensaje
        assertEquals("123456789", error.getFieldValue());
        assertEquals(ImportConstants.ErrorCodes.BUSINESS_RULE_VIOLATION, error.getErrorCode());
        assertEquals("NIT: Error de regla de negocio", error.getErrorMessage());
        assertEquals(ImportErrorType.BUSINESS_RULE_ERROR, error.getErrorType());
    }

    @Test
    @DisplayName("Debe crear error de duplicado correctamente")
    void testCreateDuplicateError_WithValidData() {
        // Act
        ImportErrorDetail error = ErrorMappingUtils.createDuplicateError(8, "123456789", "NIT", columnMap);

        // Assert
        assertEquals(8, error.getRowNumber());
        assertEquals(1, error.getColumnNumber()); // NIT está en columna 0, +1 = 1
        assertEquals("NIT", error.getColumnName());
        assertEquals("123456789", error.getFieldValue());
        assertEquals(ImportConstants.ErrorCodes.DUPLICATE_RECORD, error.getErrorCode());
        assertEquals("El registro con NIT '123456789' ya existe", error.getErrorMessage());
        assertEquals(ImportErrorType.DUPLICATE_ERROR, error.getErrorType());
    }

    @Test
    @DisplayName("Debe crear error del sistema correctamente")
    void testCreateSystemError_WithValidData() {
        // Act
        ImportErrorDetail error = ErrorMappingUtils.createSystemError("Error de conexión a base de datos");

        // Assert
        assertNull(error.getRowNumber());
        assertNull(error.getColumnNumber());
        assertNull(error.getColumnName());
        assertNull(error.getFieldValue());
        assertEquals(ImportConstants.ErrorCodes.SYSTEM_ERROR, error.getErrorCode());
        assertEquals("Error del sistema durante la importación: Error de conexión a base de datos", error.getErrorMessage());
        assertEquals(ImportErrorType.SYSTEM_ERROR, error.getErrorType());
    }

    @Test
    @DisplayName("Debe crear error de procesamiento correctamente")
    void testCreateProcessingError_WithValidData() {
        // Act
        ImportErrorDetail error = ErrorMappingUtils.createProcessingError(testExcelData, "Error al procesar datos");

        // Assert
        assertEquals(5, error.getRowNumber());
        assertNull(error.getColumnNumber());
        assertNull(error.getColumnName());
        assertNull(error.getFieldValue());
        assertEquals(ImportConstants.ErrorCodes.SYSTEM_ERROR, error.getErrorCode());
        assertEquals("Error al procesar registro: Error al procesar datos", error.getErrorMessage());
        assertEquals(ImportErrorType.SYSTEM_ERROR, error.getErrorType());
    }

    @Test
    @DisplayName("Debe crear error de ciudad faltante correctamente")
    void testCreateMissingCityError_WithValidData() {
        // Arrange
        Map<String, Integer> cityColumnMap = new HashMap<>();
        cityColumnMap.put("Ciudad", 3); // Usar el nombre de columna que espera el método

        // Act
        ImportErrorDetail error = ErrorMappingUtils.createMissingCityError(9, cityColumnMap);

        // Assert
        assertEquals(9, error.getRowNumber());
        assertEquals(4, error.getColumnNumber()); // Ciudad está en columna 3, +1 = 4
        assertEquals("Ciudad", error.getColumnName());
        assertNull(error.getFieldValue());
        assertEquals(ImportConstants.ErrorCodes.MISSING_CITY_FOR_COMPLETE_ADDRESS, error.getErrorCode());
        assertEquals("La ciudad es obligatoria cuando se especifica departamento", error.getErrorMessage());
        assertEquals(ImportErrorType.VALIDATION_ERROR, error.getErrorType());
    }

    @Test
    @DisplayName("Debe generar código de error correctamente")
    void testGenerateErrorCode_WithValidData() {
        // Act
        String errorCode1 = ErrorMappingUtils.generateErrorCode("VAL", "EMAIL");
        String errorCode2 = ErrorMappingUtils.generateErrorCode("REF", "CITY");

        // Assert
        assertEquals("VAL_EMAIL", errorCode1);
        assertEquals("REF_CITY", errorCode2);
    }

    @Test
    @DisplayName("Debe generar código de error de campo inválido correctamente")
    void testGenerateInvalidFieldErrorCode_WithValidData() {
        // Act
        String errorCode = ErrorMappingUtils.generateInvalidFieldErrorCode("EMAIL");

        // Assert
        assertEquals("INVALID_EMAIL", errorCode);
    }

    @Test
    @DisplayName("Debe generar código de error de parsing correctamente")
    void testGenerateParsingErrorCode_WithValidData() {
        // Act
        String errorCode = ErrorMappingUtils.generateParsingErrorCode("PHONE");

        // Assert
        assertEquals("PARSING_ERROR_PHONE", errorCode);
    }

    @Test
    @DisplayName("Debe obtener valor de campo desde Excel data correctamente")
    void testGetFieldValueFromExcelData_WithValidData() {
        // Act
        String nitValue = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "Número Identificación");
        String emailValue = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "Email");
        String unknownField = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "UNKNOWN");

        // Assert
        assertEquals("123456789", nitValue);
        assertEquals("juan@email.com", emailValue);
        assertNull(unknownField);
    }

    @Test
    @DisplayName("Debe obtener Tipo Identificación desde Excel data")
    void testGetFieldValueFromExcelData_TypeIdName() {
        String result = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "Tipo Identificación");

        assertEquals("CC", result);
    }

    @Test
    @DisplayName("Debe obtener Nombres desde Excel data")
    void testGetFieldValueFromExcelData_Names() {
        String result = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "Nombres");

        assertEquals("Juan Carlos", result);
    }

    @Test
    @DisplayName("Debe obtener Apellidos desde Excel data")
    void testGetFieldValueFromExcelData_LastNames() {
        String result = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "Apellidos");

        assertEquals("Pérez Gómez", result);
    }

    @Test
    @DisplayName("Debe obtener Razón Social desde Excel data")
    void testGetFieldValueFromExcelData_SocialReason() {
        String result = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "Razón Social");

        assertEquals("Empresa Test S.A.S", result);
    }

    @Test
    @DisplayName("Debe obtener Género desde Excel data")
    void testGetFieldValueFromExcelData_Gender() {
        String result = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "Género");

        assertNotNull(result);
    }

    @Test
    @DisplayName("Debe obtener Teléfono desde Excel data")
    void testGetFieldValueFromExcelData_PhoneNumber() {
        String result = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "Teléfono");

        assertEquals("3001234567", result);
    }

    @Test
    @DisplayName("Debe obtener País desde Excel data")
    void testGetFieldValueFromExcelData_Country() {
        String result = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "País");

        assertEquals("Colombia", result);
    }

    @Test
    @DisplayName("Debe obtener Departamento desde Excel data")
    void testGetFieldValueFromExcelData_State() {
        String result = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "Departamento");

        assertEquals("Cauca", result);
    }

    @Test
    @DisplayName("Debe obtener Ciudad desde Excel data")
    void testGetFieldValueFromExcelData_City() {
        String result = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "Ciudad");

        assertEquals("Popayán", result);
    }

    @Test
    @DisplayName("Debe obtener Dirección desde Excel data")
    void testGetFieldValueFromExcelData_Address() {
        String result = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "Dirección");

        assertEquals("Calle 5 # 4-70", result);
    }

    @Test
    @DisplayName("Debe devolver null cuando el campo es desconocido")
    void testGetFieldValueFromExcelData_WithUnknownField() {
        String result = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "Campo Inexistente");

        assertNull(result);
    }

    @Test
    @DisplayName("Debe devolver null cuando excelData es null")
    void testGetFieldValueFromExcelData_WithNullExcelData() {
        String result = ErrorMappingUtils.getFieldValueFromExcelData(null, "Email");

        assertNull(result);
    }

    @Test
    @DisplayName("Debe devolver null cuando fieldName es null")
    void testGetFieldValueFromExcelData_WithNullFieldName() {
        String result = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, null);

        assertNull(result);
    }

    @Test
    @DisplayName("Debe devolver null cuando Género es null en excelData")
    void testGetFieldValueFromExcelData_WithNullGender() {
        testExcelData.setGender(null);
        String result = ErrorMappingUtils.getFieldValueFromExcelData(testExcelData, "Género");

        assertNull(result);
    }
}
