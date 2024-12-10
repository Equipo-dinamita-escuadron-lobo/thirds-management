package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.domain.model.Third;

/**
 * Interfaz que representa el caso de uso para la creación de un tercero.
 */
public interface CreateThirdUseCase {
    /**
     * Crea un nuevo tercero.
     * @param third Tercero a crear
     * @return Tercero creado
     */
    Third createThird(Third third);
}
