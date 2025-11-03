package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thirdsmanagement.thirds.application.ports.input.ListGeographyUseCase;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.CityResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.CountryResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.StateResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.GeographyRestMapper;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de información geográfica.
 * Expone endpoints para consultar países, estados y ciudades con validación de jerarquía.
 */
@RestController
@RequestMapping("/api/thirds/geography")
@RequiredArgsConstructor
public class GeographyRestAdapter {

    private final ListGeographyUseCase listGeographyUseCase;
    private final GeographyRestMapper geographyRestMapper;

    /**
     * Obtiene todos los países activos.
     * @return Lista de países activos ordenados por nombre
     */
    @GetMapping("/countries")
    public ResponseEntity<List<CountryResponse>> getAllCountries() {
        List<Country> countries = listGeographyUseCase.getAllCountries();
        List<CountryResponse> response = geographyRestMapper.toCountryResponseList(countries);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene todos los estados activos de un país específico.
     * @param countryCode Código del país (requerido)
     * @return Lista de estados activos del país ordenados por nombre
     */
    @GetMapping("/states")
    public ResponseEntity<List<StateResponse>> getStatesByCountry(
            @NotBlank(message = "El código del país es obligatorio") 
            @RequestParam("countryCode") String countryCode) {
        
        List<State> states = listGeographyUseCase.getStatesByCountry(countryCode);
        List<StateResponse> response = geographyRestMapper.toStateResponseList(states);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene todas las ciudades activas de un estado específico.
     * @param stateCode Código del estado (requerido)
     * @param countryCode Código del país (requerido)
     * @return Lista de ciudades activas del estado ordenadas por nombre
     */
    @GetMapping("/cities")
    public ResponseEntity<List<CityResponse>> getCitiesByState(
            @NotBlank(message = "El código del estado es obligatorio") 
            @RequestParam("stateCode") String stateCode,
            @NotBlank(message = "El código del país es obligatorio") 
            @RequestParam("countryCode") String countryCode) {
        
        List<City> cities = listGeographyUseCase.getCitiesByState(stateCode, countryCode);
        List<CityResponse> response = geographyRestMapper.toCityResponseList(cities);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
