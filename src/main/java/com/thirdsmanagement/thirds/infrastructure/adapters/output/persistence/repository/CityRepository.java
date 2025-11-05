package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.CityEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.CityEntityId;

/**
 * @brief Repositorio JPA para entidades de ciudades con clave compuesta triple
 */
@Repository
public interface CityRepository extends JpaRepository<CityEntity, CityEntityId> {

    @Query("SELECT c FROM CityEntity c WHERE c.stateCode = :stateCode AND c.countryCode = :countryCode ORDER BY c.cityName")
    List<CityEntity> findByStateCodeAndCountryCodeOrderByCityName(@Param("stateCode") String stateCode, @Param("countryCode") String countryCode);

    @Query("SELECT COUNT(c) > 0 FROM CityEntity c WHERE c.cityCode = :cityCode AND c.stateCode = :stateCode AND c.countryCode = :countryCode")
    boolean existsByCityCodeAndStateCodeAndCountryCode(@Param("cityCode") String cityCode, @Param("stateCode") String stateCode, @Param("countryCode") String countryCode);
}
