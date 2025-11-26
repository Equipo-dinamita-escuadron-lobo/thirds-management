package com.thirdsmanagement.thirds.unit.application.service.third;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.application.service.third.ThirdGeographyValidationService;
import com.thirdsmanagement.thirds.domain.exceptions.geography.CountryNotFoundException;
import com.thirdsmanagement.thirds.domain.exceptions.geography.GeographyInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.geography.StateNotFoundException;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ThirdGeographyValidationServiceUnitTest {

    @Mock
    private GeographyOutputPort geographyOutputPort;

    @InjectMocks
    private ThirdGeographyValidationService thirdGeographyValidationService;

    private Country country;
    private State state;
    private City city;

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
                .build();

        city = City.builder()
                .cityCode("05001")
                .cityName("Medellín")
                .stateCode("05")
                .countryCode("CO")
                .build();
    }

    // ==================== Validación completa exitosa ====================

    @Test
    @DisplayName("test_ValidateAndGetGeography_ConPaisDepartamentoYCiudad_RetornaGeografiaCompleta")
    void testValidateAndGetGeographyConPaisDepartamentoYCiudadRetornaGeografiaCompleta() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("05", "CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(List.of(state));
        when(geographyOutputPort.existsActiveCity("05001", "05", "CO")).thenReturn(true);
        when(geographyOutputPort.getCitiesByState("05", "CO")).thenReturn(List.of(city));

        // Act
        Object[] result = thirdGeographyValidationService.validateAndGetGeography("CO", "05", "05001");

        // Assert
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals(country, result[0]);
        assertEquals(state, result[1]);
        assertEquals(city, result[2]);
        verify(geographyOutputPort).existsActiveCountry("CO");
        verify(geographyOutputPort).getAllActiveCountries();
        verify(geographyOutputPort).existsActiveState("05", "CO");
        verify(geographyOutputPort).getStatesByCountry("CO");
        verify(geographyOutputPort).existsActiveCity("05001", "05", "CO");
        verify(geographyOutputPort).getCitiesByState("05", "CO");
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_SoloPaisYDepartamento_RetornaGeografiaParcial")
    void testValidateAndGetGeographySoloPaisYDepartamentoRetornaGeografiaParcial() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("05", "CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(List.of(state));

        // Act
        Object[] result = thirdGeographyValidationService.validateAndGetGeography("CO", "05", null);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals(country, result[0]);
        assertEquals(state, result[1]);
        assertNull(result[2]);
        verify(geographyOutputPort).existsActiveCountry("CO");
        verify(geographyOutputPort).getAllActiveCountries();
        verify(geographyOutputPort).existsActiveState("05", "CO");
        verify(geographyOutputPort).getStatesByCountry("CO");
        verify(geographyOutputPort, never()).existsActiveCity(anyString(), anyString(), anyString());
        verify(geographyOutputPort, never()).getCitiesByState(anyString(), anyString());
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_SoloPais_RetornaSoloPais")
    void testValidateAndGetGeographySoloPaisRetornaSoloPais() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));

        // Act
        Object[] result = thirdGeographyValidationService.validateAndGetGeography("CO", null, null);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals(country, result[0]);
        assertNull(result[1]);
        assertNull(result[2]);
        verify(geographyOutputPort).existsActiveCountry("CO");
        verify(geographyOutputPort).getAllActiveCountries();
        verify(geographyOutputPort, never()).existsActiveState(anyString(), anyString());
        verify(geographyOutputPort, never()).getStatesByCountry(anyString());
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_TodosNulos_RetornaTodosNulos")
    void testValidateAndGetGeographyTodosNulosRetornaTodosNulos() {
        // Arrange - No se necesita configurar mocks

        // Act
        Object[] result = thirdGeographyValidationService.validateAndGetGeography(null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.length);
        assertNull(result[0]);
        assertNull(result[1]);
        assertNull(result[2]);
        verify(geographyOutputPort, never()).existsActiveCountry(anyString());
        verify(geographyOutputPort, never()).existsActiveState(anyString(), anyString());
        verify(geographyOutputPort, never()).existsActiveCity(anyString(), anyString(), anyString());
    }

    // ==================== Normalización de códigos ====================

    @Test
    @DisplayName("test_ValidateAndGetGeography_CodigosConEspacios_NormalizaCorrectamente")
    void testValidateAndGetGeographyCodigosConEspaciosNormalizaCorrectamente() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("05", "CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(List.of(state));

        // Act
        Object[] result = thirdGeographyValidationService.validateAndGetGeography("  CO  ", "  05  ", null);

        // Assert
        assertNotNull(result);
        assertEquals(country, result[0]);
        assertEquals(state, result[1]);
        verify(geographyOutputPort).existsActiveCountry("CO");
        verify(geographyOutputPort).existsActiveState("05", "CO");
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_CodigosPaisMinusculas_ConvierteAMayusculas")
    void testValidateAndGetGeographyCodigosPaisMinusculasConvierteAMayusculas() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));

        // Act
        Object[] result = thirdGeographyValidationService.validateAndGetGeography("co", null, null);

        // Assert
        assertNotNull(result);
        assertEquals(country, result[0]);
        verify(geographyOutputPort).existsActiveCountry("CO");
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_CodigosVacios_TratadosComoNulos")
    void testValidateAndGetGeographyCodigosVaciosTratadosComoNulos() {
        // Arrange - No se necesita configurar mocks

        // Act
        Object[] result = thirdGeographyValidationService.validateAndGetGeography("", "  ", "   ");

        // Assert
        assertNotNull(result);
        assertNull(result[0]);
        assertNull(result[1]);
        assertNull(result[2]);
        verify(geographyOutputPort, never()).existsActiveCountry(anyString());
    }

    // ==================== Excepciones por país no encontrado ====================

    @Test
    @DisplayName("test_ValidateAndGetGeography_PaisNoExiste_LanzaCountryNotFoundException")
    void testValidateAndGetGeographyPaisNoExisteLanzaCountryNotFoundException() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("XX")).thenReturn(false);

        // Act & Assert
        CountryNotFoundException exception = assertThrows(
                CountryNotFoundException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography("XX", null, null)
        );

        assertNotNull(exception);
        verify(geographyOutputPort).existsActiveCountry("XX");
        verify(geographyOutputPort, never()).getAllActiveCountries();
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_PaisExisteEnExistsPeroNoEnLista_LanzaCountryNotFoundException")
    void testValidateAndGetGeographyPaisExisteEnExistsPeroNoEnListaLanzaCountryNotFoundException() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(Collections.emptyList());

        // Act & Assert
        CountryNotFoundException exception = assertThrows(
                CountryNotFoundException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography("CO", null, null)
        );

        assertNotNull(exception);
        verify(geographyOutputPort).existsActiveCountry("CO");
        verify(geographyOutputPort).getAllActiveCountries();
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_PaisInactivoNoEncontrado_LanzaCountryNotFoundException")
    void testValidateAndGetGeographyPaisInactivoNoEncontradoLanzaCountryNotFoundException() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("VE")).thenReturn(false);

        // Act & Assert
        assertThrows(
                CountryNotFoundException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography("VE", "01", null)
        );

        verify(geographyOutputPort).existsActiveCountry("VE");
    }

    // ==================== Excepciones por departamento no encontrado ====================

    @Test
    @DisplayName("test_ValidateAndGetGeography_DepartamentoNoExiste_LanzaStateNotFoundException")
    void testValidateAndGetGeographyDepartamentoNoExisteLanzaStateNotFoundException() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("99", "CO")).thenReturn(false);

        // Act & Assert
        StateNotFoundException exception = assertThrows(
                StateNotFoundException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography("CO", "99", null)
        );

        assertNotNull(exception);
        verify(geographyOutputPort).existsActiveState("99", "CO");
        verify(geographyOutputPort, never()).getStatesByCountry(anyString());
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_DepartamentoExisteEnExistsPeroNoEnLista_LanzaStateNotFoundException")
    void testValidateAndGetGeographyDepartamentoExisteEnExistsPeroNoEnListaLanzaStateNotFoundException() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("05", "CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(Collections.emptyList());

        // Act & Assert
        StateNotFoundException exception = assertThrows(
                StateNotFoundException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography("CO", "05", null)
        );

        assertNotNull(exception);
        verify(geographyOutputPort).getStatesByCountry("CO");
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_DepartamentoSinPais_LanzaGeographyInvalidDataException")
    void testValidateAndGetGeographyDepartamentoSinPaisLanzaGeographyInvalidDataException() {
        // Arrange - No se necesita configurar mocks

        // Act & Assert
        GeographyInvalidDataException exception = assertThrows(
                GeographyInvalidDataException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography(null, "05", null)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("obligatorio cuando se especifica estado"));
        verify(geographyOutputPort, never()).existsActiveState(anyString(), anyString());
    }

    // ==================== Excepciones por ciudad no encontrada ====================

    @Test
    @DisplayName("test_ValidateAndGetGeography_CiudadNoExiste_LanzaGeographyInvalidDataException")
    void testValidateAndGetGeographyCiudadNoExisteLanzaGeographyInvalidDataException() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("05", "CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(List.of(state));
        when(geographyOutputPort.existsActiveCity("99999", "05", "CO")).thenReturn(false);

        // Act & Assert
        GeographyInvalidDataException exception = assertThrows(
                GeographyInvalidDataException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography("CO", "05", "99999")
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("no existe"));
        verify(geographyOutputPort).existsActiveCity("99999", "05", "CO");
        verify(geographyOutputPort, never()).getCitiesByState(anyString(), anyString());
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_CiudadExisteEnExistsPeroNoEnLista_LanzaGeographyInvalidDataException")
    void testValidateAndGetGeographyCiudadExisteEnExistsPeroNoEnListaLanzaGeographyInvalidDataException() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("05", "CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(List.of(state));
        when(geographyOutputPort.existsActiveCity("05001", "05", "CO")).thenReturn(true);
        when(geographyOutputPort.getCitiesByState("05", "CO")).thenReturn(Collections.emptyList());

        // Act & Assert
        GeographyInvalidDataException exception = assertThrows(
                GeographyInvalidDataException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography("CO", "05", "05001")
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("no existe"));
        verify(geographyOutputPort).getCitiesByState("05", "CO");
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_CiudadSinDepartamento_LanzaGeographyInvalidDataException")
    void testValidateAndGetGeographyCiudadSinDepartamentoLanzaGeographyInvalidDataException() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));

        // Act & Assert
        GeographyInvalidDataException exception = assertThrows(
                GeographyInvalidDataException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography("CO", null, "05001")
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("obligatorio cuando se especifica ciudad"));
        verify(geographyOutputPort, never()).existsActiveCity(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_CiudadSinPais_LanzaGeographyInvalidDataException")
    void testValidateAndGetGeographyCiudadSinPaisLanzaGeographyInvalidDataException() {
        // Arrange - No se necesita configurar mocks

        // Act & Assert
        GeographyInvalidDataException exception = assertThrows(
                GeographyInvalidDataException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography(null, "05", "05001")
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("obligatorio cuando se especifica estado"));
        verify(geographyOutputPort, never()).existsActiveCity(anyString(), anyString(), anyString());
    }

    // ==================== Validación de jerarquía geográfica ====================

    @Test
    @DisplayName("test_ValidateAndGetGeography_DepartamentoNoPerteneceAPais_LanzaGeographyInvalidDataException")
    void testValidateAndGetGeographyDepartamentoNoPerteneceAPaisLanzaGeographyInvalidDataException() {
        // Arrange
        State stateVenezuela = State.builder()
                .stateCode("01")
                .stateName("Distrito Capital")
                .countryCode("VE")
                .build();

        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("01", "CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(List.of(stateVenezuela));

        // Act & Assert
        GeographyInvalidDataException exception = assertThrows(
                GeographyInvalidDataException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography("CO", "01", null)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("no pertenece al país"));
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_CiudadNoPerteneceADepartamento_LanzaGeographyInvalidDataException")
    void testValidateAndGetGeographyCiudadNoPerteneceADepartamentoLanzaGeographyInvalidDataException() {
        // Arrange
        City cityBogota = City.builder()
                .cityCode("11001")
                .cityName("Bogotá")
                .stateCode("11")
                .countryCode("CO")
                .build();

        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("05", "CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(List.of(state));
        when(geographyOutputPort.existsActiveCity("11001", "05", "CO")).thenReturn(true);
        when(geographyOutputPort.getCitiesByState("05", "CO")).thenReturn(List.of(cityBogota));

        // Act & Assert
        GeographyInvalidDataException exception = assertThrows(
                GeographyInvalidDataException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography("CO", "05", "11001")
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("no pertenece al estado"));
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_CiudadNoPerteneceAPais_LanzaGeographyInvalidDataException")
    void testValidateAndGetGeographyCiudadNoPerteneceAPaisLanzaGeographyInvalidDataException() {
        // Arrange
        City cityCaracas = City.builder()
                .cityCode("01001")
                .cityName("Caracas")
                .stateCode("05")
                .countryCode("VE")
                .build();

        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("05", "CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(List.of(state));
        when(geographyOutputPort.existsActiveCity("01001", "05", "CO")).thenReturn(true);
        when(geographyOutputPort.getCitiesByState("05", "CO")).thenReturn(List.of(cityCaracas));

        // Act & Assert
        GeographyInvalidDataException exception = assertThrows(
                GeographyInvalidDataException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography("CO", "05", "01001")
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("no pertenece al estado"));
    }

    // ==================== Diferentes entidades geográficas ====================

    @Test
    @DisplayName("test_ValidateAndGetGeography_DiferentesPaises_ValidaCorrectamente")
    void testValidateAndGetGeographyDiferentesPaisesValidaCorrectamente() {
        // Arrange
        Country mexico = Country.builder()
                .countryCode("MX")
                .countryName("México")
                .build();

        when(geographyOutputPort.existsActiveCountry("MX")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(mexico));

        // Act
        Object[] result = thirdGeographyValidationService.validateAndGetGeography("MX", null, null);

        // Assert
        assertNotNull(result);
        assertEquals(mexico, result[0]);
        verify(geographyOutputPort).existsActiveCountry("MX");
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_DiferentesDepartamentos_ValidaCorrectamente")
    void testValidateAndGetGeographyDiferentesDepartamentosValidaCorrectamente() {
        // Arrange
        State cundinamarca = State.builder()
                .stateCode("25")
                .stateName("Cundinamarca")
                .countryCode("CO")
                .build();

        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("25", "CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(List.of(cundinamarca));

        // Act
        Object[] result = thirdGeographyValidationService.validateAndGetGeography("CO", "25", null);

        // Assert
        assertNotNull(result);
        assertEquals(country, result[0]);
        assertEquals(cundinamarca, result[1]);
        verify(geographyOutputPort).existsActiveState("25", "CO");
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_DiferentesCiudades_ValidaCorrectamente")
    void testValidateAndGetGeographyDiferentesCiudadesValidaCorrectamente() {
        // Arrange
        City envigado = City.builder()
                .cityCode("05266")
                .cityName("Envigado")
                .stateCode("05")
                .countryCode("CO")
                .build();

        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("05", "CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(List.of(state));
        when(geographyOutputPort.existsActiveCity("05266", "05", "CO")).thenReturn(true);
        when(geographyOutputPort.getCitiesByState("05", "CO")).thenReturn(List.of(envigado));

        // Act
        Object[] result = thirdGeographyValidationService.validateAndGetGeography("CO", "05", "05266");

        // Assert
        assertNotNull(result);
        assertEquals(country, result[0]);
        assertEquals(state, result[1]);
        assertEquals(envigado, result[2]);
        verify(geographyOutputPort).existsActiveCity("05266", "05", "CO");
    }

    // ==================== Casos de integración ====================

    @Test
    @DisplayName("test_ValidateAndGetGeography_MultiplesPaises_CadaUnoConSusDatos")
    void testValidateAndGetGeographyMultiplesPaisesCadaUnoConSusDatos() {
        // Arrange
        Country usa = Country.builder()
                .countryCode("US")
                .countryName("United States")
                .build();

        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.existsActiveCountry("US")).thenReturn(true);

        // Act
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        Object[] result1 = thirdGeographyValidationService.validateAndGetGeography("CO", null, null);
        
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(usa));
        Object[] result2 = thirdGeographyValidationService.validateAndGetGeography("US", null, null);

        // Assert
        assertEquals(country, result1[0]);
        assertEquals(usa, result2[0]);
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_JerarquiaCompleta_TodasLasValidacionesEjecutadas")
    void testValidateAndGetGeographyJerarquiaCompletaTodasLasValidacionesEjecutadas() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("05", "CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(List.of(state));
        when(geographyOutputPort.existsActiveCity("05001", "05", "CO")).thenReturn(true);
        when(geographyOutputPort.getCitiesByState("05", "CO")).thenReturn(List.of(city));

        // Act
        Object[] result = thirdGeographyValidationService.validateAndGetGeography("CO", "05", "05001");

        // Assert
        assertNotNull(result);
        assertEquals(country, result[0]);
        assertEquals(state, result[1]);
        assertEquals(city, result[2]);
        verify(geographyOutputPort).existsActiveCountry("CO");
        verify(geographyOutputPort).getAllActiveCountries();
        verify(geographyOutputPort).existsActiveState("05", "CO");
        verify(geographyOutputPort).getStatesByCountry("CO");
        verify(geographyOutputPort).existsActiveCity("05001", "05", "CO");
        verify(geographyOutputPort).getCitiesByState("05", "CO");
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_MultiplesCiudadesEnLista_EncuentraLaCorrecta")
    void testValidateAndGetGeographyMultiplesCiudadesEnListaEncuentraLaCorrecta() {
        // Arrange
        City itagui = City.builder()
                .cityCode("05360")
                .cityName("Itagüí")
                .stateCode("05")
                .countryCode("CO")
                .build();

        City bello = City.builder()
                .cityCode("05088")
                .cityName("Bello")
                .stateCode("05")
                .countryCode("CO")
                .build();

        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("05", "CO")).thenReturn(true);
        when(geographyOutputPort.getStatesByCountry("CO")).thenReturn(List.of(state));
        when(geographyOutputPort.existsActiveCity("05001", "05", "CO")).thenReturn(true);
        when(geographyOutputPort.getCitiesByState("05", "CO")).thenReturn(List.of(itagui, city, bello));

        // Act
        Object[] result = thirdGeographyValidationService.validateAndGetGeography("CO", "05", "05001");

        // Assert
        assertNotNull(result);
        assertEquals(city, result[2]);
        assertEquals("Medellín", ((City) result[2]).getCityName());
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_PropagaExcepcionDelPuerto_CuandoOcurreError")
    void testValidateAndGetGeographyPropagaExcepcionDelPuertoCuandoOcurreError() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenThrow(new RuntimeException("Error de conexión"));

        // Act & Assert
        assertThrows(
                RuntimeException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography("CO", null, null)
        );

        verify(geographyOutputPort).existsActiveCountry("CO");
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_ValidacionDeOrden_VerificaPaisAntesDeDepartamento")
    void testValidateAndGetGeographyValidacionDeOrdenVerificaPaisAntesDeDepartamento() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(false);

        // Act & Assert
        assertThrows(
                CountryNotFoundException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography("CO", "05", null)
        );

        verify(geographyOutputPort).existsActiveCountry("CO");
        verify(geographyOutputPort, never()).existsActiveState(anyString(), anyString());
    }

    @Test
    @DisplayName("test_ValidateAndGetGeography_ValidacionDeOrden_VerificaDepartamentoAntesDeCiudad")
    void testValidateAndGetGeographyValidacionDeOrdenVerificaDepartamentoAntesDeCiudad() {
        // Arrange
        when(geographyOutputPort.existsActiveCountry("CO")).thenReturn(true);
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(List.of(country));
        when(geographyOutputPort.existsActiveState("05", "CO")).thenReturn(false);

        // Act & Assert
        assertThrows(
                StateNotFoundException.class,
                () -> thirdGeographyValidationService.validateAndGetGeography("CO", "05", "05001")
        );

        verify(geographyOutputPort).existsActiveState("05", "CO");
        verify(geographyOutputPort, never()).existsActiveCity(anyString(), anyString(), anyString());
    }
}
