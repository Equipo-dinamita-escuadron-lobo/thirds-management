package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.ChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.event.ThirdStateUpdateEvent;
import com.thirdsmanagement.thirds.domain.exception.ThirdStateNotChanged;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChangeThirdStateService implements  ChangeThirdStateUseCase{

    private final ThirdOutputPort thirdOutputPort;
    private final ThirdEventPublisher thirdEventPublisher;

    @Override
    public boolean changeThirdState(Long thId) {
        boolean result = thirdOutputPort.changeThirdState(thId);
        thirdEventPublisher.publishThirdStateUpdateEvent(new ThirdStateUpdateEvent(thId));

        if(result == false){
            throw new ThirdStateNotChanged("The state has not changed because a internal error occurred");
        }

        return result;
    }
    
}
