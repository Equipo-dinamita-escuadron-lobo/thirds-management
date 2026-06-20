package com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.controller;

import com.thirdsmanagement.thirds.copy.application.input.IExecuteThirdsCopyPhasePort;
import com.thirdsmanagement.thirds.copy.domain.enums.CopyEstado;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyPhaseRequestDto;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyPhaseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para ejecutar una fase del proceso de copia de terceros.
 * Endpoint: POST /api/thirds/copy/phase
 */
@RestController
@RequestMapping("/api/thirds/copy")
@RequiredArgsConstructor
public class CopyThirdsPhaseController {

    private final IExecuteThirdsCopyPhasePort executePort;

    /**
     * Ejecuta una fase del proceso de copia de terceros.
     *
     * @param request datos del proceso de copia
     * @return resultado de la ejecución con HTTP status acorde al estado
     */
    @PostMapping("/phase")
    public ResponseEntity<CopyPhaseResponseDto> ejecutarFase(@Valid @RequestBody CopyPhaseRequestDto request) {
        CopyPhaseResponseDto response = executePort.ejecutar(request);
        HttpStatus status = determinarHttpStatus(response.getEstado());
        return ResponseEntity.status(status).body(response);
    }

    private HttpStatus determinarHttpStatus(String estado) {
        if (CopyEstado.ERROR_NO_REINTENTABLE.name().equals(estado)) {
            return HttpStatus.UNPROCESSABLE_ENTITY;
        } else if (CopyEstado.FALLIDO.name().equals(estado)) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.OK;
    }
}
