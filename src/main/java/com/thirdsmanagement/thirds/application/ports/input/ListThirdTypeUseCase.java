package com.thirdsmanagement.thirds.application.ports.input;

import java.util.List;

import org.springframework.data.domain.Page;

import com.thirdsmanagement.thirds.domain.model.ThirdType;

/**
 * Interfaz que representa el caso de uso para listar los tipos de terceros.
 */
public interface ListThirdTypeUseCase {
    /**
     * Obtiene todos los tipos de terceros
     * @param entId Identificador de la empresa
     * @return Lista de tipos de terceros
     */
    List<ThirdType> getAllThirdTypes(String entId);
    
    /**
     * Obtiene todos los tipos de tercero con paginación y ordenamiento.
     * @param entId El id de la empresa
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de tercero
     */
    Page<ThirdType> getAllThirdTypesWithSort(String entId, int page, int size, String sortField, String sortOrder);
    
    /**
     * Busca tipos de tercero por empresa y término de búsqueda.
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de tercero que coinciden
     */
    Page<ThirdType> findThirdTypesByEntIdAndSearch(String entId, String search, int page, int size, String sortField, String sortOrder);
    
    /**
     * Cuenta tipos de tercero por empresa.
     * @param entId El id de la empresa
     * @return Cantidad de tipos de tercero
     */
    long countThirdTypesByEntId(String entId);
    
    /**
     * Cuenta tipos de tercero por empresa y término de búsqueda.
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @return Cantidad de tipos de tercero que coinciden
     */
    long countThirdTypesByEntIdAndSearch(String entId, String search);
    
    /**
     * Cuenta tipos de tercero activos por empresa y término de búsqueda.
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @return Cantidad de tipos de tercero activos que coinciden
     */
    long countActiveThirdTypesByEntIdAndSearch(String entId, String search);
    
    /**
     * Cuenta tipos de tercero activos por empresa.
     * @param entId El id de la empresa
     * @return Cantidad de tipos de tercero activos
     */
    long countActiveThirdTypesByEntId(String entId);
    
    /**
     * Obtiene todos los tipos de tercero activos con paginación y ordenamiento.
     * @param entId El id de la empresa
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de tercero activos
     */
    Page<ThirdType> getAllActiveThirdTypesWithSort(String entId, int page, int size, String sortField, String sortOrder);
    
    /**
     * Busca tipos de tercero activos por empresa y término de búsqueda.
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de tercero activos que coinciden
     */
    Page<ThirdType> findActiveThirdTypesByEntIdAndSearch(String entId, String search, int page, int size, String sortField, String sortOrder);
}
