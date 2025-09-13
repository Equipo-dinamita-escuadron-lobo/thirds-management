package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;

/**
 * Clase que representa el mapeo de los objetos de la capa de persistencia.
 * Contiene los métodos para mapear los objetos de la capa de persistencia.
 * Se utiliza la anotación @Mapper para indicar que es una clase de mapeo.
 * Se utiliza la anotación @ComponentModel para indicar que es un componente de spring.
 * Se utiliza la anotación @Mapping para indicar el mapeo de los atributos de los objetos.
 * Se utiliza la anotación @InheritInverseConfiguration para indicar el mapeo inverso de los atributos de los objetos.
 */
@Mapper(componentModel = "spring", uses = {IdPersistenceMapper.class})
public interface ThirdPersistenceMapper {

    /**
     * Método para mapear un objeto de tercero a una entidad de tercero.
     * @param third Objeto de tercero.
     * @return Entidad de tercero.
     */
    @Mapping(target = "thirdTypes", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "country", expression = "java(mapCountryToString(third.getCountry()))")
    @Mapping(target = "province", expression = "java(mapStateToString(third.getProvince()))")
    @Mapping(target = "city", expression = "java(mapCityToString(third.getCity()))")
    ThirdEntity toThirdEntity(Third third);

    /**
     * Método para mapear una entidad de tercero a un objeto de tercero.
     * @param thirdEntity Entidad de tercero.
     * @return Objeto de tercero.
     */
    @Mapping(target = "thirdTypes", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "province", ignore = true)
    @Mapping(target = "city", ignore = true)
    Third toThird(ThirdEntity thirdEntity);
    
    /**
     * Mapea un objeto Country a su código de país como String.
     * @param country el objeto Country
     * @return el código del país o null si el país es null
     */
    default String mapCountryToString(Country country) {
        return country != null ? country.getCountryCode() : null;
    }
    
    /**
     * Mapea un objeto State a su código de estado como String.
     * @param state el objeto State
     * @return el código del estado o null si el estado es null
     */
    default String mapStateToString(State state) {
        return state != null ? state.getStateCode() : null;
    }
    
    /**
     * Mapea un objeto City a su código de ciudad como String.
     * @param city el objeto City
     * @return el código de la ciudad o null si la ciudad es null
     */
    default String mapCityToString(City city) {
        return city != null ? city.getCityCode() : null;
    }
}