package com.thirdsmanagement.thirds.infrastructure.security;

/**
 * @brief Interfaz contrato para utilidades JWT
 *
 * Define contrato para acceso a información del contexto de seguridad JWT,
 * incluyendo ID de usuario y token de autenticación.
 */
public interface IJwtUtils {

    String getId();

    String getToken();

}
