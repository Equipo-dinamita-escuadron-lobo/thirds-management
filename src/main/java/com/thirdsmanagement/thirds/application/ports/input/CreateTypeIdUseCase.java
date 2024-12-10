package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.domain.model.TypeId;

/**
 * Interfaz que define el metodo para crear un nuevo tipo de identificacion para un tercero.
 */
public interface CreateTypeIdUseCase {
    /**
     * Crea un nuevo tipo de identificacion para un tercero.
     * @param typeId El tipo de identificacion a crear
     * @return El tipo de identificacion creado
     */
    TypeId createTypeId(TypeId typeId);
} 
