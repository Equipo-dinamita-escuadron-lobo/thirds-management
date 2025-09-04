package com.thirdsmanagement.thirds.infrastructure.adapters.output.eventpublisher;

import org.springframework.context.ApplicationEventPublisher;

import com.thirdsmanagement.thirds.application.ports.output.ThirdTypeEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.TypeIdEventPublisher;
import com.thirdsmanagement.thirds.domain.event.ThirdTypeCreatedEvent;
import com.thirdsmanagement.thirds.domain.event.TypeIdCreatedEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Clase adaptador de publicación de eventos para el tipo de identificación.
 * Implementa la interfaz {@link TypeIdEventPublisher}.
 * Utiliza {@link ApplicationEventPublisher} para publicar eventos.
 */
@Component
@RequiredArgsConstructor
public class IdEventPublisherAdapter implements ThirdTypeEventPublisher, TypeIdEventPublisher {
    /**
     * Publicador de eventos de la aplicación.
     */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Publica un evento de creación de tipo de tercero.
     */
    @Override
    public void publishThirdTypeCreatedEvent(ThirdTypeCreatedEvent event){
        applicationEventPublisher.publishEvent(event);
    }

    /**
     * Publica un evento de creación de tipo de identificación.
     */
    @Override
    public void publishTypeIdCreatedEvent(TypeIdCreatedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
