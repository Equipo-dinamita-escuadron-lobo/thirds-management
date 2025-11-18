package com.thirdsmanagement.thirds.application.service.typeId;

import com.thirdsmanagement.thirds.application.ports.input.UpdateTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateTypeIdService implements UpdateTypeIdUseCase {

    private final IdOutputPort idOutputPort;

    /**
     * @brief Actualiza un tipo de identificación existente en el sistema
     * @param typeId tipo de identificación con los datos actualizados
     * @return tipo de identificación actualizado
     */
    @Override
    public TypeId updateTypeId(TypeId typeId) {
        // Actualizar el tipo de identificación
        TypeId updatedTypeId = idOutputPort.updateTypeId(typeId);

        return updatedTypeId;
    }
}
