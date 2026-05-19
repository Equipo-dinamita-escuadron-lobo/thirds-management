package com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.controller;

import com.thirdsmanagement.thirds.copy.application.input.ICancelThirdsCopyPort;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyCancelResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para cancelar un proceso de copia de terceros.
 * Endpoint: POST /api/thirds/copy/cancel/{idProceso}
 */
@RestController
@RequestMapping("/api/thirds/copy")
@RequiredArgsConstructor
public class CopyThirdsCancelController {

    private final ICancelThirdsCopyPort cancelPort;

    /**
     * Cancela el proceso de copia en curso.
     *
     * @param idProceso identificador UUID del proceso
     * @return respuesta con estado CANCELADO
     */
    @PostMapping("/cancel/{idProceso}")
    public ResponseEntity<CopyCancelResponseDto> cancelar(@PathVariable String idProceso) {
        CopyCancelResponseDto response = cancelPort.cancelar(idProceso);
        return ResponseEntity.ok(response);
    }
}
