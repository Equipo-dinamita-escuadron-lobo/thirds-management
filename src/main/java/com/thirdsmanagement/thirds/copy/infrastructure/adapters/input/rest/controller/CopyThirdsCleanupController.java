package com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.controller;

import com.thirdsmanagement.thirds.copy.application.input.ICleanupThirdsCopyPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para limpiar los registros de log de un proceso de copia.
 * Endpoint: DELETE /api/thirds/copy/cleanup/{idProceso}
 */
@RestController
@RequestMapping("/api/thirds/copy")
@RequiredArgsConstructor
public class CopyThirdsCleanupController {

    private final ICleanupThirdsCopyPort cleanupPort;

    /**
     * Elimina los registros de log del proceso indicado.
     *
     * @param idProceso identificador UUID del proceso
     * @return 204 No Content
     */
    @DeleteMapping("/cleanup/{idProceso}")
    public ResponseEntity<Void> limpiar(@PathVariable String idProceso) {
        cleanupPort.limpiar(idProceso);
        return ResponseEntity.noContent().build();
    }
}
