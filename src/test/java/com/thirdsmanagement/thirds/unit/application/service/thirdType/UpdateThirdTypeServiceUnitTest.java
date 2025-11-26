package com.thirdsmanagement.thirds.unit.application.service.thirdType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.service.thirdType.UpdateThirdTypeService;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeInUseException;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeNotFound;
import com.thirdsmanagement.thirds.domain.model.ThirdType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateThirdTypeServiceUnitTest {

    @Mock
    private IdOutputPort idOutputPort;

    @InjectMocks
    private UpdateThirdTypeService updateThirdTypeService;

    private String entId;
    private ThirdType existingThirdType;
    private ThirdType updatedThirdType;

    @BeforeEach
    void setUp() {
        entId = "ENT001";
        
        existingThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(true)
                .build();

        updatedThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente Premium")
                .entId(entId)
                .status(true)
                .build();
    }

    // ==================== Actualización exitosa ====================

    @Test
    @DisplayName("Debe actualizar correctamente tipo de tercero válido")
    void testUpdateThirdTypeThirdTypeValidoActualizaCorrectamente() {
        // Arrange
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateThirdType(updatedThirdType)).thenReturn(updatedThirdType);

        // Act
        ThirdType result = updateThirdTypeService.updateThirdType(updatedThirdType);

        // Assert
        assertNotNull(result);
        assertEquals("Cliente Premium", result.getThirdTypeName());
        verify(idOutputPort).getThirdTypeById(1L);
        verify(idOutputPort).hasThirdTypeThirdsWithMovements(1L, entId);
        verify(idOutputPort).updateThirdType(updatedThirdType);
    }

    @Test
    @DisplayName("Debe actualizar correctamente con cambio de nombre")
    void testUpdateThirdTypeCambioDeNombreActualizaCorrectamente() {
        // Arrange
        ThirdType newNameThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Proveedor")
                .entId(entId)
                .status(true)
                .build();

        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateThirdType(newNameThirdType)).thenReturn(newNameThirdType);

        // Act
        ThirdType result = updateThirdTypeService.updateThirdType(newNameThirdType);

        // Assert
        assertNotNull(result);
        assertEquals("Proveedor", result.getThirdTypeName());
        verify(idOutputPort).updateThirdType(newNameThirdType);
    }

    @Test
    @DisplayName("Debe actualizar correctamente con cambio de estado")
    void testUpdateThirdTypeCambioDeStatusActualizaCorrectamente() {
        // Arrange
        ThirdType inactiveThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(false)
                .build();

        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateThirdType(inactiveThirdType)).thenReturn(inactiveThirdType);

        // Act
        ThirdType result = updateThirdTypeService.updateThirdType(inactiveThirdType);

        // Assert
        assertNotNull(result);
        assertFalse(result.getStatus());
        verify(idOutputPort).updateThirdType(inactiveThirdType);
    }

    @Test
    @DisplayName("Debe actualizar correctamente con cambio de nombre y estado")
    void testUpdateThirdTypeCambioDeNombreYStatusActualizaCorrectamente() {
        // Arrange
        ThirdType completeUpdateThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente VIP")
                .entId(entId)
                .status(false)
                .build();

        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateThirdType(completeUpdateThirdType)).thenReturn(completeUpdateThirdType);

        // Act
        ThirdType result = updateThirdTypeService.updateThirdType(completeUpdateThirdType);

        // Assert
        assertNotNull(result);
        assertEquals("Cliente VIP", result.getThirdTypeName());
        assertFalse(result.getStatus());
        verify(idOutputPort).updateThirdType(completeUpdateThirdType);
    }

    // ==================== Validación null ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo de tercero es nulo")
    void testUpdateThirdTypeThirdTypeNuloLanzaIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> updateThirdTypeService.updateThirdType(null));

        assertEquals("El tipo de tercero y su ID son requeridos para actualizar", exception.getMessage());
        verify(idOutputPort, never()).updateThirdType(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando ID de tipo de tercero es nulo")
    void testUpdateThirdTypeThirdTypeIdNuloLanzaIllegalArgumentException() {
        // Arrange
        ThirdType thirdTypeWithoutId = ThirdType.builder()
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(true)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> updateThirdTypeService.updateThirdType(thirdTypeWithoutId));

        assertEquals("El tipo de tercero y su ID son requeridos para actualizar", exception.getMessage());
        verify(idOutputPort, never()).updateThirdType(any());
    }

    // ==================== Validación de existencia ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo de tercero no existe")
    void testUpdateThirdTypeThirdTypeNoExisteLanzaThirdTypeNotFound() {
        // Arrange
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(null);

        // Act & Assert
        ThirdTypeNotFound exception = assertThrows(ThirdTypeNotFound.class,
                () -> updateThirdTypeService.updateThirdType(updatedThirdType));

        assertEquals("El tipo de tercero con ID 1 no existe", exception.getMessage());
        verify(idOutputPort).getThirdTypeById(1L);
        verify(idOutputPort, never()).updateThirdType(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando ID no existe en base de datos")
    void testUpdateThirdTypeIdNoExisteEnBaseDatosLanzaThirdTypeNotFound() {
        // Arrange
        ThirdType nonExistentThirdType = ThirdType.builder()
                .thirdTypeId(999L)
                .thirdTypeName("No Existe")
                .entId(entId)
                .status(true)
                .build();

        when(idOutputPort.getThirdTypeById(999L)).thenReturn(null);

        // Act & Assert
        assertThrows(ThirdTypeNotFound.class,
                () -> updateThirdTypeService.updateThirdType(nonExistentThirdType));

        verify(idOutputPort, never()).updateThirdType(any());
    }

    // ==================== Validación con movimientos ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo de tercero tiene movimientos")
    void testUpdateThirdTypeThirdTypeConMovimientosLanzaThirdTypeInUseException() {
        // Arrange
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(true);

        // Act & Assert
        ThirdTypeInUseException exception = assertThrows(ThirdTypeInUseException.class,
                () -> updateThirdTypeService.updateThirdType(updatedThirdType));

        assertTrue(exception.getMessage().contains("Cliente"));
        verify(idOutputPort).getThirdTypeById(1L);
        verify(idOutputPort).hasThirdTypeThirdsWithMovements(1L, entId);
        verify(idOutputPort, never()).updateThirdType(any());
    }

    @Test
    @DisplayName("Debe no permitir actualización cuando tipo de tercero tiene movimientos contables")
    void testUpdateThirdTypeThirdTypeTieneMovimientosContablesNoPermiteActualizacion() {
        // Arrange
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(true);

        // Act & Assert
        assertThrows(ThirdTypeInUseException.class,
                () -> updateThirdTypeService.updateThirdType(updatedThirdType));

        verify(idOutputPort, never()).updateThirdType(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo de tercero inactivo tiene movimientos")
    void testUpdateThirdTypeThirdTypeInactivoConMovimientosLanzaThirdTypeInUseException() {
        // Arrange
        ThirdType inactiveExisting = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(false)
                .build();

        when(idOutputPort.getThirdTypeById(1L)).thenReturn(inactiveExisting);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(true);

        // Act & Assert
        assertThrows(ThirdTypeInUseException.class,
                () -> updateThirdTypeService.updateThirdType(updatedThirdType));

        verify(idOutputPort, never()).updateThirdType(any());
    }

    // ==================== Casos especiales ====================

    @Test
    @DisplayName("Debe validar correctamente cuando ID es cero")
    void testUpdateThirdTypeIdCeroValidaCorrectamente() {
        // Arrange
        ThirdType zeroIdThirdType = ThirdType.builder()
                .thirdTypeId(0L)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(true)
                .build();

        when(idOutputPort.getThirdTypeById(0L)).thenReturn(null);

        // Act & Assert
        assertThrows(ThirdTypeNotFound.class,
                () -> updateThirdTypeService.updateThirdType(zeroIdThirdType));

        verify(idOutputPort, never()).updateThirdType(any());
    }

    @Test
    @DisplayName("Debe validar correctamente cuando ID es negativo")
    void testUpdateThirdTypeIdNegativoValidaCorrectamente() {
        // Arrange
        ThirdType negativeIdThirdType = ThirdType.builder()
                .thirdTypeId(-1L)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(true)
                .build();

        when(idOutputPort.getThirdTypeById(-1L)).thenReturn(null);

        // Act & Assert
        assertThrows(ThirdTypeNotFound.class,
                () -> updateThirdTypeService.updateThirdType(negativeIdThirdType));

        verify(idOutputPort, never()).updateThirdType(any());
    }

    @Test
    @DisplayName("Debe actualizar correctamente con mismos datos")
    void testUpdateThirdTypeMismosDatosActualizaCorrectamente() {
        // Arrange
        ThirdType sameDataThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(true)
                .build();

        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateThirdType(sameDataThirdType)).thenReturn(sameDataThirdType);

        // Act
        ThirdType result = updateThirdTypeService.updateThirdType(sameDataThirdType);

        // Assert
        assertNotNull(result);
        assertEquals("Cliente", result.getThirdTypeName());
        verify(idOutputPort).updateThirdType(sameDataThirdType);
    }

    // ==================== Propagación de excepciones ====================

    @Test
    @DisplayName("Debe propagar excepción cuando hay error al obtener tipo de tercero por ID")
    void testUpdateThirdTypeErrorEnGetThirdTypeByIdPropagaExcepcion() {
        // Arrange
        when(idOutputPort.getThirdTypeById(1L))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> updateThirdTypeService.updateThirdType(updatedThirdType));

        verify(idOutputPort, never()).updateThirdType(any());
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error al verificar movimientos")
    void testUpdateThirdTypeErrorEnHasThirdTypeThirdsWithMovementsPropagaExcepcion() {
        // Arrange
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId))
                .thenThrow(new RuntimeException("Error al verificar movimientos"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> updateThirdTypeService.updateThirdType(updatedThirdType));

        verify(idOutputPort, never()).updateThirdType(any());
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error al actualizar tipo de tercero")
    void testUpdateThirdTypeErrorEnUpdateThirdTypePropagaExcepcion() {
        // Arrange
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateThirdType(updatedThirdType))
                .thenThrow(new RuntimeException("Error al actualizar"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> updateThirdTypeService.updateThirdType(updatedThirdType));
    }

    // ==================== Delegación correcta ====================

    @Test
    @DisplayName("Debe delegar al puerto para validar existencia correctamente")
    void testUpdateThirdTypeDelegaAlPuertoParaValidarExistenciaCorrectamente() {
        // Arrange
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateThirdType(updatedThirdType)).thenReturn(updatedThirdType);

        // Act
        updateThirdTypeService.updateThirdType(updatedThirdType);

        // Assert
        verify(idOutputPort, times(1)).getThirdTypeById(1L);
    }

    @Test
    @DisplayName("Debe delegar al puerto para validar movimientos correctamente")
    void testUpdateThirdTypeDelegaAlPuertoParaValidarMovimientosCorrectamente() {
        // Arrange
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateThirdType(updatedThirdType)).thenReturn(updatedThirdType);

        // Act
        updateThirdTypeService.updateThirdType(updatedThirdType);

        // Assert
        verify(idOutputPort, times(1)).hasThirdTypeThirdsWithMovements(1L, entId);
    }

    @Test
    @DisplayName("Debe delegar al puerto para actualizar correctamente")
    void testUpdateThirdTypeDelegaAlPuertoParaActualizarCorrectamente() {
        // Arrange
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateThirdType(updatedThirdType)).thenReturn(updatedThirdType);

        // Act
        updateThirdTypeService.updateThirdType(updatedThirdType);

        // Assert
        verify(idOutputPort, times(1)).updateThirdType(updatedThirdType);
    }

    @Test
    @DisplayName("Debe pasar datos completos al puerto")
    void testUpdateThirdTypePasaDatosCompletosAlPuerto() {
        // Arrange
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateThirdType(any(ThirdType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        updateThirdTypeService.updateThirdType(updatedThirdType);

        // Assert
        verify(idOutputPort).updateThirdType(argThat(tt ->
                tt.getThirdTypeId().equals(1L) &&
                tt.getThirdTypeName().equals("Cliente Premium") &&
                tt.getEntId().equals(entId) &&
                tt.getStatus() != null
        ));
    }

    // ==================== Integración ====================

    @Test
    @DisplayName("Debe validar y actualizar en proceso completo")
    void testUpdateThirdTypeProcesoCompletoValidaYActualiza() {
        // Arrange
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateThirdType(updatedThirdType)).thenReturn(updatedThirdType);

        // Act
        ThirdType result = updateThirdTypeService.updateThirdType(updatedThirdType);

        // Assert
        assertNotNull(result);
        InOrder inOrder = inOrder(idOutputPort);
        inOrder.verify(idOutputPort).getThirdTypeById(1L);
        inOrder.verify(idOutputPort).hasThirdTypeThirdsWithMovements(1L, entId);
        inOrder.verify(idOutputPort).updateThirdType(updatedThirdType);
    }

    @Test
    @DisplayName("Debe validar independientemente cada una en varias actualizaciones")
    void testUpdateThirdTypeVariasActualizacionesCadaUnaValidaIndependientemente() {
        // Arrange
        ThirdType tt1 = ThirdType.builder().thirdTypeId(1L).thirdTypeName("Cliente").entId(entId).status(true).build();
        ThirdType tt2 = ThirdType.builder().thirdTypeId(2L).thirdTypeName("Proveedor").entId(entId).status(true).build();
        ThirdType tt3 = ThirdType.builder().thirdTypeId(3L).thirdTypeName("Empleado").entId(entId).status(true).build();

        ThirdType existing1 = ThirdType.builder().thirdTypeId(1L).thirdTypeName("Cliente Original").entId(entId).status(true).build();
        ThirdType existing2 = ThirdType.builder().thirdTypeId(2L).thirdTypeName("Proveedor Original").entId(entId).status(true).build();
        ThirdType existing3 = ThirdType.builder().thirdTypeId(3L).thirdTypeName("Empleado Original").entId(entId).status(true).build();

        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existing1);
        when(idOutputPort.getThirdTypeById(2L)).thenReturn(existing2);
        when(idOutputPort.getThirdTypeById(3L)).thenReturn(existing3);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(anyLong(), eq(entId))).thenReturn(false);
        when(idOutputPort.updateThirdType(any(ThirdType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        updateThirdTypeService.updateThirdType(tt1);
        updateThirdTypeService.updateThirdType(tt2);
        updateThirdTypeService.updateThirdType(tt3);

        // Assert
        verify(idOutputPort, times(3)).getThirdTypeById(anyLong());
        verify(idOutputPort, times(3)).hasThirdTypeThirdsWithMovements(anyLong(), eq(entId));
        verify(idOutputPort, times(3)).updateThirdType(any(ThirdType.class));
    }

    @Test
    @DisplayName("Debe detener validaciones secuenciales si alguna falla")
    void testUpdateThirdTypeValidacionesSecuencialesSeDetieneSiAlgunaFalla() {
        // Arrange
        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(true);

        // Act & Assert
        assertThrows(ThirdTypeInUseException.class,
                () -> updateThirdTypeService.updateThirdType(updatedThirdType));

        verify(idOutputPort).getThirdTypeById(1L);
        verify(idOutputPort).hasThirdTypeThirdsWithMovements(1L, entId);
        verify(idOutputPort, never()).updateThirdType(any());
    }

    @Test
    @DisplayName("Debe retornar resultado del puerto correctamente")
    void testUpdateThirdTypeRetornaResultadoDelPuertoCorrectamente() {
        // Arrange
        ThirdType portResult = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente Premium")
                .entId(entId)
                .status(true)
                .build();

        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existingThirdType);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateThirdType(updatedThirdType)).thenReturn(portResult);

        // Act
        ThirdType result = updateThirdTypeService.updateThirdType(updatedThirdType);

        // Assert
        assertSame(portResult, result);
        assertEquals("Cliente Premium", result.getThirdTypeName());
        verify(idOutputPort).updateThirdType(updatedThirdType);
    }

    @Test
    @DisplayName("Debe actualizar independientemente con diferentes entidades")
    void testUpdateThirdTypeDiferentesEntidadesActualizaIndependientemente() {
        // Arrange
        String entId2 = "ENT002";
        ThirdType existing1 = ThirdType.builder().thirdTypeId(1L).thirdTypeName("Cliente").entId(entId).status(true).build();
        ThirdType existing2 = ThirdType.builder().thirdTypeId(2L).thirdTypeName("Cliente").entId(entId2).status(true).build();

        ThirdType update1 = ThirdType.builder().thirdTypeId(1L).thirdTypeName("Cliente VIP").entId(entId).status(true).build();
        ThirdType update2 = ThirdType.builder().thirdTypeId(2L).thirdTypeName("Cliente VIP").entId(entId2).status(true).build();

        when(idOutputPort.getThirdTypeById(1L)).thenReturn(existing1);
        when(idOutputPort.getThirdTypeById(2L)).thenReturn(existing2);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.hasThirdTypeThirdsWithMovements(2L, entId2)).thenReturn(false);
        when(idOutputPort.updateThirdType(update1)).thenReturn(update1);
        when(idOutputPort.updateThirdType(update2)).thenReturn(update2);

        // Act
        ThirdType result1 = updateThirdTypeService.updateThirdType(update1);
        ThirdType result2 = updateThirdTypeService.updateThirdType(update2);

        // Assert
        assertEquals(entId, result1.getEntId());
        assertEquals(entId2, result2.getEntId());
        verify(idOutputPort).hasThirdTypeThirdsWithMovements(1L, entId);
        verify(idOutputPort).hasThirdTypeThirdsWithMovements(2L, entId2);
    }
}
