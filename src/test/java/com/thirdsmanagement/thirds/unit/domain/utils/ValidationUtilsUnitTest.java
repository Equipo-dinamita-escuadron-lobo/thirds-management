package com.thirdsmanagement.thirds.unit.domain.utils;

import com.thirdsmanagement.thirds.domain.utils.ValidationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para ValidationUtils
 */
class ValidationUtilsUnitTest {

    // ===== VALIDACIONES DE FORMATO =====

    @Test
    @DisplayName("Debe retornar true para emails válidos")
    void testIsValidEmail_WithValidEmails() {
        // Act
        boolean gmailValid = ValidationUtils.isValidEmail("usuario@gmail.com");
        boolean outlookValid = ValidationUtils.isValidEmail("test@outlook.com");
        boolean subdomainValid = ValidationUtils.isValidEmail("user@sub.dominio.com");

        // Assert
        assertTrue(gmailValid);
        assertTrue(outlookValid);
        assertTrue(subdomainValid);
    }

    @Test
    @DisplayName("Debe retornar false para emails inválidos")
    void testIsValidEmail_WithInvalidEmails() {
        // Act
        boolean noAt = ValidationUtils.isValidEmail("usuariogmail.com");
        boolean noDomain = ValidationUtils.isValidEmail("usuario@");
        boolean noUser = ValidationUtils.isValidEmail("@gmail.com");
        boolean spaces = ValidationUtils.isValidEmail("user @ gmail.com");
        boolean nullEmail = ValidationUtils.isValidEmail(null);
        boolean emptyEmail = ValidationUtils.isValidEmail("");

        // Assert
        assertFalse(noAt);
        assertFalse(noDomain);
        assertFalse(noUser);
        assertFalse(spaces);
        assertFalse(nullEmail);
        assertFalse(emptyEmail);
    }

    @Test
    @DisplayName("Debe retornar true para números de teléfono válidos")
    void testIsValidPhoneNumber_WithValidPhones() {
        // Act
        boolean withPlus = ValidationUtils.isValidPhoneNumber("+573001234567");
        boolean withoutPlus = ValidationUtils.isValidPhoneNumber("3001234567");
        boolean minLength = ValidationUtils.isValidPhoneNumber("1234567");
        boolean maxLength = ValidationUtils.isValidPhoneNumber("123456789012345");

        // Assert
        assertTrue(withPlus);
        assertTrue(withoutPlus);
        assertTrue(minLength);
        assertTrue(maxLength);
    }

    @Test
    @DisplayName("Debe retornar false para números de teléfono inválidos")
    void testIsValidPhoneNumber_WithInvalidPhones() {
        // Act
        boolean tooShort = ValidationUtils.isValidPhoneNumber("123456");
        boolean tooLong = ValidationUtils.isValidPhoneNumber("1234567890123456");
        boolean withLetters = ValidationUtils.isValidPhoneNumber("300ABC4567");
        boolean withSpaces = ValidationUtils.isValidPhoneNumber("300 123 4567");
        boolean nullPhone = ValidationUtils.isValidPhoneNumber(null);
        boolean emptyPhone = ValidationUtils.isValidPhoneNumber("");

        // Assert
        assertFalse(tooShort);
        assertFalse(tooLong);
        assertFalse(withLetters);
        assertFalse(withSpaces);
        assertFalse(nullPhone);
        assertFalse(emptyPhone);
    }

    @Test
    @DisplayName("Debe retornar true para formatos NIT válidos")
    void testIsValidNitFormat_WithValidNits() {
        // Act
        boolean basicNit = ValidationUtils.isValidNitFormat("901234567");
        boolean nitWithDash = ValidationUtils.isValidNitFormat("901234567-8");
        boolean nitWithVerification = ValidationUtils.isValidNitFormat("9012345678");

        // Assert
        assertTrue(basicNit);
        assertTrue(nitWithDash);
        assertTrue(nitWithVerification);
    }

    @Test
    @DisplayName("Debe retornar false para formatos NIT inválidos")
    void testIsValidNitFormat_WithInvalidNits() {
        // Act
        boolean tooShort = ValidationUtils.isValidNitFormat("12345678");
        boolean withLetters = ValidationUtils.isValidNitFormat("90123A567");
        boolean invalidStart = ValidationUtils.isValidNitFormat("012345678");
        boolean nullNit = ValidationUtils.isValidNitFormat(null);
        boolean emptyNit = ValidationUtils.isValidNitFormat("");

        // Assert
        assertFalse(tooShort);
        assertFalse(withLetters);
        assertFalse(invalidStart);
        assertFalse(nullNit);
        assertFalse(emptyNit);
    }

