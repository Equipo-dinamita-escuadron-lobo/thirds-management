package com.thirdsmanagement.thirds.unit.application.service.third;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.application.service.third.DeleteThirdService;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdInUseException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdNotFound;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;

/**
 * Tests unitarios para DeleteThirdService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeleteThirdServiceUnitTest {

    @Mock
    private ThirdOutputPort thirdOutputPort;

    @InjectMocks
    private DeleteThirdService deleteThirdService;

    private Long thirdId;
    private String entId;
    private Third third;
    private TypeId typeId;
    private ThirdType thirdType;

    @BeforeEach
    void setUp() {
        thirdId = 1L;
        entId = "ENT001";

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
                .thId(thirdId)
                .entId(entId)
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .thirdTypes(Set.of(thirdType))
                .names("Juan Carlos")
                .lastNames("Pérez Gómez")
                .idNumber(123456789L)
                .state(true)
                .usageCount(0)
                .build();
    }

    // ========== deleteThird Tests - Eliminación Exitosa ==========

    @Test
    @DisplayName("Debe eliminar tercero exitosamente cuando no está en uso")
    void testDeleteThird_SuccessfulDeletion() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));
        when(thirdOutputPort.deleteThird(thirdId, entId)).thenReturn(true);

        // Act
        boolean result = deleteThirdService.deleteThird(thirdId, entId);

        // Assert
        assertTrue(result);
        verify(thirdOutputPort).existThirdById(thirdId, entId);
        verify(thirdOutputPort).getThirdById(thirdId, entId);
        verify(thirdOutputPort).deleteThird(thirdId, entId);
    }

    @Test
    @DisplayName("Debe retornar true cuando la eliminación es exitosa")
    void testDeleteThird_ReturnsTrue() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));
        when(thirdOutputPort.deleteThird(thirdId, entId)).thenReturn(true);

        // Act
        boolean result = deleteThirdService.deleteThird(thirdId, entId);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe eliminar tercero con usageCount en cero")
    void testDeleteThird_WithZeroUsageCount() {
        // Arrange
        third.setUsageCount(0);

        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));
        when(thirdOutputPort.deleteThird(thirdId, entId)).thenReturn(true);

        // Act
        boolean result = deleteThirdService.deleteThird(thirdId, entId);

        // Assert
        assertTrue(result);
        verify(thirdOutputPort).deleteThird(thirdId, entId);
    }

    @Test
    @DisplayName("Debe eliminar tercero con usageCount null")
    void testDeleteThird_WithNullUsageCount() {
        // Arrange
        third.setUsageCount(null);

        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));
        when(thirdOutputPort.deleteThird(thirdId, entId)).thenReturn(true);

        // Act
        boolean result = deleteThirdService.deleteThird(thirdId, entId);

        // Assert
        assertTrue(result);
        verify(thirdOutputPort).deleteThird(thirdId, entId);
    }

    // ========== deleteThird Tests - Validación Tercero No Existe ==========

    @Test
    @DisplayName("Debe lanzar excepción cuando tercero no existe en validación inicial")
    void testDeleteThird_ThrowsExceptionWhenThirdNotExists() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(false);

        // Act & Assert
        ThirdNotFound exception = assertThrows(ThirdNotFound.class, () -> {
            deleteThirdService.deleteThird(thirdId, entId);
        });

        assertTrue(exception.getMessage().contains("no existe"));
        verify(thirdOutputPort).existThirdById(thirdId, entId);
        verify(thirdOutputPort, never()).getThirdById(anyLong(), anyString());
        verify(thirdOutputPort, never()).deleteThird(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando getThirdById retorna Optional vacío")
    void testDeleteThird_ThrowsExceptionWhenGetThirdByIdReturnsEmpty() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.empty());

        // Act & Assert
        ThirdNotFound exception = assertThrows(ThirdNotFound.class, () -> {
            deleteThirdService.deleteThird(thirdId, entId);
        });

        assertTrue(exception.getMessage().contains("no existe"));
        verify(thirdOutputPort).existThirdById(thirdId, entId);
        verify(thirdOutputPort).getThirdById(thirdId, entId);
        verify(thirdOutputPort, never()).deleteThird(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe incluir entId en mensaje de error cuando tercero no existe")
    void testDeleteThird_IncludesEntIdInErrorMessage() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(false);

        // Act & Assert
        ThirdNotFound exception = assertThrows(ThirdNotFound.class, () -> {
            deleteThirdService.deleteThird(thirdId, entId);
        });

        assertTrue(exception.getMessage().contains(entId));
        assertTrue(exception.getMessage().contains(thirdId.toString()));
    }

    // ========== deleteThird Tests - Validación Tercero En Uso ==========

    @Test
    @DisplayName("Debe lanzar excepción cuando tercero está en uso")
    void testDeleteThird_ThrowsExceptionWhenThirdInUse() {
        // Arrange
        third.setUsageCount(5);

        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));

        // Act & Assert
        ThirdInUseException exception = assertThrows(ThirdInUseException.class, () -> {
            deleteThirdService.deleteThird(thirdId, entId);
        });

        assertNotNull(exception);
        verify(thirdOutputPort).existThirdById(thirdId, entId);
        verify(thirdOutputPort).getThirdById(thirdId, entId);
        verify(thirdOutputPort, never()).deleteThird(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando usageCount es 1")
    void testDeleteThird_ThrowsExceptionWhenUsageCountIsOne() {
        // Arrange
        third.setUsageCount(1);

        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));

        // Act & Assert
        assertThrows(ThirdInUseException.class, () -> {
            deleteThirdService.deleteThird(thirdId, entId);
        });

        verify(thirdOutputPort, never()).deleteThird(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando usageCount es alto")
    void testDeleteThird_ThrowsExceptionWhenHighUsageCount() {
        // Arrange
        third.setUsageCount(100);

        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));

        // Act & Assert
        assertThrows(ThirdInUseException.class, () -> {
            deleteThirdService.deleteThird(thirdId, entId);
        });
    }

    @Test
    @DisplayName("Debe verificar isInUse correctamente")
    void testDeleteThird_ChecksIsInUseCorrectly() {
        // Arrange
        third.setUsageCount(3);

        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));

        // Act & Assert
        assertThrows(ThirdInUseException.class, () -> {
            deleteThirdService.deleteThird(thirdId, entId);
        });

        assertTrue(third.isInUse());
    }

    // ========== deleteThird Tests - Diferentes Terceros ==========

    @Test
    @DisplayName("Debe eliminar terceros con diferentes IDs")
    void testDeleteThird_DifferentThirdIds() {
        // Arrange
        Long thirdId1 = 1L;
        Long thirdId2 = 2L;

        Third third1 = Third.builder().thId(thirdId1).entId(entId).idNumber(123456789L).usageCount(0).build();
        Third third2 = Third.builder().thId(thirdId2).entId(entId).idNumber(987654321L).usageCount(0).build();

        when(thirdOutputPort.existThirdById(thirdId1, entId)).thenReturn(true);
        when(thirdOutputPort.existThirdById(thirdId2, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId1, entId)).thenReturn(Optional.of(third1));
        when(thirdOutputPort.getThirdById(thirdId2, entId)).thenReturn(Optional.of(third2));
        when(thirdOutputPort.deleteThird(thirdId1, entId)).thenReturn(true);
        when(thirdOutputPort.deleteThird(thirdId2, entId)).thenReturn(true);

        // Act
        boolean result1 = deleteThirdService.deleteThird(thirdId1, entId);
        boolean result2 = deleteThirdService.deleteThird(thirdId2, entId);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        verify(thirdOutputPort).deleteThird(thirdId1, entId);
        verify(thirdOutputPort).deleteThird(thirdId2, entId);
    }

    @Test
    @DisplayName("Debe eliminar terceros de diferentes entidades")
    void testDeleteThird_DifferentEntities() {
        // Arrange
        String entId1 = "ENT001";
        String entId2 = "ENT002";

        Third third1 = Third.builder().thId(thirdId).entId(entId1).idNumber(123456789L).usageCount(0).build();
        Third third2 = Third.builder().thId(thirdId).entId(entId2).idNumber(123456789L).usageCount(0).build();

        when(thirdOutputPort.existThirdById(thirdId, entId1)).thenReturn(true);
        when(thirdOutputPort.existThirdById(thirdId, entId2)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId1)).thenReturn(Optional.of(third1));
        when(thirdOutputPort.getThirdById(thirdId, entId2)).thenReturn(Optional.of(third2));
        when(thirdOutputPort.deleteThird(thirdId, entId1)).thenReturn(true);
        when(thirdOutputPort.deleteThird(thirdId, entId2)).thenReturn(true);

        // Act
        boolean result1 = deleteThirdService.deleteThird(thirdId, entId1);
        boolean result2 = deleteThirdService.deleteThird(thirdId, entId2);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
    }

    @Test
    @DisplayName("Debe manejar IDs de terceros grandes")
    void testDeleteThird_LargeThirdIds() {
        // Arrange
        Long largeThirdId = 999999999L;
        Third largeThird = Third.builder().thId(largeThirdId).entId(entId).idNumber(123456789L).usageCount(0).build();

        when(thirdOutputPort.existThirdById(largeThirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(largeThirdId, entId)).thenReturn(Optional.of(largeThird));
        when(thirdOutputPort.deleteThird(largeThirdId, entId)).thenReturn(true);

        // Act
        boolean result = deleteThirdService.deleteThird(largeThirdId, entId);

        // Assert
        assertTrue(result);
    }

    // ========== deleteThird Tests - Orden de Validaciones ==========

    @Test
    @DisplayName("Debe ejecutar validaciones en orden correcto")
    void testDeleteThird_ExecutesValidationsInOrder() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));
        when(thirdOutputPort.deleteThird(thirdId, entId)).thenReturn(true);

        // Act
        deleteThirdService.deleteThird(thirdId, entId);

        // Assert
        var inOrder = inOrder(thirdOutputPort);
        inOrder.verify(thirdOutputPort).existThirdById(thirdId, entId);
        inOrder.verify(thirdOutputPort).getThirdById(thirdId, entId);
        inOrder.verify(thirdOutputPort).deleteThird(thirdId, entId);
    }

    @Test
    @DisplayName("Debe detener flujo si tercero no existe en primera validación")
    void testDeleteThird_StopsIfThirdNotExistsInFirstValidation() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(false);

        // Act & Assert
        assertThrows(ThirdNotFound.class, () -> {
            deleteThirdService.deleteThird(thirdId, entId);
        });

        verify(thirdOutputPort).existThirdById(thirdId, entId);
        verify(thirdOutputPort, never()).getThirdById(anyLong(), anyString());
        verify(thirdOutputPort, never()).deleteThird(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe detener flujo si tercero está en uso")
    void testDeleteThird_StopsIfThirdInUse() {
        // Arrange
        third.setUsageCount(5);

        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));

        // Act & Assert
        assertThrows(ThirdInUseException.class, () -> {
            deleteThirdService.deleteThird(thirdId, entId);
        });

        verify(thirdOutputPort).existThirdById(thirdId, entId);
        verify(thirdOutputPort).getThirdById(thirdId, entId);
        verify(thirdOutputPort, never()).deleteThird(anyLong(), anyString());
    }

    // ========== deleteThird Tests - Valor de Retorno ==========

    @Test
    @DisplayName("Debe retornar false cuando deleteThird retorna false")
    void testDeleteThird_ReturnsFalseWhenDeleteFails() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));
        when(thirdOutputPort.deleteThird(thirdId, entId)).thenReturn(false);

        // Act
        boolean result = deleteThirdService.deleteThird(thirdId, entId);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe propagar el resultado del output port correctamente")
    void testDeleteThird_PropagatesOutputPortResult() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));
        when(thirdOutputPort.deleteThird(thirdId, entId)).thenReturn(true);

        // Act
        boolean result = deleteThirdService.deleteThird(thirdId, entId);

        // Assert
        assertTrue(result);
        verify(thirdOutputPort).deleteThird(thirdId, entId);
    }

    @Test
    @DisplayName("Debe retornar el valor booleano correcto del output port")
    void testDeleteThird_ReturnsBooleanFromOutputPort() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));
        when(thirdOutputPort.deleteThird(thirdId, entId)).thenReturn(true);

        // Act
        boolean result = deleteThirdService.deleteThird(thirdId, entId);

        // Assert
        assertEquals(true, result);
    }

    // ========== deleteThird Tests - Propagación de Excepciones ==========

    @Test
    @DisplayName("Debe propagar excepción del output port durante existThirdById")
    void testDeleteThird_PropagatesExceptionFromExistThirdById() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId))
                .thenThrow(new RuntimeException("Error en base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            deleteThirdService.deleteThird(thirdId, entId);
        });

        verify(thirdOutputPort, never()).deleteThird(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe propagar excepción del output port durante getThirdById")
    void testDeleteThird_PropagatesExceptionFromGetThirdById() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId))
                .thenThrow(new RuntimeException("Error al obtener tercero"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            deleteThirdService.deleteThird(thirdId, entId);
        });

        verify(thirdOutputPort, never()).deleteThird(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe propagar excepción del output port durante deleteThird")
    void testDeleteThird_PropagatesExceptionFromDeleteThird() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));
        when(thirdOutputPort.deleteThird(thirdId, entId))
                .thenThrow(new RuntimeException("Error al eliminar"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            deleteThirdService.deleteThird(thirdId, entId);
        });
    }

    // ========== Integration Scenario Tests ==========

    @Test
    @DisplayName("Debe completar flujo completo de eliminación exitosa")
    void testDeleteThird_CompleteSuccessfulFlow() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));
        when(thirdOutputPort.deleteThird(thirdId, entId)).thenReturn(true);

        // Act
        boolean result = deleteThirdService.deleteThird(thirdId, entId);

        // Assert
        assertTrue(result);
        verify(thirdOutputPort, times(1)).existThirdById(thirdId, entId);
        verify(thirdOutputPort, times(1)).getThirdById(thirdId, entId);
        verify(thirdOutputPort, times(1)).deleteThird(thirdId, entId);
        verifyNoMoreInteractions(thirdOutputPort);
    }

    @Test
    @DisplayName("Debe manejar escenario de eliminación de múltiples terceros")
    void testDeleteThird_MultipleThirdsDeleteSequence() {
        // Arrange
        Long thirdId1 = 1L;
        Long thirdId2 = 2L;
        Long thirdId3 = 3L;

        Third third1 = Third.builder().thId(thirdId1).entId(entId).usageCount(0).build();
        Third third2 = Third.builder().thId(thirdId2).entId(entId).usageCount(0).build();
        Third third3 = Third.builder().thId(thirdId3).entId(entId).usageCount(0).build();

        when(thirdOutputPort.existThirdById(thirdId1, entId)).thenReturn(true);
        when(thirdOutputPort.existThirdById(thirdId2, entId)).thenReturn(true);
        when(thirdOutputPort.existThirdById(thirdId3, entId)).thenReturn(true);

        when(thirdOutputPort.getThirdById(thirdId1, entId)).thenReturn(Optional.of(third1));
        when(thirdOutputPort.getThirdById(thirdId2, entId)).thenReturn(Optional.of(third2));
        when(thirdOutputPort.getThirdById(thirdId3, entId)).thenReturn(Optional.of(third3));

        when(thirdOutputPort.deleteThird(thirdId1, entId)).thenReturn(true);
        when(thirdOutputPort.deleteThird(thirdId2, entId)).thenReturn(true);
        when(thirdOutputPort.deleteThird(thirdId3, entId)).thenReturn(true);

        // Act
        boolean result1 = deleteThirdService.deleteThird(thirdId1, entId);
        boolean result2 = deleteThirdService.deleteThird(thirdId2, entId);
        boolean result3 = deleteThirdService.deleteThird(thirdId3, entId);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
        verify(thirdOutputPort, times(3)).deleteThird(anyLong(), eq(entId));
    }

    @Test
    @DisplayName("Debe validar tercero antes de cada eliminación")
    void testDeleteThird_ValidatesBeforeEachDeletion() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));
        when(thirdOutputPort.deleteThird(thirdId, entId)).thenReturn(true);

        // Act
        deleteThirdService.deleteThird(thirdId, entId);

        // Assert
        verify(thirdOutputPort).existThirdById(thirdId, entId);
        verify(thirdOutputPort).getThirdById(thirdId, entId);
        verify(thirdOutputPort).deleteThird(thirdId, entId);
    }

    @Test
    @DisplayName("Debe invocar métodos del output port con parámetros correctos")
    void testDeleteThird_InvokesWithCorrectParameters() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));
        when(thirdOutputPort.deleteThird(thirdId, entId)).thenReturn(true);

        // Act
        deleteThirdService.deleteThird(thirdId, entId);

        // Assert
        verify(thirdOutputPort).existThirdById(eq(thirdId), eq(entId));
        verify(thirdOutputPort).getThirdById(eq(thirdId), eq(entId));
        verify(thirdOutputPort).deleteThird(eq(thirdId), eq(entId));
    }

    @Test
    @DisplayName("Debe manejar correctamente el caso de tercero sin uso")
    void testDeleteThird_HandlesThirdWithoutUsage() {
        // Arrange
        assertFalse(third.isInUse());

        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));
        when(thirdOutputPort.deleteThird(thirdId, entId)).thenReturn(true);

        // Act
        boolean result = deleteThirdService.deleteThird(thirdId, entId);

        // Assert
        assertTrue(result);
        assertFalse(third.isInUse());
    }
}
