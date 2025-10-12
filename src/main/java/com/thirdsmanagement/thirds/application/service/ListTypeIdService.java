package com.thirdsmanagement.thirds.application.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.thirdsmanagement.thirds.application.ports.input.ListTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import lombok.RequiredArgsConstructor;

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

    /**
     * Obtiene todos los tipos de identificación con paginación y ordenamiento.
     * 
     * @param entId El id de la empresa
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de identificación
     */
    @Override
    public Page<TypeId> getAllTypeIdsWithSort(String entId, int page, int size, String sortField, String sortOrder) {
        return idOutputPort.getAllTypeIdsWithSort(entId, page, size, sortField, sortOrder);
    }

    /**
     * Busca tipos de identificación por empresa y término de búsqueda.
     * 
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de identificación que coinciden
     */
    @Override
    public Page<TypeId> findByEntIdAndSearch(String entId, String search, int page, int size, String sortField, String sortOrder) {
        return idOutputPort.findByEntIdAndSearch(entId, search, page, size, sortField, sortOrder);
    }

    /**
     * Cuenta tipos de identificación por empresa.
     * 
     * @param entId El id de la empresa
     * @return Cantidad de tipos de identificación
     */
    @Override
    public long countByEntId(String entId) {
        return idOutputPort.countByEntId(entId);
    }

    /**
     * Cuenta tipos de identificación por empresa y término de búsqueda.
     * 
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @return Cantidad de tipos de identificación que coinciden
     */
    @Override
    public long countByEntIdAndSearch(String entId, String search) {
        return idOutputPort.countByEntIdAndSearch(entId, search);
    }

}
