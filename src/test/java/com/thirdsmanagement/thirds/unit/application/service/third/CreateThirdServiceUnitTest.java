package com.thirdsmanagement.thirds.unit.application.service.third;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.application.service.third.CreateThirdService;
import com.thirdsmanagement.thirds.application.service.third.ThirdGeographyValidationService;
import com.thirdsmanagement.thirds.application.service.third.ThirdValidationService;
import com.thirdsmanagement.thirds.application.service.typeId.TypeIdLoaderService;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdAlreadyExistsException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeForeignKeyViolationException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdInvalidDataException;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;

/**
 * Tests unitarios para CreateThirdService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreateThirdServiceUnitTest {

    @Mock
    private ThirdOutputPort thirdOutputPort;

    @Mock
    private ThirdRepository thirdRepository;

    @Mock
    private ThirdGeographyValidationService geographyValidationService;

    @Mock
    private ThirdValidationService thirdValidationService;

    @Mock
    private TypeIdLoaderService typeIdLoaderService;

    @Mock
    private IdOutputPort idOutputPort;

    @InjectMocks
    private CreateThirdService createThirdService;

    private String entId;
    private String countryCode;
    private String stateCode;
    private String cityCode;
    private Third third;
    private TypeId typeId;
    private ThirdType thirdType;
    private Country country;
    private State state;
    private City city;

    @BeforeEach
    void setUp() {
        entId = "ENT001";
        countryCode = "CO";
        stateCode = "05";
        cityCode = "05001";

        typeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .status(true)
                .build();

        thirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .status(true)
                .build();

        third = Third.builder()
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .thirdTypes(Set.of(thirdType))
                .names("Juan Carlos")
                .lastNames("Pérez Gómez")
                .gender(eThirdGender.Masculino)
                .idNumber(123456789L)
                .state(true)
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("juan@email.com")
                .build();

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

    // ========== createThird Tests - Creación Exitosa Persona Natural ==========

    @Test
    @DisplayName("Debe crear tercero persona natural exitosamente con geografía")
    void testCreateThird_SuccessfulNaturalPersonWithGeography() {
        // Arrange
        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        doNothing().when(thirdValidationService).validateNitFormat(third);
        doNothing().when(thirdValidationService).validateVerificationDigit(third);
        when(idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdType.getThirdTypeId())).thenReturn(thirdType);
        when(geographyValidationService.validateAndGetGeography(countryCode, stateCode, cityCode))
                .thenReturn(new Object[]{country, state, city});
        when(thirdRepository.existThirdBy(third.getIdNumber(), entId)).thenReturn(false);
        when(thirdOutputPort.saveThird(any(Third.class))).thenReturn(third);

        // Act
        Third result = createThirdService.createThird(third, countryCode, stateCode, cityCode);

        // Assert
        assertNotNull(result);
        verify(thirdValidationService).validatePersonTypeConsistency(third);
        verify(typeIdLoaderService).loadCompleteTypeId(third);
        verify(geographyValidationService).validateAndGetGeography(countryCode, stateCode, cityCode);
        verify(thirdRepository).existThirdBy(third.getIdNumber(), entId);
        verify(thirdOutputPort).saveThird(any(Third.class));
    }

    @Test
    @DisplayName("Debe crear tercero persona natural sin geografía")
    void testCreateThird_SuccessfulNaturalPersonWithoutGeography() {
        // Arrange
        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        doNothing().when(thirdValidationService).validateNitFormat(third);
        doNothing().when(thirdValidationService).validateVerificationDigit(third);
        when(idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdType.getThirdTypeId())).thenReturn(thirdType);
        when(thirdRepository.existThirdBy(third.getIdNumber(), entId)).thenReturn(false);
        when(thirdOutputPort.saveThird(any(Third.class))).thenReturn(third);

        // Act
        Third result = createThirdService.createThird(third, null, null, null);

        // Assert
        assertNotNull(result);
        verify(geographyValidationService, never()).validateAndGetGeography(anyString(), anyString(), anyString());
        verify(thirdOutputPort).saveThird(any(Third.class));
    }

    @Test
    @DisplayName("Debe normalizar nombres y apellidos preservando mayúsculas")
    void testCreateThird_NormalizesNamesPreservingCase() {
        // Arrange
        third.setNames("  Juan   Carlos  ");
        third.setLastNames("  Pérez   Gómez  ");

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        when(idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdType.getThirdTypeId())).thenReturn(thirdType);
        when(thirdRepository.existThirdBy(third.getIdNumber(), entId)).thenReturn(false);
        when(thirdOutputPort.saveThird(any(Third.class))).thenReturn(third);

        // Act
        createThirdService.createThird(third, null, null, null);

        // Assert
        verify(thirdOutputPort).saveThird(argThat(t ->
                t.getNames() != null && !t.getNames().startsWith(" ") &&
                        t.getLastNames() != null && !t.getLastNames().startsWith(" ")
        ));
    }

    // ========== createThird Tests - Creación Exitosa Persona Jurídica ==========

    @Test
    @DisplayName("Debe crear tercero persona jurídica exitosamente")
    void testCreateThird_SuccessfulLegalEntity() {
        // Arrange
        TypeId nitTypeId = TypeId.builder()
                .id(2L)
                .typeId("NIT")
                .typeIdname("Número de Identificación Tributaria")
                .status(true)
                .build();

        Third juridica = Third.builder()
                .entId(entId)
                .personType(ePersonType.Juridica)
                .typeId(nitTypeId)
                .thirdTypes(Set.of(thirdType))
                .socialReason("Empresa Test S.A.S")
                .idNumber(900123456L)
                .verificationNumber(7L)
                .state(true)
                .build();

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(juridica);
        when(typeIdLoaderService.loadCompleteTypeId(juridica)).thenReturn(juridica);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(juridica);
        doNothing().when(thirdValidationService).validateNitFormat(juridica);
        doNothing().when(thirdValidationService).validateVerificationDigit(juridica);
        when(idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdType.getThirdTypeId())).thenReturn(thirdType);
        when(thirdRepository.existThirdBy(juridica.getIdNumber(), entId)).thenReturn(false);
        when(thirdOutputPort.saveThird(any(Third.class))).thenReturn(juridica);

        // Act
        Third result = createThirdService.createThird(juridica, null, null, null);

        // Assert
        assertNotNull(result);
        verify(thirdValidationService).validateNitFormat(juridica);
        verify(thirdValidationService).validateVerificationDigit(juridica);
    }

    @Test
    @DisplayName("Debe normalizar razón social preservando mayúsculas")
    void testCreateThird_NormalizesSocialReasonPreservingCase() {
        // Arrange
        Third juridica = Third.builder()
                .entId(entId)
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .thirdTypes(Set.of(thirdType))
                .socialReason("  Empresa   Test   S.A.S  ")
                .idNumber(900123456L)
                .build();

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(juridica);
        when(typeIdLoaderService.loadCompleteTypeId(juridica)).thenReturn(juridica);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(juridica);
        when(idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdType.getThirdTypeId())).thenReturn(thirdType);
        when(thirdRepository.existThirdBy(juridica.getIdNumber(), entId)).thenReturn(false);
        when(thirdOutputPort.saveThird(any(Third.class))).thenReturn(juridica);

        // Act
        createThirdService.createThird(juridica, null, null, null);

        // Assert
        verify(thirdOutputPort).saveThird(argThat(t ->
                t.getSocialReason() != null && !t.getSocialReason().startsWith(" ")
        ));
    }

    // ========== createThird Tests - Validación TypeId Inactivo ==========

    @Test
    @DisplayName("Debe lanzar excepción cuando TypeId está inactivo")
    void testCreateThird_ThrowsExceptionWhenTypeIdInactive() {
        // Arrange
        typeId.setStatus(false);

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);

        // Act & Assert
        TypeIdInvalidDataException exception = assertThrows(TypeIdInvalidDataException.class, () -> {
            createThirdService.createThird(third, countryCode, stateCode, cityCode);
        });

        assertEquals("El tipo de identificación seleccionado está inactivo", exception.getMessage());
        verify(thirdOutputPort, never()).saveThird(any(Third.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando TypeId status es null")
    void testCreateThird_ThrowsExceptionWhenTypeIdStatusNull() {
        // Arrange
        typeId.setStatus(null);

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);

        // Act & Assert
        assertThrows(TypeIdInvalidDataException.class, () -> {
            createThirdService.createThird(third, countryCode, stateCode, cityCode);
        });
    }

    // ========== createThird Tests - Validación ThirdTypes ==========

    @Test
    @DisplayName("Debe lanzar excepción cuando ThirdTypes está vacío")
    void testCreateThird_ThrowsExceptionWhenThirdTypesEmpty() {
        // Arrange
        third.setThirdTypes(Set.of());

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);

        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(ThirdInvalidDataException.class, () -> {
            createThirdService.createThird(third, countryCode, stateCode, cityCode);
        });

        assertEquals("Los tipos de tercero no pueden estar vacíos", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando ThirdTypes es null")
    void testCreateThird_ThrowsExceptionWhenThirdTypesNull() {
        // Arrange
        third.setThirdTypes(null);

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);

        // Act & Assert
        assertThrows(ThirdInvalidDataException.class, () -> {
            createThirdService.createThird(third, countryCode, stateCode, cityCode);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando ThirdType ID es null")
    void testCreateThird_ThrowsExceptionWhenThirdTypeIdNull() {
        // Arrange
        ThirdType invalidThirdType = ThirdType.builder()
                .thirdTypeId(null)
                .thirdTypeName("Cliente")
                .build();
        third.setThirdTypes(Set.of(invalidThirdType));

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);

        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(ThirdInvalidDataException.class, () -> {
            createThirdService.createThird(third, countryCode, stateCode, cityCode);
        });

        assertEquals("El ID del tipo de tercero no puede ser null", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando ThirdType no existe")
    void testCreateThird_ThrowsExceptionWhenThirdTypeNotExists() {
        // Arrange
        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        when(idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())).thenReturn(false);

        // Act & Assert
        assertThrows(ThirdTypeForeignKeyViolationException.class, () -> {
            createThirdService.createThird(third, countryCode, stateCode, cityCode);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando ThirdType está inactivo")
    void testCreateThird_ThrowsExceptionWhenThirdTypeInactive() {
        // Arrange
        ThirdType inactiveThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .status(false)
                .build();

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        when(idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdType.getThirdTypeId())).thenReturn(inactiveThirdType);

        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(ThirdInvalidDataException.class, () -> {
            createThirdService.createThird(third, countryCode, stateCode, cityCode);
        });

        assertTrue(exception.getMessage().contains("inactivo"));
    }

    @Test
    @DisplayName("Debe validar múltiples ThirdTypes correctamente")
    void testCreateThird_ValidatesMultipleThirdTypes() {
        // Arrange
        ThirdType thirdType2 = ThirdType.builder()
                .thirdTypeId(2L)
                .thirdTypeName("Proveedor")
                .status(true)
                .build();

        third.setThirdTypes(Set.of(thirdType, thirdType2));

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.existsThirdTypeById(2L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(idOutputPort.getThirdTypeById(2L)).thenReturn(thirdType2);
        when(thirdRepository.existThirdBy(third.getIdNumber(), entId)).thenReturn(false);
        when(thirdOutputPort.saveThird(any(Third.class))).thenReturn(third);

        // Act
        Third result = createThirdService.createThird(third, null, null, null);

        // Assert
        assertNotNull(result);
        verify(idOutputPort, times(2)).existsThirdTypeById(anyLong());
        verify(idOutputPort, times(2)).getThirdTypeById(anyLong());
    }

    // ========== createThird Tests - Validación Duplicados ==========

    @Test
    @DisplayName("Debe lanzar excepción cuando tercero ya existe")
    void testCreateThird_ThrowsExceptionWhenThirdAlreadyExists() {
        // Arrange
        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        when(idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdType.getThirdTypeId())).thenReturn(thirdType);
        when(thirdRepository.existThirdBy(third.getIdNumber(), entId)).thenReturn(true);

        // Act & Assert
        assertThrows(ThirdAlreadyExistsException.class, () -> {
            createThirdService.createThird(third, null, null, null);
        });

        verify(thirdOutputPort, never()).saveThird(any(Third.class));
    }

    @Test
    @DisplayName("Debe validar duplicados solo para la misma entidad")
    void testCreateThird_ValidatesDuplicatesForSameEntity() {
        // Arrange
        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        when(idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdType.getThirdTypeId())).thenReturn(thirdType);
        when(thirdRepository.existThirdBy(third.getIdNumber(), entId)).thenReturn(false);
        when(thirdOutputPort.saveThird(any(Third.class))).thenReturn(third);

        // Act
        createThirdService.createThird(third, null, null, null);

        // Assert
        verify(thirdRepository).existThirdBy(third.getIdNumber(), entId);
    }

    // ========== createThird Tests - Estado por Defecto ==========

    @Test
    @DisplayName("Debe establecer estado true por defecto cuando es null")
    void testCreateThird_SetsDefaultStateTrue() {
        // Arrange
        third.setState(null);

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        when(idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdType.getThirdTypeId())).thenReturn(thirdType);
        when(thirdRepository.existThirdBy(third.getIdNumber(), entId)).thenReturn(false);
        when(thirdOutputPort.saveThird(any(Third.class))).thenReturn(third);

        // Act
        createThirdService.createThird(third, null, null, null);

        // Assert
        verify(thirdOutputPort).saveThird(argThat(t -> Boolean.TRUE.equals(t.getState())));
    }

    @Test
    @DisplayName("Debe mantener estado false cuando se proporciona")
    void testCreateThird_MaintainsProvidedStateFalse() {
        // Arrange
        third.setState(false);

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        when(idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdType.getThirdTypeId())).thenReturn(thirdType);
        when(thirdRepository.existThirdBy(third.getIdNumber(), entId)).thenReturn(false);
        when(thirdOutputPort.saveThird(any(Third.class))).thenReturn(third);

        // Act
        createThirdService.createThird(third, null, null, null);

        // Assert
        verify(thirdOutputPort).saveThird(argThat(t -> Boolean.FALSE.equals(t.getState())));
    }

    // ========== prepareThirdForBatchSave Tests - Preparación Exitosa ==========

    @Test
    @DisplayName("Debe preparar tercero para guardado en lote sin acceder BD")
    void testPrepareThirdForBatchSave_PreparesWithoutDbAccess() {
        // Arrange
        third.setCountry(country);
        third.setProvince(state);
        third.setCity(city);

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);

        // Act
        Third result = createThirdService.prepareThirdForBatchSave(third, countryCode, stateCode, cityCode);

        // Assert
        assertNotNull(result);
        verify(thirdValidationService).validatePersonTypeConsistency(third);
        verify(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        verify(typeIdLoaderService, never()).loadCompleteTypeId(any());
        verify(thirdRepository, never()).existThirdBy(anyLong(), anyString());
        verify(thirdOutputPort, never()).saveThird(any());
    }

    @Test
    @DisplayName("Debe normalizar nombres en preparación de lote")
    void testPrepareThirdForBatchSave_NormalizesNames() {
        // Arrange
        third.setNames("  Carlos   Alberto  ");
        third.setLastNames("  López   Martínez  ");
        third.setCountry(country);
        third.setProvince(state);
        third.setCity(city);

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);

        // Act
        Third result = createThirdService.prepareThirdForBatchSave(third, countryCode, stateCode, cityCode);

        // Assert
        assertNotNull(result.getNames());
        assertNotNull(result.getLastNames());
        assertFalse(result.getNames().startsWith(" "));
        assertFalse(result.getLastNames().startsWith(" "));
    }

    @Test
    @DisplayName("Debe usar geografía ya cargada en lote")
    void testPrepareThirdForBatchSave_UsesPreloadedGeography() {
        // Arrange
        third.setCountry(country);
        third.setProvince(state);
        third.setCity(city);

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);

        // Act
        Third result = createThirdService.prepareThirdForBatchSave(third, countryCode, stateCode, cityCode);

        // Assert
        assertEquals(country, result.getCountry());
        assertEquals(state, result.getProvince());
        assertEquals(city, result.getCity());
        verify(geographyValidationService, never()).validateAndGetGeography(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando TypeId inactivo en lote")
    void testPrepareThirdForBatchSave_ThrowsExceptionWhenTypeIdInactive() {
        // Arrange
        typeId.setStatus(false);

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);

        // Act & Assert
        assertThrows(TypeIdInvalidDataException.class, () -> {
            createThirdService.prepareThirdForBatchSave(third, countryCode, stateCode, cityCode);
        });
    }

    @Test
    @DisplayName("Debe validar formato NIT en preparación de lote")
    void testPrepareThirdForBatchSave_ValidatesNitFormat() {
        // Arrange
        Third juridica = Third.builder()
                .entId(entId)
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .socialReason("Empresa Test")
                .idNumber(900123456L)
                .build();

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(juridica);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(juridica);
        doNothing().when(thirdValidationService).validateNitFormat(juridica);
        doNothing().when(thirdValidationService).validateVerificationDigit(juridica);

        // Act
        createThirdService.prepareThirdForBatchSave(juridica, null, null, null);

        // Assert
        verify(thirdValidationService).validateNitFormat(juridica);
    }

    @Test
    @DisplayName("Debe validar dígito de verificación en preparación de lote")
    void testPrepareThirdForBatchSave_ValidatesVerificationDigit() {
        // Arrange
        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        doNothing().when(thirdValidationService).validateVerificationDigit(third);

        // Act
        createThirdService.prepareThirdForBatchSave(third, null, null, null);

        // Assert
        verify(thirdValidationService).validateVerificationDigit(third);
    }

    @Test
    @DisplayName("Debe establecer estado true por defecto en lote")
    void testPrepareThirdForBatchSave_SetsDefaultStateTrue() {
        // Arrange
        third.setState(null);

        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);

        // Act
        Third result = createThirdService.prepareThirdForBatchSave(third, null, null, null);

        // Assert
        assertTrue(result.getState());
    }

    // ========== Integration Scenario Tests ==========

    @Test
    @DisplayName("Debe ejecutar todas las validaciones en orden correcto")
    void testCreateThird_ExecutesValidationsInOrder() {
        // Arrange
        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        doNothing().when(thirdValidationService).validateNitFormat(third);
        doNothing().when(thirdValidationService).validateVerificationDigit(third);
        when(idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdType.getThirdTypeId())).thenReturn(thirdType);
        when(thirdRepository.existThirdBy(third.getIdNumber(), entId)).thenReturn(false);
        when(thirdOutputPort.saveThird(any(Third.class))).thenReturn(third);

        // Act
        createThirdService.createThird(third, null, null, null);

        // Assert
        var inOrder = inOrder(thirdValidationService, typeIdLoaderService, idOutputPort, thirdRepository, thirdOutputPort);
        inOrder.verify(thirdValidationService).validatePersonTypeConsistency(third);
        inOrder.verify(typeIdLoaderService).loadCompleteTypeId(third);
        inOrder.verify(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        inOrder.verify(thirdValidationService).validateNitFormat(third);
        inOrder.verify(thirdValidationService).validateVerificationDigit(third);
        inOrder.verify(thirdRepository).existThirdBy(third.getIdNumber(), entId);
        inOrder.verify(thirdOutputPort).saveThird(any(Third.class));
    }

    @Test
    @DisplayName("Debe propagar excepción de validación de consistencia")
    void testCreateThird_PropagatesPersonTypeConsistencyException() {
        // Arrange
        doThrow(new ThirdInvalidDataException("Error de consistencia"))
                .when(thirdValidationService).validatePersonTypeConsistency(third);

        // Act & Assert
        assertThrows(ThirdInvalidDataException.class, () -> {
            createThirdService.createThird(third, countryCode, stateCode, cityCode);
        });

        verify(thirdOutputPort, never()).saveThird(any(Third.class));
    }

    @Test
    @DisplayName("Debe crear tercero con datos completos")
    void testCreateThird_WithCompleteData() {
        // Arrange
        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        when(idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdType.getThirdTypeId())).thenReturn(thirdType);
        when(geographyValidationService.validateAndGetGeography(countryCode, stateCode, cityCode))
                .thenReturn(new Object[]{country, state, city});
        when(thirdRepository.existThirdBy(third.getIdNumber(), entId)).thenReturn(false);
        when(thirdOutputPort.saveThird(any(Third.class))).thenReturn(third);

        // Act
        Third result = createThirdService.createThird(third, countryCode, stateCode, cityCode);

        // Assert
        assertNotNull(result);
        assertEquals(third.getEntId(), result.getEntId());
        assertEquals(third.getIdNumber(), result.getIdNumber());
    }

    @Test
    @DisplayName("Debe preservar todos los campos del tercero al guardar")
    void testCreateThird_PreservesAllFields() {
        // Arrange
        doNothing().when(thirdValidationService).validatePersonTypeConsistency(third);
        when(typeIdLoaderService.loadCompleteTypeId(third)).thenReturn(third);
        doNothing().when(thirdValidationService).validateTypeIdPersonTypeCompatibility(third);
        when(idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdType.getThirdTypeId())).thenReturn(thirdType);
        when(thirdRepository.existThirdBy(third.getIdNumber(), entId)).thenReturn(false);
        when(thirdOutputPort.saveThird(any(Third.class))).thenReturn(third);

        // Act
        createThirdService.createThird(third, null, null, null);

        // Assert
        verify(thirdOutputPort).saveThird(argThat(t ->
                t.getEntId().equals(entId) &&
                        t.getPersonType() == ePersonType.Natural &&
                        t.getIdNumber().equals(123456789L) &&
                        t.getAddress().equals("Calle 123") &&
                        t.getPhoneNumber().equals("3001234567") &&
                        t.getEmail().equals("juan@email.com")
        ));
    }
}
