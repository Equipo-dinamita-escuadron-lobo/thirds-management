package com.thirdsmanagement.thirds.application.ports.output;

import com.thirdsmanagement.thirds.domain.event.ThirdCreatedEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdDeleteEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdStateUpdateEvent;
import com.thirdsmanagement.thirds.domain.event.ThirdUpdateEvent;

public interface ThirdEventPublisher {
    
    void publishThirdCreatedEvent(ThirdCreatedEvent event);

    void publishThirdStateUpdateEvent(ThirdStateUpdateEvent event);

    void publishThirdUpdateEvent(ThirdUpdateEvent event);

    void publishThirdDeleteEvent(ThirdDeleteEvent event);
}
