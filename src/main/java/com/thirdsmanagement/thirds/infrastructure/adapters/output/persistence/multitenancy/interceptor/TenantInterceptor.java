package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.multitenancy.interceptor;


import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.multitenancy.util.TenantContext;
import com.thirdsmanagement.thirds.infrastructure.adapters.security.IJwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

/**
 * Clase que representa el interceptor de inquilino.
 * Contiene los métodos para interceptar las peticiones.
 */
@Component
public class TenantInterceptor implements WebRequestInterceptor {
    /**
     * Utilidades de JWT.
     */
    @Autowired
    private IJwtUtils jwtUtils;

    /**
     * Método para interceptar la petición antes de ser manejada.
     * @param request Petición web.
     */
    @Override
    public void preHandle(WebRequest request) throws Exception {
        TenantContext.setTenantId(jwtUtils.getId());
    }

    /**
     * Método para interceptar la petición después de ser manejada.
     * @param request Petición web.
     * @param model Modelo.
     */
    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {
        TenantContext.clear();
    }

    /**
     * Método para interceptar la petición después de ser completada.
     * @param request Petición web.
     * @param ex Excepción.
     */
    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {

    }
}
