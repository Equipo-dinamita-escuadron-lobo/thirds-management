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
import com.thirdsmanagement.thirds.application.service.third.BulkChangeThirdStateService;

/**
 * Tests unitarios para BulkChangeThirdStateService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BulkChangeThirdStateServiceUnitTest {

    @Mock
    private ThirdOutputPort thirdOutputPort;

    @InjectMocks
    private BulkChangeThirdStateService bulkChangeThirdStateService;

    private String entId;

    @BeforeEach
    void setUp() {
        entId = "ENT001";
    }

    // ========== changeAllThirdsState Tests - Activar Terceros ==========

    @Test
    @DisplayName("Debe activar todos los terceros de una entidad")
    void testChangeAllThirdsState_ActivateAllThirds() {
        // Arrange
        Boolean newState = true;
        int expectedCount = 10;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(expectedCount);

        // Act
        int result = bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        assertEquals(expectedCount, result);
        verify(thirdOutputPort).bulkUpdateThirdState(entId, newState);
    }

    @Test
    @DisplayName("Debe retornar 0 cuando no hay terceros para activar")
    void testChangeAllThirdsState_ActivateWithNoRecords() {
        // Arrange
        Boolean newState = true;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(0);

        // Act
        int result = bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        assertEquals(0, result);
        verify(thirdOutputPort).bulkUpdateThirdState(entId, newState);
    }

    @Test
    @DisplayName("Debe activar múltiples terceros correctamente")
    void testChangeAllThirdsState_ActivateMultipleThirds() {
        // Arrange
        Boolean newState = true;
        int expectedCount = 50;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(expectedCount);

        // Act
        int result = bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        assertEquals(expectedCount, result);
        verify(thirdOutputPort, times(1)).bulkUpdateThirdState(entId, newState);
    }

    // ========== changeAllThirdsState Tests - Desactivar Terceros ==========

    @Test
    @DisplayName("Debe desactivar todos los terceros de una entidad")
    void testChangeAllThirdsState_DeactivateAllThirds() {
        // Arrange
        Boolean newState = false;
        int expectedCount = 15;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(expectedCount);

        // Act
        int result = bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        assertEquals(expectedCount, result);
        verify(thirdOutputPort).bulkUpdateThirdState(entId, newState);
    }

    @Test
    @DisplayName("Debe retornar 0 cuando no hay terceros para desactivar")
    void testChangeAllThirdsState_DeactivateWithNoRecords() {
        // Arrange
        Boolean newState = false;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(0);

        // Act
        int result = bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        assertEquals(0, result);
        verify(thirdOutputPort).bulkUpdateThirdState(entId, newState);
    }

    @Test
    @DisplayName("Debe desactivar múltiples terceros correctamente")
    void testChangeAllThirdsState_DeactivateMultipleThirds() {
        // Arrange
        Boolean newState = false;
        int expectedCount = 100;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(expectedCount);

        // Act
        int result = bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        assertEquals(expectedCount, result);
        verify(thirdOutputPort, times(1)).bulkUpdateThirdState(entId, newState);
    }

    // ========== changeAllThirdsState Tests - Diferentes Entidades ==========

    @Test
    @DisplayName("Debe procesar correctamente diferentes entidades")
    void testChangeAllThirdsState_DifferentEntities() {
        // Arrange
        String entId1 = "ENT001";
        String entId2 = "ENT002";
        Boolean newState = true;

        when(thirdOutputPort.bulkUpdateThirdState(entId1, newState)).thenReturn(10);
        when(thirdOutputPort.bulkUpdateThirdState(entId2, newState)).thenReturn(20);

        // Act
        int result1 = bulkChangeThirdStateService.changeAllThirdsState(entId1, newState);
        int result2 = bulkChangeThirdStateService.changeAllThirdsState(entId2, newState);

        // Assert
        assertEquals(10, result1);
        assertEquals(20, result2);
        verify(thirdOutputPort).bulkUpdateThirdState(entId1, newState);
        verify(thirdOutputPort).bulkUpdateThirdState(entId2, newState);
    }

    @Test
    @DisplayName("Debe manejar entidad sin terceros")
    void testChangeAllThirdsState_EntityWithoutThirds() {
        // Arrange
        String emptyEntId = "ENT999";
        Boolean newState = true;
        when(thirdOutputPort.bulkUpdateThirdState(emptyEntId, newState)).thenReturn(0);

        // Act
        int result = bulkChangeThirdStateService.changeAllThirdsState(emptyEntId, newState);

        // Assert
        assertEquals(0, result);
        verify(thirdOutputPort).bulkUpdateThirdState(emptyEntId, newState);
    }

    @Test
    @DisplayName("Debe procesar correctamente entidad con un solo tercero")
    void testChangeAllThirdsState_EntityWithSingleThird() {
        // Arrange
        Boolean newState = true;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(1);

        // Act
        int result = bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        assertEquals(1, result);
        verify(thirdOutputPort).bulkUpdateThirdState(entId, newState);
    }

    // ========== changeAllThirdsState Tests - Validación de Parámetros ==========

    @Test
    @DisplayName("Debe invocar el output port con parámetros correctos para activación")
    void testChangeAllThirdsState_InvokesOutputPortWithCorrectParamsActivate() {
        // Arrange
        Boolean newState = true;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(5);

        // Act
        bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        verify(thirdOutputPort).bulkUpdateThirdState(eq(entId), eq(true));
    }

    @Test
    @DisplayName("Debe invocar el output port con parámetros correctos para desactivación")
    void testChangeAllThirdsState_InvokesOutputPortWithCorrectParamsDeactivate() {
        // Arrange
        Boolean newState = false;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(5);

        // Act
        bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        verify(thirdOutputPort).bulkUpdateThirdState(eq(entId), eq(false));
    }

    @Test
    @DisplayName("Debe invocar el output port una sola vez por llamada")
    void testChangeAllThirdsState_InvokesOutputPortOnce() {
        // Arrange
        Boolean newState = true;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(10);

        // Act
        bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        verify(thirdOutputPort, times(1)).bulkUpdateThirdState(anyString(), anyBoolean());
    }

    // ========== changeAllThirdsState Tests - Valores de Retorno ==========

    @Test
    @DisplayName("Debe retornar el valor exacto del output port")
    void testChangeAllThirdsState_ReturnsExactValueFromOutputPort() {
        // Arrange
        Boolean newState = true;
        int expectedValue = 42;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(expectedValue);

        // Act
        int result = bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        assertEquals(expectedValue, result);
    }

    @Test
    @DisplayName("Debe retornar valores grandes correctamente")
    void testChangeAllThirdsState_ReturnsLargeValues() {
        // Arrange
        Boolean newState = true;
        int largeValue = 10000;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(largeValue);

        // Act
        int result = bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        assertEquals(largeValue, result);
        verify(thirdOutputPort).bulkUpdateThirdState(entId, newState);
    }

    @Test
    @DisplayName("Debe retornar valor negativo si el output port lo retorna")
    void testChangeAllThirdsState_ReturnsNegativeValue() {
        // Arrange
        Boolean newState = true;
        int negativeValue = -1;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(negativeValue);

        // Act
        int result = bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        assertEquals(negativeValue, result);
    }

    // ========== changeAllThirdsState Tests - Manejo de Excepciones ==========

    @Test
    @DisplayName("Debe propagar excepción cuando el output port falla")
    void testChangeAllThirdsState_PropagatesExceptionFromOutputPort() {
        // Arrange
        Boolean newState = true;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState))
                .thenThrow(new RuntimeException("Error en base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            bulkChangeThirdStateService.changeAllThirdsState(entId, newState);
        });

        verify(thirdOutputPort).bulkUpdateThirdState(entId, newState);
    }

    @Test
    @DisplayName("Debe propagar excepción de tipo específico")
    void testChangeAllThirdsState_PropagatesSpecificException() {
        // Arrange
        Boolean newState = false;
        IllegalStateException exception = new IllegalStateException("Estado inválido");
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenThrow(exception);

        // Act & Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            bulkChangeThirdStateService.changeAllThirdsState(entId, newState);
        });

        assertEquals("Estado inválido", thrown.getMessage());
        verify(thirdOutputPort).bulkUpdateThirdState(entId, newState);
    }

    @Test
    @DisplayName("Debe manejar excepción de null pointer del output port")
    void testChangeAllThirdsState_PropagatesNullPointerException() {
        // Arrange
        Boolean newState = true;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState))
                .thenThrow(new NullPointerException("Parámetro nulo"));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            bulkChangeThirdStateService.changeAllThirdsState(entId, newState);
        });
    }

    // ========== Integration Scenario Tests ==========

    @Test
    @DisplayName("Debe procesar múltiples cambios de estado consecutivos")
    void testChangeAllThirdsState_MultipleConsecutiveChanges() {
        // Arrange
        when(thirdOutputPort.bulkUpdateThirdState(entId, true)).thenReturn(10);
        when(thirdOutputPort.bulkUpdateThirdState(entId, false)).thenReturn(10);

        // Act
        int activateResult = bulkChangeThirdStateService.changeAllThirdsState(entId, true);
        int deactivateResult = bulkChangeThirdStateService.changeAllThirdsState(entId, false);

        // Assert
        assertEquals(10, activateResult);
        assertEquals(10, deactivateResult);
        verify(thirdOutputPort).bulkUpdateThirdState(entId, true);
        verify(thirdOutputPort).bulkUpdateThirdState(entId, false);
    }

    @Test
    @DisplayName("Debe procesar cambios para múltiples entidades en secuencia")
    void testChangeAllThirdsState_MultipleEntitiesSequential() {
        // Arrange
        String entId1 = "ENT001";
        String entId2 = "ENT002";
        String entId3 = "ENT003";
        Boolean newState = true;

        when(thirdOutputPort.bulkUpdateThirdState(entId1, newState)).thenReturn(5);
        when(thirdOutputPort.bulkUpdateThirdState(entId2, newState)).thenReturn(10);
        when(thirdOutputPort.bulkUpdateThirdState(entId3, newState)).thenReturn(15);

        // Act
        int result1 = bulkChangeThirdStateService.changeAllThirdsState(entId1, newState);
        int result2 = bulkChangeThirdStateService.changeAllThirdsState(entId2, newState);
        int result3 = bulkChangeThirdStateService.changeAllThirdsState(entId3, newState);

        // Assert
        assertEquals(5, result1);
        assertEquals(10, result2);
        assertEquals(15, result3);
        verify(thirdOutputPort, times(3)).bulkUpdateThirdState(anyString(), eq(newState));
    }

    @Test
    @DisplayName("Debe alternar estados correctamente en múltiples llamadas")
    void testChangeAllThirdsState_AlternatingStates() {
        // Arrange
        when(thirdOutputPort.bulkUpdateThirdState(entId, true)).thenReturn(10);
        when(thirdOutputPort.bulkUpdateThirdState(entId, false)).thenReturn(10);

        // Act & Assert
        assertEquals(10, bulkChangeThirdStateService.changeAllThirdsState(entId, true));
        assertEquals(10, bulkChangeThirdStateService.changeAllThirdsState(entId, false));
        assertEquals(10, bulkChangeThirdStateService.changeAllThirdsState(entId, true));
        assertEquals(10, bulkChangeThirdStateService.changeAllThirdsState(entId, false));

        verify(thirdOutputPort, times(2)).bulkUpdateThirdState(entId, true);
        verify(thirdOutputPort, times(2)).bulkUpdateThirdState(entId, false);
    }

    @Test
    @DisplayName("Debe procesar correctamente activación masiva de gran volumen")
    void testChangeAllThirdsState_LargeVolumeActivation() {
        // Arrange
        Boolean newState = true;
        int largeCount = 5000;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(largeCount);

        // Act
        int result = bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        assertEquals(largeCount, result);
        verify(thirdOutputPort).bulkUpdateThirdState(entId, newState);
    }

    @Test
    @DisplayName("Debe delegar completamente la operación al output port")
    void testChangeAllThirdsState_DelegatesToOutputPortCompletely() {
        // Arrange
        Boolean newState = true;
        when(thirdOutputPort.bulkUpdateThirdState(entId, newState)).thenReturn(25);

        // Act
        int result = bulkChangeThirdStateService.changeAllThirdsState(entId, newState);

        // Assert
        assertEquals(25, result);
        verify(thirdOutputPort).bulkUpdateThirdState(entId, newState);
        verifyNoMoreInteractions(thirdOutputPort);
    }
}
