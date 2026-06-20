package com.thirdsmanagement.thirds.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * @brief Configuración de documentación OpenAPI/Swagger para la API REST
 *
 * Configura la especificación OpenAPI 3.0 con autenticación JWT Bearer,
 * información del API y esquemas de seguridad para la documentación interactiva.
 */
@Configuration
public class SwaggerConfig {

    /**
     * @brief Configura especificación OpenAPI con autenticación JWT
     * @details Crea configuración completa de OpenAPI con esquema de seguridad Bearer JWT,
     * metadatos del API (título, descripción, versión) y requerimientos de seguridad globales.
     * @return OpenAPI configurado con esquema JWT y metadatos del API
     */
    @Bean
    public OpenAPI customizeOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .info(new Info().title("Thirds Management API")
                        .description("API para la gestión de terceros")
                        .version("1.0"));
    }
}
