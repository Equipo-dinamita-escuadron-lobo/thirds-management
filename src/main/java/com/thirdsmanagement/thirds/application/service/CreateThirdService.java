package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.event.ThirdCreatedEvent;
import com.thirdsmanagement.thirds.domain.model.Third;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateThirdService implements CreateThirdUseCase {

    private final ThirdOutputPort thirdOutputPort;
    private final ThirdEventPublisher thirdEventPublisher;

    /**
     * Crea un nuevo tercero en el sistema.
     * 
     * @param third el tercero a crear
     * @return el tercero creado con su ID asignado
     * @throws IllegalArgumentException si el tercero es null o tiene datos inválidos
     */
    @Override
    public Third createThird(Third third) {
        if (third == null) {
            throw new IllegalArgumentException("El tercero no puede ser null");
        }
        
        // Guardar el tercero y obtener la entidad persistida con ID
        Third createdThird = thirdOutputPort.saveThird(third);
        
        // Publicar evento de creación
        thirdEventPublisher.publishThirdCreatedEvent(new ThirdCreatedEvent(createdThird.getThId()));
        
        return createdThird;
    }
}
