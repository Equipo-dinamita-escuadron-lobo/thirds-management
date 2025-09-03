package com.thirdsmanagement.commons.multitenancy;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.thirdsmanagement.commons.multitenancy.util.TenantContext;

import java.util.Map;

/**
 * Clase que representa el resolutor de identificador de inquilino actual.
 * Contiene los métodos para resolver el identificador de inquilino actual.
 */
@Component
class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {
    /**
     * Método para resolver el identificador de inquilino actual.
     * @return Identificador de inquilino actual.
     * Si no se encuentra un identificador de inquilino, se devuelve "BOOTSTRAP".
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getTenantId();
        if (!ObjectUtils.isEmpty(tenantId)) {
            return tenantId;
        } else {
            // Allow bootstrapping the EntityManagerFactory, in which case no tenant is needed
            return "BOOTSTRAP";
        }
    }

    /**
     * Método para validar las sesiones actuales existentes.
     * @return Verdadero si se deben validar las sesiones actuales existentes.
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    /**
     * Método para personalizar las propiedades de Hibernate.
     * @param hibernateProperties Propiedades de Hibernate.
     */
    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }

}