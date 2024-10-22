package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;

public interface ThirdTypeRepository extends JpaRepository<ThirdTypeEntity,Long>{
    @Query("SELECT tt FROM ThirdTypeEntity tt WHERE tt.ttName LIKE %:name%")
    List<ThirdTypeEntity> findByName(String name);

    List<ThirdTypeEntity> findByTtNameContaining(String name);

    List<ThirdTypeEntity> findAllByTtentId(String ttentId);

    
}
