package com.thirdsmanagement.thirds.unit.application.service.typeId;

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
import com.thirdsmanagement.thirds.application.service.typeId.DeleteTypeIdService;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdInUseException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdNotFound;
import com.thirdsmanagement.thirds.domain.model.PersonClassification;
import com.thirdsmanagement.thirds.domain.model.TypeId;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeleteTypeIdServiceUnitTest {

    @Mock
    private IdOutputPort idOutputPort;

    @InjectMocks
    private DeleteTypeIdService deleteTypeIdService;

    private Long typeIdId;
    private String entId;
    private TypeId typeId;

    @BeforeEach
    void setUp() {
        typeIdId = 1L;
        entId = "ENT001";
        
        typeId = TypeId.builder()
                .id(typeIdId)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();
    }

    // ==================== Eliminación exitosa ====================

    @Test
    @DisplayName("Debe eliminar correctamente tipo de identificación válido y no en uso")
    void testDeleteTypeIdTypeIdValidoYNoEnUsoEliminaCorrectamente() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(typeId);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId)).thenReturn(false);
        when(idOutputPort.deleteTypeId(typeIdId, entId)).thenReturn(true);

        // Act
        boolean result = deleteTypeIdService.deleteTypeId(typeIdId, entId);

        // Assert
        assertTrue(result);
        verify(idOutputPort).existsTypeIdById(typeIdId);
        verify(idOutputPort).getTypeIdById(typeIdId);
        verify(idOutputPort).isTypeIdInUse(typeIdId, entId);
        verify(idOutputPort).deleteTypeId(typeIdId, entId);
    }

    @Test
    @DisplayName("Debe eliminar correctamente cuando status es false")
    void testDeleteTypeIdStatusFalseEliminaCorrectamente() {
        // Arrange
        TypeId inactiveTypeId = TypeId.builder()
                .id(typeIdId)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(false)
                .build();

        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(inactiveTypeId);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId)).thenReturn(false);
        when(idOutputPort.deleteTypeId(typeIdId, entId)).thenReturn(true);

        // Act
        boolean result = deleteTypeIdService.deleteTypeId(typeIdId, entId);

        // Assert
        assertTrue(result);
        verify(idOutputPort).deleteTypeId(typeIdId, entId);
    }

    @Test
    @DisplayName("Debe eliminar cada uno de diferentes tipos de identificación")
    void testDeleteTypeIdDiferentesTiposIdEliminaCadaUno() {
        // Arrange
        Long typeIdId2 = 2L;
        TypeId typeId2 = TypeId.builder()
                .id(typeIdId2)
                .typeId("NIT")
                .typeIdname("NIT")
                .entId(entId)
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(typeId);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId)).thenReturn(false);
        when(idOutputPort.deleteTypeId(typeIdId, entId)).thenReturn(true);

        when(idOutputPort.existsTypeIdById(typeIdId2)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId2)).thenReturn(typeId2);
        when(idOutputPort.isTypeIdInUse(typeIdId2, entId)).thenReturn(false);
        when(idOutputPort.deleteTypeId(typeIdId2, entId)).thenReturn(true);

        // Act
        boolean result1 = deleteTypeIdService.deleteTypeId(typeIdId, entId);
        boolean result2 = deleteTypeIdService.deleteTypeId(typeIdId2, entId);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        verify(idOutputPort).deleteTypeId(typeIdId, entId);
        verify(idOutputPort).deleteTypeId(typeIdId2, entId);
    }

    @Test
    @DisplayName("Debe eliminar cada uno con diferentes entidades")
    void testDeleteTypeIdDiferentesEntIdsEliminaCadaUno() {
        // Arrange
        String entId2 = "ENT002";
        TypeId typeIdEnt2 = TypeId.builder()
                .id(typeIdId)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId2)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId))
                .thenReturn(typeId)
                .thenReturn(typeIdEnt2);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId)).thenReturn(false);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId2)).thenReturn(false);
        when(idOutputPort.deleteTypeId(typeIdId, entId)).thenReturn(true);
        when(idOutputPort.deleteTypeId(typeIdId, entId2)).thenReturn(true);

        // Act
        boolean result1 = deleteTypeIdService.deleteTypeId(typeIdId, entId);
        boolean result2 = deleteTypeIdService.deleteTypeId(typeIdId, entId2);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        verify(idOutputPort).deleteTypeId(typeIdId, entId);
        verify(idOutputPort).deleteTypeId(typeIdId, entId2);
    }

    // ==================== Validación de existencia ====================

    @Test
    @DisplayName("Debe lanzar TypeIdNotFound cuando ID no existe")
    void testDeleteTypeIdIdNoExisteLanzaTypeIdNotFound() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(false);

        // Act & Assert
        TypeIdNotFound exception = assertThrows(TypeIdNotFound.class,
                () -> deleteTypeIdService.deleteTypeId(typeIdId, entId));

        assertEquals("El tipo de identificación con ID " + typeIdId + " no existe", exception.getMessage());
        verify(idOutputPort).existsTypeIdById(typeIdId);
        verify(idOutputPort, never()).deleteTypeId(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar TypeIdNotFound cuando tipo de identificación es nulo del repositorio")
    void testDeleteTypeIdTypeIdNuloDelRepositoryLanzaTypeIdNotFound() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(null);

        // Act & Assert
        TypeIdNotFound exception = assertThrows(TypeIdNotFound.class,
                () -> deleteTypeIdService.deleteTypeId(typeIdId, entId));

        assertEquals("El tipo de identificación con ID " + typeIdId + " no existe para la empresa " + entId,
                exception.getMessage());
        verify(idOutputPort, never()).deleteTypeId(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar TypeIdNotFound cuando ID de entidad no coincide")
    void testDeleteTypeIdEntIdNoCoincideLanzaTypeIdNotFound() {
        // Arrange
        String differentEntId = "ENT002";
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(typeId);

        // Act & Assert
        TypeIdNotFound exception = assertThrows(TypeIdNotFound.class,
                () -> deleteTypeIdService.deleteTypeId(typeIdId, differentEntId));

        assertEquals("El tipo de identificación con ID " + typeIdId + " no existe para la empresa " + differentEntId,
                exception.getMessage());
        verify(idOutputPort, never()).deleteTypeId(anyLong(), anyString());
    }

    // ==================== Validación en uso ====================

    @Test
    @DisplayName("Debe lanzar TypeIdInUseException cuando tipo de identificación está en uso")
    void testDeleteTypeIdTypeIdEnUsoLanzaTypeIdInUseException() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(typeId);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId)).thenReturn(true);

        // Act & Assert
        TypeIdInUseException exception = assertThrows(TypeIdInUseException.class,
                () -> deleteTypeIdService.deleteTypeId(typeIdId, entId));

        assertTrue(exception.getMessage().contains("CC"));
        verify(idOutputPort, never()).deleteTypeId(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar TypeIdInUseException cuando tipo de identificación es usado por varios terceros")
    void testDeleteTypeIdTypeIdUsadoPorVariosThirdsLanzaTypeIdInUseException() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(typeId);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId)).thenReturn(true);

        // Act & Assert
        assertThrows(TypeIdInUseException.class,
                () -> deleteTypeIdService.deleteTypeId(typeIdId, entId));

        verify(idOutputPort).isTypeIdInUse(typeIdId, entId);
        verify(idOutputPort, never()).deleteTypeId(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar TypeIdInUseException cuando tipo de identificación inactivo está en uso")
    void testDeleteTypeIdTypeIdInactivoEnUsoLanzaTypeIdInUseException() {
        // Arrange
        TypeId inactiveTypeId = TypeId.builder()
                .id(typeIdId)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(false)
                .build();

        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(inactiveTypeId);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId)).thenReturn(true);

        // Act & Assert
        assertThrows(TypeIdInUseException.class,
                () -> deleteTypeIdService.deleteTypeId(typeIdId, entId));

        verify(idOutputPort, never()).deleteTypeId(anyLong(), anyString());
    }

    // ==================== Casos especiales ====================

    @Test
    @DisplayName("Debe validar correctamente cuando ID es cero")
    void testDeleteTypeIdIdCeroValidaCorrectamente() {
        // Arrange
        Long zeroId = 0L;
        when(idOutputPort.existsTypeIdById(zeroId)).thenReturn(false);

        // Act & Assert
        assertThrows(TypeIdNotFound.class,
                () -> deleteTypeIdService.deleteTypeId(zeroId, entId));

        verify(idOutputPort).existsTypeIdById(zeroId);
        verify(idOutputPort, never()).deleteTypeId(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe validar correctamente cuando ID es negativo")
    void testDeleteTypeIdIdNegativoValidaCorrectamente() {
        // Arrange
        Long negativeId = -1L;
        when(idOutputPort.existsTypeIdById(negativeId)).thenReturn(false);

        // Act & Assert
        assertThrows(TypeIdNotFound.class,
                () -> deleteTypeIdService.deleteTypeId(negativeId, entId));

        verify(idOutputPort).existsTypeIdById(negativeId);
        verify(idOutputPort, never()).deleteTypeId(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe validar correctamente cuando ID de entidad está vacío")
    void testDeleteTypeIdEntIdVacioValidaCorrectamente() {
        // Arrange
        String emptyEntId = "";
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(typeId);

        // Act & Assert
        assertThrows(TypeIdNotFound.class,
                () -> deleteTypeIdService.deleteTypeId(typeIdId, emptyEntId));

        verify(idOutputPort).getTypeIdById(typeIdId);
        verify(idOutputPort, never()).deleteTypeId(anyLong(), anyString());
    }

    // ==================== Propagación de excepciones ====================

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en existsTypeIdById")
    void testDeleteTypeIdErrorEnExistsTypeIdByIdPropagaExcepcion() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> deleteTypeIdService.deleteTypeId(typeIdId, entId));

        verify(idOutputPort, never()).deleteTypeId(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en getTypeIdById")
    void testDeleteTypeIdErrorEnGetTypeIdByIdPropagaExcepcion() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId))
                .thenThrow(new RuntimeException("Error de conexión"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> deleteTypeIdService.deleteTypeId(typeIdId, entId));

        verify(idOutputPort, never()).deleteTypeId(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en isTypeIdInUse")
    void testDeleteTypeIdErrorEnIsTypeIdInUsePropagaExcepcion() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(typeId);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId))
                .thenThrow(new RuntimeException("Error al verificar uso"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> deleteTypeIdService.deleteTypeId(typeIdId, entId));

        verify(idOutputPort, never()).deleteTypeId(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en deleteTypeId")
    void testDeleteTypeIdErrorEnDeleteTypeIdPropagaExcepcion() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(typeId);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId)).thenReturn(false);
        when(idOutputPort.deleteTypeId(typeIdId, entId))
                .thenThrow(new RuntimeException("Error al eliminar"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> deleteTypeIdService.deleteTypeId(typeIdId, entId));
    }

    // ==================== Delegación correcta ====================

    @Test
    @DisplayName("Debe delegar correctamente al puerto para validar existencia")
    void testDeleteTypeIdDelegaAlPuertoParaValidarExistenciaCorrectamente() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(typeId);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId)).thenReturn(false);
        when(idOutputPort.deleteTypeId(typeIdId, entId)).thenReturn(true);

        // Act
        deleteTypeIdService.deleteTypeId(typeIdId, entId);

        // Assert
        verify(idOutputPort, times(1)).existsTypeIdById(typeIdId);
        verify(idOutputPort, times(1)).getTypeIdById(typeIdId);
    }

    @Test
    @DisplayName("Debe delegar correctamente al puerto para validar uso")
    void testDeleteTypeIdDelegaAlPuertoParaValidarUsoCorrectamente() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(typeId);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId)).thenReturn(false);
        when(idOutputPort.deleteTypeId(typeIdId, entId)).thenReturn(true);

        // Act
        deleteTypeIdService.deleteTypeId(typeIdId, entId);

        // Assert
        verify(idOutputPort, times(1)).isTypeIdInUse(typeIdId, entId);
    }

    @Test
    @DisplayName("Debe delegar correctamente al puerto para eliminar")
    void testDeleteTypeIdDelegaAlPuertoParaEliminarCorrectamente() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(typeId);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId)).thenReturn(false);
        when(idOutputPort.deleteTypeId(typeIdId, entId)).thenReturn(true);

        // Act
        deleteTypeIdService.deleteTypeId(typeIdId, entId);

        // Assert
        verify(idOutputPort, times(1)).deleteTypeId(typeIdId, entId);
    }

    // ==================== Integración ====================

    @Test
    @DisplayName("Debe validar y eliminar en proceso completo")
    void testDeleteTypeIdProcesoCompletoValidaYElimina() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(typeId);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId)).thenReturn(false);
        when(idOutputPort.deleteTypeId(typeIdId, entId)).thenReturn(true);

        // Act
        boolean result = deleteTypeIdService.deleteTypeId(typeIdId, entId);

        // Assert
        assertTrue(result);
        InOrder inOrder = inOrder(idOutputPort);
        inOrder.verify(idOutputPort).existsTypeIdById(typeIdId);
        inOrder.verify(idOutputPort).getTypeIdById(typeIdId);
        inOrder.verify(idOutputPort).isTypeIdInUse(typeIdId, entId);
        inOrder.verify(idOutputPort).deleteTypeId(typeIdId, entId);
    }

    @Test
    @DisplayName("Debe validar independientemente cada uno de varios intentos de eliminación")
    void testDeleteTypeIdVariosIntentosEliminacionCadaUnoValidaIndependientemente() {
        // Arrange
        Long id1 = 1L;
        Long id2 = 2L;
        Long id3 = 3L;

        TypeId ti1 = TypeId.builder().id(id1).typeId("CC").typeIdname("Cédula de Ciudadanía")
                .entId(entId).classification(PersonClassification.NATURAL_PERSON).status(true).build();
        TypeId ti2 = TypeId.builder().id(id2).typeId("CE").typeIdname("Cédula de Extranjería")
                .entId(entId).classification(PersonClassification.NATURAL_PERSON).status(true).build();
        TypeId ti3 = TypeId.builder().id(id3).typeId("NIT").typeIdname("NIT")
                .entId(entId).classification(PersonClassification.LEGAL_ENTITY).status(true).build();

        when(idOutputPort.existsTypeIdById(id1)).thenReturn(true);
        when(idOutputPort.existsTypeIdById(id2)).thenReturn(true);
        when(idOutputPort.existsTypeIdById(id3)).thenReturn(true);
        when(idOutputPort.getTypeIdById(id1)).thenReturn(ti1);
        when(idOutputPort.getTypeIdById(id2)).thenReturn(ti2);
        when(idOutputPort.getTypeIdById(id3)).thenReturn(ti3);
        when(idOutputPort.isTypeIdInUse(anyLong(), eq(entId))).thenReturn(false);
        when(idOutputPort.deleteTypeId(anyLong(), eq(entId))).thenReturn(true);

        // Act
        deleteTypeIdService.deleteTypeId(id1, entId);
        deleteTypeIdService.deleteTypeId(id2, entId);
        deleteTypeIdService.deleteTypeId(id3, entId);

        // Assert
        verify(idOutputPort, times(3)).existsTypeIdById(anyLong());
        verify(idOutputPort, times(3)).getTypeIdById(anyLong());
        verify(idOutputPort, times(3)).isTypeIdInUse(anyLong(), eq(entId));
        verify(idOutputPort, times(3)).deleteTypeId(anyLong(), eq(entId));
    }

    @Test
    @DisplayName("Debe detenerse si alguna validación secuencial falla")
    void testDeleteTypeIdValidacionesSecuencialesSeDetieneSiAlgunaFalla() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(typeId);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId)).thenReturn(true);

        // Act & Assert
        assertThrows(TypeIdInUseException.class,
                () -> deleteTypeIdService.deleteTypeId(typeIdId, entId));

        verify(idOutputPort).existsTypeIdById(typeIdId);
        verify(idOutputPort).getTypeIdById(typeIdId);
        verify(idOutputPort).isTypeIdInUse(typeIdId, entId);
        verify(idOutputPort, never()).deleteTypeId(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe retornar correctamente resultado del puerto")
    void testDeleteTypeIdRetornaResultadoDelPuertoCorrectamente() {
        // Arrange
        when(idOutputPort.existsTypeIdById(typeIdId)).thenReturn(true);
        when(idOutputPort.getTypeIdById(typeIdId)).thenReturn(typeId);
        when(idOutputPort.isTypeIdInUse(typeIdId, entId)).thenReturn(false);
        when(idOutputPort.deleteTypeId(typeIdId, entId)).thenReturn(true);

        // Act
        boolean result = deleteTypeIdService.deleteTypeId(typeIdId, entId);

        // Assert
        assertTrue(result);
        verify(idOutputPort).deleteTypeId(typeIdId, entId);
    }
}
