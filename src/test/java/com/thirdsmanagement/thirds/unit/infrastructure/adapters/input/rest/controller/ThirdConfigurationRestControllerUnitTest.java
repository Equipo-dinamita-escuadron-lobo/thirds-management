package com.thirdsmanagement.thirds.unit.infrastructure.adapters.input.rest.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.input.CreateTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.DeleteThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.input.DeleteTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ListThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ListTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.UpdateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.input.UpdateTypeIdUseCase;
import com.thirdsmanagement.thirds.domain.model.PersonClassification;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.controller.ThirdConfigurationRestController;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdTypeCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdTypeUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.TypeIdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.TypeIdUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ThirdTypeResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.IdRestMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ThirdConfigurationRestControllerUnitTest {

    @Mock
    private CreateThirdTypeUseCase createThirdTypeUseCase;

    @Mock
    private ListThirdTypeUseCase listThirdTypeUseCase;

    @Mock
    private UpdateThirdTypeUseCase updateThirdTypeUseCase;

    @Mock
    private DeleteThirdTypeUseCase deleteThirdTypeUseCase;

    @Mock
    private CreateTypeIdUseCase createTypeIdUseCase;

    @Mock
    private ListTypeIdUseCase listTypeIdUseCase;

    @Mock
    private UpdateTypeIdUseCase updateTypeIdUseCase;

    @Mock
    private DeleteTypeIdUseCase deleteTypeIdUseCase;

    @Mock
    private IdRestMapper idRestMapper;

    @InjectMocks
    private ThirdConfigurationRestController controller;

    private String entId;
    private ThirdType thirdType;
    private ThirdTypeCreateRequest thirdTypeCreateRequest;
    private ThirdTypeUpdateRequest thirdTypeUpdateRequest;
    private ThirdTypeResponse thirdTypeResponse;
    private TypeId typeId;
    private TypeIdCreateRequest typeIdCreateRequest;
    private TypeIdUpdateRequest typeIdUpdateRequest;

    @BeforeEach
    void setUp() {
        entId = "ENT001";

        thirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .entId(entId)
                .thirdTypeName("Cliente")
                .status(true)
                .build();

        thirdTypeCreateRequest = ThirdTypeCreateRequest.builder()
                .entId(entId)
                .thirdTypeName("Cliente")
                .status(true)
                .build();

        thirdTypeUpdateRequest = ThirdTypeUpdateRequest.builder()
                .thirdTypeId(1L)
                .entId(entId)
                .thirdTypeName("Cliente Actualizado")
                .status(true)
                .build();

        thirdTypeResponse = ThirdTypeResponse.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .status(true)
                .build();

        typeId = TypeId.builder()
                .id(1L)
                .entId(entId)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        typeIdCreateRequest = TypeIdCreateRequest.builder()
                .entId(entId)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        typeIdUpdateRequest = TypeIdUpdateRequest.builder()
                .id(1L)
                .entId(entId)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía Actualizada")
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();
    }

    // ==================== createThirdType ====================

    @Test
    @DisplayName("Debe crear tipo de tercero correctamente y retornar CREATED")
    void testCreateThirdTypeCreatesCorrectlyAndReturnsCreated() {
        // Arrange
        when(idRestMapper.toThirdType(thirdTypeCreateRequest)).thenReturn(thirdType);
        when(createThirdTypeUseCase.createThirdType(thirdType)).thenReturn(thirdType);
        when(idRestMapper.toThirdTypeResponse(thirdType)).thenReturn(thirdTypeResponse);

        // Act
        ResponseEntity<ThirdTypeResponse> response = controller.createThirdType(thirdTypeCreateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cliente", response.getBody().getThirdTypeName());
        verify(idRestMapper).toThirdType(thirdTypeCreateRequest);
        verify(createThirdTypeUseCase).createThirdType(thirdType);
        verify(idRestMapper).toThirdTypeResponse(thirdType);
    }

    @Test
    @DisplayName("Debe delegar correctamente la creación de tipo de tercero")
    void testCreateThirdTypeDelegatesCorrectly() {
        // Arrange
        when(idRestMapper.toThirdType(thirdTypeCreateRequest)).thenReturn(thirdType);
        when(createThirdTypeUseCase.createThirdType(thirdType)).thenReturn(thirdType);
        when(idRestMapper.toThirdTypeResponse(thirdType)).thenReturn(thirdTypeResponse);

        // Act
        controller.createThirdType(thirdTypeCreateRequest);

        // Assert
        verify(idRestMapper, times(1)).toThirdType(thirdTypeCreateRequest);
        verify(createThirdTypeUseCase, times(1)).createThirdType(thirdType);
        verify(idRestMapper, times(1)).toThirdTypeResponse(thirdType);
    }

    @Test
    @DisplayName("Debe propagar excepción cuando falla la creación de tipo de tercero")
    void testCreateThirdTypePropagatesException() {
        // Arrange
        when(idRestMapper.toThirdType(thirdTypeCreateRequest)).thenReturn(thirdType);
        when(createThirdTypeUseCase.createThirdType(thirdType))
                .thenThrow(new RuntimeException("Error al crear"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.createThirdType(thirdTypeCreateRequest));
        verify(createThirdTypeUseCase).createThirdType(thirdType);
    }

    // ==================== getThirdType ====================

    @Test
    @DisplayName("Debe retornar página de tipos de tercero sin filtro")
    void testGetThirdTypeWithoutFilterReturnsPage() {
        // Arrange
        List<ThirdType> thirdTypes = Arrays.asList(thirdType);
        Page<ThirdType> page = new PageImpl<>(thirdTypes, PageRequest.of(0, 10), 1);

        when(listThirdTypeUseCase.countThirdTypesByEntId(entId)).thenReturn(1L);
        when(listThirdTypeUseCase.getAllThirdTypesWithSort(entId, 0, 10, "ttName", "asc"))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<ThirdType>> response = controller.getThirdType(
                entId, Optional.of(0), Optional.of(10), "asc", null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(listThirdTypeUseCase).countThirdTypesByEntId(entId);
        verify(listThirdTypeUseCase).getAllThirdTypesWithSort(entId, 0, 10, "ttName", "asc");
    }

    @Test
    @DisplayName("Debe retornar página de tipos de tercero con filtro de búsqueda")
    void testGetThirdTypeWithSearchFilterReturnsPage() {
        // Arrange
        String search = "Cliente";
        List<ThirdType> thirdTypes = Arrays.asList(thirdType);
        Page<ThirdType> page = new PageImpl<>(thirdTypes, PageRequest.of(0, 10), 1);

        when(listThirdTypeUseCase.countThirdTypesByEntIdAndSearch(entId, search)).thenReturn(1L);
        when(listThirdTypeUseCase.findThirdTypesByEntIdAndSearch(entId, search, 0, 10, "ttName", "asc"))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<ThirdType>> response = controller.getThirdType(
                entId, Optional.of(0), Optional.of(10), "asc", search);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(listThirdTypeUseCase).countThirdTypesByEntIdAndSearch(entId, search);
        verify(listThirdTypeUseCase).findThirdTypesByEntIdAndSearch(entId, search, 0, 10, "ttName", "asc");
    }

    @Test
    @DisplayName("Debe ignorar filtro de búsqueda vacío")
    void testGetThirdTypeIgnoresEmptySearchFilter() {
        // Arrange
        String search = "   ";
        List<ThirdType> thirdTypes = Arrays.asList(thirdType);
        Page<ThirdType> page = new PageImpl<>(thirdTypes, PageRequest.of(0, 10), 1);

        when(listThirdTypeUseCase.countThirdTypesByEntId(entId)).thenReturn(1L);
        when(listThirdTypeUseCase.getAllThirdTypesWithSort(entId, 0, 10, "ttName", "asc"))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<ThirdType>> response = controller.getThirdType(
                entId, Optional.of(0), Optional.of(10), "asc", search);

        // Assert
        assertNotNull(response);
        verify(listThirdTypeUseCase).countThirdTypesByEntId(entId);
        verify(listThirdTypeUseCase).getAllThirdTypesWithSort(entId, 0, 10, "ttName", "asc");
        verify(listThirdTypeUseCase, never()).countThirdTypesByEntIdAndSearch(anyString(), anyString());
    }

    // ==================== updateThirdType ====================

    @Test
    @DisplayName("Debe actualizar tipo de tercero correctamente y retornar OK")
    void testUpdateThirdTypeUpdatesCorrectlyAndReturnsOk() {
        // Arrange
        ThirdType updatedThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .entId(entId)
                .thirdTypeName("Cliente Actualizado")
                .status(true)
                .build();

        ThirdTypeResponse updatedResponse = ThirdTypeResponse.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente Actualizado")
                .status(true)
                .build();

        when(idRestMapper.toThirdType(thirdTypeUpdateRequest)).thenReturn(updatedThirdType);
        when(updateThirdTypeUseCase.updateThirdType(updatedThirdType)).thenReturn(updatedThirdType);
        when(idRestMapper.toThirdTypeResponse(updatedThirdType)).thenReturn(updatedResponse);

        // Act
        ResponseEntity<ThirdTypeResponse> response = controller.updateThirdType(thirdTypeUpdateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cliente Actualizado", response.getBody().getThirdTypeName());
        verify(idRestMapper).toThirdType(thirdTypeUpdateRequest);
        verify(updateThirdTypeUseCase).updateThirdType(updatedThirdType);
        verify(idRestMapper).toThirdTypeResponse(updatedThirdType);
    }

    @Test
    @DisplayName("Debe propagar excepción cuando falla actualización de tipo de tercero")
    void testUpdateThirdTypePropagatesException() {
        // Arrange
        ThirdType updatedThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .entId(entId)
                .thirdTypeName("Cliente Actualizado")
                .status(true)
                .build();

        when(idRestMapper.toThirdType(thirdTypeUpdateRequest)).thenReturn(updatedThirdType);
        when(updateThirdTypeUseCase.updateThirdType(updatedThirdType))
                .thenThrow(new RuntimeException("Error al actualizar"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.updateThirdType(thirdTypeUpdateRequest));
        verify(updateThirdTypeUseCase).updateThirdType(updatedThirdType);
    }

    // ==================== deleteThirdType ====================

    @Test
    @DisplayName("Debe eliminar tipo de tercero correctamente y retornar true")
    void testDeleteThirdTypeDeletesCorrectlyAndReturnsTrue() {
        // Arrange
        Long thirdTypeId = 1L;
        when(deleteThirdTypeUseCase.deleteThirdType(thirdTypeId, entId)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = controller.deleteThirdType(thirdTypeId, entId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(deleteThirdTypeUseCase).deleteThirdType(thirdTypeId, entId);
    }

    @Test
    @DisplayName("Debe retornar false cuando no se puede eliminar tipo de tercero")
    void testDeleteThirdTypeReturnsFalseWhenCannotDelete() {
        // Arrange
        Long thirdTypeId = 1L;
        when(deleteThirdTypeUseCase.deleteThirdType(thirdTypeId, entId)).thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = controller.deleteThirdType(thirdTypeId, entId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
        verify(deleteThirdTypeUseCase).deleteThirdType(thirdTypeId, entId);
    }

    @Test
    @DisplayName("Debe propagar excepción cuando falla eliminación de tipo de tercero")
    void testDeleteThirdTypePropagatesException() {
        // Arrange
        Long thirdTypeId = 1L;
        when(deleteThirdTypeUseCase.deleteThirdType(thirdTypeId, entId))
                .thenThrow(new RuntimeException("Error al eliminar"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.deleteThirdType(thirdTypeId, entId));
        verify(deleteThirdTypeUseCase).deleteThirdType(thirdTypeId, entId);
    }

    // ==================== getActiveThirdType ====================

    @Test
    @DisplayName("Debe retornar página de tipos de tercero activos")
    void testGetActiveThirdTypeReturnsActivePage() {
        // Arrange
        List<ThirdType> activeThirdTypes = Arrays.asList(thirdType);
        Page<ThirdType> page = new PageImpl<>(activeThirdTypes, PageRequest.of(0, 10), 1);

        when(listThirdTypeUseCase.countActiveThirdTypesByEntId(entId)).thenReturn(1L);
        when(listThirdTypeUseCase.getAllActiveThirdTypes(entId, 0, 10)).thenReturn(page);

        // Act
        ResponseEntity<Page<ThirdType>> response = controller.getActiveThirdType(
                entId, Optional.of(0), Optional.of(10));

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(listThirdTypeUseCase).countActiveThirdTypesByEntId(entId);
        verify(listThirdTypeUseCase).getAllActiveThirdTypes(entId, 0, 10);
    }

    // ==================== createTypeId ====================

    @Test
    @DisplayName("Debe crear tipo de identificación correctamente y retornar CREATED")
    void testCreateTypeIdCreatesCorrectlyAndReturnsCreated() {
        // Arrange
        when(idRestMapper.toTypeId(typeIdCreateRequest)).thenReturn(typeId);
        when(createTypeIdUseCase.createTypeId(typeId)).thenReturn(typeId);

        // Act
        ResponseEntity<TypeId> response = controller.createTypeId(typeIdCreateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CC", response.getBody().getTypeId());
        verify(idRestMapper).toTypeId(typeIdCreateRequest);
        verify(createTypeIdUseCase).createTypeId(typeId);
    }

    @Test
    @DisplayName("Debe delegar correctamente la creación de tipo de identificación")
    void testCreateTypeIdDelegatesCorrectly() {
        // Arrange
        when(idRestMapper.toTypeId(typeIdCreateRequest)).thenReturn(typeId);
        when(createTypeIdUseCase.createTypeId(typeId)).thenReturn(typeId);

        // Act
        controller.createTypeId(typeIdCreateRequest);

        // Assert
        verify(idRestMapper, times(1)).toTypeId(typeIdCreateRequest);
        verify(createTypeIdUseCase, times(1)).createTypeId(typeId);
    }

    @Test
    @DisplayName("Debe propagar excepción cuando falla la creación de tipo de identificación")
    void testCreateTypeIdPropagatesException() {
        // Arrange
        when(idRestMapper.toTypeId(typeIdCreateRequest)).thenReturn(typeId);
        when(createTypeIdUseCase.createTypeId(typeId))
                .thenThrow(new RuntimeException("Error al crear"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.createTypeId(typeIdCreateRequest));
        verify(createTypeIdUseCase).createTypeId(typeId);
    }

    // ==================== ListTypeId ====================

    @Test
    @DisplayName("Debe retornar página de tipos de identificación sin filtro")
    void testListTypeIdWithoutFilterReturnsPage() {
        // Arrange
        List<TypeId> typeIds = Arrays.asList(typeId);
        Page<TypeId> page = new PageImpl<>(typeIds, PageRequest.of(0, 10), 1);

        when(listTypeIdUseCase.countByEntId(entId)).thenReturn(1L);
        when(listTypeIdUseCase.getAllTypeIdsWithSort(entId, 0, 10, "tiName", "asc"))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<TypeId>> response = controller.ListTypeId(
                entId, Optional.of(0), Optional.of(10), "tiName", "asc", null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(listTypeIdUseCase).countByEntId(entId);
        verify(listTypeIdUseCase).getAllTypeIdsWithSort(entId, 0, 10, "tiName", "asc");
    }

    @Test
    @DisplayName("Debe retornar página de tipos de identificación con filtro de búsqueda")
    void testListTypeIdWithSearchFilterReturnsPage() {
        // Arrange
        String search = "Cedula";
        List<TypeId> typeIds = Arrays.asList(typeId);
        Page<TypeId> page = new PageImpl<>(typeIds, PageRequest.of(0, 10), 1);

        when(listTypeIdUseCase.countByEntIdAndSearch(entId, search)).thenReturn(1L);
        when(listTypeIdUseCase.findByEntIdAndSearch(entId, search, 0, 10, "tiName", "asc"))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<TypeId>> response = controller.ListTypeId(
                entId, Optional.of(0), Optional.of(10), "tiName", "asc", search);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(listTypeIdUseCase).countByEntIdAndSearch(entId, search);
        verify(listTypeIdUseCase).findByEntIdAndSearch(entId, search, 0, 10, "tiName", "asc");
    }

    @Test
    @DisplayName("Debe ignorar filtro de búsqueda vacío en tipos de identificación")
    void testListTypeIdIgnoresEmptySearchFilter() {
        // Arrange
        String search = "   ";
        List<TypeId> typeIds = Arrays.asList(typeId);
        Page<TypeId> page = new PageImpl<>(typeIds, PageRequest.of(0, 10), 1);

        when(listTypeIdUseCase.countByEntId(entId)).thenReturn(1L);
        when(listTypeIdUseCase.getAllTypeIdsWithSort(entId, 0, 10, "tiName", "asc"))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<TypeId>> response = controller.ListTypeId(
                entId, Optional.of(0), Optional.of(10), "tiName", "asc", search);

        // Assert
        assertNotNull(response);
        verify(listTypeIdUseCase).countByEntId(entId);
        verify(listTypeIdUseCase).getAllTypeIdsWithSort(entId, 0, 10, "tiName", "asc");
        verify(listTypeIdUseCase, never()).countByEntIdAndSearch(anyString(), anyString());
    }

    // ==================== updateTypeId ====================

    @Test
    @DisplayName("Debe actualizar tipo de identificación correctamente y retornar OK")
    void testUpdateTypeIdUpdatesCorrectlyAndReturnsOk() {
        // Arrange
        TypeId updatedTypeId = TypeId.builder()
                .id(1L)
                .entId(entId)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía Actualizada")
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        when(idRestMapper.toTypeId(typeIdUpdateRequest)).thenReturn(updatedTypeId);
        when(updateTypeIdUseCase.updateTypeId(updatedTypeId)).thenReturn(updatedTypeId);

        // Act
        ResponseEntity<TypeId> response = controller.updateTypeId(typeIdUpdateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Cédula de Ciudadanía Actualizada", response.getBody().getTypeIdname());
        verify(idRestMapper).toTypeId(typeIdUpdateRequest);
        verify(updateTypeIdUseCase).updateTypeId(updatedTypeId);
    }

    @Test
    @DisplayName("Debe propagar excepción cuando falla actualización de tipo de identificación")
    void testUpdateTypeIdPropagatesException() {
        // Arrange
        TypeId updatedTypeId = TypeId.builder()
                .id(1L)
                .entId(entId)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía Actualizada")
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        when(idRestMapper.toTypeId(typeIdUpdateRequest)).thenReturn(updatedTypeId);
        when(updateTypeIdUseCase.updateTypeId(updatedTypeId))
                .thenThrow(new RuntimeException("Error al actualizar"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.updateTypeId(typeIdUpdateRequest));
        verify(updateTypeIdUseCase).updateTypeId(updatedTypeId);
    }

    // ==================== deleteTypeId ====================

    @Test
    @DisplayName("Debe eliminar tipo de identificación correctamente y retornar true")
    void testDeleteTypeIdDeletesCorrectlyAndReturnsTrue() {
        // Arrange
        Long typeIdId = 1L;
        when(deleteTypeIdUseCase.deleteTypeId(typeIdId, entId)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = controller.deleteTypeId(typeIdId, entId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(deleteTypeIdUseCase).deleteTypeId(typeIdId, entId);
    }

    @Test
    @DisplayName("Debe retornar false cuando no se puede eliminar tipo de identificación")
    void testDeleteTypeIdReturnsFalseWhenCannotDelete() {
        // Arrange
        Long typeIdId = 1L;
        when(deleteTypeIdUseCase.deleteTypeId(typeIdId, entId)).thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = controller.deleteTypeId(typeIdId, entId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
        verify(deleteTypeIdUseCase).deleteTypeId(typeIdId, entId);
    }

    @Test
    @DisplayName("Debe propagar excepción cuando falla eliminación de tipo de identificación")
    void testDeleteTypeIdPropagatesException() {
        // Arrange
        Long typeIdId = 1L;
        when(deleteTypeIdUseCase.deleteTypeId(typeIdId, entId))
                .thenThrow(new RuntimeException("Error al eliminar"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.deleteTypeId(typeIdId, entId));
        verify(deleteTypeIdUseCase).deleteTypeId(typeIdId, entId);
    }

    // ==================== getActiveTypeId ====================

    @Test
    @DisplayName("Debe retornar página de tipos de identificación activos")
    void testGetActiveTypeIdReturnsActivePage() {
        // Arrange
        List<TypeId> activeTypeIds = Arrays.asList(typeId);
        Page<TypeId> page = new PageImpl<>(activeTypeIds, PageRequest.of(0, 10), 1);

        when(listTypeIdUseCase.countActiveByEntId(entId)).thenReturn(1L);
        when(listTypeIdUseCase.getAllActiveTypeIds(entId, 0, 10)).thenReturn(page);

        // Act
        ResponseEntity<Page<TypeId>> response = controller.getActiveTypeId(
                entId, Optional.of(0), Optional.of(10));

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(listTypeIdUseCase).countActiveByEntId(entId);
        verify(listTypeIdUseCase).getAllActiveTypeIds(entId, 0, 10);
    }

    // ==================== Integración y casos complejos ====================

    @Test
    @DisplayName("Debe mantener independencia entre operaciones de tipo de tercero y tipo de identificación")
    void testIndependenceBetweenOperations() {
        // Arrange
        when(idRestMapper.toThirdType(thirdTypeCreateRequest)).thenReturn(thirdType);
        when(createThirdTypeUseCase.createThirdType(thirdType)).thenReturn(thirdType);
        when(idRestMapper.toThirdTypeResponse(thirdType)).thenReturn(thirdTypeResponse);

        when(idRestMapper.toTypeId(typeIdCreateRequest)).thenReturn(typeId);
        when(createTypeIdUseCase.createTypeId(typeId)).thenReturn(typeId);

        // Act
        controller.createThirdType(thirdTypeCreateRequest);
        controller.createTypeId(typeIdCreateRequest);

        // Assert
        verify(createThirdTypeUseCase).createThirdType(thirdType);
        verify(createTypeIdUseCase).createTypeId(typeId);
    }

    @Test
    @DisplayName("Debe manejar correctamente parámetros opcionales vacíos en paginación")
    void testHandlesEmptyOptionalParametersInPagination() {
        // Arrange
        List<ThirdType> thirdTypes = Arrays.asList(thirdType);
        Page<ThirdType> page = new PageImpl<>(thirdTypes, PageRequest.of(0, 10), 1);

        when(listThirdTypeUseCase.countThirdTypesByEntId(entId)).thenReturn(1L);
        when(listThirdTypeUseCase.getAllThirdTypesWithSort(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<ThirdType>> response = controller.getThirdType(
                entId, Optional.empty(), Optional.empty(), "asc", null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Debe procesar correctamente diferentes órdenes de clasificación")
    void testProcessesDifferentSortOrders() {
        // Arrange
        List<ThirdType> thirdTypes = Arrays.asList(thirdType);
        Page<ThirdType> pageAsc = new PageImpl<>(thirdTypes, PageRequest.of(0, 10), 1);
        Page<ThirdType> pageDesc = new PageImpl<>(thirdTypes, PageRequest.of(0, 10), 1);

        when(listThirdTypeUseCase.countThirdTypesByEntId(entId)).thenReturn(1L);
        when(listThirdTypeUseCase.getAllThirdTypesWithSort(entId, 0, 10, "ttName", "asc"))
                .thenReturn(pageAsc);
        when(listThirdTypeUseCase.getAllThirdTypesWithSort(entId, 0, 10, "ttName", "desc"))
                .thenReturn(pageDesc);

        // Act
        ResponseEntity<Page<ThirdType>> responseAsc = controller.getThirdType(
                entId, Optional.of(0), Optional.of(10), "asc", null);
        ResponseEntity<Page<ThirdType>> responseDesc = controller.getThirdType(
                entId, Optional.of(0), Optional.of(10), "desc", null);

        // Assert
        assertNotNull(responseAsc);
        assertNotNull(responseDesc);
        verify(listThirdTypeUseCase).getAllThirdTypesWithSort(entId, 0, 10, "ttName", "asc");
        verify(listThirdTypeUseCase).getAllThirdTypesWithSort(entId, 0, 10, "ttName", "desc");
    }

    @Test
    @DisplayName("Debe retornar status correcto para cada tipo de operación")
    void testReturnsCorrectStatusForEachOperation() {
        // Arrange
        when(idRestMapper.toThirdType(thirdTypeCreateRequest)).thenReturn(thirdType);
        when(createThirdTypeUseCase.createThirdType(thirdType)).thenReturn(thirdType);
        when(idRestMapper.toThirdTypeResponse(thirdType)).thenReturn(thirdTypeResponse);

        when(idRestMapper.toThirdType(thirdTypeUpdateRequest)).thenReturn(thirdType);
        when(updateThirdTypeUseCase.updateThirdType(thirdType)).thenReturn(thirdType);

        when(deleteThirdTypeUseCase.deleteThirdType(1L, entId)).thenReturn(true);

        // Act
        ResponseEntity<ThirdTypeResponse> createResponse = controller.createThirdType(thirdTypeCreateRequest);
        ResponseEntity<ThirdTypeResponse> updateResponse = controller.updateThirdType(thirdTypeUpdateRequest);
        ResponseEntity<Boolean> deleteResponse = controller.deleteThirdType(1L, entId);

        // Assert
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
    }

    @Test
    @DisplayName("Debe manejar correctamente múltiples llamadas consecutivas al mismo endpoint")
    void testHandlesMultipleConsecutiveCalls() {
        // Arrange
        when(deleteThirdTypeUseCase.deleteThirdType(anyLong(), anyString())).thenReturn(true);

        // Act
        controller.deleteThirdType(1L, entId);
        controller.deleteThirdType(2L, entId);
        controller.deleteThirdType(3L, entId);

        // Assert
        verify(deleteThirdTypeUseCase, times(3)).deleteThirdType(anyLong(), anyString());
    }
}
