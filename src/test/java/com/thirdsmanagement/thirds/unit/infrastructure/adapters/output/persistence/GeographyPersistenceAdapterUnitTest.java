package com.thirdsmanagement.thirds.unit.infrastructure.adapters.output.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.GeographyPersistenceAdapter;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.CityEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.CountryEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.StateEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.GeographyPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.CityRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.CountryRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.StateRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GeographyPersistenceAdapterUnitTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private StateRepository stateRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private GeographyPersistenceMapper geographyMapper;

    @InjectMocks
    private GeographyPersistenceAdapter adapter;

    private CountryEntity countryEntity;
    private Country country;
    private StateEntity stateEntity;
    private State state;
    private CityEntity cityEntity;
    private City city;

    @BeforeEach
    void setUp() {
        countryEntity = CountryEntity.builder()
                .countryCode("CO")
                .countryName("Colombia")
                .build();

        country = Country.builder()
                .countryCode("CO")
                .countryName("Colombia")
                .build();

        stateEntity = StateEntity.builder()
                .stateCode("11")
                .stateName("Bogotá D.C.")
                .countryCode("CO")
                .build();

        state = State.builder()
                .stateCode("11")
                .stateName("Bogotá D.C.")
                .countryCode("CO")
                .build();

        cityEntity = CityEntity.builder()
                .cityCode("11001")
                .cityName("Bogotá")
                .stateCode("11")
                .countryCode("CO")
                .build();

        city = City.builder()
                .cityCode("11001")
                .cityName("Bogotá")
                .stateCode("11")
                .countryCode("CO")
                .build();
    }

    @Test
    @DisplayName("Debe obtener todos los países activos correctamente")
    void testGetAllActiveCountries() {
        // Arrange
        List<CountryEntity> countryEntities = Arrays.asList(countryEntity);
        List<Country> expectedCountries = Arrays.asList(country);

        when(countryRepository.findAllCountries()).thenReturn(countryEntities);
        when(geographyMapper.toCountryList(countryEntities)).thenReturn(expectedCountries);

        // Act
        List<Country> result = adapter.getAllActiveCountries();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CO", result.get(0).getCountryCode());
        assertEquals("Colombia", result.get(0).getCountryName());
        verify(countryRepository).findAllCountries();
        verify(geographyMapper).toCountryList(countryEntities);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay países activos")
    void testGetAllActiveCountriesReturnsEmptyList() {
        // Arrange
        when(countryRepository.findAllCountries()).thenReturn(Collections.emptyList());
        when(geographyMapper.toCountryList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<Country> result = adapter.getAllActiveCountries();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(countryRepository).findAllCountries();
    }

    @Test
    @DisplayName("Debe obtener estados por código de país correctamente")
    void testGetStatesByCountry() {
        // Arrange
        String countryCode = "CO";
        List<StateEntity> stateEntities = Arrays.asList(stateEntity);
        List<State> expectedStates = Arrays.asList(state);

        when(stateRepository.findByCountryCodeOrderByStateName(countryCode)).thenReturn(stateEntities);
        when(geographyMapper.toStateList(stateEntities)).thenReturn(expectedStates);

        // Act
        List<State> result = adapter.getStatesByCountry(countryCode);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("11", result.get(0).getStateCode());
        assertEquals("Bogotá D.C.", result.get(0).getStateName());
        verify(stateRepository).findByCountryCodeOrderByStateName(countryCode);
        verify(geographyMapper).toStateList(stateEntities);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay estados para el país")
    void testGetStatesByCountryReturnsEmptyList() {
        // Arrange
        String countryCode = "XX";
        when(stateRepository.findByCountryCodeOrderByStateName(countryCode)).thenReturn(Collections.emptyList());
        when(geographyMapper.toStateList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<State> result = adapter.getStatesByCountry(countryCode);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe obtener ciudades por estado y país correctamente")
    void testGetCitiesByState() {
        // Arrange
        String stateCode = "11";
        String countryCode = "CO";
        List<CityEntity> cityEntities = Arrays.asList(cityEntity);
        List<City> expectedCities = Arrays.asList(city);

        when(cityRepository.findByStateCodeAndCountryCodeOrderByCityName(stateCode, countryCode))
                .thenReturn(cityEntities);
        when(geographyMapper.toCityList(cityEntities)).thenReturn(expectedCities);

        // Act
        List<City> result = adapter.getCitiesByState(stateCode, countryCode);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("11001", result.get(0).getCityCode());
        assertEquals("Bogotá", result.get(0).getCityName());
        verify(cityRepository).findByStateCodeAndCountryCodeOrderByCityName(stateCode, countryCode);
        verify(geographyMapper).toCityList(cityEntities);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay ciudades para el estado")
    void testGetCitiesByStateReturnsEmptyList() {
        // Arrange
        String stateCode = "99";
        String countryCode = "XX";
        
        when(cityRepository.findByStateCodeAndCountryCodeOrderByCityName(stateCode, countryCode))
                .thenReturn(Collections.emptyList());
        when(geographyMapper.toCityList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<City> result = adapter.getCitiesByState(stateCode, countryCode);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe verificar existencia de país activo correctamente")
    void testExistsActiveCountryReturnsTrue() {
        // Arrange
        String countryCode = "CO";
        when(countryRepository.existsByCountryCode(countryCode)).thenReturn(true);

        // Act
        boolean result = adapter.existsActiveCountry(countryCode);

        // Assert
        assertTrue(result);
        verify(countryRepository).existsByCountryCode(countryCode);
    }

    @Test
    @DisplayName("Debe retornar false cuando el país no existe")
    void testExistsActiveCountryReturnsFalse() {
        // Arrange
        String countryCode = "XX";
        when(countryRepository.existsByCountryCode(countryCode)).thenReturn(false);

        // Act
        boolean result = adapter.existsActiveCountry(countryCode);

        // Assert
        assertFalse(result);
        verify(countryRepository).existsByCountryCode(countryCode);
    }

    @Test
    @DisplayName("Debe verificar existencia de estado activo correctamente")
    void testExistsActiveStateReturnsTrue() {
        // Arrange
        String stateCode = "11";
        String countryCode = "CO";
        when(stateRepository.existsByStateCodeAndCountryCode(stateCode, countryCode)).thenReturn(true);

        // Act
        boolean result = adapter.existsActiveState(stateCode, countryCode);

        // Assert
        assertTrue(result);
        verify(stateRepository).existsByStateCodeAndCountryCode(stateCode, countryCode);
    }

    @Test
    @DisplayName("Debe retornar false cuando el estado no existe")
    void testExistsActiveStateReturnsFalse() {
        // Arrange
        String stateCode = "99";
        String countryCode = "XX";
        when(stateRepository.existsByStateCodeAndCountryCode(stateCode, countryCode)).thenReturn(false);

        // Act
        boolean result = adapter.existsActiveState(stateCode, countryCode);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe verificar existencia de ciudad activa correctamente")
    void testExistsActiveCityReturnsTrue() {
        // Arrange
        String cityCode = "11001";
        String stateCode = "11";
        String countryCode = "CO";
        when(cityRepository.existsByCityCodeAndStateCodeAndCountryCode(cityCode, stateCode, countryCode))
                .thenReturn(true);

        // Act
        boolean result = adapter.existsActiveCity(cityCode, stateCode, countryCode);

        // Assert
        assertTrue(result);
        verify(cityRepository).existsByCityCodeAndStateCodeAndCountryCode(cityCode, stateCode, countryCode);
    }

    @Test
    @DisplayName("Debe retornar false cuando la ciudad no existe")
    void testExistsActiveCityReturnsFalse() {
        // Arrange
        String cityCode = "99999";
        String stateCode = "99";
        String countryCode = "XX";
        when(cityRepository.existsByCityCodeAndStateCodeAndCountryCode(cityCode, stateCode, countryCode))
                .thenReturn(false);

        // Act
        boolean result = adapter.existsActiveCity(cityCode, stateCode, countryCode);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe obtener todos los estados activos correctamente")
    void testGetAllActiveStates() {
        // Arrange
        List<StateEntity> stateEntities = Arrays.asList(stateEntity);
        List<State> expectedStates = Arrays.asList(state);

        when(stateRepository.findAllStates()).thenReturn(stateEntities);
        when(geographyMapper.toStateList(stateEntities)).thenReturn(expectedStates);

        // Act
        List<State> result = adapter.getAllActiveStates();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("11", result.get(0).getStateCode());
        verify(stateRepository).findAllStates();
        verify(geographyMapper).toStateList(stateEntities);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay estados activos")
    void testGetAllActiveStatesReturnsEmptyList() {
        // Arrange
        when(stateRepository.findAllStates()).thenReturn(Collections.emptyList());
        when(geographyMapper.toStateList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<State> result = adapter.getAllActiveStates();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe obtener todas las ciudades activas correctamente")
    void testGetAllActiveCities() {
        // Arrange
        List<CityEntity> cityEntities = Arrays.asList(cityEntity);
        List<City> expectedCities = Arrays.asList(city);

        when(cityRepository.findAllCities()).thenReturn(cityEntities);
        when(geographyMapper.toCityList(cityEntities)).thenReturn(expectedCities);

        // Act
        List<City> result = adapter.getAllActiveCities();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("11001", result.get(0).getCityCode());
        verify(cityRepository).findAllCities();
        verify(geographyMapper).toCityList(cityEntities);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay ciudades activas")
    void testGetAllActiveCitiesReturnsEmptyList() {
        // Arrange
        when(cityRepository.findAllCities()).thenReturn(Collections.emptyList());
        when(geographyMapper.toCityList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<City> result = adapter.getAllActiveCities();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe manejar múltiples países correctamente")
    void testGetAllActiveCountriesWithMultipleCountries() {
        // Arrange
        CountryEntity country2 = CountryEntity.builder()
                .countryCode("US")
                .countryName("United States")
                .build();

        Country domainCountry2 = Country.builder()
                .countryCode("US")
                .countryName("United States")
                .build();

        List<CountryEntity> countryEntities = Arrays.asList(countryEntity, country2);
        List<Country> expectedCountries = Arrays.asList(country, domainCountry2);

        when(countryRepository.findAllCountries()).thenReturn(countryEntities);
        when(geographyMapper.toCountryList(countryEntities)).thenReturn(expectedCountries);

        // Act
        List<Country> result = adapter.getAllActiveCountries();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("CO", result.get(0).getCountryCode());
        assertEquals("US", result.get(1).getCountryCode());
    }

    @Test
    @DisplayName("Debe manejar múltiples estados para un país")
    void testGetStatesByCountryWithMultipleStates() {
        // Arrange
        StateEntity state2 = StateEntity.builder()
                .stateCode("05")
                .stateName("Antioquia")
                .countryCode("CO")
                .build();

        State domainState2 = State.builder()
                .stateCode("05")
                .stateName("Antioquia")
                .countryCode("CO")
                .build();

        List<StateEntity> stateEntities = Arrays.asList(stateEntity, state2);
        List<State> expectedStates = Arrays.asList(state, domainState2);

        when(stateRepository.findByCountryCodeOrderByStateName("CO")).thenReturn(stateEntities);
        when(geographyMapper.toStateList(stateEntities)).thenReturn(expectedStates);

        // Act
        List<State> result = adapter.getStatesByCountry("CO");

        // Assert
        assertEquals(2, result.size());
        assertEquals("11", result.get(0).getStateCode());
        assertEquals("05", result.get(1).getStateCode());
    }

    @Test
    @DisplayName("Debe manejar múltiples ciudades para un estado")
    void testGetCitiesByStateWithMultipleCities() {
        // Arrange
        CityEntity city2 = CityEntity.builder()
                .cityCode("11002")
                .cityName("Soacha")
                .stateCode("11")
                .countryCode("CO")
                .build();

        City domainCity2 = City.builder()
                .cityCode("11002")
                .cityName("Soacha")
                .stateCode("11")
                .countryCode("CO")
                .build();

        List<CityEntity> cityEntities = Arrays.asList(cityEntity, city2);
        List<City> expectedCities = Arrays.asList(city, domainCity2);

        when(cityRepository.findByStateCodeAndCountryCodeOrderByCityName("11", "CO"))
                .thenReturn(cityEntities);
        when(geographyMapper.toCityList(cityEntities)).thenReturn(expectedCities);

        // Act
        List<City> result = adapter.getCitiesByState("11", "CO");

        // Assert
        assertEquals(2, result.size());
        assertEquals("11001", result.get(0).getCityCode());
        assertEquals("11002", result.get(1).getCityCode());
    }
}
