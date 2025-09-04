package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;

/**
 * Repositorio de tipos de identificación.
 * Proporciona métodos para acceder a los datos de los tipos de identificación.
 */
@Repository
public interface TypeIdRepository extends JpaRepository<TypeIdEntity,String>{
    @Query("SELECT t FROM TypeIdEntity t WHERE t.tientId = :tientId OR t.tientId = 'standart'")
    List<TypeIdEntity> findAllByTientId(@Param("tientId") String tientId);
}
