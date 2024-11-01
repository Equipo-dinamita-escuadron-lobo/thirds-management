package com.thirdsmanagement.thirds.infrastructure.adapters.input.eventlistener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.thirdsmanagement.thirds.domain.event.ThirdCreatedEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdStateUpdateEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ThirdEventListenerAdapter {
    
    @EventListener
    public void handleCreate(ThirdCreatedEvent event){
        log.info("Third created with id " + event.getThId() + " at "+event.getDate());
    }

    @EventListener
    public void handleChangeState(ThirdStateUpdateEvent event){
        log.info("The third with id "+event.getThId() +" has had its status changed at "+event.getDate());
    }
}
