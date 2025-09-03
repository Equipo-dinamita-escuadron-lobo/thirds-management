package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.CreateTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.TypeIdEventPublisher;
import com.thirdsmanagement.thirds.domain.event.TypeIdCreatedEvent;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio para la creación de un tipo de identificacion.
 * Este servicio controla la lógica de negocios para crear un nuevo TypeId y guardarlo mediante IdOutputPort.
 */
@Service
@AllArgsConstructor
public class CreateTypeIdService implements CreateTypeIdUseCase{
    
    private final IdOutputPort idOutputPort;
   //private final TypeIdEventPublisher typeIdEventPublisher;

    @Override
    public TypeId createTypeId(TypeId typeId) {
        typeId = idOutputPort.saveTypeId(typeId);
        //typeIdEventPublisher.publishTypeIdCreatedEvent(new TypeIdCreatedEvent(typeId.getTypeId()));
        return typeId;
    }
    
}
