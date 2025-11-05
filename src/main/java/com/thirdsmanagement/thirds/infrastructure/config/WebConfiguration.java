package com.thirdsmanagement.thirds.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.thirdsmanagement.thirds.infrastructure.multitenancy.interceptor.TenantInterceptor;

import lombok.RequiredArgsConstructor;

/**
 * @brief Configuración web MVC con soporte para multi-tenancy
 *
 * Implementa WebMvcConfigurer para registrar interceptores que manejan
 * la lógica de multi-tenancy basada en requests HTTP. Extrae información
 * del tenant de headers o parámetros de request.
 */
@RequiredArgsConstructor
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final TenantInterceptor tenantInterceptor;

    @Override
    public void addInterceptors(@SuppressWarnings("null") InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(tenantInterceptor);
    }

}
