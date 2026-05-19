package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdsAndTypesEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdsAndTypesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para leer ThirdsAndTypes de la empresa origen sin filtro de tenant.
 */
@Repository
public interface ThirdsAndTypesCopySourceRepository extends JpaRepository<ThirdsAndTypesEntity, ThirdsAndTypesId> {

    /**
     * Obtiene todas las relaciones ThirdsAndTypes de terceros de una empresa.
     * JOIN con ThirdEntity para filtrar por entOrigen.
     *
     * @param entOrigen ID de la empresa origen
     * @return lista de relaciones de la empresa origen
     */
    @Query("SELECT tat FROM ThirdsAndTypesEntity tat " +
           "JOIN ThirdEntity t ON tat.thId = t.thId " +
           "WHERE t.entId = :entOrigen")
    List<ThirdsAndTypesEntity> findByEmpresaOrigen(@Param("entOrigen") String entOrigen);
}
