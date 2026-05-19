package com.thirdsmanagement.thirds.copy.application.output;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdsAndTypesEntity;

import java.util.List;

/**
 * Puerto de salida para leer ThirdsAndTypes de la empresa origen (bypass tenant filter).
 */
public interface IThirdsAndTypesSourceRepositoryPort {

    /**
     * Obtiene todas las relaciones ThirdsAndTypes de la empresa origen.
     *
     * @param entOrigen ID de la empresa origen
     * @return lista de entidades de origen
     */
    List<ThirdsAndTypesEntity> obtenerPorEmpresa(String entOrigen);
}
