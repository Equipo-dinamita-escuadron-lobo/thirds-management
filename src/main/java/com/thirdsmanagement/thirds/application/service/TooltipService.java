package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.domain.model.Tooltip;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TooltipRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Clase de servicio para los tooltips.
 * Utiliza {@link TooltipRepository} para las operaciones de persistencia. 
 * Este servicio proporciona métodos para listar, buscar, guardar y eliminar tooltips. 
 */
@Service
public class TooltipService {
    /**
     * Repositorio de tooltips.
     */
    @Autowired
    private TooltipRepository tooltipRepository;

    /**
     * Método para listar todos los tooltips.
     * @return Lista de tooltips.
     */
    public List<Tooltip> findAll() {
        return tooltipRepository.findAll();
    }

    /**
     * Método para buscar un tooltip por su ID.
     * @param id ID del tooltip.
     * @return Tooltip.
     */
    public Optional<Tooltip> findById(String id) {
        return tooltipRepository.findById(id);
    }

    /**
     * Método para guardar un tooltip.
     * @param tooltip Tooltip a guardar.
     * @return Tooltip guardado.
     */
    public Tooltip save(Tooltip tooltip) {
        return tooltipRepository.save(tooltip);
    }

    /**
     * Método para eliminar un tooltip por su ID.
     * @param id ID del tooltip.
     */
    public void deleteById(String id) {
        tooltipRepository.deleteById(id);
    }
}