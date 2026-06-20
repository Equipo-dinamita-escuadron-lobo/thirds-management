package com.thirdsmanagement.thirds.infrastructure.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.thirdsmanagement.thirds.infrastructure.multitenancy.async.TenantAwareTaskDecorator;
import com.thirdsmanagement.thirds.infrastructure.multitenancy.utils.TenantContext;

import jakarta.annotation.PreDestroy;

/**
 * @brief Configuración centralizada de multi-tenancy y procesamiento asíncrono
 *
 * Establece tenant por defecto al arranque, configura pool de hilos para operaciones
 * asíncronas con preservación de contexto multi-tenant, y maneja limpieza de recursos.
 * Garantiza aislamiento de datos entre tenants en operaciones concurrentes.
 */
@Configuration
@EnableAsync
public class TenantConfig {

    private static final String DEFAULT_TENANT = "default";

    /**
     * @brief Inicializa tenant por defecto al arranque de la aplicación
     * @details Evita errores de Hibernate al establecer un tenant válido antes
     * de cualquier operación de base de datos.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        TenantContext.setTenantId(DEFAULT_TENANT);
    }

    /**
     * @brief Configura pool de hilos asíncronos con soporte multi-tenancy
     * @details Crea ThreadPoolTaskExecutor con TaskDecorator que preserva el contexto
     * del tenant en operaciones @Async. Configura pool dinámico con límites apropiados.
     * @return ThreadPoolTaskExecutor configurado para operaciones concurrentes
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
     * @brief Proporciona decorador que preserva contexto multi-tenant en hilos
     * @details Bean que crea instancia de TenantAwareTaskDecorator para asegurar
     * que las operaciones asíncronas mantengan el tenant correcto.
     * @return TaskDecorator configurado para preservar contexto de tenant
     */
    @Bean
    public TaskDecorator tenantAwareTaskDecorator() {
        return new TenantAwareTaskDecorator();
    }

    /**
     * @brief Limpieza de recursos antes de destruir el bean de configuración
     * @details Libera el contexto del tenant para evitar memory leaks y asegurar
     * limpieza apropiada de recursos durante el shutdown de la aplicación.
     */
    @PreDestroy
    public void cleanup() {
        TenantContext.clear();
    }
}
