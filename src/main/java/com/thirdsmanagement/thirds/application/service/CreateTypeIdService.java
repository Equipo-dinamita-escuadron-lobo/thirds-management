package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.CreateTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CreateTypeIdService implements CreateTypeIdUseCase {
    
    private final IdOutputPort idOutputPort;

    /**
     * Crea un nuevo tipo de identificación en el sistema.
     * 
     * @param typeId el tipo de identificación a crear
     * @return el tipo de identificación creado con su ID asignado
     * @throws IllegalArgumentException si el tipo de identificación es null o tiene datos inválidos
     */
    @Override
    public TypeId createTypeId(TypeId typeId) {
        if (typeId == null) {
            throw new IllegalArgumentException("El tipo de identificación no puede ser null");
        }
        
        // Guardar el tipo de identificación
        TypeId createdTypeId = idOutputPort.saveTypeId(typeId);
        
        // TODO: Implementar publicación de eventos cuando esté disponible el publisher
        // typeIdEventPublisher.publishTypeIdCreatedEvent(new TypeIdCreatedEvent(createdTypeId.getTypeId()));
        
        return createdTypeId;
    }
    
}
