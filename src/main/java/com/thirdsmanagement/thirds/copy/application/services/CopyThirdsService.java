package com.thirdsmanagement.thirds.copy.application.services;

import com.thirdsmanagement.thirds.copy.application.input.IExecuteThirdsCopyPhasePort;
import com.thirdsmanagement.thirds.copy.application.output.ICopyJobLogRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.IThirdSourceRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.IThirdTargetRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.IThirdTypeSourceRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.IThirdTypeTargetRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.IThirdsAndTypesSourceRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.IThirdsAndTypesTargetRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.ITypeIdSourceRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.ITypeIdTargetRepositoryPort;
import com.thirdsmanagement.thirds.copy.domain.enums.CopyEstado;
import com.thirdsmanagement.thirds.copy.domain.models.CopyEquivalencia;
import com.thirdsmanagement.thirds.copy.domain.models.CopyJobLog;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyEquivalenciaDto;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyPhaseRequestDto;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyPhaseResponseDto;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdsAndTypesEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import com.thirdsmanagement.thirds.infrastructure.multitenancy.utils.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación que implementa la copia de terceros entre empresas.
 *
 * Orden topológico de copia:
 *   TypeId → ThirdType → Third (remap FK typeId) → ThirdsAndTypes (remap FK thId+ttId)
 *
 * Multi-tenancy: tenant override con TenantContext.setTenantId(entDestino) en bloque try/finally.
 * Idempotencia: verificación en copy_job_log antes de ejecutar.
 * Sin FKs cross-servicio: equivalenciasPrev siempre ignorado (THIRDS no lo usa).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CopyThirdsService implements IExecuteThirdsCopyPhasePort {

    /** Nombre del módulo en el log de idempotencia */
    private static final String MODULO = "thirds";

    private final ICopyJobLogRepositoryPort logRepo;
    private final ITypeIdSourceRepositoryPort typeIdSource;
    private final ITypeIdTargetRepositoryPort typeIdTarget;
    private final IThirdTypeSourceRepositoryPort thirdTypeSource;
    private final IThirdTypeTargetRepositoryPort thirdTypeTarget;
    private final IThirdSourceRepositoryPort thirdSource;
    private final IThirdTargetRepositoryPort thirdTarget;
    private final IThirdsAndTypesSourceRepositoryPort thirdsAndTypesSource;
    private final IThirdsAndTypesTargetRepositoryPort thirdsAndTypesTarget;

    /**
     * Ejecuta la copia de terceros de entOrigen a entDestino.
     *
     * @param request datos del proceso de copia
     * @return resultado de la ejecución con estado y equivalencias generadas
     */
    @Override
    @Transactional
    public CopyPhaseResponseDto ejecutar(CopyPhaseRequestDto request) {
        // Validación básica
        if (request.getEntOrigen().equals(request.getEntDestino())) {
            throw new IllegalArgumentException(
                    "entOrigen y entDestino no pueden ser la misma empresa: " + request.getEntOrigen());
        }

        String idProcesoStr = request.getIdProceso().toString();

        // Idempotencia: si ya existe un log para este proceso y fase, retornar resultado previo
        Optional<CopyJobLog> logExistente = logRepo.buscarPorIdProcesoYFase(idProcesoStr, request.getFase());
        if (logExistente.isPresent()) {
            log.info("Proceso idempotente: idProceso={}, fase={} ya fue ejecutado con estado={}",
                    idProcesoStr, request.getFase(), logExistente.get().getEstado());
            return construirResponseDesdeLog(logExistente.get());
        }

        // Registrar inicio del proceso
        CopyJobLog logInicio = CopyJobLog.builder()
                .idProceso(request.getIdProceso())
                .fase(request.getFase())
                .modulo(MODULO)
                .estado(CopyEstado.EN_PROCESO)
                .fechaInicio(Instant.now())
                .build();
        logRepo.guardar(logInicio);

        List<String> advertencias = new ArrayList<>();
        CopyEquivalenceMapper equivalenceMapper = new CopyEquivalenceMapper();
        int totalRegistros = 0;

        try {
            // Override del tenant: todas las operaciones de escritura se harán en entDestino
            TenantContext.setTenantId(request.getEntDestino());

            // Paso 1: Copiar TypeId (sin FKs internas, sin @TenantId — forzar manual)
            totalRegistros += copiarTypeId(request, equivalenceMapper);

            // Paso 2: Copiar ThirdType (sin FKs internas, sin @TenantId — forzar manual)
            totalRegistros += copiarThirdType(request, equivalenceMapper);

            // Paso 3: Copiar Third con remap FK typeId usando equivalenceMapper
            totalRegistros += copiarThirds(request, equivalenceMapper, advertencias);

            // Paso 4: Copiar ThirdsAndTypes con remap FKs thId + ttId
            totalRegistros += copiarThirdsAndTypes(request, equivalenceMapper, advertencias);

        } finally {
            TenantContext.clear();
        }

        // Determinar estado final
        CopyEstado estadoFinal = advertencias.isEmpty()
                ? CopyEstado.COMPLETADO
                : CopyEstado.COMPLETADO_CON_ADVERTENCIAS;

        // Construir lista de equivalencias para retornar al orquestador
        List<CopyEquivalencia> equivalencias = equivalenceMapper.toList();

        // Registrar fin del proceso
        CopyJobLog logFin = CopyJobLog.builder()
                .idProceso(request.getIdProceso())
                .fase(request.getFase())
                .modulo(MODULO)
                .estado(estadoFinal)
                .registrosProcesados(totalRegistros)
                .equivalenciasGeneradas(equivalencias.size())
                .advertencias(advertencias)
                .mensaje("Copia de terceros completada")
                .fechaInicio(logInicio.getFechaInicio())
                .fechaFin(Instant.now())
                .build();
        logRepo.guardar(logFin);

        return CopyPhaseResponseDto.builder()
                .estado(estadoFinal.name())
                .registrosProcesados(totalRegistros)
                .equivalenciasGeneradas(equivalencias.size())
                .mensaje(logFin.getMensaje())
                .advertencias(advertencias)
                .equivalencias(mapearEquivalenciasDto(equivalencias))
                .build();
    }

    /**
     * Copia los tipos de identificación (TypeId) de entOrigen a entDestino.
     * TypeId tiene tientId (no @TenantId) → forzar manual.
     */
    private int copiarTypeId(CopyPhaseRequestDto request, CopyEquivalenceMapper mapper) {
        List<TypeIdEntity> origen = typeIdSource.obtenerPorEmpresaYCorte(
                request.getEntOrigen(), request.getSnapshotCorte());

        for (TypeIdEntity original : origen) {
            Long idOriginal = original.getId();
            TypeIdEntity nuevo = TypeIdEntity.builder()
                    .tiId(original.getTiId())
                    .tiName(original.getTiName())
                    .tientId(request.getEntDestino()) // forzar entDestino manual (no @TenantId)
                    .status(original.getStatus())
                    .classification(original.getClassification())
                    .build();
            TypeIdEntity guardado = typeIdTarget.guardar(nuevo);
            mapper.registrar("type_id", idOriginal, guardado.getId());
        }
        return origen.size();
    }

    /**
     * Copia los tipos de tercero (ThirdType) de entOrigen a entDestino.
     * ThirdType tiene ttentId (no @TenantId) → forzar manual.
     */
    private int copiarThirdType(CopyPhaseRequestDto request, CopyEquivalenceMapper mapper) {
        List<ThirdTypeEntity> origen = thirdTypeSource.obtenerPorEmpresaYCorte(
                request.getEntOrigen(), request.getSnapshotCorte());

        for (ThirdTypeEntity original : origen) {
            Long idOriginal = original.getTtId();
            ThirdTypeEntity nuevo = ThirdTypeEntity.builder()
                    .ttName(original.getTtName())
                    .ttentId(request.getEntDestino()) // forzar entDestino manual
                    .status(original.getStatus())
                    .build();
            ThirdTypeEntity guardado = thirdTypeTarget.guardar(nuevo);
            mapper.registrar("third_type", idOriginal, guardado.getTtId());
        }
        return origen.size();
    }

    /**
     * Copia los terceros (Third) de entOrigen a entDestino.
     * Third tiene @TenantId → Hibernate lo asigna automáticamente desde TenantContext.
     * Remap FK typeId usando el equivalenceMapper.
     */
    private int copiarThirds(CopyPhaseRequestDto request, CopyEquivalenceMapper mapper,
                              List<String> advertencias) {
        List<ThirdEntity> origen = thirdSource.obtenerPorEmpresaYCorte(
                request.getEntOrigen(), request.getSnapshotCorte());

        for (ThirdEntity original : origen) {
            Long idOriginal = original.getThId();

            // Remap FK typeId interno
            TypeIdEntity nuevaTypeId = null;
            if (original.getTypeId() != null) {
                Long nuevoTypeIdId = mapper.resolverNuevoId("type_id", original.getTypeId().getId());
                if (nuevoTypeIdId != null) {
                    nuevaTypeId = TypeIdEntity.builder().id(nuevoTypeIdId).build();
                } else {
                    advertencias.add("Third id=" + idOriginal + ": typeId=" +
                            original.getTypeId().getId() + " sin equivalencia, se insertará con typeId null");
                    log.warn("Third id={}: typeId={} sin equivalencia en el mapper",
                            idOriginal, original.getTypeId().getId());
                }
            }

            ThirdEntity nuevo = ThirdEntity.builder()
                    .entId(request.getEntDestino())
                    .typeId(nuevaTypeId)
                    .personType(original.getPersonType())
                    .names(original.getNames())
                    .lastNames(original.getLastNames())
                    .socialReason(original.getSocialReason())
                    .gender(original.getGender())
                    .idNumber(original.getIdNumber())
                    .verificationNumber(original.getVerificationNumber())
                    .state(original.getState())
                    // Geografía: copiar como Strings literales sin remap (ADR-31)
                    .country(original.getCountry())
                    .province(original.getProvince())
                    .city(original.getCity())
                    .address(original.getAddress())
                    .phoneNumber(original.getPhoneNumber())
                    .email(original.getEmail())
                    .usageCount(0)
                    .build();

            ThirdEntity guardado = thirdTarget.guardar(nuevo);
            mapper.registrar("thirds", idOriginal, guardado.getThId());
        }
        return origen.size();
    }

    /**
     * Copia las relaciones ThirdsAndTypes de entOrigen a entDestino.
     * ThirdsAndTypes tiene @TenantId → Hibernate lo asigna automáticamente.
     * Remap FKs thId (→ thirds) y ttId (→ third_type) usando el equivalenceMapper.
     */
    private int copiarThirdsAndTypes(CopyPhaseRequestDto request, CopyEquivalenceMapper mapper,
                                      List<String> advertencias) {
        List<ThirdsAndTypesEntity> origen = thirdsAndTypesSource.obtenerPorEmpresa(request.getEntOrigen());
        int copiadas = 0;

        for (ThirdsAndTypesEntity original : origen) {
            Long nuevoThId = mapper.resolverNuevoId("thirds", original.getThId());
            Long nuevoTtId = mapper.resolverNuevoId("third_type", original.getTtId());

            if (nuevoThId == null) {
                advertencias.add("ThirdsAndTypes thId=" + original.getThId() +
                        " sin equivalencia en thirds, relación omitida");
                log.warn("ThirdsAndTypes thId={} sin equivalencia, relación omitida", original.getThId());
                continue;
            }
            if (nuevoTtId == null) {
                advertencias.add("ThirdsAndTypes ttId=" + original.getTtId() +
                        " sin equivalencia en third_type, relación omitida");
                log.warn("ThirdsAndTypes ttId={} sin equivalencia, relación omitida", original.getTtId());
                continue;
            }

            ThirdsAndTypesEntity nuevo = ThirdsAndTypesEntity.builder()
                    .thId(nuevoThId)
                    .ttId(nuevoTtId)
                    .build();
            thirdsAndTypesTarget.guardar(nuevo);
            copiadas++;
        }
        return copiadas;
    }

    /**
     * Construye el DTO de respuesta a partir de un log previo (idempotencia).
     */
    private CopyPhaseResponseDto construirResponseDesdeLog(CopyJobLog log) {
        return CopyPhaseResponseDto.builder()
                .estado(log.getEstado().name())
                .registrosProcesados(log.getRegistrosProcesados())
                .equivalenciasGeneradas(log.getEquivalenciasGeneradas())
                .mensaje(log.getMensaje())
                .advertencias(log.getAdvertencias() != null ? log.getAdvertencias() : Collections.emptyList())
                .equivalencias(Collections.emptyList())
                .build();
    }

    /**
     * Convierte las equivalencias de dominio a DTOs para la respuesta REST.
     */
    private List<CopyEquivalenciaDto> mapearEquivalenciasDto(List<CopyEquivalencia> equivalencias) {
        return equivalencias.stream()
                .map(eq -> CopyEquivalenciaDto.builder()
                        .modulo("THIRDS")
                        .tabla(eq.getTabla())
                        .idViejo(eq.getIdViejo())
                        .idNuevo(eq.getIdNuevo())
                        .build())
                .collect(Collectors.toList());
    }
}
