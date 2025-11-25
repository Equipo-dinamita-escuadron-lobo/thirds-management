package com.thirdsmanagement.thirds.unit.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;

/**
 * Tests unitarios para la entidad Third (tercero)
 */
class ThirdUnitTest {

    private Third third;
    private TypeId typeId;
    private Set<ThirdType> thirdTypes;
    private ThirdType thirdType;

    @BeforeEach
    void setUp() {
        // Arrange
        typeId = TypeId.builder()
            .entId("EMP001")
            .typeId("CC")
            .typeIdname("Cédula de Ciudadanía")
            .build();

        thirdType = ThirdType.builder()
            .entId("EMP001")
            .thirdTypeId(1L)
            .thirdTypeName("Cliente")
            .build();

        thirdTypes = new HashSet<>();
        thirdTypes.add(thirdType);

        third = Third.builder()
            .thId(1L)
            .entId("EMP001")
            .typeId(typeId)
            .thirdTypes(thirdTypes)
            .personType(ePersonType.Natural)
            .names("Juan")
            .lastNames("Pérez")
            .idNumber(12345678L)
            .state(true)
            .email("juan.perez@email.com")
            .build();
    }

    @Test
    @DisplayName("Debe activar tercero al llamar método activate")
    void testActivate() {
        // Arrange
        third.setState(false);

        // Act
        third.activate();

        // Assert
        assertTrue(third.getState());
        assertTrue(third.isActive());
    }

    @Test
    @DisplayName("Debe desactivar tercero al llamar método deactivate")
    void testDeactivate() {
        // Arrange
        third.setState(true);

        // Act
        third.deactivate();

        // Assert
        assertFalse(third.getState());
        assertFalse(third.isActive());
    }

    @Test
    @DisplayName("Debe retornar true cuando tercero está activo")
    void testIsActive_WhenStateIsTrue() {
        // Arrange
        third.setState(true);

        // Act
        boolean result = third.isActive();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe retornar false cuando tercero está inactivo")
    void testIsActive_WhenStateIsFalse() {
        // Arrange
        third.setState(false);

        // Act
        boolean result = third.isActive();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false cuando estado del tercero es null")
    void testIsActive_WhenStateIsNull() {
        // Arrange
        third.setState(null);

        // Act
        boolean result = third.isActive();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar true cuando tipo de persona es jurídica")
    void testIsLegalEntity_WhenPersonTypeIsJuridica() {
        // Arrange
        third.setPersonType(ePersonType.Juridica);

        // Act
        boolean isLegal = third.isLegalEntity();
        boolean isNatural = third.isNaturalPerson();

        // Assert
        assertTrue(isLegal);
        assertFalse(isNatural);
    }

    @Test
    @DisplayName("Debe retornar true cuando tipo de persona es natural")
    void testIsNaturalPerson_WhenPersonTypeIsNatural() {
        // Arrange
        third.setPersonType(ePersonType.Natural);

        // Act
        boolean isNatural = third.isNaturalPerson();
        boolean isLegal = third.isLegalEntity();

        // Assert
        assertTrue(isNatural);
        assertFalse(isLegal);
    }

    @Test
    @DisplayName("Debe retornar true cuando tercero está en uso")
    void testIsInUse_WhenUsageCountGreaterThanZero() {
        // Arrange
        third.setUsageCount(5);

        // Act
        boolean result = third.isInUse();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe retornar false cuando tercero no está en uso")
    void testIsInUse_WhenUsageCountIsZero() {
        // Arrange
        third.setUsageCount(0);

        // Act
        boolean result = third.isInUse();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false cuando contador de uso es null")
    void testIsInUse_WhenUsageCountIsNull() {
        // Arrange
        third.setUsageCount(null);

        // Act
        boolean result = third.isInUse();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe crear tercero con datos válidos")
    void testThirdCreation_WithValidData() {
        // Act & Assert
        assertNotNull(third);
        assertEquals("EMP001", third.getEntId());
        assertEquals(typeId, third.getTypeId());
        assertEquals(ePersonType.Natural, third.getPersonType());
        assertEquals("Juan", third.getNames());
        assertEquals("Pérez", third.getLastNames());
        assertEquals(12345678L, third.getIdNumber());
        assertTrue(third.getState());
        assertEquals("juan.perez@email.com", third.getEmail());
        assertEquals(0, third.getUsageCount());
    }

    @Test
    @DisplayName("Debe crear tercero con contador de uso por defecto")
    void testThirdCreation_WithDefaultUsageCount() {
        // Arrange & Act
        Third newThird = Third.builder()
            .entId("EMP001")
            .typeId(typeId)
            .personType(ePersonType.Natural)
            .idNumber(12345678L)
            .build();

        // Assert
        assertEquals(0, newThird.getUsageCount());
    }

    @Test
    @DisplayName("Debe crear tercero con conjunto de tipos por defecto")
    void testThirdCreation_WithDefaultThirdTypes() {
        // Arrange & Act
        Third newThird = Third.builder()
            .entId("EMP001")
            .typeId(typeId)
            .personType(ePersonType.Natural)
            .idNumber(12345678L)
            .build();

        // Assert
        assertNotNull(newThird.getThirdTypes());
        assertTrue(newThird.getThirdTypes().isEmpty());
    }

    @Test
    @DisplayName("Debe manejar género null correctamente")
    void testThirdCreation_WithNullGender() {
        // Arrange & Act
        third.setGender(null);

        // Assert
        assertNull(third.getGender());
    }

    @Test
    @DisplayName("Debe manejar datos geográficos")
    void testThirdCreation_WithGeographicalData() {
        // Arrange
        Country country = Country.builder()
            .countryCode("CO")
            .countryName("Colombia")
            .build();

        State state = State.builder()
            .stateCode("CUN")
            .stateName("Cundinamarca")
            .countryCode("CO")
            .build();

        City city = City.builder()
            .cityCode("BOG")
            .cityName("Bogotá")
            .stateCode("CUN")
            .countryCode("CO")
            .build();

        // Act
        third.setCountry(country);
        third.setProvince(state);
        third.setCity(city);
        third.setAddress("Calle 123 # 45-67");

        // Assert
        assertEquals(country, third.getCountry());
        assertEquals(state, third.getProvince());
        assertEquals(city, third.getCity());
        assertEquals("Calle 123 # 45-67", third.getAddress());
    }
}
