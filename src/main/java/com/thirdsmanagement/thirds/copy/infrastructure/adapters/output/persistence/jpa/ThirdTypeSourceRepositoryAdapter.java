package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import com.thirdsmanagement.thirds.copy.application.output.IThirdTypeSourceRepositoryPort;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Adaptador JPA para leer ThirdType de la empresa origen.
 */
@Component
@RequiredArgsConstructor
public class ThirdTypeSourceRepositoryAdapter implements IThirdTypeSourceRepositoryPort {

    private final ThirdTypeCopySourceRepository jpaRepository;

    @Override
    public List<ThirdTypeEntity> obtenerPorEmpresaYCorte(String entOrigen, Instant snapshotCorte) {
        // ThirdType no tiene campo created_at en entidad — se leen todos los del origen
        return jpaRepository.findByEmpresaOrigen(entOrigen);
    }
}
