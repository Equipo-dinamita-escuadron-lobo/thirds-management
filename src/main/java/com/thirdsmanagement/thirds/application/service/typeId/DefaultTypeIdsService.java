package com.thirdsmanagement.thirds.application.service.typeId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdsmanagement.thirds.application.ports.input.CreateTypeIdUseCase;
import com.thirdsmanagement.thirds.domain.constants.DefaultTypeIdDefinitions;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultTypeIdsService {

    private final CreateTypeIdUseCase createTypeIdUseCase;
    private final TypeIdRepository typeIdRepository;

    @Transactional
    public void ensureForEnterprise(String entId) {
        for (DefaultTypeIdDefinitions.Definition definition : DefaultTypeIdDefinitions.ALL) {
            if (typeIdRepository.existsByTiIdAndTientId(definition.code(), entId)) {
                continue;
            }
            createTypeIdUseCase.createTypeId(
                    TypeId.builder()
                            .typeId(definition.code())
                            .typeIdname(definition.name())
                            .entId(entId)
                            .classification(definition.classification())
                            .status(true)
                            .build());
        }
    }
}
