package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.CityEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.CountryEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.StateEntity;

/**
 * Mapper para la conversión entre entidades de geografía y modelos de dominio.
 * Utiliza MapStruct para generar automáticamente las implementaciones de mapeo.
 */
@Mapper(componentModel = "spring")
public interface GeographyPersistenceMapper {

    // Country mappings
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    CountryEntity toCountryEntity(Country country);

    Country toCountry(CountryEntity countryEntity);

    List<CountryEntity> toCountryEntityList(List<Country> countries);

    List<Country> toCountryList(List<CountryEntity> countryEntities);

    // State mappings
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "country", ignore = true)
    StateEntity toStateEntity(State state);

    @Mapping(target = "country", ignore = true)
    State toState(StateEntity stateEntity);

    List<StateEntity> toStateEntityList(List<State> states);

    List<State> toStateList(List<StateEntity> stateEntities);

    // City mappings
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "state", ignore = true)
    CityEntity toCityEntity(City city);

    @Mapping(target = "state", ignore = true)
    City toCity(CityEntity cityEntity);

    List<CityEntity> toCityEntityList(List<City> cities);

    List<City> toCityList(List<CityEntity> cityEntities);
}
