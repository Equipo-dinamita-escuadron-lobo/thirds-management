package com.thirdsmanagement.thirds.infrastructure.adapters.output.eventpublisher;

import org.springframework.context.ApplicationEventPublisher;

import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.domain.event.ThirdCreatedEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdStateUpdateEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdUpdateEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ThirdEventPublisherAdapter implements ThirdEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishThirdCreatedEvent(ThirdCreatedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishThirdStateUpdateEvent(ThirdStateUpdateEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishThirdUpdateEvent(ThirdUpdateEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
