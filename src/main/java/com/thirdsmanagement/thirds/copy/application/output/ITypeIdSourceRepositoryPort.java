package com.thirdsmanagement.thirds.copy.application.output;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;

import java.time.Instant;
import java.util.List;

/**
 * Puerto de salida para leer TypeId de la empresa origen (bypass tenant filter).
 */
public interface ITypeIdSourceRepositoryPort {

    /**
     * Obtiene todos los TypeId de la empresa origen creados hasta el corte.
     *
     * @param entOrigen     ID de la empresa origen
     * @param snapshotCorte fecha de corte del snapshot
     * @return lista de entidades de origen
     */
    List<TypeIdEntity> obtenerPorEmpresaYCorte(String entOrigen, Instant snapshotCorte);
}
