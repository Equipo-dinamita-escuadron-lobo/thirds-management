package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




/**
 * Controlador REST para verificar la disponibilidad del servicio.
 * Este controlador expone un endpoint para verificar si el servicio está disponible y en funcionamiento.
 * @return Una cadena de texto "pong" indicando que el servicio está disponible.
 */
@RestController
@RequestMapping("/api/thirds/test")
public class TestRestAdapter {
    /**
     * Verificar disponibilidad del servicio.
     * @return Una cadena de texto "pong" indicando que el servicio está disponible.
     */
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
