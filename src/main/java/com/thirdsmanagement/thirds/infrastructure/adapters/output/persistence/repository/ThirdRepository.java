package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;

/**
 * Repositorio de terceros.
 * Proporciona métodos para acceder a los datos de los terceros.
 */
@Repository
public interface ThirdRepository extends JpaRepository<ThirdEntity,Long>{

    @Query("SELECT t FROM ThirdEntity t INNER JOIN t.thirdTypes tt WHERE t.entId like :entId")
    Page<ThirdEntity> getThirdsBy(String entId, Pageable page);

    @Query("SELECT COUNT(t) > 0 FROM ThirdEntity t WHERE t.idNumber = :idNumber AND t.entId = :entId")
    boolean existThirdBy(@Param("idNumber") Long idNumber, @Param("entId") String entId);

    @Query("SELECT t FROM ThirdEntity t INNER JOIN t.thirdTypes tt WHERE t.entId like :entId AND t.state = 'false'")
    Page<ThirdEntity> getInactiveThirdsBy(String entId, Pageable page);

    @Query("SELECT t FROM ThirdEntity t INNER JOIN t.thirdTypes tt WHERE t.entId like :entId AND t.state = 'true' AND tt.ttName = 'Proveedor'")
    Page<ThirdEntity> getProvidersBy(String entId, Pageable page);

    @Query("SELECT t FROM ThirdEntity t INNER JOIN t.thirdTypes tt WHERE t.entId like :entId AND t.state = 'true' AND tt.ttName = 'Cliente'")
    Page<ThirdEntity> getCustomersBy(String entId, Pageable page);
    Page<ThirdEntity> getThirdsByEntId(Long entId, Pageable page);

    @Query("SELECT t FROM ThirdEntity t INNER JOIN t.thirdTypes tt WHERE t.entId = :entId AND t.state = 'false'")
    Page<ThirdEntity> getInactiveThirdsBy(Long entId, Pageable page);

    Page<ThirdEntity> findByEntIdAndStateFalse(Long entId, Pageable page);

    @Query("SELECT t FROM ThirdEntity t INNER JOIN t.thirdTypes tt WHERE t.entId = :entId AND t.state = 'true' AND tt.ttName = 'Proveedor'")
    Page<ThirdEntity> getProvidersBy(Long entId, Pageable page);

    @Query("SELECT t FROM ThirdEntity t INNER JOIN t.thirdTypes tt WHERE t.entId = :entId AND t.state = 'true' AND tt.ttName = 'Cliente'")
    Page<ThirdEntity> getCustomersBy(Long entId, Pageable page);

    Page<ThirdEntity> findByEntIdAndStateTrueAndThirdTypes_TtName(Long entId, String ttName, Pageable page);

    @Query("SELECT t FROM ThirdEntity t INNER JOIN t.thirdTypes tt WHERE t.entId = :entId")
    List<ThirdEntity> getAllThirds(String entId);

}
