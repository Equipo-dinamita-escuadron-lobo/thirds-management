package com.thirdsmanagement.thirds.unit.domain.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.domain.enums.ExportableField;

/**
 * Tests unitarios para el enum ExportableField (campos exportables)
 */
class ExportableFieldUnitTest {

    @Test
    @DisplayName("Debe retornar GENDER para campo gender")
    void testFromFieldName_WithValidGenderField() {
        // Act
        ExportableField result = ExportableField.fromFieldName("gender");

        // Assert
        assertEquals(ExportableField.GENDER, result);
    }

    @Test
    @DisplayName("Debe retornar COUNTRY para campo country")
    void testFromFieldName_WithValidCountryField() {
        // Act
        ExportableField result = ExportableField.fromFieldName("country");

        // Assert
        assertEquals(ExportableField.COUNTRY, result);
    }

    @Test
    @DisplayName("Debe retornar STATE para campo state")
    void testFromFieldName_WithValidStateField() {
        // Act
        ExportableField result = ExportableField.fromFieldName("state");

        // Assert
        assertEquals(ExportableField.STATE, result);
    }

    @Test
    @DisplayName("Debe retornar CITY para campo city")
    void testFromFieldName_WithValidCityField() {
        // Act
        ExportableField result = ExportableField.fromFieldName("city");

        // Assert
        assertEquals(ExportableField.CITY, result);
    }

    @Test
    @DisplayName("Debe retornar null para campo null")
    void testFromFieldName_WithNullField() {
        // Act
        ExportableField result = ExportableField.fromFieldName(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe retornar null para campo inválido")
    void testFromFieldName_WithInvalidField() {
        // Act
        ExportableField result = ExportableField.fromFieldName("invalid_field");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe manejar búsqueda insensible a mayúsculas")
    void testFromFieldName_WithCaseInsensitive() {
        // Act
        ExportableField result1 = ExportableField.fromFieldName("GENDER");
        ExportableField result2 = ExportableField.fromFieldName("Gender");
        ExportableField result3 = ExportableField.fromFieldName("COUNTRY");
        ExportableField result4 = ExportableField.fromFieldName("State");

        // Assert
        assertEquals(ExportableField.GENDER, result1);
        assertEquals(ExportableField.GENDER, result2);
        assertEquals(ExportableField.COUNTRY, result3);
        assertEquals(ExportableField.STATE, result4);
    }

    @Test
    @DisplayName("Debe tener valores correctos de display name")
    void testGetDisplayName() {
        // Act
        String genderDisplay = ExportableField.GENDER.getDisplayName();
        String countryDisplay = ExportableField.COUNTRY.getDisplayName();
        String stateDisplay = ExportableField.STATE.getDisplayName();
        String cityDisplay = ExportableField.CITY.getDisplayName();

        // Assert
        assertEquals("Género", genderDisplay);
        assertEquals("País", countryDisplay);
        assertEquals("Departamento", stateDisplay);
        assertEquals("Ciudad", cityDisplay);
    }

    @Test
    @DisplayName("Debe tener valores correctos de field name")
    void testGetFieldName() {
        // Act
        String genderField = ExportableField.GENDER.getFieldName();
        String countryField = ExportableField.COUNTRY.getFieldName();
        String stateField = ExportableField.STATE.getFieldName();
        String cityField = ExportableField.CITY.getFieldName();

        // Assert
        assertEquals("gender", genderField);
        assertEquals("country", countryField);
        assertEquals("state", stateField);
        assertEquals("city", cityField);
    }

    @Test
    @DisplayName("Debe tener exactamente cuatro valores de enum")
    void testEnumValues() {
        // Act
        ExportableField[] values = ExportableField.values();

        // Assert
        assertEquals(4, values.length);
        assertEquals(ExportableField.GENDER, values[0]);
        assertEquals(ExportableField.COUNTRY, values[1]);
        assertEquals(ExportableField.STATE, values[2]);
        assertEquals(ExportableField.CITY, values[3]);
    }
}
