package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import com.thirdsmanagement.thirds.copy.application.output.IThirdsAndTypesTargetRepositoryPort;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdsAndTypesEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdsAndTypesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA para guardar ThirdsAndTypes en la empresa destino.
 */
@Component
@RequiredArgsConstructor
public class ThirdsAndTypesTargetRepositoryAdapter implements IThirdsAndTypesTargetRepositoryPort {

    private final ThirdsAndTypesRepository jpaRepository;

    @Override
    public ThirdsAndTypesEntity guardar(ThirdsAndTypesEntity entity) {
        return jpaRepository.save(entity);
    }
}
