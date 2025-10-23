package com.thirdsmanagement.thirds.application.ports.input;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thirdsmanagement.thirds.domain.model.Third;

/**
 * Interfaz que representa el caso de uso para listar terceros.
 */
public interface ListThirdsUseCase {
    /**
     * Obtiene todos los terceros.
     * @param entId El id de la empresa
     * @param pageable El objeto pageable
     * @return La página de terceros
     */
    Page<Third> getAllThirdsBy(String entId, Pageable pageable);
    
    /**
     * Cuenta el total de terceros por empresa.
     * @param entId El id de la empresa
     * @return El número total de terceros
     */
    long countAllThirdsByEntId(String entId);
    
    /**
     * Busca terceros por empresa y término de búsqueda con ordenamiento.
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de terceros que coinciden con la búsqueda
     */
    Page<Third> findByEntIdAndSearch(String entId, String search, int page, int size, String sortField, String sortOrder);
    
    /**
     * Cuenta terceros por empresa y término de búsqueda.
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @return Cantidad de terceros que coinciden
     */
    long countByEntIdAndSearch(String entId, String search);
    
    /**
     * Obtiene todos los terceros con ordenamiento.
     * @param entId El id de la empresa
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de terceros ordenados
     */
    Page<Third> getAllThirdsByWithSort(String entId, int page, int size, String sortField, String sortOrder);

    /**
     * Obtiene todos los terceros activos con ordenamiento.
     * @param entId El id de la empresa
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de terceros activos ordenados
     */
    Page<Third> getAllActiveThirdsByWithSort(String entId, int page, int size, String sortField, String sortOrder);

    /**
     * Cuenta el total de terceros activos por empresa.
     * @param entId El id de la empresa
     * @return El número total de terceros activos
     */
    long countActiveThirdsByEntId(String entId);

}
