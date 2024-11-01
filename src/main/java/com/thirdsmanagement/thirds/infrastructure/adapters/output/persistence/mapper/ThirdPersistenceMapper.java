package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;

@Mapper(componentModel = "spring")
public interface ThirdPersistenceMapper {

    @Mapping(source = "typeId.typeId", target = "typeId.tiId")
    @Mapping(target = "thirdTypes", ignore = true)
    @Mapping(target = "thId", ignore = true)
    ThirdEntity toThirdEntity(Third third);

    @Mapping(source = "typeId.tiId", target = "typeId.typeId")
    @Mapping(target = "thirdTypes", ignore = true)
    Third toThird(ThirdEntity thirdEntity);
}