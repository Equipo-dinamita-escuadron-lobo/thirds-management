package com.thirdsmanagement.thirds.unit.application.service.importExport;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.thirdsmanagement.thirds.application.service.importExport.BatchValidationService;
import com.thirdsmanagement.thirds.application.service.importExport.DataConverter;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.domain.model.*;

/**
 * Tests unitarios para DataConverter
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataConverterUnitTest {

    @InjectMocks
    private DataConverter dataConverter;

    private BatchValidationService.ReferenceDataCache cache;
    private ThirdExcelData excelData;
    private TypeId typeId;
    private ThirdType thirdType1;
    private ThirdType thirdType2;
    private Country country;
    private State state;
    private City city;

    @BeforeEach
    void setUp() {
        // Arrange - TypeId
        typeId = TypeId.builder()
                .id(1L)
                .entId("ENT001")
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .status(true)
                .classification(PersonClassification.NATURAL_PERSON)
                .build();

        // Arrange - ThirdTypes
        thirdType1 = ThirdType.builder()
                .thirdTypeId(1L)
                .entId("ENT001")
                .thirdTypeName("Cliente")
                .status(true)
                .build();

        thirdType2 = ThirdType.builder()
                .thirdTypeId(2L)
                .entId("ENT001")
                .thirdTypeName("Proveedor")
                .status(true)
                .build();

        // Arrange - Geography
        country = Country.builder()
                .countryCode("COL")
                .countryName("Colombia")
                .build();

        state = State.builder()
                .stateCode("19")
                .stateName("Cauca")
                .countryCode("COL")
                .build();

        city = City.builder()
                .cityCode("19001")
                .cityName("Popayán")
                .stateCode("19")
                .build();

        // Arrange - Cache usando reflexión para acceder al constructor
        Map<String, TypeId> typeIds = new HashMap<>();
        typeIds.put("CC", typeId);

        Map<String, ThirdType> thirdTypes = new HashMap<>();
        thirdTypes.put("CLIENTE", thirdType1);
        thirdTypes.put("PROVEEDOR", thirdType2);

        Map<String, Country> countries = new HashMap<>();
        countries.put("COLOMBIA", country);

        Map<String, State> states = new HashMap<>();
        states.put("CAUCA", state);

        Map<String, City> cities = new HashMap<>();
        cities.put("POPAYAN_19", city);

        try {
            java.lang.reflect.Constructor<BatchValidationService.ReferenceDataCache> constructor = 
                BatchValidationService.ReferenceDataCache.class.getDeclaredConstructor(
                    Map.class, Map.class, Map.class, Map.class, Map.class);
            constructor.setAccessible(true);
            cache = constructor.newInstance(typeIds, thirdTypes, countries, states, cities);
        } catch (Exception e) {
            throw new RuntimeException("Error creando ReferenceDataCache", e);
        }

        // Arrange - Excel Data básico
        excelData = ThirdExcelData.builder()
                .entId("ENT001")
                .typeIdName("CC")
                .idNumber(123456789L)
                .verificationNumber(5L)
                .personType(ePersonType.Natural)
                .names("Juan Carlos")
                .lastNames("Pérez Gómez")
                .gender(eThirdGender.Masculino)
                .email("juan@email.com")
                .phoneNumber("3001234567")
                .address("Calle 5 # 4-70")
                .state(true)
                .thirdTypesNames(new HashSet<>(Arrays.asList("Cliente", "Proveedor")))
                .countryName("Colombia")
                .stateName("Cauca")
                .cityName("Popayán")
                .build();
    }

    // ========== convertWithCache Tests ==========

    @Test
    @DisplayName("Debe convertir ThirdExcelData a Third exitosamente con todos los campos")
    void testConvertWithCache_WithCompleteData() {
        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals("ENT001", result.getEntId());
        assertEquals(typeId, result.getTypeId());
        assertEquals(2, result.getThirdTypes().size());
        assertEquals(ePersonType.Natural, result.getPersonType());
        assertEquals("Juan Carlos", result.getNames());
        assertEquals("Perez Gomez", result.getLastNames());
        assertNull(result.getSocialReason());
        assertEquals(eThirdGender.Masculino, result.getGender());
        assertEquals(123456789L, result.getIdNumber());
        assertEquals(5L, result.getVerificationNumber());
        assertTrue(result.getState());
        assertEquals("Calle 5 # 4-70", result.getAddress());
        assertEquals("3001234567", result.getPhoneNumber());
        assertEquals("juan@email.com", result.getEmail());
        assertEquals(country, result.getCountry());
        assertEquals(state, result.getProvince());
        assertEquals(city, result.getCity());
    }

    @Test
    @DisplayName("Debe devolver null cuando ThirdExcelData no tiene campos requeridos")
    void testConvertWithCache_WithMissingRequiredFields() {
        // Arrange
        excelData.setIdNumber(null);

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe devolver null cuando TypeId no se encuentra en cache")
    void testConvertWithCache_WithInvalidTypeId() {
        // Arrange
        excelData.setTypeIdName("INVALID_TYPE");

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe devolver null cuando TypeId es null en ExcelData")
    void testConvertWithCache_WithNullTypeId() {
        // Arrange
        excelData.setTypeIdName(null);

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe convertir con ThirdTypes vacíos cuando no hay tipos")
    void testConvertWithCache_WithEmptyThirdTypes() {
        // Arrange
        excelData.setThirdTypesNames(new HashSet<>());

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertTrue(result.getThirdTypes().isEmpty());
    }

    @Test
    @DisplayName("Debe convertir con ThirdTypes vacíos cuando thirdTypesNames es null")
    void testConvertWithCache_WithNullThirdTypes() {
        // Arrange
        excelData.setThirdTypesNames(null);

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertTrue(result.getThirdTypes().isEmpty());
    }

    @Test
    @DisplayName("Debe filtrar ThirdTypes que no existen en cache")
    void testConvertWithCache_WithInvalidThirdTypes() {
        // Arrange
        excelData.setThirdTypesNames(new HashSet<>(Arrays.asList("Cliente", "TipoInvalido")));

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getThirdTypes().size());
        assertTrue(result.getThirdTypes().contains(thirdType1));
    }

    @Test
    @DisplayName("Debe convertir sin país cuando countryName es null")
    void testConvertWithCache_WithNullCountry() {
        // Arrange
        excelData.setCountryName(null);

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertNull(result.getCountry());
    }

    @Test
    @DisplayName("Debe convertir sin país cuando countryName está vacío")
    void testConvertWithCache_WithEmptyCountry() {
        // Arrange
        excelData.setCountryName("   ");

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertNull(result.getCountry());
    }

    @Test
    @DisplayName("Debe convertir sin estado cuando stateName es null")
    void testConvertWithCache_WithNullState() {
        // Arrange
        excelData.setStateName(null);

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertNull(result.getProvince());
    }

    @Test
    @DisplayName("Debe convertir sin estado cuando stateName está vacío")
    void testConvertWithCache_WithEmptyState() {
        // Arrange
        excelData.setStateName("  ");

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertNull(result.getProvince());
    }

    @Test
    @DisplayName("Debe convertir sin ciudad cuando cityName es null")
    void testConvertWithCache_WithNullCity() {
        // Arrange
        excelData.setCityName(null);

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertNull(result.getCity());
    }

    @Test
    @DisplayName("Debe convertir sin ciudad cuando cityName está vacío")
    void testConvertWithCache_WithEmptyCity() {
        // Arrange
        excelData.setCityName("   ");

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertNull(result.getCity());
    }

    @Test
    @DisplayName("Debe convertir sin ciudad cuando state es null")
    void testConvertWithCache_WithoutState() {
        // Arrange
        excelData.setStateName("EstadoInvalido");

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertNull(result.getProvince());
        assertNull(result.getCity());
    }

    @Test
    @DisplayName("Debe usar estado true cuando state es null en ExcelData")
    void testConvertWithCache_WithNullStateField() {
        // Arrange
        excelData.setState(null);

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertTrue(result.getState());
    }

    @Test
    @DisplayName("Debe usar estado false cuando está explícitamente false en ExcelData")
    void testConvertWithCache_WithFalseState() {
        // Arrange
        excelData.setState(false);

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertFalse(result.getState());
    }

    @Test
    @DisplayName("Debe normalizar nombres preservando case")
    void testConvertWithCache_NormalizesNames() {
        // Arrange
        excelData.setNames("  José María  ");
        excelData.setLastNames("  Pérez Gómez  ");

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals("Jose Maria", result.getNames());
        assertEquals("Perez Gomez", result.getLastNames());
    }

    @Test
    @DisplayName("Debe normalizar razón social preservando case")
    void testConvertWithCache_NormalizesSocialReason() {
        // Arrange
        excelData.setSocialReason("  Empresa S.A.S.  ");

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals("Empresa S.A.S.", result.getSocialReason());
    }

    @Test
    @DisplayName("Debe manejar nombres null correctamente")
    void testConvertWithCache_WithNullNames() {
        // Arrange
        excelData.setNames(null);

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertNull(result.getNames());
    }

    @Test
    @DisplayName("Debe manejar apellidos null correctamente")
    void testConvertWithCache_WithNullLastNames() {
        // Arrange
        excelData.setLastNames(null);

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertNull(result.getLastNames());
    }

    @Test
    @DisplayName("Debe manejar razón social null correctamente")
    void testConvertWithCache_WithNullSocialReason() {
        // Arrange
        excelData.setSocialReason(null);

        // Act
        Third result = dataConverter.convertWithCache(excelData, cache);

        // Assert
        assertNotNull(result);
        assertNull(result.getSocialReason());
    }

    // ========== getGeographyData Tests ==========

    @Test
    @DisplayName("Debe obtener GeographyData completo con todos los códigos")
    void testGetGeographyData_WithCompleteGeography() {
        // Act
        DataConverter.GeographyData result = dataConverter.getGeographyData(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals("COL", result.getCountryCode());
        assertEquals("19", result.getStateCode());
        assertEquals("19001", result.getCityCode());
    }

    @Test
    @DisplayName("Debe devolver GeographyData con códigos null cuando no hay país")
    void testGetGeographyData_WithoutCountry() {
        // Arrange
        excelData.setCountryName(null);

        // Act
        DataConverter.GeographyData result = dataConverter.getGeographyData(excelData, cache);

        // Assert
        assertNotNull(result);
        assertNull(result.getCountryCode());
        assertNull(result.getStateCode());
        assertNull(result.getCityCode());
    }

    @Test
    @DisplayName("Debe devolver GeographyData con stateCode null cuando país no está en cache")
    void testGetGeographyData_WithInvalidCountry() {
        // Arrange
        excelData.setCountryName("Venezuela");

        // Act
        DataConverter.GeographyData result = dataConverter.getGeographyData(excelData, cache);

        // Assert
        assertNotNull(result);
        assertNull(result.getCountryCode());
        assertNull(result.getStateCode());
        assertNull(result.getCityCode());
    }

    @Test
    @DisplayName("Debe devolver GeographyData sin estado cuando stateName es null")
    void testGetGeographyData_WithoutState() {
        // Arrange
        excelData.setStateName(null);

        // Act
        DataConverter.GeographyData result = dataConverter.getGeographyData(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals("COL", result.getCountryCode());
        assertNull(result.getStateCode());
        assertNull(result.getCityCode());
    }

    @Test
    @DisplayName("Debe devolver GeographyData sin estado cuando state no está en cache")
    void testGetGeographyData_WithInvalidState() {
        // Arrange
        excelData.setStateName("Valle");

        // Act
        DataConverter.GeographyData result = dataConverter.getGeographyData(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals("COL", result.getCountryCode());
        assertNull(result.getStateCode());
        assertNull(result.getCityCode());
    }

    @Test
    @DisplayName("Debe devolver GeographyData sin ciudad cuando cityName es null")
    void testGetGeographyData_WithoutCity() {
        // Arrange
        excelData.setCityName(null);

        // Act
        DataConverter.GeographyData result = dataConverter.getGeographyData(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals("COL", result.getCountryCode());
        assertEquals("19", result.getStateCode());
        assertNull(result.getCityCode());
    }

    @Test
    @DisplayName("Debe devolver GeographyData sin ciudad cuando city no está en cache")
    void testGetGeographyData_WithInvalidCity() {
        // Arrange
        excelData.setCityName("Cali");

        // Act
        DataConverter.GeographyData result = dataConverter.getGeographyData(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals("COL", result.getCountryCode());
        assertEquals("19", result.getStateCode());
        assertNull(result.getCityCode());
    }

    @Test
    @DisplayName("Debe manejar country name con espacios en blanco")
    void testGetGeographyData_WithCountryNameWithSpaces() {
        // Arrange
        excelData.setCountryName("  Colombia  ");

        // Act
        DataConverter.GeographyData result = dataConverter.getGeographyData(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals("COL", result.getCountryCode());
    }

    @Test
    @DisplayName("Debe manejar state name con espacios en blanco")
    void testGetGeographyData_WithStateNameWithSpaces() {
        // Arrange
        excelData.setStateName("  Cauca  ");

        // Act
        DataConverter.GeographyData result = dataConverter.getGeographyData(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals("19", result.getStateCode());
    }

    @Test
    @DisplayName("Debe manejar city name con espacios en blanco")
    void testGetGeographyData_WithCityNameWithSpaces() {
        // Arrange
        excelData.setCityName("  Popayán  ");

        // Act
        DataConverter.GeographyData result = dataConverter.getGeographyData(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals("19001", result.getCityCode());
    }

    @Test
    @DisplayName("Debe devolver null en cityCode cuando city no existe para el stateCode dado")
    void testGetGeographyData_WithCityNotFoundForState() {
        // Arrange
        excelData.setCityName("CiudadInexistente");

        // Act
        DataConverter.GeographyData result = dataConverter.getGeographyData(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals("COL", result.getCountryCode());
        assertEquals("19", result.getStateCode());
        assertNull(result.getCityCode());
    }

    @Test
    @DisplayName("Debe manejar countryName vacío")
    void testGetGeographyData_WithEmptyCountryName() {
        // Arrange
        excelData.setCountryName("   ");

        // Act
        DataConverter.GeographyData result = dataConverter.getGeographyData(excelData, cache);

        // Assert
        assertNotNull(result);
        assertNull(result.getCountryCode());
        assertNull(result.getStateCode());
        assertNull(result.getCityCode());
    }

    @Test
    @DisplayName("Debe manejar stateName vacío")
    void testGetGeographyData_WithEmptyStateName() {
        // Arrange
        excelData.setStateName("   ");

        // Act
        DataConverter.GeographyData result = dataConverter.getGeographyData(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals("COL", result.getCountryCode());
        assertNull(result.getStateCode());
        assertNull(result.getCityCode());
    }

    @Test
    @DisplayName("Debe manejar cityName vacío")
    void testGetGeographyData_WithEmptyCityName() {
        // Arrange
        excelData.setCityName("   ");

        // Act
        DataConverter.GeographyData result = dataConverter.getGeographyData(excelData, cache);

        // Assert
        assertNotNull(result);
        assertEquals("COL", result.getCountryCode());
        assertEquals("19", result.getStateCode());
        assertNull(result.getCityCode());
    }
}
