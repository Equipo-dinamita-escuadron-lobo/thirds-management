package com.thirdsmanagement.thirds.unit.infrastructure.adapters.input.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.thirdsmanagement.thirds.application.ports.input.ListGeographyUseCase;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.controller.GeographyRestController;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.CityResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.CountryResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.StateResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.GeographyRestMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GeographyRestControllerUnitTest {

    @Mock
    private ListGeographyUseCase listGeographyUseCase;

    @Mock
    private GeographyRestMapper geographyRestMapper;

    @InjectMocks
    private GeographyRestController geographyRestController;

    private Country country;
    private State state;
    private City city;
    private CountryResponse countryResponse;
    private StateResponse stateResponse;
    private CityResponse cityResponse;

    @BeforeEach
    void setUp() {
        country = Country.builder()
                .countryCode("CO")
                .countryName("Colombia")
                .build();

        state = State.builder()
                .stateCode("05")
                .stateName("Antioquia")
                .countryCode("CO")
                .country(country)
                .build();

        city = City.builder()
                .cityCode("05001")
                .cityName("Medellín")
                .stateCode("05")
                .countryCode("CO")
                .state(state)
                .build();

        countryResponse = CountryResponse.builder()
                .countryCode("CO")
                .countryName("Colombia")
                .build();

        stateResponse = StateResponse.builder()
                .stateCode("05")
                .stateName("Antioquia")
                .countryCode("CO")
                .build();

        cityResponse = CityResponse.builder()
                .cityCode("05001")
                .cityName("Medellín")
                .stateCode("05")
                .countryCode("CO")
                .build();
    }

    // ==================== getAllCountries ====================

    @Test
    @DisplayName("Debe retornar lista de países cuando hay países disponibles")
    void testGetAllCountriesWithAvailableCountriesReturnsList() {
        // Arrange
        Country country2 = Country.builder()
                .countryCode("US")
                .countryName("Estados Unidos")
                .build();

        CountryResponse countryResponse2 = CountryResponse.builder()
                .countryCode("US")
                .countryName("Estados Unidos")
                .build();

        List<Country> countries = Arrays.asList(country, country2);
        List<CountryResponse> countryResponses = Arrays.asList(countryResponse, countryResponse2);

        when(listGeographyUseCase.getAllCountries()).thenReturn(countries);
        when(geographyRestMapper.toCountryResponseList(countries)).thenReturn(countryResponses);

        // Act
        ResponseEntity<List<CountryResponse>> response = geographyRestController.getAllCountries();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("CO", response.getBody().get(0).getCountryCode());
        assertEquals("Colombia", response.getBody().get(0).getCountryName());
        verify(listGeographyUseCase).getAllCountries();
        verify(geographyRestMapper).toCountryResponseList(countries);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay países")
    void testGetAllCountriesWithoutCountriesReturnsEmptyList() {
        // Arrange
        List<Country> countries = Arrays.asList();
        List<CountryResponse> countryResponses = Arrays.asList();

        when(listGeographyUseCase.getAllCountries()).thenReturn(countries);
        when(geographyRestMapper.toCountryResponseList(countries)).thenReturn(countryResponses);

        // Act
        ResponseEntity<List<CountryResponse>> response = geographyRestController.getAllCountries();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(listGeographyUseCase).getAllCountries();
        verify(geographyRestMapper).toCountryResponseList(countries);
    }

    @Test
    @DisplayName("Debe delegar correctamente al caso de uso y mapper")
    void testGetAllCountriesDelegatesCorrectly() {
        // Arrange
        List<Country> countries = Arrays.asList(country);
        List<CountryResponse> countryResponses = Arrays.asList(countryResponse);

        when(listGeographyUseCase.getAllCountries()).thenReturn(countries);
        when(geographyRestMapper.toCountryResponseList(countries)).thenReturn(countryResponses);

        // Act
        geographyRestController.getAllCountries();

        // Assert
        verify(listGeographyUseCase, times(1)).getAllCountries();
        verify(geographyRestMapper, times(1)).toCountryResponseList(countries);
        verifyNoMoreInteractions(listGeographyUseCase, geographyRestMapper);
    }

    @Test
    @DisplayName("Debe propagar excepción cuando caso de uso falla")
    void testGetAllCountriesPropagatesException() {
        // Arrange
        when(listGeographyUseCase.getAllCountries())
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> geographyRestController.getAllCountries());
        verify(listGeographyUseCase).getAllCountries();
        verify(geographyRestMapper, never()).toCountryResponseList(any());
    }

    // ==================== getStatesByCountry ====================

    @Test
    @DisplayName("Debe retornar lista de estados cuando código de país es válido")
    void testGetStatesByCountryValidCountryCodeReturnsStatesList() {
        // Arrange
        State state2 = State.builder()
                .stateCode("11")
                .stateName("Bogotá D.C.")
                .countryCode("CO")
                .country(country)
                .build();

        StateResponse stateResponse2 = StateResponse.builder()
                .stateCode("11")
                .stateName("Bogotá D.C.")
                .countryCode("CO")
                .build();

        List<State> states = Arrays.asList(state, state2);
        List<StateResponse> stateResponses = Arrays.asList(stateResponse, stateResponse2);

        when(listGeographyUseCase.getStatesByCountry("CO")).thenReturn(states);
        when(geographyRestMapper.toStateResponseList(states)).thenReturn(stateResponses);

        // Act
        ResponseEntity<List<StateResponse>> response = geographyRestController.getStatesByCountry("CO");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("05", response.getBody().get(0).getStateCode());
        assertEquals("Antioquia", response.getBody().get(0).getStateName());
        verify(listGeographyUseCase).getStatesByCountry("CO");
        verify(geographyRestMapper).toStateResponseList(states);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando país no tiene estados")
    void testGetStatesByCountryCountryWithoutStatesReturnsEmptyList() {
        // Arrange
        List<State> states = Arrays.asList();
        List<StateResponse> stateResponses = Arrays.asList();

        when(listGeographyUseCase.getStatesByCountry("US")).thenReturn(states);
        when(geographyRestMapper.toStateResponseList(states)).thenReturn(stateResponses);

        // Act
        ResponseEntity<List<StateResponse>> response = geographyRestController.getStatesByCountry("US");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(listGeographyUseCase).getStatesByCountry("US");
        verify(geographyRestMapper).toStateResponseList(states);
    }

    @Test
    @DisplayName("Debe delegar correctamente parámetro de código de país")
    void testGetStatesByCountryDelegatesParameterCorrectly() {
        // Arrange
        String countryCode = "CO";
        List<State> states = Arrays.asList(state);
        List<StateResponse> stateResponses = Arrays.asList(stateResponse);

        when(listGeographyUseCase.getStatesByCountry(countryCode)).thenReturn(states);
        when(geographyRestMapper.toStateResponseList(states)).thenReturn(stateResponses);

        // Act
        geographyRestController.getStatesByCountry(countryCode);

        // Assert
        verify(listGeographyUseCase, times(1)).getStatesByCountry(countryCode);
        verify(geographyRestMapper, times(1)).toStateResponseList(states);
        verifyNoMoreInteractions(listGeographyUseCase, geographyRestMapper);
    }

    @Test
    @DisplayName("Debe propagar excepción cuando país no existe")
    void testGetStatesByCountryNonExistentCountryPropagatesException() {
        // Arrange
        when(listGeographyUseCase.getStatesByCountry("XX"))
                .thenThrow(new IllegalArgumentException("País no encontrado"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> geographyRestController.getStatesByCountry("XX"));
        verify(listGeographyUseCase).getStatesByCountry("XX");
        verify(geographyRestMapper, never()).toStateResponseList(any());
    }

    @Test
    @DisplayName("Debe procesar correctamente diferentes códigos de país")
    void testGetStatesByCountryDifferentCountryCodes() {
        // Arrange
        List<State> states = Arrays.asList(state);
        List<StateResponse> stateResponses = Arrays.asList(stateResponse);

        when(listGeographyUseCase.getStatesByCountry(anyString())).thenReturn(states);
        when(geographyRestMapper.toStateResponseList(states)).thenReturn(stateResponses);

        // Act
        ResponseEntity<List<StateResponse>> response1 = geographyRestController.getStatesByCountry("CO");
        ResponseEntity<List<StateResponse>> response2 = geographyRestController.getStatesByCountry("US");

        // Assert
        assertNotNull(response1);
        assertNotNull(response2);
        verify(listGeographyUseCase).getStatesByCountry("CO");
        verify(listGeographyUseCase).getStatesByCountry("US");
    }

    // ==================== getCitiesByState ====================

    @Test
    @DisplayName("Debe retornar lista de ciudades cuando códigos son válidos")
    void testGetCitiesByStateValidCodesReturnsCitiesList() {
        // Arrange
        City city2 = City.builder()
                .cityCode("05002")
                .cityName("Envigado")
                .stateCode("05")
                .countryCode("CO")
                .state(state)
                .build();

        CityResponse cityResponse2 = CityResponse.builder()
                .cityCode("05002")
                .cityName("Envigado")
                .stateCode("05")
                .countryCode("CO")
                .build();

        List<City> cities = Arrays.asList(city, city2);
        List<CityResponse> cityResponses = Arrays.asList(cityResponse, cityResponse2);

        when(listGeographyUseCase.getCitiesByState("05", "CO")).thenReturn(cities);
        when(geographyRestMapper.toCityResponseList(cities)).thenReturn(cityResponses);

        // Act
        ResponseEntity<List<CityResponse>> response = geographyRestController.getCitiesByState("05", "CO");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("05001", response.getBody().get(0).getCityCode());
        assertEquals("Medellín", response.getBody().get(0).getCityName());
        verify(listGeographyUseCase).getCitiesByState("05", "CO");
        verify(geographyRestMapper).toCityResponseList(cities);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando estado no tiene ciudades")
    void testGetCitiesByStateStateWithoutCitiesReturnsEmptyList() {
        // Arrange
        List<City> cities = Arrays.asList();
        List<CityResponse> cityResponses = Arrays.asList();

        when(listGeographyUseCase.getCitiesByState("99", "CO")).thenReturn(cities);
        when(geographyRestMapper.toCityResponseList(cities)).thenReturn(cityResponses);

        // Act
        ResponseEntity<List<CityResponse>> response = geographyRestController.getCitiesByState("99", "CO");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(listGeographyUseCase).getCitiesByState("99", "CO");
        verify(geographyRestMapper).toCityResponseList(cities);
    }

    @Test
    @DisplayName("Debe delegar correctamente ambos parámetros")
    void testGetCitiesByStateDelegatesBothParametersCorrectly() {
        // Arrange
        String stateCode = "05";
        String countryCode = "CO";
        List<City> cities = Arrays.asList(city);
        List<CityResponse> cityResponses = Arrays.asList(cityResponse);

        when(listGeographyUseCase.getCitiesByState(stateCode, countryCode)).thenReturn(cities);
        when(geographyRestMapper.toCityResponseList(cities)).thenReturn(cityResponses);

        // Act
        geographyRestController.getCitiesByState(stateCode, countryCode);

        // Assert
        verify(listGeographyUseCase, times(1)).getCitiesByState(stateCode, countryCode);
        verify(geographyRestMapper, times(1)).toCityResponseList(cities);
        verifyNoMoreInteractions(listGeographyUseCase, geographyRestMapper);
    }

    @Test
    @DisplayName("Debe propagar excepción cuando estado no existe")
    void testGetCitiesByStateNonExistentStatePropagatesException() {
        // Arrange
        when(listGeographyUseCase.getCitiesByState("XX", "CO"))
                .thenThrow(new IllegalArgumentException("Estado no encontrado"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> geographyRestController.getCitiesByState("XX", "CO"));
        verify(listGeographyUseCase).getCitiesByState("XX", "CO");
        verify(geographyRestMapper, never()).toCityResponseList(any());
    }

    @Test
    @DisplayName("Debe propagar excepción cuando estado no pertenece al país")
    void testGetCitiesByStateStateDoesNotBelongToCountryPropagatesException() {
        // Arrange
        when(listGeographyUseCase.getCitiesByState("05", "US"))
                .thenThrow(new IllegalArgumentException("Estado no pertenece al país"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> geographyRestController.getCitiesByState("05", "US"));
        verify(listGeographyUseCase).getCitiesByState("05", "US");
        verify(geographyRestMapper, never()).toCityResponseList(any());
    }

    @Test
    @DisplayName("Debe procesar correctamente diferentes combinaciones de parámetros")
    void testGetCitiesByStateDifferentParameterCombinations() {
        // Arrange
        List<City> cities = Arrays.asList(city);
        List<CityResponse> cityResponses = Arrays.asList(cityResponse);

        when(listGeographyUseCase.getCitiesByState(anyString(), anyString())).thenReturn(cities);
        when(geographyRestMapper.toCityResponseList(cities)).thenReturn(cityResponses);

        // Act
        geographyRestController.getCitiesByState("05", "CO");
        geographyRestController.getCitiesByState("11", "CO");
        geographyRestController.getCitiesByState("NY", "US");

        // Assert
        verify(listGeographyUseCase).getCitiesByState("05", "CO");
        verify(listGeographyUseCase).getCitiesByState("11", "CO");
        verify(listGeographyUseCase).getCitiesByState("NY", "US");
    }

    // ==================== Integración y casos complejos ====================

    @Test
    @DisplayName("Debe mantener independencia entre llamadas de diferentes endpoints")
    void testIndependenceBetweenEndpoints() {
        // Arrange
        List<Country> countries = Arrays.asList(country);
        List<CountryResponse> countryResponses = Arrays.asList(countryResponse);
        List<State> states = Arrays.asList(state);
        List<StateResponse> stateResponses = Arrays.asList(stateResponse);

        when(listGeographyUseCase.getAllCountries()).thenReturn(countries);
        when(geographyRestMapper.toCountryResponseList(countries)).thenReturn(countryResponses);
        when(listGeographyUseCase.getStatesByCountry("CO")).thenReturn(states);
        when(geographyRestMapper.toStateResponseList(states)).thenReturn(stateResponses);

        // Act
        geographyRestController.getAllCountries();
        geographyRestController.getStatesByCountry("CO");

        // Assert
        verify(listGeographyUseCase).getAllCountries();
        verify(listGeographyUseCase).getStatesByCountry("CO");
        verify(geographyRestMapper).toCountryResponseList(countries);
        verify(geographyRestMapper).toStateResponseList(states);
    }

    @Test
    @DisplayName("Debe retornar status OK para todas las operaciones exitosas")
    void testReturnsOkStatusForSuccessfulOperations() {
        // Arrange
        List<Country> countries = Arrays.asList(country);
        List<CountryResponse> countryResponses = Arrays.asList(countryResponse);
        List<State> states = Arrays.asList(state);
        List<StateResponse> stateResponses = Arrays.asList(stateResponse);
        List<City> cities = Arrays.asList(city);
        List<CityResponse> cityResponses = Arrays.asList(cityResponse);

        when(listGeographyUseCase.getAllCountries()).thenReturn(countries);
        when(geographyRestMapper.toCountryResponseList(countries)).thenReturn(countryResponses);
        when(listGeographyUseCase.getStatesByCountry("CO")).thenReturn(states);
        when(geographyRestMapper.toStateResponseList(states)).thenReturn(stateResponses);
        when(listGeographyUseCase.getCitiesByState("05", "CO")).thenReturn(cities);
        when(geographyRestMapper.toCityResponseList(cities)).thenReturn(cityResponses);

        // Act
        ResponseEntity<List<CountryResponse>> response1 = geographyRestController.getAllCountries();
        ResponseEntity<List<StateResponse>> response2 = geographyRestController.getStatesByCountry("CO");
        ResponseEntity<List<CityResponse>> response3 = geographyRestController.getCitiesByState("05", "CO");

        // Assert
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
    }

    @Test
    @DisplayName("Debe manejar correctamente datos con caracteres especiales")
    void testHandlesDataWithSpecialCharacters() {
        // Arrange
        Country countryEspecial = Country.builder()
                .countryCode("BR")
                .countryName("Brasil (República Federativa)")
                .build();

        CountryResponse responseEspecial = CountryResponse.builder()
                .countryCode("BR")
                .countryName("Brasil (República Federativa)")
                .build();

        List<Country> countries = Arrays.asList(countryEspecial);
        List<CountryResponse> countryResponses = Arrays.asList(responseEspecial);

        when(listGeographyUseCase.getAllCountries()).thenReturn(countries);
        when(geographyRestMapper.toCountryResponseList(countries)).thenReturn(countryResponses);

        // Act
        ResponseEntity<List<CountryResponse>> response = geographyRestController.getAllCountries();

        // Assert
        assertNotNull(response.getBody());
        assertEquals("Brasil (República Federativa)", response.getBody().get(0).getCountryName());
    }

    @Test
    @DisplayName("Debe procesar correctamente múltiples llamadas consecutivas")
    void testProcessesMultipleConsecutiveCallsCorrectly() {
        // Arrange
        List<State> states = Arrays.asList(state);
        List<StateResponse> stateResponses = Arrays.asList(stateResponse);

        when(listGeographyUseCase.getStatesByCountry("CO")).thenReturn(states);
        when(geographyRestMapper.toStateResponseList(states)).thenReturn(stateResponses);

        // Act
        ResponseEntity<List<StateResponse>> response1 = geographyRestController.getStatesByCountry("CO");
        ResponseEntity<List<StateResponse>> response2 = geographyRestController.getStatesByCountry("CO");
        ResponseEntity<List<StateResponse>> response3 = geographyRestController.getStatesByCountry("CO");

        // Assert
        assertNotNull(response1);
        assertNotNull(response2);
        assertNotNull(response3);
        verify(listGeographyUseCase, times(3)).getStatesByCountry("CO");
    }
}
