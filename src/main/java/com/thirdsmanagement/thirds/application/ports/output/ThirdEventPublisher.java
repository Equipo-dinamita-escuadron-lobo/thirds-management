package com.thirdsmanagement.thirds.application.ports.output;

import com.thirdsmanagement.thirds.domain.event.ThirdCreatedEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdStateUpdateEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdUpdateEvent;

/**
 * @brief Puerto de salida para publicación de eventos de tercero
 *
 * Define el contrato para publicar eventos relacionados
 * con operaciones de creación, actualización y cambios de estado de terceros.
 */
public interface ThirdEventPublisher {
    /**
     * @brief Publica evento de tercero creado
     * @param event El evento de tercero creado
     */
    void publishThirdCreatedEvent(ThirdCreatedEvent event);

    /**
     * @brief Publica evento de actualización de estado de tercero
     * @param event El evento de actualización de estado de tercero
     */
    void publishThirdStateUpdateEvent(ThirdStateUpdateEvent event);

    /**
     * @brief Publica evento de actualización de tercero
     * @param event El evento de actualización de tercero
     */
    void publishThirdUpdateEvent(ThirdUpdateEvent event);
}
