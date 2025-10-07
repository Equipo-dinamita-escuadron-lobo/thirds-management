package com.thirdsmanagement.thirds.domain.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.thirdsmanagement.thirds.domain.enums.ExportableField;

/**
 * Configuración de exportación para terceros.
 * Implementa el patrón Builder para construcción flexible de configuraciones.
 */
public class ExportConfiguration {

    private final Set<ExportableField> includedFields;

    // Constructor privado - solo accesible desde Builder
    private ExportConfiguration(Builder builder) {
        this.includedFields = Collections.unmodifiableSet(builder.includedFields);
    }

    public Set<ExportableField> getIncludedFields() {
        return includedFields;
    }

    /**
     * Verifica si un campo específico debe incluirse en la exportación.
     * 
     * @param field campo a verificar
     * @return true si el campo debe incluirse
     */
    public boolean includes(ExportableField field) {
        return includedFields.contains(field);
    }

    /**
     * Crea un nuevo Builder para construcción de configuraciones.
     * 
     * @return nuevo Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder para construcción flexible de ExportConfiguration.
     * Implementa patrón Builder con método fluent.
     */
    public static class Builder {
        private final Set<ExportableField> includedFields;

        private Builder() {
            this.includedFields = EnumSet.noneOf(ExportableField.class);
        }

        /**
         * Incluye un campo específico en la exportación.
         * 
         * @param field campo a incluir
         * @return este Builder para encadenamiento
         */
        public Builder includeField(ExportableField field) {
            if (field != null) {
                this.includedFields.add(field);
            }
            return this;
        }

        /**
         * Incluye múltiples campos en la exportación.
         * 
         * @param fields campos a incluir
         * @return este Builder para encadenamiento
         */
        public Builder includeFields(Set<ExportableField> fields) {
            if (fields != null) {
                this.includedFields.addAll(fields);
            }
            return this;
        }

        /**
         * Incluye todos los campos disponibles en la exportación.
         * 
         * @return este Builder para encadenamiento
         */
        public Builder includeAllFields() {
            this.includedFields.addAll(EnumSet.allOf(ExportableField.class));
            return this;
        }

        /**
         * Excluye un campo específico de la exportación.
         * 
         * @param field campo a excluir
         * @return este Builder para encadenamiento
         */
        public Builder excludeField(ExportableField field) {
            if (field != null) {
                this.includedFields.remove(field);
            }
            return this;
        }

        /**
         * Limpia todos los campos incluidos.
         * 
         * @return este Builder para encadenamiento
         */
        public Builder clear() {
            this.includedFields.clear();
            return this;
        }

        /**
         * Construye la configuración inmutable.
         * 
         * @return ExportConfiguration construida
         */
        public ExportConfiguration build() {
            return new ExportConfiguration(this);
        }
    }
}
