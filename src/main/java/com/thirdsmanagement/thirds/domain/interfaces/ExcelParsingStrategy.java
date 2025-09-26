package com.thirdsmanagement.thirds.domain.interfaces;

import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ImportErrorDetail;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Estrategia genérica para parseo de archivos Excel.
 * Define el contrato para diferentes tipos de parseo de Excel
 * permitiendo reutilización en otros módulos del sistema.
 * 
 * @param <T> tipo de dato que se extrae del Excel
 */
public interface ExcelParsingStrategy<T> {

    /**
     * Resultado del parseo de Excel con datos y metadatos.
     * 
     * @param <T> tipo de datos parseados
     */
    interface ExcelParsingResult<T> {
        List<T> getData();
        List<ImportErrorDetail> getErrors();
        Map<String, Integer> getColumnMap();
        int getTotalRows();
    }

    /**
     * Parsea un archivo Excel y extrae los datos del tipo especificado.
     * 
     * @param file archivo Excel a parsear
     * @param entityId identificador de la entidad (para contexto)
     * @return resultado del parseo con datos y errores
     */
    ExcelParsingResult<T> parseExcelFile(MultipartFile file, String entityId);

    /**
     * Valida el formato básico del archivo Excel.
     * 
     * @param file archivo a validar
     * @throws IllegalArgumentException si el archivo no es válido
     */
    default void validateExcelFile(MultipartFile file) {
        com.thirdsmanagement.thirds.domain.utils.ExcelUtils.validateExcelFile(file);
    }

    /**
     * Obtiene los encabezados requeridos para este tipo de parseo.
     * 
     * @return array de encabezados requeridos
     */
    String[] getRequiredHeaders();

    /**
     * Obtiene los encabezados opcionales para este tipo de parseo.
     * 
     * @return array de encabezados opcionales
     */
    default String[] getOptionalHeaders() {
        return new String[0];
    }
}
