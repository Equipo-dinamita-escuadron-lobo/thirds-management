package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.UpdateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.ThirdTypeEventPublisher;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.event.ThirdTypeUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para la actualización de tipos de tercero.
 * Implementa el caso de uso {@link UpdateThirdTypeUseCase}.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateThirdTypeService implements UpdateThirdTypeUseCase {

    private final IdOutputPort idOutputPort;
    private final ThirdTypeEventPublisher thirdTypeEventPublisher;

    /**
     * Actualiza un tipo de tercero existente.
     * 
     * @param thirdType El tipo de tercero con los datos actualizados
     * @return El tipo de tercero actualizado
     */
    @Override
    public ThirdType updateThirdType(ThirdType thirdType) {
        ThirdType updatedThirdType = idOutputPort.updateThirdType(thirdType);
        
        // Publicar evento de actualización
        ThirdTypeUpdatedEvent event = ThirdTypeUpdatedEvent.builder()
                .thirdTypeId(updatedThirdType.getThirdTypeId())
                .thirdTypeName(updatedThirdType.getThirdTypeName())
                .entId(updatedThirdType.getEntId())
                .build();
        
        thirdTypeEventPublisher.publishThirdTypeUpdatedEvent(event);
        
        return updatedThirdType;
    }
}
