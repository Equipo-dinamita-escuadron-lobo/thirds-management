package com.thirdsmanagement.thirds.application.ports.output;

import com.thirdsmanagement.thirds.domain.model.Third;

public interface IThirdEventPublisher {
    public void publishThirdUpdatedEvent(Third third);
}
