package com.thirdsmanagement.thirds.infrastructure.multitenancy.async;

import org.springframework.core.task.TaskDecorator;

import com.thirdsmanagement.thirds.infrastructure.multitenancy.utils.TenantContext;

/**
 * @brief Decorador de tareas que preserva contexto multi-tenant en operaciones asíncronas
 *
 * Implementa TaskDecorator para asegurar que las operaciones @Async ejecutadas en hilos
 * separados mantengan el contexto correcto del tenant. Captura el tenant del hilo principal
 * y lo restaura en el hilo de ejecución asíncrona, garantizando aislamiento de datos.
 */
public class TenantAwareTaskDecorator implements TaskDecorator {

    /**
     * @brief Envuelve tarea Runnable preservando contexto del tenant
     * @details Captura el tenant ID del hilo actual antes de la ejecución asíncrona,
     * crea un Runnable que restaura el tenant en el nuevo hilo, ejecuta la tarea,
     * y finalmente limpia el contexto para evitar memory leaks.
     * @param runnable tarea original a decorar
     * @return Runnable decorado con preservación de contexto tenant
     */
    @Override
    public Runnable decorate(Runnable runnable) {
        String tenantId = TenantContext.getTenantId();
        return () -> {
            try {
                TenantContext.setTenantId(tenantId);
                runnable.run();
            } finally {
                TenantContext.clear();
            }
        };
    }
}
