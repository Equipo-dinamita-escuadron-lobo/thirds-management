package com.thirdsmanagement.thirds.infrastructure.multitenancy;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.thirdsmanagement.thirds.infrastructure.multitenancy.utils.TenantContext;

import java.util.Map;

/**
 * @brief Resolver de identificador de tenant actual para Hibernate multi-tenancy
 *
 * Implementa CurrentTenantIdentifierResolver para proporcionar a Hibernate el tenant ID
 * actual desde TenantContext. Registra automáticamente esta implementación en las
 * propiedades de Hibernate para habilitar aislamiento de datos por tenant.
 */
@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver<String>, HibernatePropertiesCustomizer {

    /**
     * @brief Resuelve dinámicamente el tenant ID actual para Hibernate
     * @details Obtiene el tenant ID desde TenantContext. Si no hay contexto establecido,
     * retorna "default" como fallback para desarrollo. Este método es llamado por Hibernate
     * en cada operación de base de datos para determinar qué tenant usar.
     * @return tenant ID actual o "default" si no hay contexto
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
     * @brief Indica validación de sesiones existentes al cambiar tenant
     * @details Retorna true para que Hibernate valide las sesiones existentes cuando
     * cambia el tenant ID, asegurando integridad de datos.
     * @return siempre true para habilitar validación de sesiones
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    /**
     * @brief Registra este resolver en propiedades de Hibernate
     * @details Método de HibernatePropertiesCustomizer que registra automáticamente
     * esta implementación como el resolver de tenant ID en las propiedades de Hibernate,
     * habilitando multi-tenancy sin configuración manual.
     * @param hibernateProperties mapa de propiedades de Hibernate a modificar
     */
    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }

}