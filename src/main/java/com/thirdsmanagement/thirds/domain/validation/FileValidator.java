package com.thirdsmanagement.thirds.domain.validation;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interfaz para validadores de archivos.
 * Implementa el patrón Strategy para diferentes tipos de archivos.
 * Permite extensibilidad y centralización de la lógica de validación.
 */
public interface FileValidator {
    
    /**
     * Valida el archivo según reglas específicas del tipo.
     * 
     * @param file archivo a validar
     * @throws FileValidationException si la validación falla
     */
    void validate(MultipartFile file);
    
    /**
     * Indica los tipos MIME soportados por este validador.
     * 
     * @return array de tipos MIME soportados
     */
    String[] getSupportedMimeTypes();
    
    /**
     * Indica las extensiones soportadas por este validador.
     * 
     * @return array de extensiones soportadas
     */
    String[] getSupportedExtensions();
}
