package com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.controller;

import com.thirdsmanagement.thirds.copy.application.input.ICancelThirdsCopyPort;
import com.thirdsmanagement.thirds.copy.application.input.ICleanupThirdsCopyPort;
import com.thirdsmanagement.thirds.copy.application.input.IGetThirdsCopyStatusPort;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyCancelResponseDto;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyStatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expone los endpoints del contrato uniforme del orquestador de copia.
 * Patrón estándar: /{idProceso}/{operacion}
 *
 * Los controladores originales usan /{operacion}/{idProceso} — este controlador
 * agrega los paths correctos sin modificar el código existente.
 */
@RestController
@RequestMapping("/api/thirds/copy")
@RequiredArgsConstructor
public class CopyThirdsContractController {

    private final IGetThirdsCopyStatusPort statusPort;
    private final ICancelThirdsCopyPort cancelPort;
    private final ICleanupThirdsCopyPort cleanupPort;

    @GetMapping("/{idProceso}/status")
    public ResponseEntity<CopyStatusResponseDto> obtenerEstado(@PathVariable String idProceso) {
        return ResponseEntity.ok(statusPort.obtenerEstado(idProceso));
    }

    @PostMapping("/{idProceso}/cancel")
    public ResponseEntity<CopyCancelResponseDto> cancelar(@PathVariable String idProceso) {
        return ResponseEntity.ok(cancelPort.cancelar(idProceso));
    }

    @DeleteMapping("/{idProceso}/cleanup")
    public ResponseEntity<Void> limpiar(@PathVariable String idProceso) {
        cleanupPort.limpiar(idProceso);
        return ResponseEntity.noContent().build();
    }
}
