package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.CountryEntity;

/**
 * Repositorio de países.
 * Proporciona métodos para acceder a los datos de los países.
 */
@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, String> {
    
    /**
     * Obtiene todos los países ordenados por nombre.
     */
    @Query("SELECT c FROM CountryEntity c ORDER BY c.countryName")
    List<CountryEntity> findAllCountries();
    
    /**
     * Verifica si existe un país con el código especificado.
     */
    @Query("SELECT COUNT(c) > 0 FROM CountryEntity c WHERE c.countryCode = :countryCode")
    boolean existsByCountryCode(String countryCode);
}
