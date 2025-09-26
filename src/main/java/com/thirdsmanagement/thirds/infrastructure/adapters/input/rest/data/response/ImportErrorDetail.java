package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un error específico durante la importación.
 * Proporciona información detallada para facilitar la corrección.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportErrorDetail {

    /**
     * Número de fila en el Excel donde ocurrió el error (base 1).
     */
    private Integer rowNumber;

    /**
     * Número de columna donde ocurrió el error (base 1).
     */
    private Integer columnNumber;

    /**
     * Nombre de la columna donde ocurrió el error.
     */
    private String columnName;

    /**
     * Valor que causó el error.
     */
    private String fieldValue;

    /**
     * Código del error para categorización.
     */
    private String errorCode;

    /**
     * Mensaje descriptivo del error.
     */
    private String errorMessage;

    /**
     * Tipo de error para clasificación.
     */
    private ErrorType errorType;

    /**
     * Tipos de errores de importación.
     */
    public enum ErrorType {
        VALIDATION_ERROR("Error de validación"),
        DUPLICATE_ERROR("Error de duplicado"),
        REFERENCE_ERROR("Error de referencia"),
        FORMAT_ERROR("Error de formato"),
        BUSINESS_RULE_ERROR("Error de regla de negocio"),
        SYSTEM_ERROR("Error del sistema");

        private final String description;

        ErrorType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
