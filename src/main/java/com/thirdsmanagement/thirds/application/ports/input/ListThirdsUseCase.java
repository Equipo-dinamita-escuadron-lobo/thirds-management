package com.thirdsmanagement.thirds.application.ports.input;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

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
     * Obtiene todos los proveedores.
     * @param entId El id de la empresa
     * @param pageable El objeto pageable
     * @return La página de proveedores
     */
    Page<Third> getAllProvidersBy(String entId,Pageable pageable);
    
    /**
     * Obtiene todos los clientes.
     * @param entId El id de la empresa
     * @param pageable El objeto pageable
     * @return La página de clientes
     */
    Page<Third> getAllCustomersBy(String entId,Pageable pageable);
    
    /**
     * Obtiene todos los terceros.
     * @param entId El id de la empresa
     * @return La lista de terceros
     */
    List<Third> getAllThirds(String entId);
    
    /**
     * Obtiene todos los terceros filtrados por estado.
     * @param entId El id de la empresa
     * @param pageable El objeto pageable
     * @param isActive El estado del tercero (true para activos, false para inactivos)
     * @return La página de terceros filtrados por estado
     */
    Page<Third> getAllThirdsByStatus(String entId, Pageable pageable, boolean isActive);
}
