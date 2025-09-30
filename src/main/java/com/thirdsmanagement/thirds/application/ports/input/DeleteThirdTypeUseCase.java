package com.thirdsmanagement.thirds.application.ports.input;

/**
 * Interfaz que representa el caso de uso para la eliminación de un tipo de tercero.
 */
public interface DeleteThirdTypeUseCase {
    /**
     * Elimina un tipo de tercero del sistema.
     * Valida que el tipo de tercero no esté siendo utilizado por terceros existentes.
     * 
     * @param thirdTypeId el ID del tipo de tercero a eliminar
     * @param entId el ID de la empresa
     * @return true si se eliminó correctamente, false en caso contrario
     * @throws com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeNotFound si el tipo de tercero no existe
     * @throws com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeInUseException si el tipo de tercero está siendo utilizado
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    boolean deleteThirdType(Long thirdTypeId, String entId);
}
