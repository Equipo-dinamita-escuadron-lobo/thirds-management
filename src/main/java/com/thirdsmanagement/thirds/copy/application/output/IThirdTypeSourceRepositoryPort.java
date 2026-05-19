package com.thirdsmanagement.thirds.copy.application.output;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;

import java.time.Instant;
import java.util.List;

/**
 * Puerto de salida para leer ThirdType de la empresa origen (bypass tenant filter).
 */
public interface IThirdTypeSourceRepositoryPort {

    /**
     * Obtiene todos los ThirdType de la empresa origen creados hasta el corte.
     *
     * @param entOrigen     ID de la empresa origen
     * @param snapshotCorte fecha de corte del snapshot
     * @return lista de entidades de origen
     */
    List<ThirdTypeEntity> obtenerPorEmpresaYCorte(String entOrigen, Instant snapshotCorte);
}
