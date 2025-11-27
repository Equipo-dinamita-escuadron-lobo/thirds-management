package com.thirdsmanagement.thirds.unit.infrastructure.adapters.output.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeNameAlreadyExistsException;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeNotFound;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.PersonClassificationInvalidException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdAlreadyExists;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdNameAlreadyExistsException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdNotFound;
import com.thirdsmanagement.thirds.domain.model.PersonClassification;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.IdPersistenceAdapter;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.IdPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdsAndTypesRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class IdPersistenceAdapterUnitTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ThirdRepository thirdRepository;

    @Mock
    private ThirdTypeRepository thirdTypeRepository;

    @Mock
    private ThirdsAndTypesRepository thirdsAndTypesRepository;

    @Mock
    private TypeIdRepository typeIdRepository;

    @Mock
    private IdPersistenceMapper idPersistenceMapper;

    @InjectMocks
    private IdPersistenceAdapter idPersistenceAdapter;

    private ThirdType thirdType;
    private ThirdTypeEntity thirdTypeEntity;
    private TypeId typeId;
    private TypeIdEntity typeIdEntity;

    @BeforeEach
    void setUp() {
        thirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .entId("ENT001")
                .status(true)
                .build();

        thirdTypeEntity = ThirdTypeEntity.builder()
                .ttId(1L)
                .ttName("Cliente")
                .ttentId("ENT001")
                .status(true)
                .tenantId("TENANT001")
                .build();

        typeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .entId("ENT001")
                .status(true)
                .classification(PersonClassification.NATURAL_PERSON)
                .build();

        typeIdEntity = TypeIdEntity.builder()
                .id(1L)
                .tiId("CC")
                .tiName("Cédula de Ciudadanía")
                .tientId("ENT001")
                .status(true)
                .classification(PersonClassification.NATURAL_PERSON)
                .tenantId("TENANT001")
                .build();
    }

    @Test
    @DisplayName("Debe guardar tipo de tercero correctamente")
    void testSaveThirdTypeSuccess() {
        // Arrange
        when(idPersistenceMapper.toThirdTypeEntity(thirdType)).thenReturn(thirdTypeEntity);
        when(thirdTypeRepository.save(thirdTypeEntity)).thenReturn(thirdTypeEntity);
        when(idPersistenceMapper.toThirdType(thirdTypeEntity)).thenReturn(thirdType);

        // Act
        ThirdType result = idPersistenceAdapter.saveThirdType(thirdType);

        // Assert
        assertNotNull(result);
        assertEquals(thirdType.getThirdTypeName(), result.getThirdTypeName());
        verify(thirdTypeRepository).save(any(ThirdTypeEntity.class));
    }

    @Test
    @DisplayName("Debe obtener todos los tipos de tercero por empresa")
    void testGetAllThirdTypes() {
        // Arrange
        List<ThirdTypeEntity> entities = Arrays.asList(thirdTypeEntity);
        List<ThirdType> types = Arrays.asList(thirdType);
        when(thirdTypeRepository.findAllByTtentId("ENT001")).thenReturn(entities);
        when(idPersistenceMapper.toThirdTypeList(entities)).thenReturn(types);

        // Act
        List<ThirdType> result = idPersistenceAdapter.getALLThirdTypes("ENT001");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(thirdTypeRepository).findAllByTtentId("ENT001");
    }

    @Test
    @DisplayName("Debe guardar tipo de identificación correctamente")
    void testSaveTypeIdSuccess() {
        // Arrange
        when(typeIdRepository.existsByTiIdAndTientId("CC", "ENT001")).thenReturn(false);
        when(typeIdRepository.existsByTiNameIgnoreCaseAndTientId("Cédula de Ciudadanía", "ENT001"))
                .thenReturn(false);
        when(idPersistenceMapper.toTypeIdEntity(any(TypeId.class))).thenReturn(typeIdEntity);
        when(typeIdRepository.save(typeIdEntity)).thenReturn(typeIdEntity);
        when(idPersistenceMapper.toTypeId(typeIdEntity)).thenReturn(typeId);

        // Act
        TypeId result = idPersistenceAdapter.saveTypeId(typeId);

        // Assert
        assertNotNull(result);
        assertEquals(typeId.getTypeId(), result.getTypeId());
        verify(typeIdRepository).save(any(TypeIdEntity.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar tipo de identificación con código duplicado")
    void testSaveTypeIdThrowsExceptionWhenCodeExists() {
        // Arrange
        when(typeIdRepository.existsByTiIdAndTientId("CC", "ENT001")).thenReturn(true);

        // Act & Assert
        assertThrows(TypeIdAlreadyExists.class, () -> idPersistenceAdapter.saveTypeId(typeId));
        verify(typeIdRepository, never()).save(any(TypeIdEntity.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar tipo de identificación con nombre duplicado")
    void testSaveTypeIdThrowsExceptionWhenNameExists() {
        // Arrange
        when(typeIdRepository.existsByTiIdAndTientId("CC", "ENT001")).thenReturn(false);
        when(typeIdRepository.existsByTiNameIgnoreCaseAndTientId(anyString(), eq("ENT001")))
                .thenReturn(true);

        // Act & Assert
        assertThrows(TypeIdNameAlreadyExistsException.class, () -> idPersistenceAdapter.saveTypeId(typeId));
        verify(typeIdRepository, never()).save(any(TypeIdEntity.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar tipo de identificación null")
    void testSaveTypeIdThrowsExceptionWhenNull() {
        // Act & Assert
        assertThrows(TypeIdInvalidDataException.class, () -> idPersistenceAdapter.saveTypeId(null));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar tipo de identificación con código vacío")
    void testSaveTypeIdThrowsExceptionWhenCodeEmpty() {
        // Arrange
        typeId = TypeId.builder()
                .typeId("")
                .typeIdname("Nombre")
                .entId("ENT001")
                .classification(PersonClassification.NATURAL_PERSON)
                .build();

        // Act & Assert
        assertThrows(TypeIdInvalidDataException.class, () -> idPersistenceAdapter.saveTypeId(typeId));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar tipo de identificación con nombre vacío")
    void testSaveTypeIdThrowsExceptionWhenNameEmpty() {
        // Arrange
        typeId = TypeId.builder()
                .typeId("CC")
                .typeIdname("")
                .entId("ENT001")
                .classification(PersonClassification.NATURAL_PERSON)
                .build();

        // Act & Assert
        assertThrows(TypeIdInvalidDataException.class, () -> idPersistenceAdapter.saveTypeId(typeId));
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar tipo de identificación con clasificación null")
    void testSaveTypeIdThrowsExceptionWhenClassificationNull() {
        // Arrange
        typeId = TypeId.builder()
                .typeId("CC")
                .typeIdname("Nombre")
                .entId("ENT001")
                .classification(null)
                .build();

        // Act & Assert
        assertThrows(PersonClassificationInvalidException.class, () -> idPersistenceAdapter.saveTypeId(typeId));
    }

    @Test
    @DisplayName("Debe obtener todos los tipos de identificación por empresa")
    void testGetAllTypeIds() {
        // Arrange
        List<TypeIdEntity> entities = Arrays.asList(typeIdEntity);
        List<TypeId> typeIds = Arrays.asList(typeId);
        when(typeIdRepository.findAllByTientId("ENT001")).thenReturn(entities);
        when(idPersistenceMapper.toTypeIdList(entities)).thenReturn(typeIds);

        // Act
        List<TypeId> result = idPersistenceAdapter.getAllTypeIds("ENT001");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(typeIdRepository).findAllByTientId("ENT001");
    }

    @Test
    @DisplayName("Debe actualizar tipo de identificación correctamente")
    void testUpdateTypeIdSuccess() {
        // Arrange
        when(typeIdRepository.findById(1L)).thenReturn(Optional.of(typeIdEntity));
        when(typeIdRepository.existsByTiIdAndTientId("CC", "ENT001")).thenReturn(false);
        when(typeIdRepository.existsByTiNameIgnoreCaseAndTientId("Cédula de Ciudadanía", "ENT001"))
                .thenReturn(false);
        when(idPersistenceMapper.toTypeIdEntity(any(TypeId.class))).thenReturn(typeIdEntity);
        when(typeIdRepository.save(typeIdEntity)).thenReturn(typeIdEntity);
        when(idPersistenceMapper.toTypeId(typeIdEntity)).thenReturn(typeId);

        // Act
        TypeId result = idPersistenceAdapter.updateTypeId(typeId);

        // Assert
        assertNotNull(result);
        verify(typeIdRepository).save(any(TypeIdEntity.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar tipo de identificación inexistente")
    void testUpdateTypeIdThrowsExceptionWhenNotFound() {
        // Arrange
        when(typeIdRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TypeIdNotFound.class, () -> idPersistenceAdapter.updateTypeId(typeId));
        verify(typeIdRepository, never()).save(any(TypeIdEntity.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar tipo de identificación con datos inválidos de empresa")
    void testUpdateTypeIdThrowsExceptionWhenInvalidEntId() {
        // Arrange
        TypeIdEntity existingEntity = TypeIdEntity.builder()
                .id(1L)
                .tiId("CC")
                .tiName("Cédula")
                .tientId("ENT002")
                .build();
        when(typeIdRepository.findById(1L)).thenReturn(Optional.of(existingEntity));

        // Act & Assert
        assertThrows(TypeIdInvalidDataException.class, () -> idPersistenceAdapter.updateTypeId(typeId));
        verify(typeIdRepository, never()).save(any(TypeIdEntity.class));
    }

    @Test
    @DisplayName("Debe actualizar tipo de tercero correctamente")
    void testUpdateThirdTypeSuccess() {
        // Arrange
        when(thirdTypeRepository.findById(1L)).thenReturn(Optional.of(thirdTypeEntity));
        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Cliente", "ENT001")).thenReturn(false);
        when(idPersistenceMapper.toThirdTypeEntity(any(ThirdType.class))).thenReturn(thirdTypeEntity);
        when(thirdTypeRepository.save(thirdTypeEntity)).thenReturn(thirdTypeEntity);
        when(idPersistenceMapper.toThirdType(thirdTypeEntity)).thenReturn(thirdType);

        // Act
        ThirdType result = idPersistenceAdapter.updateThirdType(thirdType);

        // Assert
        assertNotNull(result);
        verify(thirdTypeRepository).save(any(ThirdTypeEntity.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar tipo de tercero inexistente")
    void testUpdateThirdTypeThrowsExceptionWhenNotFound() {
        // Arrange
        when(thirdTypeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ThirdTypeNotFound.class, () -> idPersistenceAdapter.updateThirdType(thirdType));
        verify(thirdTypeRepository, never()).save(any(ThirdTypeEntity.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar tipo de tercero con nombre duplicado")
    void testUpdateThirdTypeThrowsExceptionWhenNameExists() {
        // Arrange
        ThirdType updatedThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Proveedor")
                .entId("ENT001")
                .build();
        when(thirdTypeRepository.findById(1L)).thenReturn(Optional.of(thirdTypeEntity));
        when(thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId("Proveedor", "ENT001")).thenReturn(true);

        // Act & Assert
        assertThrows(ThirdTypeNameAlreadyExistsException.class,
                () -> idPersistenceAdapter.updateThirdType(updatedThirdType));
        verify(thirdTypeRepository, never()).save(any(ThirdTypeEntity.class));
    }

    @Test
    @DisplayName("Debe verificar existencia de tipo de identificación por ID")
    void testExistsTypeIdById() {
        // Arrange
        when(typeIdRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = idPersistenceAdapter.existsTypeIdById(1L);

        // Assert
        assertTrue(result);
        verify(typeIdRepository).existsById(1L);
    }

    @Test
    @DisplayName("Debe retornar falso al verificar existencia de tipo de identificación con ID null")
    void testExistsTypeIdByIdWhenNull() {
        // Act
        boolean result = idPersistenceAdapter.existsTypeIdById(null);

        // Assert
        assertFalse(result);
        verify(typeIdRepository, never()).existsById(anyLong());
    }

    @Test
    @DisplayName("Debe verificar existencia de tipo de tercero por ID")
    void testExistsThirdTypeById() {
        // Arrange
        when(thirdTypeRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = idPersistenceAdapter.existsThirdTypeById(1L);

        // Assert
        assertTrue(result);
        verify(thirdTypeRepository).existsById(1L);
    }

    @Test
    @DisplayName("Debe obtener tipo de identificación por ID")
    void testGetTypeIdById() {
        // Arrange
        when(typeIdRepository.findById(1L)).thenReturn(Optional.of(typeIdEntity));
        when(idPersistenceMapper.toTypeId(typeIdEntity)).thenReturn(typeId);

        // Act
        TypeId result = idPersistenceAdapter.getTypeIdById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(typeId.getTypeId(), result.getTypeId());
    }

    @Test
    @DisplayName("Debe retornar null al obtener tipo de identificación inexistente")
    void testGetTypeIdByIdWhenNotFound() {
        // Arrange
        when(typeIdRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        TypeId result = idPersistenceAdapter.getTypeIdById(1L);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe obtener tipo de tercero por ID")
    void testGetThirdTypeById() {
        // Arrange
        when(thirdTypeRepository.findById(1L)).thenReturn(Optional.of(thirdTypeEntity));
        when(idPersistenceMapper.toThirdType(thirdTypeEntity)).thenReturn(thirdType);

        // Act
        ThirdType result = idPersistenceAdapter.getThirdTypeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(thirdType.getThirdTypeName(), result.getThirdTypeName());
    }

    @Test
    @DisplayName("Debe eliminar tipo de tercero correctamente")
    void testDeleteThirdTypeSuccess() {
        // Arrange
        when(thirdTypeRepository.findByTtIdAndTtentId(1L, "ENT001")).thenReturn(Optional.of(thirdTypeEntity));

        // Act
        boolean result = idPersistenceAdapter.deleteThirdType(1L, "ENT001");

        // Assert
        assertTrue(result);
        verify(thirdTypeRepository).delete(thirdTypeEntity);
    }

    @Test
    @DisplayName("Debe retornar falso al eliminar tipo de tercero inexistente")
    void testDeleteThirdTypeWhenNotFound() {
        // Arrange
        when(thirdTypeRepository.findByTtIdAndTtentId(1L, "ENT001")).thenReturn(Optional.empty());

        // Act
        boolean result = idPersistenceAdapter.deleteThirdType(1L, "ENT001");

        // Assert
        assertFalse(result);
        verify(thirdTypeRepository, never()).delete(any(ThirdTypeEntity.class));
    }

    @Test
    @DisplayName("Debe verificar si tipo de tercero está en uso")
    void testIsThirdTypeInUse() {
        // Arrange
        when(thirdsAndTypesRepository.existsByTtId(1L)).thenReturn(true);

        // Act
        boolean result = idPersistenceAdapter.isThirdTypeInUse(1L, "ENT001");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe eliminar tipo de identificación correctamente")
    void testDeleteTypeIdSuccess() {
        // Arrange
        when(typeIdRepository.findById(1L)).thenReturn(Optional.of(typeIdEntity));

        // Act
        boolean result = idPersistenceAdapter.deleteTypeId(1L, "ENT001");

        // Assert
        assertTrue(result);
        verify(typeIdRepository).delete(typeIdEntity);
    }

    @Test
    @DisplayName("Debe retornar falso al eliminar tipo de identificación inexistente")
    void testDeleteTypeIdWhenNotFound() {
        // Arrange
        when(typeIdRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean result = idPersistenceAdapter.deleteTypeId(1L, "ENT001");

        // Assert
        assertFalse(result);
        verify(typeIdRepository, never()).delete(any(TypeIdEntity.class));
    }

    @Test
    @DisplayName("Debe verificar si tipo de identificación está en uso")
    void testIsTypeIdInUse() {
        // Arrange
        when(typeIdRepository.findById(1L)).thenReturn(Optional.of(typeIdEntity));
        when(thirdRepository.existsByTypeIdTiIdAndEntId("CC", "ENT001")).thenReturn(true);

        // Act
        boolean result = idPersistenceAdapter.isTypeIdInUse(1L, "ENT001");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe obtener tipos de identificación con paginación y ordenamiento")
    void testGetAllTypeIdsWithSort() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("tiName").ascending());
        Page<TypeIdEntity> entityPage = new PageImpl<>(Arrays.asList(typeIdEntity));
        when(typeIdRepository.findAllByTientIdPageable("ENT001", pageable)).thenReturn(entityPage);
        when(idPersistenceMapper.toTypeId(typeIdEntity)).thenReturn(typeId);

        // Act
        Page<TypeId> result = idPersistenceAdapter.getAllTypeIdsWithSort("ENT001", 0, 10, "typeIdname", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Debe buscar tipos de identificación por término de búsqueda")
    void testFindByEntIdAndSearch() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("tiName").ascending());
        Page<TypeIdEntity> entityPage = new PageImpl<>(Arrays.asList(typeIdEntity));
        when(typeIdRepository.findByTientIdAndSearch("ENT001", "Cédula", pageable)).thenReturn(entityPage);
        when(idPersistenceMapper.toTypeId(typeIdEntity)).thenReturn(typeId);

        // Act
        Page<TypeId> result = idPersistenceAdapter.findByEntIdAndSearch("ENT001", "Cédula", 0, 10, "typeIdname",
                "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Debe contar tipos de identificación por empresa")
    void testCountByEntId() {
        // Arrange
        when(typeIdRepository.countByTientId("ENT001")).thenReturn(5L);

        // Act
        long result = idPersistenceAdapter.countByEntId("ENT001");

        // Assert
        assertEquals(5L, result);
    }

    @Test
    @DisplayName("Debe contar tipos de identificación por empresa y término de búsqueda")
    void testCountByEntIdAndSearch() {
        // Arrange
        when(typeIdRepository.countByTientIdAndSearch("ENT001", "Cédula")).thenReturn(3L);

        // Act
        long result = idPersistenceAdapter.countByEntIdAndSearch("ENT001", "Cédula");

        // Assert
        assertEquals(3L, result);
    }

    @Test
    @DisplayName("Debe obtener tipos de tercero con paginación y ordenamiento")
    void testGetAllThirdTypesWithSort() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("ttName").ascending());
        Page<ThirdTypeEntity> entityPage = new PageImpl<>(Arrays.asList(thirdTypeEntity));
        when(thirdTypeRepository.findAllByTtentIdPageable("ENT001", pageable)).thenReturn(entityPage);
        when(idPersistenceMapper.toThirdType(thirdTypeEntity)).thenReturn(thirdType);

        // Act
        Page<ThirdType> result = idPersistenceAdapter.getAllThirdTypesWithSort("ENT001", 0, 10, "ttName", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Debe buscar tipos de tercero por término de búsqueda")
    void testFindThirdTypesByEntIdAndSearch() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("ttName").ascending());
        Page<ThirdTypeEntity> entityPage = new PageImpl<>(Arrays.asList(thirdTypeEntity));
        when(thirdTypeRepository.findByTtentIdAndSearch("ENT001", "Cliente", pageable)).thenReturn(entityPage);
        when(idPersistenceMapper.toThirdType(thirdTypeEntity)).thenReturn(thirdType);

        // Act
        Page<ThirdType> result = idPersistenceAdapter.findThirdTypesByEntIdAndSearch("ENT001", "Cliente", 0, 10,
                "ttName", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Debe contar tipos de tercero por empresa")
    void testCountThirdTypesByEntId() {
        // Arrange
        when(thirdTypeRepository.countByTtentId("ENT001")).thenReturn(4L);

        // Act
        long result = idPersistenceAdapter.countThirdTypesByEntId("ENT001");

        // Assert
        assertEquals(4L, result);
    }

    @Test
    @DisplayName("Debe contar tipos de tercero por empresa y término de búsqueda")
    void testCountThirdTypesByEntIdAndSearch() {
        // Arrange
        when(thirdTypeRepository.countByTtentIdAndSearch("ENT001", "Cliente")).thenReturn(2L);

        // Act
        long result = idPersistenceAdapter.countThirdTypesByEntIdAndSearch("ENT001", "Cliente");

        // Assert
        assertEquals(2L, result);
    }

    @Test
    @DisplayName("Debe obtener tipos de identificación activos con paginación")
    void testGetAllActiveTypeIds() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("tiName").ascending());
        Page<TypeIdEntity> entityPage = new PageImpl<>(Arrays.asList(typeIdEntity));
        when(typeIdRepository.findActiveByTientIdPageable("ENT001", pageable)).thenReturn(entityPage);
        when(idPersistenceMapper.toTypeId(typeIdEntity)).thenReturn(typeId);

        // Act
        Page<TypeId> result = idPersistenceAdapter.getAllActiveTypeIds("ENT001", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Debe contar tipos de identificación activos por empresa")
    void testCountActiveByEntId() {
        // Arrange
        when(typeIdRepository.countActiveByTientId("ENT001")).thenReturn(3L);

        // Act
        long result = idPersistenceAdapter.countActiveByEntId("ENT001");

        // Assert
        assertEquals(3L, result);
    }

    @Test
    @DisplayName("Debe obtener tipos de tercero activos con paginación")
    void testGetAllActiveThirdTypes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("ttName").ascending());
        Page<ThirdTypeEntity> entityPage = new PageImpl<>(Arrays.asList(thirdTypeEntity));
        when(thirdTypeRepository.findActiveByTtentIdPageable("ENT001", pageable)).thenReturn(entityPage);
        when(idPersistenceMapper.toThirdType(thirdTypeEntity)).thenReturn(thirdType);

        // Act
        Page<ThirdType> result = idPersistenceAdapter.getAllActiveThirdTypes("ENT001", 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Debe contar tipos de tercero activos por empresa")
    void testCountActiveThirdTypesByEntId() {
        // Arrange
        when(thirdTypeRepository.countActiveByTtentId("ENT001")).thenReturn(2L);

        // Act
        long result = idPersistenceAdapter.countActiveThirdTypesByEntId("ENT001");

        // Assert
        assertEquals(2L, result);
    }

    @Test
    @DisplayName("Debe verificar si tipo de identificación tiene terceros con movimientos")
    void testHasTypeIdThirdsWithMovements() {
        // Arrange
        when(typeIdRepository.findById(1L)).thenReturn(Optional.of(typeIdEntity));
        when(thirdRepository.existsByTypeIdTiIdAndEntIdAndUsageCountGreaterThan("CC", "ENT001", 0)).thenReturn(true);

        // Act
        boolean result = idPersistenceAdapter.hasTypeIdThirdsWithMovements(1L, "ENT001");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe verificar si tipo de tercero tiene terceros con movimientos")
    void testHasThirdTypeThirdsWithMovements() {
        // Arrange
        when(thirdRepository.existsByThirdTypeIdAndEntIdAndUsageCountGreaterThan(1L, "ENT001", 0)).thenReturn(true);

        // Act
        boolean result = idPersistenceAdapter.hasThirdTypeThirdsWithMovements(1L, "ENT001");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe mapear campo de ordenamiento por defecto cuando es null")
    void testMapTypeIdSortFieldWhenNull() {
        // Arrange
        Page<TypeIdEntity> entityPage = new PageImpl<>(Arrays.asList(typeIdEntity));
        when(typeIdRepository.findAllByTientIdPageable(eq("ENT001"), any(Pageable.class))).thenReturn(entityPage);
        when(idPersistenceMapper.toTypeId(typeIdEntity)).thenReturn(typeId);

        // Act
        Page<TypeId> result = idPersistenceAdapter.getAllTypeIdsWithSort("ENT001", 0, 10, null, "asc");

        // Assert
        assertNotNull(result);
        verify(typeIdRepository).findAllByTientIdPageable(eq("ENT001"), any(Pageable.class));
    }

    @Test
    @DisplayName("Debe mapear campo de ordenamiento 'typeid' a 'tiId'")
    void testMapTypeIdSortFieldForTypeId() {
        // Arrange
        Page<TypeIdEntity> entityPage = new PageImpl<>(Arrays.asList(typeIdEntity));
        when(typeIdRepository.findAllByTientIdPageable(eq("ENT001"), any(Pageable.class))).thenReturn(entityPage);
        when(idPersistenceMapper.toTypeId(typeIdEntity)).thenReturn(typeId);

        // Act
        Page<TypeId> result = idPersistenceAdapter.getAllTypeIdsWithSort("ENT001", 0, 10, "typeid", "asc");

        // Assert
        assertNotNull(result);
        verify(typeIdRepository).findAllByTientIdPageable(eq("ENT001"), any(Pageable.class));
    }

    @Test
    @DisplayName("Debe retornar falso cuando tipo de identificación no existe al verificar movimientos")
    void testHasTypeIdThirdsWithMovementsWhenTypeIdNotFound() {
        // Arrange
        when(typeIdRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean result = idPersistenceAdapter.hasTypeIdThirdsWithMovements(1L, "ENT001");

        // Assert
        assertFalse(result);
        verify(thirdRepository, never()).existsByTypeIdTiIdAndEntIdAndUsageCountGreaterThan(anyString(), anyString(),
                eq(0));
    }

    @Test
    @DisplayName("Debe retornar falso cuando entId es null al verificar uso de tipo de tercero")
    void testIsThirdTypeInUseWhenEntIdNull() {
        // Act
        boolean result = idPersistenceAdapter.isThirdTypeInUse(1L, null);

        // Assert
        assertFalse(result);
        verify(thirdsAndTypesRepository, never()).existsByTtId(anyLong());
    }

    @Test
    @DisplayName("Debe retornar falso al eliminar tipo de tercero con entId vacío")
    void testDeleteThirdTypeWhenEntIdEmpty() {
        // Act
        boolean result = idPersistenceAdapter.deleteThirdType(1L, "");

        // Assert
        assertFalse(result);
        verify(thirdTypeRepository, never()).findByTtIdAndTtentId(anyLong(), anyString());
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar tipo de tercero null")
    void testUpdateThirdTypeThrowsExceptionWhenNull() {
        // Act & Assert
        assertThrows(ThirdTypeInvalidDataException.class, () -> idPersistenceAdapter.updateThirdType(null));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar tipo de tercero con ID null")
    void testUpdateThirdTypeThrowsExceptionWhenIdNull() {
        // Arrange
        ThirdType invalidThirdType = ThirdType.builder()
                .thirdTypeId(null)
                .thirdTypeName("Cliente")
                .build();

        // Act & Assert
        assertThrows(ThirdTypeInvalidDataException.class, () -> idPersistenceAdapter.updateThirdType(invalidThirdType));
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar tipo de tercero con nombre vacío")
    void testUpdateThirdTypeThrowsExceptionWhenNameEmpty() {
        // Arrange
        ThirdType invalidThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("")
                .build();

        // Act & Assert
        assertThrows(ThirdTypeInvalidDataException.class, () -> idPersistenceAdapter.updateThirdType(invalidThirdType));
    }
}
