package com.thirdsmanagement.thirds.application.ports.input;

import java.util.List;

import com.thirdsmanagement.thirds.domain.model.TypeId;

/**
 * Interfaz que representa el caso de uso para listar los tipos de identificacion.
 */
public interface ListTypeIdUseCase {
    /**
     * Obtiene todos los tipos de identidicacion
     * @param entId Identificador de la empresa
     * @return Lista de tipos de identificacion
     */
    List<TypeId> getAllTypeId(String entId);
    
} 
