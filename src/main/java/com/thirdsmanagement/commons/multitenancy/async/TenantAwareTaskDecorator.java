package com.thirdsmanagement.commons.multitenancy.async;

import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;

import com.thirdsmanagement.commons.multitenancy.util.TenantContext;

/**
 * Clase que representa el decorador de tareas consciente del inquilino.
 * Contiene el método para decorar una tarea con el inquilino actual.
 */
public class TenantAwareTaskDecorator implements TaskDecorator {
    /**
     * Método para decorar una tarea con el inquilino actual.
     * @param runnable Tarea a decorar.
     * @return Tarea decorada con el inquilino actual.
     */
    @Override
    @NonNull
    public Runnable decorate(@NonNull Runnable runnable) {
        String tenantId = TenantContext.getTenantId();
        return () -> {
            try {
                TenantContext.setTenantId(tenantId);
                runnable.run();
            } finally {
                TenantContext.setTenantId(null);
            }
        };
    }
}