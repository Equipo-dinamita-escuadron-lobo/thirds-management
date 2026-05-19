package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import com.thirdsmanagement.thirds.copy.application.output.IThirdTargetRepositoryPort;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA para guardar Third en la empresa destino.
 * Reutiliza el repositorio de producción — Hibernate aplica @TenantId automáticamente
 * desde TenantContext cuando está configurado al entDestino.
 */
@Component
@RequiredArgsConstructor
public class ThirdTargetRepositoryAdapter implements IThirdTargetRepositoryPort {

    private final ThirdRepository jpaRepository;

    @Override
    public ThirdEntity guardar(ThirdEntity entity) {
        return jpaRepository.save(entity);
    }
}
