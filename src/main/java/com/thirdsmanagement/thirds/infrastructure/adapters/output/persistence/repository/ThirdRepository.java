package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;

@Repository
public interface ThirdRepository extends JpaRepository<ThirdEntity,Long>{

    @Query("SELECT t FROM ThirdEntity t INNER JOIN t.thirdTypes tt WHERE t.entId = :entId")
    Page<ThirdEntity> getThirdsBy(Long entId, Pageable page);

    @Query("SELECT t FROM ThirdEntity t INNER JOIN t.thirdTypes tt WHERE t.entId = :entId AND t.state = 'false'")
    Page<ThirdEntity> getInactiveThirdsBy(Long entId, Pageable page);

    @Query("SELECT t FROM ThirdEntity t INNER JOIN t.thirdTypes tt WHERE t.entId = :entId AND t.state = 'true' AND tt.ttName = 'Proveedor'")
    Page<ThirdEntity> getProvidersBy(Long entId, Pageable page);

    @Query("SELECT t FROM ThirdEntity t INNER JOIN t.thirdTypes tt WHERE t.entId = :entId AND t.state = 'true' AND tt.ttName = 'Cliente'")
    Page<ThirdEntity> getCustomersBy(Long entId, Pageable page);
}
