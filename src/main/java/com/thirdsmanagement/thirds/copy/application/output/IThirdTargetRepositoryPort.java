package com.thirdsmanagement.thirds.copy.application.output;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;

/**
 * Puerto de salida para guardar Third en la empresa destino.
 */
public interface IThirdTargetRepositoryPort {

    /**
     * Persiste una entidad Third en el tenant destino.
     *
     * @param entity entidad a guardar
     * @return entidad guardada con ID generado
     */
    ThirdEntity guardar(ThirdEntity entity);
}
