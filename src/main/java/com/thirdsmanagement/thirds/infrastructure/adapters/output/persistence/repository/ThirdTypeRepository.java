package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;

/**
 * Repositorio de tipos de terceros.
 * Proporciona métodos para acceder a los datos de los tipos de terceros.
 */
@Repository
public interface ThirdTypeRepository extends JpaRepository<ThirdTypeEntity,Long>{
    @Query("SELECT tt FROM ThirdTypeEntity tt WHERE tt.ttName LIKE %:name%")
    List<ThirdTypeEntity> findByName(String name);

    List<ThirdTypeEntity> findByTtNameContaining(String name);

    @Query("SELECT tt FROM ThirdTypeEntity tt WHERE tt.ttentId = :ttentId")
    List<ThirdTypeEntity> findAllByTtentId(@Param("ttentId") String ttentId);

    @Query("SELECT COUNT(tt) > 0 FROM ThirdTypeEntity tt WHERE LOWER(tt.ttName) = LOWER(:ttName) AND tt.ttentId = :ttentId")
    boolean existsByTtNameIgnoreCaseAndTtentId(@Param("ttName") String ttName, @Param("ttentId") String ttentId);

    @Query("SELECT COUNT(tt) > 0 FROM ThirdTypeEntity tt WHERE tt.ttName = :ttName AND tt.ttentId = :ttentId")
    boolean existsByTtNameAndTtentId(@Param("ttName") String ttName, @Param("ttentId") String ttentId);

    @Query("SELECT tt FROM ThirdTypeEntity tt WHERE tt.ttId = :ttId AND tt.ttentId = :ttentId")
    Optional<ThirdTypeEntity> findByTtIdAndTtentId(@Param("ttId") Long ttId, @Param("ttentId") String ttentId);

    void deleteByTtentId(String ttentId);
}
