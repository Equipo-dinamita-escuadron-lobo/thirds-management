package com.thirdsmanagement.thirds.infrastructure.audit.aspect;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.audit.annotation.Auditable;
import com.thirdsmanagement.thirds.infrastructure.audit.annotation.OperationType;
import com.thirdsmanagement.thirds.infrastructure.audit.builder.AuditEventBuilder;
import com.thirdsmanagement.thirds.infrastructure.audit.builder.OperationEventDto;
import com.thirdsmanagement.thirds.infrastructure.audit.publisher.AuditEventPublisher;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class AuditAspect {

    private final AuditEventBuilder auditEventBuilder;
    private final AuditEventPublisher auditEventPublisher;
    @Lazy
    private final ThirdOutputPort thirdOutputPort;
    @Lazy
    private final IdOutputPort idOutputPort;

    public AuditAspect(AuditEventBuilder auditEventBuilder, AuditEventPublisher auditEventPublisher,
            @Lazy ThirdOutputPort thirdOutputPort, @Lazy IdOutputPort idOutputPort) {
        this.auditEventBuilder = auditEventBuilder;
        this.auditEventPublisher = auditEventPublisher;
        this.thirdOutputPort = thirdOutputPort;
        this.idOutputPort = idOutputPort;
    }

    @Around("@annotation(auditable)")
    public Object audit(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {

        Object[] args = joinPoint.getArgs();

        Map<String, Object> beforeData = captureBeforeData(auditable, args);

        // Ejecutamos el metodo de negocio, este si o si se debe ejecutar
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            // Si el negocio falla no auditamos
            throw ex;
        }

        try {
            Map<String, Object> diff = null;
            if (auditable.operationType() == OperationType.UPDATE) {
                Map<String, Object> afterData = fetchCurrentState(auditable, args);
                diff = buildDiff(beforeData, afterData);
            }

            OperationType resolvedType = resolveFinalOperationType(auditable, beforeData, diff);
            String enterpriseId = resolveEnterpriseId(auditable, args, result, beforeData);
            String registerId = resolveRegisterId(auditable, args, result, beforeData);
            Map<String, Object> dataObject = buildDataObject(resolvedType, args, result, beforeData, diff);

            OperationEventDto dto = auditEventBuilder.build(
                    auditable,
                    resolvedType,
                    enterpriseId,
                    registerId,
                    dataObject);

            auditEventPublisher.publish(dto);
        } catch (Exception e) {
            log.error("Error construyendo evento de auditoría [{}]: {}",
                    auditable.operationType(), e.getMessage(), e);
        }

        return result;
    }

    private Map<String, Object> captureBeforeData(Auditable auditable, Object[] args) {
        try {
            return switch (auditable.operationType()) {
                case UPDATE, INACTIVATE, DELETE -> fetchCurrentState(auditable, args);
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> fetchCurrentState(Auditable auditable, Object[] args) {
        return switch (auditable.affectedTable()) {
            case "THIRD" -> {
                Long id = (args[0] instanceof Long) ? (Long) args[0] : ((Third) args[0]).getThId();
                String entId = (args[0] instanceof Long) ? (String) args[1] : ((Third) args[0]).getEntId();
                yield thirdOutputPort.getThirdById(id, entId)
                        .map(this::thirdToMap)
                        .orElse(null);
            }
            case "TYPE_ID" -> {
                Long id = (args[0] instanceof Long) ? (Long) args[0] : ((TypeId) args[0]).getId();
                TypeId found = idOutputPort.getTypeIdById(id);
                yield found != null ? typeIdToMap(found) : null;
            }
            case "THIRD_TYPE" -> {
                Long id = (args[0] instanceof Long) ? (Long) args[0] : ((ThirdType) args[0]).getThirdTypeId();
                ThirdType found = idOutputPort.getThirdTypeById(id);
                yield found != null ? thirdTypeToMap(found) : null;
            }
            default -> null;
        };
    }

    private OperationType resolveFinalOperationType(
            Auditable auditable,
            Map<String, Object> beforeData,
            Map<String, Object> diff) {
        if (auditable.operationType() == OperationType.UPDATE
                && diff != null && diff.size() == 1 && diff.containsKey("state")) {
            Object stateObj = diff.get("state");
            if (stateObj instanceof Map<?, ?> stateMap) {
                Object after = stateMap.get("after");
                if (after instanceof Boolean afterState) {
                    return afterState ? OperationType.ACTIVATE : OperationType.INACTIVATE;
                }
            }
        }
        if (auditable.operationType() == OperationType.INACTIVATE && beforeData != null) {
            boolean wasActive = Boolean.TRUE.equals(beforeData.get("state"));
            return wasActive ? OperationType.INACTIVATE : OperationType.ACTIVATE;
        }
        return auditable.operationType();
    }

    private Map<String, Object> buildDataObject(OperationType resolvedType,
            Object[] args, Object result,
            Map<String, Object> beforeData, Map<String, Object> diff) {
        return switch (resolvedType) {
            case CREATE -> {
                Map<String, Object> data = new LinkedHashMap<>();
                if (result instanceof Third t)
                    data.put("entity", thirdToMap(t));
                if (result instanceof TypeId ti)
                    data.put("entity", typeIdToMap(ti));
                if (result instanceof ThirdType tt)
                    data.put("entity", thirdTypeToMap(tt));
                yield data;
            }
            case UPDATE -> Map.of("changes", diff);
            case ACTIVATE, INACTIVATE -> {
                if (beforeData != null) {
                    Map<String, Object> beforeState = Map.of("state", beforeData.get("state"));
                    Map<String, Object> afterState = Map.of("state", !((Boolean) beforeData.get("state")));
                    yield Map.of("changes", buildDiff(beforeState, afterState));
                }
                yield Map.of();
            }
            case DELETE -> Map.of("entity", beforeData != null ? beforeData : Map.of("id", args[0]));
        };
    }

    private Map<String, Object> buildDiff(Map<String, Object> before, Map<String, Object> after) {
        Map<String, Object> diff = new LinkedHashMap<>();
        if (before == null || after == null)
            return diff;

        after.forEach((key, afterValue) -> {
            Object beforeValue = before.get(key);
            if (!Objects.equals(beforeValue, afterValue)) {
                Map<String, Object> change = new LinkedHashMap<>();
                change.put("before", beforeValue);
                change.put("after", afterValue);
                diff.put(key, change);
            }
        });
        return diff;
    }

    private String resolveEnterpriseId(Auditable auditable, Object[] args, Object result,
            Map<String, Object> beforeData) {
        return switch (auditable.operationType()) {
            case CREATE -> {
                if (result instanceof Third t)
                    yield t.getEntId();
                if (result instanceof TypeId ti)
                    yield ti.getEntId();
                if (result instanceof ThirdType tt)
                    yield tt.getEntId();
                yield "UNKNOWN";
            }
            default -> beforeData != null ? (String) beforeData.get("entId") : "UNKNOWN";
        };
    }

    private String resolveRegisterId(Auditable auditable, Object[] args, Object result,
            Map<String, Object> beforeData) {
        return switch (auditable.operationType()) {
            case CREATE -> {
                if (result instanceof Third t)
                    yield String.valueOf(t.getThId());
                if (result instanceof TypeId ti)
                    yield String.valueOf(ti.getId());
                if (result instanceof ThirdType tt)
                    yield String.valueOf(tt.getThirdTypeId());
                yield "UNKNOWN";
            }
            default -> beforeData != null ? String.valueOf(beforeData.get("id")) : "UNKNOWN";
        };
    }

    private Map<String, Object> thirdToMap(Third t) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", t.getThId());
        map.put("entId", t.getEntId());
        map.put("idNumber", t.getIdNumber());
        map.put("verificationNumber", t.getVerificationNumber());
        map.put("personType", t.getPersonType() != null ? t.getPersonType().name() : null);
        map.put("names", t.getNames());
        map.put("lastNames", t.getLastNames());
        map.put("socialReason", t.getSocialReason());
        map.put("gender", t.getGender() != null ? t.getGender().name() : null);
        map.put("email", t.getEmail());
        map.put("phoneNumber", t.getPhoneNumber());
        map.put("address", t.getAddress());
        map.put("typeIdId", t.getTypeId() != null ? t.getTypeId().getId() : null);
        map.put("country", t.getCountry().getCountryName() != null ? t.getCountry().getCountryName() : null);
        map.put("province", t.getProvince().getStateName() != null ? t.getProvince().getStateName() : null);
        map.put("city", t.getCity().getCityName() != null ? t.getCity().getCityName() : null);
        map.put("state", t.getState());

        // Elimina cualquier entrada con valor null antes de retornar
        map.entrySet().removeIf(entry -> entry.getValue() == null);
        return map;
    }

    private Map<String, Object> typeIdToMap(TypeId ti) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", ti.getId());
        map.put("entId", ti.getEntId());
        map.put("typeId", ti.getTypeId());
        map.put("typeIdname", ti.getTypeIdname());
        map.put("classification", ti.getClassification() != null ? ti.getClassification().name() : null);
        map.put("state", ti.getStatus());

        map.entrySet().removeIf(entry -> entry.getValue() == null);
        return map;
    }

    private Map<String, Object> thirdTypeToMap(ThirdType tt) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", tt.getThirdTypeId());
        map.put("entId", tt.getEntId());
        map.put("thirdTypeName", tt.getThirdTypeName());
        map.put("state", tt.getStatus());

        map.entrySet().removeIf(entry -> entry.getValue() == null);
        return map;
    }

}