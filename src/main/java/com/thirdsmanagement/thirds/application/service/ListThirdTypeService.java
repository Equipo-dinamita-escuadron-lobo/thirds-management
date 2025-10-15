package com.thirdsmanagement.thirds.application.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.thirdsmanagement.thirds.application.ports.input.ListThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.ThirdType;

import lombok.RequiredArgsConstructor;

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

    /**
     * Obtiene todos los tipos de tercero con paginación y ordenamiento.
     * 
     * @param entId El id de la empresa
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de tercero
     */
    @Override
    public Page<ThirdType> getAllThirdTypesWithSort(String entId, int page, int size, String sortField, String sortOrder) {
        return idOutputPort.getAllThirdTypesWithSort(entId, page, size, sortField, sortOrder);
    }

    /**
     * Busca tipos de tercero por empresa y término de búsqueda.
     * 
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de tercero que coinciden
     */
    @Override
    public Page<ThirdType> findThirdTypesByEntIdAndSearch(String entId, String search, int page, int size, String sortField, String sortOrder) {
        return idOutputPort.findThirdTypesByEntIdAndSearch(entId, search, page, size, sortField, sortOrder);
    }

    /**
     * Cuenta tipos de tercero por empresa.
     * 
     * @param entId El id de la empresa
     * @return Cantidad de tipos de tercero
     */
    @Override
    public long countThirdTypesByEntId(String entId) {
        return idOutputPort.countThirdTypesByEntId(entId);
    }

    /**
     * Cuenta tipos de tercero por empresa y término de búsqueda.
     * 
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @return Cantidad de tipos de tercero que coinciden
     */
    @Override
    public long countThirdTypesByEntIdAndSearch(String entId, String search) {
        return idOutputPort.countThirdTypesByEntIdAndSearch(entId, search);
    }
    
    /**
     * Cuenta tipos de tercero activos por empresa y término de búsqueda.
     * 
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @return Cantidad de tipos de tercero activos que coinciden
     */
    @Override
    public long countActiveThirdTypesByEntIdAndSearch(String entId, String search) {
        return idOutputPort.countActiveThirdTypesByEntIdAndSearch(entId, search);
    }
    
    /**
     * Cuenta tipos de tercero activos por empresa.
     * 
     * @param entId El id de la empresa
     * @return Cantidad de tipos de tercero activos
     */
    @Override
    public long countActiveThirdTypesByEntId(String entId) {
        return idOutputPort.countActiveThirdTypesByEntId(entId);
    }
    
    /**
     * Obtiene todos los tipos de tercero activos con paginación y ordenamiento.
     * 
     * @param entId El id de la empresa
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de tercero activos
     */
    @Override
    public Page<ThirdType> getAllActiveThirdTypesWithSort(String entId, int page, int size, String sortField, String sortOrder) {
        return idOutputPort.getAllActiveThirdTypesWithSort(entId, page, size, sortField, sortOrder);
    }
    
    /**
     * Busca tipos de tercero activos por empresa y término de búsqueda.
     * 
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de tercero activos que coinciden
     */
    @Override
    public Page<ThirdType> findActiveThirdTypesByEntIdAndSearch(String entId, String search, int page, int size, String sortField, String sortOrder) {
        return idOutputPort.findActiveThirdTypesByEntIdAndSearch(entId, search, page, size, sortField, sortOrder);
    }

}
