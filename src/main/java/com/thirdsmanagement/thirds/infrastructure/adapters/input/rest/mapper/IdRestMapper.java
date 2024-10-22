package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper;

import org.mapstruct.Mapper;

import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdTypeCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.TypeIdCreateRequest;

@Mapper
public interface IdRestMapper {

    ThirdTypeCreateRequest toThirdTypeCreateRequest(ThirdType thirdType);

    ThirdType toThirdType(ThirdTypeCreateRequest thirdTypeCreateRequest);

    TypeIdCreateRequest toTypeIdCreateRequest(TypeId typeId);

    TypeId toTypeId(TypeIdCreateRequest typeIdCreateRequest);
    
    
    
} 