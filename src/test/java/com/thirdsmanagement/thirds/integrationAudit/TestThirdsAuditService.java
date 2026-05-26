package com.thirdsmanagement.thirds.integrationAudit;

import org.springframework.stereotype.Service;

import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.audit.annotation.Auditable;
import com.thirdsmanagement.thirds.infrastructure.audit.annotation.OperationType;

@Service
public class TestThirdsAuditService {

    @Auditable(operationType = OperationType.CREATE, affectedTable = "THIRD")
    public Third createThird(Third third) {
        return third;
    }

    @Auditable(operationType = OperationType.UPDATE, affectedTable = "TYPE_ID")
    public TypeId updateTypeId(TypeId typeId) {
        return typeId;
    }

    @Auditable(operationType = OperationType.DELETE, affectedTable = "THIRD_TYPE")
    public void deleteThirdType(Long id, String enterpriseId) {
    }

    @Auditable(operationType = OperationType.INACTIVATE, affectedTable = "THIRD", idArgIndex = 0, enterpriseIdArgIndex = 1)
    public void changeThirdState(Long id, String enterpriseId) {
    }
}
