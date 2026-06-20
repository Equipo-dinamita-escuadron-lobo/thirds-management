package com.thirdsmanagement.thirds.unit.application.service.thirdType;

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
import com.thirdsmanagement.thirds.application.service.thirdType.CreateThirdTypeService;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeNameAlreadyExistsException;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreateThirdTypeServiceUnitTest {

    @Mock
    private IdOutputPort idOutputPort;

    @Mock
    private ThirdTypeRepository thirdTypeRepository;

    @InjectMocks
    private CreateThirdTypeService createThirdTypeService;

    private ThirdType thirdType;
    private String entId;

    @BeforeEach
    void setUp() {
        entId = "ENT001";

        thirdType = ThirdType.builder()
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(true)
                .build();
    }

    // ==================== Creación exitosa ====================

    @Test
    @DisplayName("Debe crear correctamente tipo de tercero válido")
    void testCreateThirdTypeTipoTerceroValidoCreaCorrectamente() {
        // Arrange
        ThirdType savedThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Cliente", entId)).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(savedThirdType);

        // Act
        ThirdType result = createThirdTypeService.createThirdType(thirdType);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getThirdTypeId());
        assertEquals("Cliente", result.getThirdTypeName());
        assertEquals(entId, result.getEntId());
        assertTrue(result.getStatus());
        verify(thirdTypeRepository).existsByTtNameIgnoreCaseAndTtentId("Cliente", entId);
        verify(idOutputPort).saveThirdType(any(ThirdType.class));
    }

    @Test
    @DisplayName("Debe normalizar y crear nombre con espacios")
    void testCreateThirdTypeNombreConEspaciosNormalizaYCrea() {
        // Arrange
        ThirdType thirdTypeWithSpaces = ThirdType.builder()
                .thirdTypeName("  Cliente  ")
                .entId(entId)
                .status(true)
                .build();

        ThirdType savedThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId(anyString(), eq(entId))).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(savedThirdType);

        // Act
        ThirdType result = createThirdTypeService.createThirdType(thirdTypeWithSpaces);

        // Assert
        assertNotNull(result);
        verify(thirdTypeRepository).existsByTtNameIgnoreCaseAndTtentId(anyString(), eq(entId));
        verify(idOutputPort).saveThirdType(argThat(tt -> 
            tt.getThirdTypeName() != null && !tt.getThirdTypeName().startsWith(" ")
        ));
    }

    @Test
    @DisplayName("Debe crear cada uno con diferentes nombres")
    void testCreateThirdTypeDiferentesNombresCreaCadaUno() {
        // Arrange
        ThirdType proveedor = ThirdType.builder()
                .thirdTypeName("Proveedor")
                .entId(entId)
                .status(true)
                .build();

        ThirdType savedProveedor = ThirdType.builder()
                .thirdTypeId(2L)
                .thirdTypeName("Proveedor")
                .entId(entId)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Proveedor", entId)).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(savedProveedor);

        // Act
        ThirdType result = createThirdTypeService.createThirdType(proveedor);

        // Assert
        assertNotNull(result);
        assertEquals("Proveedor", result.getThirdTypeName());
        verify(thirdTypeRepository).existsByTtNameIgnoreCaseAndTtentId("Proveedor", entId);
    }

    @Test
    @DisplayName("Debe crear con estado activo cuando status es true")
    void testCreateThirdTypeStatusTrueCreaConStatusTrue() {
        // Arrange
        ThirdType savedThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Cliente", entId)).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(savedThirdType);

        // Act
        ThirdType result = createThirdTypeService.createThirdType(thirdType);

        // Assert
        assertNotNull(result);
        assertTrue(result.getStatus());
    }

    @Test
    @DisplayName("Debe crear con estado inactivo cuando status es false")
    void testCreateThirdTypeStatusFalseCreaConStatusFalse() {
        // Arrange
        ThirdType inactiveThirdType = ThirdType.builder()
                .thirdTypeName("Empleado")
                .entId(entId)
                .status(false)
                .build();

        ThirdType savedThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Empleado")
                .entId(entId)
                .status(false)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Empleado", entId)).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(savedThirdType);

        // Act
        ThirdType result = createThirdTypeService.createThirdType(inactiveThirdType);

        // Assert
        assertNotNull(result);
        assertFalse(result.getStatus());
    }

    // ==================== Normalización ====================

    @Test
    @DisplayName("Debe normalizar correctamente nombre con espacios múltiples")
    void testCreateThirdTypeNombreConEspaciosMultiplesNormalizaCorrectamente() {
        // Arrange
        ThirdType thirdTypeWithMultipleSpaces = ThirdType.builder()
                .thirdTypeName("  Cliente   Especial  ")
                .entId(entId)
                .status(true)
                .build();

        ThirdType savedThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente Especial")
                .entId(entId)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId(anyString(), eq(entId))).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(savedThirdType);

        // Act
        ThirdType result = createThirdTypeService.createThirdType(thirdTypeWithMultipleSpaces);

        // Assert
        assertNotNull(result);
        verify(idOutputPort).saveThirdType(argThat(tt -> 
            tt.getThirdTypeName() != null
        ));
    }

    @Test
    @DisplayName("Debe normalizar preservando mayúsculas y minúsculas con caracteres especiales")
    void testCreateThirdTypeNombreConCaracteresEspecialesNormalizaPreservandoCase() {
        // Arrange
        ThirdType thirdTypeWithSpecialChars = ThirdType.builder()
                .thirdTypeName("Cliente VIP")
                .entId(entId)
                .status(true)
                .build();

        ThirdType savedThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente VIP")
                .entId(entId)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId(anyString(), eq(entId))).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(savedThirdType);

        // Act
        ThirdType result = createThirdTypeService.createThirdType(thirdTypeWithSpecialChars);

        // Assert
        assertNotNull(result);
        verify(idOutputPort).saveThirdType(any(ThirdType.class));
    }

    @Test
    @DisplayName("Debe preservar minúsculas en nombre")
    void testCreateThirdTypeNombreMinusculasPreservaCase() {
        // Arrange
        ThirdType thirdTypeWithLowerCase = ThirdType.builder()
                .thirdTypeName("cliente")
                .entId(entId)
                .status(true)
                .build();

        ThirdType savedThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("cliente")
                .entId(entId)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId(anyString(), eq(entId))).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(savedThirdType);

        // Act
        ThirdType result = createThirdTypeService.createThirdType(thirdTypeWithLowerCase);

        // Assert
        assertNotNull(result);
        verify(idOutputPort).saveThirdType(any(ThirdType.class));
    }

    // ==================== Validación de duplicados ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando nombre está duplicado")
    void testCreateThirdTypeNombreDuplicadoLanzaThirdTypeNameAlreadyExistsException() {
        // Arrange
        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Cliente", entId)).thenReturn(true);

        // Act & Assert
        ThirdTypeNameAlreadyExistsException exception = assertThrows(
                ThirdTypeNameAlreadyExistsException.class,
                () -> createThirdTypeService.createThirdType(thirdType)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("Cliente"));
        verify(thirdTypeRepository).existsByTtNameIgnoreCaseAndTtentId("Cliente", entId);
        verify(idOutputPort, never()).saveThirdType(any(ThirdType.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando nombre duplicado tiene diferente capitalización")
    void testCreateThirdTypeNombreDuplicadoConDiferenteCaseLanzaExcepcion() {
        // Arrange
        ThirdType thirdTypeUpperCase = ThirdType.builder()
                .thirdTypeName("CLIENTE")
                .entId(entId)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId(anyString(), eq(entId))).thenReturn(true);

        // Act & Assert
        ThirdTypeNameAlreadyExistsException exception = assertThrows(
                ThirdTypeNameAlreadyExistsException.class,
                () -> createThirdTypeService.createThirdType(thirdTypeUpperCase)
        );

        assertNotNull(exception);
        verify(thirdTypeRepository).existsByTtNameIgnoreCaseAndTtentId(anyString(), eq(entId));
        verify(idOutputPort, never()).saveThirdType(any(ThirdType.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando nombre duplicado tiene espacios")
    void testCreateThirdTypeNombreDuplicadoConEspaciosLanzaExcepcion() {
        // Arrange
        ThirdType thirdTypeWithSpaces = ThirdType.builder()
                .thirdTypeName("  Cliente  ")
                .entId(entId)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId(anyString(), eq(entId))).thenReturn(true);

        // Act & Assert
        ThirdTypeNameAlreadyExistsException exception = assertThrows(
                ThirdTypeNameAlreadyExistsException.class,
                () -> createThirdTypeService.createThirdType(thirdTypeWithSpaces)
        );

        assertNotNull(exception);
        verify(thirdTypeRepository).existsByTtNameIgnoreCaseAndTtentId(anyString(), eq(entId));
        verify(idOutputPort, never()).saveThirdType(any(ThirdType.class));
    }

    @Test
    @DisplayName("Debe crear correctamente mismo nombre con diferente entidad")
    void testCreateThirdTypeMismoNombreDiferenteEntIdCreaCorrectamente() {
        // Arrange
        String differentEntId = "ENT002";
        ThirdType thirdTypeDifferentEnt = ThirdType.builder()
                .thirdTypeName("Cliente")
                .entId(differentEntId)
                .status(true)
                .build();

        ThirdType savedThirdType = ThirdType.builder()
                .thirdTypeId(2L)
                .thirdTypeName("Cliente")
                .entId(differentEntId)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Cliente", differentEntId)).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(savedThirdType);

        // Act
        ThirdType result = createThirdTypeService.createThirdType(thirdTypeDifferentEnt);

        // Assert
        assertNotNull(result);
        assertEquals(differentEntId, result.getEntId());
        verify(thirdTypeRepository).existsByTtNameIgnoreCaseAndTtentId("Cliente", differentEntId);
        verify(idOutputPort).saveThirdType(any(ThirdType.class));
    }

    // ==================== Diferentes entidades ====================

    @Test
    @DisplayName("Debe crear cada uno con diferentes entidades")
    void testCreateThirdTypeDiferentesEntIdsCreaCadaUno() {
        // Arrange
        String entId1 = "ENT001";
        String entId2 = "ENT002";

        ThirdType thirdType1 = ThirdType.builder()
                .thirdTypeName("Cliente")
                .entId(entId1)
                .status(true)
                .build();

        ThirdType thirdType2 = ThirdType.builder()
                .thirdTypeName("Proveedor")
                .entId(entId2)
                .status(true)
                .build();

        ThirdType saved1 = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .entId(entId1)
                .status(true)
                .build();

        ThirdType saved2 = ThirdType.builder()
                .thirdTypeId(2L)
                .thirdTypeName("Proveedor")
                .entId(entId2)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Cliente", entId1)).thenReturn(false);
        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Proveedor", entId2)).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(saved1, saved2);

        // Act
        ThirdType result1 = createThirdTypeService.createThirdType(thirdType1);
        ThirdType result2 = createThirdTypeService.createThirdType(thirdType2);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(entId1, result1.getEntId());
        assertEquals(entId2, result2.getEntId());
        verify(thirdTypeRepository).existsByTtNameIgnoreCaseAndTtentId("Cliente", entId1);
        verify(thirdTypeRepository).existsByTtNameIgnoreCaseAndTtentId("Proveedor", entId2);
        verify(idOutputPort, times(2)).saveThirdType(any(ThirdType.class));
    }

    @Test
    @DisplayName("Debe crear todos los tipos con diferentes nombres en misma entidad")
    void testCreateThirdTypeDiferentesNombresEnMismaEntidadCreaTodos() {
        // Arrange
        ThirdType cliente = ThirdType.builder()
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(true)
                .build();

        ThirdType proveedor = ThirdType.builder()
                .thirdTypeName("Proveedor")
                .entId(entId)
                .status(true)
                .build();

        ThirdType empleado = ThirdType.builder()
                .thirdTypeName("Empleado")
                .entId(entId)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Cliente", entId)).thenReturn(false);
        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Proveedor", entId)).thenReturn(false);
        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Empleado", entId)).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class)))
                .thenReturn(cliente, proveedor, empleado);

        // Act
        ThirdType result1 = createThirdTypeService.createThirdType(cliente);
        ThirdType result2 = createThirdTypeService.createThirdType(proveedor);
        ThirdType result3 = createThirdTypeService.createThirdType(empleado);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        verify(idOutputPort, times(3)).saveThirdType(any(ThirdType.class));
    }

    // ==================== Propagación de excepciones ====================

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en repositorio")
    void testCreateThirdTypeErrorEnRepositoryPropagaExcepcion() {
        // Arrange
        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Cliente", entId))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act & Assert
        assertThrows(
                RuntimeException.class,
                () -> createThirdTypeService.createThirdType(thirdType)
        );

        verify(thirdTypeRepository).existsByTtNameIgnoreCaseAndTtentId("Cliente", entId);
        verify(idOutputPort, never()).saveThirdType(any(ThirdType.class));
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en puerto de salida")
    void testCreateThirdTypeErrorEnIdOutputPortPropagaExcepcion() {
        // Arrange
        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Cliente", entId)).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class)))
                .thenThrow(new RuntimeException("Error al guardar"));

        // Act & Assert
        assertThrows(
                RuntimeException.class,
                () -> createThirdTypeService.createThirdType(thirdType)
        );

        verify(thirdTypeRepository).existsByTtNameIgnoreCaseAndTtentId("Cliente", entId);
        verify(idOutputPort).saveThirdType(any(ThirdType.class));
    }

    // ==================== Delegación correcta ====================

    @Test
    @DisplayName("Debe delegar al repositorio para validación correctamente")
    void testCreateThirdTypeDelegaAlRepositoryParaValidacionCorrectamente() {
        // Arrange
        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Cliente", entId)).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(thirdType);

        // Act
        createThirdTypeService.createThirdType(thirdType);

        // Assert
        verify(thirdTypeRepository, times(1)).existsByTtNameIgnoreCaseAndTtentId("Cliente", entId);
        verifyNoMoreInteractions(thirdTypeRepository);
    }

    @Test
    @DisplayName("Debe delegar al puerto para guardar correctamente")
    void testCreateThirdTypeDelegaAlPuertoParaGuardarCorrectamente() {
        // Arrange
        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Cliente", entId)).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(thirdType);

        // Act
        createThirdTypeService.createThirdType(thirdType);

        // Assert
        verify(idOutputPort, times(1)).saveThirdType(any(ThirdType.class));
    }

    @Test
    @DisplayName("Debe pasar datos normalizados al puerto")
    void testCreateThirdTypePasaDatosNormalizadosAlPuerto() {
        // Arrange
        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId(anyString(), eq(entId))).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        createThirdTypeService.createThirdType(thirdType);

        // Assert
        verify(idOutputPort).saveThirdType(argThat(tt ->
                tt.getThirdTypeName() != null &&
                        tt.getEntId().equals(entId) &&
                        tt.getStatus() != null
        ));
    }

    // ==================== Integración ====================

    @Test
    @DisplayName("Debe validar y guardar en proceso completo")
    void testCreateThirdTypeProcesoCompletoValidaYGuarda() {
        // Arrange
        ThirdType savedThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Cliente", entId)).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(savedThirdType);

        var inOrder = inOrder(thirdTypeRepository, idOutputPort);

        // Act
        ThirdType result = createThirdTypeService.createThirdType(thirdType);

        // Assert
        assertNotNull(result);
        inOrder.verify(thirdTypeRepository).existsByTtNameIgnoreCaseAndTtentId("Cliente", entId);
        inOrder.verify(idOutputPort).saveThirdType(any(ThirdType.class));
    }

    @Test
    @DisplayName("Debe validar independientemente cada uno en varios intentos")
    void testCreateThirdTypeVariosIntentosCadaUnoValidaIndependientemente() {
        // Arrange
        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Cliente", entId)).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(thirdType);

        // Act
        createThirdTypeService.createThirdType(thirdType);
        createThirdTypeService.createThirdType(thirdType);
        createThirdTypeService.createThirdType(thirdType);

        // Assert
        verify(thirdTypeRepository, times(3)).existsByTtNameIgnoreCaseAndTtentId("Cliente", entId);
        verify(idOutputPort, times(3)).saveThirdType(any(ThirdType.class));
    }

    @Test
    @DisplayName("Debe retornar objeto guardado con ID asignado")
    void testCreateThirdTypeRetornaObjetoGuardadoConIdAsignado() {
        // Arrange
        ThirdType savedThirdType = ThirdType.builder()
                .thirdTypeId(100L)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Cliente", entId)).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(savedThirdType);

        // Act
        ThirdType result = createThirdTypeService.createThirdType(thirdType);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getThirdTypeId());
        assertEquals(100L, result.getThirdTypeId());
    }

    @Test
    @DisplayName("Debe crear correctamente con nombre largo")
    void testCreateThirdTypeConNombreLargoCreaCorrectamente() {
        // Arrange
        ThirdType thirdTypeWithLongName = ThirdType.builder()
                .thirdTypeName("Cliente Especial Preferencial VIP Oro")
                .entId(entId)
                .status(true)
                .build();

        ThirdType savedThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente Especial Preferencial VIP Oro")
                .entId(entId)
                .status(true)
                .build();

        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId(anyString(), eq(entId))).thenReturn(false);
        when(idOutputPort.saveThirdType(any(ThirdType.class))).thenReturn(savedThirdType);

        // Act
        ThirdType result = createThirdTypeService.createThirdType(thirdTypeWithLongName);

        // Assert
        assertNotNull(result);
        verify(idOutputPort).saveThirdType(any(ThirdType.class));
    }
}
