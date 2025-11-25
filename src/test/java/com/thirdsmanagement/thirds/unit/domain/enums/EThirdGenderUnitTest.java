package com.thirdsmanagement.thirds.unit.domain.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.domain.enums.eThirdGender;

/**
 * Tests unitarios para el enum eThirdGender (género de tercero)
 */
class EThirdGenderUnitTest {

    @Test
    @DisplayName("Debe retornar MASCULINO para código válido masculino")
    void testFromCode_WithValidMasculinoCode() {
        // Act
        eThirdGender result = eThirdGender.fromCode("M");

        // Assert
        assertEquals(eThirdGender.Masculino, result);
    }

    @Test
    @DisplayName("Debe retornar FEMENINO para código válido femenino")
    void testFromCode_WithValidFemeninoCode() {
        // Act
        eThirdGender result = eThirdGender.fromCode("F");

        // Assert
        assertEquals(eThirdGender.Femenino, result);
    }

    @Test
    @DisplayName("Debe retornar OTRO para código válido otro")
    void testFromCode_WithValidOtroCode() {
        // Act
        eThirdGender result = eThirdGender.fromCode("O");

        // Assert
        assertEquals(eThirdGender.Otro, result);
    }

    @Test
    @DisplayName("Debe retornar null para código null")
    void testFromCode_WithNullCode() {
        // Act
        eThirdGender result = eThirdGender.fromCode(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe retornar null para código inválido")
    void testFromCode_WithInvalidCode() {
        // Act
        eThirdGender result = eThirdGender.fromCode("X");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe manejar códigos insensibles a mayúsculas")
    void testFromCode_WithCaseInsensitive() {
        // Act
        eThirdGender result1 = eThirdGender.fromCode("m");
        eThirdGender result2 = eThirdGender.fromCode("M");
        eThirdGender result3 = eThirdGender.fromCode("f");
        eThirdGender result4 = eThirdGender.fromCode("o");

        // Assert
        assertEquals(eThirdGender.Masculino, result1);
        assertEquals(eThirdGender.Masculino, result2);
        assertEquals(eThirdGender.Femenino, result3);
        assertEquals(eThirdGender.Otro, result4);
    }

    @Test
    @DisplayName("Debe retornar MASCULINO para descripción masculina")
    void testFromDescription_WithValidMasculinoDescription() {
        // Act
        eThirdGender result = eThirdGender.fromDescription("Masculino");

        // Assert
        assertEquals(eThirdGender.Masculino, result);
    }

    @Test
    @DisplayName("Debe retornar FEMENINO para descripción femenina")
    void testFromDescription_WithValidFemeninoDescription() {
        // Act
        eThirdGender result = eThirdGender.fromDescription("Femenino");

        // Assert
        assertEquals(eThirdGender.Femenino, result);
    }

    @Test
    @DisplayName("Debe retornar OTRO para descripción otro")
    void testFromDescription_WithValidOtroDescription() {
        // Act
        eThirdGender result = eThirdGender.fromDescription("Otro/Preferir no decir");

        // Assert
        assertEquals(eThirdGender.Otro, result);
    }

    @Test
    @DisplayName("Debe retornar null para descripción null")
    void testFromDescription_WithNullDescription() {
        // Act
        eThirdGender result = eThirdGender.fromDescription(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe manejar coincidencias parciales de descripción")
    void testFromDescription_WithPartialDescription() {
        // Act
        eThirdGender result = eThirdGender.fromDescription("Otro");

        // Assert
        assertNull(result); // No debe coincidir con descripciones parciales
    }

    @Test
    @DisplayName("Debe retornar true para códigos válidos")
    void testIsValidCode_WithValidCodes() {
        // Act
        boolean mValid = eThirdGender.isValidCode("M");
        boolean fValid = eThirdGender.isValidCode("F");
        boolean oValid = eThirdGender.isValidCode("O");

        // Assert
        assertTrue(mValid);
        assertTrue(fValid);
        assertTrue(oValid);
    }

    @Test
    @DisplayName("Debe retornar false para códigos inválidos")
    void testIsValidCode_WithInvalidCodes() {
        // Act
        boolean xValid = eThirdGender.isValidCode("X");
        boolean nullValid = eThirdGender.isValidCode(null);
        boolean emptyValid = eThirdGender.isValidCode("");

        // Assert
        assertFalse(xValid);
        assertFalse(nullValid);
        assertFalse(emptyValid);
    }

    @Test
    @DisplayName("Debe retornar true para validación masculina")
    void testIsMasculino_ForMasculino() {
        // Act
        boolean masculinoIsMasculino = eThirdGender.Masculino.isMasculino();
        boolean masculinoIsFemenino = eThirdGender.Masculino.isFemenino();
        boolean masculinoIsOtro = eThirdGender.Masculino.isOtro();

        // Assert
        assertTrue(masculinoIsMasculino);
        assertFalse(masculinoIsFemenino);
        assertFalse(masculinoIsOtro);
    }

    @Test
    @DisplayName("Debe retornar true para validación femenina")
    void testIsFemenino_ForFemenino() {
        // Act
        boolean femeninoIsFemenino = eThirdGender.Femenino.isFemenino();
        boolean femeninoIsMasculino = eThirdGender.Femenino.isMasculino();
        boolean femeninoIsOtro = eThirdGender.Femenino.isOtro();

        // Assert
        assertTrue(femeninoIsFemenino);
        assertFalse(femeninoIsMasculino);
        assertFalse(femeninoIsOtro);
    }

    @Test
    @DisplayName("Debe retornar true para validación de otro género")
    void testIsOtro_ForOtro() {
        // Act
        boolean otroIsOtro = eThirdGender.Otro.isOtro();
        boolean otroIsMasculino = eThirdGender.Otro.isMasculino();
        boolean otroIsFemenino = eThirdGender.Otro.isFemenino();

        // Assert
        assertTrue(otroIsOtro);
        assertFalse(otroIsMasculino);
        assertFalse(otroIsFemenino);
    }

    @Test
    @DisplayName("Debe tener valores correctos de código")
    void testGetCode() {
        // Act
        String masculinoCode = eThirdGender.Masculino.getCode();
        String femeninoCode = eThirdGender.Femenino.getCode();
        String otroCode = eThirdGender.Otro.getCode();

        // Assert
        assertEquals("M", masculinoCode);
        assertEquals("F", femeninoCode);
        assertEquals("O", otroCode);
    }

    @Test
    @DisplayName("Debe tener valores correctos de descripción")
    void testGetDescription() {
        // Act
        String masculinoDesc = eThirdGender.Masculino.getDescription();
        String femeninoDesc = eThirdGender.Femenino.getDescription();
        String otroDesc = eThirdGender.Otro.getDescription();

        // Assert
        assertEquals("Masculino", masculinoDesc);
        assertEquals("Femenino", femeninoDesc);
        assertEquals("Otro/Preferir no decir", otroDesc);
    }

    @Test
    @DisplayName("Debe tener exactamente tres valores de enum")
    void testEnumValues() {
        // Act
        eThirdGender[] values = eThirdGender.values();

        // Assert
        assertEquals(3, values.length);
        assertEquals(eThirdGender.Masculino, values[0]);
        assertEquals(eThirdGender.Femenino, values[1]);
        assertEquals(eThirdGender.Otro, values[2]);
    }

    @Test
    @DisplayName("Debe retornar descripción en toString")
    void testToString() {
        // Act
        String masculinoToString = eThirdGender.Masculino.toString();
        String femeninoToString = eThirdGender.Femenino.toString();
        String otroToString = eThirdGender.Otro.toString();

        // Assert
        assertEquals("Masculino", masculinoToString);
        assertEquals("Femenino", femeninoToString);
        assertEquals("Otro/Preferir no decir", otroToString);
    }
}
