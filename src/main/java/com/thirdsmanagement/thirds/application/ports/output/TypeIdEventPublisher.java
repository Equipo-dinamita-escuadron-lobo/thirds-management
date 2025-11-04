package com.thirdsmanagement.thirds.application.ports.output;

import com.thirdsmanagement.thirds.domain.event.TypeIdCreatedEvent;

/**
 * @brief Puerto de salida para publicación de eventos de tipos de identificación
 *
 * Define el contrato para publicar eventos relacionados
 * con operaciones de creación de tipos de identificación.
 */
public interface TypeIdEventPublisher {
    /**
     * @brief Publica evento de creación de tipo de identificación
     * @param event Evento de creación de tipo de identificación
     */
    void publishTypeIdCreatedEvent(TypeIdCreatedEvent event);
} 
