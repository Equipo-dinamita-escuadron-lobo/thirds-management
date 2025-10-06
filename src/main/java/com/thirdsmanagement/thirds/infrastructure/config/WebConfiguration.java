package com.thirdsmanagement.thirds.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.multitenancy.interceptor.TenantInterceptor;

import lombok.RequiredArgsConstructor;

/**
 * Clase de configuración para la configuración web MVC.
 * Configura un interceptor para manejar la lógica de multitenancy basada en web requests.
 */
@RequiredArgsConstructor
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final TenantInterceptor tenantInterceptor;

    /**
     * Registra el interceptor de inquilino (tenant) en el registro de interceptores.
     *
     * @param registry el registro de interceptores de la configuración web MVC.
     */
    @Override
    public void addInterceptors(@SuppressWarnings("null") InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(tenantInterceptor);
    }

}
