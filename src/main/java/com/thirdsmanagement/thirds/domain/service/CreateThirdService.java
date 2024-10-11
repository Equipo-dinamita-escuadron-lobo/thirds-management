package com.thirdsmanagement.thirds.domain.service;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.event.ThirdCreatedEvent;
import com.thirdsmanagement.thirds.domain.model.Third;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CreateThirdService implements CreateThirdUseCase{

    private final ThirdOutputPort thirdOutputPort;
    private final ThirdEventPublisher thirdEventPublisher;

    @Override
    public Third createThird(Third third) {
        third = thirdOutputPort.saveThird(third);
        thirdEventPublisher.publishThirdCreatedEvent(new ThirdCreatedEvent(third.getThId()));
        return third;
    }
}
