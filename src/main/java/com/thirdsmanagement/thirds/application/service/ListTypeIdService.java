package com.thirdsmanagement.thirds.application.service;

import java.util.List;

import org.hibernate.bytecode.internal.bytebuddy.PrivateAccessorException;

import com.thirdsmanagement.thirds.application.ports.input.ListTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Clase de servicio para listar los tipos de identificación.
 * Implementa la interfaz {@link ListTypeIdUseCase}.
 * Utiliza {@link IdOutputPort} para las operaciones de persistencia. 
 * Este servicio proporciona un método para listar todos los tipos de identificación. 
 */
@Service
@AllArgsConstructor
public class ListTypeIdService implements ListTypeIdUseCase {

    private final IdOutputPort idOutputPort;

    @Override
    public List<TypeId> getAllTypeId(String entId) {
        List<TypeId> result = idOutputPort.getAllTypeIds(entId);
        return result;
    }
    
}
