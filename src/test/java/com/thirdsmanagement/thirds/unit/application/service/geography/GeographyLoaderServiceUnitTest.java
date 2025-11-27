package com.thirdsmanagement.thirds.unit.application.service.geography;

import com.thirdsmanagement.thirds.application.service.geography.GeographyLoaderService;
import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para GeographyLoaderService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GeographyLoaderServiceUnitTest {

    @Mock
    private GeographyOutputPort geographyOutputPort;

    @InjectMocks
    private GeographyLoaderService geographyLoaderService;

    private Country colombia;
    private State cundinamarca;
    private City bogota;

    @BeforeEach
    void setUp() {
        colombia = Country.builder()
                .countryCode("CO")
                .countryName("Colombia")
                .build();

        cundinamarca = State.builder()
                .stateCode("CUN")
                .stateName("Cundinamarca")
                .countryCode("CO")
                .build();

        bogota = City.builder()
                .cityCode("BOG")
                .cityName("Bogotá")
                .stateCode("CUN")
                .countryCode("CO")
                .build();
    }

    @Test
    @DisplayName("Debe cargar país correctamente por código")
    void testLoadCountryByCode_WithValidCode() {
        // Arrange
        List<Country> countries = Arrays.asList(colombia);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(countries);

        // Act
        Country result = geographyLoaderService.loadCountryByCode("CO");

        // Assert
        assertNotNull(result);
        assertEquals("CO", result.getCountryCode());
        assertEquals("Colombia", result.getCountryName());
        verify(geographyOutputPort).getAllActiveCountries();
    }

    @Test
    @DisplayName("Debe retornar null para código de país null")
    void testLoadCountryByCode_WithNullCode() {
        // Act
        Country result = geographyLoaderService.loadCountryByCode(null);

        // Assert
        assertNull(result);
        verify(geographyOutputPort, never()).getAllActiveCountries();
    }

    @Test
    @DisplayName("Debe retornar null para código de país vacío")
    void testLoadCountryByCode_WithEmptyCode() {
        // Act
        Country result = geographyLoaderService.loadCountryByCode("");

        // Assert
        assertNull(result);
        verify(geographyOutputPort, never()).getAllActiveCountries();
    }

    @Test
    @DisplayName("Debe retornar null para código de país solo espacios")
    void testLoadCountryByCode_WithWhitespaceCode() {
        // Act
        Country result = geographyLoaderService.loadCountryByCode("   ");

        // Assert
        assertNull(result);
        verify(geographyOutputPort, never()).getAllActiveCountries();
    }

    @Test
    @DisplayName("Debe retornar null cuando país no existe")
    void testLoadCountryByCode_WithNonExistentCountry() {
        // Arrange
        List<Country> countries = Arrays.asList(colombia);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(countries);

        // Act
        Country result = geographyLoaderService.loadCountryByCode("XX");

        // Assert
        assertNull(result);
        verify(geographyOutputPort).getAllActiveCountries();
    }

    @Test
    @DisplayName("Debe cargar estado correctamente por código y país")
    void testLoadStateByCode_WithValidCodes() {
        // Arrange
        List<Country> countries = Arrays.asList(colombia);
        List<State> states = Arrays.asList(cundinamarca);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(countries);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(states);

        // Act
        State result = geographyLoaderService.loadStateByCode("CUN", "CO");

        // Assert
        assertNotNull(result);
        assertEquals("CUN", result.getStateCode());
        assertEquals("Cundinamarca", result.getStateName());
        assertEquals("CO", result.getCountryCode());
        verify(geographyOutputPort).getAllActiveCountries();
        verify(geographyOutputPort).getStatesByCountry("CO");
    }

    @Test
    @DisplayName("Debe retornar null para código de estado null")
    void testLoadStateByCode_WithNullStateCode() {
        // Act
        State result = geographyLoaderService.loadStateByCode(null, "CO");

        // Assert
        assertNull(result);
        verify(geographyOutputPort, never()).getAllActiveCountries();
        verify(geographyOutputPort, never()).getStatesByCountry(anyString());
    }

    @Test
    @DisplayName("Debe retornar null para código de país null en estado")
    void testLoadStateByCode_WithNullCountryCode() {
        // Act
        State result = geographyLoaderService.loadStateByCode("CUN", null);

        // Assert
        assertNull(result);
        verify(geographyOutputPort, never()).getAllActiveCountries();
        verify(geographyOutputPort, never()).getStatesByCountry(anyString());
    }

    @Test
    @DisplayName("Debe retornar null cuando estado no existe")
    void testLoadStateByCode_WithNonExistentState() {
        // Arrange
        List<Country> countries = Arrays.asList(colombia);
        List<State> states = Arrays.asList(cundinamarca);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(countries);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(states);

        // Act
        State result = geographyLoaderService.loadStateByCode("XXX", "CO");

        // Assert
        assertNull(result);
        verify(geographyOutputPort).getAllActiveCountries();
        verify(geographyOutputPort).getStatesByCountry("CO");
    }

    @Test
    @DisplayName("Debe cargar ciudad correctamente por códigos")
    void testLoadCityByCode_WithValidCodes() {
        // Arrange
        List<Country> countries = Arrays.asList(colombia);
        List<State> states = Arrays.asList(cundinamarca);
        List<City> cities = Arrays.asList(bogota);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(countries);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(states);
        when(geographyOutputPort.getCitiesByState("CUN", "CO")).thenReturn(cities);

        // Act
        City result = geographyLoaderService.loadCityByCode("BOG", "CUN", "CO");

        // Assert
        assertNotNull(result);
        assertEquals("BOG", result.getCityCode());
        assertEquals("Bogotá", result.getCityName());
        assertEquals("CUN", result.getStateCode());
        assertEquals("CO", result.getCountryCode());
        verify(geographyOutputPort).getAllActiveCountries();
        verify(geographyOutputPort).getStatesByCountry("CO");
        verify(geographyOutputPort).getCitiesByState("CUN", "CO");
    }

    @Test
    @DisplayName("Debe retornar null para código de ciudad null")
    void testLoadCityByCode_WithNullCityCode() {
        // Act
        City result = geographyLoaderService.loadCityByCode(null, "CUN", "CO");

        // Assert
        assertNull(result);
        verify(geographyOutputPort, never()).getAllActiveCountries();
        verify(geographyOutputPort, never()).getStatesByCountry(anyString());
        verify(geographyOutputPort, never()).getCitiesByState(anyString(), anyString());
    }

    @Test
    @DisplayName("Debe retornar null para código de estado null en ciudad")
    void testLoadCityByCode_WithNullStateCode() {
        // Act
        City result = geographyLoaderService.loadCityByCode("BOG", null, "CO");

        // Assert
        assertNull(result);
        verify(geographyOutputPort, never()).getAllActiveCountries();
        verify(geographyOutputPort, never()).getStatesByCountry(anyString());
        verify(geographyOutputPort, never()).getCitiesByState(anyString(), anyString());
    }

    @Test
    @DisplayName("Debe retornar null para código de país null en ciudad")
    void testLoadCityByCode_WithNullCountryCode() {
        // Act
        City result = geographyLoaderService.loadCityByCode("BOG", "CUN", null);

        // Assert
        assertNull(result);
        verify(geographyOutputPort, never()).getAllActiveCountries();
        verify(geographyOutputPort, never()).getStatesByCountry(anyString());
        verify(geographyOutputPort, never()).getCitiesByState(anyString(), anyString());
    }

    @Test
    @DisplayName("Debe retornar null cuando ciudad no existe")
    void testLoadCityByCode_WithNonExistentCity() {
        // Arrange
        List<Country> countries = Arrays.asList(colombia);
        List<State> states = Arrays.asList(cundinamarca);
        List<City> cities = Arrays.asList(bogota);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(countries);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(states);
        when(geographyOutputPort.getCitiesByState("CUN", "CO")).thenReturn(cities);

        // Act
        City result = geographyLoaderService.loadCityByCode("XXX", "CUN", "CO");

        // Assert
        assertNull(result);
        verify(geographyOutputPort).getAllActiveCountries();
        verify(geographyOutputPort).getStatesByCountry("CO");
        verify(geographyOutputPort).getCitiesByState("CUN", "CO");
    }

    @Test
    @DisplayName("Debe manejar excepciones del puerto correctamente")
    void testLoadCityByCode_WithPortException() {
        // Arrange
        List<Country> countries = Arrays.asList(colombia);
        List<State> states = Arrays.asList(cundinamarca);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(countries);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(states);
        when(geographyOutputPort.getCitiesByState("CUN", "CO")).thenThrow(new RuntimeException("DB Error"));

        // Act
        City result = geographyLoaderService.loadCityByCode("BOG", "CUN", "CO");

        // Assert
        assertNotNull(result);
        assertEquals("BOG", result.getCityCode());
        assertEquals("", result.getCityName());
        assertEquals("CUN", result.getStateCode());
        assertEquals("CO", result.getCountryCode());
        verify(geographyOutputPort).getCitiesByState("CUN", "CO");
    }
}
