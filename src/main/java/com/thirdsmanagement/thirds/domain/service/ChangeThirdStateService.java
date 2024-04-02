package com.thirdsmanagement.thirds.domain.service;

import com.thirdsmanagement.thirds.application.ports.input.ChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.event.ThirdStateUpdateEvent;
import com.thirdsmanagement.thirds.domain.model.Third;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChangeThirdStateService implements  ChangeThirdStateUseCase{

    private final ThirdOutputPort thirdOutputPort;
    private final ThirdEventPublisher thirdEventPublisher;

    @Override
    public boolean changeThirdState(Third third) {
        boolean result = thirdOutputPort.changeThirdState(third);
        thirdEventPublisher.publishThirdStateUpdateEvent(new ThirdStateUpdateEvent(third.getThId(),third.getState()));
        return result;
    }
    
}
