package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    /**
     * Obtiene todos los tipos de tercero con paginación y ordenamiento.
     * 
     * @param ttentId ID de la empresa
     * @param pageable Paginación con ordenamiento
     * @return Página de tipos de tercero
     */
    @Query("SELECT tt FROM ThirdTypeEntity tt WHERE tt.ttentId = :ttentId")
    Page<ThirdTypeEntity> findAllByTtentIdPageable(@Param("ttentId") String ttentId, Pageable pageable);
    
    /**
     * Busca tipos de tercero por empresa y término de búsqueda.
     * Busca en: nombre (ttName).
     * 
     * @param ttentId ID de la empresa
     * @param search Término de búsqueda
     * @param pageable Paginación con ordenamiento
     * @return Página de tipos de tercero que coinciden con la búsqueda
     */
    @Query("SELECT tt FROM ThirdTypeEntity tt WHERE tt.ttentId = :ttentId AND " +
           "LOWER(tt.ttName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<ThirdTypeEntity> findByTtentIdAndSearch(@Param("ttentId") String ttentId, @Param("search") String search, Pageable pageable);
    
    /**
     * Cuenta tipos de tercero por empresa.
     * 
     * @param ttentId ID de la empresa
     * @return Cantidad de tipos de tercero
     */
    @Query("SELECT COUNT(tt) FROM ThirdTypeEntity tt WHERE tt.ttentId = :ttentId")
    long countByTtentId(@Param("ttentId") String ttentId);
    
    /**
     * Cuenta tipos de tercero por empresa y término de búsqueda.
     * 
     * @param ttentId ID de la empresa
     * @param search Término de búsqueda
     * @return Cantidad de tipos de tercero que coinciden
     */
    @Query("SELECT COUNT(tt) FROM ThirdTypeEntity tt WHERE tt.ttentId = :ttentId AND " +
           "LOWER(tt.ttName) LIKE LOWER(CONCAT('%', :search, '%'))")
    long countByTtentIdAndSearch(@Param("ttentId") String ttentId, @Param("search") String search);
}
