package com.thirdsmanagement.thirds.infrastructure.adapters.input.eventlistener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.thirdsmanagement.thirds.domain.event.ThirdCreatedEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdStateUpdateEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase que maneja los eventos de terceros.
 */
@Component
@Slf4j
public class ThirdEventListenerAdapter {
    /**
     * Maneja el evento de creación de un tercero.
     * @param event Evento de creación de un tercero.
     */
    @EventListener
    public void handleCreate(ThirdCreatedEvent event){
        log.info("Third created with id " + event.getThId() + " at "+event.getDate());
    }

    /**
     * Maneja el evento de cambio de estado de un tercero.
     * @param event Evento de cambio de estado de un tercero.
     */
    @EventListener
    public void handleChangeState(ThirdStateUpdateEvent event){
        log.info("The third with id "+event.getThId() +" has had its status changed at "+event.getDate());
    }
}
