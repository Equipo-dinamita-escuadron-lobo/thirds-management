package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;


import com.thirdsmanagement.thirds.application.service.TooltipService;
import com.thirdsmanagement.thirds.domain.model.Tooltip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tooltip")
public class TooltipController {

    @Autowired
    private TooltipService tooltipService;

    @GetMapping
    public List<Tooltip> getAllTooltips() {
        return tooltipService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tooltip> getTooltipById(@PathVariable String id) {
        Optional<Tooltip> tooltip = tooltipService.findById(id);
        return tooltip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Tooltip createTooltip(@RequestBody Tooltip tooltip) {
        return tooltipService.save(tooltip);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTooltip(@PathVariable String id) {
        tooltipService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}