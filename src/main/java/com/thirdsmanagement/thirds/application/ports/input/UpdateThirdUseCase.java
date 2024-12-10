package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.domain.model.Third;

/**
 * Interfaz que representa el caso de uso para la actualización de un tercero.
 */
public interface UpdateThirdUseCase {
    /**
     * Actualiza un tercero
     * @param third tercero a actualizar
     * @return tercero actualizado
     */
    Third updateThird(Third third);
}
