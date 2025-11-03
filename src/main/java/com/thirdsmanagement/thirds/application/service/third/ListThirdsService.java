package com.thirdsmanagement.thirds.application.service.third;

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

    /**
     * Busca terceros por empresa y término de búsqueda con ordenamiento.
     * 
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de terceros que coinciden con la búsqueda
     */
    @Override
    public Page<Third> findByEntIdAndSearch(String entId, String search, int page, int size, String sortField, String sortOrder) {
        return thirdOutputPort.findByEntIdAndSearch(entId, search, page, size, sortField, sortOrder);
    }

    /**
     * Cuenta terceros por empresa y término de búsqueda.
     * 
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @return Cantidad de terceros que coinciden
     */
    @Override
    public long countByEntIdAndSearch(String entId, String search) {
        return thirdOutputPort.countByEntIdAndSearch(entId, search);
    }

    /**
     * Obtiene todos los terceros con ordenamiento.
     * 
     * @param entId El id de la empresa
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de terceros ordenados
     */
    @Override
    public Page<Third> getAllThirdsByWithSort(String entId, int page, int size, String sortField, String sortOrder) {
        return thirdOutputPort.getAllThirdsByWithSort(entId, page, size, sortField, sortOrder);
    }

    /**
     * Obtiene todos los terceros activos con ordenamiento.
     *
     * @param entId El id de la empresa
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de terceros activos ordenados
     */
    @Override
    public Page<Third> getAllActiveThirdsByWithSort(String entId, int page, int size, String sortField, String sortOrder) {
        return thirdOutputPort.getAllActiveThirdsByWithSort(entId, page, size, sortField, sortOrder);
    }

    /**
     * Cuenta el total de terceros activos por empresa.
     *
     * @param entId el ID de la empresa
     * @return el número total de terceros activos
     */
    @Override
    public long countActiveThirdsByEntId(String entId) {
        return thirdOutputPort.countActiveThirdsByEntId(entId);
    }

}
