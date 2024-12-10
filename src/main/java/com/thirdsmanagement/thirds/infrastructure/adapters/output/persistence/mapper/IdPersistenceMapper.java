package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;

import aj.org.objectweb.asm.Type;

/**
 * Clase que representa el mapeo de los objetos de la capa de persistencia.
 * Contiene los métodos para mapear los objetos de la capa de persistencia.
 * Se utiliza la anotación @Mapper para indicar que es una clase de mapeo.
 * Se utiliza la anotación @ComponentModel para indicar que es un componente de spring.
 * Se utiliza la anotación @Mapping para indicar el mapeo de los atributos de los objetos.
 * Se utiliza la anotación @InheritInverseConfiguration para indicar el mapeo inverso de los atributos de los objetos.
 */
@Mapper(componentModel = "spring")
public interface IdPersistenceMapper {
    /**
     * Método para mapear un objeto de la entidad de tipo de tercero a un objeto de tipo de tercero.
     * @param thirdTypeEntity Objeto de la entidad de tipo de tercero.
     * @return Objeto de tipo de tercero.
     */
    @Mapping(source = "ttId", target = "thirdTypeId" )
    @Mapping(source = "ttName", target = "thirdTypeName" )
    @Mapping(source = "ttentId", target = "entId" )
    ThirdType toThirdTypeEntity(ThirdTypeEntity thirdTypeEntity);

    /**
     * Método para mapear una lista de objetos de la entidad de tipo de tercero a una lista de objetos de tipo de tercero.
     * @param thirdTypeEntities Lista de objetos de la entidad de tipo de tercero.
     * @return Lista de objetos de tipo de tercero.
     */
    @Mapping(target =  "ttId", source = "thirdTypeId" )
    @Mapping(target =  "ttName", source = "thirdTypeName" )
    @Mapping(target =  "ttentId", source = "entId" )
    ThirdTypeEntity toThirdType(ThirdType thirdType);

    /**
     * Método para mapear un objeto de tipo de identificación a un objeto de la entidad de tipo de identificación.
     * @param thirdTypes Objeto de tipo de identificación.
     * @return Objeto de la entidad de tipo de identificación.
     */
    List<ThirdTypeEntity> toThirdTypes(List<ThirdType> thirdTypes);

    /**
     * Método para mapear una lista de objetos de tipo de identificación a una lista de objetos de la entidad de tipo de identificación.
     * @param thirdTypeEntities Lista de objetos de tipo de identificación.
     * @return Lista de objetos de la entidad de tipo de identificación.
     */
    List<ThirdType> toThirdTypeEntitys(List<ThirdTypeEntity> thirdTypeEntities);

    /**
     * Método para mapear un objeto de tipo de identificación a un objeto de la entidad de tipo de identificación.
     * @param typeId Objeto de tipo de identificación.
     * @return Objeto de la entidad de tipo de identificación.
     */
    @Mapping(target =  "tiId", source = "typeId" )
    @Mapping(target =  "tiName", source = "typeIdname")
    @Mapping(target =  "tientId", source = "entId" )
    TypeIdEntity toTypeId(TypeId typeId);

    /**
     * Método para mapear una lista de objetos de tipo de identificación a una lista de objetos de la entidad de tipo de identificación.
     * @param typeIdEntities Lista de objetos de tipo de identificación.
     * @return Lista de objetos de la entidad de tipo de identificación.
     */
    @Mapping(source =  "tiId", target = "typeId" )
    @Mapping(source =  "tiName", target = "typeIdname")
    @Mapping(source =  "tientId", target = "entId" )
    TypeId toTypeIdEntity(TypeIdEntity typeIdEntity);

    /**
     * Método para mapear una lista de objetos de tipo de identificación a una lista de objetos de la entidad de tipo de identificación.
     * @param typeIdEntities Lista de objetos de tipo de identificación.
     * @return Lista de objetos de la entidad de tipo de identificación.
     */
    List<TypeId> toTypeIdEntititys(List<TypeIdEntity> typeIdEntities);

    /**
     * Método para mapear un objeto de tipo de identificación a un objeto de la entidad de tipo de identificación.
     * @param typeId Objeto de tipo de identificación.
     * @return Objeto de la entidad de tipo de identificación.
     */
    List<TypeIdEntity> toTypeIds(List<TypeId> typeIds);
    
}
