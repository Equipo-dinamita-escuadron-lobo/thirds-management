package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.ThirdType;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/**
 * Clase de servicio para crear un nuevo ThirdType.
 * Implementa la interfaz CreateThirdTypeUseCase.
 * Este servicio se encarga de gestionar la creación de tipos de terceros.
 * Utiliza IdOutputPort para guardar el yipo de tercero y generar un ID para él.
 */
@AllArgsConstructor
public class CreateThirdTypeService implements CreateThirdTypeUseCase {
    
    private final IdOutputPort idOutputPort;
    //private final ThirdTypeEventPublisher thirdTypeEventPublisher;
    @Override
    @Transactional
    public ThirdType createThirdType(ThirdType thirdType) {
        thirdType = idOutputPort.saveThirdType(thirdType);
        //thirdTypeEventPublisher.publishThirdTypeCreatedEvent(new ThirdTypeCreatedEvent(thirdType.getThirdTypeId()));
        return thirdType;
    }
    

    
}
