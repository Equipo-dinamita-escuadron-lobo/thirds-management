package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;

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
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdTypeCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdTypeUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.TypeIdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.TypeIdUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdTypeResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.IdRestMapper;
import com.thirdsmanagement.thirds.infrastructure.utils.PaginationHelper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la configuración de terceros.
 * Este controlador expone endpoints para la creación y listado de tipos de terceros y tipos de identificación.
 */
@RestController
@RequestMapping("/api/thirds/configuration")
@RequiredArgsConstructor
public class ThirdConfigurationRestAdapter {

    private final CreateThirdTypeUseCase createThirdTypeUseCase;
    private final ListThirdTypeUseCase listThirdTypeUseCase;
    private final UpdateThirdTypeUseCase updateThirdTypeUseCase;
    private final DeleteThirdTypeUseCase deleteThirdTypeUseCase;
    private final CreateTypeIdUseCase createTypeIdUseCase;
    private final ListTypeIdUseCase listTypeIdUseCase;
    private final UpdateTypeIdUseCase updateTypeIdUseCase;
    private final DeleteTypeIdUseCase deleteTypeIdUseCase;

    private final IdRestMapper idRestMapper;

    @PostMapping("/thirdtype")
    public ResponseEntity<ThirdTypeResponse> createThirdType(
            @RequestBody @Valid ThirdTypeCreateRequest thirdTypeCreateRequest) {

        ThirdType thirdType = idRestMapper.toThirdType(thirdTypeCreateRequest);
        ThirdType createdThirdType = createThirdTypeUseCase.createThirdType(thirdType);

        return new ResponseEntity<>(idRestMapper.toThirdTypeResponse(createdThirdType), HttpStatus.CREATED);
    }

    /**
     * Obtiene una lista de tipos de tercero con paginación flexible, búsqueda y ordenamiento.
     * Si no se especifican parámetros de paginación, retorna todos los tipos de tercero.
     * Solo se ordena por nombre (ttName).
     * 
     * @param entId Id de la empresa
     * @param numPage Número de página (opcional)
     * @param size Tamaño de página (opcional)
     * @param sortOrder Orden asc/desc (opcional, default: "asc")
     * @param search Término de búsqueda (opcional)
     * @return Respuesta con la lista de tipos de tercero
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

    @PostMapping("/thirdtype/update")
    public ResponseEntity<ThirdTypeResponse> updateThirdType(@RequestBody @Valid ThirdTypeUpdateRequest thirdTypeUpdateRequest) {
        ThirdType thirdType = idRestMapper.toThirdType(thirdTypeUpdateRequest);
        ThirdType updatedThirdType = updateThirdTypeUseCase.updateThirdType(thirdType);

        return new ResponseEntity<>(idRestMapper.toThirdTypeResponse(updatedThirdType), HttpStatus.OK);
    }

    @PostMapping("/typeid")
    public ResponseEntity<TypeId> createTypeId(@RequestBody @Valid TypeIdCreateRequest typeIdCreateRequest) {
        TypeId typeId = idRestMapper.toTypeId(typeIdCreateRequest);
        typeId = createTypeIdUseCase.createTypeId(typeId);

        return new ResponseEntity<>(typeId, HttpStatus.CREATED);
    }

    /**
     * Obtiene una lista de tipos de identificación con paginación flexible, búsqueda y ordenamiento.
     * Si no se especifican parámetros de paginación, retorna todos los tipos de identificación.
     * 
     * @param entId Id de la empresa
     * @param numPage Número de página (opcional)
     * @param size Tamaño de página (opcional)
     * @param sortField Campo de ordenamiento (opcional, default: "tiName")
     * @param sortOrder Orden asc/desc (opcional, default: "asc")
     * @param search Término de búsqueda (opcional)
     * @return Respuesta con la lista de tipos de identificación
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

    @PostMapping("/typeid/update")
    public ResponseEntity<TypeId> updateTypeId(@RequestBody @Valid TypeIdUpdateRequest typeIdUpdateRequest) {
        TypeId typeId = idRestMapper.toTypeId(typeIdUpdateRequest);
        typeId = updateTypeIdUseCase.updateTypeId(typeId);

        return new ResponseEntity<>(typeId, HttpStatus.OK);
    }

    @DeleteMapping("/thirdtype/delete")
    public ResponseEntity<Boolean> deleteThirdType(
            @NotNull(message = "Third type ID not be empty") @RequestParam("thirdTypeId") Long thirdTypeId,
            @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId) {
        
        boolean deleted = deleteThirdTypeUseCase.deleteThirdType(thirdTypeId, entId);
        
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    @DeleteMapping("/typeid/delete")
    public ResponseEntity<Boolean> deleteTypeId(
            @NotNull(message = "Type ID not be empty") @RequestParam("typeIdId") Long typeIdId,
            @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId) {
        
        boolean deleted = deleteTypeIdUseCase.deleteTypeId(typeIdId, entId);
        
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    /**
     * Obtiene una lista de tipos de identificación activos con paginación simple.
     * Si no se especifican parámetros de paginación, retorna todos los tipos de identificación activos.
     * Los resultados se ordenan por nombre de forma ascendente.
     * 
     * @param entId Id de la empresa
     * @param numPage Número de página (opcional)
     * @param size Tamaño de página (opcional)
     * @return Respuesta con la lista de tipos de identificación activos
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
     * Obtiene una lista de tipos de tercero activos con paginación simple.
     * Si no se especifican parámetros de paginación, retorna todos los tipos de tercero activos.
     * Los resultados se ordenan por nombre de forma ascendente.
     * 
     * @param entId Id de la empresa
     * @param numPage Número de página (opcional)
     * @param size Tamaño de página (opcional)
     * @return Respuesta con la lista de tipos de tercero activos
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
