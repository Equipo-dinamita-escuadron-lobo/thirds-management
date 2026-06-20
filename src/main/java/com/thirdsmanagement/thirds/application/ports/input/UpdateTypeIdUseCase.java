package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.domain.model.TypeId;

/**
 * @brief Caso de uso para actualización de tipos de identificación
 *
 * Permite modificar tipos de identificación existentes
 * con validación de reglas de negocio e integridad.
 */
public interface UpdateTypeIdUseCase {
    /**
     * @brief Actualiza un tipo de identificación existente
     * @param typeId El tipo de identificación a actualizar
     * @return El tipo de identificación actualizado
     */
    TypeId updateTypeId(TypeId typeId);
}
