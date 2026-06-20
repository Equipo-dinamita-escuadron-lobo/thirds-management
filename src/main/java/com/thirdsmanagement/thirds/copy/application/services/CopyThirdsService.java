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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        // Modo RESTORE: datos importados presentes — insertar en empresa destino
        if (request.getDatosImportados() != null) {
            return ejecutarImportacion(request);
        }

        // Modo BACKUP: sin empresa destino — exportar datos de la empresa origen
        if (request.getEntDestino() == null || request.getEntDestino().isBlank()) {
            return ejecutarExportacion(request);
        }

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

        List<String> advertencias = new ArrayList<>();
        CopyEquivalenceMapper equivalenceMapper = new CopyEquivalenceMapper();
        Map<Long, TypeIdEntity> savedTypeIdEntities = new LinkedHashMap<>();
        int totalRegistros = 0;

        try {
            // Override del tenant: todas las operaciones de escritura se harán en entDestino
            TenantContext.setTenantId(request.getEntDestino());

            // Paso 1: Copiar TypeId — llena savedTypeIdEntities con entidades manejadas por JPA
            totalRegistros += copiarTypeId(request, equivalenceMapper, savedTypeIdEntities);

            // Paso 2: Copiar ThirdType (sin FKs internas, sin @TenantId — forzar manual)
            totalRegistros += copiarThirdType(request, equivalenceMapper);

            // Paso 3: Copiar Third con remap FK typeId usando entidades manejadas
            totalRegistros += copiarThirds(request, equivalenceMapper, advertencias, savedTypeIdEntities);

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
                .equivalenciasGeneradas(mapearEquivalenciasDto(equivalencias))
                .mensaje(logFin.getMensaje())
                .advertencias(advertencias)
                .build();
    }

    /**
     * Copia los tipos de identificación (TypeId) de entOrigen a entDestino.
     * TypeId tiene tientId (no @TenantId) → forzar manual.
     */
    private int copiarTypeId(CopyPhaseRequestDto request, CopyEquivalenceMapper mapper,
                             Map<Long, TypeIdEntity> savedTypeIdEntities) {
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
            // Guardar la entidad manejada por JPA para usarla como FK en copiarThirds
            savedTypeIdEntities.put(idOriginal, guardado);
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
                              List<String> advertencias, Map<Long, TypeIdEntity> savedTypeIdEntities) {
        List<ThirdEntity> origen = thirdSource.obtenerPorEmpresaYCorte(
                request.getEntOrigen(), request.getSnapshotCorte());

        for (ThirdEntity original : origen) {
            Long idOriginal = original.getThId();

            // Remap FK typeId — usar entidad manejada por JPA para evitar TransientPropertyValueException
            TypeIdEntity nuevaTypeId = null;
            if (original.getTypeId() != null) {
                Long originalTypeIdId = original.getTypeId().getId();
                nuevaTypeId = savedTypeIdEntities.get(originalTypeIdId);
                if (nuevaTypeId == null) {
                    advertencias.add("Third id=" + idOriginal + ": typeId=" +
                            originalTypeIdId + " sin equivalencia, se insertará con typeId null");
                    log.warn("Third id={}: typeId={} sin equivalencia en el mapper",
                            idOriginal, originalTypeIdId);
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
            mapper.registrar("third", idOriginal, guardado.getThId());
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
            Long nuevoThId = mapper.resolverNuevoId("third", original.getThId());
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

    // -------------------------------------------------------------------------
    // MODO BACKUP — Exportar datos de la empresa origen
    // -------------------------------------------------------------------------

    /**
     * Exporta todos los datos de terceros de la empresa origen como un snapshot JSON.
     * Retorna el mapa de datos en datosExportados para que el orquestador lo distribuya.
     */
    private CopyPhaseResponseDto ejecutarExportacion(CopyPhaseRequestDto request) {
        log.info("Modo BACKUP — exportando datos de entOrigen={}", request.getEntOrigen());

        // TypeId
        List<TypeIdEntity> typeIds = typeIdSource.obtenerPorEmpresaYCorte(
                request.getEntOrigen(), request.getSnapshotCorte());
        List<Map<String, Object>> typeIdsData = new ArrayList<>();
        for (TypeIdEntity e : typeIds) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", e.getId());
            m.put("tiId", e.getTiId());
            m.put("tiName", e.getTiName());
            m.put("tientId", e.getTientId());
            m.put("status", e.getStatus());
            m.put("classification", e.getClassification() != null ? e.getClassification().name() : null);
            typeIdsData.add(m);
        }

        // ThirdType
        List<ThirdTypeEntity> thirdTypes = thirdTypeSource.obtenerPorEmpresaYCorte(
                request.getEntOrigen(), request.getSnapshotCorte());
        List<Map<String, Object>> thirdTypesData = new ArrayList<>();
        for (ThirdTypeEntity e : thirdTypes) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("ttId", e.getTtId());
            m.put("ttName", e.getTtName());
            m.put("ttentId", e.getTtentId());
            m.put("status", e.getStatus());
            thirdTypesData.add(m);
        }

        // Third
        List<ThirdEntity> thirds = thirdSource.obtenerPorEmpresaYCorte(
                request.getEntOrigen(), request.getSnapshotCorte());
        List<Map<String, Object>> thirdsData = new ArrayList<>();
        for (ThirdEntity e : thirds) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("thId", e.getThId());
            m.put("entId", e.getEntId());
            m.put("typeIdFk", e.getTypeId() != null ? e.getTypeId().getId() : null);
            m.put("personType", e.getPersonType() != null ? e.getPersonType().name() : null);
            m.put("names", e.getNames());
            m.put("lastNames", e.getLastNames());
            m.put("socialReason", e.getSocialReason());
            m.put("gender", e.getGender());
            m.put("idNumber", e.getIdNumber());
            m.put("verificationNumber", e.getVerificationNumber());
            m.put("state", e.getState());
            m.put("country", e.getCountry());
            m.put("province", e.getProvince());
            m.put("city", e.getCity());
            m.put("address", e.getAddress());
            m.put("phoneNumber", e.getPhoneNumber());
            m.put("email", e.getEmail());
            m.put("usageCount", e.getUsageCount());
            thirdsData.add(m);
        }

        // ThirdsAndTypes
        List<ThirdsAndTypesEntity> thirdsAndTypes = thirdsAndTypesSource.obtenerPorEmpresa(
                request.getEntOrigen());
        List<Map<String, Object>> thirdsAndTypesData = new ArrayList<>();
        for (ThirdsAndTypesEntity e : thirdsAndTypes) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("thId", e.getThId());
            m.put("ttId", e.getTtId());
            thirdsAndTypesData.add(m);
        }

        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("typeIds", typeIdsData);
        snapshot.put("thirdTypes", thirdTypesData);
        snapshot.put("thirds", thirdsData);
        snapshot.put("thirdsAndTypes", thirdsAndTypesData);

        int total = typeIds.size() + thirdTypes.size() + thirds.size() + thirdsAndTypes.size();
        log.info("BACKUP completado — {} registros exportados de entOrigen={}", total, request.getEntOrigen());

        return CopyPhaseResponseDto.builder()
                .estado("COMPLETADO")
                .registrosProcesados(total)
                .equivalenciasGeneradas(Collections.emptyList())
                .mensaje("Modo BACKUP — " + total + " registros exportados de " + request.getEntOrigen())
                .advertencias(Collections.emptyList())
                .datosExportados(snapshot)
                .build();
    }

    // -------------------------------------------------------------------------
    // MODO RESTORE — Importar datos desde snapshot JSON en empresa destino
    // -------------------------------------------------------------------------

    /**
     * Importa el snapshot de datos de terceros hacia la empresa destino.
     * Implementa idempotencia y logging igual que el flujo DUPLICATE.
     */
    @SuppressWarnings("unchecked")
    private CopyPhaseResponseDto ejecutarImportacion(CopyPhaseRequestDto request) {
        log.info("Modo RESTORE — importando datos en entDestino={}", request.getEntDestino());

        // Validación básica
        if (request.getEntDestino() == null || request.getEntDestino().isBlank()) {
            throw new IllegalArgumentException(
                    "Modo RESTORE requiere entDestino — no puede estar vacío");
        }

        String idProcesoStr = request.getIdProceso().toString();

        // Idempotencia: si ya existe un log para este proceso y fase, retornar resultado previo
        Optional<CopyJobLog> logExistente = logRepo.buscarPorIdProcesoYFase(idProcesoStr, request.getFase());
        if (logExistente.isPresent()) {
            log.info("Proceso idempotente (RESTORE): idProceso={}, fase={} ya fue ejecutado con estado={}",
                    idProcesoStr, request.getFase(), logExistente.get().getEstado());
            return construirResponseDesdeLog(logExistente.get());
        }

        // Registrar inicio
        CopyJobLog logInicio = CopyJobLog.builder()
                .idProceso(request.getIdProceso())
                .fase(request.getFase())
                .modulo(MODULO)
                .estado(CopyEstado.EN_PROCESO)
                .fechaInicio(Instant.now())
                .build();

        Map<String, Object> datos = (Map<String, Object>) request.getDatosImportados();
        List<String> advertencias = new ArrayList<>();
        CopyEquivalenceMapper equivalenceMapper = new CopyEquivalenceMapper();
        Map<Long, TypeIdEntity> savedTypeIdEntities = new LinkedHashMap<>();
        int totalRegistros = 0;

        try {
            // Paso 1: Importar TypeId — llena savedTypeIdEntities con entidades manejadas por JPA
            totalRegistros += importarTypeId(datos, request, equivalenceMapper, savedTypeIdEntities);

            // Paso 2: Importar ThirdType
            totalRegistros += importarThirdType(datos, request, equivalenceMapper);

            // Paso 3: Importar Third con remap FK typeId usando entidades manejadas
            totalRegistros += importarThirds(datos, request, equivalenceMapper, advertencias, savedTypeIdEntities);

            // Paso 4: Importar ThirdsAndTypes con remap FKs
            totalRegistros += importarThirdsAndTypes(datos, equivalenceMapper, advertencias);

        } catch (Exception ex) {
            log.error("Error en RESTORE de thirds idProceso={} fase={}: {}",
                    idProcesoStr, request.getFase(), ex.getMessage(), ex);
            throw ex;
        } finally {
            TenantContext.clear();
        }

        CopyEstado estadoFinal = advertencias.isEmpty()
                ? CopyEstado.COMPLETADO
                : CopyEstado.COMPLETADO_CON_ADVERTENCIAS;

        List<CopyEquivalencia> equivalencias = equivalenceMapper.toList();

        CopyJobLog logFin = CopyJobLog.builder()
                .idProceso(request.getIdProceso())
                .fase(request.getFase())
                .modulo(MODULO)
                .estado(estadoFinal)
                .registrosProcesados(totalRegistros)
                .equivalenciasGeneradas(equivalencias.size())
                .advertencias(advertencias)
                .mensaje("Importación de terceros completada (RESTORE)")
                .fechaInicio(logInicio.getFechaInicio())
                .fechaFin(Instant.now())
                .build();
        logRepo.guardar(logFin);

        return CopyPhaseResponseDto.builder()
                .estado(estadoFinal.name())
                .registrosProcesados(totalRegistros)
                .equivalenciasGeneradas(mapearEquivalenciasDto(equivalencias))
                .mensaje(logFin.getMensaje())
                .advertencias(advertencias)
                .build();
    }

    @SuppressWarnings("unchecked")
    private int importarTypeId(Map<String, Object> datos, CopyPhaseRequestDto request,
                               CopyEquivalenceMapper mapper, Map<Long, TypeIdEntity> savedTypeIdEntities) {
        List<Map<String, Object>> lista = (List<Map<String, Object>>) datos.getOrDefault("typeIds", Collections.emptyList());
        for (Map<String, Object> row : lista) {
            Long idOriginal = toLong(row.get("id"));
            String classificationStr = toStr(row.get("classification"));
            com.thirdsmanagement.thirds.domain.model.PersonClassification classification =
                    com.thirdsmanagement.thirds.domain.model.PersonClassification.fromCode(classificationStr);

            TypeIdEntity nuevo = TypeIdEntity.builder()
                    .tiId(toStr(row.get("tiId")))
                    .tiName(toStr(row.get("tiName")))
                    .tientId(request.getEntDestino())
                    .status(toBool(row.get("status")))
                    .classification(classification)
                    .build();
            TypeIdEntity guardado = typeIdTarget.guardar(nuevo);
            if (idOriginal != null) {
                mapper.registrar("type_id", idOriginal, guardado.getId());
                // Guardar la entidad manejada por JPA para usarla como FK en importarThirds
                savedTypeIdEntities.put(idOriginal, guardado);
            }
        }
        return lista.size();
    }

    @SuppressWarnings("unchecked")
    private int importarThirdType(Map<String, Object> datos, CopyPhaseRequestDto request,
                                  CopyEquivalenceMapper mapper) {
        List<Map<String, Object>> lista = (List<Map<String, Object>>) datos.getOrDefault("thirdTypes", Collections.emptyList());
        for (Map<String, Object> row : lista) {
            Long idOriginal = toLong(row.get("ttId"));
            ThirdTypeEntity nuevo = ThirdTypeEntity.builder()
                    .ttName(toStr(row.get("ttName")))
                    .ttentId(request.getEntDestino())
                    .status(toBool(row.get("status")))
                    .build();
            ThirdTypeEntity guardado = thirdTypeTarget.guardar(nuevo);
            if (idOriginal != null) {
                mapper.registrar("third_type", idOriginal, guardado.getTtId());
            }
        }
        return lista.size();
    }

    @SuppressWarnings("unchecked")
    private int importarThirds(Map<String, Object> datos, CopyPhaseRequestDto request,
                               CopyEquivalenceMapper mapper, List<String> advertencias,
                               Map<Long, TypeIdEntity> savedTypeIdEntities) {
        List<Map<String, Object>> lista = (List<Map<String, Object>>) datos.getOrDefault("thirds", Collections.emptyList());
        for (Map<String, Object> row : lista) {
            Long idOriginal = toLong(row.get("thId"));

            // Remap FK typeId — usar entidad manejada por JPA para evitar TransientPropertyValueException
            TypeIdEntity nuevaTypeId = null;
            Long typeIdFk = toLong(row.get("typeIdFk"));
            if (typeIdFk != null) {
                nuevaTypeId = savedTypeIdEntities.get(typeIdFk);
                if (nuevaTypeId == null) {
                    advertencias.add("Third original id=" + idOriginal + ": typeIdFk=" + typeIdFk +
                            " sin equivalencia, se insertará con typeId null");
                    log.warn("Third original id={}: typeIdFk={} sin equivalencia en el mapper",
                            idOriginal, typeIdFk);
                }
            }

            String personTypeStr = toStr(row.get("personType"));
            com.thirdsmanagement.thirds.domain.enums.ePersonType personType = null;
            if (personTypeStr != null) {
                try {
                    personType = com.thirdsmanagement.thirds.domain.enums.ePersonType.valueOf(personTypeStr);
                } catch (IllegalArgumentException e) {
                    personType = com.thirdsmanagement.thirds.domain.enums.ePersonType.fromCode(personTypeStr);
                }
            }

            ThirdEntity nuevo = ThirdEntity.builder()
                    .entId(request.getEntDestino())
                    .typeId(nuevaTypeId)
                    .personType(personType)
                    .names(toStr(row.get("names")))
                    .lastNames(toStr(row.get("lastNames")))
                    .socialReason(toStr(row.get("socialReason")))
                    .gender(toStr(row.get("gender")))
                    .idNumber(toLong(row.get("idNumber")))
                    .verificationNumber(toLong(row.get("verificationNumber")))
                    .state(toBool(row.get("state")))
                    .country(toStr(row.get("country")))
                    .province(toStr(row.get("province")))
                    .city(toStr(row.get("city")))
                    .address(toStr(row.get("address")))
                    .phoneNumber(toStr(row.get("phoneNumber")))
                    .email(toStr(row.get("email")))
                    .usageCount(0)
                    .build();

            ThirdEntity guardado = thirdTarget.guardar(nuevo);
            if (idOriginal != null) {
                mapper.registrar("third", idOriginal, guardado.getThId());
            }
        }
        return lista.size();
    }

    @SuppressWarnings("unchecked")
    private int importarThirdsAndTypes(Map<String, Object> datos, CopyEquivalenceMapper mapper,
                                       List<String> advertencias) {
        List<Map<String, Object>> lista = (List<Map<String, Object>>) datos.getOrDefault("thirdsAndTypes", Collections.emptyList());
        int copiadas = 0;
        for (Map<String, Object> row : lista) {
            Long nuevoThId = mapper.resolverNuevoId("third", toLong(row.get("thId")));
            Long nuevoTtId = mapper.resolverNuevoId("third_type", toLong(row.get("ttId")));

            if (nuevoThId == null) {
                advertencias.add("ThirdsAndTypes thId=" + row.get("thId") +
                        " sin equivalencia en thirds, relación omitida (RESTORE)");
                log.warn("ThirdsAndTypes thId={} sin equivalencia (RESTORE), relación omitida", row.get("thId"));
                continue;
            }
            if (nuevoTtId == null) {
                advertencias.add("ThirdsAndTypes ttId=" + row.get("ttId") +
                        " sin equivalencia en third_type, relación omitida (RESTORE)");
                log.warn("ThirdsAndTypes ttId={} sin equivalencia (RESTORE), relación omitida", row.get("ttId"));
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

    // -------------------------------------------------------------------------
    // Helpers de conversión de tipos (para deserialización desde Object/Map)
    // -------------------------------------------------------------------------

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof Number n) return n.longValue();
        return null;
    }

    private String toStr(Object v) {
        return v != null ? v.toString() : null;
    }

    private boolean toBool(Object v) {
        return v instanceof Boolean b && b;
    }

    /**
     * Construye el DTO de respuesta a partir de un log previo (idempotencia).
     */
    private CopyPhaseResponseDto construirResponseDesdeLog(CopyJobLog log) {
        return CopyPhaseResponseDto.builder()
                .estado(log.getEstado().name())
                .registrosProcesados(log.getRegistrosProcesados())
                .equivalenciasGeneradas(Collections.emptyList())
                .mensaje(log.getMensaje())
                .advertencias(log.getAdvertencias() != null ? log.getAdvertencias() : Collections.emptyList())
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
