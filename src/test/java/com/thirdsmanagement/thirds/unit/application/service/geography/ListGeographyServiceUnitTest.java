package com.thirdsmanagement.thirds.unit.application.service.geography;

import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.application.service.geography.ListGeographyService;
import com.thirdsmanagement.thirds.domain.exceptions.geography.CountryNotFoundException;
import com.thirdsmanagement.thirds.domain.exceptions.geography.StateNotFoundException;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ListGeographyService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ListGeographyServiceUnitTest {

    @Mock
    private GeographyOutputPort geographyOutputPort;

    @InjectMocks
    private ListGeographyService listGeographyService;

    private Country colombia;
    private Country mexico;
    private State cundinamarca;
    private State antioquia;
    private City bogota;
    private City medellin;

    @BeforeEach
    void setUp() {
        colombia = Country.builder()
                .countryCode("CO")
                .countryName("Colombia")
                .build();

        mexico = Country.builder()
                .countryCode("MX")
                .countryName("México")
                .build();

        cundinamarca = State.builder()
                .stateCode("CUN")
                .stateName("Cundinamarca")
                .countryCode("CO")
                .build();

        antioquia = State.builder()
                .stateCode("ANT")
                .stateName("Antioquia")
                .countryCode("CO")
                .build();

        bogota = City.builder()
                .cityCode("BOG")
                .cityName("Bogotá")
                .stateCode("CUN")
                .countryCode("CO")
                .build();

        medellin = City.builder()
                .cityCode("MED")
                .cityName("Medellín")
                .stateCode("ANT")
                .countryCode("CO")
                .build();
    }

    @Test
    @DisplayName("Debe obtener todos los países correctamente")
    void testGetAllCountries_WithCountriesPresent() {
        // Arrange
        List<Country> countries = Arrays.asList(colombia, mexico);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(countries);

        // Act
        List<Country> result = listGeographyService.getAllCountries();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(colombia));
        assertTrue(result.contains(mexico));
        verify(geographyOutputPort).getAllActiveCountries();
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay países")
    void testGetAllCountries_WithNoCountries() {
        // Arrange
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(Collections.emptyList());

        // Act
        List<Country> result = listGeographyService.getAllCountries();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(geographyOutputPort).getAllActiveCountries();
    }

    @Test
    @DisplayName("Debe obtener estados de un país correctamente")
    void testGetStatesByCountry_WithValidCountry() {
        // Arrange
        List<State> states = Arrays.asList(cundinamarca, antioquia);
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(states);

        // Act
        List<State> result = listGeographyService.getStatesByCountry("CO");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(cundinamarca));
        assertTrue(result.contains(antioquia));
        verify(geographyOutputPort).existsActiveCountry("CO");
        verify(geographyOutputPort).getStatesByCountry("CO");
    }

    @Test
    @DisplayName("Debe lanzar excepción para país no existente")
    void testGetStatesByCountry_WithNonExistentCountry() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("XX")).thenReturn(false);

        // Act & Assert
        CountryNotFoundException exception = assertThrows(CountryNotFoundException.class, () -> {
            listGeographyService.getStatesByCountry("XX");
        });

        assertEquals("El país con código 'XX' no existe", exception.getMessage());
        verify(geographyOutputPort).existsActiveCountry("XX");
        verify(geographyOutputPort, never()).getStatesByCountry(anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción para código de país null")
    void testGetStatesByCountry_WithNullCountryCode() {
        // Act & Assert
        assertThrows(CountryNotFoundException.class, () -> {
            listGeographyService.getStatesByCountry(null);
        });

        verify(geographyOutputPort, never()).existsActiveCountry(anyString());
        verify(geographyOutputPort, never()).getStatesByCountry(anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción para código de país vacío")
    void testGetStatesByCountry_WithEmptyCountryCode() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("")).thenReturn(false);

        // Act & Assert
        assertThrows(CountryNotFoundException.class, () -> {
            listGeographyService.getStatesByCountry("");
        });

        verify(geographyOutputPort).existsActiveCountry("");
        verify(geographyOutputPort, never()).getStatesByCountry(anyString());
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando país no tiene estados")
    void testGetStatesByCountry_WithCountryWithoutStates() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(Collections.emptyList());

        // Act
        List<State> result = listGeographyService.getStatesByCountry("CO");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(geographyOutputPort).existsActiveCountry("CO");
        verify(geographyOutputPort).getStatesByCountry("CO");
    }

    @Test
    @DisplayName("Debe obtener ciudades de un estado correctamente")
    void testGetCitiesByState_WithValidStateAndCountry() {
        // Arrange
        List<City> cities = Arrays.asList(bogota);
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.existsActiveState("CUN", "CO")).thenReturn(true);
        when(geographyOutputPort.getCitiesByState("CUN", "CO")).thenReturn(cities);

        // Act
        List<City> result = listGeographyService.getCitiesByState("CUN", "CO");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(bogota));
        verify(geographyOutputPort).existsActiveCountry("CO");
        verify(geographyOutputPort).existsActiveState("CUN", "CO");
        verify(geographyOutputPort).getCitiesByState("CUN", "CO");
    }

    @Test
    @DisplayName("Debe lanzar excepción para estado no existente")
    void testGetCitiesByState_WithNonExistentState() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.existsActiveState("XXX", "CO")).thenReturn(false);

        // Act & Assert
        assertThrows(StateNotFoundException.class, () -> {
            listGeographyService.getCitiesByState("XXX", "CO");
        });

        verify(geographyOutputPort).existsActiveCountry("CO");
        verify(geographyOutputPort).existsActiveState("XXX", "CO");
        verify(geographyOutputPort, never()).getCitiesByState(anyString(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción para código de estado null")
    void testGetCitiesByState_WithNullStateCode() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.existsActiveState(null, "CO")).thenReturn(false);

        // Act & Assert
        assertThrows(StateNotFoundException.class, () -> {
            listGeographyService.getCitiesByState(null, "CO");
        });

        verify(geographyOutputPort).existsActiveCountry("CO");
        verify(geographyOutputPort).existsActiveState(null, "CO");
        verify(geographyOutputPort, never()).getCitiesByState(anyString(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción para código de país null en ciudades")
    void testGetCitiesByState_WithNullCountryCode() {
        // Act & Assert
        assertThrows(CountryNotFoundException.class, () -> {
            listGeographyService.getCitiesByState("CUN", null);
        });

        verify(geographyOutputPort, never()).existsActiveCountry(anyString());
        verify(geographyOutputPort, never()).existsActiveState(anyString(), anyString());
        verify(geographyOutputPort, never()).getCitiesByState(anyString(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción para códigos de estado y país vacíos")
    void testGetCitiesByState_WithEmptyCodes() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("")).thenReturn(false);

        // Act & Assert
        assertThrows(CountryNotFoundException.class, () -> {
            listGeographyService.getCitiesByState("", "");
        });

        verify(geographyOutputPort).existsActiveCountry("");
        verify(geographyOutputPort, never()).existsActiveState(anyString(), anyString());
        verify(geographyOutputPort, never()).getCitiesByState(anyString(), anyString());
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando estado no tiene ciudades")
    void testGetCitiesByState_WithStateWithoutCities() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.existsActiveState("CUN", "CO")).thenReturn(true);
        when(geographyOutputPort.getCitiesByState("CUN", "CO")).thenReturn(Collections.emptyList());

        // Act
        List<City> result = listGeographyService.getCitiesByState("CUN", "CO");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(geographyOutputPort).existsActiveCountry("CO");
        verify(geographyOutputPort).existsActiveState("CUN", "CO");
        verify(geographyOutputPort).getCitiesByState("CUN", "CO");
    }

    @Test
    @DisplayName("Debe validar jerarquía geográfica correctamente")
    void testGetCitiesByState_WithHierarchyValidation() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("MX")).thenReturn(true);
        when(geographyOutputPort.existsActiveState("CUN", "MX")).thenReturn(false);

        // Act & Assert
        assertThrows(StateNotFoundException.class, () -> {
            listGeographyService.getCitiesByState("CUN", "MX");
        });

        verify(geographyOutputPort).existsActiveCountry("MX");
        verify(geographyOutputPort).existsActiveState("CUN", "MX");
        verify(geographyOutputPort, never()).getCitiesByState(anyString(), anyString());
    }

    @Test
    @DisplayName("Debe manejar múltiples ciudades correctamente")
    void testGetCitiesByState_WithMultipleCities() {
        // Arrange
        List<City> cities = Arrays.asList(bogota, medellin);
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.existsActiveState("ANT", "CO")).thenReturn(true);
        when(geographyOutputPort.getCitiesByState("ANT", "CO")).thenReturn(cities);

        // Act
        List<City> result = listGeographyService.getCitiesByState("ANT", "CO");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(bogota));
        assertTrue(result.contains(medellin));
        verify(geographyOutputPort).existsActiveCountry("CO");
        verify(geographyOutputPort).existsActiveState("ANT", "CO");
        verify(geographyOutputPort).getCitiesByState("ANT", "CO");
    }
}
