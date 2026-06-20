package com.thirdsmanagement.thirds.unit.application.service.third;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.application.service.third.ChangeThirdStateService;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdStateNotChanged;

/**
 * Tests unitarios para ChangeThirdStateService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChangeThirdStateServiceUnitTest {

    @Mock
    private ThirdOutputPort thirdOutputPort;

    @InjectMocks
    private ChangeThirdStateService changeThirdStateService;

    private Long thId;
    private String entId;

    @BeforeEach
    void setUp() {
        thId = 1L;
        entId = "ENT001";
    }

    // ========== changeThirdState Tests - Cambio Exitoso ==========

    @Test
    @DisplayName("Debe cambiar el estado del tercero exitosamente")
    void testChangeThirdState_SuccessfulStateChange() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(true);

        // Act
        boolean result = changeThirdStateService.changeThirdState(thId, entId);

        // Assert
        assertTrue(result);
        verify(thirdOutputPort).changeThirdState(thId, entId);
    }

    @Test
    @DisplayName("Debe retornar true cuando el cambio es exitoso")
    void testChangeThirdState_ReturnsTrue() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(true);

        // Act
        boolean result = changeThirdStateService.changeThirdState(thId, entId);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe invocar el output port con los parámetros correctos")
    void testChangeThirdState_InvokesOutputPortWithCorrectParams() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(true);

        // Act
        changeThirdStateService.changeThirdState(thId, entId);

        // Assert
        verify(thirdOutputPort).changeThirdState(eq(thId), eq(entId));
    }

    @Test
    @DisplayName("Debe invocar el output port una sola vez")
    void testChangeThirdState_InvokesOutputPortOnce() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(true);

        // Act
        changeThirdStateService.changeThirdState(thId, entId);

        // Assert
        verify(thirdOutputPort, times(1)).changeThirdState(anyLong(), anyString());
    }

    // ========== changeThirdState Tests - Cambio Fallido ==========

    @Test
    @DisplayName("Debe lanzar excepción cuando el output port retorna false")
    void testChangeThirdState_ThrowsExceptionWhenReturnsFalse() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(false);

        // Act & Assert
        ThirdStateNotChanged exception = assertThrows(ThirdStateNotChanged.class, () -> {
            changeThirdStateService.changeThirdState(thId, entId);
        });

        assertEquals("El estado no se pudo cambiar debido a un error interno", exception.getMessage());
        verify(thirdOutputPort).changeThirdState(thId, entId);
    }

    @Test
    @DisplayName("Debe lanzar ThirdStateNotChanged con mensaje correcto")
    void testChangeThirdState_ThrowsExceptionWithCorrectMessage() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(false);

        // Act & Assert
        ThirdStateNotChanged exception = assertThrows(ThirdStateNotChanged.class, () -> {
            changeThirdStateService.changeThirdState(thId, entId);
        });

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("estado no se pudo cambiar"));
    }

    @Test
    @DisplayName("Debe verificar llamada al output port incluso cuando falla")
    void testChangeThirdState_VerifiesCallEvenWhenFails() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(false);

        // Act & Assert
        assertThrows(ThirdStateNotChanged.class, () -> {
            changeThirdStateService.changeThirdState(thId, entId);
        });

        verify(thirdOutputPort, times(1)).changeThirdState(thId, entId);
    }

    // ========== changeThirdState Tests - Diferentes Terceros ==========

    @Test
    @DisplayName("Debe procesar correctamente diferentes IDs de terceros")
    void testChangeThirdState_DifferentThirdIds() {
        // Arrange
        Long thId1 = 1L;
        Long thId2 = 2L;

        when(thirdOutputPort.changeThirdState(thId1, entId)).thenReturn(true);
        when(thirdOutputPort.changeThirdState(thId2, entId)).thenReturn(true);

        // Act
        boolean result1 = changeThirdStateService.changeThirdState(thId1, entId);
        boolean result2 = changeThirdStateService.changeThirdState(thId2, entId);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        verify(thirdOutputPort).changeThirdState(thId1, entId);
        verify(thirdOutputPort).changeThirdState(thId2, entId);
    }

    @Test
    @DisplayName("Debe procesar correctamente diferentes entidades")
    void testChangeThirdState_DifferentEntities() {
        // Arrange
        String entId1 = "ENT001";
        String entId2 = "ENT002";

        when(thirdOutputPort.changeThirdState(thId, entId1)).thenReturn(true);
        when(thirdOutputPort.changeThirdState(thId, entId2)).thenReturn(true);

        // Act
        boolean result1 = changeThirdStateService.changeThirdState(thId, entId1);
        boolean result2 = changeThirdStateService.changeThirdState(thId, entId2);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        verify(thirdOutputPort).changeThirdState(thId, entId1);
        verify(thirdOutputPort).changeThirdState(thId, entId2);
    }

    @Test
    @DisplayName("Debe manejar IDs de terceros grandes")
    void testChangeThirdState_LargeThirdIds() {
        // Arrange
        Long largeThId = 999999999L;
        when(thirdOutputPort.changeThirdState(largeThId, entId)).thenReturn(true);

        // Act
        boolean result = changeThirdStateService.changeThirdState(largeThId, entId);

        // Assert
        assertTrue(result);
        verify(thirdOutputPort).changeThirdState(largeThId, entId);
    }

    // ========== changeThirdState Tests - Propagación de Excepciones ==========

    @Test
    @DisplayName("Debe propagar excepción del output port")
    void testChangeThirdState_PropagatesOutputPortException() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId))
                .thenThrow(new RuntimeException("Error en base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            changeThirdStateService.changeThirdState(thId, entId);
        });

        verify(thirdOutputPort).changeThirdState(thId, entId);
    }

    @Test
    @DisplayName("Debe propagar excepción de tipo específico")
    void testChangeThirdState_PropagatesSpecificException() {
        // Arrange
        IllegalStateException exception = new IllegalStateException("Estado inválido");
        when(thirdOutputPort.changeThirdState(thId, entId)).thenThrow(exception);

        // Act & Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            changeThirdStateService.changeThirdState(thId, entId);
        });

        assertEquals("Estado inválido", thrown.getMessage());
        verify(thirdOutputPort).changeThirdState(thId, entId);
    }

    @Test
    @DisplayName("Debe propagar NullPointerException del output port")
    void testChangeThirdState_PropagatesNullPointerException() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId))
                .thenThrow(new NullPointerException("Parámetro nulo"));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            changeThirdStateService.changeThirdState(thId, entId);
        });
    }

    // ========== changeThirdState Tests - Validación de Lógica ==========

    @Test
    @DisplayName("Debe no lanzar excepción cuando retorna true")
    void testChangeThirdState_NoExceptionWhenTrue() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> {
            changeThirdStateService.changeThirdState(thId, entId);
        });
    }

    @Test
    @DisplayName("Debe siempre lanzar excepción cuando retorna false")
    void testChangeThirdState_AlwaysThrowsWhenFalse() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(false);

        // Act & Assert
        assertThrows(ThirdStateNotChanged.class, () -> {
            changeThirdStateService.changeThirdState(thId, entId);
        });
    }

    @Test
    @DisplayName("Debe delegar completamente al output port")
    void testChangeThirdState_DelegatesToOutputPort() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(true);

        // Act
        changeThirdStateService.changeThirdState(thId, entId);

        // Assert
        verify(thirdOutputPort).changeThirdState(thId, entId);
        verifyNoMoreInteractions(thirdOutputPort);
    }

    // ========== Integration Scenario Tests ==========

    @Test
    @DisplayName("Debe procesar múltiples cambios de estado consecutivos exitosos")
    void testChangeThirdState_MultipleSuccessfulChanges() {
        // Arrange
        Long thId1 = 1L;
        Long thId2 = 2L;
        Long thId3 = 3L;

        when(thirdOutputPort.changeThirdState(thId1, entId)).thenReturn(true);
        when(thirdOutputPort.changeThirdState(thId2, entId)).thenReturn(true);
        when(thirdOutputPort.changeThirdState(thId3, entId)).thenReturn(true);

        // Act
        boolean result1 = changeThirdStateService.changeThirdState(thId1, entId);
        boolean result2 = changeThirdStateService.changeThirdState(thId2, entId);
        boolean result3 = changeThirdStateService.changeThirdState(thId3, entId);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
        verify(thirdOutputPort, times(3)).changeThirdState(anyLong(), eq(entId));
    }

    @Test
    @DisplayName("Debe manejar escenario mixto de éxito y fallo")
    void testChangeThirdState_MixedSuccessAndFailure() {
        // Arrange
        Long thId1 = 1L;
        Long thId2 = 2L;

        when(thirdOutputPort.changeThirdState(thId1, entId)).thenReturn(true);
        when(thirdOutputPort.changeThirdState(thId2, entId)).thenReturn(false);

        // Act & Assert
        boolean result1 = changeThirdStateService.changeThirdState(thId1, entId);
        assertTrue(result1);

        assertThrows(ThirdStateNotChanged.class, () -> {
            changeThirdStateService.changeThirdState(thId2, entId);
        });

        verify(thirdOutputPort).changeThirdState(thId1, entId);
        verify(thirdOutputPort).changeThirdState(thId2, entId);
    }

    @Test
    @DisplayName("Debe procesar cambios para diferentes combinaciones tercero-entidad")
    void testChangeThirdState_DifferentThirdEntityCombinations() {
        // Arrange
        Long thId1 = 1L;
        Long thId2 = 2L;
        String entId1 = "ENT001";
        String entId2 = "ENT002";

        when(thirdOutputPort.changeThirdState(thId1, entId1)).thenReturn(true);
        when(thirdOutputPort.changeThirdState(thId1, entId2)).thenReturn(true);
        when(thirdOutputPort.changeThirdState(thId2, entId1)).thenReturn(true);
        when(thirdOutputPort.changeThirdState(thId2, entId2)).thenReturn(true);

        // Act
        boolean result1 = changeThirdStateService.changeThirdState(thId1, entId1);
        boolean result2 = changeThirdStateService.changeThirdState(thId1, entId2);
        boolean result3 = changeThirdStateService.changeThirdState(thId2, entId1);
        boolean result4 = changeThirdStateService.changeThirdState(thId2, entId2);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
        assertTrue(result4);
        verify(thirdOutputPort, times(4)).changeThirdState(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe mantener consistencia en múltiples llamadas exitosas")
    void testChangeThirdState_ConsistencyInMultipleCalls() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(true);

        // Act
        boolean result1 = changeThirdStateService.changeThirdState(thId, entId);
        boolean result2 = changeThirdStateService.changeThirdState(thId, entId);
        boolean result3 = changeThirdStateService.changeThirdState(thId, entId);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
        verify(thirdOutputPort, times(3)).changeThirdState(thId, entId);
    }

    @Test
    @DisplayName("Debe lanzar excepción consistentemente cuando siempre falla")
    void testChangeThirdState_ConsistentFailure() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(false);

        // Act & Assert
        assertThrows(ThirdStateNotChanged.class, () -> {
            changeThirdStateService.changeThirdState(thId, entId);
        });

        assertThrows(ThirdStateNotChanged.class, () -> {
            changeThirdStateService.changeThirdState(thId, entId);
        });

        assertThrows(ThirdStateNotChanged.class, () -> {
            changeThirdStateService.changeThirdState(thId, entId);
        });

        verify(thirdOutputPort, times(3)).changeThirdState(thId, entId);
    }

    @Test
    @DisplayName("Debe procesar correctamente el primer cambio de estado")
    void testChangeThirdState_FirstTimeChange() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(true);

        // Act
        boolean result = changeThirdStateService.changeThirdState(thId, entId);

        // Assert
        assertTrue(result);
        verify(thirdOutputPort, times(1)).changeThirdState(thId, entId);
    }

    @Test
    @DisplayName("Debe validar que el resultado del output port se propague correctamente")
    void testChangeThirdState_OutputPortResultPropagation() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(true);

        // Act
        boolean result = changeThirdStateService.changeThirdState(thId, entId);

        // Assert
        assertTrue(result);
        assertEquals(true, result);
    }

    @Test
    @DisplayName("Debe lanzar excepción antes de retornar cuando falla")
    void testChangeThirdState_ExceptionBeforeReturn() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(false);

        // Act & Assert
        ThirdStateNotChanged exception = assertThrows(ThirdStateNotChanged.class, () -> {
            changeThirdStateService.changeThirdState(thId, entId);
        });

        assertNotNull(exception);
        verify(thirdOutputPort).changeThirdState(thId, entId);
    }

    @Test
    @DisplayName("Debe verificar que no hay interacciones adicionales después del cambio exitoso")
    void testChangeThirdState_NoAdditionalInteractionsOnSuccess() {
        // Arrange
        when(thirdOutputPort.changeThirdState(thId, entId)).thenReturn(true);

        // Act
        changeThirdStateService.changeThirdState(thId, entId);

        // Assert
        verify(thirdOutputPort, times(1)).changeThirdState(thId, entId);
        verifyNoMoreInteractions(thirdOutputPort);
    }
}
