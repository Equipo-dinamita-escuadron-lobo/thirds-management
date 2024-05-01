package com.thirdsmanagement.thirds.application.ports.output;

import com.thirdsmanagement.thirds.domain.event.ThirdTypeCreatedEvent;


public interface ThirdTypeEventPublisher {
    
    void publishThirdTypeCreatedEvent(ThirdTypeCreatedEvent event);
}
