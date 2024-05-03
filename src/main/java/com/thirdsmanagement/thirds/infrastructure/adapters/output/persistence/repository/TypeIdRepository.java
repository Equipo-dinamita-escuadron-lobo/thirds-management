package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;

public interface TypeIdRepository extends JpaRepository<TypeIdEntity,String>{
    List<TypeIdEntity> findAllByTientId(String tientId);
}
