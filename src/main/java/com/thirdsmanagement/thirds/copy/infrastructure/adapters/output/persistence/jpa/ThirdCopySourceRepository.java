package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para leer Third de la empresa origen sin filtro de tenant.
 * Usa JPQL explícito con entId para bypassar el @TenantId automático de Hibernate.
 */
@Repository
public interface ThirdCopySourceRepository extends JpaRepository<ThirdEntity, Long> {

    /**
     * Obtiene todos los terceros de una empresa usando el campo entId (bypass tenant filter).
     * LEFT JOIN FETCH para cargar typeId y evitar N+1.
     *
     * @param entOrigen ID de la empresa origen
     * @return lista de terceros de la empresa origen
     */
    @Query("SELECT t FROM ThirdEntity t LEFT JOIN FETCH t.typeId WHERE t.entId = :entOrigen")
    List<ThirdEntity> findByEmpresaOrigen(@Param("entOrigen") String entOrigen);
}
