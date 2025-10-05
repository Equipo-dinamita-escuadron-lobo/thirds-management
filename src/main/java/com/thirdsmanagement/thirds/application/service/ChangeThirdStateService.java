package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.ChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.event.ThirdStateUpdateEvent;
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
     * @param thId  el ID del tercero cuyo estado se va a cambiar
     * @param entId el ID de la empresa a la que pertenece el tercero
     * @return true si el cambio fue exitoso, false en caso contrario
     * @throws ThirdStateNotChanged si no se pudo cambiar el estado por un error interno
     */
    @Override
    public boolean changeThirdState(Long thId, String entId) {
        boolean result = thirdOutputPort.changeThirdState(thId, entId);

        if (!result) {
            throw new ThirdStateNotChanged("El estado no se pudo cambiar debido a un error interno");
        }

        // Publicar evento de cambio de estado
        thirdEventPublisher.publishThirdStateUpdateEvent(new ThirdStateUpdateEvent(thId));

        return result;
    }

}
