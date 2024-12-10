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
@Mapper(componentModel = "spring")
public interface ThirdPersistenceMapper {

    /**
     * Método para mapear un objeto de la entidad de tercero a un objeto de tercero.
     * @param third Objeto de la entidad de tercero.
     * @return Objeto de tercero.
     */
    @Mapping(source = "typeId.typeId", target = "typeId.tiId")
    @Mapping(target = "thirdTypes", ignore = true)
    @Mapping(target = "thId", ignore = true)
    ThirdEntity toThirdEntity(Third third);

    /**
     * Método para mapear un objeto de tercero a un objeto de la entidad de tercero.
     * @param thirdEntity Objeto de tercero.
     * @return Objeto de la entidad de tercero.
     */
    @Mapping(source = "typeId.tiId", target = "typeId.typeId")
    @Mapping(target = "thirdTypes", ignore = true)
    Third toThird(ThirdEntity thirdEntity);
}