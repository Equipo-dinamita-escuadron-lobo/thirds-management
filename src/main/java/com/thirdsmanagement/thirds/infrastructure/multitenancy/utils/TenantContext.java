package com.thirdsmanagement.thirds.infrastructure.multitenancy.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @brief Utilidad para gestión de contexto multi-tenant con InheritableThreadLocal
 *
 * Clase utilitaria que maneja el contexto del tenant usando InheritableThreadLocal
 * para asegurar que los hilos hijos hereden el contexto del tenant padre.
 * Esencial para mantener aislamiento de datos en operaciones multi-threaded.
 */
@Slf4j
public class TenantContext {

    private static final InheritableThreadLocal<String> currentTenant = new InheritableThreadLocal<>();

    /**
     * @brief Establece el ID del tenant en el contexto del hilo actual
     * @details Almacena el tenant ID en InheritableThreadLocal para que esté disponible
     * en el hilo actual y sea heredado por hilos hijos en operaciones asíncronas.
     * @param tenantId identificador del tenant a establecer
     */
    public static void setTenantId(String tenantId) {
        log.debug("Setting tenantId to " + tenantId);
        currentTenant.set(tenantId);
    }

    /**
     * @brief Obtiene el ID del tenant del contexto del hilo actual
     * @details Recupera el tenant ID almacenado en el InheritableThreadLocal del hilo actual.
     * Retorna null si no hay tenant establecido.
     * @return ID del tenant actual o null si no está establecido
     */
    public static String getTenantId() {
        return currentTenant.get();
    }

    /**
     * @brief Limpia el contexto del tenant para evitar memory leaks
     * @details Remueve el tenant ID del InheritableThreadLocal del hilo actual.
     * Esencial para limpieza después del procesamiento de requests HTTP.
     */
    public static void clear(){
        currentTenant.remove();
    }
}