    // ===== VALIDACIONES DE CONTENIDO =====

    @Test
    @DisplayName("Debe retornar true cuando el valor tiene contenido")
    void testHasContent_WithContent() {
        // Act
        boolean hasContent = ValidationUtils.hasContent("test value");
        boolean hasContentWithSpaces = ValidationUtils.hasContent("  test  ");

        // Assert
        assertTrue(hasContent);
        assertTrue(hasContentWithSpaces);
    }

    @Test
    @DisplayName("Debe retornar false cuando el valor no tiene contenido")
    void testHasContent_WithoutContent() {
        // Act
        boolean nullValue = ValidationUtils.hasContent(null);
        boolean emptyValue = ValidationUtils.hasContent("");
        boolean onlySpaces = ValidationUtils.hasContent("   ");
        boolean onlyTabs = ValidationUtils.hasContent("\t\t");

        // Assert
        assertFalse(nullValue);
        assertFalse(emptyValue);
        assertFalse(onlySpaces);
        assertFalse(onlyTabs);
    }

    @Test
    @DisplayName("Debe validar longitud exacta correctamente")
    void testHasLength_WithExactLength() {
        // Act
        boolean exactLength = ValidationUtils.hasLength("12345", 5);
        boolean wrongLength = ValidationUtils.hasLength("1234", 5);
        boolean nullValue = ValidationUtils.hasLength(null, 5);

        // Assert
        assertTrue(exactLength);
        assertFalse(wrongLength);
        assertFalse(nullValue);
    }

    @Test
    @DisplayName("Debe validar rango de longitud correctamente")
    void testIsValidLength_WithValidRange() {
        // Act
        boolean withinRange = ValidationUtils.isValidLength("12345", 3, 10);
        boolean atMinLength = ValidationUtils.isValidLength("123", 3, 10);
        boolean atMaxLength = ValidationUtils.isValidLength("1234567890", 3, 10);
        boolean belowMin = ValidationUtils.isValidLength("12", 3, 10);
        boolean aboveMax = ValidationUtils.isValidLength("12345678901", 3, 10);
        boolean nullValue = ValidationUtils.isValidLength(null, 3, 10);

        // Assert
        assertTrue(withinRange);
        assertTrue(atMinLength);
        assertTrue(atMaxLength);
        assertFalse(belowMin);
        assertFalse(aboveMax);
        assertFalse(nullValue);
    }

    @Test
    @DisplayName("Debe validar dígito de verificación correctamente")
    void testIsValidVerificationDigit_WithValidDigits() {
        // Act
        boolean validDigit1 = ValidationUtils.isValidVerificationDigit(1L);
        boolean validDigit0 = ValidationUtils.isValidVerificationDigit(0L);
        boolean validDigit9 = ValidationUtils.isValidVerificationDigit(9L);
        boolean negativeDigit = ValidationUtils.isValidVerificationDigit(-1L);
        boolean digit10 = ValidationUtils.isValidVerificationDigit(10L);
        boolean nullDigit = ValidationUtils.isValidVerificationDigit(null);

        // Assert
        assertTrue(validDigit1);
        assertTrue(validDigit0);
        assertTrue(validDigit9);
        assertFalse(negativeDigit);
        assertFalse(digit10);
        assertFalse(nullDigit);
    }

    @Test
    @DisplayName("Debe validar longitud NIT correctamente")
    void testIsValidNitLength_WithValidLengths() {
        // Act
        boolean valid9Digits = ValidationUtils.isValidNitLength(123456789L);
        boolean valid10Digits = ValidationUtils.isValidNitLength(1234567890L);
        boolean tooShort = ValidationUtils.isValidNitLength(12345678L);
        boolean tooLong = ValidationUtils.isValidNitLength(12345678901L);
        boolean nullValue = ValidationUtils.isValidNitLength(null);

        // Assert
        assertTrue(valid9Digits);
        assertTrue(valid10Digits);
        assertFalse(tooShort);
        assertFalse(tooLong);
        assertFalse(nullValue);
    }

    // ===== VALIDACIONES DE ERRORES =====

