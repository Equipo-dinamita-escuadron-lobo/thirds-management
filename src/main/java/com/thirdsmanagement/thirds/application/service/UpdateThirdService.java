package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.UpdateThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.event.ThirdUpdateEvent;
import com.thirdsmanagement.thirds.domain.model.Third;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Clase de servicio para actualizar un tercero.
 * Implementa la interfaz {@link UpdateThirdUseCase}.
 * Utiliza {@link ThirdOutputPort} para las operaciones de persistencia y {@link ThirdEventPublisher}
 * para publicar el evento.
 * Este servicio proporciona un método para actualizar un tercero.
 * Después de actualizar el tercero, publica un evento de actualización de tercero.
 */
@Service
@AllArgsConstructor
public class UpdateThirdService implements UpdateThirdUseCase{

    private final ThirdOutputPort thirdOutputPort;
    private final ThirdEventPublisher thirdEventPublisher;

    @Override
    public Third updateThird(Third third) {
        
        Third result = thirdOutputPort.updateThird(third);
         thirdEventPublisher.publishThirdUpdateEvent(new ThirdUpdateEvent(third.getThId()));

        return result;
    }
    
    
}
