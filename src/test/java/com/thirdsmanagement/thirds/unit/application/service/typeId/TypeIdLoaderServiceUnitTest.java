package com.thirdsmanagement.thirds.unit.application.service.typeId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.thirdsmanagement.thirds.application.service.typeId.TypeIdLoaderService;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdForeignKeyViolationException;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.PersonClassification;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TypeIdLoaderServiceUnitTest {

    @Mock
    private IdOutputPort idOutputPort;

    @InjectMocks
    private TypeIdLoaderService typeIdLoaderService;

    private Third third;
    private TypeId completeTypeId;
    private TypeId incompleteTypeId;
    private String entId;

    @BeforeEach
    void setUp() {
        entId = "ENT001";

        completeTypeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        incompleteTypeId = TypeId.builder()
                .id(1L)
                .build();

        third = Third.builder()
                .thId(100L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(incompleteTypeId)
                .names("Juan")
                .lastNames("Pérez")
                .gender(eThirdGender.Masculino)
                .idNumber(123456789L)
                .state(true)
                .address("Calle 123")
                .phoneNumber("1234567")
                .email("juan@test.com")
                .build();
    }

    // ==================== loadCompleteTypeId ====================

    @Test
    @DisplayName("Debe cargar tipo de identificación completo cuando tipo de identificación está incompleto")
    void testLoadCompleteTypeIdTypeIdIncompletoCargaTypeIdCompleto() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(completeTypeId);

        // Act
        Third result = typeIdLoaderService.loadCompleteTypeId(third);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getTypeId());
        assertEquals("CC", result.getTypeId().getTypeId());
        assertEquals("Cédula de Ciudadanía", result.getTypeId().getTypeIdname());
        verify(idOutputPort).getTypeIdById(1L);
    }

    @Test
    @DisplayName("Debe no recargar cuando tipo de identificación ya está completo")
    void testLoadCompleteTypeIdTypeIdYaCompletoNoRecarga() {
        // Arrange
        Third thirdWithCompleteTypeId = Third.builder()
                .thId(100L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(completeTypeId)
                .names("Juan")
                .lastNames("Pérez")
                .gender(eThirdGender.Masculino)
                .idNumber(123456789L)
                .state(true)
                .build();

        // Act
        Third result = typeIdLoaderService.loadCompleteTypeId(thirdWithCompleteTypeId);

        // Assert
        assertNotNull(result);
        assertEquals("CC", result.getTypeId().getTypeId());
        verify(idOutputPort, never()).getTypeIdById(anyLong());
    }

    @Test
    @DisplayName("Debe retornar tercero sin modificar cuando tipo de identificación es nulo")
    void testLoadCompleteTypeIdTypeIdNuloRetornaTerceroSinModificar() {
        // Arrange
        Third thirdWithoutTypeId = Third.builder()
                .thId(100L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(null)
                .names("Juan")
                .lastNames("Pérez")
                .build();

        // Act
        Third result = typeIdLoaderService.loadCompleteTypeId(thirdWithoutTypeId);

        // Assert
        assertNotNull(result);
        assertNull(result.getTypeId());
        verify(idOutputPort, never()).getTypeIdById(anyLong());
    }

    @Test
    @DisplayName("Debe retornar tercero sin modificar cuando tipo de identificación no tiene ID")
    void testLoadCompleteTypeIdTypeIdSinIdRetornaTerceroSinModificar() {
        // Arrange
        TypeId typeIdWithoutId = TypeId.builder()
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .build();

        Third thirdWithTypeIdWithoutId = Third.builder()
                .thId(100L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeIdWithoutId)
                .names("Juan")
                .lastNames("Pérez")
                .build();

        // Act
        Third result = typeIdLoaderService.loadCompleteTypeId(thirdWithTypeIdWithoutId);

        // Assert
        assertNotNull(result);
        assertNull(result.getTypeId().getId());
        verify(idOutputPort, never()).getTypeIdById(anyLong());
    }

    @Test
    @DisplayName("Debe cargar tipo de identificación completo cuando código está vacío")
    void testLoadCompleteTypeIdTypeIdConCodigoVacioCargaTypeIdCompleto() {
        // Arrange
        TypeId typeIdWithEmptyCode = TypeId.builder()
                .id(1L)
                .typeId("")
                .build();

        Third thirdWithEmptyCode = Third.builder()
                .thId(100L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeIdWithEmptyCode)
                .names("Juan")
                .lastNames("Pérez")
                .build();

        when(idOutputPort.getTypeIdById(1L)).thenReturn(completeTypeId);

        // Act
        Third result = typeIdLoaderService.loadCompleteTypeId(thirdWithEmptyCode);

        // Assert
        assertEquals("CC", result.getTypeId().getTypeId());
        verify(idOutputPort).getTypeIdById(1L);
    }

    @Test
    @DisplayName("Debe cargar tipo de identificación completo cuando código tiene solo espacios")
    void testLoadCompleteTypeIdTypeIdConCodigoSoloEspaciosCargaTypeIdCompleto() {
        // Arrange
        TypeId typeIdWithSpaces = TypeId.builder()
                .id(1L)
                .typeId("   ")
                .build();

        Third thirdWithSpaces = Third.builder()
                .thId(100L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeIdWithSpaces)
                .names("Juan")
                .lastNames("Pérez")
                .build();

        when(idOutputPort.getTypeIdById(1L)).thenReturn(completeTypeId);

        // Act
        Third result = typeIdLoaderService.loadCompleteTypeId(thirdWithSpaces);

        // Assert
        assertEquals("CC", result.getTypeId().getTypeId());
        verify(idOutputPort).getTypeIdById(1L);
    }

    @Test
    @DisplayName("Debe lanzar TypeIdForeignKeyViolationException cuando tipo de identificación no existe")
    void testLoadCompleteTypeIdTypeIdNoExisteLanzaTypeIdForeignKeyViolationException() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(null);

        // Act & Assert
        TypeIdForeignKeyViolationException exception = assertThrows(
                TypeIdForeignKeyViolationException.class,
                () -> typeIdLoaderService.loadCompleteTypeId(third));

        assertTrue(exception.getMessage().contains("1"));
        verify(idOutputPort).getTypeIdById(1L);
    }

    @Test
    @DisplayName("Debe preservar datos de tercero al cargar tipo de identificación")
    void testLoadCompleteTypeIdPreservaDatosTerceroAlCargarTypeId() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(completeTypeId);

        // Act
        Third result = typeIdLoaderService.loadCompleteTypeId(third);

        // Assert
        assertEquals(third.getThId(), result.getThId());
        assertEquals(third.getEntId(), result.getEntId());
        assertEquals(third.getPersonType(), result.getPersonType());
        assertEquals(third.getNames(), result.getNames());
        assertEquals(third.getLastNames(), result.getLastNames());
        assertEquals(third.getGender(), result.getGender());
        assertEquals(third.getIdNumber(), result.getIdNumber());
        assertEquals(third.getState(), result.getState());
        assertEquals(third.getAddress(), result.getAddress());
        assertEquals(third.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(third.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("Debe preservar todos los campos cuando tercero tiene todos los campos")
    void testLoadCompleteTypeIdTerceroConTodosLosCamposPreservaTodos() {
        // Arrange
        Country country = Country.builder().countryCode("COL").countryName("Colombia").build();
        State province = State.builder().stateCode("CAU").stateName("Cauca").build();
        City city = City.builder().cityCode("POP").cityName("Popayán").build();

        Third fullThird = Third.builder()
                .thId(100L)
                .entId(entId)
                .personType(ePersonType.Juridica)
                .typeId(incompleteTypeId)
                .names("María")
                .lastNames("González")
                .socialReason("Empresa S.A.")
                .gender(eThirdGender.Femenino)
                .idNumber(987654321L)
                .verificationNumber(1L)
                .state(true)
                .address("Carrera 45")
                .phoneNumber("3001234567")
                .email("maria@empresa.com")
                .country(country)
                .province(province)
                .city(city)
                .build();

        when(idOutputPort.getTypeIdById(1L)).thenReturn(completeTypeId);

        // Act
        Third result = typeIdLoaderService.loadCompleteTypeId(fullThird);

        // Assert
        assertEquals(fullThird.getThId(), result.getThId());
        assertEquals(fullThird.getSocialReason(), result.getSocialReason());
        assertEquals(fullThird.getVerificationNumber(), result.getVerificationNumber());
        assertEquals(fullThird.getCountry(), result.getCountry());
        assertEquals(fullThird.getProvince(), result.getProvince());
        assertEquals(fullThird.getCity(), result.getCity());
    }

    // ==================== existsTypeId ====================

    @Test
    @DisplayName("Debe retornar true cuando ID es válido")
    void testExistsTypeIdIdValidoRetornaTrue() {
        // Arrange
        when(idOutputPort.existsTypeIdById(1L)).thenReturn(true);

        // Act
        boolean result = typeIdLoaderService.existsTypeId(1L);

        // Assert
        assertTrue(result);
        verify(idOutputPort).existsTypeIdById(1L);
    }

    @Test
    @DisplayName("Debe retornar false cuando ID no existe")
    void testExistsTypeIdIdNoExisteRetornaFalse() {
        // Arrange
        when(idOutputPort.existsTypeIdById(999L)).thenReturn(false);

        // Act
        boolean result = typeIdLoaderService.existsTypeId(999L);

        // Assert
        assertFalse(result);
        verify(idOutputPort).existsTypeIdById(999L);
    }

    @Test
    @DisplayName("Debe retornar false cuando ID es nulo")
    void testExistsTypeIdIdNuloRetornaFalse() {
        // Act
        boolean result = typeIdLoaderService.existsTypeId(null);

        // Assert
        assertFalse(result);
        verify(idOutputPort, never()).existsTypeIdById(anyLong());
    }

    @Test
    @DisplayName("Debe validar correctamente cuando ID es cero")
    void testExistsTypeIdIdCeroValidaCorrectamente() {
        // Arrange
        when(idOutputPort.existsTypeIdById(0L)).thenReturn(false);

        // Act
        boolean result = typeIdLoaderService.existsTypeId(0L);

        // Assert
        assertFalse(result);
        verify(idOutputPort).existsTypeIdById(0L);
    }

    @Test
    @DisplayName("Debe validar cada uno de diferentes IDs")
    void testExistsTypeIdDiferentesIdsValidaCadaUno() {
        // Arrange
        when(idOutputPort.existsTypeIdById(1L)).thenReturn(true);
        when(idOutputPort.existsTypeIdById(2L)).thenReturn(true);
        when(idOutputPort.existsTypeIdById(3L)).thenReturn(false);

        // Act
        boolean result1 = typeIdLoaderService.existsTypeId(1L);
        boolean result2 = typeIdLoaderService.existsTypeId(2L);
        boolean result3 = typeIdLoaderService.existsTypeId(3L);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        assertFalse(result3);
        verify(idOutputPort).existsTypeIdById(1L);
        verify(idOutputPort).existsTypeIdById(2L);
        verify(idOutputPort).existsTypeIdById(3L);
    }

    // ==================== Propagación de excepciones ====================

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en getTypeIdById")
    void testLoadCompleteTypeIdErrorEnGetTypeIdByIdPropagaExcepcion() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenThrow(new RuntimeException("Error de base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> typeIdLoaderService.loadCompleteTypeId(third));
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en existsTypeIdById")
    void testExistsTypeIdErrorEnExistsTypeIdByIdPropagaExcepcion() {
        // Arrange
        when(idOutputPort.existsTypeIdById(1L)).thenThrow(new RuntimeException("Error de conexión"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> typeIdLoaderService.existsTypeId(1L));
    }

    // ==================== Delegación correcta ====================

    @Test
    @DisplayName("Debe delegar correctamente al puerto para cargar")
    void testLoadCompleteTypeIdDelegaAlPuertoParaCargarCorrectamente() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(completeTypeId);

        // Act
        typeIdLoaderService.loadCompleteTypeId(third);

        // Assert
        verify(idOutputPort, times(1)).getTypeIdById(1L);
    }

    @Test
    @DisplayName("Debe delegar correctamente al puerto para validar")
    void testExistsTypeIdDelegaAlPuertoParaValidarCorrectamente() {
        // Arrange
        when(idOutputPort.existsTypeIdById(1L)).thenReturn(true);

        // Act
        typeIdLoaderService.existsTypeId(1L);

        // Assert
        verify(idOutputPort, times(1)).existsTypeIdById(1L);
    }

    // ==================== Integración ====================

    @Test
    @DisplayName("Debe funcionar correctamente en proceso completo de validación y carga")
    void testTypeIdLoaderServiceProcesoCompletoValidacionYCargaFuncionaCorrectamente() {
        // Arrange
        when(idOutputPort.existsTypeIdById(1L)).thenReturn(true);
        when(idOutputPort.getTypeIdById(1L)).thenReturn(completeTypeId);

        // Act
        boolean exists = typeIdLoaderService.existsTypeId(1L);
        Third result = typeIdLoaderService.loadCompleteTypeId(third);

        // Assert
        assertTrue(exists);
        assertNotNull(result);
        assertEquals("CC", result.getTypeId().getTypeId());
        assertEquals("Cédula de Ciudadanía", result.getTypeId().getTypeIdname());
    }

    @Test
    @DisplayName("Debe cargar correctamente cada uno de varios intentos con diferentes terceros")
    void testLoadCompleteTypeIdVariosIntentosConDiferentesTercerosCargaCorrectamenteCadaUno() {
        // Arrange
        TypeId typeId2 = TypeId.builder()
                .id(2L)
                .typeId("NIT")
                .typeIdname("NIT")
                .entId(entId)
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        TypeId incompleteTypeId2 = TypeId.builder().id(2L).build();

        Third third2 = Third.builder()
                .thId(200L)
                .entId(entId)
                .personType(ePersonType.Juridica)
                .typeId(incompleteTypeId2)
                .socialReason("Empresa XYZ")
                .idNumber(900123456L)
                .build();

        when(idOutputPort.getTypeIdById(1L)).thenReturn(completeTypeId);
        when(idOutputPort.getTypeIdById(2L)).thenReturn(typeId2);

        // Act
        Third result1 = typeIdLoaderService.loadCompleteTypeId(third);
        Third result2 = typeIdLoaderService.loadCompleteTypeId(third2);

        // Assert
        assertEquals("CC", result1.getTypeId().getTypeId());
        assertEquals("NIT", result2.getTypeId().getTypeId());
        verify(idOutputPort).getTypeIdById(1L);
        verify(idOutputPort).getTypeIdById(2L);
    }

    @Test
    @DisplayName("Debe no recargar desde base de datos cuando tipo de identificación tiene nombre completo")
    void testLoadCompleteTypeIdTypeIdConNombreCompletoNoRecargaDesdeBaseDatos() {
        // Arrange
        TypeId completeTypeIdWithName = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .build();

        Third thirdWithCompleteName = Third.builder()
                .thId(100L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(completeTypeIdWithName)
                .names("Juan")
                .lastNames("Pérez")
                .build();

        // Act
        Third result = typeIdLoaderService.loadCompleteTypeId(thirdWithCompleteName);

        // Assert
        assertNotNull(result);
        assertEquals("CC", result.getTypeId().getTypeId());
        assertEquals("Cédula de Ciudadanía", result.getTypeId().getTypeIdname());
        verify(idOutputPort, never()).getTypeIdById(anyLong());
    }

    @Test
    @DisplayName("Debe preservar tipos de tercero cuando tercero tiene tipos de tercero")
    void testLoadCompleteTypeIdTerceroConThirdTypesPreservaThirdTypes() {
        // Arrange
        ThirdType thirdType1 = ThirdType.builder().thirdTypeId(1L).thirdTypeName("Cliente").build();
        ThirdType thirdType2 = ThirdType.builder().thirdTypeId(2L).thirdTypeName("Proveedor").build();

        Third thirdWithTypes = Third.builder()
                .thId(100L)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(incompleteTypeId)
                .names("Juan")
                .lastNames("Pérez")
                .thirdTypes(Set.of(thirdType1, thirdType2))
                .build();

        when(idOutputPort.getTypeIdById(1L)).thenReturn(completeTypeId);

        // Act
        Third result = typeIdLoaderService.loadCompleteTypeId(thirdWithTypes);

        // Assert
        assertNotNull(result.getThirdTypes());
        assertEquals(2, result.getThirdTypes().size());
    }
}
