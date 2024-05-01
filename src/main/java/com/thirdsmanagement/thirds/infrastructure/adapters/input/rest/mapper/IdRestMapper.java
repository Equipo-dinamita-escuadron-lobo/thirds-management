package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper;

import org.mapstruct.Mapper;

import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdTypeCreateRequest;

@Mapper
public interface IdRestMapper {

    ThirdTypeCreateRequest toThirdTypeCreateRequest(ThirdType thirdType);

    ThirdType toThirdType(ThirdTypeCreateRequest thirdTypeCreateRequest);
    
    
} 