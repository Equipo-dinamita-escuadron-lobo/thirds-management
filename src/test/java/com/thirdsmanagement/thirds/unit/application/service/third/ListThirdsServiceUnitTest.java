package com.thirdsmanagement.thirds.unit.application.service.third;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.application.service.thirdType.DefaultThirdTypesService;
import com.thirdsmanagement.thirds.application.service.third.ListThirdsService;
import com.thirdsmanagement.thirds.domain.model.Third;

/**
 * Tests unitarios para ListThirdsService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ListThirdsServiceUnitTest {

    @Mock
    private ThirdOutputPort thirdOutputPort;

    @Mock
    private DefaultThirdTypesService defaultThirdTypesService;

    @InjectMocks
    private ListThirdsService listThirdsService;

    private String entId;
    private Pageable pageable;
    private Page<Third> thirdsPage;
    private List<Third> thirdsList;

    @BeforeEach
    void setUp() {
        entId = "ENT001";
        pageable = PageRequest.of(0, 10, Sort.by("names").ascending());

        thirdsList = List.of(
                Third.builder().thId(1L).entId(entId).names("Juan").idNumber(123456789L).build(),
                Third.builder().thId(2L).entId(entId).names("María").idNumber(987654321L).build(),
                Third.builder().thId(3L).entId(entId).names("Pedro").idNumber(111222333L).build()
        );

        thirdsPage = new PageImpl<>(thirdsList, pageable, thirdsList.size());
    }

    // ========== getAllThirdsBy Tests ==========

    @Test
    @DisplayName("Debe obtener todos los terceros por entidad con paginación")
    void testGetAllThirdsBy_SuccessfulRetrieval() {
        // Arrange
        when(thirdOutputPort.getAllThirdsBy(entId, pageable)).thenReturn(thirdsPage);

        // Act
        Page<Third> result = listThirdsService.getAllThirdsBy(entId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        verify(thirdOutputPort).getAllThirdsBy(entId, pageable);
    }

    @Test
    @DisplayName("Debe retornar página vacía cuando no hay terceros")
    void testGetAllThirdsBy_EmptyPage() {
        // Arrange
        Page<Third> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(thirdOutputPort.getAllThirdsBy(entId, pageable)).thenReturn(emptyPage);

        // Act
        Page<Third> result = listThirdsService.getAllThirdsBy(entId, pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("Debe invocar output port con parámetros correctos en getAllThirdsBy")
    void testGetAllThirdsBy_InvokesWithCorrectParams() {
        // Arrange
        when(thirdOutputPort.getAllThirdsBy(entId, pageable)).thenReturn(thirdsPage);

        // Act
        listThirdsService.getAllThirdsBy(entId, pageable);

        // Assert
        verify(thirdOutputPort).getAllThirdsBy(eq(entId), eq(pageable));
    }

    @Test
    @DisplayName("Debe delegar completamente al output port en getAllThirdsBy")
    void testGetAllThirdsBy_DelegatesToOutputPort() {
        // Arrange
        when(thirdOutputPort.getAllThirdsBy(entId, pageable)).thenReturn(thirdsPage);

        // Act
        listThirdsService.getAllThirdsBy(entId, pageable);

        // Assert
        verify(thirdOutputPort).getAllThirdsBy(entId, pageable);
        verifyNoMoreInteractions(thirdOutputPort);
    }

    // ========== countAllThirdsByEntId Tests ==========

    @Test
    @DisplayName("Debe contar todos los terceros por entidad")
    void testCountAllThirdsByEntId_ReturnsCount() {
        // Arrange
        long expectedCount = 10L;
        when(thirdOutputPort.countAllThirdsByEntId(entId)).thenReturn(expectedCount);

        // Act
        long result = listThirdsService.countAllThirdsByEntId(entId);

        // Assert
        assertEquals(expectedCount, result);
        verify(thirdOutputPort).countAllThirdsByEntId(entId);
    }

    @Test
    @DisplayName("Debe retornar cero cuando no hay terceros")
    void testCountAllThirdsByEntId_ReturnsZero() {
        // Arrange
        when(thirdOutputPort.countAllThirdsByEntId(entId)).thenReturn(0L);

        // Act
        long result = listThirdsService.countAllThirdsByEntId(entId);

        // Assert
        assertEquals(0L, result);
    }

    @Test
    @DisplayName("Debe retornar conteo grande correctamente")
    void testCountAllThirdsByEntId_LargeCount() {
        // Arrange
        long largeCount = 10000L;
        when(thirdOutputPort.countAllThirdsByEntId(entId)).thenReturn(largeCount);

        // Act
        long result = listThirdsService.countAllThirdsByEntId(entId);

        // Assert
        assertEquals(largeCount, result);
    }

    @Test
    @DisplayName("Debe invocar output port con entId correcto en conteo")
    void testCountAllThirdsByEntId_InvokesWithCorrectParam() {
        // Arrange
        when(thirdOutputPort.countAllThirdsByEntId(entId)).thenReturn(5L);

        // Act
        listThirdsService.countAllThirdsByEntId(entId);

        // Assert
        verify(thirdOutputPort).countAllThirdsByEntId(eq(entId));
    }

    // ========== findByEntIdAndSearch Tests ==========

    @Test
    @DisplayName("Debe buscar terceros con criterio de búsqueda")
    void testFindByEntIdAndSearch_WithSearchCriteria() {
        // Arrange
        String search = "Juan";
        int page = 0;
        int size = 10;
        String sortField = "names";
        String sortOrder = "asc";

        when(thirdOutputPort.findByEntIdAndSearch(entId, search, page, size, sortField, sortOrder))
                .thenReturn(thirdsPage);

        // Act
        Page<Third> result = listThirdsService.findByEntIdAndSearch(entId, search, page, size, sortField, sortOrder);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        verify(thirdOutputPort).findByEntIdAndSearch(entId, search, page, size, sortField, sortOrder);
    }

    @Test
    @DisplayName("Debe buscar con ordenamiento descendente")
    void testFindByEntIdAndSearch_DescendingOrder() {
        // Arrange
        String search = "test";
        int page = 0;
        int size = 20;
        String sortField = "idNumber";
        String sortOrder = "desc";

        when(thirdOutputPort.findByEntIdAndSearch(entId, search, page, size, sortField, sortOrder))
                .thenReturn(thirdsPage);

        // Act
        Page<Third> result = listThirdsService.findByEntIdAndSearch(entId, search, page, size, sortField, sortOrder);

        // Assert
        assertNotNull(result);
        verify(thirdOutputPort).findByEntIdAndSearch(entId, search, page, size, sortField, sortOrder);
    }

    @Test
    @DisplayName("Debe invocar búsqueda con todos los parámetros correctos")
    void testFindByEntIdAndSearch_InvokesWithAllParams() {
        // Arrange
        String search = "search";
        int page = 1;
        int size = 15;
        String sortField = "lastNames";
        String sortOrder = "asc";

        when(thirdOutputPort.findByEntIdAndSearch(entId, search, page, size, sortField, sortOrder))
                .thenReturn(thirdsPage);

        // Act
        listThirdsService.findByEntIdAndSearch(entId, search, page, size, sortField, sortOrder);

        // Assert
        verify(thirdOutputPort).findByEntIdAndSearch(
                eq(entId), eq(search), eq(page), eq(size), eq(sortField), eq(sortOrder)
        );
    }

    @Test
    @DisplayName("Debe manejar búsqueda con texto vacío")
    void testFindByEntIdAndSearch_EmptySearch() {
        // Arrange
        String emptySearch = "";
        when(thirdOutputPort.findByEntIdAndSearch(entId, emptySearch, 0, 10, "names", "asc"))
                .thenReturn(thirdsPage);

        // Act
        Page<Third> result = listThirdsService.findByEntIdAndSearch(entId, emptySearch, 0, 10, "names", "asc");

        // Assert
        assertNotNull(result);
    }

    // ========== countByEntIdAndSearch Tests ==========

    @Test
    @DisplayName("Debe contar terceros por criterio de búsqueda")
    void testCountByEntIdAndSearch_ReturnsCount() {
        // Arrange
        String search = "Juan";
        long expectedCount = 5L;
        when(thirdOutputPort.countByEntIdAndSearch(entId, search)).thenReturn(expectedCount);

        // Act
        long result = listThirdsService.countByEntIdAndSearch(entId, search);

        // Assert
        assertEquals(expectedCount, result);
        verify(thirdOutputPort).countByEntIdAndSearch(entId, search);
    }

    @Test
    @DisplayName("Debe retornar cero cuando búsqueda no encuentra resultados")
    void testCountByEntIdAndSearch_ReturnsZero() {
        // Arrange
        String search = "NoExiste";
        when(thirdOutputPort.countByEntIdAndSearch(entId, search)).thenReturn(0L);

        // Act
        long result = listThirdsService.countByEntIdAndSearch(entId, search);

        // Assert
        assertEquals(0L, result);
    }

    @Test
    @DisplayName("Debe invocar conteo de búsqueda con parámetros correctos")
    void testCountByEntIdAndSearch_InvokesWithCorrectParams() {
        // Arrange
        String search = "María";
        when(thirdOutputPort.countByEntIdAndSearch(entId, search)).thenReturn(3L);

        // Act
        listThirdsService.countByEntIdAndSearch(entId, search);

        // Assert
        verify(thirdOutputPort).countByEntIdAndSearch(eq(entId), eq(search));
    }

    // ========== getAllThirdsByWithSort Tests ==========

    @Test
    @DisplayName("Debe obtener todos los terceros con ordenamiento")
    void testGetAllThirdsByWithSort_WithSorting() {
        // Arrange
        int page = 0;
        int size = 10;
        String sortField = "names";
        String sortOrder = "asc";

        when(thirdOutputPort.getAllThirdsByWithSort(entId, page, size, sortField, sortOrder))
                .thenReturn(thirdsPage);

        // Act
        Page<Third> result = listThirdsService.getAllThirdsByWithSort(entId, page, size, sortField, sortOrder);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        verify(thirdOutputPort).getAllThirdsByWithSort(entId, page, size, sortField, sortOrder);
    }

    @Test
    @DisplayName("Debe obtener terceros con ordenamiento descendente")
    void testGetAllThirdsByWithSort_DescendingOrder() {
        // Arrange
        int page = 1;
        int size = 20;
        String sortField = "idNumber";
        String sortOrder = "desc";

        when(thirdOutputPort.getAllThirdsByWithSort(entId, page, size, sortField, sortOrder))
                .thenReturn(thirdsPage);

        // Act
        Page<Third> result = listThirdsService.getAllThirdsByWithSort(entId, page, size, sortField, sortOrder);

        // Assert
        assertNotNull(result);
        verify(thirdOutputPort).getAllThirdsByWithSort(entId, page, size, sortField, sortOrder);
    }

    @Test
    @DisplayName("Debe invocar getAllThirdsByWithSort con todos los parámetros")
    void testGetAllThirdsByWithSort_InvokesWithAllParams() {
        // Arrange
        int page = 2;
        int size = 15;
        String sortField = "email";
        String sortOrder = "asc";

        when(thirdOutputPort.getAllThirdsByWithSort(entId, page, size, sortField, sortOrder))
                .thenReturn(thirdsPage);

        // Act
        listThirdsService.getAllThirdsByWithSort(entId, page, size, sortField, sortOrder);

        // Assert
        verify(thirdOutputPort).getAllThirdsByWithSort(
                eq(entId), eq(page), eq(size), eq(sortField), eq(sortOrder)
        );
    }

    // ========== getAllActiveThirdsByWithSort Tests ==========

    @Test
    @DisplayName("Debe obtener solo terceros activos con ordenamiento")
    void testGetAllActiveThirdsByWithSort_OnlyActiveThirds() {
        // Arrange
        int page = 0;
        int size = 10;
        String sortField = "names";
        String sortOrder = "asc";

        when(thirdOutputPort.getAllActiveThirdsByWithSort(entId, page, size, sortField, sortOrder))
                .thenReturn(thirdsPage);

        // Act
        Page<Third> result = listThirdsService.getAllActiveThirdsByWithSort(entId, page, size, sortField, sortOrder);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        verify(thirdOutputPort).getAllActiveThirdsByWithSort(entId, page, size, sortField, sortOrder);
    }

    @Test
    @DisplayName("Debe obtener terceros activos con diferentes criterios de ordenamiento")
    void testGetAllActiveThirdsByWithSort_DifferentSortCriteria() {
        // Arrange
        int page = 0;
        int size = 25;
        String sortField = "lastNames";
        String sortOrder = "desc";

        when(thirdOutputPort.getAllActiveThirdsByWithSort(entId, page, size, sortField, sortOrder))
                .thenReturn(thirdsPage);

        // Act
        Page<Third> result = listThirdsService.getAllActiveThirdsByWithSort(entId, page, size, sortField, sortOrder);

        // Assert
        assertNotNull(result);
        verify(thirdOutputPort).getAllActiveThirdsByWithSort(entId, page, size, sortField, sortOrder);
    }

    @Test
    @DisplayName("Debe invocar getAllActiveThirdsByWithSort con parámetros correctos")
    void testGetAllActiveThirdsByWithSort_InvokesWithCorrectParams() {
        // Arrange
        int page = 1;
        int size = 50;
        String sortField = "idNumber";
        String sortOrder = "asc";

        when(thirdOutputPort.getAllActiveThirdsByWithSort(entId, page, size, sortField, sortOrder))
                .thenReturn(thirdsPage);

        // Act
        listThirdsService.getAllActiveThirdsByWithSort(entId, page, size, sortField, sortOrder);

        // Assert
        verify(thirdOutputPort).getAllActiveThirdsByWithSort(
                eq(entId), eq(page), eq(size), eq(sortField), eq(sortOrder)
        );
    }

    // ========== countActiveThirdsByEntId Tests ==========

    @Test
    @DisplayName("Debe contar solo terceros activos por entidad")
    void testCountActiveThirdsByEntId_ReturnsActiveCount() {
        // Arrange
        long expectedCount = 8L;
        when(thirdOutputPort.countActiveThirdsByEntId(entId)).thenReturn(expectedCount);

        // Act
        long result = listThirdsService.countActiveThirdsByEntId(entId);

        // Assert
        assertEquals(expectedCount, result);
        verify(thirdOutputPort).countActiveThirdsByEntId(entId);
    }

    @Test
    @DisplayName("Debe retornar cero cuando no hay terceros activos")
    void testCountActiveThirdsByEntId_ReturnsZero() {
        // Arrange
        when(thirdOutputPort.countActiveThirdsByEntId(entId)).thenReturn(0L);

        // Act
        long result = listThirdsService.countActiveThirdsByEntId(entId);

        // Assert
        assertEquals(0L, result);
    }

    @Test
    @DisplayName("Debe invocar conteo de activos con entId correcto")
    void testCountActiveThirdsByEntId_InvokesWithCorrectParam() {
        // Arrange
        when(thirdOutputPort.countActiveThirdsByEntId(entId)).thenReturn(15L);

        // Act
        listThirdsService.countActiveThirdsByEntId(entId);

        // Assert
        verify(thirdOutputPort).countActiveThirdsByEntId(eq(entId));
    }

    // ========== getThirdsByEntIdAndThirdTypeName Tests ==========

    @Test
    @DisplayName("Debe obtener terceros por tipo de tercero")
    void testGetThirdsByEntIdAndThirdTypeName_WithThirdType() {
        // Arrange
        String thirdTypeName = "Cliente";
        int page = 0;
        int size = 10;

        when(thirdOutputPort.getThirdsByEntIdAndThirdTypeName(eq(entId), eq(thirdTypeName), any(PageRequest.class)))
                .thenReturn(thirdsPage);

        // Act
        Page<Third> result = listThirdsService.getThirdsByEntIdAndThirdTypeName(entId, thirdTypeName, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        verify(thirdOutputPort).getThirdsByEntIdAndThirdTypeName(eq(entId), eq(thirdTypeName), any(PageRequest.class));
    }

    @Test
    @DisplayName("Debe crear PageRequest con ordenamiento ascendente por nombres")
    void testGetThirdsByEntIdAndThirdTypeName_CreatesPageRequestWithSort() {
        // Arrange
        String thirdTypeName = "Proveedor";
        int page = 1;
        int size = 20;

        when(thirdOutputPort.getThirdsByEntIdAndThirdTypeName(eq(entId), eq(thirdTypeName), any(PageRequest.class)))
                .thenReturn(thirdsPage);

        // Act
        listThirdsService.getThirdsByEntIdAndThirdTypeName(entId, thirdTypeName, page, size);

        // Assert
        verify(thirdOutputPort).getThirdsByEntIdAndThirdTypeName(
                eq(entId),
                eq(thirdTypeName),
                argThat(pageRequest ->
                        pageRequest.getPageNumber() == page &&
                        pageRequest.getPageSize() == size &&
                        pageRequest.getSort().isSorted() &&
                        pageRequest.getSort().getOrderFor("names") != null &&
                        pageRequest.getSort().getOrderFor("names").isAscending()
                )
        );
    }

    @Test
    @DisplayName("Debe obtener terceros de diferentes tipos")
    void testGetThirdsByEntIdAndThirdTypeName_DifferentThirdTypes() {
        // Arrange
        String thirdType1 = "Cliente";
        String thirdType2 = "Proveedor";

        when(thirdOutputPort.getThirdsByEntIdAndThirdTypeName(eq(entId), eq(thirdType1), any(PageRequest.class)))
                .thenReturn(thirdsPage);
        when(thirdOutputPort.getThirdsByEntIdAndThirdTypeName(eq(entId), eq(thirdType2), any(PageRequest.class)))
                .thenReturn(thirdsPage);

        // Act
        Page<Third> result1 = listThirdsService.getThirdsByEntIdAndThirdTypeName(entId, thirdType1, 0, 10);
        Page<Third> result2 = listThirdsService.getThirdsByEntIdAndThirdTypeName(entId, thirdType2, 0, 10);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        verify(thirdOutputPort).getThirdsByEntIdAndThirdTypeName(eq(entId), eq(thirdType1), any(PageRequest.class));
        verify(thirdOutputPort).getThirdsByEntIdAndThirdTypeName(eq(entId), eq(thirdType2), any(PageRequest.class));
    }

    @Test
    @DisplayName("Debe manejar diferentes páginas para tipo de tercero")
    void testGetThirdsByEntIdAndThirdTypeName_DifferentPages() {
        // Arrange
        String thirdTypeName = "Cliente";

        when(thirdOutputPort.getThirdsByEntIdAndThirdTypeName(eq(entId), eq(thirdTypeName), any(PageRequest.class)))
                .thenReturn(thirdsPage);

        // Act
        listThirdsService.getThirdsByEntIdAndThirdTypeName(entId, thirdTypeName, 0, 10);
        listThirdsService.getThirdsByEntIdAndThirdTypeName(entId, thirdTypeName, 1, 10);

        // Assert
        verify(thirdOutputPort, times(2)).getThirdsByEntIdAndThirdTypeName(
                eq(entId), eq(thirdTypeName), any(PageRequest.class)
        );
    }

    // ========== countThirdsByEntIdAndThirdTypeName Tests ==========

    @Test
    @DisplayName("Debe contar terceros por tipo de tercero")
    void testCountThirdsByEntIdAndThirdTypeName_ReturnsCount() {
        // Arrange
        String thirdTypeName = "Cliente";
        long expectedCount = 12L;
        when(thirdOutputPort.countThirdsByEntIdAndThirdTypeName(entId, thirdTypeName)).thenReturn(expectedCount);

        // Act
        long result = listThirdsService.countThirdsByEntIdAndThirdTypeName(entId, thirdTypeName);

        // Assert
        assertEquals(expectedCount, result);
        verify(thirdOutputPort).countThirdsByEntIdAndThirdTypeName(entId, thirdTypeName);
    }

    @Test
    @DisplayName("Debe retornar cero cuando no hay terceros del tipo especificado")
    void testCountThirdsByEntIdAndThirdTypeName_ReturnsZero() {
        // Arrange
        String thirdTypeName = "TipoInexistente";
        when(thirdOutputPort.countThirdsByEntIdAndThirdTypeName(entId, thirdTypeName)).thenReturn(0L);

        // Act
        long result = listThirdsService.countThirdsByEntIdAndThirdTypeName(entId, thirdTypeName);

        // Assert
        assertEquals(0L, result);
    }

    @Test
    @DisplayName("Debe contar diferentes tipos de terceros")
    void testCountThirdsByEntIdAndThirdTypeName_DifferentTypes() {
        // Arrange
        String type1 = "Cliente";
        String type2 = "Proveedor";

        when(thirdOutputPort.countThirdsByEntIdAndThirdTypeName(entId, type1)).thenReturn(10L);
        when(thirdOutputPort.countThirdsByEntIdAndThirdTypeName(entId, type2)).thenReturn(5L);

        // Act
        long count1 = listThirdsService.countThirdsByEntIdAndThirdTypeName(entId, type1);
        long count2 = listThirdsService.countThirdsByEntIdAndThirdTypeName(entId, type2);

        // Assert
        assertEquals(10L, count1);
        assertEquals(5L, count2);
    }

    @Test
    @DisplayName("Debe invocar conteo por tipo con parámetros correctos")
    void testCountThirdsByEntIdAndThirdTypeName_InvokesWithCorrectParams() {
        // Arrange
        String thirdTypeName = "Empleado";
        when(thirdOutputPort.countThirdsByEntIdAndThirdTypeName(entId, thirdTypeName)).thenReturn(7L);

        // Act
        listThirdsService.countThirdsByEntIdAndThirdTypeName(entId, thirdTypeName);

        // Assert
        verify(thirdOutputPort).countThirdsByEntIdAndThirdTypeName(eq(entId), eq(thirdTypeName));
    }

    // ========== Integration Scenario Tests ==========

    @Test
    @DisplayName("Debe propagar excepción del output port")
    void testPropagatesException_FromOutputPort() {
        // Arrange
        when(thirdOutputPort.getAllThirdsBy(entId, pageable))
                .thenThrow(new RuntimeException("Error en base de datos"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            listThirdsService.getAllThirdsBy(entId, pageable);
        });
    }

    @Test
    @DisplayName("Debe manejar múltiples consultas para diferentes entidades")
    void testMultipleQueries_DifferentEntities() {
        // Arrange
        String entId1 = "ENT001";
        String entId2 = "ENT002";

        when(thirdOutputPort.countAllThirdsByEntId(entId1)).thenReturn(10L);
        when(thirdOutputPort.countAllThirdsByEntId(entId2)).thenReturn(20L);

        // Act
        long count1 = listThirdsService.countAllThirdsByEntId(entId1);
        long count2 = listThirdsService.countAllThirdsByEntId(entId2);

        // Assert
        assertEquals(10L, count1);
        assertEquals(20L, count2);
        verify(thirdOutputPort).countAllThirdsByEntId(entId1);
        verify(thirdOutputPort).countAllThirdsByEntId(entId2);
    }

    @Test
    @DisplayName("Debe delegar todas las operaciones al output port sin lógica adicional")
    void testDelegatesAllOperations_ToOutputPort() {
        // Arrange
        when(thirdOutputPort.getAllThirdsBy(entId, pageable)).thenReturn(thirdsPage);
        when(thirdOutputPort.countAllThirdsByEntId(entId)).thenReturn(10L);
        when(thirdOutputPort.countActiveThirdsByEntId(entId)).thenReturn(8L);

        // Act
        listThirdsService.getAllThirdsBy(entId, pageable);
        listThirdsService.countAllThirdsByEntId(entId);
        listThirdsService.countActiveThirdsByEntId(entId);

        // Assert
        verify(thirdOutputPort).getAllThirdsBy(entId, pageable);
        verify(thirdOutputPort).countAllThirdsByEntId(entId);
        verify(thirdOutputPort).countActiveThirdsByEntId(entId);
        verifyNoMoreInteractions(thirdOutputPort);
    }

    @Test
    @DisplayName("Debe manejar paginación completa correctamente")
    void testHandlesPagination_Correctly() {
        // Arrange
        PageRequest page0 = PageRequest.of(0, 10);
        PageRequest page1 = PageRequest.of(1, 10);

        when(thirdOutputPort.getAllThirdsBy(entId, page0)).thenReturn(thirdsPage);
        when(thirdOutputPort.getAllThirdsBy(entId, page1)).thenReturn(thirdsPage);

        // Act
        Page<Third> result0 = listThirdsService.getAllThirdsBy(entId, page0);
        Page<Third> result1 = listThirdsService.getAllThirdsBy(entId, page1);

        // Assert
        assertNotNull(result0);
        assertNotNull(result1);
        verify(thirdOutputPort).getAllThirdsBy(entId, page0);
        verify(thirdOutputPort).getAllThirdsBy(entId, page1);
    }

    @Test
    @DisplayName("Debe manejar diferentes tamaños de página")
    void testHandlesDifferentPageSizes() {
        // Arrange
        when(thirdOutputPort.getAllThirdsByWithSort(entId, 0, 10, "names", "asc")).thenReturn(thirdsPage);
        when(thirdOutputPort.getAllThirdsByWithSort(entId, 0, 50, "names", "asc")).thenReturn(thirdsPage);
        when(thirdOutputPort.getAllThirdsByWithSort(entId, 0, 100, "names", "asc")).thenReturn(thirdsPage);

        // Act
        listThirdsService.getAllThirdsByWithSort(entId, 0, 10, "names", "asc");
        listThirdsService.getAllThirdsByWithSort(entId, 0, 50, "names", "asc");
        listThirdsService.getAllThirdsByWithSort(entId, 0, 100, "names", "asc");

        // Assert
        verify(thirdOutputPort).getAllThirdsByWithSort(entId, 0, 10, "names", "asc");
        verify(thirdOutputPort).getAllThirdsByWithSort(entId, 0, 50, "names", "asc");
        verify(thirdOutputPort).getAllThirdsByWithSort(entId, 0, 100, "names", "asc");
    }
}
