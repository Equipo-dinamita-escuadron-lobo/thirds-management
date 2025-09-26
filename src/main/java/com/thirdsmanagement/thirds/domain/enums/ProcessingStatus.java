package com.thirdsmanagement.thirds.domain.enums;

import lombok.Getter;

/**
 * Estados posibles del procesamiento de un registro individual durante la importación.
 * Define el resultado del procesamiento de cada registro de tercero.
 */
@Getter
public enum ProcessingStatus {
    
    /**
     * El registro fue procesado y creado exitosamente.
     */
    SUCCESS("Éxito"),
    
    /**
     * El registro fue omitido por ser un duplicado existente.
     */
    DUPLICATE_SKIPPED("Duplicado Omitido"),
    
    /**
     * El procesamiento del registro falló debido a errores.
     */
    FAILED("Fallido"),
    
    /**
     * El registro fue omitido por razones de validación o datos incompletos.
     */
    SKIPPED("Omitido");

    private final String description;

    ProcessingStatus(String description) {
        this.description = description;
    }

    /**
     * Verifica si el estado indica un procesamiento exitoso.
     * 
     * @return true si el procesamiento fue exitoso
     */
    public boolean isSuccessful() {
        return this == SUCCESS;
    }

    /**
     * Verifica si el estado indica que el registro fue omitido.
     * 
     * @return true si el registro fue omitido por cualquier razón
     */
    public boolean wasSkipped() {
        return this == DUPLICATE_SKIPPED || this == SKIPPED;
    }

    /**
     * Verifica si el estado indica un fallo en el procesamiento.
     * 
     * @return true si el procesamiento falló
     */
    public boolean isFailed() {
        return this == FAILED;
    }

    /**
     * Verifica si el estado debe contarse como un duplicado.
     * 
     * @return true si fue omitido por ser duplicado
     */
    public boolean isDuplicate() {
        return this == DUPLICATE_SKIPPED;
    }
}
