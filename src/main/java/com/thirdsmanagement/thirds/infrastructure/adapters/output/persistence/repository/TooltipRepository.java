package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import com.thirdsmanagement.thirds.domain.model.Tooltip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de tooltips.
 * Proporciona métodos para acceder a los datos de los tooltips.
 */
@Repository
public interface TooltipRepository extends JpaRepository<Tooltip, String> {
    
}