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
    Page<Third> getAllThirdsBy(String entId,Pageable pageable);
    
    
    /**
     * Obtiene todos los terceros filtrados por tipo de tercero.
     * @param entId El id de la empresa
     * @param pageable El objeto pageable
     * @param thirdType El tipo de tercero (ej: "Proveedor", "Cliente")
     * @return La página de terceros filtrados por tipo
     */
    Page<Third> getAllThirdsByType(String entId, Pageable pageable, String thirdType);
    
    
    /**
     * Obtiene todos los terceros filtrados por estado.
     * @param entId El id de la empresa
     * @param pageable El objeto pageable
     * @param isActive El estado del tercero (true para activos, false para inactivos)
     * @return La página de terceros filtrados por estado
     */
    Page<Third> getAllThirdsByStatus(String entId, Pageable pageable, boolean isActive);
}
