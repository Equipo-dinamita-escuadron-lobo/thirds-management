package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.domain.model.ThirdType;

/**
 * Interfaz que representa el caso de uso para la creación de un tipo de tercero.
 */
public interface CreateThirdTypeUseCase {

    /**
     * Crea un nuevo tipo de tercero.
     * @param thirdType Tipo de tercero a crear
     * @return Tipo de tercero creado
     */
    ThirdType createThirdType(ThirdType thirdType);
    
} 
