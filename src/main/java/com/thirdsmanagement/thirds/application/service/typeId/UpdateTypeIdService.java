package com.thirdsmanagement.thirds.application.service.typeId;

import com.thirdsmanagement.thirds.application.ports.input.UpdateTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.TypeIdEventPublisher;
import com.thirdsmanagement.thirds.domain.event.TypeIdCreatedEvent;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateTypeIdService implements UpdateTypeIdUseCase {

    private final IdOutputPort idOutputPort;
    private final TypeIdEventPublisher typeIdEventPublisher;

    /**
     * Actualiza un tipo de identificación existente en el sistema.
     * 
     * @param typeId el tipo de identificación con los datos actualizados
     * @return el tipo de identificación actualizado
     */
    @Override
    public TypeId updateTypeId(TypeId typeId) {
        // Actualizar el tipo de identificación
        TypeId updatedTypeId = idOutputPort.updateTypeId(typeId);

        // Publicar evento de actualización (reutilizamos el evento de creación)
        typeIdEventPublisher.publishTypeIdCreatedEvent(new TypeIdCreatedEvent(updatedTypeId.getTypeId()));

        return updatedTypeId;
    }
}
