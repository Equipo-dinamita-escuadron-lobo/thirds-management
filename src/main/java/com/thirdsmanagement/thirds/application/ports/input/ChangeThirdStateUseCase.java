package com.thirdsmanagement.thirds.application.ports.input;

/**
 * Interfaz que define el método para cambiar el estado de un tercero.
 */
public interface ChangeThirdStateUseCase {
    /**
     * Cambia el estado de un tercero.
     * @param thId Identificador del tercero
     * @param entId Identificador de la empresa
     * @return true si el estado cambió, false en caso contrario
     */
    boolean changeThirdState(Long thId, String entId);
}
