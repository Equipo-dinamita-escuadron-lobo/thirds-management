package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;

/**
 * Repositorio de tipos de terceros.
 * Proporciona métodos para acceder a los datos de los tipos de terceros.
 */
public interface ThirdTypeRepository extends JpaRepository<ThirdTypeEntity,Long>{
    @Query("SELECT tt FROM ThirdTypeEntity tt WHERE tt.ttName LIKE %:name%")
    List<ThirdTypeEntity> findByName(String name);

    List<ThirdTypeEntity> findByTtNameContaining(String name);

    @Query("SELECT tt FROM ThirdTypeEntity tt WHERE tt.ttentId = :ttentId OR tt.ttentId = 'standart'")
    List<ThirdTypeEntity> findAllByTtentId(@Param("ttentId") String ttentId);

    void deleteByTtentId(String ttentId);
}
