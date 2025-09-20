package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.domain.model.Third;

/**
 * Interfaz que representa el caso de uso para la actualización de un tercero.
 */
public interface UpdateThirdUseCase {
    /**
     * Actualiza un tercero existente en el sistema con validación opcional de geografía.
     * 
     * @param third el tercero con los datos actualizados
     * @param countryCode código del país (opcional, puede ser null)
     * @param stateCode código del estado/departamento (opcional, puede ser null)  
     * @param cityCode código de la ciudad (opcional, puede ser null)
     * @return el tercero actualizado
     */
    Third updateThirdWithGeography(Third third, String countryCode, String stateCode, String cityCode);
}
