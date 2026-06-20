package com.thirdsmanagement.thirds.infrastructure.security;

import java.util.List;

/**
 * @brief Interfaz contrato para utilidades JWT
 *
 *        Define contrato para acceso a información del contexto de seguridad
 *        JWT,
 *        incluyendo ID de usuario y token de autenticación.
 */
public interface IJwtUtils {

    String getId();

    String getToken();

    String getUsername();

    List<String> getRealmRoles();

}
