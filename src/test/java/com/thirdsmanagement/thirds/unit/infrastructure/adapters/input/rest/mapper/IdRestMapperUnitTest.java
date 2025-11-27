package com.thirdsmanagement.thirds.unit.infrastructure.adapters.input.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.thirdsmanagement.thirds.domain.model.PersonClassification;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdTypeCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdTypeUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.TypeIdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.TypeIdUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ThirdTypeResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.IdRestMapper;

@SpringBootTest
class IdRestMapperUnitTest {

    @Autowired
    private IdRestMapper mapper;

    private String entId;

    @BeforeEach
    void setUp() {
        entId = "ENT001";
    }

    // ==================== toThirdTypeCreateRequest ====================

    @Test
    @DisplayName("Debe mapear ThirdType a ThirdTypeCreateRequest correctamente")
    void testToThirdTypeCreateRequest() {
        // Arrange
        ThirdType thirdType = ThirdType.builder()
                .thirdTypeId(1L)
                .entId(entId)
                .thirdTypeName("Cliente")
                .status(true)
                .build();

        // Act
        ThirdTypeCreateRequest result = mapper.toThirdTypeCreateRequest(thirdType);

        // Assert
        assertNotNull(result);
        assertEquals(entId, result.getEntId());
        assertEquals("Cliente", result.getThirdTypeName());
        assertTrue(result.getStatus());
    }

    // ==================== toThirdType (ThirdTypeCreateRequest) ====================

    @Test
    @DisplayName("Debe mapear ThirdTypeCreateRequest a ThirdType con status por defecto")
    void testToThirdTypeFromCreateRequest() {
        // Arrange
        ThirdTypeCreateRequest request = ThirdTypeCreateRequest.builder()
                .entId(entId)
                .thirdTypeName("Proveedor")
                .status(null)
                .build();

        // Act
        ThirdType result = mapper.toThirdType(request);

        // Assert
        assertNotNull(result);
        assertEquals(entId, result.getEntId());
        assertEquals("Proveedor", result.getThirdTypeName());
        assertTrue(result.getStatus());
    }

    @Test
    @DisplayName("Debe respetar status explícito en ThirdTypeCreateRequest")
    void testToThirdTypeFromCreateRequestExplicitStatus() {
        // Arrange
        ThirdTypeCreateRequest request = ThirdTypeCreateRequest.builder()
                .entId(entId)
                .thirdTypeName("Cliente")
                .status(false)
                .build();

        // Act
        ThirdType result = mapper.toThirdType(request);

        // Assert
        assertNotNull(result);
        assertFalse(result.getStatus());
    }

    // ==================== toTypeIdCreateRequest ====================

    @Test
    @DisplayName("Debe mapear TypeId a TypeIdCreateRequest correctamente")
    void testToTypeIdCreateRequest() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .id(1L)
                .entId(entId)
                .typeId("CC")
                .typeIdname("Cédula de Ciudadanía")
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        // Act
        TypeIdCreateRequest result = mapper.toTypeIdCreateRequest(typeId);

