package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Query("SELECT t FROM TypeIdEntity t WHERE t.tientId = :tientId")
    List<TypeIdEntity> findAllByTientId(@Param("tientId") String tientId);
    
    /**
     * Verifica si existe un tipo de identificación con el mismo nombre (case-insensitive) para una entidad.
     * Utiliza LOWER() para comparación case-insensitive.
     */
    @Query("SELECT COUNT(t) > 0 FROM TypeIdEntity t WHERE LOWER(t.tiName) = LOWER(:tiName) AND t.tientId = :tientId")
    boolean existsByTiNameIgnoreCaseAndTientId(@Param("tiName") String tiName, @Param("tientId") String tientId);
    
    /**
     * Verifica si existe un tipo de identificación con el mismo código para una entidad.
     */
    @Query("SELECT COUNT(t) > 0 FROM TypeIdEntity t WHERE t.tiId = :tiId AND t.tientId = :tientId")
    boolean existsByTiIdAndTientId(@Param("tiId") String tiId, @Param("tientId") String tientId);
    
    /**
     * Busca un tipo de identificación por su código.
     */
    @Query("SELECT t FROM TypeIdEntity t WHERE t.tiId = :tiId")
    Optional<TypeIdEntity> findByTiId(@Param("tiId") String tiId);
    
    /**
     * Busca un tipo de identificación por su código para una entidad específica.
     */
    @Query("SELECT t FROM TypeIdEntity t WHERE t.tiId = :tiId AND t.tientId = :tientId")
    Optional<TypeIdEntity> findByTiIdAndTientId(@Param("tiId") String tiId, @Param("tientId") String tientId);
    
    /**
     * Obtiene todos los tipos de identificación con paginación y ordenamiento.
     * 
     * @param tientId ID de la empresa
     * @param pageable Paginación con ordenamiento
     * @return Página de tipos de identificación
     */
    @Query("SELECT t FROM TypeIdEntity t WHERE t.tientId = :tientId")
    Page<TypeIdEntity> findAllByTientIdPageable(@Param("tientId") String tientId, Pageable pageable);
    
    /**
     * Busca tipos de identificación por empresa y término de búsqueda.
     * Busca en: código (tiId) y nombre (tiName).
     * 
     * @param tientId ID de la empresa
     * @param search Término de búsqueda
     * @param pageable Paginación con ordenamiento
     * @return Página de tipos de identificación que coinciden con la búsqueda
     */
    @Query("SELECT t FROM TypeIdEntity t WHERE t.tientId = :tientId AND " +
           "(LOWER(t.tiId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.tiName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<TypeIdEntity> findByTientIdAndSearch(@Param("tientId") String tientId, @Param("search") String search, Pageable pageable);
    
    /**
     * Cuenta tipos de identificación por empresa.
     * 
     * @param tientId ID de la empresa
     * @return Cantidad de tipos de identificación
     */
    @Query("SELECT COUNT(t) FROM TypeIdEntity t WHERE t.tientId = :tientId")
    long countByTientId(@Param("tientId") String tientId);
    
    /**
     * Cuenta tipos de identificación por empresa y término de búsqueda.
     * 
     * @param tientId ID de la empresa
     * @param search Término de búsqueda
     * @return Cantidad de tipos de identificación que coinciden
     */
    @Query("SELECT COUNT(t) FROM TypeIdEntity t WHERE t.tientId = :tientId AND " +
           "(LOWER(t.tiId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.tiName) LIKE LOWER(CONCAT('%', :search, '%')))")
    long countByTientIdAndSearch(@Param("tientId") String tientId, @Param("search") String search);
}
