package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;

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
    ThirdType toThirdType(ThirdTypeEntity thirdTypeEntity);

    /**
     * Método para mapear un objeto de tipo de tercero a una entidad de tipo de tercero.
     * @param thirdType Objeto de tipo de tercero.
     * @return Entidad de tipo de tercero.
     */
    @Mapping(target =  "ttId", source = "thirdTypeId" )
    @Mapping(target =  "ttName", source = "thirdTypeName" )
    @Mapping(target =  "ttentId", source = "entId" )
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    ThirdTypeEntity toThirdTypeEntity(ThirdType thirdType);

    /**
     * Método para mapear una lista de objetos de tipo de tercero a una lista de entidades de tipo de tercero.
     * @param thirdTypes Lista de objetos de tipo de tercero.
     * @return Lista de entidades de tipo de tercero.
     */
    List<ThirdTypeEntity> toThirdTypeEntityList(List<ThirdType> thirdTypes);

    /**
     * Método para mapear una lista de entidades de tipo de tercero a una lista de objetos de tipo de tercero.
     * @param thirdTypeEntities Lista de entidades de tipo de tercero.
     * @return Lista de objetos de tipo de tercero.
     */
    List<ThirdType> toThirdTypeList(List<ThirdTypeEntity> thirdTypeEntities);

    /**
     * Método para mapear un objeto de tipo de identificación a una entidad de tipo de identificación.
     * @param typeId Objeto de tipo de identificación.
     * @return Entidad de tipo de identificación.
     */
    @Mapping(target =  "id", source = "id" )
    @Mapping(target =  "tiId", source = "typeId" )
    @Mapping(target =  "tiName", source = "typeIdname")
    @Mapping(target =  "tientId", source = "entId" )
    @Mapping(target =  "classification", source = "classification")
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    TypeIdEntity toTypeIdEntity(TypeId typeId);

    /**
     * Método para mapear una entidad de tipo de identificación a un objeto de tipo de identificación.
     * @param typeIdEntity Entidad de tipo de identificación.
     * @return Objeto de tipo de identificación.
     */
    @Mapping(source =  "id", target = "id" )
    @Mapping(source =  "tiId", target = "typeId" )
    @Mapping(source =  "tiName", target = "typeIdname")
    @Mapping(source =  "tientId", target = "entId" )
    @Mapping(source =  "classification", target = "classification")
    TypeId toTypeId(TypeIdEntity typeIdEntity);

    /**
     * Método para mapear una lista de objetos de tipo de identificación a una lista de entidades de tipo de identificación.
     * @param typeIds Lista de objetos de tipo de identificación.
     * @return Lista de entidades de tipo de identificación.
     */
    List<TypeIdEntity> toTypeIdEntityList(List<TypeId> typeIds);

    /**
     * Método para mapear una lista de entidades de tipo de identificación a una lista de objetos de tipo de identificación.
     * @param typeIdEntities Lista de entidades de tipo de identificación.
     * @return Lista de objetos de tipo de identificación.
     */
    List<TypeId> toTypeIdList(List<TypeIdEntity> typeIdEntities);
    
}
