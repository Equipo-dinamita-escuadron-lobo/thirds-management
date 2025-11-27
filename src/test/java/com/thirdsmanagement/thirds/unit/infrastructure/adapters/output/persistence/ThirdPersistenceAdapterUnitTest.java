package com.thirdsmanagement.thirds.unit.infrastructure.adapters.output.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.application.service.geography.GeographyLoaderService;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdNotFound;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeForeignKeyViolationException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdForeignKeyViolationException;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.PersonClassification;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.ThirdPersistenceAdapter;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdsAndTypesEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.ThirdPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdsAndTypesRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;
import com.thirdsmanagement.thirds.infrastructure.multitenancy.utils.TenantContext;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ThirdPersistenceAdapterUnitTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ThirdRepository thirdRepository;

    @Mock
    private ThirdTypeRepository thirdTypeRepository;

    @Mock
    private TypeIdRepository typeIdRepository;

    @Mock
    private ThirdsAndTypesRepository thirdsAndTypesRepository;

    @Mock
    private ThirdPersistenceMapper thirdPersistenceMapper;

    @Mock
    private GeographyLoaderService geographyLoaderService;

    @Mock
    private GeographyOutputPort geographyOutputPort;

    @InjectMocks
    private ThirdPersistenceAdapter thirdPersistenceAdapter;

    private Third sampleThird;
    private ThirdEntity sampleThirdEntity;
    private TypeIdEntity sampleTypeIdEntity;
    private ThirdTypeEntity sampleThirdTypeEntity;
    private TypeId sampleTypeId;
    private ThirdType sampleThirdType;
    private Country sampleCountry;
    private State sampleState;
    private City sampleCity;

    @BeforeEach
    void setUp() {
        TenantContext.setTenantId("TEST_TENANT");

        sampleTypeId = TypeId.builder()
                .id(1L)
                .typeId("CC")
                .typeIdname("Cedula Ciudadania")
                .classification(PersonClassification.NATURAL_PERSON)
                .entId("ENT001")
                .status(true)
                .build();

        sampleTypeIdEntity = TypeIdEntity.builder()
                .id(1L)
                .tiId("CC")
                .tiName("Cedula Ciudadania")
                .classification(PersonClassification.NATURAL_PERSON)
                .tientId("ENT001")
                .status(true)
                .build();

        sampleThirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Cliente")
                .entId("ENT001")
                .status(true)
                .build();

        sampleThirdTypeEntity = ThirdTypeEntity.builder()
                .ttId(1L)
                .ttName("Cliente")
                .ttentId("ENT001")
                .status(true)
                .build();

        sampleCountry = Country.builder()
                .countryCode("CO")
                .countryName("Colombia")
                .build();

        sampleState = State.builder()
                .stateCode("05")
                .stateName("Antioquia")
                .countryCode("CO")
                .country(sampleCountry)
                .build();

        sampleCity = City.builder()
                .cityCode("05001")
                .cityName("Medellin")
                .stateCode("05")
                .countryCode("CO")
                .state(sampleState)
                .build();

        Set<ThirdType> thirdTypes = new HashSet<>();
        thirdTypes.add(sampleThirdType);

        sampleThird = Third.builder()
                .thId(1L)
                .entId("ENT001")
                .typeId(sampleTypeId)
                .thirdTypes(thirdTypes)
                .personType(ePersonType.Natural)
                .names("Juan Carlos")
                .lastNames("Perez Lopez")
                .idNumber(123456789L)
                .gender(eThirdGender.Masculino)
                .state(true)
                .country(sampleCountry)
                .province(sampleState)
                .city(sampleCity)
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("juan@test.com")
                .build();

        sampleThirdEntity = ThirdEntity.builder()
                .thId(1L)
                .entId("ENT001")
                .typeId(sampleTypeIdEntity)
                .personType(ePersonType.Natural)
                .names("Juan Carlos")
                .lastNames("Perez Lopez")
                .idNumber(123456789L)
                .gender("Masculino")
                .state(true)
                .country("CO")
                .province("05")
                .city("05001")
                .address("Calle 123")
                .phoneNumber("3001234567")
                .email("juan@test.com")
                .tenantId("TEST_TENANT")
                .usageCount(0)
                .build();
    }

    @Test
    @DisplayName("test Guarda tercero exitosamente")
    void testSaveThirdSuccessfully() {
        // Arrange
        when(typeIdRepository.existsById(1L)).thenReturn(true);
        when(thirdTypeRepository.existsById(1L)).thenReturn(true);
        when(typeIdRepository.findById(1L)).thenReturn(Optional.of(sampleTypeIdEntity));
        when(thirdPersistenceMapper.toThirdEntity(any(Third.class))).thenReturn(sampleThirdEntity);
        when(thirdRepository.save(any(ThirdEntity.class))).thenReturn(sampleThirdEntity);
        when(thirdPersistenceMapper.toThird(any(ThirdEntity.class))).thenReturn(sampleThird);
        when(geographyLoaderService.loadCountryByCode("CO")).thenReturn(sampleCountry);
        when(geographyLoaderService.loadStateByCode("05", "CO")).thenReturn(sampleState);
        when(geographyLoaderService.loadCityByCode("05001", "05", "CO")).thenReturn(sampleCity);

        // Act
        Third result = thirdPersistenceAdapter.saveThird(sampleThird);

        // Assert
        assertNotNull(result);
        verify(typeIdRepository).existsById(1L);
        verify(thirdTypeRepository).existsById(1L);
        verify(thirdRepository).save(any(ThirdEntity.class));
        verify(thirdsAndTypesRepository).save(any(ThirdsAndTypesEntity.class));
    }

    @Test
    @DisplayName("test Lanza excepción cuando tercero es null")
    void testSaveThirdThrowsExceptionWhenThirdIsNull() {
        // Arrange & Act & Assert
        assertThrows(IllegalArgumentException.class, () -> thirdPersistenceAdapter.saveThird(null));
    }

    @Test
    @DisplayName("test Lanza excepción cuando TypeId es null")
    void testSaveThirdThrowsExceptionWhenTypeIdIsNull() {
        // Arrange
        sampleThird = Third.builder()
                .entId("ENT001")
                .typeId(null)
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> thirdPersistenceAdapter.saveThird(sampleThird));
    }

    @Test
    @DisplayName("test Lanza excepción cuando TypeId no existe")
    void testSaveThirdThrowsExceptionWhenTypeIdDoesNotExist() {
        // Arrange
        when(typeIdRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(TypeIdForeignKeyViolationException.class, () -> thirdPersistenceAdapter.saveThird(sampleThird));
    }

    @Test
    @DisplayName("test Lanza excepción cuando ThirdType no existe")
    void testSaveThirdThrowsExceptionWhenThirdTypeDoesNotExist() {
        // Arrange
        when(typeIdRepository.existsById(1L)).thenReturn(true);
        when(thirdTypeRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ThirdTypeForeignKeyViolationException.class, () -> thirdPersistenceAdapter.saveThird(sampleThird));
    }

    @Test
    @DisplayName("test Guarda tercero sin tipos de tercero")
    void testSaveThirdWithoutThirdTypes() {
        // Arrange
        sampleThird.setThirdTypes(new HashSet<>());
        when(typeIdRepository.existsById(1L)).thenReturn(true);
        when(typeIdRepository.findById(1L)).thenReturn(Optional.of(sampleTypeIdEntity));
        when(thirdPersistenceMapper.toThirdEntity(any(Third.class))).thenReturn(sampleThirdEntity);
        when(thirdRepository.save(any(ThirdEntity.class))).thenReturn(sampleThirdEntity);
        when(thirdPersistenceMapper.toThird(any(ThirdEntity.class))).thenReturn(sampleThird);
        when(geographyLoaderService.loadCountryByCode("CO")).thenReturn(sampleCountry);
        when(geographyLoaderService.loadStateByCode("05", "CO")).thenReturn(sampleState);
        when(geographyLoaderService.loadCityByCode("05001", "05", "CO")).thenReturn(sampleCity);

        // Act
        Third result = thirdPersistenceAdapter.saveThird(sampleThird);

        // Assert
        assertNotNull(result);
        verify(thirdsAndTypesRepository, never()).save(any(ThirdsAndTypesEntity.class));
    }

    @Test
    @DisplayName("test Propaga DataIntegrityViolationException al guardar tercero")
    void testSaveThirdPropagatesDataIntegrityViolationException() {
        // Arrange
        when(typeIdRepository.existsById(1L)).thenReturn(true);
        when(thirdTypeRepository.existsById(1L)).thenReturn(true);
        when(typeIdRepository.findById(1L)).thenReturn(Optional.of(sampleTypeIdEntity));
        when(thirdPersistenceMapper.toThirdEntity(any(Third.class))).thenReturn(sampleThirdEntity);
        when(thirdRepository.save(any(ThirdEntity.class))).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> thirdPersistenceAdapter.saveThird(sampleThird));
    }

    @Test
    @DisplayName("test Guarda múltiples terceros exitosamente")
    void testSaveAllThirdsSuccessfully() {
        // Arrange
        List<Third> thirds = Arrays.asList(sampleThird);
        List<ThirdEntity> entities = Arrays.asList(sampleThirdEntity);
        
        when(typeIdRepository.findAllById(any())).thenReturn(Arrays.asList(sampleTypeIdEntity));
        when(thirdPersistenceMapper.toThirdEntity(any(Third.class))).thenReturn(sampleThirdEntity);
        when(thirdRepository.saveAll(any())).thenReturn(entities);

        // Act
        List<Third> result = thirdPersistenceAdapter.saveAllThirds(thirds);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(thirdRepository).saveAll(any());
        verify(thirdsAndTypesRepository).saveAll(any());
    }

    @Test
    @DisplayName("test Retorna lista vacía cuando lista de terceros es null")
    void testSaveAllThirdsReturnsEmptyListWhenNull() {
        // Arrange & Act
        List<Third> result = thirdPersistenceAdapter.saveAllThirds(null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("test Retorna lista vacía cuando lista de terceros está vacía")
    void testSaveAllThirdsReturnsEmptyListWhenEmpty() {
        // Arrange & Act
        List<Third> result = thirdPersistenceAdapter.saveAllThirds(new ArrayList<>());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("test Obtiene tercero por ID y empresa exitosamente")
    void testGetThirdByIdSuccessfully() {
        // Arrange
        when(thirdRepository.findByThIdAndEntId(1L, "ENT001")).thenReturn(Optional.of(sampleThirdEntity));
        when(thirdPersistenceMapper.toThird(any(ThirdEntity.class))).thenReturn(sampleThird);
        when(geographyLoaderService.loadCountryByCode("CO")).thenReturn(sampleCountry);
        when(geographyLoaderService.loadStateByCode("05", "CO")).thenReturn(sampleState);
        when(geographyLoaderService.loadCityByCode("05001", "05", "CO")).thenReturn(sampleCity);
        when(thirdsAndTypesRepository.findByThId(1L)).thenReturn(Arrays.asList(
                ThirdsAndTypesEntity.builder().thId(1L).ttId(1L).build()
        ));
        when(thirdTypeRepository.findById(1L)).thenReturn(Optional.of(sampleThirdTypeEntity));

        // Act
        Optional<Third> result = thirdPersistenceAdapter.getThirdById(1L, "ENT001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getThId());
    }

    @Test
    @DisplayName("test Retorna vacío cuando tercero no existe")
    void testGetThirdByIdReturnsEmptyWhenNotFound() {
        // Arrange
        when(thirdRepository.findByThIdAndEntId(1L, "ENT001")).thenReturn(Optional.empty());

        // Act
        Optional<Third> result = thirdPersistenceAdapter.getThirdById(1L, "ENT001");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("test Encuentra tercero por ID sin filtrar por empresa")
    void testFindByIdSuccessfully() {
        // Arrange
        when(thirdRepository.findByIdWithTypeId(1L)).thenReturn(Optional.of(sampleThirdEntity));
        when(thirdPersistenceMapper.toThird(any(ThirdEntity.class))).thenReturn(sampleThird);
        when(geographyLoaderService.loadCountryByCode("CO")).thenReturn(sampleCountry);
        when(geographyLoaderService.loadStateByCode("05", "CO")).thenReturn(sampleState);
        when(geographyLoaderService.loadCityByCode("05001", "05", "CO")).thenReturn(sampleCity);
        when(thirdsAndTypesRepository.findByThId(1L)).thenReturn(Arrays.asList(
                ThirdsAndTypesEntity.builder().thId(1L).ttId(1L).build()
        ));
        when(thirdTypeRepository.findById(1L)).thenReturn(Optional.of(sampleThirdTypeEntity));

        // Act
        Third result = thirdPersistenceAdapter.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getThId());
    }

    @Test
    @DisplayName("test Retorna null cuando tercero no existe por ID")
    void testFindByIdReturnsNullWhenNotFound() {
        // Arrange
        when(thirdRepository.findByIdWithTypeId(1L)).thenReturn(Optional.empty());

        // Act
        Third result = thirdPersistenceAdapter.findById(1L);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("test Verifica existencia de tercero exitosamente")
    void testExistThirdByIdReturnsTrue() {
        // Arrange
        when(thirdRepository.existThirdByThIdAndEntId(1L, "ENT001")).thenReturn(true);

        // Act
        boolean result = thirdPersistenceAdapter.existThirdById(1L, "ENT001");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("test Verifica no existencia de tercero")
    void testExistThirdByIdReturnsFalse() {
        // Arrange
        when(thirdRepository.existThirdByThIdAndEntId(1L, "ENT001")).thenReturn(false);

        // Act
        boolean result = thirdPersistenceAdapter.existThirdById(1L, "ENT001");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("test Cambia estado de tercero exitosamente")
    void testChangeThirdStateSuccessfully() {
        // Arrange
        when(thirdRepository.findByThIdAndEntId(1L, "ENT001")).thenReturn(Optional.of(sampleThirdEntity));
        when(thirdRepository.save(any(ThirdEntity.class))).thenReturn(sampleThirdEntity);

        // Act
        boolean result = thirdPersistenceAdapter.changeThirdState(1L, "ENT001");

        // Assert
        assertTrue(result);
        verify(thirdRepository).save(any(ThirdEntity.class));
    }

    @Test
    @DisplayName("test Retorna false cuando tercero no existe al cambiar estado")
    void testChangeThirdStateReturnsFalseWhenNotFound() {
        // Arrange
        when(thirdRepository.findByThIdAndEntId(1L, "ENT001")).thenReturn(Optional.empty());

        // Act
        boolean result = thirdPersistenceAdapter.changeThirdState(1L, "ENT001");

        // Assert
        assertFalse(result);
        verify(thirdRepository, never()).save(any(ThirdEntity.class));
    }

    @Test
    @DisplayName("test Obtiene página de terceros por empresa")
    void testGetAllThirdsBySuccessfully() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ThirdEntity> pageEntities = new PageImpl<>(Arrays.asList(sampleThirdEntity));
        
        when(thirdRepository.getThirdsBy("ENT001", pageable)).thenReturn(pageEntities);
        when(thirdPersistenceMapper.toThird(any(ThirdEntity.class))).thenReturn(sampleThird);
        when(geographyLoaderService.loadCountryByCode("CO")).thenReturn(sampleCountry);
        when(geographyLoaderService.loadStateByCode("05", "CO")).thenReturn(sampleState);
        when(geographyLoaderService.loadCityByCode("05001", "05", "CO")).thenReturn(sampleCity);
        when(thirdsAndTypesRepository.findByThId(1L)).thenReturn(new ArrayList<>());

        // Act
        Page<Third> result = thirdPersistenceAdapter.getAllThirdsBy("ENT001", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("test Obtiene terceros filtrados por estado")
    void testGetAllThirdsByStateSuccessfully() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ThirdEntity> pageEntities = new PageImpl<>(Arrays.asList(sampleThirdEntity));
        
        when(thirdRepository.getThirdsByEntIdAndState("ENT001", true, pageable)).thenReturn(pageEntities);
        when(thirdPersistenceMapper.toThird(any(ThirdEntity.class))).thenReturn(sampleThird);
        when(geographyLoaderService.loadCountryByCode("CO")).thenReturn(sampleCountry);
        when(geographyLoaderService.loadStateByCode("05", "CO")).thenReturn(sampleState);
        when(geographyLoaderService.loadCityByCode("05001", "05", "CO")).thenReturn(sampleCity);
        when(thirdsAndTypesRepository.findByThId(1L)).thenReturn(new ArrayList<>());

        // Act
        Page<Third> result = thirdPersistenceAdapter.getAllThirdsByState("ENT001", true, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("test Actualiza tercero exitosamente")
    void testUpdateThirdSuccessfully() {
        // Arrange
        when(thirdRepository.findById(1L)).thenReturn(Optional.of(sampleThirdEntity));
        when(typeIdRepository.existsById(1L)).thenReturn(true);
        when(thirdTypeRepository.existsById(1L)).thenReturn(true);
        when(typeIdRepository.findById(1L)).thenReturn(Optional.of(sampleTypeIdEntity));
        when(thirdRepository.save(any(ThirdEntity.class))).thenReturn(sampleThirdEntity);
        when(thirdsAndTypesRepository.findByThId(1L)).thenReturn(new ArrayList<>());
        when(thirdPersistenceMapper.toThird(any(ThirdEntity.class))).thenReturn(sampleThird);
        when(geographyLoaderService.loadCountryByCode("CO")).thenReturn(sampleCountry);
        when(geographyLoaderService.loadStateByCode("05", "CO")).thenReturn(sampleState);
        when(geographyLoaderService.loadCityByCode("05001", "05", "CO")).thenReturn(sampleCity);

        // Act
        Third result = thirdPersistenceAdapter.updateThird(sampleThird);

        // Assert
        assertNotNull(result);
        verify(thirdRepository).save(any(ThirdEntity.class));
        verify(thirdsAndTypesRepository).save(any(ThirdsAndTypesEntity.class));
    }

    @Test
    @DisplayName("test Lanza excepción cuando tercero a actualizar es null")
    void testUpdateThirdThrowsExceptionWhenThirdIsNull() {
        // Arrange & Act & Assert
        assertThrows(IllegalArgumentException.class, () -> thirdPersistenceAdapter.updateThird(null));
    }

    @Test
    @DisplayName("test Lanza excepción cuando ID de tercero a actualizar es null")
    void testUpdateThirdThrowsExceptionWhenThirdIdIsNull() {
        // Arrange
        sampleThird.setThId(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> thirdPersistenceAdapter.updateThird(sampleThird));
    }

    @Test
    @DisplayName("test Lanza excepción cuando tercero a actualizar no existe")
    void testUpdateThirdThrowsExceptionWhenThirdNotFound() {
        // Arrange
        when(thirdRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ThirdNotFound.class, () -> thirdPersistenceAdapter.updateThird(sampleThird));
    }

    @Test
    @DisplayName("test Elimina tercero y sus relaciones exitosamente")
    void testDeleteThirdSuccessfully() {
        // Arrange
        when(thirdRepository.findByThIdAndEntId(1L, "ENT001")).thenReturn(Optional.of(sampleThirdEntity));
        when(thirdsAndTypesRepository.findByThId(1L)).thenReturn(Arrays.asList(
                ThirdsAndTypesEntity.builder().thId(1L).ttId(1L).build()
        ));

        // Act
        boolean result = thirdPersistenceAdapter.deleteThird(1L, "ENT001");

        // Assert
        assertTrue(result);
        verify(thirdsAndTypesRepository).deleteAll(any());
        verify(thirdRepository).delete(any(ThirdEntity.class));
    }

    @Test
    @DisplayName("test Lanza excepción cuando tercero a eliminar no existe")
    void testDeleteThirdThrowsExceptionWhenNotFound() {
        // Arrange
        when(thirdRepository.findByThIdAndEntId(1L, "ENT001")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ThirdNotFound.class, () -> thirdPersistenceAdapter.deleteThird(1L, "ENT001"));
    }

    @Test
    @DisplayName("test Lanza excepción cuando parámetros de eliminación son inválidos")
    void testDeleteThirdThrowsExceptionWhenParametersAreInvalid() {
        // Arrange & Act & Assert
        assertThrows(IllegalArgumentException.class, () -> thirdPersistenceAdapter.deleteThird(null, "ENT001"));
        assertThrows(IllegalArgumentException.class, () -> thirdPersistenceAdapter.deleteThird(1L, null));
        assertThrows(IllegalArgumentException.class, () -> thirdPersistenceAdapter.deleteThird(1L, ""));
    }

    @Test
    @DisplayName("test Cuenta total de terceros por empresa")
    void testCountAllThirdsByEntIdSuccessfully() {
        // Arrange
        when(thirdRepository.countByEntId("ENT001")).thenReturn(10L);

        // Act
        long result = thirdPersistenceAdapter.countAllThirdsByEntId("ENT001");

        // Assert
        assertEquals(10L, result);
    }

    @Test
    @DisplayName("test Lanza excepción al contar con entId null")
    void testCountAllThirdsByEntIdThrowsExceptionWhenEntIdIsNull() {
        // Arrange & Act & Assert
        assertThrows(IllegalArgumentException.class, () -> thirdPersistenceAdapter.countAllThirdsByEntId(null));
    }

    @Test
    @DisplayName("test Actualiza estado masivamente")
    void testBulkUpdateThirdStateSuccessfully() {
        // Arrange
        when(thirdRepository.bulkUpdateStateByEntId("ENT001", false)).thenReturn(5);

        // Act
        int result = thirdPersistenceAdapter.bulkUpdateThirdState("ENT001", false);

        // Assert
        assertEquals(5, result);
        verify(thirdRepository).bulkUpdateStateByEntId("ENT001", false);
    }

    @Test
    @DisplayName("test Encuentra números de identificación existentes")
    void testFindExistingIdNumbersSuccessfully() {
        // Arrange
        Set<Long> idNumbers = new HashSet<>(Arrays.asList(123456789L, 987654321L));
        List<Long> existingIds = Arrays.asList(123456789L);
        
        when(thirdRepository.findExistingIdNumbers(idNumbers, "ENT001")).thenReturn(existingIds);

        // Act
        Set<Long> result = thirdPersistenceAdapter.findExistingIdNumbers(idNumbers, "ENT001");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(123456789L));
    }

    @Test
    @DisplayName("test Retorna conjunto vacío cuando lista de IDs es null")
    void testFindExistingIdNumbersReturnsEmptyWhenNull() {
        // Arrange & Act
        Set<Long> result = thirdPersistenceAdapter.findExistingIdNumbers(null, "ENT001");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("test Lanza excepción cuando entId es null al buscar IDs")
    void testFindExistingIdNumbersThrowsExceptionWhenEntIdIsNull() {
        // Arrange
        Set<Long> idNumbers = new HashSet<>(Arrays.asList(123456789L));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> thirdPersistenceAdapter.findExistingIdNumbers(idNumbers, null));
    }

    @Test
    @DisplayName("test Busca terceros con término de búsqueda y ordenamiento")
    void testFindByEntIdAndSearchSuccessfully() {
        // Arrange
        Page<ThirdEntity> pageEntities = new PageImpl<>(Arrays.asList(sampleThirdEntity));
        
        when(thirdRepository.findByEntIdAndSearch(eq("ENT001"), eq("Juan"), any(Pageable.class)))
                .thenReturn(pageEntities);
        when(thirdPersistenceMapper.toThird(any(ThirdEntity.class))).thenReturn(sampleThird);
        when(geographyLoaderService.loadCountryByCode("CO")).thenReturn(sampleCountry);
        when(geographyLoaderService.loadStateByCode("05", "CO")).thenReturn(sampleState);
        when(geographyLoaderService.loadCityByCode("05001", "05", "CO")).thenReturn(sampleCity);
        when(thirdsAndTypesRepository.findByThId(1L)).thenReturn(new ArrayList<>());

        // Act
        Page<Third> result = thirdPersistenceAdapter.findByEntIdAndSearch("ENT001", "Juan", 0, 10, "names", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("test Cuenta terceros con término de búsqueda")
    void testCountByEntIdAndSearchSuccessfully() {
        // Arrange
        when(thirdRepository.countByEntIdAndSearch("ENT001", "Juan")).thenReturn(5L);

        // Act
        long result = thirdPersistenceAdapter.countByEntIdAndSearch("ENT001", "Juan");

        // Assert
        assertEquals(5L, result);
    }

    @Test
    @DisplayName("test Obtiene terceros con ordenamiento personalizado")
    void testGetAllThirdsByWithSortSuccessfully() {
        // Arrange
        Page<ThirdEntity> pageEntities = new PageImpl<>(Arrays.asList(sampleThirdEntity));
        
        when(thirdRepository.getThirdsBy(eq("ENT001"), any(Pageable.class))).thenReturn(pageEntities);
        when(thirdPersistenceMapper.toThird(any(ThirdEntity.class))).thenReturn(sampleThird);
        when(geographyLoaderService.loadCountryByCode("CO")).thenReturn(sampleCountry);
        when(geographyLoaderService.loadStateByCode("05", "CO")).thenReturn(sampleState);
        when(geographyLoaderService.loadCityByCode("05001", "05", "CO")).thenReturn(sampleCity);
        when(thirdsAndTypesRepository.findByThId(1L)).thenReturn(new ArrayList<>());

        // Act
        Page<Third> result = thirdPersistenceAdapter.getAllThirdsByWithSort("ENT001", 0, 10, "names", "desc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("test Obtiene terceros activos con ordenamiento")
    void testGetAllActiveThirdsByWithSortSuccessfully() {
        // Arrange
        Page<ThirdEntity> pageEntities = new PageImpl<>(Arrays.asList(sampleThirdEntity));
        
        when(thirdRepository.getActiveThirdsBy(eq("ENT001"), any(Pageable.class))).thenReturn(pageEntities);
        when(thirdPersistenceMapper.toThird(any(ThirdEntity.class))).thenReturn(sampleThird);
        when(geographyLoaderService.loadCountryByCode("CO")).thenReturn(sampleCountry);
        when(geographyLoaderService.loadStateByCode("05", "CO")).thenReturn(sampleState);
        when(geographyLoaderService.loadCityByCode("05001", "05", "CO")).thenReturn(sampleCity);
        when(thirdsAndTypesRepository.findByThId(1L)).thenReturn(new ArrayList<>());

        // Act
        Page<Third> result = thirdPersistenceAdapter.getAllActiveThirdsByWithSort("ENT001", 0, 10, "names", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("test Cuenta terceros activos por empresa")
    void testCountActiveThirdsByEntIdSuccessfully() {
        // Arrange
        when(thirdRepository.countActiveByEntId("ENT001")).thenReturn(8L);

        // Act
        long result = thirdPersistenceAdapter.countActiveThirdsByEntId("ENT001");

        // Assert
        assertEquals(8L, result);
    }

    @Test
    @DisplayName("test Obtiene terceros por tipo de tercero específico")
    void testGetThirdsByEntIdAndThirdTypeNameSuccessfully() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ThirdEntity> pageEntities = new PageImpl<>(Arrays.asList(sampleThirdEntity));
        
        when(thirdRepository.findByEntIdAndThirdTypeName("ENT001", "Cliente", pageable)).thenReturn(pageEntities);
        when(thirdPersistenceMapper.toThird(any(ThirdEntity.class))).thenReturn(sampleThird);
        when(geographyLoaderService.loadCountryByCode("CO")).thenReturn(sampleCountry);
        when(geographyLoaderService.loadStateByCode("05", "CO")).thenReturn(sampleState);
        when(geographyLoaderService.loadCityByCode("05001", "05", "CO")).thenReturn(sampleCity);
        when(thirdsAndTypesRepository.findByThId(1L)).thenReturn(new ArrayList<>());

        // Act
        Page<Third> result = thirdPersistenceAdapter.getThirdsByEntIdAndThirdTypeName("ENT001", "Cliente", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("test Cuenta terceros por tipo de tercero específico")
    void testCountThirdsByEntIdAndThirdTypeNameSuccessfully() {
        // Arrange
        when(thirdRepository.countByEntIdAndThirdTypeName("ENT001", "Cliente")).thenReturn(15L);

        // Act
        long result = thirdPersistenceAdapter.countThirdsByEntIdAndThirdTypeName("ENT001", "Cliente");

        // Assert
        assertEquals(15L, result);
    }

    @Test
    @DisplayName("test Incrementa contador de uso exitosamente")
    void testIncrementUsageCountSuccessfully() {
        // Arrange
        when(thirdRepository.incrementUsageCountByThirdId(1L)).thenReturn(1);

        // Act
        boolean result = thirdPersistenceAdapter.incrementUsageCount(1L);

        // Assert
        assertTrue(result);
        verify(thirdRepository).incrementUsageCountByThirdId(1L);
    }

    @Test
    @DisplayName("test Retorna false al incrementar contador con ID null")
    void testIncrementUsageCountReturnsFalseWhenIdIsNull() {
        // Arrange & Act
        boolean result = thirdPersistenceAdapter.incrementUsageCount(null);

        // Assert
        assertFalse(result);
        verify(thirdRepository, never()).incrementUsageCountByThirdId(anyLong());
    }

    @Test
    @DisplayName("test Retorna false cuando no se incrementa el contador")
    void testIncrementUsageCountReturnsFalseWhenNotUpdated() {
        // Arrange
        when(thirdRepository.incrementUsageCountByThirdId(1L)).thenReturn(0);

        // Act
        boolean result = thirdPersistenceAdapter.incrementUsageCount(1L);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("test Obtiene terceros optimizados para exportación")
    void testGetAllThirdsForExportSuccessfully() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ThirdEntity> pageEntities = new PageImpl<>(Arrays.asList(sampleThirdEntity));
        
        when(thirdRepository.findAllForExport("ENT001", pageable)).thenReturn(pageEntities);
        when(thirdsAndTypesRepository.findByThIdInWithThirdType(any())).thenReturn(Arrays.asList(
                ThirdsAndTypesEntity.builder().thId(1L).ttId(1L).thirdType(sampleThirdTypeEntity).build()
        ));
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(Arrays.asList(sampleCountry));
        when(geographyOutputPort.getAllActiveStates()).thenReturn(Arrays.asList(sampleState));
        when(geographyOutputPort.getAllActiveCities()).thenReturn(Arrays.asList(sampleCity));
        when(thirdPersistenceMapper.toThird(any(ThirdEntity.class))).thenReturn(sampleThird);

        // Act
        Page<Third> result = thirdPersistenceAdapter.getAllThirdsForExport("ENT001", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(geographyOutputPort).getAllActiveCountries();
        verify(geographyOutputPort).getAllActiveStates();
        verify(geographyOutputPort).getAllActiveCities();
    }

    @Test
    @DisplayName("test Obtiene terceros por estado optimizados para exportación")
    void testGetAllThirdsByStateForExportSuccessfully() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ThirdEntity> pageEntities = new PageImpl<>(Arrays.asList(sampleThirdEntity));
        
        when(thirdRepository.findAllByStateForExport("ENT001", true, pageable)).thenReturn(pageEntities);
        when(thirdsAndTypesRepository.findByThIdInWithThirdType(any())).thenReturn(Arrays.asList(
                ThirdsAndTypesEntity.builder().thId(1L).ttId(1L).thirdType(sampleThirdTypeEntity).build()
        ));
        when(geographyOutputPort.getAllActiveCountries()).thenReturn(Arrays.asList(sampleCountry));
        when(geographyOutputPort.getAllActiveStates()).thenReturn(Arrays.asList(sampleState));
        when(geographyOutputPort.getAllActiveCities()).thenReturn(Arrays.asList(sampleCity));
        when(thirdPersistenceMapper.toThird(any(ThirdEntity.class))).thenReturn(sampleThird);

        // Act
        Page<Third> result = thirdPersistenceAdapter.getAllThirdsByStateForExport("ENT001", true, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(geographyOutputPort).getAllActiveCountries();
        verify(geographyOutputPort).getAllActiveStates();
        verify(geographyOutputPort).getAllActiveCities();
    }

    @Test
    @DisplayName("test Retorna página vacía en exportación cuando no hay terceros")
    void testGetAllThirdsForExportReturnsEmptyPageWhenNoThirds() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ThirdEntity> pageEntities = Page.empty();
        
        when(thirdRepository.findAllForExport("ENT001", pageable)).thenReturn(pageEntities);

        // Act
        Page<Third> result = thirdPersistenceAdapter.getAllThirdsForExport("ENT001", pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(geographyOutputPort, never()).getAllActiveCountries();
    }
}
