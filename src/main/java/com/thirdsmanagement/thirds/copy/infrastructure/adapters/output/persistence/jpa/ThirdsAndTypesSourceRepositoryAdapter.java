package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import com.thirdsmanagement.thirds.copy.application.output.IThirdsAndTypesSourceRepositoryPort;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdsAndTypesEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptador JPA para leer ThirdsAndTypes de la empresa origen.
 */
@Component
@RequiredArgsConstructor
public class ThirdsAndTypesSourceRepositoryAdapter implements IThirdsAndTypesSourceRepositoryPort {

    private final ThirdsAndTypesCopySourceRepository jpaRepository;

    @Override
    public List<ThirdsAndTypesEntity> obtenerPorEmpresa(String entOrigen) {
        return jpaRepository.findByEmpresaOrigen(entOrigen);
    }
}
