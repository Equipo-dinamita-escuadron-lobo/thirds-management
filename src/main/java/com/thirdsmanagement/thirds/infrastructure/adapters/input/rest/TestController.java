package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


/**
 * Controlador REST para verificar la disponibilidad del servicio.
 * Este controlador expone un endpoint para verificar si el servicio está disponible y en funcionamiento.
 * @return Una cadena de texto "pong" indicando que el servicio está disponible.
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api/thirds/test")
public class TestController {
    /**
     * Verificar disponibilidad del servicio.
     * @return Una cadena de texto "pong" indicando que el servicio está disponible.
     */
    @Operation(summary = "Verificar disponibilidad del servicio", description = "Este endpoint se utiliza para verificar si el servicio está disponible y en funcionamiento.", responses = {
            @ApiResponse(responseCode = "200", description = "El servicio está disponible", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
