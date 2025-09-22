package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdExportRequest;
import org.springframework.core.io.Resource;

/**
 * Caso de uso para exportar terceros en formato Excel.
 */
public interface ExportThirdUseCase {

    /**
     * Exporta una plantilla de terceros con validaciones de datos (listas desplegables).
     *
     * @param entId ID de la entidad
     * @return Resource que contiene la plantilla Excel con validaciones
     */
    Resource exportThirdTemplateWithValidations(String entId);

    /**
     * Exporta terceros existentes con validaciones de datos (listas desplegables).
     * Combina los datos reales con las validaciones de la plantilla.
     *
     * @param exportRequest Los filtros y parámetros para la exportación
     * @return Resource que contiene el archivo Excel con datos y validaciones
     */
    Resource exportThirdsWithValidations(ThirdExportRequest exportRequest);

}
