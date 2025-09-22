package com.thirdsmanagement.thirds.domain.exceptions.typeId;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción que se lanza cuando ya existe un tipo de identificación con el mismo nombre.
 */
public class TypeIdNameAlreadyExistsException extends BaseBusinessException {

    /**
     * Constructor con mensaje personalizado.
     * @param typeIdName nombre del tipo de identificación que ya existe
     */
    public TypeIdNameAlreadyExistsException(String typeIdName) {
        super(TypeIdErrorCode.TYPE_ID_NAME_ALREADY_EXISTS, 
              "Ya existe un tipo de identificación con el nombre '" + typeIdName + "'");
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param typeIdName nombre del tipo de identificación que ya existe
     * @param cause causa del error
     */
    public TypeIdNameAlreadyExistsException(String typeIdName, Throwable cause) {
        super(TypeIdErrorCode.TYPE_ID_NAME_ALREADY_EXISTS, 
              "Ya existe un tipo de identificación con el nombre '" + typeIdName + "'", cause);
    }
}
