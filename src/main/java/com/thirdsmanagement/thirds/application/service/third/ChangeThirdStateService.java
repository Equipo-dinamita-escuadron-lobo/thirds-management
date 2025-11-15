package com.thirdsmanagement.thirds.application.service.third;

import com.thirdsmanagement.thirds.application.ports.input.ChangeThirdStateUseCase; 
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdStateNotChanged;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeThirdStateService implements ChangeThirdStateUseCase {

    private final ThirdOutputPort thirdOutputPort;

    @Override
    public boolean changeThirdState(Long thId, String entId) {
        boolean result = thirdOutputPort.changeThirdState(thId, entId);

        if (!result) {
            throw new ThirdStateNotChanged("El estado no se pudo cambiar debido a un error interno");
        }

        return result;
    }

}
