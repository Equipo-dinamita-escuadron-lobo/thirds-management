package com.thirdsmanagement.thirds.copy.application.output;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdsAndTypesEntity;

/**
 * Puerto de salida para guardar ThirdsAndTypes en la empresa destino.
 */
public interface IThirdsAndTypesTargetRepositoryPort {

    /**
     * Persiste una relación ThirdsAndTypes en el tenant destino.
     *
     * @param entity entidad a guardar
     * @return entidad guardada
     */
    ThirdsAndTypesEntity guardar(ThirdsAndTypesEntity entity);
}
