package com.thirdsmanagement.thirds.application.service;
import java.util.ArrayList;
import java.util.List;

import com.thirdsmanagement.thirds.application.ports.input.ListThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.ThirdType;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Clase de servicio para listar los tipos de terceros.
 * Implementa la interfaz {@link ListThirdTypeUseCase}.
 * Utiliza {@link IdOutputPort} para las operaciones de persistencia.
 * Este servicio proporciona un método para listar todos los tipos de terceros.
 */
@Service
@AllArgsConstructor
public class ListThirdTypeService implements ListThirdTypeUseCase {
    
    private final IdOutputPort idOutputPort;
    
    @Override
    public List<ThirdType> getAllThirdTypes(String entId) {
       
        List<ThirdType> result = idOutputPort.getALLThirdTypes(entId);
        return result;
    }
    
}
