package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;


import com.thirdsmanagement.thirds.application.service.TooltipService;
import com.thirdsmanagement.thirds.domain.model.Tooltip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador de tooltips.
 * Proporciona puntos de acceso para obtener, crear, actualizar y eliminar tooltips.
 */
@RestController
@RequestMapping("/api/thirds/tooltip")
public class TooltipController {
    /**
     * Servicio de tooltips.
     */
    @Autowired
    private TooltipService tooltipService;

    /**
     * Método para obtener todos los tooltips.
     * @return Lista de tooltips.
     */
    @GetMapping
    public List<Tooltip> getAllTooltips() {
        return tooltipService.findAll();
    }

    /**
     * Método para obtener un tooltip por su identificador.
     * @param id Identificador del tooltip.
     * @return Tooltip si existe, si no, un error 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tooltip> getTooltipById(@PathVariable String id) {
        Optional<Tooltip> tooltip = tooltipService.findById(id);
        return tooltip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Método para crear un tooltip.
     * @param tooltip Tooltip a crear.
     * @return Tooltip creado.
     */
    @PostMapping
    public Tooltip createTooltip(@RequestBody Tooltip tooltip) {
        return tooltipService.save(tooltip);
    }

    /**
     * Método para actualizar un tooltip.
     * @param id Identificador del tooltip.
     * @param tooltipDetails Detalles del tooltip a actualizar.
     * @return Tooltip actualizado si existe, si no, un error 404.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Tooltip> updateTooltip(@PathVariable String id, @RequestBody Tooltip tooltipDetails) {
        Optional<Tooltip> tooltip = tooltipService.findById(id);
        if (tooltip.isPresent()) {
            Tooltip updatedTooltip = tooltip.get();
            updatedTooltip.setTip(tooltipDetails.getTip());
            return ResponseEntity.ok(tooltipService.save(updatedTooltip));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Método para eliminar un tooltip.
     * @param id Identificador del tooltip.
     * @return Respuesta vacía.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTooltip(@PathVariable String id) {
        tooltipService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}