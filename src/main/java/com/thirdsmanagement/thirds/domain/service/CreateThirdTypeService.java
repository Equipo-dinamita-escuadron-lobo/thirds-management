package com.thirdsmanagement.thirds.domain.service;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.ThirdTypeEventPublisher;
import com.thirdsmanagement.thirds.domain.event.ThirdTypeCreatedEvent;
import com.thirdsmanagement.thirds.domain.model.ThirdType;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateThirdTypeService implements CreateThirdTypeUseCase {
    
    private final IdOutputPort idOutputPort;
    //private final ThirdTypeEventPublisher thirdTypeEventPublisher;
    @Override
    public ThirdType createThirdType(ThirdType thirdType) {
        thirdType = idOutputPort.saveThirdType(thirdType);
        //thirdTypeEventPublisher.publishThirdTypeCreatedEvent(new ThirdTypeCreatedEvent(thirdType.getThirdTypeId()));
        return thirdType;
    }
    

    
}