        // Assert
        assertNotNull(result);
        assertEquals(entId, result.getEntId());
        assertEquals("CC", result.getTypeId());
        assertEquals("Cédula de Ciudadanía", result.getTypeIdname());
        assertEquals(PersonClassification.NATURAL_PERSON, result.getClassification());
    }

    // ==================== toTypeId (TypeIdCreateRequest) ====================

    @Test
    @DisplayName("Debe mapear TypeIdCreateRequest a TypeId ignorando id")
    void testToTypeIdFromCreateRequestIgnoresId() {
        // Arrange
        TypeIdCreateRequest request = TypeIdCreateRequest.builder()
                .entId(entId)
                .typeId("NIT")
                .typeIdname("Número de Identificación Tributaria")
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        // Act
        TypeId result = mapper.toTypeId(request);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(entId, result.getEntId());
        assertEquals("NIT", result.getTypeId());
        assertEquals("Número de Identificación Tributaria", result.getTypeIdname());
        assertEquals(PersonClassification.LEGAL_ENTITY, result.getClassification());
        assertTrue(result.getStatus());
    }

    @Test
    @DisplayName("Debe mapear todos los campos de TypeIdCreateRequest correctamente")
    void testToTypeIdFromCreateRequestAllFields() {
        // Arrange
        TypeIdCreateRequest request = TypeIdCreateRequest.builder()
                .entId(entId)
                .typeId("CE")
                .typeIdname("Cédula de Extranjería")
                .classification(PersonClassification.NATURAL_PERSON)
                .status(false)
                .build();

        // Act
        TypeId result = mapper.toTypeId(request);

        // Assert
        assertNotNull(result);
        assertEquals("CE", result.getTypeId());
        assertEquals("Cédula de Extranjería", result.getTypeIdname());
        assertEquals(PersonClassification.NATURAL_PERSON, result.getClassification());
        assertFalse(result.getStatus());
    }

    // ==================== toTypeId (TypeIdUpdateRequest) ====================

    @Test
    @DisplayName("Debe mapear TypeIdUpdateRequest a TypeId incluyendo id")
    void testToTypeIdFromUpdateRequestIncludesId() {
        // Arrange
        TypeIdUpdateRequest request = TypeIdUpdateRequest.builder()
                .id(5L)
                .entId(entId)
                .typeId("PP")
                .typeIdname("Pasaporte")
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        // Act
        TypeId result = mapper.toTypeId(request);

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals(entId, result.getEntId());
        assertEquals("PP", result.getTypeId());
        assertEquals("Pasaporte", result.getTypeIdname());
        assertEquals(PersonClassification.NATURAL_PERSON, result.getClassification());
        assertTrue(result.getStatus());
    }

    @Test
    @DisplayName("Debe mapear TypeIdUpdateRequest con status false")
    void testToTypeIdFromUpdateRequestWithStatusFalse() {
        // Arrange
        TypeIdUpdateRequest request = TypeIdUpdateRequest.builder()
                .id(3L)
                .entId(entId)
                .typeId("TI")
                .typeIdname("Tarjeta de Identidad")
                .classification(PersonClassification.NATURAL_PERSON)
                .status(false)
                .build();

        // Act
        TypeId result = mapper.toTypeId(request);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertFalse(result.getStatus());
    }

    @Test
    @DisplayName("Debe mapear TypeIdUpdateRequest para persona jurídica")
    void testToTypeIdFromUpdateRequestLegalEntity() {
        // Arrange
        TypeIdUpdateRequest request = TypeIdUpdateRequest.builder()
                .id(10L)
                .entId(entId)
                .typeId("NIT")
                .typeIdname("NIT Empresarial")
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        // Act
        TypeId result = mapper.toTypeId(request);

        // Assert
        assertNotNull(result);
        assertEquals(PersonClassification.LEGAL_ENTITY, result.getClassification());
    }

    // ==================== toThirdType (ThirdTypeUpdateRequest) ====================

    @Test
    @DisplayName("Debe mapear ThirdTypeUpdateRequest a ThirdType correctamente")
    void testToThirdTypeFromUpdateRequest() {
        // Arrange
        ThirdTypeUpdateRequest request = ThirdTypeUpdateRequest.builder()
                .thirdTypeId(7L)
                .entId(entId)
                .thirdTypeName("Empleado")
                .status(true)
                .build();

        // Act
        ThirdType result = mapper.toThirdType(request);

        // Assert
        assertNotNull(result);
        assertEquals(7L, result.getThirdTypeId());
        assertEquals(entId, result.getEntId());
        assertEquals("Empleado", result.getThirdTypeName());
        assertTrue(result.getStatus());
    }

    @Test
    @DisplayName("Debe mapear ThirdTypeUpdateRequest con status false")
    void testToThirdTypeFromUpdateRequestInactive() {
        // Arrange
        ThirdTypeUpdateRequest request = ThirdTypeUpdateRequest.builder()
                .thirdTypeId(2L)
                .entId(entId)
                .thirdTypeName("Proveedor Inactivo")
                .status(false)
                .build();

        // Act
        ThirdType result = mapper.toThirdType(request);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getThirdTypeId());
        assertFalse(result.getStatus());
    }

    // ==================== toThirdTypeResponse ====================

    @Test
    @DisplayName("Debe mapear ThirdType a ThirdTypeResponse correctamente")
    void testToThirdTypeResponse() {
        // Arrange
        ThirdType thirdType = ThirdType.builder()
                .thirdTypeId(15L)
                .entId(entId)
                .thirdTypeName("Cliente VIP")
                .status(true)
                .build();

        // Act
        ThirdTypeResponse result = mapper.toThirdTypeResponse(thirdType);

        // Assert
        assertNotNull(result);
        assertEquals(15L, result.getThirdTypeId());
        assertEquals("Cliente VIP", result.getThirdTypeName());
        assertTrue(result.getStatus());
    }

    @Test
    @DisplayName("Debe mapear ThirdType inactivo a ThirdTypeResponse")
    void testToThirdTypeResponseInactive() {
        // Arrange
        ThirdType thirdType = ThirdType.builder()
                .thirdTypeId(20L)
                .entId(entId)
                .thirdTypeName("Suspendido")
                .status(false)
                .build();

        // Act
        ThirdTypeResponse result = mapper.toThirdTypeResponse(thirdType);

        // Assert
        assertNotNull(result);
        assertFalse(result.getStatus());
    }

    // ==================== toThirdTypeResponseList ====================

    @Test
    @DisplayName("Debe mapear lista de ThirdType a lista de ThirdTypeResponse")
    void testToThirdTypeResponseList() {
        // Arrange
        ThirdType thirdType1 = ThirdType.builder()
                .thirdTypeId(1L)
                .entId(entId)
                .thirdTypeName("Cliente")
                .status(true)
                .build();

        ThirdType thirdType2 = ThirdType.builder()
                .thirdTypeId(2L)
                .entId(entId)
                .thirdTypeName("Proveedor")
                .status(true)
                .build();

        ThirdType thirdType3 = ThirdType.builder()
                .thirdTypeId(3L)
                .entId(entId)
                .thirdTypeName("Empleado")
                .status(false)
                .build();

        List<ThirdType> thirdTypes = Arrays.asList(thirdType1, thirdType2, thirdType3);

        // Act
        List<ThirdTypeResponse> result = mapper.toThirdTypeResponseList(thirdTypes);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Cliente", result.get(0).getThirdTypeName());
        assertEquals("Proveedor", result.get(1).getThirdTypeName());
        assertEquals("Empleado", result.get(2).getThirdTypeName());
        assertTrue(result.get(0).getStatus());
        assertFalse(result.get(2).getStatus());
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando input es vacío")
    void testToThirdTypeResponseListEmpty() {
        // Arrange
        List<ThirdType> emptyList = Arrays.asList();

        // Act
        List<ThirdTypeResponse> result = mapper.toThirdTypeResponseList(emptyList);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== Casos de integración ====================

    @Test
    @DisplayName("Debe mapear correctamente ciclo completo de TypeId create")
    void testTypeIdCreateCycle() {
        // Arrange
        TypeIdCreateRequest request = TypeIdCreateRequest.builder()
                .entId(entId)
                .typeId("CC")
                .typeIdname("Cédula")
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        // Act
        TypeId typeId = mapper.toTypeId(request);
        TypeIdCreateRequest backToRequest = mapper.toTypeIdCreateRequest(typeId);

        // Assert
        assertNull(typeId.getId());
        assertEquals(request.getTypeId(), backToRequest.getTypeId());
        assertEquals(request.getTypeIdname(), backToRequest.getTypeIdname());
        assertEquals(request.getClassification(), backToRequest.getClassification());
    }

    @Test
    @DisplayName("Debe mapear correctamente ciclo completo de TypeId update")
    void testTypeIdUpdateCycle() {
        // Arrange
        TypeIdUpdateRequest request = TypeIdUpdateRequest.builder()
                .id(99L)
                .entId(entId)
                .typeId("NIT")
                .typeIdname("Número Tributario")
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        // Act
        TypeId typeId = mapper.toTypeId(request);

        // Assert
        assertEquals(99L, typeId.getId());
        assertEquals("NIT", typeId.getTypeId());
        assertEquals("Número Tributario", typeId.getTypeIdname());
        assertEquals(PersonClassification.LEGAL_ENTITY, typeId.getClassification());
        assertTrue(typeId.getStatus());
    }

    @Test
    @DisplayName("Debe mapear correctamente ciclo completo de ThirdType")
    void testThirdTypeCreateCycle() {
        // Arrange
        ThirdTypeCreateRequest request = ThirdTypeCreateRequest.builder()
                .entId(entId)
                .thirdTypeName("Socio")
                .status(true)
                .build();

        // Act
        ThirdType thirdType = mapper.toThirdType(request);
        ThirdTypeResponse response = mapper.toThirdTypeResponse(thirdType);

        // Assert
        assertEquals(request.getThirdTypeName(), thirdType.getThirdTypeName());
        assertEquals(thirdType.getThirdTypeName(), response.getThirdTypeName());
        assertEquals(request.getStatus(), response.getStatus());
    }

    @Test
    @DisplayName("Debe preservar clasificación en mapeos de TypeId")
    void testPreservesClassificationInMappings() {
        // Arrange - Natural Person
        TypeIdCreateRequest naturalRequest = TypeIdCreateRequest.builder()
                .entId(entId)
                .typeId("CC")
                .typeIdname("Cédula")
                .classification(PersonClassification.NATURAL_PERSON)
                .status(true)
                .build();

        // Arrange - Legal Entity
        TypeIdCreateRequest legalRequest = TypeIdCreateRequest.builder()
                .entId(entId)
                .typeId("NIT")
                .typeIdname("NIT")
                .classification(PersonClassification.LEGAL_ENTITY)
                .status(true)
                .build();

        // Act
        TypeId naturalResult = mapper.toTypeId(naturalRequest);
        TypeId legalResult = mapper.toTypeId(legalRequest);

        // Assert
        assertEquals(PersonClassification.NATURAL_PERSON, naturalResult.getClassification());
        assertEquals(PersonClassification.LEGAL_ENTITY, legalResult.getClassification());
    }

    @Test
    @DisplayName("Debe manejar caracteres especiales en nombres")
    void testHandlesSpecialCharactersInNames() {
        // Arrange
        ThirdTypeCreateRequest request = ThirdTypeCreateRequest.builder()
                .entId(entId)
                .thirdTypeName("Cliente & Proveedor - VIP (Nivel 1)")
                .status(true)
                .build();

        // Act
        ThirdType result = mapper.toThirdType(request);
        ThirdTypeResponse response = mapper.toThirdTypeResponse(result);

        // Assert
        assertEquals("Cliente & Proveedor - VIP (Nivel 1)", result.getThirdTypeName());
        assertEquals("Cliente & Proveedor - VIP (Nivel 1)", response.getThirdTypeName());
    }

    @Test
    @DisplayName("Debe mapear correctamente múltiples ThirdTypes con diferentes estados")
    void testMapsMultipleThirdTypesWithDifferentStates() {
        // Arrange
        ThirdType active = ThirdType.builder()
                .thirdTypeId(1L)
                .thirdTypeName("Activo")
                .status(true)
                .build();

        ThirdType inactive = ThirdType.builder()
                .thirdTypeId(2L)
                .thirdTypeName("Inactivo")
                .status(false)
                .build();

        List<ThirdType> list = Arrays.asList(active, inactive);

        // Act
        List<ThirdTypeResponse> results = mapper.toThirdTypeResponseList(list);

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.get(0).getStatus());
        assertFalse(results.get(1).getStatus());
    }
}
