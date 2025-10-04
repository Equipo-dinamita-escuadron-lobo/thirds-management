package com.thirdsmanagement.thirds.application.service;

import java.util.List;

import com.thirdsmanagement.thirds.application.ports.input.ListTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListTypeIdService implements ListTypeIdUseCase {

    private final IdOutputPort idOutputPort;

    /**
     * Obtiene todos los tipos de identificación para una empresa específica.
     * 
     * @param entId el ID de la empresa
     * @return lista de tipos de identificación disponibles
     */
    @Override
    public List<TypeId> getAllTypeId(String entId) {
        return idOutputPort.getAllTypeIds(entId);
    }

}
