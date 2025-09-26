package com.thirdsmanagement.thirds.domain.interfaces;

import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ImportErrorDetail;

import java.util.List;

/**
 * Estrategia genérica para detección de duplicados durante la importación.
 * Define el contrato para detectar duplicados en diferentes tipos de datos
 * permitiendo reutilización en otros módulos del sistema.
 * 
 * @param <T> tipo de dato para detección de duplicados
 */
public interface DuplicateDetectionStrategy<T> {

    /**
     * Resultado de la detección de duplicados.
     * 
     * @param <T> tipo de datos únicos
     */
    interface DuplicateDetectionResult<T> {
        List<T> getUniqueRecords();
        List<ImportErrorDetail> getErrors();
        int getUniqueCount();
        int getDuplicateCount();
    }

    /**
     * Detecta duplicados en un lote de datos.
     * 
     * @param data lista de datos a analizar
     * @param entityId identificador de la entidad (para contexto)
     * @param skipDuplicates indica si se deben omitir duplicados o marcar como error
     * @return resultado con registros únicos y reportes de duplicados
     */
    DuplicateDetectionResult<T> detectDuplicates(List<T> data, String entityId, boolean skipDuplicates);

    /**
     * Detecta duplicados internos dentro del mismo lote de datos.
     * 
     * @param data lista de datos a analizar
     * @param skipDuplicates indica si se deben omitir duplicados o marcar como error
     * @return resultado de análisis interno
     */
    DuplicateDetectionResult<T> detectInternalDuplicates(List<T> data, boolean skipDuplicates);

    /**
     * Detecta duplicados comparando con datos existentes en la base de datos.
     * 
     * @param data lista de datos únicos internamente
     * @param entityId identificador de la entidad
     * @param skipDuplicates indica si se deben omitir duplicados o marcar como error
     * @return resultado de análisis con base de datos
     */
    DuplicateDetectionResult<T> detectDatabaseDuplicates(List<T> data, String entityId, boolean skipDuplicates);

    /**
     * Genera una clave única para identificar duplicados de un registro.
     * 
     * @param data registro del cual generar la clave
     * @param entityId identificador de la entidad
     * @return clave única para comparación
     */
    String generateDuplicateKey(T data, String entityId);

    /**
     * Verifica si un registro ya existe en la base de datos.
     * 
     * @param data registro a verificar
     * @param entityId identificador de la entidad
     * @return true si el registro ya existe
     */
    boolean existsInDatabase(T data, String entityId);
}
