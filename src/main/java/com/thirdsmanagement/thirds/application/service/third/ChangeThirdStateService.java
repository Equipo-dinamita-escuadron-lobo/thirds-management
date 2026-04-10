package com.thirdsmanagement.thirds.application.service.third;

import com.thirdsmanagement.thirds.application.ports.input.ChangeThirdStateUseCase; 
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdStateNotChanged;
import com.thirdsmanagement.thirds.infrastructure.audit.annotation.Auditable;
import com.thirdsmanagement.thirds.infrastructure.audit.annotation.OperationType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeThirdStateService implements ChangeThirdStateUseCase {

    private final ThirdOutputPort thirdOutputPort;

    @Auditable(operationType = OperationType.INACTIVATE, affectedTable = "THIRD", idArgIndex = 0, enterpriseIdArgIndex = 1)
    @Override
    public boolean changeThirdState(Long thId, String entId) {
        boolean result = thirdOutputPort.changeThirdState(thId, entId);

        if (!result) {
            throw new ThirdStateNotChanged("El estado no se pudo cambiar debido a un error interno");
        }

        return result;
    }

}
