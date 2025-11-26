package com.thirdsmanagement.thirds.unit.application.service.third;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
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
import com.thirdsmanagement.thirds.application.service.third.ThirdGeographyValidationService;
import com.thirdsmanagement.thirds.application.service.third.ThirdValidationService;
import com.thirdsmanagement.thirds.application.service.third.UpdateThirdService;
import com.thirdsmanagement.thirds.application.service.typeId.TypeIdLoaderService;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdAlreadyExistsException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdInUseException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdNotFound;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeForeignKeyViolationException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdForeignKeyViolationException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdInvalidDataException;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.PersonClassification;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateThirdServiceUnitTest {

    @Mock
    private ThirdOutputPort thirdOutputPort;

    @Mock
    private ThirdGeographyValidationService geographyValidationService;

    @Mock
    private ThirdValidationService thirdValidationService;

    @Mock
    private TypeIdLoaderService typeIdLoaderService;

    @Mock
    private IdOutputPort idOutputPort;

    @Mock
    private ThirdRepository thirdRepository;

    @InjectMocks
    private UpdateThirdService updateThirdService;

    private Third existingThird;
    private Third updatedThird;
    private TypeId typeId;
    private ThirdType thirdType;
    private Country country;
    private State state;
    private City city;
    private String entId;

    @BeforeEach
    void setUp() {
        entId = "ENT001";

        typeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .status(true)
                .classification(PersonClassification.NATURAL_PERSON)
                .build();

        thirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .status(true)
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

        existingThird = Third.builder()
                .thId(1L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .thirdTypes(Set.of(thirdType))
                .names("Juan")
                .lastNames("Pérez")
                .gender(eThirdGender.Masculino)
                .idNumber(1234567890L)
                .state(true)
                .usageCount(0)
                .build();

        updatedThird = Third.builder()
                .thId(1L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .thirdTypes(Set.of(thirdType))
                .names("Juan Carlos")
                .lastNames("Pérez García")
                .gender(eThirdGender.Masculino)
                .idNumber(1234567890L)
                .state(true)
                .build();
    }

    // ==================== Actualización exitosa ====================

    @Test
    @DisplayName("Debe actualizar correctamente tercero válido sin cambio de número de identificación")
    void testUpdateThirdWithGeographyTerceroValidoSinCambioIdNumberActualizaCorrectamente() {
        // Arrange
        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(updatedThird);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(geographyValidationService.validateAndGetGeography("CO", "05", "05001"))
                .thenReturn(new Object[]{country, state, city});
        when(thirdOutputPort.updateThird(any(Third.class))).thenReturn(updatedThird);

        // Act
        Third result = updateThirdService.updateThirdWithGeography(updatedThird, "CO", "05", "05001");

        // Assert
        assertNotNull(result);
        verify(thirdOutputPort).existThirdById(1L, entId);
        verify(thirdOutputPort, times(2)).getThirdById(1L, entId);
        verify(thirdValidationService).validatePersonTypeConsistency(updatedThird);
        verify(typeIdLoaderService).loadCompleteTypeId(any(Third.class));
        verify(thirdValidationService).validateTypeIdPersonTypeCompatibility(updatedThird);
        verify(thirdValidationService).validateNitFormat(updatedThird);
        verify(thirdValidationService).validateVerificationDigit(updatedThird);
        verify(geographyValidationService).validateAndGetGeography("CO", "05", "05001");
        verify(thirdOutputPort).updateThird(any(Third.class));
    }

    @Test
    @DisplayName("Debe actualizar solo tercero cuando no tiene geografía")
    void testUpdateThirdWithGeographySinGeografiaActualizaSoloTercero() {
        // Arrange
        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(updatedThird);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(thirdOutputPort.updateThird(any(Third.class))).thenReturn(updatedThird);

        // Act
        Third result = updateThirdService.updateThirdWithGeography(updatedThird, null, null, null);

        // Assert
        assertNotNull(result);
        verify(geographyValidationService, never()).validateAndGetGeography(anyString(), anyString(), anyString());
        verify(thirdOutputPort).updateThird(any(Third.class));
    }

    @Test
    @DisplayName("Debe actualizar correctamente con cambio de número de identificación válido")
    void testUpdateThirdWithGeographyConCambioIdNumberValidoActualizaCorrectamente() {
        // Arrange
        Third thirdWithNewIdNumber = Third.builder()
                .thId(1L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .thirdTypes(Set.of(thirdType))
                .names("Juan Carlos")
                .lastNames("Pérez García")
                .idNumber(9876543210L)
                .state(true)
                .build();

        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(thirdWithNewIdNumber);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(thirdRepository.existThirdBy(9876543210L, entId)).thenReturn(false);
        when(thirdOutputPort.updateThird(any(Third.class))).thenReturn(thirdWithNewIdNumber);

        // Act
        Third result = updateThirdService.updateThirdWithGeography(thirdWithNewIdNumber, null, null, null);

        // Assert
        assertNotNull(result);
        verify(thirdRepository).existThirdBy(9876543210L, entId);
        verify(thirdOutputPort).updateThird(any(Third.class));
    }

    @Test
    @DisplayName("Debe actualizar correctamente persona jurídica")
    void testUpdateThirdWithGeographyPersonaJuridicaActualizaCorrectamente() {
        // Arrange
        TypeId nitTypeId = TypeId.builder()
                .id(2L)
                .typeId("NIT")
                .typeIdname("Número de Identificación Tributaria")
                .status(true)
                .classification(PersonClassification.LEGAL_ENTITY)
                .build();

        Third juridicaThird = Third.builder()
                .thId(2L)
                .entId(entId)
                .personType(ePersonType.Juridica)
                .typeId(nitTypeId)
                .thirdTypes(Set.of(thirdType))
                .socialReason("Empresa XYZ S.A.S.")
                .idNumber(800123456L)
                .verificationNumber(5L)
                .state(true)
                .usageCount(0)
                .build();

        when(thirdOutputPort.existThirdById(2L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(2L, entId)).thenReturn(Optional.of(juridicaThird));
        when(typeIdLoaderService.existsTypeId(2L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(juridicaThird);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(thirdOutputPort.updateThird(any(Third.class))).thenReturn(juridicaThird);

        // Act
        Third result = updateThirdService.updateThirdWithGeography(juridicaThird, null, null, null);

        // Assert
        assertNotNull(result);
        verify(thirdValidationService).validatePersonTypeConsistency(juridicaThird);
        verify(thirdValidationService).validateNitFormat(juridicaThird);
        verify(thirdValidationService).validateVerificationDigit(juridicaThird);
    }

    // ==================== Validación de tercero null ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando tercero es nulo")
    void testUpdateThirdWithGeographyTerceroNullLanzaIllegalArgumentException() {
        // Arrange - Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateThirdService.updateThirdWithGeography(null, null, null, null)
        );

        assertTrue(exception.getMessage().contains("tercero no puede ser null"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando ID de tercero es nulo")
    void testUpdateThirdWithGeographyIdTerceroNullLanzaIllegalArgumentException() {
        // Arrange
        Third thirdWithoutId = Third.builder()
                .entId(entId)
                .personType(ePersonType.Natural)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateThirdService.updateThirdWithGeography(thirdWithoutId, null, null, null)
        );

        assertTrue(exception.getMessage().contains("ID del tercero no puede ser null"));
    }

    // ==================== Validación de existencia ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando tercero no existe")
    void testUpdateThirdWithGeographyTerceroNoExisteLanzaThirdNotFound() {
        // Arrange
        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(false);

        // Act & Assert
        ThirdNotFound exception = assertThrows(
                ThirdNotFound.class,
                () -> updateThirdService.updateThirdWithGeography(updatedThird, null, null, null)
        );

        assertTrue(exception.getMessage().contains("no existe"));
        verify(thirdOutputPort).existThirdById(1L, entId);
        verify(thirdOutputPort, never()).updateThird(any(Third.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando tercero existe en verificación pero no en obtención")
    void testUpdateThirdWithGeographyTerceroExisteEnExistsPeroNoEnGetLanzaThirdNotFound() {
        // Arrange
        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.empty());

        // Act & Assert
        ThirdNotFound exception = assertThrows(
                ThirdNotFound.class,
                () -> updateThirdService.updateThirdWithGeography(updatedThird, null, null, null)
        );

        assertTrue(exception.getMessage().contains("no existe"));
        verify(thirdOutputPort).getThirdById(1L, entId);
    }

    // ==================== Validación de tercero en uso ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando tercero está en uso")
    void testUpdateThirdWithGeographyTerceroEnUsoLanzaThirdInUseException() {
        // Arrange
        Third thirdInUse = Third.builder()
                .thId(1L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .thirdTypes(Set.of(thirdType))
                .names("Juan")
                .lastNames("Pérez")
                .idNumber(1234567890L)
                .state(true)
                .usageCount(5)
                .build();

        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(thirdInUse));

        // Act & Assert
        ThirdInUseException exception = assertThrows(
                ThirdInUseException.class,
                () -> updateThirdService.updateThirdWithGeography(updatedThird, null, null, null)
        );

        assertNotNull(exception);
        verify(thirdOutputPort).getThirdById(1L, entId);
        verify(thirdOutputPort, never()).updateThird(any(Third.class));
    }

    @Test
    @DisplayName("Debe actualizar correctamente cuando contador de uso es nulo")
    void testUpdateThirdWithGeographyTerceroConUsageCountNullNoLanzaExcepcion() {
        // Arrange
        Third thirdWithNullUsage = Third.builder()
                .thId(1L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .thirdTypes(Set.of(thirdType))
                .names("Juan")
                .lastNames("Pérez")
                .idNumber(1234567890L)
                .state(true)
                .usageCount(null)
                .build();

        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(thirdWithNullUsage));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(updatedThird);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(thirdOutputPort.updateThird(any(Third.class))).thenReturn(updatedThird);

        // Act & Assert
        assertDoesNotThrow(() -> updateThirdService.updateThirdWithGeography(updatedThird, null, null, null));
    }

    // ==================== Validación de TypeId ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo de identificación es nulo")
    void testUpdateThirdWithGeographyTypeIdNullLanzaThirdInvalidDataException() {
        // Arrange
        Third thirdWithoutTypeId = Third.builder()
                .thId(1L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .thirdTypes(Set.of(thirdType))
                .names("Juan")
                .lastNames("Pérez")
                .idNumber(1234567890L)
                .build();

        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));

        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(
                ThirdInvalidDataException.class,
                () -> updateThirdService.updateThirdWithGeography(thirdWithoutTypeId, null, null, null)
        );

        assertTrue(exception.getMessage().contains("tipo de identificación"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo de identificación no existe")
    void testUpdateThirdWithGeographyTypeIdNoExisteLanzaTypeIdForeignKeyViolationException() {
        // Arrange
        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(false);

        // Act & Assert
        TypeIdForeignKeyViolationException exception = assertThrows(
                TypeIdForeignKeyViolationException.class,
                () -> updateThirdService.updateThirdWithGeography(updatedThird, null, null, null)
        );

        assertNotNull(exception);
        verify(typeIdLoaderService).existsTypeId(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo de identificación está inactivo")
    void testUpdateThirdWithGeographyTypeIdInactivoLanzaTypeIdInvalidDataException() {
        // Arrange
        TypeId inactiveTypeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .status(false)
                .classification(PersonClassification.NATURAL_PERSON)
                .build();

        Third thirdWithInactiveTypeId = Third.builder()
                .thId(1L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(inactiveTypeId)
                .thirdTypes(Set.of(thirdType))
                .names("Juan")
                .lastNames("Pérez")
                .idNumber(1234567890L)
                .build();

        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(thirdWithInactiveTypeId);

        // Act & Assert
        TypeIdInvalidDataException exception = assertThrows(
                TypeIdInvalidDataException.class,
                () -> updateThirdService.updateThirdWithGeography(thirdWithInactiveTypeId, null, null, null)
        );

        assertTrue(exception.getMessage().contains("activo"));
    }

    // ==================== Validación de ThirdTypes ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando tipos de tercero está vacío")
    void testUpdateThirdWithGeographyThirdTypesVacioLanzaThirdInvalidDataException() {
        // Arrange
        Third thirdWithoutTypes = Third.builder()
                .thId(1L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .thirdTypes(Set.of())
                .names("Juan")
                .lastNames("Pérez")
                .idNumber(1234567890L)
                .build();

        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(thirdWithoutTypes);

        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(
                ThirdInvalidDataException.class,
                () -> updateThirdService.updateThirdWithGeography(thirdWithoutTypes, null, null, null)
        );

        assertTrue(exception.getMessage().contains("tipos de tercero"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando ID de tipo de tercero es nulo")
    void testUpdateThirdWithGeographyThirdTypeIdNullLanzaThirdInvalidDataException() {
        // Arrange
        ThirdType thirdTypeWithoutId = ThirdType.builder()
                .thirdTypeName("Cliente")
                .build();

        Third thirdWithInvalidType = Third.builder()
                .thId(1L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .thirdTypes(Set.of(thirdTypeWithoutId))
                .names("Juan")
                .lastNames("Pérez")
                .idNumber(1234567890L)
                .build();

        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(thirdWithInvalidType);

        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(
                ThirdInvalidDataException.class,
                () -> updateThirdService.updateThirdWithGeography(thirdWithInvalidType, null, null, null)
        );

        assertTrue(exception.getMessage().contains("ID del tipo de tercero"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo de tercero no existe")
    void testUpdateThirdWithGeographyThirdTypeNoExisteLanzaThirdTypeForeignKeyViolationException() {
        // Arrange
        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(updatedThird);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(false);

        // Act & Assert
        ThirdTypeForeignKeyViolationException exception = assertThrows(
                ThirdTypeForeignKeyViolationException.class,
                () -> updateThirdService.updateThirdWithGeography(updatedThird, null, null, null)
        );

        assertNotNull(exception);
        verify(idOutputPort).existsThirdTypeById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo de tercero está inactivo")
    void testUpdateThirdWithGeographyThirdTypeInactivoLanzaThirdInvalidDataException() {
        // Arrange
        ThirdType inactiveThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .status(false)
                .build();

        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(updatedThird);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(inactiveThirdType);

        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(
                ThirdInvalidDataException.class,
                () -> updateThirdService.updateThirdWithGeography(updatedThird, null, null, null)
        );

        assertTrue(exception.getMessage().contains("inactivo"));
    }

    // ==================== Validación de duplicados ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando cambio de número de identificación resulta duplicado")
    void testUpdateThirdWithGeographyCambioIdNumberADuplicadoLanzaThirdAlreadyExistsException() {
        // Arrange
        Third thirdWithDuplicateIdNumber = Third.builder()
                .thId(1L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .thirdTypes(Set.of(thirdType))
                .names("Juan")
                .lastNames("Pérez")
                .idNumber(9876543210L)
                .build();

        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(thirdWithDuplicateIdNumber);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(thirdRepository.existThirdBy(9876543210L, entId)).thenReturn(true);

        // Act & Assert
        ThirdAlreadyExistsException exception = assertThrows(
                ThirdAlreadyExistsException.class,
                () -> updateThirdService.updateThirdWithGeography(thirdWithDuplicateIdNumber, null, null, null)
        );

        assertNotNull(exception);
        verify(thirdRepository).existThirdBy(9876543210L, entId);
    }

    // ==================== Propagación de validaciones ====================

    @Test
    @DisplayName("Debe propagar excepción cuando validación de consistencia de tipo de persona falla")
    void testUpdateThirdWithGeographyValidacionPersonTypeConsistencyFallaPropagaExcepcion() {
        // Arrange
        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        doThrow(new ThirdInvalidDataException("Error de consistencia"))
                .when(thirdValidationService).validatePersonTypeConsistency(any(Third.class));

        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(
                ThirdInvalidDataException.class,
                () -> updateThirdService.updateThirdWithGeography(updatedThird, null, null, null)
        );

        assertTrue(exception.getMessage().contains("consistencia"));
        verify(thirdValidationService).validatePersonTypeConsistency(updatedThird);
    }

    @Test
    @DisplayName("Debe propagar excepción cuando validación de geografía falla")
    void testUpdateThirdWithGeographyValidacionGeografiaFallaPropagaExcepcion() {
        // Arrange
        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(updatedThird);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(geographyValidationService.validateAndGetGeography("CO", "05", "05001"))
                .thenThrow(new RuntimeException("Error de geografía"));

        // Act & Assert
        assertThrows(
                RuntimeException.class,
                () -> updateThirdService.updateThirdWithGeography(updatedThird, "CO", "05", "05001")
        );

        verify(geographyValidationService).validateAndGetGeography("CO", "05", "05001");
    }

    // ==================== Normalización de datos ====================

    @Test
    @DisplayName("Debe normalizar nombres y apellidos correctamente")
    void testUpdateThirdWithGeographyNormalizaNombresYApellidosCorrectamente() {
        // Arrange
        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(updatedThird);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(thirdOutputPort.updateThird(any(Third.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Third result = updateThirdService.updateThirdWithGeography(updatedThird, null, null, null);

        // Assert
        assertNotNull(result);
        verify(thirdOutputPort).updateThird(argThat(third ->
                third.getNames() != null && third.getLastNames() != null
        ));
    }

    @Test
    @DisplayName("Debe normalizar razón social correctamente")
    void testUpdateThirdWithGeographyNormalizaRazonSocialCorrectamente() {
        // Arrange
        TypeId nitTypeId = TypeId.builder()
                .id(2L)
                .typeId("NIT")
                .status(true)
                .classification(PersonClassification.LEGAL_ENTITY)
                .build();

        Third juridicaThird = Third.builder()
                .thId(2L)
                .entId(entId)
                .personType(ePersonType.Juridica)
                .typeId(nitTypeId)
                .thirdTypes(Set.of(thirdType))
                .socialReason("  Empresa  XYZ  S.A.S.  ")
                .idNumber(800123456L)
                .verificationNumber(5L)
                .state(true)
                .usageCount(0)
                .build();

        when(thirdOutputPort.existThirdById(2L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(2L, entId)).thenReturn(Optional.of(juridicaThird));
        when(typeIdLoaderService.existsTypeId(2L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(juridicaThird);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(thirdOutputPort.updateThird(any(Third.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Third result = updateThirdService.updateThirdWithGeography(juridicaThird, null, null, null);

        // Assert
        assertNotNull(result);
        verify(thirdOutputPort).updateThird(argThat(third ->
                third.getSocialReason() != null
        ));
    }

    // ==================== Estado por defecto ====================

    @Test
    @DisplayName("Debe asignar estado true por defecto cuando estado es nulo")
    void testUpdateThirdWithGeographyStateNullAsignaTruePorDefecto() {
        // Arrange
        Third thirdWithNullState = Third.builder()
                .thId(1L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .thirdTypes(Set.of(thirdType))
                .names("Juan")
                .lastNames("Pérez")
                .idNumber(1234567890L)
                .state(null)
                .build();

        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(thirdWithNullState);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(thirdOutputPort.updateThird(any(Third.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Third result = updateThirdService.updateThirdWithGeography(thirdWithNullState, null, null, null);

        // Assert
        assertNotNull(result);
        verify(thirdOutputPort).updateThird(argThat(third -> Boolean.TRUE.equals(third.getState())));
    }

    @Test
    @DisplayName("Debe mantener estado false cuando se especifica")
    void testUpdateThirdWithGeographyStateFalseMantieneFalse() {
        // Arrange
        Third thirdWithFalseState = Third.builder()
                .thId(1L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .thirdTypes(Set.of(thirdType))
                .names("Juan")
                .lastNames("Pérez")
                .idNumber(1234567890L)
                .state(false)
                .build();

        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(thirdWithFalseState);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(thirdOutputPort.updateThird(any(Third.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Third result = updateThirdService.updateThirdWithGeography(thirdWithFalseState, null, null, null);

        // Assert
        assertNotNull(result);
        verify(thirdOutputPort).updateThird(argThat(third -> Boolean.FALSE.equals(third.getState())));
    }

    // ==================== Integración ====================

    @Test
    @DisplayName("Debe ejecutar validaciones en orden correcto")
    void testUpdateThirdWithGeographyOrdenDeValidacionesEjecutaEnOrdenCorrecto() {
        // Arrange
        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(updatedThird);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(thirdOutputPort.updateThird(any(Third.class))).thenReturn(updatedThird);

        var inOrder = inOrder(thirdOutputPort, thirdValidationService, typeIdLoaderService, idOutputPort);

        // Act
        updateThirdService.updateThirdWithGeography(updatedThird, null, null, null);

        // Assert
        inOrder.verify(thirdOutputPort).existThirdById(1L, entId);
        inOrder.verify(thirdOutputPort).getThirdById(1L, entId);
        inOrder.verify(thirdValidationService).validatePersonTypeConsistency(updatedThird);
        inOrder.verify(typeIdLoaderService).existsTypeId(1L);
        inOrder.verify(typeIdLoaderService).loadCompleteTypeId(any(Third.class));
        inOrder.verify(thirdValidationService).validateTypeIdPersonTypeCompatibility(updatedThird);
        inOrder.verify(thirdValidationService).validateNitFormat(updatedThird);
        inOrder.verify(thirdValidationService).validateVerificationDigit(updatedThird);
        inOrder.verify(idOutputPort).existsThirdTypeById(1L);
        inOrder.verify(thirdOutputPort).getThirdById(1L, entId);
        inOrder.verify(thirdOutputPort).updateThird(any(Third.class));
    }

    @Test
    @DisplayName("Debe actualizar exitosamente cuando todas las validaciones pasan")
    void testUpdateThirdWithGeographyTodasLasValidacionesPasanActualizaExitosamente() {
        // Arrange
        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(updatedThird);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(geographyValidationService.validateAndGetGeography("CO", "05", "05001"))
                .thenReturn(new Object[]{country, state, city});
        when(thirdOutputPort.updateThird(any(Third.class))).thenReturn(updatedThird);

        // Act
        Third result = updateThirdService.updateThirdWithGeography(updatedThird, "CO", "05", "05001");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getThId());
        verify(thirdValidationService).validatePersonTypeConsistency(updatedThird);
        verify(thirdValidationService).validateTypeIdPersonTypeCompatibility(updatedThird);
        verify(thirdValidationService).validateNitFormat(updatedThird);
        verify(thirdValidationService).validateVerificationDigit(updatedThird);
        verify(geographyValidationService).validateAndGetGeography("CO", "05", "05001");
        verify(thirdOutputPort).updateThird(any(Third.class));
    }

    @Test
    @DisplayName("Debe validar todos los tipos de tercero cuando hay múltiples")
    void testUpdateThirdWithGeographyMultiplesThirdTypesValidaTodos() {
        // Arrange
        ThirdType thirdType2 = ThirdType.builder()
                .thirdTypeId(2L)
                .thirdTypeName("Proveedor")
                .status(true)
                .build();

        Third thirdWithMultipleTypes = Third.builder()
                .thId(1L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .thirdTypes(Set.of(thirdType, thirdType2))
                .names("Juan")
                .lastNames("Pérez")
                .idNumber(1234567890L)
                .build();

        when(thirdOutputPort.existThirdById(1L, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(1L, entId)).thenReturn(Optional.of(existingThird));
        when(typeIdLoaderService.existsTypeId(1L)).thenReturn(true);
        when(typeIdLoaderService.loadCompleteTypeId(any(Third.class))).thenReturn(thirdWithMultipleTypes);
        when(idOutputPort.existsThirdTypeById(1L)).thenReturn(true);
        when(idOutputPort.existsThirdTypeById(2L)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(thirdType);
        when(idOutputPort.getThirdTypeById(2L)).thenReturn(thirdType2);
        when(thirdOutputPort.updateThird(any(Third.class))).thenReturn(thirdWithMultipleTypes);

        // Act
        Third result = updateThirdService.updateThirdWithGeography(thirdWithMultipleTypes, null, null, null);

        // Assert
        assertNotNull(result);
        verify(idOutputPort).existsThirdTypeById(1L);
        verify(idOutputPort).existsThirdTypeById(2L);
        verify(idOutputPort).getThirdTypeById(1L);
        verify(idOutputPort).getThirdTypeById(2L);
    }
}
