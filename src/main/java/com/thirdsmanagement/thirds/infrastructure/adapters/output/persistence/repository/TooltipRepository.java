package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import com.thirdsmanagement.thirds.domain.model.Tooltip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TooltipRepository extends JpaRepository<Tooltip, String> {
    
}