package com.thirdsmanagement.thirds.application.ports.input;

/**
 * Interfaz que representa el caso de uso para la eliminación de un tercero.
 */
public interface DeleteThirdUseCase {
    /**
     * Elimina un tercero del sistema.
     * Valida que el tercero no tenga dependencias o relaciones activas antes de eliminarlo.
     * 
     * @param thirdId el ID del tercero a eliminar
     * @param entId el ID de la empresa
     * @return true si se eliminó correctamente, false en caso contrario
     * @throws com.thirdsmanagement.thirds.domain.exceptions.third.ThirdNotFound si el tercero no existe
     * @throws com.thirdsmanagement.thirds.domain.exceptions.third.ThirdHasDependenciesException si el tercero tiene dependencias
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    boolean deleteThird(Long thirdId, String entId);
}
