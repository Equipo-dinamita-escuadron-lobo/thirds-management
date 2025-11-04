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
     * @brief Obtiene todos los terceros de una empresa con paginación
     * @param entId identificador de la empresa
     * @param pageable información de paginación y ordenamiento
     * @return página de terceros encontrados
     */
    @Override
    public Page<Third> getAllThirdsBy(String entId, Pageable pageable) {
        return thirdOutputPort.getAllThirdsBy(entId, pageable);
    }

    /**
     * @brief Cuenta el total de terceros por empresa
     * @param entId identificador de la empresa
     * @return número total de terceros
     */
    @Override
    public long countAllThirdsByEntId(String entId) {
        return thirdOutputPort.countAllThirdsByEntId(entId);
    }

    /**
     * @brief Busca terceros por empresa y término de búsqueda con ordenamiento
     * @param entId identificador de la empresa
     * @param search término de búsqueda
     * @param page número de página (0-based)
     * @param size tamaño de página
     * @param sortField campo de ordenamiento
     * @param sortOrder orden (ascendente/descendente)
     * @return página de terceros que coinciden con la búsqueda
     */
    @Override
    public Page<Third> findByEntIdAndSearch(String entId, String search, int page, int size, String sortField, String sortOrder) {
        return thirdOutputPort.findByEntIdAndSearch(entId, search, page, size, sortField, sortOrder);
    }

    /**
     * @brief Cuenta terceros por empresa y término de búsqueda
     * @param entId identificador de la empresa
     * @param search término de búsqueda
     * @return cantidad de terceros que coinciden con la búsqueda
     */
    @Override
    public long countByEntIdAndSearch(String entId, String search) {
        return thirdOutputPort.countByEntIdAndSearch(entId, search);
    }

    /**
     * @brief Obtiene todos los terceros con ordenamiento personalizado
     * @param entId identificador de la empresa
     * @param page número de página (0-based)
     * @param size tamaño de página
     * @param sortField campo de ordenamiento
     * @param sortOrder orden (ascendente/descendente)
     * @return página de terceros ordenados
     */
    @Override
    public Page<Third> getAllThirdsByWithSort(String entId, int page, int size, String sortField, String sortOrder) {
        return thirdOutputPort.getAllThirdsByWithSort(entId, page, size, sortField, sortOrder);
    }

    /**
     * @brief Obtiene todos los terceros activos con ordenamiento personalizado
     * @param entId identificador de la empresa
     * @param page número de página (0-based)
     * @param size tamaño de página
     * @param sortField campo de ordenamiento
     * @param sortOrder orden (ascendente/descendente)
     * @return página de terceros activos ordenados
     */
    @Override
    public Page<Third> getAllActiveThirdsByWithSort(String entId, int page, int size, String sortField, String sortOrder) {
        return thirdOutputPort.getAllActiveThirdsByWithSort(entId, page, size, sortField, sortOrder);
    }

    /**
     * @brief Cuenta el total de terceros activos por empresa
     * @param entId identificador de la empresa
     * @return número total de terceros activos
     */
    @Override
    public long countActiveThirdsByEntId(String entId) {
        return thirdOutputPort.countActiveThirdsByEntId(entId);
    }

}
