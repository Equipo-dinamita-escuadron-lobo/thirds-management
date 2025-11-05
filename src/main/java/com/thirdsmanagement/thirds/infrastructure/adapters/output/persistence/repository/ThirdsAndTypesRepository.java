package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdsAndTypesEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdsAndTypesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @brief Repositorio JPA para relación muchos a muchos terceros-tipos con multi-tenancy
 */
@Repository
public interface ThirdsAndTypesRepository extends JpaRepository<ThirdsAndTypesEntity, ThirdsAndTypesId> {

    boolean existsByThIdAndTtId(Long thId, Long ttId);

    @Query("SELECT tat FROM ThirdsAndTypesEntity tat WHERE tat.thId = :thId")
    List<ThirdsAndTypesEntity> findByThId(@Param("thId") Long thId);

    @Query("SELECT tat FROM ThirdsAndTypesEntity tat WHERE tat.ttId = :ttId")
    List<ThirdsAndTypesEntity> findByTtId(@Param("ttId") Long ttId);

    void deleteByThId(Long thId);

    void deleteByTtId(Long ttId);

    boolean existsByTtId(Long ttId);
}