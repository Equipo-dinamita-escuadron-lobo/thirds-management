package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para el log de idempotencia de copia de terceros.
 */
@Repository
public interface CopyJobLogJpaRepository extends JpaRepository<CopyJobLogEntity, Long> {

    /**
     * Busca un log por proceso y número de fase.
     *
     * @param idProceso UUID del proceso
     * @param fase      número de fase
     * @return log encontrado o vacío
     */
    Optional<CopyJobLogEntity> findByIdProcesoAndFase(String idProceso, int fase);

    /**
     * Busca el log más reciente de un proceso (para status/cancel).
     *
     * @param idProceso UUID del proceso
     * @return log más reciente o vacío
     */
    Optional<CopyJobLogEntity> findTopByIdProcesoOrderByFechaInicioDesc(String idProceso);

    /**
     * Elimina todos los registros de log de un proceso.
     *
     * @param idProceso UUID del proceso
     */
    void deleteByIdProceso(String idProceso);
}
