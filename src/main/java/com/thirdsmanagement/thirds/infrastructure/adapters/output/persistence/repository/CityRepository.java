package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.CityEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.CityEntityId;

/**
 * Repositorio de ciudades.
 * Proporciona métodos para acceder a los datos de las ciudades.
 */
@Repository
public interface CityRepository extends JpaRepository<CityEntity, CityEntityId> {
    
    /**
     * Obtiene todas las ciudades de un estado específico ordenadas por nombre.
     */
    @Query("SELECT c FROM CityEntity c WHERE c.stateCode = :stateCode AND c.countryCode = :countryCode ORDER BY c.cityName")
    List<CityEntity> findByStateCodeAndCountryCodeOrderByCityName(@Param("stateCode") String stateCode, @Param("countryCode") String countryCode);
    
    /**
     * Verifica si existe una ciudad con el código especificado en un estado.
     */
    @Query("SELECT COUNT(c) > 0 FROM CityEntity c WHERE c.cityCode = :cityCode AND c.stateCode = :stateCode AND c.countryCode = :countryCode")
    boolean existsByCityCodeAndStateCodeAndCountryCode(@Param("cityCode") String cityCode, @Param("stateCode") String stateCode, @Param("countryCode") String countryCode);
}
