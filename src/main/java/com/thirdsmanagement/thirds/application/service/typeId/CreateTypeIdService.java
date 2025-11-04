package com.thirdsmanagement.thirds.application.service.typeId;

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
     * @brief Crea un nuevo tipo de identificación en el sistema
     * @param typeId tipo de identificación a crear
     * @return tipo de identificación creado con su ID asignado
     */
    @Override
    public TypeId createTypeId(TypeId typeId) {
        // Guardar el tipo de identificación
        TypeId createdTypeId = idOutputPort.saveTypeId(typeId);

        // TODO: Implementar publicación de eventos cuando esté disponible el publisher
        // typeIdEventPublisher.publishTypeIdCreatedEvent(new
        // TypeIdCreatedEvent(createdTypeId.getTypeId()));

        return createdTypeId;
    }

}
