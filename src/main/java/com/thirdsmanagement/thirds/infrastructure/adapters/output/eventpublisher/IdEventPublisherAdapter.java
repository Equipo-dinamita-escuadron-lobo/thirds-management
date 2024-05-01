package com.thirdsmanagement.thirds.infrastructure.adapters.output.eventpublisher;

import org.springframework.context.ApplicationEventPublisher;

import com.thirdsmanagement.thirds.application.ports.output.ThirdTypeEventPublisher;
import com.thirdsmanagement.thirds.domain.event.ThirdTypeCreatedEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IdEventPublisherAdapter implements ThirdTypeEventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishThirdTypeCreatedEvent(ThirdTypeCreatedEvent event){
        applicationEventPublisher.publishEvent(event);
    }
    
}
