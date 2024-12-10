package com.thirdsmanagement.thirds.application.ports.input;

import org.springframework.http.ResponseEntity;

/**
 * Interfaz que define el método para eliminar un tipo de tercero.
 */
public interface DeleteThirdTypeUseCase {
    /**
     * Elimina un tipo de tercero.
     * @param entId El id del tipo de tercero a eliminar
     * @return ResponseEntity<String> Mensaje de confirmación
     */
    ResponseEntity<String> deleteThirdTypeUseCase(Long entId);
}
