package com.thirdsmanagement.thirds.infrastructure.multitenancy.interceptor;

import com.thirdsmanagement.thirds.infrastructure.multitenancy.utils.TenantContext;
import com.thirdsmanagement.thirds.infrastructure.security.IJwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

/**
 * @brief Interceptor web que maneja contexto multi-tenant basado en JWT
 *
 * Implementa WebRequestInterceptor para extraer el tenant ID desde tokens JWT
 * en cada solicitud HTTP. Establece el contexto del tenant antes del procesamiento
 * de la solicitud y lo limpia después, garantizando aislamiento de datos entre tenants.
 */
@Component
public class TenantInterceptor implements WebRequestInterceptor {

    @Autowired
    private IJwtUtils jwtUtils;

    /**
     * @brief Establece tenant ID en contexto desde JWT antes de procesar solicitud
     * @details Extrae el tenant ID del token JWT actual. Si no hay JWT válido,
     * establece tenant por defecto ("default"). Maneja errores gracefully
     * para asegurar que siempre haya un tenant establecido.
     * @param request solicitud web entrante
     * @throws Exception si ocurre error durante el procesamiento
     */
    @Override
    public void preHandle(WebRequest request) throws Exception {
        try {
            String tenantId = jwtUtils.getId();
            if (tenantId != null && !tenantId.trim().isEmpty()) {
                TenantContext.setTenantId(tenantId);
            } else {
                // Usar tenant por defecto si no hay JWT disponible
                TenantContext.setTenantId("default");
            }
        } catch (Exception e) {
            // En caso de error, usar tenant por defecto
            TenantContext.setTenantId("default");
        }
    }

    /**
     * @brief Limpia contexto del tenant después del procesamiento de la solicitud
     * @details Libera el contexto del tenant para evitar memory leaks y asegurar
     * que solicitudes subsiguientes no hereden el tenant de la solicitud anterior.
     * @param request solicitud web procesada
     * @param model modelo de datos de la respuesta
     * @throws Exception si ocurre error durante la limpieza
     */
    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        TenantContext.clear();
    }

    /**
     * @brief Método de finalización de solicitud (sin implementación específica)
     * @details Método requerido por WebRequestInterceptor, no realiza acciones adicionales
     * en este interceptor específico ya que la limpieza se hace en postHandle.
     * @param request solicitud web completada
     * @param ex excepción ocurrida durante el procesamiento (puede ser null)
     * @throws Exception si ocurre error en la finalización
     */
    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {
        // No se realiza ninguna acción adicional en este interceptor
    }
}