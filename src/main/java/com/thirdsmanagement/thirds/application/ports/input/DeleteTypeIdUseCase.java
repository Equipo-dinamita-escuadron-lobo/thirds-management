package com.thirdsmanagement.thirds.application.ports.input;

/**
 * Interfaz que representa el caso de uso para la eliminación de un tipo de identificación.
 */
public interface DeleteTypeIdUseCase {
    /**
     * Elimina un tipo de identificación del sistema.
     * Valida que el tipo de identificación no esté siendo utilizado por terceros existentes.
     * 
     * @param typeIdId el ID del tipo de identificación a eliminar
     * @param entId el ID de la empresa
     * @return true si se eliminó correctamente, false en caso contrario
     * @throws com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdNotFound si el tipo de identificación no existe
     * @throws com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdInUseException si el tipo de identificación está siendo utilizado
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    boolean deleteTypeId(Long typeIdId, String entId);
}
