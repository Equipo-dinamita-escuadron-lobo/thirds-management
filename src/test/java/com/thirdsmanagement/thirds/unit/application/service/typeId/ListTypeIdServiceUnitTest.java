package com.thirdsmanagement.thirds.unit.application.service.typeId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.service.typeId.ListTypeIdService;
import com.thirdsmanagement.thirds.domain.model.PersonClassification;
import com.thirdsmanagement.thirds.domain.model.TypeId;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ListTypeIdServiceUnitTest {

    @Mock
    private IdOutputPort idOutputPort;

    @InjectMocks
    private ListTypeIdService listTypeIdService;

    private String entId;
    private TypeId typeId1;
    private TypeId typeId2;
    private TypeId typeId3;

    @BeforeEach
    void setUp() {
        entId = "ENT001";

        typeId1 = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        typeId2 = TypeId.builder()
                .id(2L)
                .typeId("NIT")
                .typeIdname("NIT")
                .entId(entId)
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        typeId3 = TypeId.builder()
                .id(3L)
                .typeId("CE")
                .typeIdname("Cédula de Extranjería")
                .entId(entId)
                .classification(PersonClassification.NATURAL_PERSON)
                .status(false)
                .build();
    }

    // ==================== getAllTypeId ====================

    @Test
    @DisplayName("Debe retornar lista de tipos de identificación con ID de entidad válido")
    void testGetAllTypeIdEntIdValidoRetornaListaTiposId() {
        // Arrange
        List<TypeId> typeIds = Arrays.asList(typeId1, typeId2, typeId3);
        when(idOutputPort.getAllTypeIds(entId)).thenReturn(typeIds);

        // Act
        List<TypeId> result = listTypeIdService.getAllTypeId(entId);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(idOutputPort).getAllTypeIds(entId);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando la lista está vacía")
    void testGetAllTypeIdListaVaciaRetornaListaVacia() {
        // Arrange
        when(idOutputPort.getAllTypeIds(entId)).thenReturn(Arrays.asList());

        // Act
        List<TypeId> result = listTypeIdService.getAllTypeId(entId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(idOutputPort).getAllTypeIds(entId);
    }

    @Test
    @DisplayName("Debe retornar listas correspondientes con diferentes IDs de entidad")
    void testGetAllTypeIdDiferentesEntIdsRetornaListasCorrespondientes() {
        // Arrange
        String entId2 = "ENT002";
        List<TypeId> typeIdsEnt1 = Arrays.asList(typeId1, typeId2);
        List<TypeId> typeIdsEnt2 = Arrays.asList(typeId3);

        when(idOutputPort.getAllTypeIds(entId)).thenReturn(typeIdsEnt1);
        when(idOutputPort.getAllTypeIds(entId2)).thenReturn(typeIdsEnt2);

        // Act
        List<TypeId> result1 = listTypeIdService.getAllTypeId(entId);
        List<TypeId> result2 = listTypeIdService.getAllTypeId(entId2);

        // Assert
        assertEquals(2, result1.size());
        assertEquals(1, result2.size());
        verify(idOutputPort).getAllTypeIds(entId);
        verify(idOutputPort).getAllTypeIds(entId2);
    }

    // ==================== getAllTypeIdsWithSort ====================

    @Test
    @DisplayName("Debe retornar página ordenada con orden ascendente")
    void testGetAllTypeIdsWithSortOrdenAscendenteRetornaPaginaOrdenada() {
        // Arrange
        Page<TypeId> page = new PageImpl<>(Arrays.asList(typeId1, typeId2), PageRequest.of(0, 10), 2);
        when(idOutputPort.getAllTypeIdsWithSort(entId, 0, 10, "typeIdname", "asc")).thenReturn(page);

        // Act
        Page<TypeId> result = listTypeIdService.getAllTypeIdsWithSort(entId, 0, 10, "typeIdname", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(idOutputPort).getAllTypeIdsWithSort(entId, 0, 10, "typeIdname", "asc");
    }

    @Test
    @DisplayName("Debe retornar página ordenada con orden descendente")
    void testGetAllTypeIdsWithSortOrdenDescendenteRetornaPaginaOrdenada() {
        // Arrange
        Page<TypeId> page = new PageImpl<>(Arrays.asList(typeId2, typeId1), PageRequest.of(0, 10), 2);
        when(idOutputPort.getAllTypeIdsWithSort(entId, 0, 10, "typeIdname", "desc")).thenReturn(page);

        // Act
        Page<TypeId> result = listTypeIdService.getAllTypeIdsWithSort(entId, 0, 10, "typeIdname", "desc");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(idOutputPort).getAllTypeIdsWithSort(entId, 0, 10, "typeIdname", "desc");
    }

    @Test
    @DisplayName("Debe retornar páginas ordenadas con diferentes campos de orden")
    void testGetAllTypeIdsWithSortDiferentesCamposOrdenRetornaPaginasOrdenadas() {
        // Arrange
        Page<TypeId> pageByName = new PageImpl<>(Arrays.asList(typeId1, typeId2), PageRequest.of(0, 10), 2);
        Page<TypeId> pageById = new PageImpl<>(Arrays.asList(typeId1, typeId2), PageRequest.of(0, 10), 2);

        when(idOutputPort.getAllTypeIdsWithSort(entId, 0, 10, "typeIdname", "asc")).thenReturn(pageByName);
        when(idOutputPort.getAllTypeIdsWithSort(entId, 0, 10, "id", "asc")).thenReturn(pageById);

        // Act
        Page<TypeId> result1 = listTypeIdService.getAllTypeIdsWithSort(entId, 0, 10, "typeIdname", "asc");
        Page<TypeId> result2 = listTypeIdService.getAllTypeIdsWithSort(entId, 0, 10, "id", "asc");

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        verify(idOutputPort).getAllTypeIdsWithSort(entId, 0, 10, "typeIdname", "asc");
        verify(idOutputPort).getAllTypeIdsWithSort(entId, 0, 10, "id", "asc");
    }

    @Test
    @DisplayName("Debe retornar página con tamaño correcto cuando página es de diferente tamaño")
    void testGetAllTypeIdsWithSortPaginaDiferenteTamañoRetornaPaginaConTamañoCorrecto() {
        // Arrange
        Page<TypeId> page = new PageImpl<>(Arrays.asList(typeId1, typeId2, typeId3), PageRequest.of(0, 20), 3);
        when(idOutputPort.getAllTypeIdsWithSort(entId, 0, 20, "typeIdname", "asc")).thenReturn(page);

        // Act
        Page<TypeId> result = listTypeIdService.getAllTypeIdsWithSort(entId, 0, 20, "typeIdname", "asc");

        // Assert
        assertEquals(3, result.getContent().size());
        verify(idOutputPort).getAllTypeIdsWithSort(entId, 0, 20, "typeIdname", "asc");
    }

    // ==================== getAllActiveTypeIds ====================

    @Test
    @DisplayName("Debe retornar solo activos cuando hay tipos de identificación activos")
    void testGetAllActiveTypeIdsTiposIdActivosRetornaSoloActivos() {
        // Arrange
        Page<TypeId> page = new PageImpl<>(Arrays.asList(typeId1, typeId2), PageRequest.of(0, 10), 2);
        when(idOutputPort.getAllActiveTypeIds(entId, 0, 10)).thenReturn(page);

        // Act
        Page<TypeId> result = listTypeIdService.getAllActiveTypeIds(entId, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().allMatch(TypeId::getStatus));
        verify(idOutputPort).getAllActiveTypeIds(entId, 0, 10);
    }

    @Test
    @DisplayName("Debe retornar página vacía cuando no hay tipos de identificación activos")
    void testGetAllActiveTypeIdsSinTiposIdActivosRetornaPaginaVacia() {
        // Arrange
        Page<TypeId> page = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);
        when(idOutputPort.getAllActiveTypeIds(entId, 0, 10)).thenReturn(page);

        // Act
        Page<TypeId> result = listTypeIdService.getAllActiveTypeIds(entId, 0, 10);

        // Assert
        assertTrue(result.getContent().isEmpty());
        verify(idOutputPort).getAllActiveTypeIds(entId, 0, 10);
    }

    @Test
    @DisplayName("Debe retornar páginas correctas con diferentes páginas")
    void testGetAllActiveTypeIdsDiferentesPaginasRetornaPaginasCorrectas() {
        // Arrange
        Page<TypeId> page1 = new PageImpl<>(Arrays.asList(typeId1), PageRequest.of(0, 1), 2);
        Page<TypeId> page2 = new PageImpl<>(Arrays.asList(typeId2), PageRequest.of(1, 1), 2);

        when(idOutputPort.getAllActiveTypeIds(entId, 0, 1)).thenReturn(page1);
        when(idOutputPort.getAllActiveTypeIds(entId, 1, 1)).thenReturn(page2);

        // Act
        Page<TypeId> result1 = listTypeIdService.getAllActiveTypeIds(entId, 0, 1);
        Page<TypeId> result2 = listTypeIdService.getAllActiveTypeIds(entId, 1, 1);

        // Assert
        assertEquals(1, result1.getContent().size());
        assertEquals(1, result2.getContent().size());
        verify(idOutputPort).getAllActiveTypeIds(entId, 0, 1);
        verify(idOutputPort).getAllActiveTypeIds(entId, 1, 1);
    }

    // ==================== findByEntIdAndSearch ====================

    @Test
    @DisplayName("Debe retornar coincidencias con término de búsqueda")
    void testFindByEntIdAndSearchTerminoBusquedaRetornaCoincidencias() {
        // Arrange
        String search = "Cédula";
        Page<TypeId> page = new PageImpl<>(Arrays.asList(typeId1, typeId3), PageRequest.of(0, 10), 2);
        when(idOutputPort.findByEntIdAndSearch(entId, search, 0, 10, "typeIdname", "asc")).thenReturn(page);

        // Act
        Page<TypeId> result = listTypeIdService.findByEntIdAndSearch(entId, search, 0, 10, "typeIdname", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(idOutputPort).findByEntIdAndSearch(entId, search, 0, 10, "typeIdname", "asc");
    }

    @Test
    @DisplayName("Debe retornar página vacía sin coincidencias")
    void testFindByEntIdAndSearchSinCoincidenciasRetornaPaginaVacia() {
        // Arrange
        String search = "Pasaporte";
        Page<TypeId> page = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);
        when(idOutputPort.findByEntIdAndSearch(entId, search, 0, 10, "typeIdname", "asc")).thenReturn(page);

        // Act
        Page<TypeId> result = listTypeIdService.findByEntIdAndSearch(entId, search, 0, 10, "typeIdname", "asc");

        // Assert
        assertTrue(result.getContent().isEmpty());
        verify(idOutputPort).findByEntIdAndSearch(entId, search, 0, 10, "typeIdname", "asc");
    }

    @Test
    @DisplayName("Debe retornar coincidencias correspondientes con diferentes términos")
    void testFindByEntIdAndSearchDiferentesTerminosRetornaCoincidenciasCorrespondientes() {
        // Arrange
        String search1 = "CC";
        String search2 = "NIT";
        Page<TypeId> page1 = new PageImpl<>(Arrays.asList(typeId1), PageRequest.of(0, 10), 1);
        Page<TypeId> page2 = new PageImpl<>(Arrays.asList(typeId2), PageRequest.of(0, 10), 1);

        when(idOutputPort.findByEntIdAndSearch(entId, search1, 0, 10, "typeIdname", "asc")).thenReturn(page1);
        when(idOutputPort.findByEntIdAndSearch(entId, search2, 0, 10, "typeIdname", "asc")).thenReturn(page2);

        // Act
        Page<TypeId> result1 = listTypeIdService.findByEntIdAndSearch(entId, search1, 0, 10, "typeIdname", "asc");
        Page<TypeId> result2 = listTypeIdService.findByEntIdAndSearch(entId, search2, 0, 10, "typeIdname", "asc");

        // Assert
        assertEquals(1, result1.getContent().size());
        assertEquals(1, result2.getContent().size());
        verify(idOutputPort).findByEntIdAndSearch(entId, search1, 0, 10, "typeIdname", "asc");
        verify(idOutputPort).findByEntIdAndSearch(entId, search2, 0, 10, "typeIdname", "asc");
    }

    // ==================== countByEntId ====================

    @Test
    @DisplayName("Debe retornar cantidad correcta con ID de entidad que tiene tipos de identificación")
    void testCountByEntIdEntIdConTiposIdRetornaCantidadCorrecta() {
        // Arrange
        when(idOutputPort.countByEntId(entId)).thenReturn(3L);

        // Act
        long result = listTypeIdService.countByEntId(entId);

        // Assert
        assertEquals(3L, result);
        verify(idOutputPort).countByEntId(entId);
    }

    @Test
    @DisplayName("Debe retornar cero con ID de entidad sin tipos de identificación")
    void testCountByEntIdEntIdSinTiposIdRetornaCero() {
        // Arrange
        when(idOutputPort.countByEntId(entId)).thenReturn(0L);

        // Act
        long result = listTypeIdService.countByEntId(entId);

        // Assert
        assertEquals(0L, result);
        verify(idOutputPort).countByEntId(entId);
    }

    @Test
    @DisplayName("Debe retornar cantidades correspondientes con diferentes IDs de entidad")
    void testCountByEntIdDiferentesEntIdsRetornaCantidadesCorrespondientes() {
        // Arrange
        String entId2 = "ENT002";
        when(idOutputPort.countByEntId(entId)).thenReturn(3L);
        when(idOutputPort.countByEntId(entId2)).thenReturn(1L);

        // Act
        long result1 = listTypeIdService.countByEntId(entId);
        long result2 = listTypeIdService.countByEntId(entId2);

        // Assert
        assertEquals(3L, result1);
        assertEquals(1L, result2);
        verify(idOutputPort).countByEntId(entId);
        verify(idOutputPort).countByEntId(entId2);
    }

    // ==================== countActiveByEntId ====================

    @Test
    @DisplayName("Debe retornar cantidad de activos con ID de entidad con activos")
    void testCountActiveByEntIdEntIdConActivosRetornaCantidadActivos() {
        // Arrange
        when(idOutputPort.countActiveByEntId(entId)).thenReturn(2L);

        // Act
        long result = listTypeIdService.countActiveByEntId(entId);

        // Assert
        assertEquals(2L, result);
        verify(idOutputPort).countActiveByEntId(entId);
    }

    @Test
    @DisplayName("Debe retornar cero con ID de entidad sin activos")
    void testCountActiveByEntIdEntIdSinActivosRetornaCero() {
        // Arrange
        when(idOutputPort.countActiveByEntId(entId)).thenReturn(0L);

        // Act
        long result = listTypeIdService.countActiveByEntId(entId);

        // Assert
        assertEquals(0L, result);
        verify(idOutputPort).countActiveByEntId(entId);
    }

    @Test
    @DisplayName("Debe retornar cantidades correspondientes con diferentes IDs de entidad")
    void testCountActiveByEntIdDiferentesEntIdsRetornaCantidadesCorrespondientes() {
        // Arrange
        String entId2 = "ENT002";
        when(idOutputPort.countActiveByEntId(entId)).thenReturn(2L);
        when(idOutputPort.countActiveByEntId(entId2)).thenReturn(1L);

        // Act
        long result1 = listTypeIdService.countActiveByEntId(entId);
        long result2 = listTypeIdService.countActiveByEntId(entId2);

        // Assert
        assertEquals(2L, result1);
        assertEquals(1L, result2);
        verify(idOutputPort).countActiveByEntId(entId);
        verify(idOutputPort).countActiveByEntId(entId2);
    }

    // ==================== countByEntIdAndSearch ====================

    @Test
    @DisplayName("Debe retornar cantidad de coincidencias con término con coincidencias")
    void testCountByEntIdAndSearchTerminoConCoincidenciasRetornaCantidadCoincidencias() {
        // Arrange
        String search = "Cédula";
        when(idOutputPort.countByEntIdAndSearch(entId, search)).thenReturn(2L);

        // Act
        long result = listTypeIdService.countByEntIdAndSearch(entId, search);

        // Assert
        assertEquals(2L, result);
        verify(idOutputPort).countByEntIdAndSearch(entId, search);
    }

    @Test
    @DisplayName("Debe retornar cero con término sin coincidencias")
    void testCountByEntIdAndSearchTerminoSinCoincidenciasRetornaCero() {
        // Arrange
        String search = "Pasaporte";
        when(idOutputPort.countByEntIdAndSearch(entId, search)).thenReturn(0L);

        // Act
        long result = listTypeIdService.countByEntIdAndSearch(entId, search);

        // Assert
        assertEquals(0L, result);
        verify(idOutputPort).countByEntIdAndSearch(entId, search);
    }

    @Test
    @DisplayName("Debe retornar cantidades correspondientes con diferentes términos")
    void testCountByEntIdAndSearchDiferentesTerminosRetornaCantidadesCorrespondientes() {
        // Arrange
        String search1 = "CC";
        String search2 = "NIT";
        when(idOutputPort.countByEntIdAndSearch(entId, search1)).thenReturn(1L);
        when(idOutputPort.countByEntIdAndSearch(entId, search2)).thenReturn(1L);

        // Act
        long result1 = listTypeIdService.countByEntIdAndSearch(entId, search1);
        long result2 = listTypeIdService.countByEntIdAndSearch(entId, search2);

        // Assert
        assertEquals(1L, result1);
        assertEquals(1L, result2);
        verify(idOutputPort).countByEntIdAndSearch(entId, search1);
        verify(idOutputPort).countByEntIdAndSearch(entId, search2);
    }

    // ==================== Propagación de excepciones ====================

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en puerto")
    void testGetAllTypeIdErrorEnPuertoPropagaExcepcion() {
        // Arrange
        when(idOutputPort.getAllTypeIds(entId)).thenThrow(new RuntimeException("Error de base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> listTypeIdService.getAllTypeId(entId));
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en puerto")
    void testGetAllTypeIdsWithSortErrorEnPuertoPropagaExcepcion() {
        // Arrange
        when(idOutputPort.getAllTypeIdsWithSort(entId, 0, 10, "typeIdname", "asc"))
                .thenThrow(new RuntimeException("Error de conexión"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> listTypeIdService.getAllTypeIdsWithSort(entId, 0, 10, "typeIdname", "asc"));
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en puerto")
    void testGetAllActiveTypeIdsErrorEnPuertoPropagaExcepcion() {
        // Arrange
        when(idOutputPort.getAllActiveTypeIds(entId, 0, 10))
                .thenThrow(new RuntimeException("Error al obtener activos"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> listTypeIdService.getAllActiveTypeIds(entId, 0, 10));
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en puerto")
    void testFindByEntIdAndSearchErrorEnPuertoPropagaExcepcion() {
        // Arrange
        when(idOutputPort.findByEntIdAndSearch(entId, "search", 0, 10, "typeIdname", "asc"))
                .thenThrow(new RuntimeException("Error en búsqueda"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> listTypeIdService.findByEntIdAndSearch(entId, "search", 0, 10, "typeIdname", "asc"));
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en puerto")
    void testCountByEntIdErrorEnPuertoPropagaExcepcion() {
        // Arrange
        when(idOutputPort.countByEntId(entId)).thenThrow(new RuntimeException("Error al contar"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> listTypeIdService.countByEntId(entId));
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en puerto")
    void testCountActiveByEntIdErrorEnPuertoPropagaExcepcion() {
        // Arrange
        when(idOutputPort.countActiveByEntId(entId)).thenThrow(new RuntimeException("Error al contar activos"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> listTypeIdService.countActiveByEntId(entId));
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en puerto")
    void testCountByEntIdAndSearchErrorEnPuertoPropagaExcepcion() {
        // Arrange
        when(idOutputPort.countByEntIdAndSearch(entId, "search"))
                .thenThrow(new RuntimeException("Error al contar búsqueda"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> listTypeIdService.countByEntIdAndSearch(entId, "search"));
    }

    // ==================== Delegación correcta ====================

    @Test
    @DisplayName("Debe delegar correctamente al puerto para todos los métodos")
    void testListTypeIdServiceDelegaCorrectamenteAlPuertoParaTodosMetodos() {
        // Arrange
        List<TypeId> list = Arrays.asList(typeId1);
        Page<TypeId> page = new PageImpl<>(list, PageRequest.of(0, 10), 1);

        when(idOutputPort.getAllTypeIds(entId)).thenReturn(list);
        when(idOutputPort.getAllTypeIdsWithSort(entId, 0, 10, "typeIdname", "asc")).thenReturn(page);
        when(idOutputPort.getAllActiveTypeIds(entId, 0, 10)).thenReturn(page);
        when(idOutputPort.findByEntIdAndSearch(entId, "search", 0, 10, "typeIdname", "asc")).thenReturn(page);
        when(idOutputPort.countByEntId(entId)).thenReturn(1L);
        when(idOutputPort.countActiveByEntId(entId)).thenReturn(1L);
        when(idOutputPort.countByEntIdAndSearch(entId, "search")).thenReturn(1L);

        // Act
        listTypeIdService.getAllTypeId(entId);
        listTypeIdService.getAllTypeIdsWithSort(entId, 0, 10, "typeIdname", "asc");
        listTypeIdService.getAllActiveTypeIds(entId, 0, 10);
        listTypeIdService.findByEntIdAndSearch(entId, "search", 0, 10, "typeIdname", "asc");
        listTypeIdService.countByEntId(entId);
        listTypeIdService.countActiveByEntId(entId);
        listTypeIdService.countByEntIdAndSearch(entId, "search");

        // Assert
        verify(idOutputPort, times(1)).getAllTypeIds(entId);
        verify(idOutputPort, times(1)).getAllTypeIdsWithSort(entId, 0, 10, "typeIdname", "asc");
        verify(idOutputPort, times(1)).getAllActiveTypeIds(entId, 0, 10);
        verify(idOutputPort, times(1)).findByEntIdAndSearch(entId, "search", 0, 10, "typeIdname", "asc");
        verify(idOutputPort, times(1)).countByEntId(entId);
        verify(idOutputPort, times(1)).countActiveByEntId(entId);
        verify(idOutputPort, times(1)).countByEntIdAndSearch(entId, "search");
    }

    // ==================== Integración ====================

    @Test
    @DisplayName("Debe funcionar correctamente en proceso completo de listado y conteo")
    void testListTypeIdServiceProcesoCompletoListadoYConteoFuncionaCorrectamente() {
        // Arrange
        List<TypeId> allTypeIds = Arrays.asList(typeId1, typeId2, typeId3);
        Page<TypeId> activePage = new PageImpl<>(Arrays.asList(typeId1, typeId2), PageRequest.of(0, 10), 2);

        when(idOutputPort.getAllTypeIds(entId)).thenReturn(allTypeIds);
        when(idOutputPort.getAllActiveTypeIds(entId, 0, 10)).thenReturn(activePage);
        when(idOutputPort.countByEntId(entId)).thenReturn(3L);
        when(idOutputPort.countActiveByEntId(entId)).thenReturn(2L);

        // Act
        List<TypeId> allResults = listTypeIdService.getAllTypeId(entId);
        Page<TypeId> activeResults = listTypeIdService.getAllActiveTypeIds(entId, 0, 10);
        long totalCount = listTypeIdService.countByEntId(entId);
        long activeCount = listTypeIdService.countActiveByEntId(entId);

        // Assert
        assertEquals(3, allResults.size());
        assertEquals(2, activeResults.getContent().size());
        assertEquals(3L, totalCount);
        assertEquals(2L, activeCount);
    }
}
