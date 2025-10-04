package com.thirdsmanagement.thirds.application.ports.output;

import java.util.List;

import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;

/**
 * Interfaz que define los métodos de salida para la gestión de tipos de tercero y tipos de identificación.
 */
public interface IdOutputPort {
    /**
     * Guarda un tipo de tercero.
     * @param thirdType El tipo de tercero a guardar
     * @return El tipo de tercero guardado
     */
    ThirdType saveThirdType(ThirdType thirdType);
    
    /**
     * Obtiene todos los tipos de tercero.
     * @param entId El id de la empresa
     * @return La lista de tipos de tercero
     */
    List<ThirdType> getALLThirdTypes(String entId);
    
    /**
     * Guarda un tipo de identificacion.
     * @param typeId El tipo de identificacion a guardar
     * @return El tipo de identificacion guardado
     */
    TypeId saveTypeId(TypeId typeId);
    
    /**
     * Obtiene todos los tipos de identificacion.
     * @param entId El id de la empresa
     * @return La lista de tipos de identificacion
     */
    List<TypeId> getAllTypeIds(String entId);
    
    /**
     * Actualiza un tipo de identificacion.
     * @param typeId El tipo de identificacion a actualizar
     * @return El tipo de identificacion actualizado
     */
    TypeId updateTypeId(TypeId typeId);
    
    /**
     * Actualiza un tipo de tercero.
     * @param thirdType El tipo de tercero a actualizar
     * @return El tipo de tercero actualizado
     */
    ThirdType updateThirdType(ThirdType thirdType);
    
    /**
     * Verifica si existe un tipo de identificación por su ID.
     * @param typeIdId El ID del tipo de identificación
     * @return true si existe, false en caso contrario
     */
    boolean existsTypeIdById(Long typeIdId);
    
    /**
     * Verifica si existe un tipo de tercero por su ID.
     * @param thirdTypeId El ID del tipo de tercero
     * @return true si existe, false en caso contrario
     */
    boolean existsThirdTypeById(Long thirdTypeId);
    
    /**
     * Obtiene un tipo de identificación completo por su ID.
     * @param typeIdId El ID del tipo de identificación
     * @return El tipo de identificación completo o null si no existe
     */
    TypeId getTypeIdById(Long typeIdId);
    
    /**
     * Obtiene un tipo de tercero completo por su ID.
     * @param thirdTypeId El ID del tipo de tercero
     * @return El tipo de tercero completo o null si no existe
     */
    ThirdType getThirdTypeById(Long thirdTypeId);
    
    /**
     * Elimina un tipo de tercero del sistema.
     * @param thirdTypeId El ID del tipo de tercero a eliminar
     * @param entId El ID de la empresa
     * @return true si se eliminó correctamente, false en caso contrario
     */
    boolean deleteThirdType(Long thirdTypeId, String entId);
    
    /**
     * Verifica si un tipo de tercero está siendo utilizado por terceros existentes.
     * @param thirdTypeId El ID del tipo de tercero
     * @param entId El ID de la empresa
     * @return true si está en uso, false en caso contrario
     */
    boolean isThirdTypeInUse(Long thirdTypeId, String entId);
    
    /**
     * Elimina un tipo de identificación del sistema.
     * @param typeIdId El ID del tipo de identificación a eliminar
     * @param entId El ID de la empresa
     * @return true si se eliminó correctamente, false en caso contrario
     */
    boolean deleteTypeId(Long typeIdId, String entId);
    
    /**
     * Verifica si un tipo de identificación está siendo utilizado por terceros existentes.
     * @param typeIdId El ID del tipo de identificación
     * @param entId El ID de la empresa
     * @return true si está en uso, false en caso contrario
     */
    boolean isTypeIdInUse(Long typeIdId, String entId);
}
