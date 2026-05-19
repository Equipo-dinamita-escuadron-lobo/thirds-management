package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import com.thirdsmanagement.thirds.copy.application.output.IThirdSourceRepositoryPort;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Adaptador JPA para leer Third de la empresa origen.
 */
@Component
@RequiredArgsConstructor
public class ThirdSourceRepositoryAdapter implements IThirdSourceRepositoryPort {

    private final ThirdCopySourceRepository jpaRepository;

    @Override
    public List<ThirdEntity> obtenerPorEmpresaYCorte(String entOrigen, Instant snapshotCorte) {
        // Third no tiene campo created_at en entidad — se leen todos los del origen
        return jpaRepository.findByEmpresaOrigen(entOrigen);
    }
}
