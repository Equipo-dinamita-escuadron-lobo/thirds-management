package com.thirdsmanagement.thirds.infrastructure.adapters.config;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.multitenancy.interceptor.TenantInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Clase de configuración de la aplicación web.
 */
@RequiredArgsConstructor
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    /**
     * Interceptor de inquilino.
     */
    private final TenantInterceptor tenantInterceptor;

    /**
     * Agrega los interceptores.
     * @param registry Registro de interceptores.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addWebRequestInterceptor(tenantInterceptor);
    }

}