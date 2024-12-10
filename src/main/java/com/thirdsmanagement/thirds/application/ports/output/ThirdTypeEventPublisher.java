package com.thirdsmanagement.thirds.application.ports.output;

import com.thirdsmanagement.thirds.domain.event.ThirdTypeCreatedEvent;

/**
 * Interfaz que define el método para publicar eventos de creación de tipos de terceros.
 */
public interface ThirdTypeEventPublisher {
    /**
     * Publica un evento de creación de tipo de tercero.
     * @param event Evento de creación de tipo de tercero.
     */	
    void publishThirdTypeCreatedEvent(ThirdTypeCreatedEvent event);
}
