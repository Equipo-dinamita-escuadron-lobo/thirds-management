package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.ThirdType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateThirdTypeService implements CreateThirdTypeUseCase {
    
    private final IdOutputPort idOutputPort;
    
    /**
     * Crea un nuevo tipo de tercero en el sistema.
     * 
     * @param thirdType el tipo de tercero a crear
     * @return el tipo de tercero creado con su ID asignado
     * @throws IllegalArgumentException si el tipo de tercero es null o tiene datos inválidos
     */
    @Override
    @Transactional
    public ThirdType createThirdType(ThirdType thirdType) {
        if (thirdType == null) {
            throw new IllegalArgumentException("El tipo de tercero no puede ser null");
        }
        
        // Guardar el tipo de tercero
        ThirdType createdThirdType = idOutputPort.saveThirdType(thirdType);
        
        // TODO: Implementar publicación de eventos cuando esté disponible el publisher
        // thirdTypeEventPublisher.publishThirdTypeCreatedEvent(new ThirdTypeCreatedEvent(createdThirdType.getThirdTypeId()));
        
        return createdThirdType;
    }
    

    
}
