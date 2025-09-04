package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.input.CreateTypeIdUseCase;
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
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.IdRestMapper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import java.util.List;

/**
 * Controlador REST para la configuración de terceros.
 * Este controlador expone endpoints para la creación y listado de tipos de terceros y tipos de identificación.
 */
@RestController
@RequestMapping("/api/thirds/configuration")
@RequiredArgsConstructor
public class ThirdConfigurationAdapter {

    private final CreateThirdTypeUseCase createThirdTypeUseCase;
    private final ListThirdTypeUseCase listThirdTypeUseCase;
    private final UpdateThirdTypeUseCase updateThirdTypeUseCase;
    private final CreateTypeIdUseCase createTypeIdUseCase;
    private final ListTypeIdUseCase listTypeIdUseCase;
    private final UpdateTypeIdUseCase updateTypeIdUseCase;

    private final IdRestMapper idRestMapper;

    @PostMapping("/thirdtype")
    public ResponseEntity<ThirdType> createThirdType(
            @RequestBody @Valid ThirdTypeCreateRequest thirdTypeCreateRequest) {

        ThirdType thirdType = idRestMapper.toThirdType(thirdTypeCreateRequest);
        thirdType = createThirdTypeUseCase.createThirdType(thirdType);

        return new ResponseEntity<>(thirdType, HttpStatus.CREATED);
    }

    @GetMapping("/thirdtype")
    public ResponseEntity<List<ThirdType>> getThirdType(
            @NotNull(message = "Third Id not be empty") @RequestParam("entId") String entId) {

        List<ThirdType> thirdTypes = listThirdTypeUseCase.getAllThirdTypes(entId);

        return new ResponseEntity<>(thirdTypes, HttpStatus.OK);

    }

    @PostMapping("/thirdtype/update")
    public ResponseEntity<ThirdType> updateThirdType(@RequestBody @Valid ThirdTypeUpdateRequest thirdTypeUpdateRequest) {
        ThirdType thirdType = idRestMapper.toThirdType(thirdTypeUpdateRequest);
        thirdType = updateThirdTypeUseCase.updateThirdType(thirdType);

        return new ResponseEntity<>(thirdType, HttpStatus.OK);
    }

    @PostMapping("/typeid")
    public ResponseEntity<TypeId> createTypeId(@RequestBody @Valid TypeIdCreateRequest typeIdCreateRequest) {
        TypeId typeId = idRestMapper.toTypeId(typeIdCreateRequest);
        typeId = createTypeIdUseCase.createTypeId(typeId);

        return new ResponseEntity<>(typeId, HttpStatus.CREATED);
    }

    @GetMapping("/typeid")
    public ResponseEntity<List<TypeId>> ListTypeId(
            @NotNull(message = "Third Id not be empty") @RequestParam("entId") String entId) {
        List<TypeId> typeIds = listTypeIdUseCase.getAllTypeId(entId);
        return new ResponseEntity<>(typeIds, HttpStatus.OK);
    }

    @PostMapping("/typeid/update")
    public ResponseEntity<TypeId> updateTypeId(@RequestBody @Valid TypeIdUpdateRequest typeIdUpdateRequest) {
        TypeId typeId = idRestMapper.toTypeId(typeIdUpdateRequest);
        typeId = updateTypeIdUseCase.updateTypeId(typeId);

        return new ResponseEntity<>(typeId, HttpStatus.OK);
    }

}
