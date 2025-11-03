package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.CityResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.CountryResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.StateResponse;

/**
 * Mapper para convertir entre modelos de dominio geográfico y DTOs de respuesta REST.
 * Utiliza MapStruct para generar automáticamente las implementaciones de mapeo.
 */
@Mapper(componentModel = "spring")
public interface GeographyRestMapper {

    /**
     * Convierte un Country del dominio a CountryResponse.
     */
    CountryResponse toCountryResponse(Country country);

    /**
     * Convierte una lista de Countries del dominio a lista de CountryResponse.
     */
    List<CountryResponse> toCountryResponseList(List<Country> countries);

    /**
     * Convierte un State del dominio a StateResponse.
     */
    StateResponse toStateResponse(State state);

    /**
     * Convierte una lista de States del dominio a lista de StateResponse.
     */
    List<StateResponse> toStateResponseList(List<State> states);

    /**
     * Convierte un City del dominio a CityResponse.
     */
    CityResponse toCityResponse(City city);

    /**
     * Convierte una lista de Cities del dominio a lista de CityResponse.
     */
    List<CityResponse> toCityResponseList(List<City> cities);
}
