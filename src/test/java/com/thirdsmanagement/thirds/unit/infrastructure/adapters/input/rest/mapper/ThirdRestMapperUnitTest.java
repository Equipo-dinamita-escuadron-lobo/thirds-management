package com.thirdsmanagement.thirds.unit.infrastructure.adapters.input.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.thirdsmanagement.thirds.domain.model.PersonClassification;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ChangeThirdStateResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.GetThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ThirdsEnterpriseListResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.ThirdRestMapperImpl;

class ThirdRestMapperUnitTest {

    private ThirdRestMapperImpl mapper;

    private String entId;
    private TypeId typeId;
    private ThirdType thirdType;
    private Country country;
    private State state;
    private City city;

    @BeforeEach
    void setUp() {
        mapper = new ThirdRestMapperImpl();
        entId = "ENT001";

        typeId = TypeId.builder()
                .id(1L)
                .entId(entId)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        thirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .entId(entId)
                .thirdTypeName("Cliente")
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

    // ==================== toThird (ThirdCreateRequest) ====================

    @Test
    @DisplayName("Debe mapear ThirdCreateRequest a Third correctamente")
    void testToThirdFromCreateRequest() {
        // Arrange
        ThirdCreateRequest request = ThirdCreateRequest.builder()
                .entId(entId)
                .typeId(typeId)
                .personType(ePersonType.Natural)
                .thirdTypes(new HashSet<>(Set.of(thirdType)))
                .names("Juan")
                .lastNames("Pérez")
                .gender(eThirdGender.Masculino)
                .idNumber(123456789L)
                .state(true)
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("juan.perez@example.com")
                .build();

        // Act
        Third result = mapper.toThird(request);

        // Assert
        assertNotNull(result);
        assertEquals(entId, result.getEntId());
        assertEquals(typeId, result.getTypeId());
        assertEquals(ePersonType.Natural, result.getPersonType());
        assertEquals("Juan", result.getNames());
        assertEquals("Pérez", result.getLastNames());
        assertEquals(123456789L, result.getIdNumber());
        assertNull(result.getCountry());
        assertNull(result.getProvince());
        assertNull(result.getCity());
    }

    @Test
    @DisplayName("Debe ignorar campos geográficos en ThirdCreateRequest")
    void testToThirdFromCreateRequestIgnoresGeography() {
        // Arrange
        ThirdCreateRequest request = ThirdCreateRequest.builder()
                .entId(entId)
                .typeId(typeId)
                .personType(ePersonType.Natural)
                .thirdTypes(new HashSet<>(Set.of(thirdType)))
                .names("Juan")
                .idNumber(123456789L)
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("juan@example.com")
                .build();

        // Act
        Third result = mapper.toThird(request);

        // Assert
        assertNull(result.getCountry());
        assertNull(result.getProvince());
        assertNull(result.getCity());
    }

    // ==================== toThird (ThirdUpdateRequest) ====================

    @Test
    @DisplayName("Debe mapear ThirdUpdateRequest a Third correctamente")
    void testToThirdFromUpdateRequest() {
        // Arrange
        ThirdUpdateRequest request = ThirdUpdateRequest.builder()
                .thId(1L)
                .entId(entId)
                .typeId(typeId)
                .personType(ePersonType.Juridica)
                .thirdTypes(new HashSet<>(Set.of(thirdType)))
                .socialReason("ACME Corporation")
                .idNumber(900123456L)
                .state(true)
                .address("Carrera 45")
                .phoneNumber("3009876543")
                .email("info@acme.com")
                .build();

        // Act
        Third result = mapper.toThird(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getThId());
        assertEquals(entId, result.getEntId());
        assertEquals(ePersonType.Juridica, result.getPersonType());
        assertEquals("ACME Corporation", result.getSocialReason());
        assertNull(result.getCountry());
        assertNull(result.getProvince());
        assertNull(result.getCity());
    }

    // ==================== getThirdName ====================

    @Test
    @DisplayName("Debe generar nombre completo para persona natural con nombres y apellidos")
    void testGetThirdNameNaturalPersonComplete() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .names("Juan Carlos")
                .lastNames("Pérez García")
                .build();

        // Act
        String result = mapper.getThirdName(third);

        // Assert
        assertEquals("Juan Carlos Pérez García", result);
    }

    @Test
    @DisplayName("Debe generar nombre solo con nombres cuando apellidos es null")
    void testGetThirdNameNaturalPersonOnlyNames() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .names("Juan")
                .lastNames(null)
                .build();

        // Act
        String result = mapper.getThirdName(third);

