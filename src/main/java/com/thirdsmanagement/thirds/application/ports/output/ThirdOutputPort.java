package com.thirdsmanagement.thirds.application.ports.output;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thirdsmanagement.thirds.domain.model.Third;

/**
 * Interfaz que define los metodos de salida para la entidad Tercero.
 */
public interface ThirdOutputPort {
    /**
     * Guarda un tercero.
     * @param third El tercero a guardar
     * @return El tercero guardado
     */
    Third saveThird(Third third);
    
    /**
     * Actualiza un tercero.
     * @param third El tercero a actualizar
     * @return El tercero actualizado
     */
    Third updateThird(Third third);
    
    /**
     * Obtiene un tercero por id y empresa.
     * @param id El id del tercero
     * @param entId El id de la empresa
     * @return El tercero si existe, null en caso contrario
     */
    Optional<Third> getThirdById(Long id, String entId);
    
    /**
     * Validar si existe un tercero por id y el id de la empresa.
     * @param id El id del tercero
     * @param entId El id de la empresa
     * @return True si existe, false en caso contrario
     */
    boolean existThirdById(long id, String entId);
    
    /**
     * Encuentra qué números de identificación ya existen en la base de datos.
     * 
     * @param idNumbers conjunto de números de identificación a verificar
     * @param entId el id de la empresa
     * @return conjunto de números de identificación que ya existen
     */
    Set<Long> findExistingIdNumbers(Set<Long> idNumbers, String entId);
    
    /**
     * Cambia el estado de un tercero.
     * @param thId El id del tercero
     * @param entId El id de la empresa
     * @return True si se cambio el estado, false en caso contrario
     */
    boolean changeThirdState(Long thId, String entId);
    
    /**
     * Obtiene todos los terceros.
     * @param entId El id de la empresa
     * @param page El pageable object
     * @return La pagina de terceros
     */
    Page<Third> getAllThirdsBy(String entId, Pageable page);
    
    /**
     * Obtiene todos los terceros filtrados por estado.
     * Optimizado para exportación con filtro de estado en BD.
     * 
     * @param entId El id de la empresa
     * @param state Estado de los terceros (true=activos, false=inactivos)
     * @param page El pageable object
     * @return La pagina de terceros filtrados por estado
     */
    Page<Third> getAllThirdsByState(String entId, Boolean state, Pageable page);
    
    
    /**
     * Elimina un tercero del sistema junto con sus asociaciones.
     * @param thirdId El ID del tercero a eliminar
     * @param entId El ID de la empresa
     * @return true si se eliminó correctamente, false en caso contrario
     */
    boolean deleteThird(Long thirdId, String entId);
    
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
     * Actualiza el estado de todos los terceros de una empresa de forma masiva.
     * @param entId El id de la empresa
     * @param newState El nuevo estado (true para activo, false para inactivo)
     * @return La cantidad de terceros actualizados
     */
    int bulkUpdateThirdState(String entId, Boolean newState);
}
