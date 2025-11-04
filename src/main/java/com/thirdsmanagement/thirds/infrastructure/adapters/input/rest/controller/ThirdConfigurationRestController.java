package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.input.CreateTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.DeleteThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.input.DeleteTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ListThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ListTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.UpdateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.input.UpdateTypeIdUseCase;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdTypeCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdTypeUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.TypeIdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.TypeIdUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ThirdTypeResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.IdRestMapper;
import com.thirdsmanagement.thirds.infrastructure.utils.PaginationHelper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

/**
 * @brief Controlador REST para configuración de maestros de terceros
 *
 * Adaptador de entrada que expone endpoints para gestionar los datos maestros
 * del sistema de terceros: tipos de tercero y tipos de identificación.
 * Traduce entre el protocolo HTTP y los casos de uso del dominio.
 */
@RestController
@RequestMapping("/api/thirds/configuration")
@RequiredArgsConstructor
public class ThirdConfigurationRestController {

    private final CreateThirdTypeUseCase createThirdTypeUseCase;
    private final ListThirdTypeUseCase listThirdTypeUseCase;
    private final UpdateThirdTypeUseCase updateThirdTypeUseCase;
    private final DeleteThirdTypeUseCase deleteThirdTypeUseCase;
    private final CreateTypeIdUseCase createTypeIdUseCase;
    private final ListTypeIdUseCase listTypeIdUseCase;
    private final UpdateTypeIdUseCase updateTypeIdUseCase;
    private final DeleteTypeIdUseCase deleteTypeIdUseCase;

    private final IdRestMapper idRestMapper;

    /**
     * @brief Crea un nuevo tipo de tercero
     *
     * Endpoint para crear un nuevo tipo de tercero en el sistema.
     * @param thirdTypeCreateRequest datos del tipo de tercero a crear
     * @return tipo de tercero creado con código HTTP 201
     */
    @PostMapping("/thirdtype")
    public ResponseEntity<ThirdTypeResponse> createThirdType(
            @RequestBody @Valid ThirdTypeCreateRequest thirdTypeCreateRequest) {

        ThirdType thirdType = idRestMapper.toThirdType(thirdTypeCreateRequest);
        ThirdType createdThirdType = createThirdTypeUseCase.createThirdType(thirdType);

        return new ResponseEntity<>(idRestMapper.toThirdTypeResponse(createdThirdType), HttpStatus.CREATED);
    }

