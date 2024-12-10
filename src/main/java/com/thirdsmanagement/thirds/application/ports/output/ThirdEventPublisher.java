package com.thirdsmanagement.thirds.application.ports.output;

import com.thirdsmanagement.thirds.domain.event.ThirdCreatedEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdStateUpdateEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdUpdateEvent;

/**
 * Interfaz que define los métodos de salida para la publicación de eventos de tercero.
 */
public interface ThirdEventPublisher {
    /**
     * Publica un evento de tercero creado.
     * @param event El evento de tercero creado
     */
    void publishThirdCreatedEvent(ThirdCreatedEvent event);

    /**
     * Publica un evento de actualización de estado de tercero.
     * @param event El evento de actualización de estado de tercero
     */
    void publishThirdStateUpdateEvent(ThirdStateUpdateEvent event);

    /**
     * Publica un evento de actualización de tercero.
     * @param event El evento de actualización de tercero
     */
    void publishThirdUpdateEvent(ThirdUpdateEvent event);
}
