package com.thirdsmanagement.thirds.application.service;

import java.util.List;

import org.hibernate.bytecode.internal.bytebuddy.PrivateAccessorException;

import com.thirdsmanagement.thirds.application.ports.input.ListTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ListTypeIdService implements ListTypeIdUseCase {

    private final IdOutputPort idOutputPort;

    @Override
    public List<TypeId> getAllTypeId(String entId) {
        List<TypeId> result = idOutputPort.getAllTypeIds(entId);
        return result;
    }
    
}
