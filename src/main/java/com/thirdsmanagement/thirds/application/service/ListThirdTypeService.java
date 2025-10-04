package com.thirdsmanagement.thirds.application.service;

import java.util.List;

import com.thirdsmanagement.thirds.application.ports.input.ListThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.ThirdType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListThirdTypeService implements ListThirdTypeUseCase {

    private final IdOutputPort idOutputPort;

    /**
     * Obtiene todos los tipos de terceros para una empresa específica.
     * 
     * @param entId el ID de la empresa
     * @return lista de tipos de terceros disponibles
     */
    @Override
    public List<ThirdType> getAllThirdTypes(String entId) {
        return idOutputPort.getALLThirdTypes(entId);
    }

}
