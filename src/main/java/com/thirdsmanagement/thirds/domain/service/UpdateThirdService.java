package com.thirdsmanagement.thirds.domain.service;

import com.thirdsmanagement.thirds.application.ports.input.UpdateThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.event.ThirdUpdateEvent;
import com.thirdsmanagement.thirds.domain.model.Third;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateThirdService implements UpdateThirdUseCase{

     private final ThirdOutputPort thirdOutputPort;
      private final ThirdEventPublisher thirdEventPublisher;

    @Override
    public Third updateThird(Third third) {
        
        Third result = thirdOutputPort.updateThird(third);
         thirdEventPublisher.publishThirdUpdateEvent(new ThirdUpdateEvent(third.getThId()));

        return result;
    }
    
    
}
