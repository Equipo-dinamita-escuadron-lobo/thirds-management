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
}
