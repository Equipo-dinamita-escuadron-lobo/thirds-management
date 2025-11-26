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
    @DisplayName("test_UpdateThirdType_ThirdTypeValido_ActualizaCorrectamente")
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
    @DisplayName("test_UpdateThirdType_CambioDeNombre_ActualizaCorrectamente")
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
    @DisplayName("test_UpdateThirdType_CambioDeStatus_ActualizaCorrectamente")
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
    @DisplayName("test_UpdateThirdType_CambioDeNombreYStatus_ActualizaCorrectamente")
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
    @DisplayName("test_UpdateThirdType_ThirdTypeNulo_LanzaIllegalArgumentException")
    void testUpdateThirdTypeThirdTypeNuloLanzaIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> updateThirdTypeService.updateThirdType(null));

        assertEquals("El tipo de tercero y su ID son requeridos para actualizar", exception.getMessage());
        verify(idOutputPort, never()).updateThirdType(any());
    }

    @Test
    @DisplayName("test_UpdateThirdType_ThirdTypeIdNulo_LanzaIllegalArgumentException")
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
    @DisplayName("test_UpdateThirdType_ThirdTypeNoExiste_LanzaThirdTypeNotFound")
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
    @DisplayName("test_UpdateThirdType_IdNoExisteEnBaseDatos_LanzaThirdTypeNotFound")
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
    @DisplayName("test_UpdateThirdType_ThirdTypeConMovimientos_LanzaThirdTypeInUseException")
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
    @DisplayName("test_UpdateThirdType_ThirdTypeTieneMovimientosContables_NoPermiteActualizacion")
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
    @DisplayName("test_UpdateThirdType_ThirdTypeInactivoConMovimientos_LanzaThirdTypeInUseException")
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
    @DisplayName("test_UpdateThirdType_IdCero_ValidaCorrectamente")
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
    @DisplayName("test_UpdateThirdType_IdNegativo_ValidaCorrectamente")
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
    @DisplayName("test_UpdateThirdType_MismosDatos_ActualizaCorrectamente")
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
    @DisplayName("test_UpdateThirdType_ErrorEnGetThirdTypeById_PropagaExcepcion")
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
    @DisplayName("test_UpdateThirdType_ErrorEnHasThirdTypeThirdsWithMovements_PropagaExcepcion")
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
    @DisplayName("test_UpdateThirdType_ErrorEnUpdateThirdType_PropagaExcepcion")
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
    @DisplayName("test_UpdateThirdType_DelegaAlPuertoParaValidarExistencia_Correctamente")
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
    @DisplayName("test_UpdateThirdType_DelegaAlPuertoParaValidarMovimientos_Correctamente")
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
    @DisplayName("test_UpdateThirdType_DelegaAlPuertoParaActualizar_Correctamente")
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
    @DisplayName("test_UpdateThirdType_PasaDatosCompletos_AlPuerto")
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
    @DisplayName("test_UpdateThirdType_ProcesoCompleto_ValidaYActualiza")
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
    @DisplayName("test_UpdateThirdType_VariasActualizaciones_CadaUnaValidaIndependientemente")
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
    @DisplayName("test_UpdateThirdType_ValidacionesSecuenciales_SeDetieneSiAlgunaFalla")
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
    @DisplayName("test_UpdateThirdType_RetornaResultadoDelPuerto_Correctamente")
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
    @DisplayName("test_UpdateThirdType_DiferentesEntidades_ActualizaIndependientemente")
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
