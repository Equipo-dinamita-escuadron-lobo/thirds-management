package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;

/**
 * @brief Repositorio JPA principal para terceros con operaciones complejas de búsqueda y actualización masiva
 */
@Repository
public interface ThirdRepository extends JpaRepository<ThirdEntity, Long> {

    @Query("SELECT t FROM ThirdEntity t WHERE t.entId = :entId")
    Page<ThirdEntity> getThirdsBy(String entId, Pageable page);

    @Query("SELECT t FROM ThirdEntity t WHERE t.entId = :entId AND t.state = :state")
    Page<ThirdEntity> getThirdsByEntIdAndState(@Param("entId") String entId, @Param("state") Boolean state, Pageable page);

    /**
     * @brief Busca terceros por empresa y término de búsqueda
     * @details Busca en: nombres, apellidos, razón social, número de identificación con case insensitive
     * @param entId ID de la empresa
     * @param search Término de búsqueda
     * @param page Paginación con ordenamiento
     * @return Página de terceros que coinciden con la búsqueda
     */
    @Query("SELECT t FROM ThirdEntity t WHERE t.entId = :entId AND " +
           "(LOWER(t.names) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.lastNames) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.socialReason) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "CAST(t.idNumber AS string) LIKE CONCAT('%', :search, '%'))")
    Page<ThirdEntity> findByEntIdAndSearch(@Param("entId") String entId, @Param("search") String search, Pageable page);

    @Query("SELECT COUNT(t) FROM ThirdEntity t WHERE t.entId = :entId AND " +
           "(LOWER(t.names) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.lastNames) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.socialReason) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "CAST(t.idNumber AS string) LIKE CONCAT('%', :search, '%'))")
    long countByEntIdAndSearch(@Param("entId") String entId, @Param("search") String search);

    @Query("SELECT COUNT(t) > 0 FROM ThirdEntity t WHERE t.idNumber = :idNumber AND t.entId = :entId")
    boolean existThirdBy(@Param("idNumber") Long idNumber, @Param("entId") String entId);

    @Query("SELECT COUNT(t) > 0 FROM ThirdEntity t WHERE t.thId = :thId AND t.entId = :entId")
    boolean existThirdByThIdAndEntId(@Param("thId") Long thId, @Param("entId") String entId);

    @Query("SELECT t FROM ThirdEntity t WHERE t.thId = :thId AND t.entId = :entId")
    Optional<ThirdEntity> findByThIdAndEntId(@Param("thId") Long thId, @Param("entId") String entId);

    @Query("SELECT COUNT(t) > 0 FROM ThirdEntity t WHERE t.typeId.tiId = :typeIdCode AND t.entId = :entId")
    boolean existsByTypeIdTiIdAndEntId(@Param("typeIdCode") String typeIdCode, @Param("entId") String entId);

    @Query("SELECT COUNT(t) FROM ThirdEntity t WHERE t.entId = :entId")
    long countByEntId(@Param("entId") String entId);

    /**
     * @brief Actualiza el estado de todos los terceros de una empresa de forma masiva
     * @details Query optimizada para operaciones bulk con @Modifying
     * @param entId ID de la empresa
     * @param newState Nuevo estado (true para activo, false para inactivo)
     * @return Cantidad de registros actualizados
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ThirdEntity t SET t.state = :newState WHERE t.entId = :entId")
    int bulkUpdateStateByEntId(@Param("entId") String entId, @Param("newState") Boolean newState);

    /**
     * @brief Encuentra números de identificación que ya existen para importaciones masivas
     * @details Optimizado para validación de duplicados en procesos batch
     * @param idNumbers lista de números de identificación a verificar
     * @param entId ID de la empresa
     * @return lista de números de identificación que ya existen
     */
    @Query("SELECT t.idNumber FROM ThirdEntity t WHERE t.idNumber IN :idNumbers AND t.entId = :entId")
    List<Long> findExistingIdNumbers(@Param("idNumbers") Set<Long> idNumbers, @Param("entId") String entId);

    @Query("SELECT t FROM ThirdEntity t WHERE t.entId = :entId AND t.state = true")
    Page<ThirdEntity> getActiveThirdsBy(@Param("entId") String entId, Pageable page);

    @Query("SELECT COUNT(t) FROM ThirdEntity t WHERE t.entId = :entId AND t.state = true")
    long countActiveByEntId(@Param("entId") String entId);
}
