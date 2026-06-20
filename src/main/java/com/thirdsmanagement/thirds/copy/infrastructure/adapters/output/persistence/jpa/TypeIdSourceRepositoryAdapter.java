package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import com.thirdsmanagement.thirds.copy.application.output.ITypeIdSourceRepositoryPort;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Adaptador JPA para leer TypeId de la empresa origen.
 * Note: TypeId no tiene campo created_at en la entidad actual — se lee todo sin filtro temporal.
 */
@Component
@RequiredArgsConstructor
public class TypeIdSourceRepositoryAdapter implements ITypeIdSourceRepositoryPort {

    private final TypeIdCopySourceRepository jpaRepository;

    @Override
    public List<TypeIdEntity> obtenerPorEmpresaYCorte(String entOrigen, Instant snapshotCorte) {
        // TypeId no tiene campo created_at en entidad — se leen todos los del origen
        return jpaRepository.findByEmpresaOrigen(entOrigen);
    }
}
