package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;


import java.util.List;
import java.util.Optional;

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
public interface TypeIdRepository extends JpaRepository<TypeIdEntity,Long>{
    @Query("SELECT t FROM TypeIdEntity t WHERE t.tientId = :tientId OR t.tientId = 'standart'")
    List<TypeIdEntity> findAllByTientId(@Param("tientId") String tientId);
    
    /**
     * Verifica si existe un tipo de identificación con el mismo nombre (case-insensitive) para una entidad.
     * Utiliza LOWER() para comparación case-insensitive.
     * Busca en registros estándar Y en registros de la entidad específica para evitar duplicados.
     */
    @Query("SELECT COUNT(t) > 0 FROM TypeIdEntity t WHERE LOWER(t.tiName) = LOWER(:tiName) AND (t.tientId = :tientId OR t.tientId = 'standart')")
    boolean existsByTiNameIgnoreCaseAndTientId(@Param("tiName") String tiName, @Param("tientId") String tientId);
    
    /**
     * Verifica si existe un tipo de identificación con el mismo código para una entidad.
     */
    @Query("SELECT COUNT(t) > 0 FROM TypeIdEntity t WHERE t.tiId = :tiId AND (t.tientId = :tientId OR t.tientId = 'standart')")
    boolean existsByTiIdAndTientId(@Param("tiId") String tiId, @Param("tientId") String tientId);
    
    /**
     * Busca un tipo de identificación por su código.
     */
    @Query("SELECT t FROM TypeIdEntity t WHERE t.tiId = :tiId")
    Optional<TypeIdEntity> findByTiId(@Param("tiId") String tiId);
    
    /**
     * Busca un tipo de identificación por su código para una entidad específica.
     */
    @Query("SELECT t FROM TypeIdEntity t WHERE t.tiId = :tiId AND (t.tientId = :tientId OR t.tientId = 'standart')")
    Optional<TypeIdEntity> findByTiIdAndTientId(@Param("tiId") String tiId, @Param("tientId") String tientId);
}
