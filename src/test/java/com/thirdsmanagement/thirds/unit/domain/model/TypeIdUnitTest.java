package com.thirdsmanagement.thirds.unit.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.domain.model.PersonClassification;
import com.thirdsmanagement.thirds.domain.model.TypeId;

/**
 * Tests unitarios para la entidad TypeId (tipo de identificación)
 */
class TypeIdUnitTest {

    private TypeId typeIdNatural;
    private TypeId typeIdLegal;
    private PersonClassification naturalPerson;
    private PersonClassification legalEntity;

    @BeforeEach
    void setUp() {
        // Arrange
        naturalPerson = PersonClassification.NATURAL_PERSON;
        legalEntity = PersonClassification.LEGAL_ENTITY;

        typeIdNatural = TypeId.builder()
            .id(1L)
            .entId("EMP001")
            .typeId("CC")
            .typeIdname("Cédula de Ciudadanía")
            .status(true)
            .classification(naturalPerson)
            .build();

        typeIdLegal = TypeId.builder()
            .id(2L)
            .entId("EMP001")
            .typeId("NIT")
            .typeIdname("Número de Identificación Tributaria")
            .status(true)
            .classification(legalEntity)
            .build();
    }

    @Test
    @DisplayName("Debe crear tipo de identificación con datos válidos")
    void testTypeIdCreation_WithValidData() {
        // Act & Assert
        assertNotNull(typeIdNatural);
        assertEquals(1L, typeIdNatural.getId());
        assertEquals("EMP001", typeIdNatural.getEntId());
        assertEquals("CC", typeIdNatural.getTypeId());
        assertEquals("Cédula de Ciudadanía", typeIdNatural.getTypeIdname());
        assertTrue(typeIdNatural.getStatus());
        assertEquals(naturalPerson, typeIdNatural.getClassification());
    }

    @Test
    @DisplayName("Debe retornar true para clasificación de persona natural")
    void testIsValidForNaturalPerson_WhenNaturalPerson() {
        // Act
        boolean isValidForNatural = typeIdNatural.isValidForNaturalPerson();
        boolean isValidForLegal = typeIdNatural.isValidForLegalEntity();

        // Assert
        assertTrue(isValidForNatural);
        assertFalse(isValidForLegal);
    }

    @Test
    @DisplayName("Debe retornar true para clasificación de persona jurídica")
    void testIsValidForLegalEntity_WhenLegalEntity() {
        // Act
        boolean isValidForLegal = typeIdLegal.isValidForLegalEntity();
        boolean isValidForNatural = typeIdLegal.isValidForNaturalPerson();

        // Assert
        assertTrue(isValidForLegal);
        assertFalse(isValidForNatural);
    }

    @Test
    @DisplayName("Debe retornar false cuando clasificación es null")
    void testIsValidForPersons_WhenClassificationIsNull() {
        // Arrange
        TypeId typeIdNull = TypeId.builder()
            .id(3L)
            .entId("EMP001")
            .typeId("XX")
            .typeIdname("Tipo Desconocido")
            .classification(null)
            .build();

        // Act
        boolean isValidForNatural = typeIdNull.isValidForNaturalPerson();
        boolean isValidForLegal = typeIdNull.isValidForLegalEntity();

        // Assert
        assertFalse(isValidForNatural);
        assertFalse(isValidForLegal);
    }

    @Test
    @DisplayName("Debe normalizar código de tipo de identificación")
    void testGetNormalizedTypeId() {
        // Arrange
        typeIdNatural.setTypeId("CC-123");

        // Act
        String normalized = typeIdNatural.getNormalizedTypeId();

        // Assert
        assertEquals("CC123", normalized);
    }

    @Test
    @DisplayName("Debe normalizar código con caracteres especiales")
    void testGetNormalizedTypeId_WithSpecialCharacters() {
        // Arrange
        typeIdNatural.setTypeId("NIT@2023#");

        // Act
        String normalized = typeIdNatural.getNormalizedTypeId();

        // Assert
        assertEquals("NIT2023", normalized);
    }

    @Test
    @DisplayName("Debe normalizar código convirtiendo a mayúsculas")
    void testGetNormalizedTypeId_ConvertsToUpperCase() {
        // Arrange
        typeIdNatural.setTypeId("Cc");

        // Act
        String normalized = typeIdNatural.getNormalizedTypeId();

        // Assert
        assertEquals("CC", normalized);
    }

    @Test
    @DisplayName("Debe manejar código null correctamente")
    void testGetNormalizedTypeId_WithNullTypeId() {
        // Arrange
        typeIdNatural.setTypeId(null);

        // Act
        String normalized = typeIdNatural.getNormalizedTypeId();

        // Assert
        assertNull(normalized, "Debe retornar null cuando typeId es null");
    }

    @Test
    @DisplayName("Debe manejar código vacío")
    void testGetNormalizedTypeId_WithEmptyTypeId() {
        // Arrange
        typeIdNatural.setTypeId("");

        // Act
        String normalized = typeIdNatural.getNormalizedTypeId();

        // Assert
        assertEquals("", normalized);
    }

    @Test
    @DisplayName("Debe crear tipo de identificación con estado por defecto")
    void testTypeIdCreation_WithDefaultStatus() {
        // Arrange & Act
        TypeId newTypeId = TypeId.builder()
            .id(4L)
            .entId("EMP001")
            .typeId("TI")
            .typeIdname("Tarjeta de Identidad")
            .classification(naturalPerson)
            .build();

        // Assert
        assertTrue(newTypeId.getStatus());
    }

    @Test
    @DisplayName("Debe manejar código con caracteres unicode")
    void testGetNormalizedTypeId_WithUnicode() {
        // Arrange
        typeIdNatural.setTypeId("CC_ñ");

        // Act
        String normalized = typeIdNatural.getNormalizedTypeId();

        // Assert
        assertEquals("CCÑ", normalized);
    }
}
