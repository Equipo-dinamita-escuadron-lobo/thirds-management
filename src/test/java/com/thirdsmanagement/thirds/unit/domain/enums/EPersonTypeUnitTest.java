package com.thirdsmanagement.thirds.unit.domain.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.domain.enums.ePersonType;

/**
 * Tests unitarios para el enum ePersonType (tipo de persona)
 */
class EPersonTypeUnitTest {

    @Test
    @DisplayName("Debe retornar NATURAL para código válido de persona natural")
    void testFromCode_WithValidNaturalCode() {
        // Act
        ePersonType result = ePersonType.fromCode("NATURAL");

        // Assert
        assertEquals(ePersonType.Natural, result);
    }

    @Test
    @DisplayName("Debe retornar JURIDICA para código válido de persona jurídica")
    void testFromCode_WithValidJuridicaCode() {
        // Act
        ePersonType result = ePersonType.fromCode("JURIDICA");

        // Assert
        assertEquals(ePersonType.Juridica, result);
    }

    @Test
    @DisplayName("Debe retornar null para código null")
    void testFromCode_WithNullCode() {
        // Act
        ePersonType result = ePersonType.fromCode(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe retornar null para código inválido")
    void testFromCode_WithInvalidCode() {
        // Act
        ePersonType result = ePersonType.fromCode("INVALID");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe manejar códigos insensibles a mayúsculas")
    void testFromCode_WithCaseInsensitive() {
        // Act
        ePersonType result1 = ePersonType.fromCode("natural");
        ePersonType result2 = ePersonType.fromCode("Natural");
        ePersonType result3 = ePersonType.fromCode("NATURAL");

        // Assert
        assertEquals(ePersonType.Natural, result1);
        assertEquals(ePersonType.Natural, result2);
        assertEquals(ePersonType.Natural, result3);
    }

    @Test
    @DisplayName("Debe retornar NATURAL para descripción de persona natural")
    void testFromDescription_WithValidNaturalDescription() {
        // Act
        ePersonType result = ePersonType.fromDescription("Persona Natural");

        // Assert
        assertEquals(ePersonType.Natural, result);
    }

    @Test
    @DisplayName("Debe retornar JURIDICA para descripción de persona jurídica")
    void testFromDescription_WithValidJuridicaDescription() {
        // Act
        ePersonType result = ePersonType.fromDescription("Persona Jurídica");

        // Assert
        assertEquals(ePersonType.Juridica, result);
    }

    @Test
    @DisplayName("Debe retornar null para descripción null")
    void testFromDescription_WithNullDescription() {
        // Act
        ePersonType result = ePersonType.fromDescription(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe manejar descripciones insensibles a mayúsculas")
    void testFromDescription_WithCaseInsensitive() {
        // Act
        ePersonType result1 = ePersonType.fromDescription("persona natural");
        ePersonType result2 = ePersonType.fromDescription("Persona Natural");

        // Assert
        assertEquals(ePersonType.Natural, result1);
        assertEquals(ePersonType.Natural, result2);
    }

    @Test
    @DisplayName("Debe retornar true para validación de persona natural")
    void testIsNatural_ForNaturalPerson() {
        // Act
        boolean naturalIsNatural = ePersonType.Natural.isNatural();
        boolean naturalIsJuridica = ePersonType.Natural.isJuridica();

        // Assert
        assertTrue(naturalIsNatural);
        assertFalse(naturalIsJuridica);
    }

    @Test
    @DisplayName("Debe retornar true para validación de persona jurídica")
    void testIsJuridica_ForJuridicaPerson() {
        // Act
        boolean juridicaIsJuridica = ePersonType.Juridica.isJuridica();
        boolean juridicaIsNatural = ePersonType.Juridica.isNatural();

        // Assert
        assertTrue(juridicaIsJuridica);
        assertFalse(juridicaIsNatural);
    }

    @Test
    @DisplayName("Debe tener valores correctos de código")
    void testGetCode() {
        // Act
        String naturalCode = ePersonType.Natural.getCode();
        String juridicaCode = ePersonType.Juridica.getCode();

        // Assert
        assertEquals("NATURAL", naturalCode);
        assertEquals("JURIDICA", juridicaCode);
    }

    @Test
    @DisplayName("Debe tener valores correctos de descripción")
    void testGetDescription() {
        // Act
        String naturalDesc = ePersonType.Natural.getDescription();
        String juridicaDesc = ePersonType.Juridica.getDescription();

        // Assert
        assertEquals("Persona Natural", naturalDesc);
        assertEquals("Persona Jurídica", juridicaDesc);
    }

    @Test
    @DisplayName("Debe tener exactamente dos valores de enum")
    void testEnumValues() {
        // Act
        ePersonType[] values = ePersonType.values();

        // Assert
        assertEquals(2, values.length);
        assertEquals(ePersonType.Natural, values[0]);
        assertEquals(ePersonType.Juridica, values[1]);
    }

    @Test
    @DisplayName("Debe retornar descripción en toString")
    void testToString() {
        // Act
        String naturalToString = ePersonType.Natural.toString();
        String juridicaToString = ePersonType.Juridica.toString();

        // Assert
        assertEquals("Persona Natural", naturalToString);
        assertEquals("Persona Jurídica", juridicaToString);
    }
}
