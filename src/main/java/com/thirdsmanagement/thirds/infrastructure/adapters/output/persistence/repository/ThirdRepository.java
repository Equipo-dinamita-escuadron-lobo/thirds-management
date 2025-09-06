package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT DISTINCT t FROM ThirdEntity t LEFT JOIN t.thirdTypes tt WHERE t.entId = :entId")
    Page<ThirdEntity> getThirdsBy(String entId, Pageable page);

    @Query("SELECT COUNT(t) > 0 FROM ThirdEntity t WHERE t.idNumber = :idNumber AND t.entId = :entId")
    boolean existThirdBy(@Param("idNumber") Long idNumber, @Param("entId") String entId);


    @Query("SELECT DISTINCT t FROM ThirdEntity t INNER JOIN t.thirdTypes tt WHERE t.entId = :entId AND t.state = 'true' AND tt.ttName = 'Proveedor'")
    Page<ThirdEntity> getProvidersBy(String entId, Pageable page);

    @Query("SELECT DISTINCT t FROM ThirdEntity t INNER JOIN t.thirdTypes tt WHERE t.entId = :entId AND t.state = 'true' AND tt.ttName = 'Cliente'")
    Page<ThirdEntity> getCustomersBy(String entId, Pageable page);

    @Query("SELECT DISTINCT t FROM ThirdEntity t LEFT JOIN t.thirdTypes tt WHERE t.entId = :entId")
    List<ThirdEntity> getAllThirds(String entId);

    @Query("SELECT t FROM ThirdEntity t WHERE t.thId = :thId AND t.entId = :entId")
    Optional<ThirdEntity> findByThIdAndEntId(@Param("thId") Long thId, @Param("entId") String entId);

    @Query("SELECT DISTINCT t FROM ThirdEntity t LEFT JOIN t.thirdTypes tt WHERE t.entId = :entId AND t.state = :state")
    Page<ThirdEntity> getThirdsByStatus(@Param("entId") String entId, @Param("state") String state, Pageable page);

}
