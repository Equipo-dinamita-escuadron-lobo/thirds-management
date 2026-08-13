package com.thirdsmanagement.thirds.unit.application.service.thirdType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
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
import com.thirdsmanagement.thirds.application.service.thirdType.DefaultThirdTypesService;
import com.thirdsmanagement.thirds.application.service.thirdType.ListThirdTypeService;
import com.thirdsmanagement.thirds.domain.model.ThirdType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ListThirdTypeServiceUnitTest {

    @Mock
    private IdOutputPort idOutputPort;

    @Mock
    private DefaultThirdTypesService defaultThirdTypesService;

    @InjectMocks
    private ListThirdTypeService listThirdTypeService;

    private String entId;
    private ThirdType thirdType1;
    private ThirdType thirdType2;
    private ThirdType thirdType3;

    @BeforeEach
    void setUp() {
        entId = "ENT001";
        
        thirdType1 = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .entId(entId)
                .status(true)
                .build();

        thirdType2 = ThirdType.builder()
                .thirdTypeId(2L)
                .thirdTypeName("Proveedor")
                .entId(entId)
                .status(true)
                .build();

        thirdType3 = ThirdType.builder()
                .thirdTypeId(3L)
                .thirdTypeName("Empleado")
                .entId(entId)
                .status(false)
                .build();
    }

    // ==================== getAllThirdTypes ====================

    @Test
    @DisplayName("Debe retornar lista cuando hay datos")
    void testGetAllThirdTypesConDatosRetornaLista() {
        // Arrange
        List<ThirdType> expectedList = Arrays.asList(thirdType1, thirdType2, thirdType3);
        when(idOutputPort.getALLThirdTypes(entId)).thenReturn(expectedList);

        // Act
        List<ThirdType> result = listThirdTypeService.getAllThirdTypes(entId);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Cliente", result.get(0).getThirdTypeName());
        assertEquals("Proveedor", result.get(1).getThirdTypeName());
        assertEquals("Empleado", result.get(2).getThirdTypeName());
        verify(idOutputPort, times(1)).getALLThirdTypes(entId);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay datos")
    void testGetAllThirdTypesSinDatosRetornaListaVacia() {
        // Arrange
        when(idOutputPort.getALLThirdTypes(entId)).thenReturn(Collections.emptyList());

        // Act
        List<ThirdType> result = listThirdTypeService.getAllThirdTypes(entId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(idOutputPort, times(1)).getALLThirdTypes(entId);
    }

    @Test
    @DisplayName("Debe retornar listas por entidad con diferentes IDs de entidad")
    void testGetAllThirdTypesDiferentesEntIdsRetornaListasPorEntidad() {
        // Arrange
        String entId2 = "ENT002";
        List<ThirdType> listEnt1 = Arrays.asList(thirdType1, thirdType2);
        List<ThirdType> listEnt2 = Arrays.asList(thirdType3);

        when(idOutputPort.getALLThirdTypes(entId)).thenReturn(listEnt1);
        when(idOutputPort.getALLThirdTypes(entId2)).thenReturn(listEnt2);

        // Act
        List<ThirdType> result1 = listThirdTypeService.getAllThirdTypes(entId);
        List<ThirdType> result2 = listThirdTypeService.getAllThirdTypes(entId2);

        // Assert
        assertEquals(2, result1.size());
        assertEquals(1, result2.size());
        verify(idOutputPort).getALLThirdTypes(entId);
        verify(idOutputPort).getALLThirdTypes(entId2);
    }

    // ==================== getAllThirdTypesWithSort ====================

    @Test
    @DisplayName("Debe retornar página ordenada con paginación")
    void testGetAllThirdTypesWithSortConPaginacionRetornaPaginaOrdenada() {
        // Arrange
        Page<ThirdType> expectedPage = new PageImpl<>(Arrays.asList(thirdType1, thirdType2), 
                PageRequest.of(0, 10), 2);
        when(idOutputPort.getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc"))
                .thenReturn(expectedPage);

        // Act
        Page<ThirdType> result = listThirdTypeService.getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(idOutputPort, times(1)).getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc");
    }

    @Test
    @DisplayName("Debe retornar página ordenada en orden descendente")
    void testGetAllThirdTypesWithSortOrdenDescendenteRetornaPaginaOrdenada() {
        // Arrange
        Page<ThirdType> expectedPage = new PageImpl<>(Arrays.asList(thirdType2, thirdType1), 
                PageRequest.of(0, 10), 2);
        when(idOutputPort.getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "desc"))
                .thenReturn(expectedPage);

        // Act
        Page<ThirdType> result = listThirdTypeService.getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "desc");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Proveedor", result.getContent().get(0).getThirdTypeName());
        verify(idOutputPort, times(1)).getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "desc");
    }

    @Test
    @DisplayName("Debe delegar correctamente con diferentes campos de ordenamiento")
    void testGetAllThirdTypesWithSortDiferentesCamposOrdenamientoDelegaCorrectamente() {
        // Arrange
        Page<ThirdType> page1 = new PageImpl<>(Arrays.asList(thirdType1));
        Page<ThirdType> page2 = new PageImpl<>(Arrays.asList(thirdType2));

        when(idOutputPort.getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc")).thenReturn(page1);
        when(idOutputPort.getAllThirdTypesWithSort(entId, 0, 10, "status", "desc")).thenReturn(page2);

        // Act
        listThirdTypeService.getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc");
        listThirdTypeService.getAllThirdTypesWithSort(entId, 0, 10, "status", "desc");

        // Assert
        verify(idOutputPort).getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc");
        verify(idOutputPort).getAllThirdTypesWithSort(entId, 0, 10, "status", "desc");
    }

    @Test
    @DisplayName("Debe retornar página vacía cuando página está vacía")
    void testGetAllThirdTypesWithSortPaginaVaciaRetornaPaginaVacia() {
        // Arrange
        Page<ThirdType> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(idOutputPort.getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc"))
                .thenReturn(emptyPage);

        // Act
        Page<ThirdType> result = listThirdTypeService.getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc");

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(idOutputPort, times(1)).getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc");
    }

    // ==================== findThirdTypesByEntIdAndSearch ====================

    @Test
    @DisplayName("Debe retornar coincidencias con término de búsqueda")
    void testFindThirdTypesByEntIdAndSearchConTerminoBusquedaRetornaCoincidencias() {
        // Arrange
        Page<ThirdType> expectedPage = new PageImpl<>(Arrays.asList(thirdType1), 
                PageRequest.of(0, 10), 1);
        when(idOutputPort.findThirdTypesByEntIdAndSearch(entId, "Cliente", 0, 10, "thirdTypeName", "asc"))
                .thenReturn(expectedPage);

        // Act
        Page<ThirdType> result = listThirdTypeService.findThirdTypesByEntIdAndSearch(entId, "Cliente", 0, 10, "thirdTypeName", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Cliente", result.getContent().get(0).getThirdTypeName());
        verify(idOutputPort, times(1)).findThirdTypesByEntIdAndSearch(entId, "Cliente", 0, 10, "thirdTypeName", "asc");
    }

    @Test
    @DisplayName("Debe retornar página vacía sin coincidencias")
    void testFindThirdTypesByEntIdAndSearchSinCoincidenciasRetornaPaginaVacia() {
        // Arrange
        Page<ThirdType> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(idOutputPort.findThirdTypesByEntIdAndSearch(entId, "NoExiste", 0, 10, "thirdTypeName", "asc"))
                .thenReturn(emptyPage);

        // Act
        Page<ThirdType> result = listThirdTypeService.findThirdTypesByEntIdAndSearch(entId, "NoExiste", 0, 10, "thirdTypeName", "asc");

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(idOutputPort, times(1)).findThirdTypesByEntIdAndSearch(entId, "NoExiste", 0, 10, "thirdTypeName", "asc");
    }

    @Test
    @DisplayName("Debe retornar página con resultados cuando hay varias coincidencias")
    void testFindThirdTypesByEntIdAndSearchVariasCoincidenciasRetornaPaginaConResultados() {
        // Arrange
        Page<ThirdType> expectedPage = new PageImpl<>(Arrays.asList(thirdType1, thirdType2), 
                PageRequest.of(0, 10), 2);
        when(idOutputPort.findThirdTypesByEntIdAndSearch(entId, "e", 0, 10, "thirdTypeName", "asc"))
                .thenReturn(expectedPage);

        // Act
        Page<ThirdType> result = listThirdTypeService.findThirdTypesByEntIdAndSearch(entId, "e", 0, 10, "thirdTypeName", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(idOutputPort, times(1)).findThirdTypesByEntIdAndSearch(entId, "e", 0, 10, "thirdTypeName", "asc");
    }

    // ==================== countThirdTypesByEntId ====================

    @Test
    @DisplayName("Debe retornar cantidad cuando hay datos")
    void testCountThirdTypesByEntIdConDatosRetornaCantidad() {
        // Arrange
        when(idOutputPort.countThirdTypesByEntId(entId)).thenReturn(3L);

        // Act
        long result = listThirdTypeService.countThirdTypesByEntId(entId);

        // Assert
        assertEquals(3L, result);
        verify(idOutputPort, times(1)).countThirdTypesByEntId(entId);
    }

    @Test
    @DisplayName("Debe retornar cero cuando no hay datos")
    void testCountThirdTypesByEntIdSinDatosRetornaCero() {
        // Arrange
        when(idOutputPort.countThirdTypesByEntId(entId)).thenReturn(0L);

        // Act
        long result = listThirdTypeService.countThirdTypesByEntId(entId);

        // Assert
        assertEquals(0L, result);
        verify(idOutputPort, times(1)).countThirdTypesByEntId(entId);
    }

    @Test
    @DisplayName("Debe retornar cantidades independientes con diferentes entidades")
    void testCountThirdTypesByEntIdDiferentesEntidadesRetornaCantidadesIndependientes() {
        // Arrange
        String entId2 = "ENT002";
        when(idOutputPort.countThirdTypesByEntId(entId)).thenReturn(5L);
        when(idOutputPort.countThirdTypesByEntId(entId2)).thenReturn(3L);

        // Act
        long result1 = listThirdTypeService.countThirdTypesByEntId(entId);
        long result2 = listThirdTypeService.countThirdTypesByEntId(entId2);

        // Assert
        assertEquals(5L, result1);
        assertEquals(3L, result2);
        verify(idOutputPort).countThirdTypesByEntId(entId);
        verify(idOutputPort).countThirdTypesByEntId(entId2);
    }

    // ==================== countThirdTypesByEntIdAndSearch ====================

    @Test
    @DisplayName("Debe retornar cantidad con coincidencias")
    void testCountThirdTypesByEntIdAndSearchConCoincidenciasRetornaCantidad() {
        // Arrange
        when(idOutputPort.countThirdTypesByEntIdAndSearch(entId, "Cliente")).thenReturn(1L);

        // Act
        long result = listThirdTypeService.countThirdTypesByEntIdAndSearch(entId, "Cliente");

        // Assert
        assertEquals(1L, result);
        verify(idOutputPort, times(1)).countThirdTypesByEntIdAndSearch(entId, "Cliente");
    }

    @Test
    @DisplayName("Debe retornar cero sin coincidencias")
    void testCountThirdTypesByEntIdAndSearchSinCoincidenciasRetornaCero() {
        // Arrange
        when(idOutputPort.countThirdTypesByEntIdAndSearch(entId, "NoExiste")).thenReturn(0L);

        // Act
        long result = listThirdTypeService.countThirdTypesByEntIdAndSearch(entId, "NoExiste");

        // Assert
        assertEquals(0L, result);
        verify(idOutputPort, times(1)).countThirdTypesByEntIdAndSearch(entId, "NoExiste");
    }

    @Test
    @DisplayName("Debe retornar cantidad total con varias coincidencias")
    void testCountThirdTypesByEntIdAndSearchVariasCoincidenciasRetornaCantidadTotal() {
        // Arrange
        when(idOutputPort.countThirdTypesByEntIdAndSearch(entId, "e")).thenReturn(3L);

        // Act
        long result = listThirdTypeService.countThirdTypesByEntIdAndSearch(entId, "e");

        // Assert
        assertEquals(3L, result);
        verify(idOutputPort, times(1)).countThirdTypesByEntIdAndSearch(entId, "e");
    }

    // ==================== countActiveThirdTypesByEntId ====================

    @Test
    @DisplayName("Debe retornar cantidad con datos activos")
    void testCountActiveThirdTypesByEntIdConDatosActivosRetornaCantidad() {
        // Arrange
        when(idOutputPort.countActiveThirdTypesByEntId(entId)).thenReturn(2L);

        // Act
        long result = listThirdTypeService.countActiveThirdTypesByEntId(entId);

        // Assert
        assertEquals(2L, result);
        verify(idOutputPort, times(1)).countActiveThirdTypesByEntId(entId);
    }

    @Test
    @DisplayName("Debe retornar cero sin datos activos")
    void testCountActiveThirdTypesByEntIdSinDatosActivosRetornaCero() {
        // Arrange
        when(idOutputPort.countActiveThirdTypesByEntId(entId)).thenReturn(0L);

        // Act
        long result = listThirdTypeService.countActiveThirdTypesByEntId(entId);

        // Assert
        assertEquals(0L, result);
        verify(idOutputPort, times(1)).countActiveThirdTypesByEntId(entId);
    }

    @Test
    @DisplayName("Debe retornar cantidades independientes con diferentes entidades")
    void testCountActiveThirdTypesByEntIdDiferentesEntidadesRetornaCantidadesIndependientes() {
        // Arrange
        String entId2 = "ENT002";
        when(idOutputPort.countActiveThirdTypesByEntId(entId)).thenReturn(4L);
        when(idOutputPort.countActiveThirdTypesByEntId(entId2)).thenReturn(1L);

        // Act
        long result1 = listThirdTypeService.countActiveThirdTypesByEntId(entId);
        long result2 = listThirdTypeService.countActiveThirdTypesByEntId(entId2);

        // Assert
        assertEquals(4L, result1);
        assertEquals(1L, result2);
        verify(idOutputPort).countActiveThirdTypesByEntId(entId);
        verify(idOutputPort).countActiveThirdTypesByEntId(entId2);
    }

    // ==================== getAllActiveThirdTypes ====================

    @Test
    @DisplayName("Debe retornar página con datos activos")
    void testGetAllActiveThirdTypesConDatosActivosRetornaPagina() {
        // Arrange
        Page<ThirdType> expectedPage = new PageImpl<>(Arrays.asList(thirdType1, thirdType2), 
                PageRequest.of(0, 10), 2);
        when(idOutputPort.getAllActiveThirdTypes(entId, 0, 10)).thenReturn(expectedPage);

        // Act
        Page<ThirdType> result = listThirdTypeService.getAllActiveThirdTypes(entId, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().get(0).getStatus());
        assertTrue(result.getContent().get(1).getStatus());
        verify(idOutputPort, times(1)).getAllActiveThirdTypes(entId, 0, 10);
    }

    @Test
    @DisplayName("Debe retornar página vacía sin datos activos")
    void testGetAllActiveThirdTypesSinDatosActivosRetornaPaginaVacia() {
        // Arrange
        Page<ThirdType> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(idOutputPort.getAllActiveThirdTypes(entId, 0, 10)).thenReturn(emptyPage);

        // Act
        Page<ThirdType> result = listThirdTypeService.getAllActiveThirdTypes(entId, 0, 10);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(idOutputPort, times(1)).getAllActiveThirdTypes(entId, 0, 10);
    }

    @Test
    @DisplayName("Debe retornar páginas correctamente con diferentes páginas")
    void testGetAllActiveThirdTypesDiferentesPaginasRetornaPaginasCorrectamente() {
        // Arrange
        Page<ThirdType> page1 = new PageImpl<>(Arrays.asList(thirdType1), PageRequest.of(0, 1), 2);
        Page<ThirdType> page2 = new PageImpl<>(Arrays.asList(thirdType2), PageRequest.of(1, 1), 2);

        when(idOutputPort.getAllActiveThirdTypes(entId, 0, 1)).thenReturn(page1);
        when(idOutputPort.getAllActiveThirdTypes(entId, 1, 1)).thenReturn(page2);

        // Act
        Page<ThirdType> result1 = listThirdTypeService.getAllActiveThirdTypes(entId, 0, 1);
        Page<ThirdType> result2 = listThirdTypeService.getAllActiveThirdTypes(entId, 1, 1);

        // Assert
        assertEquals(1, result1.getContent().size());
        assertEquals(1, result2.getContent().size());
        assertEquals("Cliente", result1.getContent().get(0).getThirdTypeName());
        assertEquals("Proveedor", result2.getContent().get(0).getThirdTypeName());
        verify(idOutputPort).getAllActiveThirdTypes(entId, 0, 1);
        verify(idOutputPort).getAllActiveThirdTypes(entId, 1, 1);
    }

    // ==================== Propagación de excepciones ====================

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en puerto")
    void testGetAllThirdTypesErrorEnPuertoPropagaExcepcion() {
        // Arrange
        when(idOutputPort.getALLThirdTypes(entId))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> listThirdTypeService.getAllThirdTypes(entId));
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en puerto con ordenamiento")
    void testGetAllThirdTypesWithSortErrorEnPuertoPropagaExcepcion() {
        // Arrange
        when(idOutputPort.getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc"))
                .thenThrow(new RuntimeException("Error de conexión"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> listThirdTypeService.getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc"));
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en puerto con búsqueda")
    void testFindThirdTypesByEntIdAndSearchErrorEnPuertoPropagaExcepcion() {
        // Arrange
        when(idOutputPort.findThirdTypesByEntIdAndSearch(entId, "Cliente", 0, 10, "thirdTypeName", "asc"))
                .thenThrow(new RuntimeException("Error al buscar"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> listThirdTypeService.findThirdTypesByEntIdAndSearch(entId, "Cliente", 0, 10, "thirdTypeName", "asc"));
    }

    @Test
    @DisplayName("Debe propagar excepción cuando hay error en puerto al contar")
    void testCountThirdTypesByEntIdErrorEnPuertoPropagaExcepcion() {
        // Arrange
        when(idOutputPort.countThirdTypesByEntId(entId))
                .thenThrow(new RuntimeException("Error al contar"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> listThirdTypeService.countThirdTypesByEntId(entId));
    }

    // ==================== Delegación correcta ====================

    @Test
    @DisplayName("Debe delegar al puerto correctamente")
    void testGetAllThirdTypesDelegaAlPuertoCorrectamente() {
        // Arrange
        when(idOutputPort.getALLThirdTypes(entId)).thenReturn(Arrays.asList(thirdType1));

        // Act
        listThirdTypeService.getAllThirdTypes(entId);

        // Assert
        verify(idOutputPort, times(1)).getALLThirdTypes(entId);
        verifyNoMoreInteractions(idOutputPort);
    }

    @Test
    @DisplayName("Debe delegar al puerto con parámetros correctos con ordenamiento")
    void testGetAllThirdTypesWithSortDelegaAlPuertoConParametrosCorrectos() {
        // Arrange
        Page<ThirdType> page = new PageImpl<>(Arrays.asList(thirdType1));
        when(idOutputPort.getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc"))
                .thenReturn(page);

        // Act
        listThirdTypeService.getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc");

        // Assert
        verify(idOutputPort, times(1)).getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc");
        verifyNoMoreInteractions(idOutputPort);
    }

    @Test
    @DisplayName("Debe delegar al puerto con parámetros correctos con búsqueda")
    void testFindThirdTypesByEntIdAndSearchDelegaAlPuertoConParametrosCorrectos() {
        // Arrange
        Page<ThirdType> page = new PageImpl<>(Arrays.asList(thirdType1));
        when(idOutputPort.findThirdTypesByEntIdAndSearch(entId, "Cliente", 0, 10, "thirdTypeName", "asc"))
                .thenReturn(page);

        // Act
        listThirdTypeService.findThirdTypesByEntIdAndSearch(entId, "Cliente", 0, 10, "thirdTypeName", "asc");

        // Assert
        verify(idOutputPort, times(1)).findThirdTypesByEntIdAndSearch(entId, "Cliente", 0, 10, "thirdTypeName", "asc");
        verifyNoMoreInteractions(idOutputPort);
    }

    @Test
    @DisplayName("Debe delegar al puerto correctamente al contar")
    void testCountThirdTypesByEntIdDelegaAlPuertoCorrectamente() {
        // Arrange
        when(idOutputPort.countThirdTypesByEntId(entId)).thenReturn(5L);

        // Act
        listThirdTypeService.countThirdTypesByEntId(entId);

        // Assert
        verify(idOutputPort, times(1)).countThirdTypesByEntId(entId);
        verifyNoMoreInteractions(idOutputPort);
    }

    @Test
    @DisplayName("Debe delegar al puerto correctamente al contar con búsqueda")
    void testCountThirdTypesByEntIdAndSearchDelegaAlPuertoCorrectamente() {
        // Arrange
        when(idOutputPort.countThirdTypesByEntIdAndSearch(entId, "Cliente")).thenReturn(1L);

        // Act
        listThirdTypeService.countThirdTypesByEntIdAndSearch(entId, "Cliente");

        // Assert
        verify(idOutputPort, times(1)).countThirdTypesByEntIdAndSearch(entId, "Cliente");
        verifyNoMoreInteractions(idOutputPort);
    }

    @Test
    @DisplayName("Debe delegar al puerto correctamente al contar activos")
    void testCountActiveThirdTypesByEntIdDelegaAlPuertoCorrectamente() {
        // Arrange
        when(idOutputPort.countActiveThirdTypesByEntId(entId)).thenReturn(2L);

        // Act
        listThirdTypeService.countActiveThirdTypesByEntId(entId);

        // Assert
        verify(idOutputPort, times(1)).countActiveThirdTypesByEntId(entId);
        verifyNoMoreInteractions(idOutputPort);
    }

    @Test
    @DisplayName("Debe delegar al puerto con parámetros correctos para obtener activos")
    void testGetAllActiveThirdTypesDelegaAlPuertoConParametrosCorrectos() {
        // Arrange
        Page<ThirdType> page = new PageImpl<>(Arrays.asList(thirdType1));
        when(idOutputPort.getAllActiveThirdTypes(entId, 0, 10)).thenReturn(page);

        // Act
        listThirdTypeService.getAllActiveThirdTypes(entId, 0, 10);

        // Assert
        verify(idOutputPort, times(1)).getAllActiveThirdTypes(entId, 0, 10);
        verifyNoMoreInteractions(idOutputPort);
    }

    // ==================== Integración ====================

    @Test
    @DisplayName("Debe delegar correctamente todas las operaciones")
    void testListThirdTypeServiceTodasLasOperacionesDeleganCorrectamente() {
        // Arrange
        List<ThirdType> list = Arrays.asList(thirdType1);
        Page<ThirdType> page = new PageImpl<>(list, PageRequest.of(0, 10), 1);

        when(idOutputPort.getALLThirdTypes(entId)).thenReturn(list);
        when(idOutputPort.getAllThirdTypesWithSort(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);
        when(idOutputPort.findThirdTypesByEntIdAndSearch(anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);
        when(idOutputPort.countThirdTypesByEntId(anyString())).thenReturn(1L);
        when(idOutputPort.countThirdTypesByEntIdAndSearch(anyString(), anyString())).thenReturn(1L);
        when(idOutputPort.countActiveThirdTypesByEntId(anyString())).thenReturn(1L);
        when(idOutputPort.getAllActiveThirdTypes(anyString(), anyInt(), anyInt())).thenReturn(page);

        // Act
        listThirdTypeService.getAllThirdTypes(entId);
        listThirdTypeService.getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc");
        listThirdTypeService.findThirdTypesByEntIdAndSearch(entId, "Cliente", 0, 10, "thirdTypeName", "asc");
        listThirdTypeService.countThirdTypesByEntId(entId);
        listThirdTypeService.countThirdTypesByEntIdAndSearch(entId, "Cliente");
        listThirdTypeService.countActiveThirdTypesByEntId(entId);
        listThirdTypeService.getAllActiveThirdTypes(entId, 0, 10);

        // Assert
        verify(idOutputPort, times(1)).getALLThirdTypes(entId);
        verify(idOutputPort, times(1)).getAllThirdTypesWithSort(entId, 0, 10, "thirdTypeName", "asc");
        verify(idOutputPort, times(1)).findThirdTypesByEntIdAndSearch(entId, "Cliente", 0, 10, "thirdTypeName", "asc");
        verify(idOutputPort, times(1)).countThirdTypesByEntId(entId);
        verify(idOutputPort, times(1)).countThirdTypesByEntIdAndSearch(entId, "Cliente");
        verify(idOutputPort, times(1)).countActiveThirdTypesByEntId(entId);
        verify(idOutputPort, times(1)).getAllActiveThirdTypes(entId, 0, 10);
    }
}
