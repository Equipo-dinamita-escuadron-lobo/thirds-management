package com.thirdsmanagement.thirds.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

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
     * @param entId el ID de la empresa
     * @param pageable información de paginación
     * @return página de terceros encontrados
     * @throws IllegalArgumentException si los parámetros son inválidos
     * @throws ThirdNotFound si no se encuentran terceros
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
     * Obtiene todos los proveedores de una empresa con paginación.
     * 
     * @param entId el ID de la empresa
     * @param pageable información de paginación
     * @return página de proveedores encontrados
     * @throws IllegalArgumentException si los parámetros son inválidos
     * @throws ThirdNotFound si no se encuentran proveedores
     */
    @Override
    public Page<Third> getAllProvidersBy(String entId, Pageable pageable) {
        validateEnterpriseId(entId);
        validatePageable(pageable);
        
        Page<Third> result = thirdOutputPort.getAllProvidersBy(entId, pageable);

        if (result.isEmpty()) {
            throw new ThirdNotFound("No se encontraron proveedores para la empresa con ID: " + entId);            
        }

        return result;
    }

    /**
     * Obtiene todos los clientes de una empresa con paginación.
     * 
     * @param entId el ID de la empresa
     * @param pageable información de paginación
     * @return página de clientes encontrados
     * @throws IllegalArgumentException si los parámetros son inválidos
     * @throws ThirdNotFound si no se encuentran clientes
     */
    @Override
    public Page<Third> getAllCustomersBy(String entId, Pageable pageable) {
        validateEnterpriseId(entId);
        validatePageable(pageable);
        
        Page<Third> result = thirdOutputPort.getAllCustomersBy(entId, pageable);

        if (result.isEmpty()) {
            throw new ThirdNotFound("No se encontraron clientes para la empresa con ID: " + entId);            
        }

        return result;
    }
    
    /**
     * Obtiene todos los terceros de una empresa sin paginación.
     * 
     * @param entId el ID de la empresa
     * @return lista de todos los terceros encontrados
     * @throws IllegalArgumentException si el ID de empresa es inválido
     * @throws ThirdNotFound si no se encuentran terceros
     */
    @Override
    public List<Third> getAllThirds(String entId) {
        validateEnterpriseId(entId);
        
        List<Third> result = thirdOutputPort.getAllThirds(entId);

        if (result.isEmpty()) {
            throw new ThirdNotFound("No se encontraron terceros para la empresa con ID: " + entId);            
        }

        return result;
    }
    
    /**
     * Obtiene todos los terceros filtrados por estado de una empresa con paginación.
     * 
     * @param entId el ID de la empresa
     * @param pageable información de paginación
     * @param isActive el estado del tercero (true para activos, false para inactivos)
     * @return página de terceros filtrados por estado
     * @throws IllegalArgumentException si los parámetros son inválidos
     * @throws ThirdNotFound si no se encuentran terceros
     */
    @Override
    public Page<Third> getAllThirdsByStatus(String entId, Pageable pageable, boolean isActive) {
        validateEnterpriseId(entId);
        validatePageable(pageable);
        
        Page<Third> result = thirdOutputPort.getAllThirdsByStatus(entId, pageable, isActive);

        if (result.isEmpty()) {
            String statusMessage = isActive ? "activos" : "inactivos";
            throw new ThirdNotFound("No se encontraron terceros " + statusMessage + " para la empresa con ID: " + entId);            
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
}
