package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.interfaces;

import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdTypeCreateRequest;

import java.util.List;

import org.mapstruct.Mapper;

/**
 * @brief Interfaz mapper para tipos de tercero
 */
@Mapper(componentModel = "spring")
public interface IThirdTypeRestMapper {

    ThirdType toThirdType(ThirdTypeCreateRequest thirdTypeCreateRequest);

    ThirdType toThirdTypeResponse(ThirdType thirdType);

    List<ThirdType> toThirdTypeResponseList(List<ThirdType> thirdTypeList);
}
