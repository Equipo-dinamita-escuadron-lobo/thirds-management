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
 * @brief Controlador REST para consultas de información geográfica
 *
 * Adaptador de entrada que expone endpoints para consultar datos geográficos
 * (países, estados/departamentos, ciudades) siguiendo la jerarquía geográfica.
 * Traduce entre el protocolo HTTP y los casos de uso del dominio.
 */
@RestController
@RequestMapping("/api/thirds/geography")
@RequiredArgsConstructor
public class GeographyRestController {

    private final ListGeographyUseCase listGeographyUseCase;
    private final GeographyRestMapper geographyRestMapper;

    /**
     * @brief Obtiene todos los países activos
     *
     * Endpoint que retorna la lista completa de países disponibles para asignar
     * a terceros, filtrando solo los países marcados como activos.
     * @return lista de países activos ordenados por nombre
     */
    @GetMapping("/countries")
    public ResponseEntity<List<CountryResponse>> getAllCountries() {
        List<Country> countries = listGeographyUseCase.getAllCountries();
        List<CountryResponse> response = geographyRestMapper.toCountryResponseList(countries);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * @brief Obtiene todos los estados/departamentos de un país específico
     *
     * Endpoint que retorna los estados/departamentos activos pertenecientes
     * a un país específico, validando la jerarquía geográfica.
     * @param countryCode código único del país (requerido)
     * @return lista de estados activos del país especificado
     */
    @GetMapping("/states")
    public ResponseEntity<List<StateResponse>> getStatesByCountry(
            @NotBlank(message = "El código del país es obligatorio")
            @RequestParam String countryCode) {

        List<State> states = listGeographyUseCase.getStatesByCountry(countryCode);
        List<StateResponse> response = geographyRestMapper.toStateResponseList(states);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * @brief Obtiene todas las ciudades de un estado/departamento específico
     *
     * Endpoint que retorna las ciudades activas pertenecientes a un estado/departamento
     * específico dentro de un país, validando la jerarquía geográfica completa.
     * @param stateCode código único del estado/departamento (requerido)
     * @param countryCode código único del país (requerido)
     * @return lista de ciudades activas del estado especificado
     */
    @GetMapping("/cities")
    public ResponseEntity<List<CityResponse>> getCitiesByState(
            @NotBlank(message = "El código del estado es obligatorio")
            @RequestParam String stateCode,
            @NotBlank(message = "El código del país es obligatorio")
            @RequestParam String countryCode) {

        List<City> cities = listGeographyUseCase.getCitiesByState(stateCode, countryCode);
        List<CityResponse> response = geographyRestMapper.toCityResponseList(cities);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
