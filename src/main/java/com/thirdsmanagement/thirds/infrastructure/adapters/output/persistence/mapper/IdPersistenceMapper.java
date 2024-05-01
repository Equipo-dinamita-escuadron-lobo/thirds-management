package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;

@Mapper(componentModel = "spring")
public interface IdPersistenceMapper {

    @Mapping(source = "ttId", target = "thirdTypeId" )
    @Mapping(source = "ttName", target = "thirdTypeName" )
    @Mapping(source = "ttentId", target = "entId" )
    ThirdType toThirdTypeEntity(ThirdTypeEntity thirdTypeEntity);

    @Mapping(target =  "ttId", source = "thirdTypeId" )
    @Mapping(target =  "ttName", source = "thirdTypeName" )
    @Mapping(target =  "ttentId", source = "entId" )
    ThirdTypeEntity toThirdType(ThirdType thirdType);

    List<ThirdTypeEntity> toThirdTypes(List<ThirdType> thirdTypes);
    List<ThirdType> toThirdTypeEntitys(List<ThirdTypeEntity> thirdTypeEntities);

    
}
