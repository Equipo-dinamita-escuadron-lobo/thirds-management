package com.thirdsmanagement.thirds.infrastructure.adapters.output.eventpublisher;

import org.springframework.context.ApplicationEventPublisher;

import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.domain.event.ThirdCreatedEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdStateUpdateEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdUpdateEvent;

import lombok.RequiredArgsConstructor;

/**
 * Clase adaptador de publicación de eventos para el tercero.
 * Implementa la interfaz {@link ThirdEventPublisher}.
 * Utiliza {@link ApplicationEventPublisher} para publicar eventos.
 */
@RequiredArgsConstructor
public class ThirdEventPublisherAdapter implements ThirdEventPublisher {
    /**
     * Publicador de eventos de la aplicación.
     */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Publica un evento de creación de tercero.
     * @param event evento de creación de tercero.
     */
    @Override
    public void publishThirdCreatedEvent(ThirdCreatedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    /**
     * Publica un evento de actualización de estado de tercero.
     * @param event evento de actualización de estado de tercero.
     */
    @Override
    public void publishThirdStateUpdateEvent(ThirdStateUpdateEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    /**
     * Publica un evento de actualización de tercero.
     * @param event evento de actualización de tercero.
     */
    @Override
    public void publishThirdUpdateEvent(ThirdUpdateEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
