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
 * @brief Mapper para conversión entre entidades geográficas y DTOs de respuesta
 */
@Mapper(componentModel = "spring")
public interface GeographyRestMapper {

    CountryResponse toCountryResponse(Country country);

    List<CountryResponse> toCountryResponseList(List<Country> countries);

    StateResponse toStateResponse(State state);

    List<StateResponse> toStateResponseList(List<State> states);

    CityResponse toCityResponse(City city);

    List<CityResponse> toCityResponseList(List<City> cities);
}
