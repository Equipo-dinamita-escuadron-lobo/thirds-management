package com.thirdsmanagement.thirds.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thirdsmanagement.thirds.application.ports.input.ListThirdsUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdNotFound;
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
     * @throws ThirdNotFound            si no se encuentran terceros
     */
    @Override
    public Page<Third> getAllThirdsBy(String entId, Pageable pageable) {
        validateEnterpriseId(entId);
        validatePageable(pageable);

        Page<Third> result = thirdOutputPort.getAllThirdsBy(entId, pageable);

        if (result.isEmpty()) {
            throw new ThirdNotFound("No se encontraron terceros para la empresa con ID: " + entId);
        }

        return result;
    }

    /**
     * Obtiene todos los terceros filtrados por tipo de tercero de una empresa con
     * paginación.
     * 
     * @param entId       el ID de la empresa
     * @param pageable    información de paginación
     * @param thirdTypeId el ID del tipo de tercero
     * @return página de terceros filtrados por tipo
     * @throws IllegalArgumentException si los parámetros son inválidos
     * @throws ThirdNotFound            si no se encuentran terceros del tipo
     *                                  especificado
     */
    @Override
    public Page<Third> getAllThirdsByType(String entId, Pageable pageable, Long thirdTypeId) {
        validateEnterpriseId(entId);
        validatePageable(pageable);
        validateThirdTypeId(thirdTypeId);

        Page<Third> result = thirdOutputPort.getAllThirdsByTypeId(entId, pageable, thirdTypeId);

        if (result.isEmpty()) {
            throw new ThirdNotFound(
                    "No se encontro un tercero con ID de tipo de tercero '" + thirdTypeId + "' para la empresa con ID: " + entId);
        }

        return result;
    }


    /**
     * Obtiene todos los terceros filtrados por estado de una empresa con
     * paginación.
     * 
     * @param entId    el ID de la empresa
     * @param pageable información de paginación
     * @param isActive el estado del tercero (true para activos, false para
     *                 inactivos)
     * @return página de terceros filtrados por estado
     * @throws IllegalArgumentException si los parámetros son inválidos
     * @throws ThirdNotFound            si no se encuentran terceros
     */
    @Override
    public Page<Third> getAllThirdsByStatus(String entId, Pageable pageable, boolean isActive) {
        validateEnterpriseId(entId);
        validatePageable(pageable);

        Page<Third> result = thirdOutputPort.getAllThirdsByStatus(entId, pageable, isActive);

        if (result.isEmpty()) {
            String statusMessage = isActive ? "activos" : "inactivos";
            throw new ThirdNotFound(
                    "No se encontraron terceros " + statusMessage + " para la empresa con ID: " + entId);
        }

        return result;
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
}
