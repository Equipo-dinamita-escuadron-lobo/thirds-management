package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.domain.model.TypeId;

/**
 * Interfaz que define el método para actualizar un tipo de identificación.
 */
public interface UpdateTypeIdUseCase {
    /**
     * Actualiza un tipo de identificación existente.
     * @param typeId El tipo de identificación a actualizar
     * @return El tipo de identificación actualizado
     */
    TypeId updateTypeId(TypeId typeId);
}
