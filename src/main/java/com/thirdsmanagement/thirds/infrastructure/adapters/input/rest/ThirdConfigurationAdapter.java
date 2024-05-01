package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ListThirdTypeUseCase;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.service.ListThirdTypeService;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdTypeCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.IdRestMapper;

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
    
    private final IdRestMapper idRestMapper;

     @PostMapping("/")
    public ResponseEntity<ThirdType> createThirdType(@RequestBody @Valid ThirdTypeCreateRequest thirdTypeCreateRequest){

        System.out.println("\n");
        System.out.println("Entrando a petición post crear");
        System.out.println("\n");
    

        ThirdType thirdType = idRestMapper.toThirdType(thirdTypeCreateRequest);
        thirdType = createThirdTypeUseCase.createThirdType(thirdType);

        return new ResponseEntity<>(thirdType,HttpStatus.CREATED);
    }

    public ResponseEntity<List<ThirdType>> getThirdType(@NotNull(message = "Third Id not be empty") @RequestParam("thId") Long thId){
        System.out.println("\n");
        System.out.println("Entrando a petición post Listar");
        System.out.println("\n");

        List<ThirdType> thirdTypes = listThirdTypeUseCase.getAllThirdTypes(thId);

        return new ResponseEntity<>(thirdTypes,HttpStatus.OK);

    }
    
}
