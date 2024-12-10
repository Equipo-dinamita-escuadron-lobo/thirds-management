package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdAndTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.identifiers.ThirdsAndTypeId;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

/**
 * Repositorio de terceros y tipos.
 * Proporciona métodos para acceder a los datos de los terceros y tipos.
 */
@Repository
public interface ThirdsAndTypesRepository extends JpaRepository<ThirdAndTypeEntity, ThirdsAndTypeId> {

    void deleteById(ThirdsAndTypeId id);

    //boolean existsByTtId(Long ttId);
    boolean existsByThirdType_TtId(Long ttId);


}