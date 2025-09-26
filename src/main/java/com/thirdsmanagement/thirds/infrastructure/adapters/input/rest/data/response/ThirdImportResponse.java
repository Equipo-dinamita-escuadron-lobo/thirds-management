package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuestas de importación masiva de terceros.
 * Contiene estadísticas del proceso y reportes de errores.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdImportResponse {

    /**
     * Identificador único del proceso de importación.
     */
    private String importId;

    /**
     * Identificador de la entidad.
     */
    private String entId;

    /**
     * Nombre del archivo procesado.
     */
    private String fileName;


    /**
     * Estado del proceso de importación.
     */
    private ImportStatus status;

    /**
     * Número total de registros encontrados en el Excel.
     */
    private Integer totalRecords;

    /**
     * Número de registros procesados exitosamente.
     */
    private Integer successfulImports;

    /**
     * Número de registros que fallaron.
     */
    private Integer failedImports;

    /**
     * Número de registros omitidos (duplicados, inválidos, etc.).
     */
    private Integer skippedRecords;

    /**
     * Número de registros omitidos por ser duplicados.
     */
    private Integer duplicatesSkipped;

    /**
     * Lista de errores encontrados durante la importación.
     */
    private List<ImportErrorDetail> errors;

    /**
     * Estados posibles del proceso de importación.
     */
    public enum ImportStatus {
        IN_PROGRESS("En progreso"),
        COMPLETED("Completado"),
        COMPLETED_WITH_ERRORS("Completado con errores"),
        FAILED("Fallido"),
        CANCELLED("Cancelado");

        private final String description;

        ImportStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
