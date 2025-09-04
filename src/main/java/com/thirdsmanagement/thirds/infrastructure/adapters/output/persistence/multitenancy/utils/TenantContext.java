package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.multitenancy.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase utilitaria para manejar el contexto del inquilino (tenant) en un hilo de ejecución.
 * Utiliza un InheritableThreadLocal para almacenar el ID del inquilino.
 */
@Slf4j
public class TenantContext {
    private TenantContext() {}

    private static final InheritableThreadLocal<String> currentTenant = new InheritableThreadLocal<>();

    /**
     * Establece el ID del inquilino en el contexto actual.
     * @param tenantId ID del inquilino que se va a establecer.
     */
    public static void setTenantId(String tenantId) {
        log.debug("Setting tenantId to " + tenantId);
        currentTenant.set(tenantId);
    }

    /**
     * Obtiene el ID del inquilino del contexto actual.
     * @return ID del inquilino actualmente establecido.
     */
    public static String getTenantId() {
        return currentTenant.get();
    }

    /**
     * Limpia el contexto del inquilino actual, removiendo el ID del inquilino.
     */
    public static void clear(){
        currentTenant.remove();
    }
}