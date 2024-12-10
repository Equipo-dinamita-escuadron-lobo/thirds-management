package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.multitenancy.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Clase que representa el contexto de inquilino.
 * Contiene los métodos para obtener y establecer el identificador de inquilino.
 */
@Slf4j
public class TenantContext {
    /**
     * Constructor privado.
     */
    private TenantContext() {}

    /**
     * Hilo local para el identificador de inquilino actual.
     */
    private static final InheritableThreadLocal<String> currentTenant = new InheritableThreadLocal<>();

    /**
     * Método para establecer el identificador de inquilino.
     * @param tenantId Identificador de inquilino.
     */
    public static void setTenantId(String tenantId) {
        log.debug("Setting tenantId to " + tenantId);
        currentTenant.set(tenantId);
    }

    /**
     * Método para obtener el identificador de inquilino.
     * @return Identificador de inquilino.
     */
    public static String getTenantId() {
        return currentTenant.get();
    }

    /**
     * Método para limpiar el identificador de inquilino.
     */
    public static void clear(){
        currentTenant.remove();
    }
}
