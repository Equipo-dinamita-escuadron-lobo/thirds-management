package com.thirdsmanagement.thirds.copy.application.output;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;

/**
 * Puerto de salida para guardar ThirdType en la empresa destino.
 */
public interface IThirdTypeTargetRepositoryPort {

    /**
     * Persiste una entidad ThirdType en el tenant destino.
     *
     * @param entity entidad a guardar
     * @return entidad guardada con ID generado
     */
    ThirdTypeEntity guardar(ThirdTypeEntity entity);
}
