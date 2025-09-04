package com.thirdsmanagement.thirds.infrastructure.adapters.output.eventpublisher;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.thirdsmanagement.thirds.application.ports.output.ThirdTypeEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.TypeIdEventPublisher;
import com.thirdsmanagement.thirds.domain.event.ThirdTypeCreatedEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdTypeUpdatedEvent;
import com.thirdsmanagement.thirds.domain.event.TypeIdCreatedEvent;

import lombok.RequiredArgsConstructor;

/**
 * Adaptador para la publicación de eventos de identificación.
 */
@Component
@RequiredArgsConstructor
public class IdEventPublisherAdapter implements TypeIdEventPublisher, ThirdTypeEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishTypeIdCreatedEvent(TypeIdCreatedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishThirdTypeCreatedEvent(ThirdTypeCreatedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishThirdTypeUpdatedEvent(ThirdTypeUpdatedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
