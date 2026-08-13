package com.thirdsmanagement.thirds.application.service.thirdType;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdTypeUseCase;
import com.thirdsmanagement.thirds.domain.constants.DefaultThirdTypeNames;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultThirdTypesService {

    private final CreateThirdTypeUseCase createThirdTypeUseCase;
    private final ThirdTypeRepository thirdTypeRepository;

    @Transactional
    public void ensureForEnterprise(String entId) {
        for (String typeName : DefaultThirdTypeNames.ALL) {
            if (thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId(typeName, entId)) {
                continue;
            }
            createThirdTypeUseCase.createThirdType(
                    ThirdType.builder()
                            .thirdTypeName(typeName)
                            .entId(entId)
                            .status(true)
                            .build());
        }
    }
}
