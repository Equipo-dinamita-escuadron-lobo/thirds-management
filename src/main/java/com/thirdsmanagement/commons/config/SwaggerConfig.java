package com.thirdsmanagement.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Clase de configuración para Swagger/OpenAPI.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configura y personaliza la especificación OpenAPI para la API de gestión de productos.
     *
     * @return OpenAPI configurado.
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
