package com.thirdsmanagement.thirds.application.ports.output;

import com.thirdsmanagement.thirds.domain.event.ThirdTypeCreatedEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdTypeUpdatedEvent;

/**
 * @brief Puerto de salida para publicación de eventos de tipos de tercero
 *
 * Define el contrato para publicar eventos relacionados
 * con operaciones de creación y actualización de tipos de tercero.
 */
public interface ThirdTypeEventPublisher {
    /**
     * @brief Publica evento de creación de tipo de tercero
     * @param event Evento de creación de tipo de tercero
     */
    void publishThirdTypeCreatedEvent(ThirdTypeCreatedEvent event);

    /**
     * @brief Publica evento de actualización de tipo de tercero
     * @param event Evento de actualización de tipo de tercero
     */
    void publishThirdTypeUpdatedEvent(ThirdTypeUpdatedEvent event);
}
