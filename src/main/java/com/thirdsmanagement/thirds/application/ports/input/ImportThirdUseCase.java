package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdImportRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ThirdImportResponse;

/**
 * @brief Caso de uso para importación masiva de terceros desde Excel
 *
 * Define el contrato para la funcionalidad de importación con omisión automática de duplicados:
 * - Los duplicados se omiten automáticamente para mejorar usabilidad
 * - Se procesan archivos grandes sin límite de lote específico
 * - La importación se detiene al primer error de validación
 * - Permite reimportar archivos corregidos omitiendo registros existentes
 */
public interface ImportThirdUseCase {

    /**
     * @brief Importa terceros masivamente desde archivo Excel
     *
     * Importa terceros desde un archivo Excel con omisión automática de duplicados.
     * Los registros duplicados se omiten silenciosamente permitiendo reimportaciones del mismo archivo.
     *
     * @param importRequest la solicitud de importación con el archivo
     * @return el resultado de la importación con estadísticas detalladas incluyendo duplicados omitidos
     * @throws IllegalArgumentException si los parámetros son inválidos
     * @throws ExcelValidationException si el archivo Excel tiene formato inválido
     * @throws ThirdImportException si ocurre un error durante la importación
     */
    ThirdImportResponse importThirdsFromExcel(ThirdImportRequest importRequest);
}
