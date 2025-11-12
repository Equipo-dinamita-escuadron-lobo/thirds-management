package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.interfaces;

import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.TypeIdCreateRequest;

import java.util.List;

import org.mapstruct.Mapper;

/**
 * @brief Interfaz mapper para tipos de identificación
 */
@Mapper(componentModel = "spring")
public interface ITypeIdRestMapper {

    TypeId toTypeId(TypeIdCreateRequest typeIdCreateRequest);

    TypeId toTypeIdResponse(TypeId typeId);

    List<TypeId> toTypeIdResponseList(List<TypeId> typeIdList);
}
