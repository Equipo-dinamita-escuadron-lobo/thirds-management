package com.thirdsmanagement.thirds.application.ports.input;

/**
 * Caso de uso para cambiar el estado de múltiples terceros de forma masiva.
 * Permite activar o inactivar todos los terceros de una empresa.
 */
public interface BulkChangeThirdStateUseCase {
    
    /**
     * Cambia el estado de todos los terceros de una empresa.
     * 
     * @param entId ID de la empresa
     * @param newState Nuevo estado (true para activo, false para inactivo)
     * @return Cantidad de terceros actualizados
     */
    int changeAllThirdsState(String entId, Boolean newState);
}
