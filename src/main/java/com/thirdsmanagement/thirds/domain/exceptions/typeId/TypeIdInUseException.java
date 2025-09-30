package com.thirdsmanagement.thirds.domain.exceptions.typeId;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción lanzada cuando se intenta eliminar un tipo de identificación que está siendo utilizado por terceros existentes.
 */
public class TypeIdInUseException extends BaseBusinessException {

    /**
     * Constructor con ID específico del tipo de identificación.
     * @param typeIdId el ID del tipo de identificación que está en uso
     */
    public TypeIdInUseException(Long typeIdId) {
        super(TypeIdErrorCode.TYPE_ID_IN_USE, 
              "El tipo de identificación con ID '" + typeIdId + "' está siendo utilizado por terceros existentes y no puede ser eliminado");
    }

    /**
     * Constructor con nombre específico del tipo de identificación.
     * @param typeIdName el nombre del tipo de identificación que está en uso
     */
    public TypeIdInUseException(String typeIdName) {
        super(TypeIdErrorCode.TYPE_ID_IN_USE, 
              "El tipo de identificación '" + typeIdName + "' está siendo utilizado por terceros existentes y no puede ser eliminado");
    }

    /**
     * Constructor por defecto.
     */
    public TypeIdInUseException() {
        super(TypeIdErrorCode.TYPE_ID_IN_USE);
    }

    /**
     * Constructor con causa.
     * @param cause la causa de la excepción
     */
    public TypeIdInUseException(Throwable cause) {
        super(TypeIdErrorCode.TYPE_ID_IN_USE, TypeIdErrorCode.TYPE_ID_IN_USE.getMessage(), cause);
    }
}
