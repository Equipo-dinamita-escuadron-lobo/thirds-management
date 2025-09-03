package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.UpdateThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.event.ThirdUpdateEvent;
import com.thirdsmanagement.thirds.domain.model.Third;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateThirdService implements UpdateThirdUseCase {

    private final ThirdOutputPort thirdOutputPort;
    private final ThirdEventPublisher thirdEventPublisher;

    /**
     * Actualiza un tercero existente en el sistema.
     * 
     * @param third el tercero con los datos actualizados
     * @return el tercero actualizado
     * @throws IllegalArgumentException si el tercero es null o no tiene ID válido
     */
    @Override
    public Third updateThird(Third third) {
        if (third == null) {
            throw new IllegalArgumentException("El tercero no puede ser null");
        }
        
        if (third.getThId() == null) {
            throw new IllegalArgumentException("El ID del tercero no puede ser null para actualizar");
        }
        
        // Actualizar el tercero
        Third updatedThird = thirdOutputPort.updateThird(third);
        
        // Publicar evento de actualización
        thirdEventPublisher.publishThirdUpdateEvent(new ThirdUpdateEvent(updatedThird.getThId()));

        return updatedThird;
    }
}
