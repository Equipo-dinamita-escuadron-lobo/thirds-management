package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.input.CreateTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.DeleteThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ListThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ListTypeIdUseCase;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdTypeCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.TypeIdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.IdRestMapper;


import jakarta.persistence.EntityNotFoundException;
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
    private final CreateTypeIdUseCase createTypeIdUseCase;
    private final ListTypeIdUseCase listTypeIdUseCase;
    private final DeleteThirdTypeUseCase deleteThirdTypeUseCase;

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

    /**
     * Elimina un tipo de tercero.
     * @param entId Id del tipo de tercero a eliminar.
     * @return Mensaje de éxito o error.
     * @throws EntityNotFoundException Si el tipo de tercero no existe.
     */
    @DeleteMapping("/{entId}")
    public ResponseEntity<String> deleteThird(
            @NotNull(message = "Enterprise ID must not be empty") @PathVariable Long entId) {

        try {
            ResponseEntity<String> response = deleteThirdTypeUseCase.deleteThirdTypeUseCase(entId);

            // Evaluamos el resultado que retorna tu método
            if (response.getBody().equals("success")) {
                String successMessage = "SUCCESS";
                return ResponseEntity.ok(successMessage);
            } else {
                String errorMessage = "ERROR";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

        } catch (EntityNotFoundException e) {
            String notFoundMessage = "El tipo de tercero con ID: " + entId + " no fue encontrado.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundMessage);

        }

    }
}
