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
import com.thirdsmanagement.thirds.application.service.third.GetThirdService;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdNotFound;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;

/**
 * Tests unitarios para GetThirdService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetThirdServiceUnitTest {

    @Mock
    private ThirdOutputPort thirdOutputPort;

    @InjectMocks
    private GetThirdService getThirdService;

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
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("juan@email.com")
                .build();
    }

    // ========== getThirdById Tests - Búsqueda Exitosa ==========

    @Test
    @DisplayName("Debe obtener tercero por ID exitosamente")
    void testGetThirdById_SuccessfulRetrieval() {
        // Arrange
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));

        // Act
        Third result = getThirdService.getThirdById(thirdId, entId);

        // Assert
        assertNotNull(result);
        assertEquals(thirdId, result.getThId());
        assertEquals(entId, result.getEntId());
        verify(thirdOutputPort).getThirdById(thirdId, entId);
    }

    @Test
    @DisplayName("Debe retornar tercero con todos sus datos")
    void testGetThirdById_ReturnsCompleteThird() {
        // Arrange
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));

        // Act
        Third result = getThirdService.getThirdById(thirdId, entId);

        // Assert
        assertNotNull(result);
        assertEquals("Juan Carlos", result.getNames());
        assertEquals("Pérez Gómez", result.getLastNames());
        assertEquals(123456789L, result.getIdNumber());
        assertEquals(ePersonType.Natural, result.getPersonType());
        assertEquals("Calle 123", result.getAddress());
        assertEquals("3001234567", result.getPhoneNumber());
        assertEquals("juan@email.com", result.getEmail());
    }

    @Test
    @DisplayName("Debe invocar output port con parámetros correctos")
    void testGetThirdById_InvokesOutputPortWithCorrectParams() {
        // Arrange
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));

        // Act
        getThirdService.getThirdById(thirdId, entId);

        // Assert
        verify(thirdOutputPort).getThirdById(eq(thirdId), eq(entId));
    }

    @Test
    @DisplayName("Debe invocar output port una sola vez")
    void testGetThirdById_InvokesOutputPortOnce() {
        // Arrange
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));

        // Act
        getThirdService.getThirdById(thirdId, entId);

        // Assert
        verify(thirdOutputPort, times(1)).getThirdById(anyLong(), anyString());
    }

    // ========== getThirdById Tests - Tercero No Encontrado ==========

    @Test
    @DisplayName("Debe lanzar excepción cuando tercero no existe")
    void testGetThirdById_ThrowsExceptionWhenNotFound() {
        // Arrange
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.empty());

        // Act & Assert
        ThirdNotFound exception = assertThrows(ThirdNotFound.class, () -> {
            getThirdService.getThirdById(thirdId, entId);
        });

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("no encontrado"));
        verify(thirdOutputPort).getThirdById(thirdId, entId);
    }

    @Test
    @DisplayName("Debe incluir ID en mensaje de error")
    void testGetThirdById_IncludesIdInErrorMessage() {
        // Arrange
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.empty());

        // Act & Assert
        ThirdNotFound exception = assertThrows(ThirdNotFound.class, () -> {
            getThirdService.getThirdById(thirdId, entId);
        });

        assertTrue(exception.getMessage().contains(thirdId.toString()));
    }

    @Test
    @DisplayName("Debe incluir entId en mensaje de error")
    void testGetThirdById_IncludesEntIdInErrorMessage() {
        // Arrange
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.empty());

        // Act & Assert
        ThirdNotFound exception = assertThrows(ThirdNotFound.class, () -> {
            getThirdService.getThirdById(thirdId, entId);
        });

        assertTrue(exception.getMessage().contains(entId));
    }

    @Test
    @DisplayName("Debe lanzar ThirdNotFound con mensaje descriptivo")
    void testGetThirdById_ThrowsDescriptiveException() {
        // Arrange
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.empty());

        // Act & Assert
        ThirdNotFound exception = assertThrows(ThirdNotFound.class, () -> {
            getThirdService.getThirdById(thirdId, entId);
        });

        String message = exception.getMessage();
        assertTrue(message.contains("Tercero"));
        assertTrue(message.contains(thirdId.toString()));
        assertTrue(message.contains(entId));
    }

    // ========== getThirdById Tests - Diferentes Terceros ==========

    @Test
    @DisplayName("Debe obtener terceros con diferentes IDs")
    void testGetThirdById_DifferentThirdIds() {
        // Arrange
        Long thirdId1 = 1L;
        Long thirdId2 = 2L;

        Third third1 = Third.builder().thId(thirdId1).entId(entId).names("Juan").build();
        Third third2 = Third.builder().thId(thirdId2).entId(entId).names("María").build();

        when(thirdOutputPort.getThirdById(thirdId1, entId)).thenReturn(Optional.of(third1));
        when(thirdOutputPort.getThirdById(thirdId2, entId)).thenReturn(Optional.of(third2));

        // Act
        Third result1 = getThirdService.getThirdById(thirdId1, entId);
        Third result2 = getThirdService.getThirdById(thirdId2, entId);

        // Assert
        assertEquals("Juan", result1.getNames());
        assertEquals("María", result2.getNames());
        verify(thirdOutputPort).getThirdById(thirdId1, entId);
        verify(thirdOutputPort).getThirdById(thirdId2, entId);
    }

    @Test
    @DisplayName("Debe obtener terceros de diferentes entidades")
    void testGetThirdById_DifferentEntities() {
        // Arrange
        String entId1 = "ENT001";
        String entId2 = "ENT002";

        Third third1 = Third.builder().thId(thirdId).entId(entId1).names("Carlos").build();
        Third third2 = Third.builder().thId(thirdId).entId(entId2).names("Pedro").build();

        when(thirdOutputPort.getThirdById(thirdId, entId1)).thenReturn(Optional.of(third1));
        when(thirdOutputPort.getThirdById(thirdId, entId2)).thenReturn(Optional.of(third2));

        // Act
        Third result1 = getThirdService.getThirdById(thirdId, entId1);
        Third result2 = getThirdService.getThirdById(thirdId, entId2);

        // Assert
        assertEquals(entId1, result1.getEntId());
        assertEquals(entId2, result2.getEntId());
    }

    @Test
    @DisplayName("Debe manejar IDs de terceros grandes")
    void testGetThirdById_LargeThirdIds() {
        // Arrange
        Long largeThirdId = 999999999L;
        Third largeThird = Third.builder().thId(largeThirdId).entId(entId).build();

        when(thirdOutputPort.getThirdById(largeThirdId, entId)).thenReturn(Optional.of(largeThird));

        // Act
        Third result = getThirdService.getThirdById(largeThirdId, entId);

        // Assert
        assertNotNull(result);
        assertEquals(largeThirdId, result.getThId());
    }

    // ========== getThirdById Tests - Propagación de Excepciones ==========

    @Test
    @DisplayName("Debe propagar excepción del output port")
    void testGetThirdById_PropagatesOutputPortException() {
        // Arrange
        when(thirdOutputPort.getThirdById(thirdId, entId))
                .thenThrow(new RuntimeException("Error en base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            getThirdService.getThirdById(thirdId, entId);
        });

        verify(thirdOutputPort).getThirdById(thirdId, entId);
    }

    @Test
    @DisplayName("Debe propagar excepción de tipo específico")
    void testGetThirdById_PropagatesSpecificException() {
        // Arrange
        IllegalStateException exception = new IllegalStateException("Estado inválido");
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenThrow(exception);

        // Act & Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            getThirdService.getThirdById(thirdId, entId);
        });

        assertEquals("Estado inválido", thrown.getMessage());
    }

    @Test
    @DisplayName("Debe propagar NullPointerException del output port")
    void testGetThirdById_PropagatesNullPointerException() {
        // Arrange
        when(thirdOutputPort.getThirdById(thirdId, entId))
                .thenThrow(new NullPointerException("Parámetro nulo"));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            getThirdService.getThirdById(thirdId, entId);
        });
    }

    // ========== existThirdById Tests - Existencia True ==========

    @Test
    @DisplayName("Debe retornar true cuando tercero existe")
    void testExistThirdById_ReturnsTrueWhenExists() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);

        // Act
        boolean result = getThirdService.existThirdById(thirdId, entId);

        // Assert
        assertTrue(result);
        verify(thirdOutputPort).existThirdById(thirdId, entId);
    }

    @Test
    @DisplayName("Debe invocar output port con parámetros correctos para existencia")
    void testExistThirdById_InvokesOutputPortWithCorrectParams() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);

        // Act
        getThirdService.existThirdById(thirdId, entId);

        // Assert
        verify(thirdOutputPort).existThirdById(eq(thirdId), eq(entId));
    }

    @Test
    @DisplayName("Debe invocar output port una sola vez para existencia")
    void testExistThirdById_InvokesOutputPortOnce() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);

        // Act
        getThirdService.existThirdById(thirdId, entId);

        // Assert
        verify(thirdOutputPort, times(1)).existThirdById(anyLong(), anyString());
    }

    // ========== existThirdById Tests - Existencia False ==========

    @Test
    @DisplayName("Debe retornar false cuando tercero no existe")
    void testExistThirdById_ReturnsFalseWhenNotExists() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(false);

        // Act
        boolean result = getThirdService.existThirdById(thirdId, entId);

        // Assert
        assertFalse(result);
        verify(thirdOutputPort).existThirdById(thirdId, entId);
    }

    @Test
    @DisplayName("Debe no lanzar excepción cuando tercero no existe en existThirdById")
    void testExistThirdById_DoesNotThrowWhenNotExists() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(false);

        // Act & Assert
        assertDoesNotThrow(() -> {
            getThirdService.existThirdById(thirdId, entId);
        });
    }

    @Test
    @DisplayName("Debe retornar valor booleano correcto del output port")
    void testExistThirdById_ReturnsBooleanFromOutputPort() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);

        // Act
        boolean result = getThirdService.existThirdById(thirdId, entId);

        // Assert
        assertEquals(true, result);
    }

    // ========== existThirdById Tests - Diferentes Terceros ==========

    @Test
    @DisplayName("Debe validar existencia de terceros con diferentes IDs")
    void testExistThirdById_DifferentThirdIds() {
        // Arrange
        Long thirdId1 = 1L;
        Long thirdId2 = 2L;

        when(thirdOutputPort.existThirdById(thirdId1, entId)).thenReturn(true);
        when(thirdOutputPort.existThirdById(thirdId2, entId)).thenReturn(false);

        // Act
        boolean result1 = getThirdService.existThirdById(thirdId1, entId);
        boolean result2 = getThirdService.existThirdById(thirdId2, entId);

        // Assert
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    @DisplayName("Debe validar existencia en diferentes entidades")
    void testExistThirdById_DifferentEntities() {
        // Arrange
        String entId1 = "ENT001";
        String entId2 = "ENT002";

        when(thirdOutputPort.existThirdById(thirdId, entId1)).thenReturn(true);
        when(thirdOutputPort.existThirdById(thirdId, entId2)).thenReturn(false);

        // Act
        boolean result1 = getThirdService.existThirdById(thirdId, entId1);
        boolean result2 = getThirdService.existThirdById(thirdId, entId2);

        // Assert
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    @DisplayName("Debe manejar IDs grandes en validación de existencia")
    void testExistThirdById_LargeThirdIds() {
        // Arrange
        Long largeThirdId = 999999999L;
        when(thirdOutputPort.existThirdById(largeThirdId, entId)).thenReturn(true);

        // Act
        boolean result = getThirdService.existThirdById(largeThirdId, entId);

        // Assert
        assertTrue(result);
    }

    // ========== existThirdById Tests - Propagación de Excepciones ==========

    @Test
    @DisplayName("Debe propagar excepción del output port en existencia")
    void testExistThirdById_PropagatesOutputPortException() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId))
                .thenThrow(new RuntimeException("Error en base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            getThirdService.existThirdById(thirdId, entId);
        });
    }

    @Test
    @DisplayName("Debe propagar excepción específica en existencia")
    void testExistThirdById_PropagatesSpecificException() {
        // Arrange
        IllegalStateException exception = new IllegalStateException("Estado inválido");
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenThrow(exception);

        // Act & Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            getThirdService.existThirdById(thirdId, entId);
        });

        assertEquals("Estado inválido", thrown.getMessage());
    }

    @Test
    @DisplayName("Debe propagar NullPointerException en existencia")
    void testExistThirdById_PropagatesNullPointerException() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId))
                .thenThrow(new NullPointerException("Parámetro nulo"));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            getThirdService.existThirdById(thirdId, entId);
        });
    }

    // ========== Integration Scenario Tests ==========

    @Test
    @DisplayName("Debe delegar completamente al output port en getThirdById")
    void testGetThirdById_DelegatesToOutputPort() {
        // Arrange
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));

        // Act
        getThirdService.getThirdById(thirdId, entId);

        // Assert
        verify(thirdOutputPort).getThirdById(thirdId, entId);
        verifyNoMoreInteractions(thirdOutputPort);
    }

    @Test
    @DisplayName("Debe delegar completamente al output port en existThirdById")
    void testExistThirdById_DelegatesToOutputPort() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);

        // Act
        getThirdService.existThirdById(thirdId, entId);

        // Assert
        verify(thirdOutputPort).existThirdById(thirdId, entId);
        verifyNoMoreInteractions(thirdOutputPort);
    }

    @Test
    @DisplayName("Debe obtener múltiples terceros en secuencia")
    void testGetThirdById_MultipleSequentialCalls() {
        // Arrange
        Long thirdId1 = 1L;
        Long thirdId2 = 2L;
        Long thirdId3 = 3L;

        Third third1 = Third.builder().thId(thirdId1).entId(entId).build();
        Third third2 = Third.builder().thId(thirdId2).entId(entId).build();
        Third third3 = Third.builder().thId(thirdId3).entId(entId).build();

        when(thirdOutputPort.getThirdById(thirdId1, entId)).thenReturn(Optional.of(third1));
        when(thirdOutputPort.getThirdById(thirdId2, entId)).thenReturn(Optional.of(third2));
        when(thirdOutputPort.getThirdById(thirdId3, entId)).thenReturn(Optional.of(third3));

        // Act
        Third result1 = getThirdService.getThirdById(thirdId1, entId);
        Third result2 = getThirdService.getThirdById(thirdId2, entId);
        Third result3 = getThirdService.getThirdById(thirdId3, entId);

        // Assert
        assertEquals(thirdId1, result1.getThId());
        assertEquals(thirdId2, result2.getThId());
        assertEquals(thirdId3, result3.getThId());
        verify(thirdOutputPort, times(3)).getThirdById(anyLong(), eq(entId));
    }

    @Test
    @DisplayName("Debe validar existencia de múltiples terceros en secuencia")
    void testExistThirdById_MultipleSequentialCalls() {
        // Arrange
        Long thirdId1 = 1L;
        Long thirdId2 = 2L;
        Long thirdId3 = 3L;

        when(thirdOutputPort.existThirdById(thirdId1, entId)).thenReturn(true);
        when(thirdOutputPort.existThirdById(thirdId2, entId)).thenReturn(false);
        when(thirdOutputPort.existThirdById(thirdId3, entId)).thenReturn(true);

        // Act
        boolean result1 = getThirdService.existThirdById(thirdId1, entId);
        boolean result2 = getThirdService.existThirdById(thirdId2, entId);
        boolean result3 = getThirdService.existThirdById(thirdId3, entId);

        // Assert
        assertTrue(result1);
        assertFalse(result2);
        assertTrue(result3);
        verify(thirdOutputPort, times(3)).existThirdById(anyLong(), eq(entId));
    }

    @Test
    @DisplayName("Debe manejar llamadas alternadas entre getThirdById y existThirdById")
    void testMixedCalls_GetAndExist() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));

        // Act
        boolean exists = getThirdService.existThirdById(thirdId, entId);
        Third result = getThirdService.getThirdById(thirdId, entId);

        // Assert
        assertTrue(exists);
        assertNotNull(result);
        verify(thirdOutputPort).existThirdById(thirdId, entId);
        verify(thirdOutputPort).getThirdById(thirdId, entId);
    }

    @Test
    @DisplayName("Debe retornar mismo tercero en múltiples llamadas")
    void testGetThirdById_ConsistentReturns() {
        // Arrange
        when(thirdOutputPort.getThirdById(thirdId, entId)).thenReturn(Optional.of(third));

        // Act
        Third result1 = getThirdService.getThirdById(thirdId, entId);
        Third result2 = getThirdService.getThirdById(thirdId, entId);

        // Assert
        assertEquals(result1.getThId(), result2.getThId());
        assertEquals(result1.getIdNumber(), result2.getIdNumber());
        verify(thirdOutputPort, times(2)).getThirdById(thirdId, entId);
    }

    @Test
    @DisplayName("Debe retornar mismo valor de existencia en múltiples llamadas")
    void testExistThirdById_ConsistentReturns() {
        // Arrange
        when(thirdOutputPort.existThirdById(thirdId, entId)).thenReturn(true);

        // Act
        boolean result1 = getThirdService.existThirdById(thirdId, entId);
        boolean result2 = getThirdService.existThirdById(thirdId, entId);
        boolean result3 = getThirdService.existThirdById(thirdId, entId);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
        verify(thirdOutputPort, times(3)).existThirdById(thirdId, entId);
    }
}
