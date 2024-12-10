package com.thirdsmanagement.thirds.application.ports.input;

import java.util.List;

import com.thirdsmanagement.thirds.domain.model.ThirdType;

/**
 * Interfaz que representa el caso de uso para listar los tipos de terceros.
 */
public interface ListThirdTypeUseCase {
    /**
     * Obtiene todos los tipos de terceros
     * @param entId Identificador de la empresa
     * @return Lista de tipos de terceros
     */
    List<ThirdType> getAllThirdTypes(String entId);
    
} 
