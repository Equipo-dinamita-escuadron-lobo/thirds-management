package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response;

import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     * Número de registros omitidos por ser duplicados.
     */
    private Integer duplicatesSkipped;

    /**
     * Lista de errores encontrados durante la importación.
     */
    private List<ImportErrorDetail> errors;

    // Enum ImportStatus movido a domain.enums para reutilización
}
