package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.StateEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.StateEntityId;

/**
 * Repositorio de estados/departamentos.
 * Proporciona métodos para acceder a los datos de los estados.
 */
@Repository
public interface StateRepository extends JpaRepository<StateEntity, StateEntityId> {
    
    /**
     * Obtiene todos los estados de un país específico ordenados por nombre.
     */
    @Query("SELECT s FROM StateEntity s WHERE s.countryCode = :countryCode ORDER BY s.stateName")
    List<StateEntity> findByCountryCodeOrderByStateName(@Param("countryCode") String countryCode);
    
    /**
     * Verifica si existe un estado con el código especificado en un país.
     */
    @Query("SELECT COUNT(s) > 0 FROM StateEntity s WHERE s.stateCode = :stateCode AND s.countryCode = :countryCode")
    boolean existsByStateCodeAndCountryCode(@Param("stateCode") String stateCode, @Param("countryCode") String countryCode);
}