    /**
     * @brief Obtiene lista paginada de tipos de tercero con búsqueda
     *
     * Endpoint que retorna tipos de tercero con paginación flexible, búsqueda por nombre
     * y ordenamiento. Si no se especifican parámetros de paginación, retorna todos los registros.
     * @param entId identificador de la empresa
     * @param numPage número de página (opcional)
     * @param size tamaño de página (opcional)
     * @param sortOrder orden ascendente/descendente (default: "asc")
     * @param search término de búsqueda por nombre (opcional)
     * @return página de tipos de tercero con paginación
     */
    @GetMapping("/thirdtype")
    public ResponseEntity<Page<ThirdType>> getThirdType(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @RequestParam(required = false) Optional<Integer> numPage,
            @RequestParam(required = false) Optional<Integer> size,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String search) {

        // Contar total de registros (con o sin filtro)
        long totalRecords = (search != null && !search.trim().isEmpty())
            ? listThirdTypeUseCase.countThirdTypesByEntIdAndSearch(entId, search)
            : listThirdTypeUseCase.countThirdTypesByEntId(entId);

        // Crear Pageable flexible
        Pageable pageable = PaginationHelper.createFlexiblePageable(numPage, size, totalRecords);

        // Obtener página de datos (con o sin filtro) - siempre ordenado por ttName
        Page<ThirdType> page = (search != null && !search.trim().isEmpty())
            ? listThirdTypeUseCase.findThirdTypesByEntIdAndSearch(entId, search, pageable.getPageNumber(),
                    pageable.getPageSize(), "ttName", sortOrder)
            : listThirdTypeUseCase.getAllThirdTypesWithSort(entId, pageable.getPageNumber(),
                    pageable.getPageSize(), "ttName", sortOrder);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    /**
     * @brief Actualiza un tipo de tercero existente
     *
     * Endpoint para modificar los datos de un tipo de tercero existente.
     * @param thirdTypeUpdateRequest datos actualizados del tipo de tercero
     * @return tipo de tercero actualizado
     */
    @PostMapping("/thirdtype/update")
    public ResponseEntity<ThirdTypeResponse> updateThirdType(@RequestBody @Valid ThirdTypeUpdateRequest thirdTypeUpdateRequest) {
        ThirdType thirdType = idRestMapper.toThirdType(thirdTypeUpdateRequest);
        ThirdType updatedThirdType = updateThirdTypeUseCase.updateThirdType(thirdType);

        return new ResponseEntity<>(idRestMapper.toThirdTypeResponse(updatedThirdType), HttpStatus.OK);
    }

    /**
     * @brief Crea un nuevo tipo de identificación
     *
     * Endpoint para crear un nuevo tipo de identificación en el sistema.
     * @param typeIdCreateRequest datos del tipo de identificación a crear
     * @return tipo de identificación creado con código HTTP 201
     */
    @PostMapping("/typeid")
    public ResponseEntity<TypeId> createTypeId(@RequestBody @Valid TypeIdCreateRequest typeIdCreateRequest) {
        TypeId typeId = idRestMapper.toTypeId(typeIdCreateRequest);
        typeId = createTypeIdUseCase.createTypeId(typeId);

        return new ResponseEntity<>(typeId, HttpStatus.CREATED);
    }

    /**
     * @brief Obtiene lista paginada de tipos de identificación con búsqueda
     *
     * Endpoint que retorna tipos de identificación con paginación flexible, búsqueda,
     * ordenamiento por cualquier campo y filtrado por empresa.
     * @param entId identificador de la empresa
     * @param numPage número de página (opcional)
     * @param size tamaño de página (opcional)
     * @param sortField campo para ordenamiento (default: "tiName")
     * @param sortOrder orden ascendente/descendente (default: "asc")
     * @param search término de búsqueda (opcional)
     * @return página de tipos de identificación con paginación
     */
    @GetMapping("/typeid")
    public ResponseEntity<Page<TypeId>> ListTypeId(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @RequestParam(required = false) Optional<Integer> numPage,
            @RequestParam(required = false) Optional<Integer> size,
            @RequestParam(defaultValue = "tiName") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String search) {

        // Contar total de registros (con o sin filtro)
        long totalRecords = (search != null && !search.trim().isEmpty())
            ? listTypeIdUseCase.countByEntIdAndSearch(entId, search)
            : listTypeIdUseCase.countByEntId(entId);

        // Crear Pageable flexible
        Pageable pageable = PaginationHelper.createFlexiblePageable(numPage, size, totalRecords);

        // Obtener página de datos (con o sin filtro)
        Page<TypeId> page = (search != null && !search.trim().isEmpty())
            ? listTypeIdUseCase.findByEntIdAndSearch(entId, search, pageable.getPageNumber(),
                    pageable.getPageSize(), sortField, sortOrder)
            : listTypeIdUseCase.getAllTypeIdsWithSort(entId, pageable.getPageNumber(),
                    pageable.getPageSize(), sortField, sortOrder);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    /**
     * @brief Actualiza un tipo de identificación existente
     *
     * Endpoint para modificar los datos de un tipo de identificación existente.
     * @param typeIdUpdateRequest datos actualizados del tipo de identificación
     * @return tipo de identificación actualizado
     */
    @PostMapping("/typeid/update")
    public ResponseEntity<TypeId> updateTypeId(@RequestBody @Valid TypeIdUpdateRequest typeIdUpdateRequest) {
        TypeId typeId = idRestMapper.toTypeId(typeIdUpdateRequest);
        typeId = updateTypeIdUseCase.updateTypeId(typeId);

        return new ResponseEntity<>(typeId, HttpStatus.OK);
    }

    /**
     * @brief Elimina un tipo de tercero
     *
     * Endpoint para eliminar lógicamente un tipo de tercero del sistema.
     * @param thirdTypeId identificador del tipo de tercero a eliminar
     * @param entId identificador de la empresa
     * @return resultado de la operación de eliminación
     */
    @DeleteMapping("/thirdtype/delete")
    public ResponseEntity<Boolean> deleteThirdType(
            @NotNull(message = "Third type ID not be empty") @RequestParam("thirdTypeId") Long thirdTypeId,
            @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId) {

        boolean deleted = deleteThirdTypeUseCase.deleteThirdType(thirdTypeId, entId);

        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    /**
     * @brief Elimina un tipo de identificación
     *
     * Endpoint para eliminar lógicamente un tipo de identificación del sistema.
     * @param typeIdId identificador del tipo de identificación a eliminar
     * @param entId identificador de la empresa
     * @return resultado de la operación de eliminación
     */
    @DeleteMapping("/typeid/delete")
    public ResponseEntity<Boolean> deleteTypeId(
            @NotNull(message = "Type ID not be empty") @RequestParam("typeIdId") Long typeIdId,
            @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId) {

        boolean deleted = deleteTypeIdUseCase.deleteTypeId(typeIdId, entId);

        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    /**
     * @brief Obtiene lista paginada de tipos de identificación activos
     *
     * Endpoint que retorna únicamente los tipos de identificación marcados como activos
     * para una empresa específica, ordenados por nombre ascendente.
     * @param entId identificador de la empresa
     * @param numPage número de página (opcional)
     * @param size tamaño de página (opcional)
     * @return página de tipos de identificación activos
     */
    @GetMapping("/typeid-active")
    public ResponseEntity<Page<TypeId>> getActiveTypeId(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @RequestParam(required = false) Optional<Integer> numPage,
            @RequestParam(required = false) Optional<Integer> size) {

        // Contar total de registros activos
        long totalRecords = listTypeIdUseCase.countActiveByEntId(entId);

        // Crear Pageable flexible
        Pageable pageable = PaginationHelper.createFlexiblePageable(numPage, size, totalRecords);

        // Obtener página de datos activos ordenados por defecto (tiName asc)
        Page<TypeId> page = listTypeIdUseCase.getAllActiveTypeIds(entId, pageable.getPageNumber(),
                pageable.getPageSize());

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    /**
     * @brief Obtiene lista paginada de tipos de tercero activos
     *
     * Endpoint que retorna únicamente los tipos de tercero marcados como activos
     * para una empresa específica, ordenados por nombre ascendente.
     * @param entId identificador de la empresa
     * @param numPage número de página (opcional)
     * @param size tamaño de página (opcional)
     * @return página de tipos de tercero activos
     */
    @GetMapping("/thirdtype-active")
    public ResponseEntity<Page<ThirdType>> getActiveThirdType(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @RequestParam(required = false) Optional<Integer> numPage,
            @RequestParam(required = false) Optional<Integer> size) {

        // Contar total de registros activos
        long totalRecords = listThirdTypeUseCase.countActiveThirdTypesByEntId(entId);

        // Crear Pageable flexible
        Pageable pageable = PaginationHelper.createFlexiblePageable(numPage, size, totalRecords);

        // Obtener página de datos activos ordenados por defecto (ttName asc)
        Page<ThirdType> page = listThirdTypeUseCase.getAllActiveThirdTypes(entId, pageable.getPageNumber(),
                pageable.getPageSize());

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

}
