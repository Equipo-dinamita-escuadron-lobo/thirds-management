package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repositorio JPA para leer TypeId de la empresa origen sin filtro de tenant.
 * Usa JPQL explícito para bypassar el @TenantId automático de Hibernate.
 */
@Repository
public interface TypeIdCopySourceRepository extends JpaRepository<TypeIdEntity, Long> {

    /**
     * Obtiene todos los TypeId de una empresa creados hasta el corte de snapshot.
     * El JPQL usa el campo tientId en lugar de la anotación @TenantId para bypassar
     * el filtro automático de multi-tenancy mientras se lee del origen.
     *
     * @param entOrigen     ID de la empresa origen
     * @param snapshotCorte fecha de corte del snapshot
     * @return lista de entidades de la empresa origen
     */
    @Query("SELECT t FROM TypeIdEntity t WHERE t.tientId = :entOrigen")
    List<TypeIdEntity> findByEmpresaOrigen(@Param("entOrigen") String entOrigen);
}
