package com.thirdsmanagement.thirds.infrastructure.multitenancy.async;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @brief Configuración de procesamiento asíncrono con soporte multi-tenant
 *
 * Implementa AsyncConfigurer para proporcionar un ejecutor de tareas que preserva
 * el contexto del tenant en operaciones @Async. Configura un ThreadPoolTaskExecutor
 * con TenantAwareTaskDecorator para garantizar aislamiento de datos entre tenants
 * en operaciones concurrentes.
 */
@Configuration
@EnableAsync
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class AsyncConfig implements AsyncConfigurer {

    /**
     * @brief Configura ejecutor asíncrono con preservación de contexto tenant
     * @details Crea ThreadPoolTaskExecutor con TenantAwareTaskDecorator que asegura
     * que cada tarea @Async mantenga el tenant ID correcto. Configura pool dinámico
     * con límites apropiados para operaciones concurrentes multi-tenant.
     * @return Executor configurado con decorador de contexto tenant
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(7);
        executor.setMaxPoolSize(42);
        executor.setQueueCapacity(11);
        executor.setThreadNamePrefix("TenantAwareTaskExecutor-");
        executor.setTaskDecorator(new TenantAwareTaskDecorator());
        executor.initialize();

        return executor;
    }

}