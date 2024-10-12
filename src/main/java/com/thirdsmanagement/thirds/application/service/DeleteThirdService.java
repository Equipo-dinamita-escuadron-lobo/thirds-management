package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.DeleteThirdUserCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.event.ThirdDeleteEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DeleteThirdService implements DeleteThirdUserCase {

    private final ThirdOutputPort thirdOutputPort;
    private final ThirdEventPublisher thirdEventPublisher;

    @Override
    public void deleteThirdById(Long id) {
        thirdOutputPort.deleteThirdById(id);
        thirdEventPublisher.publishThirdDeleteEvent(new ThirdDeleteEvent(id));
    }
}
