package com.thirdsmanagement.thirds.infrastructure.output.multitenancy;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.thirdsmanagement.commons.multitenancy.utils.TenantContext;
import com.thirdsmanagement.thirds.infrastructure.output.multitenancy.async.TenantAwareTaskDecorator;

import jakarta.annotation.PreDestroy;

/**
 * Configuración de multi-tenancy para la aplicación.
 * Establece el tenant por defecto y configura el manejo de tareas asíncronas.
 */
@Configuration
@EnableAsync
public class TenantConfig {

    private static final String DEFAULT_TENANT = "default";

    /**
     * Evento que se ejecuta cuando la aplicación está lista.
     * Establece el tenant por defecto para evitar errores de Hibernate.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        TenantContext.setTenantId(DEFAULT_TENANT);
    }

    /**
     * Configura el ejecutor de tareas asíncronas con soporte para multi-tenancy.
     */
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("Thirds-");
        executor.setTaskDecorator(tenantAwareTaskDecorator());
        executor.initialize();
        return executor;
    }

    /**
     * Decorador que preserva el contexto del tenant en operaciones asíncronas.
     */
    @Bean
    public TaskDecorator tenantAwareTaskDecorator() {
        return new TenantAwareTaskDecorator();
    }

    /**
     * Método de limpieza que se ejecuta antes de destruir el bean.
     * Limpia el contexto del tenant.
     */
    @PreDestroy
    public void cleanup() {
        TenantContext.clear();
    }
}
