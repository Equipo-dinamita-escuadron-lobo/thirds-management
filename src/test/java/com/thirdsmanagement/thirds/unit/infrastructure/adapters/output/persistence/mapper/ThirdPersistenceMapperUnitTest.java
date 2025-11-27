package com.thirdsmanagement.thirds.unit.infrastructure.adapters.output.persistence.mapper;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.ThirdPersistenceMapper;

@SpringBootTest
class ThirdPersistenceMapperUnitTest {

    @Autowired
    private ThirdPersistenceMapper mapper;

    private String entId;
    private TypeId typeId;
    private TypeIdEntity typeIdEntity;
    private Country country;
    private State state;
    private City city;

    @BeforeEach
    void setUp() {
        entId = "ENT001";

        typeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .status(true)
                .build();

        typeIdEntity = TypeIdEntity.builder()
                .id(1L)
                .tiId("CC")
                .tiName("Cédula de Ciudadanía")
                .tientId(entId)
                .status(true)
                .build();

        country = Country.builder()
                .countryCode("CO")
                .countryName("Colombia")
                .build();

        state = State.builder()
                .stateCode("11")
                .stateName("Bogotá D.C.")
                .countryCode("CO")
                .country(country)
                .build();

        city = City.builder()
                .cityCode("11001")
                .cityName("Bogotá")
                .stateCode("11")
                .countryCode("CO")
                .state(state)
                .build();
    }

    // ==================== toThirdEntity ====================

