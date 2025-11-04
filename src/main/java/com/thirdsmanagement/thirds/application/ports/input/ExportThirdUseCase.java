package com.thirdsmanagement.thirds.application.ports.input;

import org.springframework.core.io.Resource;

import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdExportRequest;

/**
 * @brief Caso de uso para exportación de terceros a Excel
 *
 * Proporciona funcionalidades para exportar plantillas y datos
 * de terceros en formato Excel con validaciones integradas.
 */
public interface ExportThirdUseCase {

    /**
     * @brief Exporta plantilla de terceros con validaciones
     *
     * Genera una plantilla Excel vacía con listas desplegables
     * y validaciones de datos para facilitar la importación.
     *
     * @param entId ID de la entidad
     * @return Resource que contiene la plantilla Excel con validaciones
     */
    Resource exportThirdTemplateWithValidations(String entId);

    /**
     * @brief Exporta terceros existentes con validaciones
     *
     * Exporta los datos actuales de terceros en formato Excel,
     * incluyendo validaciones y listas desplegables para edición.
     *
     * @param exportRequest Los filtros y parámetros para la exportación
     * @return Resource que contiene el archivo Excel con datos y validaciones
     */
    Resource exportThirdsWithValidations(ThirdExportRequest exportRequest);

}
