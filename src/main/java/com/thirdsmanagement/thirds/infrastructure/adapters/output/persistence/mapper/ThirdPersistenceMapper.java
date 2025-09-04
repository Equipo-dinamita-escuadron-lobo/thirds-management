package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    ThirdEntity toThirdEntity(Third third);

    /**
     * Método para mapear una entidad de tercero a un objeto de tercero.
     * @param thirdEntity Entidad de tercero.
     * @return Objeto de tercero.
     */
    @Mapping(target = "thirdTypes", ignore = true)
    Third toThird(ThirdEntity thirdEntity);
}