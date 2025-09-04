package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdsAndTypesEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdsAndTypesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad ThirdsAndTypesEntity.
 * Maneja las operaciones de persistencia para la relación muchos a muchos
 * entre terceros y tipos de tercero.
 */
@Repository
public interface ThirdsAndTypesRepository extends JpaRepository<ThirdsAndTypesEntity, ThirdsAndTypesId> {

    /**
     * Verifica si existe una relación entre un tercero y un tipo de tercero específico.
     * @param thId ID del tercero
     * @param ttId ID del tipo de tercero
     * @return true si existe la relación, false en caso contrario
     */
    boolean existsByThIdAndTtId(Long thId, Long ttId);

    /**
     * Encuentra todas las relaciones para un tercero específico.
     * @param thId ID del tercero
     * @return Lista de entidades de relación
     */
    @Query("SELECT tat FROM ThirdsAndTypesEntity tat WHERE tat.thId = :thId")
    List<ThirdsAndTypesEntity> findByThId(@Param("thId") Long thId);

    /**
     * Encuentra todas las relaciones para un tipo de tercero específico.
     * @param ttId ID del tipo de tercero
     * @return Lista de entidades de relación
     */
    @Query("SELECT tat FROM ThirdsAndTypesEntity tat WHERE tat.ttId = :ttId")
    List<ThirdsAndTypesEntity> findByTtId(@Param("ttId") Long ttId);

    /**
     * Elimina todas las relaciones para un tercero específico.
     * @param thId ID del tercero
     */
    void deleteByThId(Long thId);

    /**
     * Elimina todas las relaciones para un tipo de tercero específico.
     * @param ttId ID del tipo de tercero
     */
    void deleteByTtId(Long ttId);

    /**
     * Verifica si existe alguna relación para un tipo de tercero específico.
     * @param ttId ID del tipo de tercero
     * @return true si existe al menos una relación, false en caso contrario
     */
    boolean existsByTtId(Long ttId);
}