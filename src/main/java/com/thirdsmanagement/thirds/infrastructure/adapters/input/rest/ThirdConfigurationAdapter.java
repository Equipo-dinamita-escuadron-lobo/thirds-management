package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.domain.service.ListThirdTypeService;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdTypeCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.TypeIdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.IdRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/thirds/configuration")
@RequiredArgsConstructor
public class ThirdConfigurationAdapter {

    private final CreateThirdTypeUseCase createThirdTypeUseCase;
    private final ListThirdTypeUseCase listThirdTypeUseCase;
    private final CreateTypeIdUseCase createTypeIdUseCase;
    private final ListTypeIdUseCase listTypeIdUseCase;
    
    private final IdRestMapper idRestMapper;

    @Operation(summary = "Crear un Tipo De Tercero",
       description = "Crea Un Tipo de Tercero con los parametros obligatorios")
     @PostMapping("/thirdtype")
    public ResponseEntity<ThirdType> createThirdType(@RequestBody @Valid ThirdTypeCreateRequest thirdTypeCreateRequest){

        System.out.println("\n");
        System.out.println("Entrando a petición post crear");
        System.out.println("\n");
    

        ThirdType thirdType = idRestMapper.toThirdType(thirdTypeCreateRequest);
        thirdType = createThirdTypeUseCase.createThirdType(thirdType);

        return new ResponseEntity<>(thirdType,HttpStatus.CREATED);
    }

    @Operation(summary = "Obtiene los Tipos De Terceros",
       description = "Devuelve Los Tipos de Terceros Asociados a una Empresa")
    @GetMapping("/thirdtype")
    public ResponseEntity<List<ThirdType>> getThirdType(@NotNull(message = "Third Id not be empty") @RequestParam("entId") String entId){
        System.out.println("\n");
        System.out.println("Entrando a petición post Listar");
        System.out.println("\n");

        List<ThirdType> thirdTypes = listThirdTypeUseCase.getAllThirdTypes(entId);

        return new ResponseEntity<>(thirdTypes,HttpStatus.OK);

    }

    @Operation(summary = "Crear un Tipo De Identificación",
       description = "Crea Un Tipo de Identificación con los parametros obligatorios")
    @PostMapping("/typeid")
    public ResponseEntity<TypeId> createTypeId(@RequestBody @Valid TypeIdCreateRequest typeIdCreateRequest){
        TypeId typeId = idRestMapper.toTypeId(typeIdCreateRequest);
        typeId = createTypeIdUseCase.createTypeId(typeId);

        return new ResponseEntity<>(typeId,HttpStatus.CREATED);
    }

    @Operation(summary = "Obtiene los Tipos De Identificación",
    description = "Devuelve Los Tipos de Identificación Asociados a una Empresa")
    @GetMapping("/typeid")
    public ResponseEntity<List<TypeId>> ListTypeId(@NotNull(message = "Third Id not be empty") @RequestParam("entId") String entId){
        List<TypeId> typeIds = listTypeIdUseCase.getAllTypeId(entId);
        return new ResponseEntity<>(typeIds,HttpStatus.OK);
    }
    
}
