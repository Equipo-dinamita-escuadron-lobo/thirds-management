package com.thirdsmanagement.thirds.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thirdsmanagement.thirds.application.ports.input.ListThirdsUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.model.Third;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListThirdsService implements ListThirdsUseCase {

    private final ThirdOutputPort thirdOutputPort;
    /**
     * Obtiene todos los terceros de una empresa con paginación.
     * 
     * @param entId    el ID de la empresa
     * @param pageable información de paginación
     * @return página de terceros encontrados (puede estar vacía si no hay datos)
     */
    @Override
    public Page<Third> getAllThirdsBy(String entId, Pageable pageable) {
        return thirdOutputPort.getAllThirdsBy(entId, pageable);
    }

    /**
     * Cuenta el total de terceros por empresa.
     * 
     * @param entId el ID de la empresa
     * @return el número total de terceros
     */
    @Override
    public long countAllThirdsByEntId(String entId) {
        return thirdOutputPort.countAllThirdsByEntId(entId);
    }
}
