package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;

import org.springframework.web.bind.annotation.RestController;

import com.thirdsmanagement.thirds.application.ports.input.ChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.input.CreateThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.GetThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ListThirdsUseCase;
import com.thirdsmanagement.thirds.application.ports.input.UpdateThirdUseCase;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ChangeThirdStateResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.ThirdRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;


@CrossOrigin("*")
@RestController
@RequestMapping("/api/thirds")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('admin_client') or hasRole('super_client')")
public class ThirdRestAdapter {

    private final CreateThirdUseCase createThirdUseCase;
    private final ListThirdsUseCase listThirdsUseCase;
    private final GetThirdUseCase getThirdUseCase;
    private final ChangeThirdStateUseCase changeThirdStateUseCase;
    private final UpdateThirdUseCase updateThirdUseCase;

    private final ThirdRestMapper thirdRestMapper;

    @Operation(summary = "Crear Tercero",
       description = "Crea Un Tercero Recibiendo Todos Los Campos Obligatorios De Este")
    @PostMapping("/")
    public ResponseEntity<ThirdResponse> createThird(@RequestBody @Valid ThirdCreateRequest thirdCreateRequest) {

        System.out.println("\n");
        System.out.println("Entrando a petición post crear");
        System.out.println("\n");

        Third third = thirdRestMapper.toThird(thirdCreateRequest);
        System.out.println("///////////////////////////////////////////////////////"+third);
        third = createThirdUseCase.createThird(third);

        return new ResponseEntity<>(thirdRestMapper.toThirdCreateResponse(third), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualiza Un Tercero",
       description = "Actualiza Un Tercero Recibiendo Todos Los Campos Obligatorios De Este")
    @PostMapping("/update")
    public ResponseEntity<ThirdResponse> updateThird(@RequestBody @Valid ThirdCreateRequest thirdCreateRequest) {

        System.out.println("\n");
        System.out.println("Entrando a petición post Update");
        System.out.println("\n");

        Third third = thirdRestMapper.toThird(thirdCreateRequest);

        third = updateThirdUseCase.updateThird(third);

        return new ResponseEntity<>(thirdRestMapper.toThirdCreateResponse(third), HttpStatus.OK);
    }

    @Operation(summary = "Cambia El Estado De Un Tercero")
    @PutMapping("/")
    public ResponseEntity<ChangeThirdStateResponse> changeThirdState(@NotNull(message = "Third ID not be empty") @RequestParam("thId") Long thId) {
        System.out.println("\n Entrando a petición put cambiar estado \n");

        Boolean result = changeThirdStateUseCase.changeThirdState(thId);

        return new ResponseEntity<>(thirdRestMapper.toChangeThirdStateResponse(result),HttpStatus.OK);
    }

    @Operation(summary = "Obtiene Un Tercero",
       description = "Obtiene Un Tercero Por El Id De Este")
    @GetMapping("/third")
    public ResponseEntity<Third> getThirdById(@NotNull(message = "Third Id not be empty") @RequestParam("thId") Long thId) {
        System.out.println("\n");
        System.out.println("Entrando a petición get third by Id");
        System.out.println("\n");

        Third third = getThirdUseCase.getThirdById(thId);


        return new ResponseEntity<>(third,HttpStatus.OK);
    }
    

    @Operation(summary = "Obtiene Una Lista de Terceros",
    description = "Obtiene Una Lista de Terceros, se debe mandar el Id the la empresa, con el numero de pagina de Terceros")
    @GetMapping("/")
    public ResponseEntity<Page<Third>> getThirdsList(
        @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId, 
        @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage) {
        System.out.println("\n");
        System.out.println("Entrando a petición get thirds");
        System.out.println("\n");

        Pageable pageable = PageRequest.of(numPage, 10);

        Page<Third> page = listThirdsUseCase.getAllThirdsBy(entId,pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);

    }

    @Operation(summary = "Inactiva Un Tercero",
    description = "Inactiva Un Tercero Dandole el Id Del Tercero")
    @GetMapping("/inactive")
    public ResponseEntity<Page<Third>> getInactiveThirdsList(
        @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId, 
        @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage) {
        System.out.println("\n");
        System.out.println("Entrando a petición get inactive thirds");
        System.out.println("\n");

        Pageable pageable = PageRequest.of(numPage, 10);

        Page<Third> page = listThirdsUseCase.getAllInactiveThirdsBy(entId,pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);

    }

    @Operation(summary = "Obtiene Una Lista de Terceros Que Son Proveedores",
    description = "Obtiene Una Lista de Terceros que son proveedores, dando como parametros en Id de la empresa y el numero de pagina")
    @GetMapping("/providers")
    public ResponseEntity<Page<Third>> getProvidersList(
        @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId, 
        @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage) {
        System.out.println("\n");
        System.out.println("Entrando a petición get providers");
        System.out.println("\n");

        Pageable pageable = PageRequest.of(numPage, 10);

        Page<Third> page = listThirdsUseCase.getAllProvidersBy(entId,pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Operation(summary = "Obtiene Una Lista de Clientes",
    description = "Obtiene Una Lista de Terceros que son Clientes, dando como parametros en Id de la empresa y el numero de pagina")
    @GetMapping("/customers")
    public ResponseEntity<Page<Third>> getCustomersList(
        @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId, 
        @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage) {
        System.out.println("\n");
        System.out.println("Entrando a petición get customers");
        System.out.println("\n");

        Pageable pageable = PageRequest.of(numPage, 10);

        Page<Third> page = listThirdsUseCase.getAllCustomersBy(entId,pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }    
} 
