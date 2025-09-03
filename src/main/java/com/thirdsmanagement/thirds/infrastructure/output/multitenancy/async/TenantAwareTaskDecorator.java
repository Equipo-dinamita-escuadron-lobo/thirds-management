package com.thirdsmanagement.thirds.infrastructure.output.multitenancy.async;

import org.springframework.core.task.TaskDecorator;

import com.thirdsmanagement.commons.multitenancy.utils.TenantContext;

/**
 * Decorador de tareas que preserva el contexto del tenant en operaciones asíncronas.
 * Garantiza que las tareas ejecutadas en hilos separados mantengan el tenant correcto.
 */
public class TenantAwareTaskDecorator implements TaskDecorator {

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
