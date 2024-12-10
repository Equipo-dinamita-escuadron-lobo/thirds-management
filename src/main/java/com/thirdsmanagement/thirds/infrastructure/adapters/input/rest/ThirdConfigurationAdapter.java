package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.thirdsmanagement.thirds.application.service.ListThirdTypeService;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdTypeCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.TypeIdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.IdRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import java.util.List;

/**
 * Controlador REST para la configuración de terceros.
 * Este controlador expone endpoints para la creación y listado de tipos de terceros y tipos de identificación.
 */
@CrossOrigin("*")
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

    @Operation(summary = "Crear un Tipo De Tercero", description = "Crea un Tipo de Tercero con los parámetros obligatorios", responses = {
            @ApiResponse(responseCode = "201", description = "Tipo de tercero creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThirdType.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta debido a datos de entrada inválidos", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/thirdtype")
    public ResponseEntity<ThirdType> createThirdType(
            @RequestBody @Valid ThirdTypeCreateRequest thirdTypeCreateRequest) {

        ThirdType thirdType = idRestMapper.toThirdType(thirdTypeCreateRequest);
        thirdType = createThirdTypeUseCase.createThirdType(thirdType);

        return new ResponseEntity<>(thirdType, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtiene los Tipos De Terceros", description = "Devuelve los Tipos de Terceros asociados a una Empresa", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de Tipos de Terceros obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ThirdType.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta debido a un identificador de empresa inválido o vacío", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/thirdtype")
    public ResponseEntity<List<ThirdType>> getThirdType(
            @NotNull(message = "Third Id not be empty") @RequestParam("entId") String entId) {

        List<ThirdType> thirdTypes = listThirdTypeUseCase.getAllThirdTypes(entId);

        return new ResponseEntity<>(thirdTypes, HttpStatus.OK);

    }

    @Operation(summary = "Crear un Tipo De Identificación", description = "Crea un Tipo de Identificación con los parámetros obligatorios", responses = {
            @ApiResponse(responseCode = "201", description = "Tipo de Identificación creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TypeId.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta debido a parámetros inválidos", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/typeid")
    public ResponseEntity<TypeId> createTypeId(@RequestBody @Valid TypeIdCreateRequest typeIdCreateRequest) {
        TypeId typeId = idRestMapper.toTypeId(typeIdCreateRequest);
        typeId = createTypeIdUseCase.createTypeId(typeId);

        return new ResponseEntity<>(typeId, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtiene los Tipos De Identificación", description = "Devuelve los Tipos de Identificación asociados a una Empresa", responses = {
            @ApiResponse(responseCode = "200", description = "Tipos de Identificación obtenidos exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TypeId.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta debido a parámetros faltantes o inválidos", content = @Content(mediaType = "application/json"))
    })
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
    @Operation(summary = "Elimina un tipo de tercero", description = "Elimina un tipo de tercero a partir del Id del tercero", responses = {
            @ApiResponse(responseCode = "200", description = "El tipo de tercero fue eliminado exitosamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud o en la eliminación del tipo de tercero", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Tipo de tercero no encontrado", content = @Content(mediaType = "application/json"))
    })
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
