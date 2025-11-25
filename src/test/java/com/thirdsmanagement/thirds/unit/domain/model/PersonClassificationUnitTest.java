package com.thirdsmanagement.thirds.unit.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.domain.model.PersonClassification;

/**
 * Tests unitarios para el enum PersonClassification (clasificación de persona)
 */
class PersonClassificationUnitTest {

    @Test
    @DisplayName("Debe retornar NATURAL_PERSON para código válido de persona natural")
    void testFromCode_WithValidNaturalPersonCode() {
        // Act
        PersonClassification result = PersonClassification.fromCode("NATURAL_PERSON");

        // Assert
        assertEquals(PersonClassification.NATURAL_PERSON, result);
    }

    @Test
    @DisplayName("Debe retornar LEGAL_ENTITY para código válido de persona jurídica")
    void testFromCode_WithValidLegalEntityCode() {
        // Act
        PersonClassification result = PersonClassification.fromCode("LEGAL_ENTITY");

        // Assert
        assertEquals(PersonClassification.LEGAL_ENTITY, result);
    }

    @Test
    @DisplayName("Debe retornar null para código null")
    void testFromCode_WithNullCode() {
        // Act
        PersonClassification result = PersonClassification.fromCode(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe retornar null para código vacío")
    void testFromCode_WithEmptyCode() {
        // Act
        PersonClassification result = PersonClassification.fromCode("");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe retornar null para código en blanco")
    void testFromCode_WithBlankCode() {
        // Act
        PersonClassification result = PersonClassification.fromCode("   ");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe retornar null para código inválido")
    void testFromCode_WithInvalidCode() {
        // Act
        PersonClassification result = PersonClassification.fromCode("INVALID_CODE");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe manejar búsqueda insensible a mayúsculas")
    void testFromCode_WithCaseInsensitive() {
        // Act
        PersonClassification result1 = PersonClassification.fromCode("natural_person");
        PersonClassification result2 = PersonClassification.fromCode("Natural_Person");
        PersonClassification result3 = PersonClassification.fromCode("NATURAL_PERSON");

        // Assert
        assertEquals(PersonClassification.NATURAL_PERSON, result1);
        assertEquals(PersonClassification.NATURAL_PERSON, result2);
        assertEquals(PersonClassification.NATURAL_PERSON, result3);
    }

    @Test
    @DisplayName("Debe retornar true para código válido de persona natural")
    void testIsValidCode_WithValidNaturalPersonCode() {
        // Act
        boolean isValid = PersonClassification.isValidCode("NATURAL_PERSON");

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Debe retornar true para código válido de persona jurídica")
    void testIsValidCode_WithValidLegalEntityCode() {
        // Act
        boolean isValid = PersonClassification.isValidCode("LEGAL_ENTITY");

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Debe retornar false para código null")
    void testIsValidCode_WithNullCode() {
        // Act
        boolean isValid = PersonClassification.isValidCode(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar false para código vacío")
    void testIsValidCode_WithEmptyCode() {
        // Act
        boolean isValid = PersonClassification.isValidCode("");

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar false para código inválido")
    void testIsValidCode_WithInvalidCode() {
        // Act
        boolean isValid = PersonClassification.isValidCode("INVALID");

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar true para validación de persona natural")
    void testIsValidForNaturalPerson_ForNaturalPerson() {
        // Act
        boolean isValidForNatural = PersonClassification.NATURAL_PERSON.isValidForNaturalPerson();
        boolean isValidForLegal = PersonClassification.NATURAL_PERSON.isValidForLegalEntity();

        // Assert
        assertTrue(isValidForNatural);
        assertFalse(isValidForLegal);
    }

    @Test
    @DisplayName("Debe retornar true para validación de persona jurídica")
    void testIsValidForLegalEntity_ForLegalEntity() {
        // Act
        boolean isValidForLegal = PersonClassification.LEGAL_ENTITY.isValidForLegalEntity();
        boolean isValidForNatural = PersonClassification.LEGAL_ENTITY.isValidForNaturalPerson();

        // Assert
        assertTrue(isValidForLegal);
        assertFalse(isValidForNatural);
    }

    @Test
    @DisplayName("Debe tener valores correctos de código")
    void testGetCode() {
        // Act & Assert
        assertEquals("NATURAL_PERSON", PersonClassification.NATURAL_PERSON.getCode());
        assertEquals("LEGAL_ENTITY", PersonClassification.LEGAL_ENTITY.getCode());
    }

    @Test
    @DisplayName("Debe tener valores correctos de descripción")
    void testGetDescription() {
        // Act & Assert
        assertEquals("Persona Natural", PersonClassification.NATURAL_PERSON.getDescription());
        assertEquals("Persona Jurídica", PersonClassification.LEGAL_ENTITY.getDescription());
    }

    @Test
    @DisplayName("Debe tener exactamente dos valores de enum")
    void testEnumValues() {
        // Act
        PersonClassification[] values = PersonClassification.values();

        // Assert
        assertEquals(2, values.length);
        assertEquals(PersonClassification.NATURAL_PERSON, values[0]);
        assertEquals(PersonClassification.LEGAL_ENTITY, values[1]);
    }

    @Test
    @DisplayName("Debe manejar códigos con espacios")
    void testFromCode_WithSpaces() {
        // Act
        PersonClassification result = PersonClassification.fromCode("  NATURAL_PERSON  ");

        // Assert
        assertEquals(PersonClassification.NATURAL_PERSON, result);
    }
}
