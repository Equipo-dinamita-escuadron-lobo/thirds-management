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
 * @brief Mapper para conversión entre entidades geográficas JPA y modelos del dominio
 */
@Mapper(componentModel = "spring")
public interface GeographyPersistenceMapper {

    CountryEntity toCountryEntity(Country country);

    Country toCountry(CountryEntity countryEntity);

    List<CountryEntity> toCountryEntityList(List<Country> countries);

    List<Country> toCountryList(List<CountryEntity> countryEntities);

    @Mapping(target = "country", ignore = true)
    StateEntity toStateEntity(State state);

    @Mapping(target = "country", ignore = true)
    State toState(StateEntity stateEntity);

    List<StateEntity> toStateEntityList(List<State> states);

    List<State> toStateList(List<StateEntity> stateEntities);

    @Mapping(target = "state", ignore = true)
    CityEntity toCityEntity(City city);

    @Mapping(target = "state", ignore = true)
    City toCity(CityEntity cityEntity);

    List<CityEntity> toCityEntityList(List<City> cities);

    List<City> toCityList(List<CityEntity> cityEntities);
}
