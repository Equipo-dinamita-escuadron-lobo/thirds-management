package com.thirdsmanagement.thirds.copy.application.output;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;

import java.time.Instant;
import java.util.List;

/**
 * Puerto de salida para leer Third de la empresa origen (bypass tenant filter).
 */
public interface IThirdSourceRepositoryPort {

    /**
     * Obtiene todos los Third de la empresa origen creados hasta el corte.
     *
     * @param entOrigen     ID de la empresa origen
     * @param snapshotCorte fecha de corte del snapshot
     * @return lista de entidades de origen
     */
    List<ThirdEntity> obtenerPorEmpresaYCorte(String entOrigen, Instant snapshotCorte);
}