    @Test
    @DisplayName("Debe identificar errores de duplicado correctamente")
    void testIsDuplicateError_WithDuplicateMessages() {
        // Act
        boolean duplicateError = ValidationUtils.isDuplicateError("Ya existe un tercero con el NIT 123456789");
        boolean notDuplicateError = ValidationUtils.isDuplicateError("Error de validación en el email");
        boolean nullMessage = ValidationUtils.isDuplicateError(null);

        // Assert
        assertTrue(duplicateError);
        assertFalse(notDuplicateError);
        assertFalse(nullMessage);
    }

    @Test
    @DisplayName("Debe identificar errores geográficos correctamente")
    void testIsGeographyError_WithGeographyMessages() {
        // Act
        boolean geographyError = ValidationUtils.isGeographyError("No se encontró la ciudad Bogotá");
        boolean notGeographyError = ValidationUtils.isGeographyError("Error de validación en el NIT");
        boolean nullMessage = ValidationUtils.isGeographyError(null);

        // Assert
        assertTrue(geographyError);
        assertFalse(notGeographyError);
        assertFalse(nullMessage);
    }

    @Test
    @DisplayName("Debe detectar campo desde errores de regla de negocio")
    void testDetectFieldFromBusinessRuleError_WithBusinessRuleMessages() {
        // Act
        String nitField = ValidationUtils.detectFieldFromBusinessRuleError("NIT: El número de verificación no es válido");
        String emailField = ValidationUtils.detectFieldFromBusinessRuleError("EMAIL: El formato del correo no es válido");
        String phoneField = ValidationUtils.detectFieldFromBusinessRuleError("PHONE: El número de teléfono no es válido");
        String unknownField = ValidationUtils.detectFieldFromBusinessRuleError("Error desconocido");
        String nullMessage = ValidationUtils.detectFieldFromBusinessRuleError(null);

        // Assert
        assertEquals("NIT", nitField);
        assertEquals("EMAIL", emailField);
        assertEquals("PHONE", phoneField);
        assertNull(unknownField);
        assertNull(nullMessage);
    }

    // ===== UTILIDADES DE PROCESAMIENTO =====

    @Test
    @DisplayName("Debe normalizar texto para comparación")
    void testNormalizeForComparison_WithDifferentCases() {
        // Act
        String normalized1 = ValidationUtils.normalizeForComparison("HOLA Mundo");
        String normalized2 = ValidationUtils.normalizeForComparison("hola mundo");
        String nullText = ValidationUtils.normalizeForComparison(null);

        // Assert
        assertEquals("hola mundo", normalized1);
        assertEquals("hola mundo", normalized2);
        assertNull(nullText);
    }

    @Test
    @DisplayName("Debe limpiar números de teléfono correctamente")
    void testCleanPhoneNumber_WithVariousFormats() {
        // Act
        String cleaned1 = ValidationUtils.cleanPhoneNumber("+57 300 123 4567");
        String cleaned2 = ValidationUtils.cleanPhoneNumber("(300) 123-4567");
        String cleaned3 = ValidationUtils.cleanPhoneNumber("300.123.4567");
        String nullPhone = ValidationUtils.cleanPhoneNumber(null);

        // Assert
        assertEquals("+573001234567", cleaned1);
        assertEquals("3001234567", cleaned2);
        assertEquals("3001234567", cleaned3);
        assertNull(nullPhone);
    }

    @Test
    @DisplayName("Debe parsear boolean opcional correctamente")
    void testParseOptionalBoolean_WithVariousValues() {
        // Act
        Boolean trueValue = ValidationUtils.parseOptionalBoolean("true");
        Boolean falseValue = ValidationUtils.parseOptionalBoolean("false");
        Boolean yesValue = ValidationUtils.parseOptionalBoolean("yes");
        Boolean noValue = ValidationUtils.parseOptionalBoolean("no");
        Boolean oneValue = ValidationUtils.parseOptionalBoolean("1");
        Boolean zeroValue = ValidationUtils.parseOptionalBoolean("0");
        Boolean invalidValue = ValidationUtils.parseOptionalBoolean("invalid");
        Boolean nullValue = ValidationUtils.parseOptionalBoolean(null);
        Boolean emptyValue = ValidationUtils.parseOptionalBoolean("");

        // Assert
        assertTrue(trueValue);
        assertFalse(falseValue);
        assertTrue(yesValue);
        assertFalse(noValue);
        assertTrue(oneValue);
        assertFalse(zeroValue);
        assertNull(invalidValue);
        assertNull(nullValue);
        assertNull(emptyValue);
    }
}
