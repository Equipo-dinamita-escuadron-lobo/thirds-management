package com.thirdsmanagement.thirds.infrastructure.security;

/**
 * @brief Interfaz para operaciones con tokens JWT en el contexto de multi-tenancy
 *
 * Define contrato para extraer información del tenant desde tokens JWT.
 * Implementaciones específicas manejan diferentes formatos y algoritmos de JWT.
 */
public interface IJwtUtils {
    
    String getId();

}