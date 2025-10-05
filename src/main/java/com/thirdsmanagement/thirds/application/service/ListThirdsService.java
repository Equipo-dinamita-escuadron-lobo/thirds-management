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
     * Obtiene todos los terceros filtrados por tipo de tercero de una empresa con
     * paginación.
     * 
     * @param entId       el ID de la empresa
     * @param pageable    información de paginación
     * @param thirdTypeId el ID del tipo de tercero
     * @return página de terceros filtrados por tipo (puede estar vacía si no hay datos)
     */
    @Override
    public Page<Third> getAllThirdsByType(String entId, Pageable pageable, Long thirdTypeId) {
        return thirdOutputPort.getAllThirdsByTypeId(entId, pageable, thirdTypeId);
    }

    /**
     * Obtiene todos los terceros filtrados por estado de una empresa con
     * paginación.
     * 
     * @param entId    el ID de la empresa
     * @param pageable información de paginación
     * @param isActive el estado del tercero (true para activos, false para
     *                 inactivos)
     * @return página de terceros filtrados por estado (puede estar vacía si no hay datos)
     */
    @Override
    public Page<Third> getAllThirdsByStatus(String entId, Pageable pageable, boolean isActive) {
        return thirdOutputPort.getAllThirdsByStatus(entId, pageable, isActive);
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

    /**
     * Cuenta el total de terceros filtrados por tipo.
     * 
     * @param entId       el ID de la empresa
     * @param thirdTypeId el ID del tipo de tercero
     * @return el número total de terceros del tipo especificado
     */
    @Override
    public long countAllThirdsByType(String entId, Long thirdTypeId) {
        return thirdOutputPort.countAllThirdsByType(entId, thirdTypeId);
    }

    /**
     * Cuenta el total de terceros filtrados por estado.
     * 
     * @param entId    el ID de la empresa
     * @param isActive el estado del tercero
     * @return el número total de terceros con el estado especificado
     */
    @Override
    public long countAllThirdsByStatus(String entId, boolean isActive) {
        return thirdOutputPort.countAllThirdsByStatus(entId, isActive);
    }
}
