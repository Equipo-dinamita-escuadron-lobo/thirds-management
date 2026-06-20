package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para leer ThirdType de la empresa origen sin filtro de tenant.
 */
@Repository
public interface ThirdTypeCopySourceRepository extends JpaRepository<ThirdTypeEntity, Long> {

    /**
     * Obtiene todos los ThirdType de una empresa usando el campo ttentId (bypass tenant filter).
     *
     * @param entOrigen ID de la empresa origen
     * @return lista de entidades de la empresa origen
     */
    @Query("SELECT t FROM ThirdTypeEntity t WHERE t.ttentId = :entOrigen")
    List<ThirdTypeEntity> findByEmpresaOrigen(@Param("entOrigen") String entOrigen);
}
