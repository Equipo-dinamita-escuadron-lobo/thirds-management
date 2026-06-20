package com.thirdsmanagement.thirds.unit.application.service.thirdType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.service.thirdType.DeleteThirdTypeService;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeInUseException;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeNotFound;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeleteThirdTypeServiceUnitTest {

    @Mock
    private IdOutputPort idOutputPort;

    @InjectMocks
    private DeleteThirdTypeService deleteThirdTypeService;

    private Long thirdTypeId;
    private String entId;
    private ThirdType thirdType;

    @BeforeEach
    void setUp() {
        thirdTypeId = 1L;
        entId = "ENT001";
        thirdType = ThirdType.builder()
                .thirdTypeId(thirdTypeId)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(true)
                .build();
    }

    // ==================== Eliminación exitosa ====================

    @Test
    @DisplayName("Debe eliminar correctamente tipo de tercero válido y no en uso")
    void testDeleteThirdTypeTipoTerceroValidoYNoEnUsoEliminaCorrectamente() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(thirdType);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId)).thenReturn(false);
        when(idOutputPort.deleteThirdType(thirdTypeId, entId)).thenReturn(true);

        // Act
        boolean result = deleteThirdTypeService.deleteThirdType(thirdTypeId, entId);

        // Assert
        assertTrue(result);
        verify(idOutputPort).existsThirdTypeById(thirdTypeId);
        verify(idOutputPort).getThirdTypeById(thirdTypeId);
        verify(idOutputPort).isThirdTypeInUse(thirdTypeId, entId);
        verify(idOutputPort).deleteThirdType(thirdTypeId, entId);
    }

    @Test
    @DisplayName("Debe eliminar correctamente cuando estado es falso")
    void testDeleteThirdTypeStatusFalseEliminaCorrectamente() {
        // Arrange
        ThirdType inactiveThirdType = ThirdType.builder()
                .thirdTypeId(thirdTypeId)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(false)
                .build();

        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(inactiveThirdType);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId)).thenReturn(false);
        when(idOutputPort.deleteThirdType(thirdTypeId, entId)).thenReturn(true);

        // Act
        boolean result = deleteThirdTypeService.deleteThirdType(thirdTypeId, entId);

        // Assert
        assertTrue(result);
        verify(idOutputPort).deleteThirdType(thirdTypeId, entId);
    }

    @Test
    @DisplayName("Debe eliminar cada uno con diferentes nombres")
    void testDeleteThirdTypeDiferentesNombresEliminaCadaUno() {
        // Arrange
        Long thirdTypeId2 = 2L;
        ThirdType thirdType2 = ThirdType.builder()
                .thirdTypeId(thirdTypeId2)
                .thirdTypeName("Proveedor")
                .entId(entId)
                .status(true)
                .build();

        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(thirdType);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId)).thenReturn(false);
        when(idOutputPort.deleteThirdType(thirdTypeId, entId)).thenReturn(true);

        when(idOutputPort.existsThirdTypeById(thirdTypeId2)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId2)).thenReturn(thirdType2);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId2, entId)).thenReturn(false);
        when(idOutputPort.deleteThirdType(thirdTypeId2, entId)).thenReturn(true);

        // Act
        boolean result1 = deleteThirdTypeService.deleteThirdType(thirdTypeId, entId);
        boolean result2 = deleteThirdTypeService.deleteThirdType(thirdTypeId2, entId);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        verify(idOutputPort).deleteThirdType(thirdTypeId, entId);
        verify(idOutputPort).deleteThirdType(thirdTypeId2, entId);
    }

    @Test
    @DisplayName("Debe eliminar cada uno con diferentes entidades")
    void testDeleteThirdTypeDiferentesEntIdsEliminaCadaUno() {
        // Arrange
        String entId2 = "ENT002";
        ThirdType thirdTypeEnt2 = ThirdType.builder()
                .thirdTypeId(thirdTypeId)
                .thirdTypeName("Cliente")
                .entId(entId2)
                .status(true)
                .build();

        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId))
                .thenReturn(thirdType)
                .thenReturn(thirdTypeEnt2);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId)).thenReturn(false);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId2)).thenReturn(false);
        when(idOutputPort.deleteThirdType(thirdTypeId, entId)).thenReturn(true);
        when(idOutputPort.deleteThirdType(thirdTypeId, entId2)).thenReturn(true);

        // Act
        boolean result1 = deleteThirdTypeService.deleteThirdType(thirdTypeId, entId);
        boolean result2 = deleteThirdTypeService.deleteThirdType(thirdTypeId, entId2);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        verify(idOutputPort).deleteThirdType(thirdTypeId, entId);
        verify(idOutputPort).deleteThirdType(thirdTypeId, entId2);
    }

    // ==================== Validación de existencia ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando ID no existe")
    void testDeleteThirdTypeIdNoExisteLanzaThirdTypeNotFound() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(false);

        // Act & Assert
        ThirdTypeNotFound exception = assertThrows(ThirdTypeNotFound.class,
                () -> deleteThirdTypeService.deleteThirdType(thirdTypeId, entId));

        assertEquals("El tipo de tercero con ID " + thirdTypeId + " no existe", exception.getMessage());
        verify(idOutputPort).existsThirdTypeById(thirdTypeId);
        verify(idOutputPort, never()).deleteThirdType(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo de tercero es nulo del repositorio")
    void testDeleteThirdTypeThirdTypeNuloDelRepositoryLanzaThirdTypeNotFound() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(null);

        // Act & Assert
        ThirdTypeNotFound exception = assertThrows(ThirdTypeNotFound.class,
                () -> deleteThirdTypeService.deleteThirdType(thirdTypeId, entId));

        assertEquals("El tipo de tercero con ID " + thirdTypeId + " no existe para la empresa " + entId,
                exception.getMessage());
        verify(idOutputPort, never()).deleteThirdType(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando ID de entidad no coincide")
    void testDeleteThirdTypeEntIdNoCoincideLanzaThirdTypeNotFound() {
        // Arrange
        String differentEntId = "ENT002";
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(thirdType);

        // Act & Assert
        ThirdTypeNotFound exception = assertThrows(ThirdTypeNotFound.class,
                () -> deleteThirdTypeService.deleteThirdType(thirdTypeId, differentEntId));

        assertEquals("El tipo de tercero con ID " + thirdTypeId + " no existe para la empresa " + differentEntId,
                exception.getMessage());
        verify(idOutputPort, never()).deleteThirdType(anyLong(), anyString());
    }

    // ==================== Validación en uso ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo de tercero está en uso")
    void testDeleteThirdTypeTipoTerceroEnUsoLanzaThirdTypeInUseException() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(thirdType);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId)).thenReturn(true);

        // Act & Assert
        ThirdTypeInUseException exception = assertThrows(ThirdTypeInUseException.class,
                () -> deleteThirdTypeService.deleteThirdType(thirdTypeId, entId));

        assertTrue(exception.getMessage().contains("Cliente"));
        verify(idOutputPort, never()).deleteThirdType(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo de tercero es usado por varios terceros")
    void testDeleteThirdTypeTipoTerceroUsadoPorVariosThirdsLanzaThirdTypeInUseException() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(thirdType);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId)).thenReturn(true);

        // Act & Assert
        assertThrows(ThirdTypeInUseException.class,
                () -> deleteThirdTypeService.deleteThirdType(thirdTypeId, entId));

        verify(idOutputPort).isThirdTypeInUse(thirdTypeId, entId);
        verify(idOutputPort, never()).deleteThirdType(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo de tercero inactivo está en uso")
    void testDeleteThirdTypeTipoTerceroInactivoEnUsoLanzaThirdTypeInUseException() {
        // Arrange
        ThirdType inactiveThirdType = ThirdType.builder()
                .thirdTypeId(thirdTypeId)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(false)
                .build();

        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(inactiveThirdType);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId)).thenReturn(true);

        // Act & Assert
        assertThrows(ThirdTypeInUseException.class,
                () -> deleteThirdTypeService.deleteThirdType(thirdTypeId, entId));

        verify(idOutputPort, never()).deleteThirdType(anyLong(), anyString());
    }

    // ==================== Casos especiales ====================

    @Test
    @DisplayName("Debe validar correctamente cuando ID es cero")
    void testDeleteThirdTypeIdCeroValidaCorrectamente() {
        // Arrange
        Long zeroId = 0L;
        when(idOutputPort.existsThirdTypeById(zeroId)).thenReturn(false);

        // Act & Assert
        assertThrows(ThirdTypeNotFound.class,
                () -> deleteThirdTypeService.deleteThirdType(zeroId, entId));

        verify(idOutputPort).existsThirdTypeById(zeroId);
        verify(idOutputPort, never()).deleteThirdType(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe validar correctamente cuando ID es negativo")
    void testDeleteThirdTypeIdNegativoValidaCorrectamente() {
        // Arrange
        Long negativeId = -1L;
        when(idOutputPort.existsThirdTypeById(negativeId)).thenReturn(false);

        // Act & Assert
        assertThrows(ThirdTypeNotFound.class,
                () -> deleteThirdTypeService.deleteThirdType(negativeId, entId));

        verify(idOutputPort).existsThirdTypeById(negativeId);
        verify(idOutputPort, never()).deleteThirdType(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe validar correctamente cuando ID de entidad está vacío")
    void testDeleteThirdTypeEntIdVacioValidaCorrectamente() {
        // Arrange
        String emptyEntId = "";
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(thirdType);

        // Act & Assert
        assertThrows(ThirdTypeNotFound.class,
                () -> deleteThirdTypeService.deleteThirdType(thirdTypeId, emptyEntId));

        verify(idOutputPort).getThirdTypeById(thirdTypeId);
        verify(idOutputPort, never()).deleteThirdType(anyLong(), anyString());
    }

    // ==================== Propagación de excepciones ====================

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en verificación de existencia por ID")
    void testDeleteThirdTypeErrorEnExistsThirdTypeByIdPropagaExcepcion() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> deleteThirdTypeService.deleteThirdType(thirdTypeId, entId));

        verify(idOutputPort, never()).deleteThirdType(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en obtener tipo de tercero por ID")
    void testDeleteThirdTypeErrorEnGetThirdTypeByIdPropagaExcepcion() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId))
                .thenThrow(new RuntimeException("Error de conexión"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> deleteThirdTypeService.deleteThirdType(thirdTypeId, entId));

        verify(idOutputPort, never()).deleteThirdType(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en verificar si tipo de tercero está en uso")
    void testDeleteThirdTypeErrorEnIsThirdTypeInUsePropagaExcepcion() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(thirdType);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId))
                .thenThrow(new RuntimeException("Error al verificar uso"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> deleteThirdTypeService.deleteThirdType(thirdTypeId, entId));

        verify(idOutputPort, never()).deleteThirdType(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en eliminar tipo de tercero")
    void testDeleteThirdTypeErrorEnDeleteThirdTypePropagaExcepcion() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(thirdType);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId)).thenReturn(false);
        when(idOutputPort.deleteThirdType(thirdTypeId, entId))
                .thenThrow(new RuntimeException("Error al eliminar"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> deleteThirdTypeService.deleteThirdType(thirdTypeId, entId));
    }

    // ==================== Delegación correcta ====================

    @Test
    @DisplayName("Debe delegar al puerto para validar existencia correctamente")
    void testDeleteThirdTypeDelegaAlPuertoParaValidarExistenciaCorrectamente() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(thirdType);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId)).thenReturn(false);
        when(idOutputPort.deleteThirdType(thirdTypeId, entId)).thenReturn(true);

        // Act
        deleteThirdTypeService.deleteThirdType(thirdTypeId, entId);

        // Assert
        verify(idOutputPort, times(1)).existsThirdTypeById(thirdTypeId);
        verify(idOutputPort, times(1)).getThirdTypeById(thirdTypeId);
    }

    @Test
    @DisplayName("Debe delegar al puerto para validar uso correctamente")
    void testDeleteThirdTypeDelegaAlPuertoParaValidarUsoCorrectamente() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(thirdType);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId)).thenReturn(false);
        when(idOutputPort.deleteThirdType(thirdTypeId, entId)).thenReturn(true);

        // Act
        deleteThirdTypeService.deleteThirdType(thirdTypeId, entId);

        // Assert
        verify(idOutputPort, times(1)).isThirdTypeInUse(thirdTypeId, entId);
    }

    @Test
    @DisplayName("Debe delegar al puerto para eliminar correctamente")
    void testDeleteThirdTypeDelegaAlPuertoParaEliminarCorrectamente() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(thirdType);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId)).thenReturn(false);
        when(idOutputPort.deleteThirdType(thirdTypeId, entId)).thenReturn(true);

        // Act
        deleteThirdTypeService.deleteThirdType(thirdTypeId, entId);

        // Assert
        verify(idOutputPort, times(1)).deleteThirdType(thirdTypeId, entId);
    }

    // ==================== Integración ====================

    @Test
    @DisplayName("Debe validar y eliminar en proceso completo")
    void testDeleteThirdTypeProcesoCompletoValidaYElimina() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(thirdType);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId)).thenReturn(false);
        when(idOutputPort.deleteThirdType(thirdTypeId, entId)).thenReturn(true);

        // Act
        boolean result = deleteThirdTypeService.deleteThirdType(thirdTypeId, entId);

        // Assert
        assertTrue(result);
        InOrder inOrder = inOrder(idOutputPort);
        inOrder.verify(idOutputPort).existsThirdTypeById(thirdTypeId);
        inOrder.verify(idOutputPort).getThirdTypeById(thirdTypeId);
        inOrder.verify(idOutputPort).isThirdTypeInUse(thirdTypeId, entId);
        inOrder.verify(idOutputPort).deleteThirdType(thirdTypeId, entId);
    }

    @Test
    @DisplayName("Debe validar independientemente cada uno en varios intentos de eliminación")
    void testDeleteThirdTypeVariosIntentosEliminacionCadaUnoValidaIndependientemente() {
        // Arrange
        Long id1 = 1L;
        Long id2 = 2L;
        Long id3 = 3L;

        ThirdType tt1 = ThirdType.builder().thirdTypeId(id1).thirdTypeName("Cliente").entId(entId).status(true).build();
        ThirdType tt2 = ThirdType.builder().thirdTypeId(id2).thirdTypeName("Proveedor").entId(entId).status(true).build();
        ThirdType tt3 = ThirdType.builder().thirdTypeId(id3).thirdTypeName("Empleado").entId(entId).status(true).build();

        when(idOutputPort.existsThirdTypeById(id1)).thenReturn(true);
        when(idOutputPort.existsThirdTypeById(id2)).thenReturn(true);
        when(idOutputPort.existsThirdTypeById(id3)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(id1)).thenReturn(tt1);
        when(idOutputPort.getThirdTypeById(id2)).thenReturn(tt2);
        when(idOutputPort.getThirdTypeById(id3)).thenReturn(tt3);
        when(idOutputPort.isThirdTypeInUse(anyLong(), eq(entId))).thenReturn(false);
        when(idOutputPort.deleteThirdType(anyLong(), eq(entId))).thenReturn(true);

        // Act
        deleteThirdTypeService.deleteThirdType(id1, entId);
        deleteThirdTypeService.deleteThirdType(id2, entId);
        deleteThirdTypeService.deleteThirdType(id3, entId);

        // Assert
        verify(idOutputPort, times(3)).existsThirdTypeById(anyLong());
        verify(idOutputPort, times(3)).getThirdTypeById(anyLong());
        verify(idOutputPort, times(3)).isThirdTypeInUse(anyLong(), eq(entId));
        verify(idOutputPort, times(3)).deleteThirdType(anyLong(), eq(entId));
    }

    @Test
    @DisplayName("Debe detener validaciones secuenciales si alguna falla")
    void testDeleteThirdTypeValidacionesSecuencialesSeDetieneSiAlgunaFalla() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(thirdType);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId)).thenReturn(true);

        // Act & Assert
        assertThrows(ThirdTypeInUseException.class,
                () -> deleteThirdTypeService.deleteThirdType(thirdTypeId, entId));

        verify(idOutputPort).existsThirdTypeById(thirdTypeId);
        verify(idOutputPort).getThirdTypeById(thirdTypeId);
        verify(idOutputPort).isThirdTypeInUse(thirdTypeId, entId);
        verify(idOutputPort, never()).deleteThirdType(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe retornar resultado del puerto correctamente")
    void testDeleteThirdTypeRetornaResultadoDelPuertoCorrectamente() {
        // Arrange
        when(idOutputPort.existsThirdTypeById(thirdTypeId)).thenReturn(true);
        when(idOutputPort.getThirdTypeById(thirdTypeId)).thenReturn(thirdType);
        when(idOutputPort.isThirdTypeInUse(thirdTypeId, entId)).thenReturn(false);
        when(idOutputPort.deleteThirdType(thirdTypeId, entId)).thenReturn(true);

        // Act
        boolean result = deleteThirdTypeService.deleteThirdType(thirdTypeId, entId);

        // Assert
        assertTrue(result);
        verify(idOutputPort).deleteThirdType(thirdTypeId, entId);
    }
}
