package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ChangeThirdStateResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.GetThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdsEnterpriseListResponse;

@Mapper
public interface ThirdRestMapper {
    
    Third toThird(ThirdCreateRequest thirdCreateRequest);
    
    @Mapping(source = "idNumber",target = "id")
    @Mapping(source = "names",target = "name")
    @Mapping(source = "state",target = "description")
    ThirdResponse toThirdCreateResponse(Third third);

    ChangeThirdStateResponse toChangeThirdStateResponse(Boolean result);

    @Mapping(source = "page", target = "results")
    ThirdsEnterpriseListResponse toListThirdsResponse(Page<Third> page);

    GetThirdResponse toGetThirdResponse(Third third);
}
