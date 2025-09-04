package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.ChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.event.ThirdStateUpdateEvent;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdStateNotChanged;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ChangeThirdStateService implements ChangeThirdStateUseCase {

    private final ThirdOutputPort thirdOutputPort;
    private final ThirdEventPublisher thirdEventPublisher;

    /**
     * Cambia el estado de un tercero (activado/desactivado).
     * 
     * @param thId el ID del tercero cuyo estado se va a cambiar
     * @return true si el cambio fue exitoso, false en caso contrario
     * @throws ThirdInvalidDataException si el ID es null
     * @throws ThirdStateNotChanged si no se pudo cambiar el estado por un error interno
     */
    @Override
    public boolean changeThirdState(Long thId) {
        if (thId == null) {
            throw new ThirdInvalidDataException("El ID del tercero no puede ser null");
        }
        
        boolean result = thirdOutputPort.changeThirdState(thId);
        
        if (!result) {
            throw new ThirdStateNotChanged("El estado no se pudo cambiar debido a un error interno");
        }
        
        // Publicar evento de cambio de estado
        thirdEventPublisher.publishThirdStateUpdateEvent(new ThirdStateUpdateEvent(thId));

        return result;
    }
    
}
