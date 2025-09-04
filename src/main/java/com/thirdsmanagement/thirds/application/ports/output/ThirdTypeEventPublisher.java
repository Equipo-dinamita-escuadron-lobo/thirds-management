package com.thirdsmanagement.thirds.application.ports.output;

import com.thirdsmanagement.thirds.domain.event.ThirdTypeCreatedEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdTypeUpdatedEvent;

/**
 * Interfaz que define los métodos para publicar eventos de tipos de terceros.
 */
public interface ThirdTypeEventPublisher {
    /**
     * Publica un evento de creación de tipo de tercero.
     * @param event Evento de creación de tipo de tercero.
     */	
    void publishThirdTypeCreatedEvent(ThirdTypeCreatedEvent event);
    
    /**
     * Publica un evento de actualización de tipo de tercero.
     * @param event Evento de actualización de tipo de tercero.
     */
    void publishThirdTypeUpdatedEvent(ThirdTypeUpdatedEvent event);
}
