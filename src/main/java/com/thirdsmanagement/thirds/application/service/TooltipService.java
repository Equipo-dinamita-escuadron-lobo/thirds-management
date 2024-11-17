package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.domain.model.Tooltip;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TooltipRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TooltipService {

    @Autowired
    private TooltipRepository tooltipRepository;

    public List<Tooltip> findAll() {
        return tooltipRepository.findAll();
    }

    public Optional<Tooltip> findById(String id) {
        return tooltipRepository.findById(id);
    }

    public Tooltip save(Tooltip tooltip) {
        return tooltipRepository.save(tooltip);
    }

    public void deleteById(String id) {
        tooltipRepository.deleteById(id);
    }
}