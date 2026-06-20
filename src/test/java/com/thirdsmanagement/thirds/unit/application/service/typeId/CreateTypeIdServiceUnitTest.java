package com.thirdsmanagement.thirds.unit.application.service.typeId;

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
import com.thirdsmanagement.thirds.application.service.typeId.CreateTypeIdService;
import com.thirdsmanagement.thirds.domain.model.PersonClassification;
import com.thirdsmanagement.thirds.domain.model.TypeId;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreateTypeIdServiceUnitTest {

    @Mock
    private IdOutputPort idOutputPort;

    @InjectMocks
    private CreateTypeIdService createTypeIdService;

    private String entId;
    private TypeId typeId;

    @BeforeEach
    void setUp() {
        entId = "ENT001";
        
        typeId = TypeId.builder()
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();
    }

    // ==================== Creación exitosa ====================

    @Test
    @DisplayName("Debe crear correctamente tipo de identificación válido")
    void testCreateTypeIdTipoIdValidoCreaCorrectamente() {
        // Arrange
        TypeId savedTypeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        when(idOutputPort.saveTypeId(typeId)).thenReturn(savedTypeId);

        // Act
        TypeId result = createTypeIdService.createTypeId(typeId);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("CC", result.getTypeId());
        assertEquals("Cédula de Ciudadanía", result.getTypeIdname());
        verify(idOutputPort, times(1)).saveTypeId(typeId);
    }

    @Test
    @DisplayName("Debe crear cada uno de diferentes tipos de identificación")
    void testCreateTypeIdDiferentesTiposIdentificacionCreaCadaUno() {
        // Arrange
        TypeId nit = TypeId.builder()
                .typeId("NIT")
                .typeIdname("NIT")
                .entId(entId)
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        when(idOutputPort.saveTypeId(any(TypeId.class))).thenAnswer(invocation -> {
            TypeId input = invocation.getArgument(0);
            if ("CC".equals(input.getTypeId())) {
                return TypeId.builder().id(1L).typeId("CC").typeIdname("Cédula de Ciudadanía")
                        .entId(entId).classification(PersonClassification.NATURAL_PERSON).status(true).build();
            } else if ("NIT".equals(input.getTypeId())) {
                return TypeId.builder().id(2L).typeId("NIT").typeIdname("NIT")
                        .entId(entId).classification(PersonClassification.LEGAL_ENTITY).status(true).build();
            }
            return null;
        });

        // Act
        TypeId result1 = createTypeIdService.createTypeId(typeId);
        TypeId result2 = createTypeIdService.createTypeId(nit);

        // Assert
        assertEquals("CC", result1.getTypeId());
        assertEquals("NIT", result2.getTypeId());
        assertEquals(PersonClassification.NATURAL_PERSON, result1.getClassification());
        assertEquals(PersonClassification.LEGAL_ENTITY, result2.getClassification());
        verify(idOutputPort, times(2)).saveTypeId(any(TypeId.class));
    }

    @Test
    @DisplayName("Debe crear con status true cuando status es true")
    void testCreateTypeIdStatusTrueCreaConStatusTrue() {
        // Arrange
        TypeId savedTypeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        when(idOutputPort.saveTypeId(typeId)).thenReturn(savedTypeId);

        // Act
        TypeId result = createTypeIdService.createTypeId(typeId);

        // Assert
        assertTrue(result.getStatus());
        verify(idOutputPort).saveTypeId(typeId);
    }

    @Test
    @DisplayName("Debe crear con status false cuando status es false")
    void testCreateTypeIdStatusFalseCreaConStatusFalse() {
        // Arrange
        TypeId inactiveTypeId = TypeId.builder()
                .typeId("CE")
                .typeIdname("Cédula de Extranjería")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(false)
                .build();

        TypeId savedTypeId = TypeId.builder()
                .id(1L)
                .typeId("CE")
                .typeIdname("Cédula de Extranjería")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(false)
                .build();

        when(idOutputPort.saveTypeId(inactiveTypeId)).thenReturn(savedTypeId);

        // Act
        TypeId result = createTypeIdService.createTypeId(inactiveTypeId);

        // Assert
        assertFalse(result.getStatus());
        verify(idOutputPort).saveTypeId(inactiveTypeId);
    }

    @Test
    @DisplayName("Debe crear correctamente con clasificación persona natural")
    void testCreateTypeIdClassificationNaturalPersonCreaCorrectamente() {
        // Arrange
        TypeId savedTypeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        when(idOutputPort.saveTypeId(typeId)).thenReturn(savedTypeId);

        // Act
        TypeId result = createTypeIdService.createTypeId(typeId);

        // Assert
        assertEquals(PersonClassification.NATURAL_PERSON, result.getClassification());
        verify(idOutputPort).saveTypeId(typeId);
    }

    @Test
    @DisplayName("Debe crear correctamente con clasificación persona jurídica")
    void testCreateTypeIdClassificationLegalEntityCreaCorrectamente() {
        // Arrange
        TypeId nitTypeId = TypeId.builder()
                .typeId("NIT")
                .typeIdname("NIT")
                .entId(entId)
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        TypeId savedTypeId = TypeId.builder()
                .id(1L)
                .typeId("NIT")
                .typeIdname("NIT")
                .entId(entId)
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        when(idOutputPort.saveTypeId(nitTypeId)).thenReturn(savedTypeId);

        // Act
        TypeId result = createTypeIdService.createTypeId(nitTypeId);

        // Assert
        assertEquals(PersonClassification.LEGAL_ENTITY, result.getClassification());
        verify(idOutputPort).saveTypeId(nitTypeId);
    }

    // ==================== Diferentes entidades ====================

    @Test
    @DisplayName("Debe crear cada uno con diferentes entidades")
    void testCreateTypeIdDiferentesEntIdsCreaCadaUno() {
        // Arrange
        String entId2 = "ENT002";
        TypeId typeIdEnt2 = TypeId.builder()
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId2)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        when(idOutputPort.saveTypeId(any(TypeId.class))).thenAnswer(invocation -> {
            TypeId input = invocation.getArgument(0);
            if (entId.equals(input.getEntId())) {
                return TypeId.builder().id(1L).typeId("CC").typeIdname("Cédula de Ciudadanía")
                        .entId(entId).classification(PersonClassification.NATURAL_PERSON).status(true).build();
            } else if (entId2.equals(input.getEntId())) {
                return TypeId.builder().id(2L).typeId("CC").typeIdname("Cédula de Ciudadanía")
                        .entId(entId2).classification(PersonClassification.NATURAL_PERSON).status(true).build();
            }
            return null;
        });

        // Act
        TypeId result1 = createTypeIdService.createTypeId(typeId);
        TypeId result2 = createTypeIdService.createTypeId(typeIdEnt2);

        // Assert
        assertEquals(entId, result1.getEntId());
        assertEquals(entId2, result2.getEntId());
        verify(idOutputPort, times(2)).saveTypeId(any(TypeId.class));
    }

    @Test
    @DisplayName("Debe crear todos los tipos en la misma entidad")
    void testCreateTypeIdVariosEnMismaEntidadCreaTodos() {
        // Arrange
        TypeId ce = TypeId.builder().typeId("CE").typeIdname("Cédula de Extranjería")
                .entId(entId).classification(PersonClassification.NATURAL_PERSON).status(true).build();
        TypeId nit = TypeId.builder().typeId("NIT").typeIdname("NIT")
                .entId(entId).classification(PersonClassification.LEGAL_ENTITY).status(true).build();

        when(idOutputPort.saveTypeId(any(TypeId.class))).thenAnswer(invocation -> {
            TypeId input = invocation.getArgument(0);
            return TypeId.builder()
                    .id(1L)
                    .typeId(input.getTypeId())
                    .typeIdname(input.getTypeIdname())
                    .entId(input.getEntId())
                    .classification(input.getClassification())
                    .status(input.getStatus())
                    .build();
        });

        // Act
        createTypeIdService.createTypeId(typeId);
        createTypeIdService.createTypeId(ce);
        createTypeIdService.createTypeId(nit);

        // Assert
        verify(idOutputPort, times(3)).saveTypeId(any(TypeId.class));
    }

    // ==================== Propagación de excepciones ====================

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en puerto de salida")
    void testCreateTypeIdErrorEnIdOutputPortPropagaExcepcion() {
        // Arrange
        when(idOutputPort.saveTypeId(typeId))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> createTypeIdService.createTypeId(typeId));
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error de conexión")
    void testCreateTypeIdErrorDeConexionPropagaExcepcion() {
        // Arrange
        when(idOutputPort.saveTypeId(typeId))
                .thenThrow(new RuntimeException("Error de conexión"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> createTypeIdService.createTypeId(typeId));

        assertEquals("Error de conexión", exception.getMessage());
    }

    // ==================== Delegación correcta ====================

    @Test
    @DisplayName("Debe delegar correctamente al puerto para guardar")
    void testCreateTypeIdDelegaAlPuertoParaGuardarCorrectamente() {
        // Arrange
        when(idOutputPort.saveTypeId(typeId)).thenReturn(typeId);

        // Act
        createTypeIdService.createTypeId(typeId);

        // Assert
        verify(idOutputPort, times(1)).saveTypeId(typeId);
        verifyNoMoreInteractions(idOutputPort);
    }

    @Test
    @DisplayName("Debe pasar datos completos al puerto")
    void testCreateTypeIdPasaDatosCompletosAlPuerto() {
        // Arrange
        when(idOutputPort.saveTypeId(any(TypeId.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        createTypeIdService.createTypeId(typeId);

        // Assert
        verify(idOutputPort).saveTypeId(argThat(ti ->
                ti.getTypeId().equals("CC") &&
                ti.getTypeIdname().equals("Cédula de Ciudadanía") &&
                ti.getEntId().equals(entId) &&
                ti.getClassification() == PersonClassification.NATURAL_PERSON &&
                ti.getStatus() != null
        ));
    }

    @Test
    @DisplayName("Debe verificar una vez llamada única al puerto")
    void testCreateTypeIdLlamadaUnicaAlPuertoVerificaUnaVez() {
        // Arrange
        TypeId savedTypeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        when(idOutputPort.saveTypeId(typeId)).thenReturn(savedTypeId);

        // Act
        createTypeIdService.createTypeId(typeId);

        // Assert
        verify(idOutputPort, times(1)).saveTypeId(typeId);
    }

    // ==================== Integración ====================

    @Test
    @DisplayName("Debe guardar y retornar en proceso completo")
    void testCreateTypeIdProcesoCompletoGuardaYRetorna() {
        // Arrange
        TypeId savedTypeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        when(idOutputPort.saveTypeId(typeId)).thenReturn(savedTypeId);

        // Act
        TypeId result = createTypeIdService.createTypeId(typeId);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(1L, result.getId());
        verify(idOutputPort).saveTypeId(typeId);
    }

    @Test
    @DisplayName("Debe guardar independientemente cada uno de varios intentos")
    void testCreateTypeIdVariosIntentosCadaUnoGuardaIndependientemente() {
        // Arrange
        when(idOutputPort.saveTypeId(any(TypeId.class))).thenAnswer(invocation -> {
            TypeId input = invocation.getArgument(0);
            return TypeId.builder()
                    .id(1L)
                    .typeId(input.getTypeId())
                    .typeIdname(input.getTypeIdname())
                    .entId(input.getEntId())
                    .classification(input.getClassification())
                    .status(input.getStatus())
                    .build();
        });

        // Act
        createTypeIdService.createTypeId(typeId);
        createTypeIdService.createTypeId(typeId);
        createTypeIdService.createTypeId(typeId);

        // Assert
        verify(idOutputPort, times(3)).saveTypeId(typeId);
    }

    @Test
    @DisplayName("Debe retornar correctamente objeto guardado con ID asignado")
    void testCreateTypeIdRetornaObjetoGuardadoConIdAsignadoCorrectamente() {
        // Arrange
        TypeId savedTypeId = TypeId.builder()
                .id(100L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        when(idOutputPort.saveTypeId(typeId)).thenReturn(savedTypeId);

        // Act
        TypeId result = createTypeIdService.createTypeId(typeId);

        // Assert
        assertNotNull(result.getId());
        assertEquals(100L, result.getId());
        assertEquals("CC", result.getTypeId());
        verify(idOutputPort).saveTypeId(typeId);
    }

    @Test
    @DisplayName("Debe crear correctamente tipo de identificación con nombre largo")
    void testCreateTypeIdTypeIdConNombreLargoCreaCorrectamente() {
        // Arrange
        TypeId longNameTypeId = TypeId.builder()
                .typeId("PASS")
                .typeIdname("Pasaporte Internacional de Ciudadano Extranjero Residente Temporal")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        TypeId savedTypeId = TypeId.builder()
                .id(1L)
                .typeId("PASS")
                .typeIdname("Pasaporte Internacional de Ciudadano Extranjero Residente Temporal")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        when(idOutputPort.saveTypeId(longNameTypeId)).thenReturn(savedTypeId);

        // Act
        TypeId result = createTypeIdService.createTypeId(longNameTypeId);

        // Assert
        assertNotNull(result);
        assertEquals("Pasaporte Internacional de Ciudadano Extranjero Residente Temporal", result.getTypeIdname());
        verify(idOutputPort).saveTypeId(longNameTypeId);
    }
}
