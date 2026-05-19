package com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.controller;

import com.thirdsmanagement.thirds.copy.application.input.IGetThirdsCopyStatusPort;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyStatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para consultar el estado de un proceso de copia de terceros.
 * Endpoint: GET /api/thirds/copy/status/{idProceso}
 */
@RestController
@RequestMapping("/api/thirds/copy")
@RequiredArgsConstructor
public class CopyThirdsStatusController {

    private final IGetThirdsCopyStatusPort statusPort;

    /**
     * Obtiene el estado actual del proceso de copia.
     *
     * @param idProceso identificador UUID del proceso
     * @return estado del proceso
     */
    @GetMapping("/status/{idProceso}")
    public ResponseEntity<CopyStatusResponseDto> obtenerEstado(@PathVariable String idProceso) {
        CopyStatusResponseDto response = statusPort.obtenerEstado(idProceso);
        return ResponseEntity.ok(response);
    }
}
