package com.thirdsmanagement.thirds.domain.interfaces;

import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ImportErrorDetail;

import java.util.List;
import java.util.Map;

/**
 * Estrategia genérica para validación en lotes de datos de importación.
 * Define el contrato para validar diferentes tipos de datos
 * permitiendo reutilización en otros módulos del sistema.
 * 
 * @param <T> tipo de dato a validar
 */
public interface BatchValidationStrategy<T> {

    /**
     * Resultado de la validación en lotes.
     * 
     * @param <T> tipo de datos validados
     */
    interface BatchValidationResult<T> {
        List<T> getValidRecords();
        List<ImportErrorDetail> getErrors();
        int getValidCount();
        int getErrorCount();
    }

    /**
     * Valida un lote de datos aplicando todas las reglas correspondientes.
     * 
     * @param data lista de datos a validar
     * @param entityId identificador de la entidad (para contexto)
     * @param columnMap mapa de columnas para reportes de errores
     * @return resultado de la validación con registros válidos y errores
     */
    BatchValidationResult<T> validateBatch(List<T> data, String entityId, Map<String, Integer> columnMap);

    /**
     * Valida campos básicos y formato de un registro individual.
     * 
     * @param data registro a validar
     * @param errors lista donde agregar errores encontrados
     * @param columnMap mapa de columnas para reportes de errores
     */
    void validateBasicFields(T data, List<ImportErrorDetail> errors, Map<String, Integer> columnMap);

    /**
     * Valida referencias a datos maestros de un registro individual.
     * 
     * @param data registro a validar
     * @param errors lista donde agregar errores encontrados
     * @param columnMap mapa de columnas para reportes de errores
     */
    void validateReferences(T data, List<ImportErrorDetail> errors, Map<String, Integer> columnMap);

    /**
     * Valida reglas de negocio específicas de un registro individual.
     * 
     * @param data registro a validar
     * @param errors lista donde agregar errores encontrados
     * @param columnMap mapa de columnas para reportes de errores
     */
    void validateBusinessRules(T data, List<ImportErrorDetail> errors, Map<String, Integer> columnMap);

    /**
     * Pre-carga datos de referencia necesarios para la validación.
     * Este método se llama una vez por lote para optimizar el rendimiento.
     * 
     * @param entityId identificador de la entidad
     */
    default void preloadReferenceData(String entityId) {
        // Implementación por defecto vacía
    }
}
