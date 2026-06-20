package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import com.thirdsmanagement.thirds.copy.application.output.ITypeIdTargetRepositoryPort;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA para guardar TypeId en la empresa destino.
 * Reutiliza el repositorio de producción — Hibernate aplica el tenant desde TenantContext.
 */
@Component
@RequiredArgsConstructor
public class TypeIdTargetRepositoryAdapter implements ITypeIdTargetRepositoryPort {

    private final TypeIdRepository jpaRepository;

    @Override
    public TypeIdEntity guardar(TypeIdEntity entity) {
        return jpaRepository.save(entity);
    }
}
