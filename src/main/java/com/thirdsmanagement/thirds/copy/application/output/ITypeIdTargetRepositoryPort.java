package com.thirdsmanagement.thirds.copy.application.output;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;

/**
 * Puerto de salida para guardar TypeId en la empresa destino.
 */
public interface ITypeIdTargetRepositoryPort {

    /**
     * Persiste una entidad TypeId en el tenant destino.
     *
     * @param entity entidad a guardar
     * @return entidad guardada con ID generado
     */
    TypeIdEntity guardar(TypeIdEntity entity);
}
