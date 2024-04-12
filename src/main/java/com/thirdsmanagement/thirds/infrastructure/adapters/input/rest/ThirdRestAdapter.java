package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;

import org.springframework.web.bind.annotation.RestController;

import com.thirdsmanagement.thirds.application.ports.input.ChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.input.CreateThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.GetThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ListThirdsUseCase;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ChangeThirdStateResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.ThirdRestMapper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

public class ThirdRestAdapter {

    private final CreateThirdUseCase createThirdUseCase;
    private final ListThirdsUseCase listThirdsUseCase;
    private final GetThirdUseCase getThirdUseCase;
    private final ChangeThirdStateUseCase changeThirdStateUseCase;

    private final ThirdRestMapper thirdRestMapper;

    @PostMapping("/")
    public ResponseEntity<ThirdResponse> createThird(@RequestBody @Valid ThirdCreateRequest thirdCreateRequest) {

        System.out.println("\n");
        System.out.println("Entrando a petición post crear");
        System.out.println("\n");

        Third third = thirdRestMapper.toThird(thirdCreateRequest);

        third = createThirdUseCase.createThird(third);

        return new ResponseEntity<>(thirdRestMapper.toThirdCreateResponse(third), HttpStatus.CREATED);
    }

    @PutMapping("/")
    public ResponseEntity<ChangeThirdStateResponse> changeThirdState(@NotNull(message = "Third ID not be empty") @RequestParam("thId") Long thId) {
        System.out.println("\n Entrando a petición put cambiar estado \n");

        Boolean result = changeThirdStateUseCase.changeThirdState(thId);

        return new ResponseEntity<>(thirdRestMapper.toChangeThirdStateResponse(result),HttpStatus.OK);
    }

    @GetMapping("/third")
    public ResponseEntity<Third> getThirdById(@NotNull(message = "Third Id not be empty") @RequestParam("thId") Long thId) {
        System.out.println("\n");
        System.out.println("Entrando a petición get third by Id");
        System.out.println("\n");

        Third third = getThirdUseCase.getThirdById(thId);


        return new ResponseEntity<>(third,HttpStatus.FOUND);
    }
    

    @GetMapping("/")
    public ResponseEntity<Page<Third>> getThirdsList(
        @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") Long entId, 
        @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage) {
        System.out.println("\n");
        System.out.println("Entrando a petición get thirds");
        System.out.println("\n");

        Pageable pageable = PageRequest.of(numPage, 10);

        Page<Third> page = listThirdsUseCase.getAllThirdsBy(entId,pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);

    }

    @GetMapping("/inactive")
    public ResponseEntity<Page<Third>> getInactiveThirdsList(
        @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") Long entId, 
        @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage) {
        System.out.println("\n");
        System.out.println("Entrando a petición get inactive thirds");
        System.out.println("\n");

        Pageable pageable = PageRequest.of(numPage, 10);

        Page<Third> page = listThirdsUseCase.getAllInactiveThirdsBy(entId,pageable);

        return new ResponseEntity<>(page, HttpStatus.FOUND);

    }

    @GetMapping("/providers")
    public ResponseEntity<Page<Third>> getProvidersList(
        @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") Long entId, 
        @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage) {
        System.out.println("\n");
        System.out.println("Entrando a petición get providers");
        System.out.println("\n");

        Pageable pageable = PageRequest.of(numPage, 10);

        Page<Third> page = listThirdsUseCase.getAllProvidersBy(entId,pageable);

        return new ResponseEntity<>(page, HttpStatus.FOUND);
    }

    @GetMapping("/customers")
    public ResponseEntity<Page<Third>> getCustomersList(
        @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") Long entId, 
        @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage) {
        System.out.println("\n");
        System.out.println("Entrando a petición get customers");
        System.out.println("\n");

        Pageable pageable = PageRequest.of(numPage, 10);

        Page<Third> page = listThirdsUseCase.getAllCustomersBy(entId,pageable);

        return new ResponseEntity<>(page, HttpStatus.FOUND);
    }    
} 
