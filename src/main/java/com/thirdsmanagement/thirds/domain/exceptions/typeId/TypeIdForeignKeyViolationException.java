package com.thirdsmanagement.thirds.domain.exceptions.typeId;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción lanzada cuando se intenta crear un tercero con un tipo de identificación que no existe.
 */
public class TypeIdForeignKeyViolationException extends BaseBusinessException {

    public TypeIdForeignKeyViolationException(String typeId) {
        super(TypeIdErrorCode.TYPE_ID_FOREIGN_KEY_VIOLATION,
              "El tipo de identificación con ID '" + typeId + "' no existe");
    }

    public TypeIdForeignKeyViolationException(String typeId, Throwable cause) {
        super(TypeIdErrorCode.TYPE_ID_FOREIGN_KEY_VIOLATION,
              "El tipo de identificación con ID '" + typeId + "' no existe", cause);
    }
}
