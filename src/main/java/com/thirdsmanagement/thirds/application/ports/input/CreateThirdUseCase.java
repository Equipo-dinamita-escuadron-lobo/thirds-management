package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.domain.model.Third;

/**
 * Interfaz que representa el caso de uso para la creación de un tercero con validación geográfica.
 */
public interface CreateThirdUseCase {
    /**
     * Crea un nuevo tercero con validación geográfica.
     * @param third el tercero a crear
     * @param countryCode código del país
     * @param stateCode código del estado/departamento
     * @param cityCode código de la ciudad
     * @return el tercero creado con geografía validada
     */
    Third createThird(Third third, String countryCode, String stateCode, String cityCode);
}
