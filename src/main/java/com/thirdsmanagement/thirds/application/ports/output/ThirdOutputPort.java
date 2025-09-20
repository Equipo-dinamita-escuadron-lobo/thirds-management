package com.thirdsmanagement.thirds.application.ports.output;

import java.util.List;
import java.util.Optional;

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
     * Obtiene todos los terceros filtrados por tipo de tercero.
     * @param entId El id de la empresa
     * @param page El pageable object
     * @param thirdType El tipo de tercero (ej: "Proveedor", "Cliente")
     * @return La pagina de terceros filtrados por tipo
     */
    Page<Third> getAllThirdsByType(String entId, Pageable page, String thirdType);
    
    /**
     * Obtiene todos los terceros filtrados por ID de tipo de tercero.
     * @param entId El id de la empresa
     * @param page El pageable object
     * @param thirdTypeId El ID del tipo de tercero
     * @return La pagina de terceros filtrados por ID de tipo
     */
    Page<Third> getAllThirdsByTypeId(String entId, Pageable page, Long thirdTypeId);
    
    /**
     * Obtiene todos los terceros.
     * @param entId El id de la empresa
     * @return La lista de terceros
     */
    List<Third> getAllThirds(String entId);
    
    /**
     * Obtiene todos los terceros filtrados por estado.
     * @param entId El id de la empresa
     * @param page El pageable object
     * @param isActive El estado del tercero (true para activos, false para inactivos)
     * @return La pagina de terceros filtrados por estado
     */
    Page<Third> getAllThirdsByStatus(String entId, Pageable page, boolean isActive);
}
