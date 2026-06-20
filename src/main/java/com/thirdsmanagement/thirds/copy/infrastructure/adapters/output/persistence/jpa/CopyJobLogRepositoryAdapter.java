package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import com.thirdsmanagement.thirds.copy.application.output.ICopyJobLogRepositoryPort;
import com.thirdsmanagement.thirds.copy.domain.models.CopyJobLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador JPA para el log de idempotencia de copia.
 * Convierte entre el modelo de dominio CopyJobLog y la entidad JPA CopyJobLogEntity.
 */
@Component
@RequiredArgsConstructor
public class CopyJobLogRepositoryAdapter implements ICopyJobLogRepositoryPort {

    private final CopyJobLogJpaRepository jpaRepository;

    @Override
    public CopyJobLog guardar(CopyJobLog log) {
        CopyJobLogEntity entity = toEntity(log);
        CopyJobLogEntity guardada = jpaRepository.save(entity);
        return toDomain(guardada);
    }

    @Override
    public Optional<CopyJobLog> buscarPorIdProcesoYFase(String idProceso, int fase) {
        return jpaRepository.findByIdProcesoAndFase(idProceso, fase)
                .map(this::toDomain);
    }

    @Override
    public Optional<CopyJobLog> buscarPorIdProceso(String idProceso) {
        return jpaRepository.findTopByIdProcesoOrderByFechaInicioDesc(idProceso)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void eliminarPorIdProceso(String idProceso) {
        jpaRepository.deleteByIdProceso(idProceso);
    }

    // Métodos de mapeo entre dominio y entidad JPA

    private CopyJobLogEntity toEntity(CopyJobLog log) {
        return CopyJobLogEntity.builder()
                .idProceso(log.getIdProceso() != null ? log.getIdProceso().toString() : null)
                .fase(log.getFase())
                .modulo(log.getModulo())
                .estado(log.getEstado())
                .registrosProcesados(log.getRegistrosProcesados())
                .equivalenciasGeneradas(log.getEquivalenciasGeneradas())
                .advertencias(serializarAdvertencias(log.getAdvertencias()))
                .mensaje(log.getMensaje())
                .fechaInicio(log.getFechaInicio())
                .fechaFin(log.getFechaFin())
                .build();
    }

    private CopyJobLog toDomain(CopyJobLogEntity entity) {
        return CopyJobLog.builder()
                .idProceso(entity.getIdProceso() != null ? UUID.fromString(entity.getIdProceso()) : null)
                .fase(entity.getFase())
                .modulo(entity.getModulo())
                .estado(entity.getEstado())
                .registrosProcesados(entity.getRegistrosProcesados())
                .equivalenciasGeneradas(entity.getEquivalenciasGeneradas())
                .advertencias(deserializarAdvertencias(entity.getAdvertencias()))
                .mensaje(entity.getMensaje())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .build();
    }

    private String serializarAdvertencias(List<String> advertencias) {
        if (advertencias == null || advertencias.isEmpty()) {
            return null;
        }
        return String.join("|", advertencias);
    }

    private List<String> deserializarAdvertencias(String advertencias) {
        if (advertencias == null || advertencias.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.asList(advertencias.split("\\|"));
    }
}
