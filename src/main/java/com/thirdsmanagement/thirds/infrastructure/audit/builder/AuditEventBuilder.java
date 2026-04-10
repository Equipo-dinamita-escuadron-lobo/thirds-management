package com.thirdsmanagement.thirds.infrastructure.audit.builder;

import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Component;

import com.thirdsmanagement.thirds.infrastructure.audit.annotation.Auditable;
import com.thirdsmanagement.thirds.infrastructure.audit.annotation.OperationType;
import com.thirdsmanagement.thirds.infrastructure.security.IJwtUtils;
import com.thirdsmanagement.thirds.infrastructure.security.JwtDecoder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditEventBuilder {

    private final IJwtUtils jwtUtils;
    private final JwtDecoder jwtDecoder;

    public OperationEventDto build(
            Auditable auditable,
            OperationType resolvedOperationType,
            String enterpriseId,
            String registerId,
            Map<String, Object> dataObject) {
        String token = jwtUtils.getToken();
        String userId = jwtDecoder.extractClaim(token, "sub");
        String userName = jwtDecoder.extractClaim(token, "preferred_username");
        String userRole = jwtDecoder.extractPrimaryRole(token);

        return OperationEventDto.builder()
                .enterpriseId(enterpriseId)
                .userId(userId)
                .userName(userName)
                .userRole(userRole)
                .operationType(resolvedOperationType.name())
                .operationAt(Instant.now())
                .moduleName(auditable.moduleName())
                .affectedTable(auditable.affectedTable())
                .registerId(registerId)
                .dataObject(dataObject)
                .build();
    }

}
