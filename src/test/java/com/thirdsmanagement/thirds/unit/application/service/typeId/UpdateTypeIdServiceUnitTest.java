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
import com.thirdsmanagement.thirds.application.service.typeId.UpdateTypeIdService;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdInUseException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdNotFound;
import com.thirdsmanagement.thirds.domain.model.PersonClassification;
import com.thirdsmanagement.thirds.domain.model.TypeId;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateTypeIdServiceUnitTest {

    @Mock
    private IdOutputPort idOutputPort;

    @InjectMocks
    private UpdateTypeIdService updateTypeIdService;

    private TypeId typeId;
    private TypeId existingTypeId;
    private String entId;

    @BeforeEach
    void setUp() {
        entId = "ENT001";

        existingTypeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        typeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía Actualizada")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();
    }

    // ==================== Actualización exitosa ====================

    @Test
    @DisplayName("Debe actualizar correctamente tipo de identificación válido sin movimientos")
    void testUpdateTypeIdTypeIdValidoSinMovimientosActualizaCorrectamente() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateTypeId(typeId)).thenReturn(typeId);

        // Act
        TypeId result = updateTypeIdService.updateTypeId(typeId);

        // Assert
        assertNotNull(result);
        assertEquals("Cédula de Ciudadanía Actualizada", result.getTypeIdname());
        verify(idOutputPort).getTypeIdById(1L);
        verify(idOutputPort).hasTypeIdThirdsWithMovements(1L, entId);
        verify(idOutputPort).updateTypeId(typeId);
    }

    @Test
    @DisplayName("Debe actualizar correctamente con cambio de nombre")
    void testUpdateTypeIdCambioNombreActualizaCorrectamente() {
        // Arrange
        TypeId updatedTypeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateTypeId(updatedTypeId)).thenReturn(updatedTypeId);

        // Act
        TypeId result = updateTypeIdService.updateTypeId(updatedTypeId);

        // Assert
        assertEquals("Cédula", result.getTypeIdname());
        verify(idOutputPort).updateTypeId(updatedTypeId);
    }

    @Test
    @DisplayName("Debe actualizar correctamente con cambio de status")
    void testUpdateTypeIdCambioStatusActualizaCorrectamente() {
        // Arrange
        TypeId inactiveTypeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(false)
                .build();

        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateTypeId(inactiveTypeId)).thenReturn(inactiveTypeId);

        // Act
        TypeId result = updateTypeIdService.updateTypeId(inactiveTypeId);

        // Assert
        assertFalse(result.getStatus());
        verify(idOutputPort).updateTypeId(inactiveTypeId);
    }

    @Test
    @DisplayName("Debe actualizar correctamente con cambio de clasificación")
    void testUpdateTypeIdCambioClasificacionActualizaCorrectamente() {
        // Arrange
        TypeId updatedTypeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateTypeId(updatedTypeId)).thenReturn(updatedTypeId);

        // Act
        TypeId result = updateTypeIdService.updateTypeId(updatedTypeId);

        // Assert
        assertEquals(PersonClassification.LEGAL_ENTITY, result.getClassification());
        verify(idOutputPort).updateTypeId(updatedTypeId);
    }

    // ==================== Validación de nulos ====================

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando tipo de identificación es nulo")
    void testUpdateTypeIdTypeIdNuloLanzaIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> updateTypeIdService.updateTypeId(null));

        assertEquals("El tipo de identificación y su ID son requeridos para actualizar", exception.getMessage());
        verify(idOutputPort, never()).updateTypeId(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando ID es nulo")
    void testUpdateTypeIdIdNuloLanzaIllegalArgumentException() {
        // Arrange
        TypeId typeIdWithoutId = TypeId.builder()
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> updateTypeIdService.updateTypeId(typeIdWithoutId));

        assertEquals("El tipo de identificación y su ID son requeridos para actualizar", exception.getMessage());
        verify(idOutputPort, never()).updateTypeId(any());
    }

    // ==================== Validación de existencia ====================

    @Test
    @DisplayName("Debe lanzar TypeIdNotFound cuando tipo de identificación no existe")
    void testUpdateTypeIdTypeIdNoExisteLanzaTypeIdNotFound() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(null);

        // Act & Assert
        TypeIdNotFound exception = assertThrows(TypeIdNotFound.class,
                () -> updateTypeIdService.updateTypeId(typeId));

        assertEquals("El tipo de identificación con ID 1 no existe", exception.getMessage());
        verify(idOutputPort).getTypeIdById(1L);
        verify(idOutputPort, never()).updateTypeId(any());
    }

    @Test
    @DisplayName("Debe lanzar TypeIdNotFound cuando ID no existe en base de datos")
    void testUpdateTypeIdIdNoExistenteEnBaseDatosLanzaTypeIdNotFound() {
        // Arrange
        TypeId nonExistentTypeId = TypeId.builder()
                .id(999L)
                .typeId("XX")
                .typeIdname("Tipo Inexistente")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        when(idOutputPort.getTypeIdById(999L)).thenReturn(null);

        // Act & Assert
        assertThrows(TypeIdNotFound.class,
                () -> updateTypeIdService.updateTypeId(nonExistentTypeId));

        verify(idOutputPort, never()).updateTypeId(any());
    }

    // ==================== Validación con movimientos ====================

    @Test
    @DisplayName("Debe lanzar TypeIdInUseException cuando tipo de identificación tiene movimientos")
    void testUpdateTypeIdTypeIdConMovimientosLanzaTypeIdInUseException() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(true);

        // Act & Assert
        TypeIdInUseException exception = assertThrows(TypeIdInUseException.class,
                () -> updateTypeIdService.updateTypeId(typeId));

        assertTrue(exception.getMessage().contains("CC"));
        verify(idOutputPort).getTypeIdById(1L);
        verify(idOutputPort).hasTypeIdThirdsWithMovements(1L, entId);
        verify(idOutputPort, never()).updateTypeId(any());
    }

    @Test
    @DisplayName("Debe lanzar TypeIdInUseException cuando tipo de identificación es usado por terceros con movimientos")
    void testUpdateTypeIdTypeIdUsadoPorTercerosConMovimientosLanzaTypeIdInUseException() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(true);

        // Act & Assert
        assertThrows(TypeIdInUseException.class,
                () -> updateTypeIdService.updateTypeId(typeId));

        verify(idOutputPort, never()).updateTypeId(any());
    }

    @Test
    @DisplayName("Debe lanzar TypeIdInUseException cuando tipo de identificación inactivo tiene movimientos")
    void testUpdateTypeIdTypeIdInactivoConMovimientosLanzaTypeIdInUseException() {
        // Arrange
        TypeId inactiveExisting = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(false)
                .build();

        when(idOutputPort.getTypeIdById(1L)).thenReturn(inactiveExisting);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(true);

        // Act & Assert
        assertThrows(TypeIdInUseException.class,
                () -> updateTypeIdService.updateTypeId(typeId));

        verify(idOutputPort, never()).updateTypeId(any());
    }

    // ==================== Casos especiales ====================

    @Test
    @DisplayName("Debe actualizar correctamente con actualización con mismos valores")
    void testUpdateTypeIdActualizacionConMismosValoresActualizaCorrectamente() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateTypeId(existingTypeId)).thenReturn(existingTypeId);

        // Act
        TypeId result = updateTypeIdService.updateTypeId(existingTypeId);

        // Assert
        assertNotNull(result);
        assertEquals(existingTypeId.getTypeIdname(), result.getTypeIdname());
        verify(idOutputPort).updateTypeId(existingTypeId);
    }

    @Test
    @DisplayName("Debe actualizar correctamente con nombre largo")
    void testUpdateTypeIdNombreLargoActualizaCorrectamente() {
        // Arrange
        TypeId longNameTypeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía de la República de Colombia con Información Extendida")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateTypeId(longNameTypeId)).thenReturn(longNameTypeId);

        // Act
        TypeId result = updateTypeIdService.updateTypeId(longNameTypeId);

        // Assert
        assertTrue(result.getTypeIdname().length() > 50);
        verify(idOutputPort).updateTypeId(longNameTypeId);
    }

    @Test
    @DisplayName("Debe actualizar cada uno de diferentes tipos de identificación")
    void testUpdateTypeIdDiferentesTiposIdActualizaCadaUno() {
        // Arrange
        TypeId existingNit = TypeId.builder()
                .id(2L)
                .typeId("NIT")
                .typeIdname("NIT")
                .entId(entId)
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        TypeId updatedNit = TypeId.builder()
                .id(2L)
                .typeId("NIT")
                .typeIdname("Número de Identificación Tributaria")
                .entId(entId)
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.getTypeIdById(2L)).thenReturn(existingNit);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.hasTypeIdThirdsWithMovements(2L, entId)).thenReturn(false);
        when(idOutputPort.updateTypeId(typeId)).thenReturn(typeId);
        when(idOutputPort.updateTypeId(updatedNit)).thenReturn(updatedNit);

        // Act
        TypeId result1 = updateTypeIdService.updateTypeId(typeId);
        TypeId result2 = updateTypeIdService.updateTypeId(updatedNit);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        verify(idOutputPort).updateTypeId(typeId);
        verify(idOutputPort).updateTypeId(updatedNit);
    }

    // ==================== Propagación de excepciones ====================

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en getTypeIdById")
    void testUpdateTypeIdErrorEnGetTypeIdByIdPropagaExcepcion() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenThrow(new RuntimeException("Error de base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> updateTypeIdService.updateTypeId(typeId));

        verify(idOutputPort, never()).updateTypeId(any());
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en hasTypeIdThirdsWithMovements")
    void testUpdateTypeIdErrorEnHasTypeIdThirdsWithMovementsPropagaExcepcion() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId))
                .thenThrow(new RuntimeException("Error al verificar movimientos"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> updateTypeIdService.updateTypeId(typeId));

        verify(idOutputPort, never()).updateTypeId(any());
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en updateTypeId")
    void testUpdateTypeIdErrorEnUpdateTypeIdPropagaExcepcion() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateTypeId(typeId)).thenThrow(new RuntimeException("Error al actualizar"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> updateTypeIdService.updateTypeId(typeId));
    }

    // ==================== Delegación correcta ====================

    @Test
    @DisplayName("Debe delegar correctamente al puerto para validar existencia")
    void testUpdateTypeIdDelegaAlPuertoParaValidarExistenciaCorrectamente() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateTypeId(typeId)).thenReturn(typeId);

        // Act
        updateTypeIdService.updateTypeId(typeId);

        // Assert
        verify(idOutputPort, times(1)).getTypeIdById(1L);
    }

    @Test
    @DisplayName("Debe delegar correctamente al puerto para validar movimientos")
    void testUpdateTypeIdDelegaAlPuertoParaValidarMovimientosCorrectamente() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateTypeId(typeId)).thenReturn(typeId);

        // Act
        updateTypeIdService.updateTypeId(typeId);

        // Assert
        verify(idOutputPort, times(1)).hasTypeIdThirdsWithMovements(1L, entId);
    }

    @Test
    @DisplayName("Debe delegar correctamente al puerto para actualizar")
    void testUpdateTypeIdDelegaAlPuertoParaActualizarCorrectamente() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateTypeId(typeId)).thenReturn(typeId);

        // Act
        updateTypeIdService.updateTypeId(typeId);

        // Assert
        verify(idOutputPort, times(1)).updateTypeId(typeId);
    }

    @Test
    @DisplayName("Debe delegar parámetros correctos al puerto")
    void testUpdateTypeIdDelegaParametrosCorrectosAlPuerto() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateTypeId(typeId)).thenReturn(typeId);

        // Act
        updateTypeIdService.updateTypeId(typeId);

        // Assert
        verify(idOutputPort).hasTypeIdThirdsWithMovements(eq(1L), eq(entId));
        verify(idOutputPort).updateTypeId(argThat(t -> 
                t.getId().equals(1L) && 
                t.getTypeIdname().equals("Cédula de Ciudadanía Actualizada")));
    }

    // ==================== Integración ====================

    @Test
    @DisplayName("Debe validar y actualizar en proceso completo")
    void testUpdateTypeIdProcesoCompletoValidaYActualiza() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateTypeId(typeId)).thenReturn(typeId);

        // Act
        TypeId result = updateTypeIdService.updateTypeId(typeId);

        // Assert
        assertNotNull(result);
        InOrder inOrder = inOrder(idOutputPort);
        inOrder.verify(idOutputPort).getTypeIdById(1L);
        inOrder.verify(idOutputPort).hasTypeIdThirdsWithMovements(1L, entId);
        inOrder.verify(idOutputPort).updateTypeId(typeId);
    }

    @Test
    @DisplayName("Debe validar independientemente cada uno de varios intentos de actualización")
    void testUpdateTypeIdVariosIntentosActualizacionCadaUnoValidaIndependientemente() {
        // Arrange
        TypeId typeId2 = TypeId.builder()
                .id(2L)
                .typeId("NIT")
                .typeIdname("NIT Actualizado")
                .entId(entId)
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        TypeId existing2 = TypeId.builder()
                .id(2L)
                .typeId("NIT")
                .typeIdname("NIT")
                .entId(entId)
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.getTypeIdById(2L)).thenReturn(existing2);
        when(idOutputPort.hasTypeIdThirdsWithMovements(anyLong(), eq(entId))).thenReturn(false);
        when(idOutputPort.updateTypeId(any(TypeId.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        updateTypeIdService.updateTypeId(typeId);
        updateTypeIdService.updateTypeId(typeId2);

        // Assert
        verify(idOutputPort, times(2)).getTypeIdById(anyLong());
        verify(idOutputPort, times(2)).hasTypeIdThirdsWithMovements(anyLong(), eq(entId));
        verify(idOutputPort, times(2)).updateTypeId(any(TypeId.class));
    }

    @Test
    @DisplayName("Debe detenerse si alguna validación secuencial falla")
    void testUpdateTypeIdValidacionesSecuencialesSeDetieneSiAlgunaFalla() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(true);

        // Act & Assert
        assertThrows(TypeIdInUseException.class,
                () -> updateTypeIdService.updateTypeId(typeId));

        verify(idOutputPort).getTypeIdById(1L);
        verify(idOutputPort).hasTypeIdThirdsWithMovements(1L, entId);
        verify(idOutputPort, never()).updateTypeId(any());
    }

    @Test
    @DisplayName("Debe retornar correctamente objeto actualizado del puerto")
    void testUpdateTypeIdRetornaObjetoActualizadoDelPuertoCorrectamente() {
        // Arrange
        when(idOutputPort.getTypeIdById(1L)).thenReturn(existingTypeId);
        when(idOutputPort.hasTypeIdThirdsWithMovements(1L, entId)).thenReturn(false);
        when(idOutputPort.updateTypeId(typeId)).thenReturn(typeId);

        // Act
        TypeId result = updateTypeIdService.updateTypeId(typeId);

        // Assert
        assertSame(typeId, result);
        assertEquals("Cédula de Ciudadanía Actualizada", result.getTypeIdname());
    }
}
