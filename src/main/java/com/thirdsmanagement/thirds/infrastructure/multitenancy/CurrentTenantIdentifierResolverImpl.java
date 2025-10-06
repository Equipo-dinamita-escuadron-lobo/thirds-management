package com.thirdsmanagement.thirds.infrastructure.multitenancy;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.thirdsmanagement.thirds.infrastructure.multitenancy.utils.TenantContext;

import java.util.Map;

/**
 * Implementación de resolver de identificador de inquilino actual para Hibernate.
 * Esta clase determina dinámicamente el inquilino actual basado en el contexto de TenantContext.
 */
@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver<String>, HibernatePropertiesCustomizer {

    /**
     * Resuelve el identificador del inquilino actual.
     * @return ID del inquilino actual o "default" si no hay inquilino configurado.
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getTenantId();
        if (!ObjectUtils.isEmpty(tenantId)) {
            return tenantId;
        } else {
            // Usar un tenant por defecto para desarrollo cuando no hay contexto
            return "default";
        }
    }

    /**
     * Indica si se deben validar las sesiones actuales existentes.
     * @return Siempre devuelve true para validar las sesiones existentes.
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    /**
     * Personaliza las propiedades de Hibernate añadiendo este resolver como identificador de inquilino.
     * @param hibernateProperties Mapa de propiedades de Hibernate a personalizar.
     */
    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }

}