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
     * @return página de terceros encontrados 
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    @Override
    public Page<Third> getAllThirdsBy(String entId, Pageable pageable) {
        validateEnterpriseId(entId);
        validatePageable(pageable);

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
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    @Override
    public Page<Third> getAllThirdsByType(String entId, Pageable pageable, Long thirdTypeId) {
        validateEnterpriseId(entId);
        validatePageable(pageable);
        validateThirdTypeId(thirdTypeId);

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
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    @Override
    public Page<Third> getAllThirdsByStatus(String entId, Pageable pageable, boolean isActive) {
        validateEnterpriseId(entId);
        validatePageable(pageable);

        return thirdOutputPort.getAllThirdsByStatus(entId, pageable, isActive);
    }

    /**
     * Valida que el ID de empresa no sea null o vacío.
     * 
     * @param entId el ID de la empresa a validar
     * @throws IllegalArgumentException si el ID es inválido
     */
    private void validateEnterpriseId(String entId) {
        if (entId == null || entId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la empresa no puede ser null o vacío");
        }
    }

    /**
     * Valida que el objeto Pageable no sea null.
     * 
     * @param pageable el objeto Pageable a validar
     * @throws IllegalArgumentException si Pageable es null
     */
    private void validatePageable(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("El objeto Pageable no puede ser null");
        }
    }

    /**
     * Valida que el ID del tipo de tercero no sea null.
     * 
     * @param thirdTypeId el ID del tipo de tercero a validar
     * @throws IllegalArgumentException si el ID del tipo de tercero es null
     */
    private void validateThirdTypeId(Long thirdTypeId) {
        if (thirdTypeId == null) {
            throw new IllegalArgumentException("El ID del tipo de tercero no puede ser null");
        }
    }

    /**
     * Cuenta el total de terceros por empresa.
     * 
     * @param entId el ID de la empresa
     * @return el número total de terceros
     * @throws IllegalArgumentException si el ID de empresa es inválido
     */
    @Override
    public long countAllThirdsByEntId(String entId) {
        validateEnterpriseId(entId);
        return thirdOutputPort.countAllThirdsByEntId(entId);
    }

    /**
     * Cuenta el total de terceros filtrados por tipo.
     * 
     * @param entId       el ID de la empresa
     * @param thirdTypeId el ID del tipo de tercero
     * @return el número total de terceros del tipo especificado
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    @Override
    public long countAllThirdsByType(String entId, Long thirdTypeId) {
        validateEnterpriseId(entId);
        validateThirdTypeId(thirdTypeId);
        return thirdOutputPort.countAllThirdsByType(entId, thirdTypeId);
    }

    /**
     * Cuenta el total de terceros filtrados por estado.
     * 
     * @param entId    el ID de la empresa
     * @param isActive el estado del tercero
     * @return el número total de terceros con el estado especificado
     * @throws IllegalArgumentException si el ID de empresa es inválido
     */
    @Override
    public long countAllThirdsByStatus(String entId, boolean isActive) {
        validateEnterpriseId(entId);
        return thirdOutputPort.countAllThirdsByStatus(entId, isActive);
    }
}
