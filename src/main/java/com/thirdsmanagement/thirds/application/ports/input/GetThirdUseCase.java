package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.domain.model.Third;

/**
 * Interfaz que representa el caso de uso para la obtención de un tercero.
 */
public interface GetThirdUseCase {
    /**
     * Obtiene un tercero por su id.
     * @param id El id del tercero a obtener
     * @return El tercero obtenido
     */
    Third getThirdById(Long id);
    
    /**
     * Verifica si un tercero existe por su id.
     * @param id El id del tercero a verificar
     * @param entId
     * @return true si el tercero existe, false en caso contrario
     */
    boolean existThirdById(long  id,  String entId);
}
