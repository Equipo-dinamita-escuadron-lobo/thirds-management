package com.thirdsmanagement.thirds.application.ports.input;

import org.springframework.http.ResponseEntity;

/**
 * Interfaz que define el metodo para eliminar el tipo de identificacion del tercero.
 */
public interface DeleteTypeIdUseCase {
    /**
     * Elimina el tipo de identificacion del tercero.
     * @param entId El id del tipo de identificacion a eliminar
     * @return ResponseEntity<String> Mensaje de confirmacion
     */
    ResponseEntity<String> deleteTypeId(Long entId);
}