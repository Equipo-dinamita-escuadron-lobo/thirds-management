package com.thirdsmanagement.thirds.unit.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.domain.model.ThirdType;

/**
 * Tests unitarios para la entidad ThirdType (tipo de tercero)
 */
class ThirdTypeUnitTest {

    private ThirdType thirdType;

    @BeforeEach
    void setUp() {
        // Arrange
        thirdType = ThirdType.builder()
            .entId("EMP001")
            .thirdTypeId(1L)
            .thirdTypeName("Cliente")
            .status(true)
            .build();
    }

    @Test
    @DisplayName("Debe crear tipo de tercero con datos válidos")
    void testThirdTypeCreation_WithValidData() {
        // Act
        // Los objetos ya están creados en setUp()

        // Assert
        assertNotNull(thirdType);
        assertEquals("EMP001", thirdType.getEntId());
        assertEquals(1L, thirdType.getThirdTypeId());
        assertEquals("Cliente", thirdType.getThirdTypeName());
        assertTrue(thirdType.getStatus());
    }

    @Test
    @DisplayName("Debe crear tipo de tercero con estado por defecto")
    void testThirdTypeCreation_WithDefaultStatus() {
        // Arrange & Act
        ThirdType newThirdType = ThirdType.builder()
            .entId("EMP001")
            .thirdTypeId(2L)
            .thirdTypeName("Proveedor")
            .build();

        // Assert
        assertTrue(newThirdType.getStatus());
    }

    @Test
    @DisplayName("Debe normalizar nombre preservando mayúsculas/minúsculas")
    void testGetNormalizedName_PreservesCase() {
        // Arrange
        thirdType.setThirdTypeName("Clíente");

        // Act
        String normalized = thirdType.getNormalizedName();

        // Assert
        assertEquals("Cliente", normalized);
    }

    @Test
    @DisplayName("Debe normalizar nombre con múltiples acentos")
    void testGetNormalizedName_WithMultipleAccents() {
        // Arrange
        thirdType.setThirdTypeName("Médicó");

        // Act
        String normalized = thirdType.getNormalizedName();

        // Assert
        assertEquals("Medico", normalized);
    }

    @Test
    @DisplayName("Debe normalizar nombre manteniendo mayúsculas")
    void testGetNormalizedName_MaintainsUppercase() {
        // Arrange
        thirdType.setThirdTypeName("CLIENTE");

        // Act
        String normalized = thirdType.getNormalizedName();

        // Assert
        assertEquals("CLIENTE", normalized);
    }

    @Test
    @DisplayName("Debe normalizar nombre manteniendo caso mixto")
    void testGetNormalizedName_MaintainsMixedCase() {
        // Arrange
        thirdType.setThirdTypeName("ClÍenTe");

        // Act
        String normalized = thirdType.getNormalizedName();

        // Assert
        assertEquals("ClIenTe", normalized);
    }

    @Test
    @DisplayName("Debe manejar nombre null correctamente")
    void testGetNormalizedName_WithNullName() {
        // Arrange
        thirdType.setThirdTypeName(null);

        // Act
        String normalizedName = thirdType.getNormalizedName();

        // Assert
        assertNull(normalizedName, "Debe retornar null cuando thirdTypeName es null");
    }

    @Test
    @DisplayName("Debe manejar nombre vacío")
    void testGetNormalizedName_WithEmptyName() {
        // Arrange
        thirdType.setThirdTypeName("");

        // Act
        String normalized = thirdType.getNormalizedName();

        // Assert
        assertEquals("", normalized);
    }

    @Test
    @DisplayName("Debe manejar nombre con caracteres especiales")
    void testGetNormalizedName_WithSpecialCharacters() {
        // Arrange
        thirdType.setThirdTypeName("Cliente@2023");

        // Act
        String normalized = thirdType.getNormalizedName();

        // Assert
        assertEquals("Cliente@2023", normalized);
    }
}