    @Test
    @DisplayName("Debe mapear Third a ThirdEntity correctamente")
    void testToThirdEntity() {
        // Arrange
        Third third = Third.builder()
                .thId(1L)
                .entId(entId)
                .typeId(typeId)
                .personType(ePersonType.Natural)
                .names("Juan")
                .lastNames("Pérez")
                .gender(eThirdGender.Masculino)
                .idNumber(123456789L)
                .verificationNumber(0L)
                .state(true)
                .country(country)
                .province(state)
                .city(city)
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("juan.perez@email.com")
                .build();

        // Act
        ThirdEntity result = mapper.toThirdEntity(third);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getThId());
        assertEquals(entId, result.getEntId());
        assertEquals(ePersonType.Natural, result.getPersonType());
        assertEquals("Juan", result.getNames());
        assertEquals("Pérez", result.getLastNames());
        assertEquals(123456789L, result.getIdNumber());
        assertEquals("CO", result.getCountry());
        assertEquals("11", result.getProvince());
        assertEquals("11001", result.getCity());
        assertEquals("Calle 123", result.getAddress());
        assertTrue(result.getState());
    }

    @Test
    @DisplayName("Debe mapear Third sin geografía a ThirdEntity")
    void testToThirdEntityWithoutGeography() {
        // Arrange
        Third third = Third.builder()
                .thId(2L)
                .entId(entId)
                .typeId(typeId)
                .personType(ePersonType.Juridica)
                .socialReason("Empresa S.A.S.")
                .idNumber(900123456L)
                .verificationNumber(7L)
                .state(true)
                .build();

        // Act
        ThirdEntity result = mapper.toThirdEntity(third);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getThId());
        assertEquals("Empresa S.A.S.", result.getSocialReason());
        assertNull(result.getCountry());
        assertNull(result.getProvince());
        assertNull(result.getCity());
    }

    // ==================== toThird ====================

    @Test
    @DisplayName("Debe mapear ThirdEntity a Third correctamente")
    void testToThird() {
        // Arrange
        ThirdEntity entity = ThirdEntity.builder()
                .thId(3L)
                .entId(entId)
                .typeId(typeIdEntity)
                .personType(ePersonType.Natural)
                .names("María")
                .lastNames("García")
                .gender(eThirdGender.Femenino.name())
                .idNumber(987654321L)
                .verificationNumber(0L)
                .state(true)
                .country("CO")
                .province("05")
                .city("05001")
                .address("Carrera 45")
                .phoneNumber("3109876543")
                .email("maria.garcia@email.com")
                .build();

        // Act
        Third result = mapper.toThird(entity);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getThId());
        assertEquals(entId, result.getEntId());
        assertNotNull(result.getTypeId());
        assertEquals("CC", result.getTypeId().getTypeId());
        assertEquals(ePersonType.Natural, result.getPersonType());
        assertEquals("María", result.getNames());
        assertEquals("García", result.getLastNames());
        assertEquals(987654321L, result.getIdNumber());
        assertEquals("Carrera 45", result.getAddress());
        assertTrue(result.isActive());
    }

    @Test
    @DisplayName("Debe mapear ThirdEntity sin typeId a Third")
    void testToThirdWithoutTypeId() {
        // Arrange
        ThirdEntity entity = ThirdEntity.builder()
                .thId(4L)
                .entId(entId)
                .typeId(null)
                .personType(ePersonType.Juridica)
                .socialReason("Corporación XYZ")
                .idNumber(800111222L)
                .state(false)
                .build();

        // Act
        Third result = mapper.toThird(entity);

        // Assert
        assertNotNull(result);
        assertEquals(4L, result.getThId());
        assertNull(result.getTypeId());
        assertEquals("Corporación XYZ", result.getSocialReason());
        assertFalse(result.isActive());
    }

    // ==================== mapCountryToString ====================

    @Test
    @DisplayName("Debe mapear Country a String con código correcto")
    void testMapCountryToString() {
        // Arrange
        Country country = Country.builder()
                .countryCode("US")
                .countryName("United States")
                .build();

        // Act
        String result = mapper.mapCountryToString(country);

        // Assert
        assertEquals("US", result);
    }

    @Test
    @DisplayName("Debe retornar null cuando Country es null")
    void testMapCountryToStringWhenNull() {
        // Act
        String result = mapper.mapCountryToString(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe mapear Country con código vacío")
    void testMapCountryToStringWithEmptyCode() {
        // Arrange
        Country country = Country.builder()
                .countryCode("")
                .countryName("Sin código")
                .build();

        // Act
        String result = mapper.mapCountryToString(country);

        // Assert
        assertEquals("", result);
    }

    // ==================== mapStateToString ====================

    @Test
    @DisplayName("Debe mapear State a String con código correcto")
    void testMapStateToString() {
        // Arrange
        State state = State.builder()
                .stateCode("ANT")
                .stateName("Antioquia")
                .countryCode("CO")
                .build();

        // Act
        String result = mapper.mapStateToString(state);

        // Assert
        assertEquals("ANT", result);
    }

    @Test
    @DisplayName("Debe retornar null cuando State es null")
    void testMapStateToStringWhenNull() {
        // Act
        String result = mapper.mapStateToString(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe mapear State con código vacío")
    void testMapStateToStringWithEmptyCode() {
        // Arrange
        State state = State.builder()
                .stateCode("")
                .stateName("Sin código")
                .countryCode("CO")
                .build();

        // Act
        String result = mapper.mapStateToString(state);

        // Assert
        assertEquals("", result);
    }

    // ==================== mapCityToString ====================

    @Test
    @DisplayName("Debe mapear City a String con código correcto")
    void testMapCityToString() {
        // Arrange
        City city = City.builder()
                .cityCode("05001")
                .cityName("Medellín")
                .stateCode("ANT")
                .countryCode("CO")
                .build();

        // Act
        String result = mapper.mapCityToString(city);

        // Assert
        assertEquals("05001", result);
    }

    @Test
    @DisplayName("Debe retornar null cuando City es null")
    void testMapCityToStringWhenNull() {
        // Act
        String result = mapper.mapCityToString(null);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe mapear City con código vacío")
    void testMapCityToStringWithEmptyCode() {
        // Arrange
        City city = City.builder()
                .cityCode("")
                .cityName("Sin código")
                .stateCode("ANT")
                .countryCode("CO")
                .build();

        // Act
        String result = mapper.mapCityToString(city);

        // Assert
        assertEquals("", result);
    }

    // ==================== Conversiones completas ====================

    @Test
    @DisplayName("Debe preservar geografía completa en conversión Third to Entity")
    void testGeographyPreservationInToEntity() {
        // Arrange
        Third third = Third.builder()
                .thId(5L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .names("Carlos")
                .lastNames("Rodríguez")
                .idNumber(111222333L)
                .state(true)
                .country(country)
                .province(state)
                .city(city)
                .build();

        // Act
        ThirdEntity result = mapper.toThirdEntity(third);

        // Assert
        assertEquals("CO", result.getCountry());
        assertEquals("11", result.getProvince());
        assertEquals("11001", result.getCity());
    }

    @Test
    @DisplayName("Debe mapear persona jurídica correctamente")
    void testLegalEntityMapping() {
        // Arrange
        Third third = Third.builder()
                .thId(6L)
                .entId(entId)
                .typeId(typeId)
                .personType(ePersonType.Juridica)
                .socialReason("Compañía Nacional S.A.")
                .idNumber(900555666L)
                .verificationNumber(3L)
                .state(true)
                .address("Av. Principal 100")
                .phoneNumber("6012345678")
                .email("contacto@compania.com")
                .build();

        // Act
        ThirdEntity result = mapper.toThirdEntity(third);

        // Assert
        assertNotNull(result);
        assertEquals(ePersonType.Juridica, result.getPersonType());
        assertEquals("Compañía Nacional S.A.", result.getSocialReason());
        assertNull(result.getNames());
        assertNull(result.getLastNames());
        assertEquals(3L, result.getVerificationNumber());
    }

    @Test
    @DisplayName("Debe mapear persona natural sin género")
    void testNaturalPersonWithoutGender() {
        // Arrange
        Third third = Third.builder()
                .thId(7L)
                .entId(entId)
                .typeId(typeId)
                .personType(ePersonType.Natural)
                .names("Ana")
                .lastNames("López")
                .gender(null)
                .idNumber(444555666L)
                .state(true)
                .build();

        // Act
        ThirdEntity result = mapper.toThirdEntity(third);

        // Assert
        assertNotNull(result);
        assertEquals(ePersonType.Natural, result.getPersonType());
        assertEquals("Ana", result.getNames());
        assertEquals("López", result.getLastNames());
        assertNull(result.getGender());
    }

    @Test
    @DisplayName("Debe mapear Third con todos los campos null excepto obligatorios")
    void testMinimalThirdMapping() {
        // Arrange
        Third third = Third.builder()
                .thId(8L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .idNumber(999888777L)
                .state(true)
                .build();

        // Act
        ThirdEntity result = mapper.toThirdEntity(third);

        // Assert
        assertNotNull(result);
        assertEquals(8L, result.getThId());
        assertEquals(entId, result.getEntId());
        assertEquals(ePersonType.Natural, result.getPersonType());
        assertEquals(999888777L, result.getIdNumber());
        assertTrue(result.getState());
        assertNull(result.getNames());
        assertNull(result.getLastNames());
        assertNull(result.getSocialReason());
        assertNull(result.getCountry());
    }

    @Test
    @DisplayName("Debe mapear ThirdEntity inactivo a Third")
    void testInactiveThirdEntityMapping() {
        // Arrange
        ThirdEntity entity = ThirdEntity.builder()
                .thId(9L)
                .entId(entId)
                .typeId(typeIdEntity)
                .personType(ePersonType.Natural)
                .names("Pedro")
                .lastNames("Martínez")
                .idNumber(777666555L)
                .state(false)
                .build();

        // Act
        Third result = mapper.toThird(entity);

        // Assert
        assertNotNull(result);
        assertEquals(9L, result.getThId());
        assertFalse(result.isActive());
    }

    @Test
    @DisplayName("Debe mapear Third con dígito de verificación cero")
    void testThirdWithZeroVerificationDigit() {
        // Arrange
        Third third = Third.builder()
                .thId(10L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .names("Luis")
                .lastNames("Gómez")
                .idNumber(123456789L)
                .verificationNumber(0L)
                .state(true)
                .build();

        // Act
        ThirdEntity result = mapper.toThirdEntity(third);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getVerificationNumber());
    }

    @Test
    @DisplayName("Debe mapear Third con solo país sin estado ni ciudad")
    void testThirdWithOnlyCountry() {
        // Arrange
        Third third = Third.builder()
                .thId(11L)
                .entId(entId)
                .personType(ePersonType.Juridica)
                .socialReason("Empresa Internacional")
                .idNumber(800999888L)
                .state(true)
                .country(country)
                .province(null)
                .city(null)
                .build();

        // Act
        ThirdEntity result = mapper.toThirdEntity(third);

        // Assert
        assertNotNull(result);
        assertEquals("CO", result.getCountry());
        assertNull(result.getProvince());
        assertNull(result.getCity());
    }

    @Test
    @DisplayName("Debe mapear ThirdEntity con datos de contacto completos")
    void testThirdEntityWithCompleteContactInfo() {
        // Arrange
        ThirdEntity entity = ThirdEntity.builder()
                .thId(12L)
                .entId(entId)
                .typeId(typeIdEntity)
                .personType(ePersonType.Natural)
                .names("Laura")
                .lastNames("Torres")
                .idNumber(333444555L)
                .state(true)
                .address("Calle 50 #30-20")
                .phoneNumber("3157891234")
                .email("laura.torres@correo.com")
                .build();

        // Act
        Third result = mapper.toThird(entity);

        // Assert
        assertNotNull(result);
        assertEquals("Calle 50 #30-20", result.getAddress());
        assertEquals("3157891234", result.getPhoneNumber());
        assertEquals("laura.torres@correo.com", result.getEmail());
    }
}
