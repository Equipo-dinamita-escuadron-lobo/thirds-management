package com.thirdsmanagement.thirds.application.ports.output;

import com.thirdsmanagement.thirds.domain.event.TypeIdCreatedEvent;

public interface TypeIdEventPublisher {
    void publishTypeIdCreatedEvent(TypeIdCreatedEvent event);
} 
