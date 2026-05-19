package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import com.thirdsmanagement.thirds.copy.application.output.IThirdTypeTargetRepositoryPort;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA para guardar ThirdType en la empresa destino.
 */
@Component
@RequiredArgsConstructor
public class ThirdTypeTargetRepositoryAdapter implements IThirdTypeTargetRepositoryPort {

    private final ThirdTypeRepository jpaRepository;

    @Override
    public ThirdTypeEntity guardar(ThirdTypeEntity entity) {
        return jpaRepository.save(entity);
    }
}
