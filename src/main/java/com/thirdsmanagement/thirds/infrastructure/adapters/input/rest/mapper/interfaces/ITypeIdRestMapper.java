package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.interfaces;

import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.TypeIdCreateRequest;

import java.util.List;

import org.mapstruct.Mapper;

/**
 * Interfaz que define métodos para mapear entre entidades de tipo de identificación y sus representaciones REST.
 */
@Mapper(componentModel = "spring")
public interface ITypeIdRestMapper {

    /**
     * Convierte una solicitud de creación de tipo de identificación ({@link TypeIdCreateRequest}) en una entidad de tipo de identificación ({@link TypeId}).
     *
     * @param typeIdCreateRequest la solicitud de creación de tipo de identificación.
     * @return la entidad de tipo de identificación convertida.
     */
    TypeId toTypeId(TypeIdCreateRequest typeIdCreateRequest);

    /**
     * Convierte una entidad de tipo de identificación ({@link TypeId}) en una respuesta de tipo de identificación ({@link TypeId}).
     *
     * @param typeId la entidad de tipo de identificación.
     * @return la respuesta de tipo de identificación convertida.
     */
    TypeId toTypeIdResponse(TypeId typeId);

    /**
     * Convierte una lista de entidades de tipo de identificación ({@link TypeId}) en una lista de respuestas de tipo de identificación ({@link TypeId}).
     *
     * @param typeIdList la lista de entidades de tipo de identificación.
     * @return la lista de respuestas de tipo de identificación convertida.
     */
    List<TypeId> toTypeIdResponseList(List<TypeId> typeIdList);
}
