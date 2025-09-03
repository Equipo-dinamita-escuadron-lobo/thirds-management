package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.commons.exceptions.thirds.ThirdStateNotChanged;
import com.thirdsmanagement.thirds.application.ports.input.ChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.event.ThirdStateUpdateEvent;

import lombok.AllArgsConstructor;

/**
 * Este servicio maneja la lógica de negocio para actualizar el estado de una entidad de terceros
 * y publicar el evento correspondiente.
 */

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