        // Assert
        assertEquals("Juan", result);
    }

    @Test
    @DisplayName("Debe generar nombre solo con apellidos cuando nombres es null")
    void testGetThirdNameNaturalPersonOnlyLastNames() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .names(null)
                .lastNames("Pérez")
                .build();

        // Act
        String result = mapper.getThirdName(third);

        // Assert
        assertEquals("Pérez", result);
    }

    @Test
    @DisplayName("Debe ignorar campos vacíos en nombre de persona natural")
    void testGetThirdNameNaturalPersonIgnoresEmpty() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .names("   ")
                .lastNames("Pérez")
                .build();

        // Act
        String result = mapper.getThirdName(third);

        // Assert
        assertEquals("Pérez", result);
    }

    @Test
    @DisplayName("Debe retornar null cuando nombres y apellidos son null")
    void testGetThirdNameNaturalPersonAllNull() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .names(null)
                .lastNames(null)
                .build();

        // Act
        String result = mapper.getThirdName(third);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe retornar razón social para persona jurídica")
    void testGetThirdNameLegalEntity() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .socialReason("ACME Corporation S.A.S.")
                .build();

        // Act
        String result = mapper.getThirdName(third);

        // Assert
        assertEquals("ACME Corporation S.A.S.", result);
    }

    @Test
    @DisplayName("Debe retornar null cuando tercero es null")
    void testGetThirdNameWhenThirdIsNull() {
        // Act
        String result = mapper.getThirdName(null);

        // Assert
        assertNull(result);
    }

    // ==================== getThirdDescription ====================

    @Test
    @DisplayName("Debe generar descripción completa con tipo persona y documento")
    void testGetThirdDescriptionComplete() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .idNumber(123456789L)
                .state(true)
                .build();

        // Act
        String result = mapper.getThirdDescription(third);

        // Assert
        assertTrue(result.contains("Persona Natural"));
        assertTrue(result.contains("Cédula de Ciudadanía: 123456789"));
        assertTrue(result.contains("Estado: Activo"));
    }

    @Test
    @DisplayName("Debe incluir estado inactivo en descripción")
    void testGetThirdDescriptionInactive() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .idNumber(900123456L)
                .state(false)
                .build();

        // Act
        String result = mapper.getThirdDescription(third);

        // Assert
        assertTrue(result.contains("Estado: Inactivo"));
    }

    @Test
    @DisplayName("Debe manejar typeId null en descripción")
    void testGetThirdDescriptionWithoutTypeId() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .typeId(null)
                .state(true)
                .build();

        // Act
        String result = mapper.getThirdDescription(third);

        // Assert
        assertTrue(result.contains("Persona Natural"));
        assertFalse(result.contains("Cédula"));
        assertTrue(result.contains("Estado: Activo"));
    }

    @Test
    @DisplayName("Debe retornar null cuando tercero es null en descripción")
    void testGetThirdDescriptionWhenThirdIsNull() {
        // Act
        String result = mapper.getThirdDescription(null);

        // Assert
        assertNull(result);
    }

    // ==================== toThirdCreateResponse ====================

    @Test
    @DisplayName("Debe mapear Third a ThirdResponse correctamente")
    void testToThirdCreateResponse() {
        // Arrange
        Third third = Third.builder()
                .thId(1L)
                .personType(ePersonType.Natural)
                .names("Juan")
                .lastNames("Pérez")
                .typeId(typeId)
                .idNumber(123456789L)
                .state(true)
                .build();

        // Act
        ThirdResponse result = mapper.toThirdCreateResponse(third);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan Pérez", result.getName());
        assertTrue(result.getDescription().contains("Persona Natural"));
    }

    @Test
    @DisplayName("Debe mapear persona jurídica a ThirdResponse")
    void testToThirdCreateResponseLegalEntity() {
        // Arrange
        Third third = Third.builder()
                .thId(2L)
                .personType(ePersonType.Juridica)
                .socialReason("ACME Corp")
                .typeId(typeId)
                .idNumber(900123456L)
                .state(true)
                .build();

        // Act
        ThirdResponse result = mapper.toThirdCreateResponse(third);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("ACME Corp", result.getName());
    }

    // ==================== toChangeThirdStateResponse ====================

    @Test
    @DisplayName("Debe mapear Boolean true a ChangeThirdStateResponse")
    void testToChangeThirdStateResponseTrue() {
        // Act
        ChangeThirdStateResponse result = mapper.toChangeThirdStateResponse(true);

        // Assert
        assertNotNull(result);
        assertTrue(result.getResult());
    }

    @Test
    @DisplayName("Debe mapear Boolean false a ChangeThirdStateResponse")
    void testToChangeThirdStateResponseFalse() {
        // Act
        ChangeThirdStateResponse result = mapper.toChangeThirdStateResponse(false);

        // Assert
        assertNotNull(result);
        assertFalse(result.getResult());
    }

    // ==================== mapCountryToString ====================

    @Test
    @DisplayName("Debe extraer código de país correctamente")
    void testMapCountryToString() {
        // Act
        String result = mapper.mapCountryToString(country);

        // Assert
        assertEquals("CO", result);
    }

    @Test
    @DisplayName("Debe retornar null cuando país es null")
    void testMapCountryToStringWhenNull() {
        // Act
        String result = mapper.mapCountryToString(null);

        // Assert
        assertNull(result);
    }

    // ==================== mapStateToString ====================

    @Test
    @DisplayName("Debe extraer código de departamento correctamente")
    void testMapStateToString() {
        // Act
        String result = mapper.mapStateToString(state);

        // Assert
        assertEquals("11", result);
    }

    @Test
    @DisplayName("Debe retornar null cuando departamento es null")
    void testMapStateToStringWhenNull() {
        // Act
        String result = mapper.mapStateToString(null);

        // Assert
        assertNull(result);
    }

    // ==================== mapCityToString ====================

    @Test
    @DisplayName("Debe extraer código de ciudad correctamente")
    void testMapCityToString() {
        // Act
        String result = mapper.mapCityToString(city);

        // Assert
        assertEquals("11001", result);
    }

    @Test
    @DisplayName("Debe retornar null cuando ciudad es null")
    void testMapCityToStringWhenNull() {
        // Act
        String result = mapper.mapCityToString(null);

        // Assert
        assertNull(result);
    }

    // ==================== toGetThirdResponse ====================

    @Test
    @DisplayName("Debe mapear Third con geografía a GetThirdResponse")
    void testToGetThirdResponseWithGeography() {
        // Arrange
        Third third = Third.builder()
                .thId(1L)
                .personType(ePersonType.Natural)
                .names("Juan")
                .lastNames("Pérez")
                .country(country)
                .province(state)
                .city(city)
                .build();

        // Act
        GetThirdResponse result = mapper.toGetThirdResponse(third);

        // Assert
        assertNotNull(result);
        assertEquals("CO", result.getCountry());
        assertEquals("11", result.getProvince());
        assertEquals("11001", result.getCity());
    }

    @Test
    @DisplayName("Debe mapear Third sin geografía a GetThirdResponse")
    void testToGetThirdResponseWithoutGeography() {
        // Arrange
        Third third = Third.builder()
                .thId(1L)
                .personType(ePersonType.Natural)
                .names("Juan")
                .country(null)
                .province(null)
                .city(null)
                .build();

        // Act
        GetThirdResponse result = mapper.toGetThirdResponse(third);

        // Assert
        assertNotNull(result);
        assertNull(result.getCountry());
        assertNull(result.getProvince());
        assertNull(result.getCity());
    }

    // ==================== toGetThirdResponseList ====================

    @Test
    @DisplayName("Debe mapear lista de Third a lista de GetThirdResponse")
    void testToGetThirdResponseList() {
        // Arrange
        Third third1 = Third.builder()
                .thId(1L)
                .personType(ePersonType.Natural)
                .names("Juan")
                .country(country)
                .build();

        Third third2 = Third.builder()
                .thId(2L)
                .personType(ePersonType.Juridica)
                .socialReason("ACME")
                .province(state)
                .build();

        List<Third> thirds = Arrays.asList(third1, third2);

        // Act
        List<GetThirdResponse> result = mapper.toGetThirdResponseList(thirds);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("CO", result.get(0).getCountry());
        assertEquals("11", result.get(1).getProvince());
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando input es vacío")
    void testToGetThirdResponseListEmpty() {
        // Arrange
        List<Third> emptyList = Arrays.asList();

        // Act
        List<GetThirdResponse> result = mapper.toGetThirdResponseList(emptyList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== toListThirdsResponse ====================

    @Test
    @DisplayName("Debe mapear Page de Third a ThirdsEnterpriseListResponse")
    void testToListThirdsResponse() {
        // Arrange
        Third third = Third.builder()
                .thId(1L)
                .personType(ePersonType.Natural)
                .names("Juan")
                .build();

        Page<Third> page = new PageImpl<>(Arrays.asList(third), PageRequest.of(0, 10), 1);

        // Act
        ThirdsEnterpriseListResponse result = mapper.toListThirdsResponse(page);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getResults());
        assertEquals(1, result.getResults().getContent().size());
    }

    // ==================== Casos edge y combinaciones ====================

    @Test
    @DisplayName("Debe manejar persona natural con todos los campos null")
    void testGetThirdNameNaturalPersonAllFieldsNull() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .names(null)
                .lastNames(null)
                .socialReason(null)
                .build();

        // Act
        String result = mapper.getThirdName(third);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe manejar espacios en blanco en nombres")
    void testGetThirdNameWithWhitespace() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .names("  Juan  ")
                .lastNames("  Pérez  ")
                .build();

        // Act
        String result = mapper.getThirdName(third);

        // Assert
        assertEquals("  Juan     Pérez  ", result);
    }

    @Test
    @DisplayName("Debe generar descripción mínima solo con estado")
    void testGetThirdDescriptionMinimal() {
        // Arrange
        Third third = Third.builder()
                .personType(null)
                .typeId(null)
                .state(true)
                .build();

        // Act
        String result = mapper.getThirdDescription(third);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Estado: Activo"));
        assertFalse(result.contains("Persona"));
    }

    @Test
    @DisplayName("Debe separar correctamente elementos en descripción con guiones")
    void testGetThirdDescriptionSeparators() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .idNumber(123456789L)
                .state(true)
                .build();

        // Act
        String result = mapper.getThirdDescription(third);

        // Assert
        long separatorCount = result.chars().filter(ch -> ch == '-').count();
        assertEquals(2, separatorCount);
    }
}
