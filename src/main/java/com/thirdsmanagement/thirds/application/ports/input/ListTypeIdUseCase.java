package com.thirdsmanagement.thirds.application.ports.input;

import java.util.List;

import org.springframework.data.domain.Page;

import com.thirdsmanagement.thirds.domain.model.TypeId;

/**
 * Interfaz que representa el caso de uso para listar los tipos de identificacion.
 */
public interface ListTypeIdUseCase {
    /**
     * Obtiene todos los tipos de identidicacion
     * @param entId Identificador de la empresa
     * @return Lista de tipos de identificacion
     */
    List<TypeId> getAllTypeId(String entId);
    
    /**
     * Obtiene todos los tipos de identificación con paginación y ordenamiento.
     * @param entId El id de la empresa
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de identificación
     */
    Page<TypeId> getAllTypeIdsWithSort(String entId, int page, int size, String sortField, String sortOrder);
    
    /**
     * Obtiene todos los tipos de identificación activos con paginación y ordenamiento.
     * @param entId El id de la empresa
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de identificación activos
     */
    Page<TypeId> getAllActiveTypeIdsWithSort(String entId, int page, int size, String sortField, String sortOrder);
    
    /**
     * Busca tipos de identificación por empresa y término de búsqueda.
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de identificación que coinciden
     */
    Page<TypeId> findByEntIdAndSearch(String entId, String search, int page, int size, String sortField, String sortOrder);
    
    /**
     * Busca tipos de identificación activos por empresa y término de búsqueda.
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @param page Número de página
     * @param size Tamaño de página
     * @param sortField Campo de ordenamiento
     * @param sortOrder Orden (asc/desc)
     * @return Página de tipos de identificación activos que coinciden
     */
    Page<TypeId> findActiveByEntIdAndSearch(String entId, String search, int page, int size, String sortField, String sortOrder);
    
    /**
     * Cuenta tipos de identificación por empresa.
     * @param entId El id de la empresa
     * @return Cantidad de tipos de identificación
     */
    long countByEntId(String entId);
    
    /**
     * Cuenta tipos de identificación activos por empresa.
     * @param entId El id de la empresa
     * @return Cantidad de tipos de identificación activos
     */
    long countActiveByEntId(String entId);
    
    /**
     * Cuenta tipos de identificación por empresa y término de búsqueda.
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @return Cantidad de tipos de identificación que coinciden
     */
    long countByEntIdAndSearch(String entId, String search);
    
    /**
     * Cuenta tipos de identificación activos por empresa y término de búsqueda.
     * @param entId El id de la empresa
     * @param search Término de búsqueda
     * @return Cantidad de tipos de identificación activos que coinciden
     */
    long countActiveByEntIdAndSearch(String entId, String search);
}
