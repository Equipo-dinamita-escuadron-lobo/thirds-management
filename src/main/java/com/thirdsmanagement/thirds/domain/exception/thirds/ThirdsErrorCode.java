package com.thirdsmanagement.thirds.domain.exception.thirds;

import com.thirdsmanagement.thirds.domain.exception.ErrorCodeDefinition;
import lombok.Getter;

/**
 * Códigos de error específicos del dominio de Terceros.
 */
@Getter
public enum ThirdsErrorCode implements ErrorCodeDefinition {

    THIRD_NOT_FOUND("THIRD_NOT_FOUND", "Tercero no encontrado"),
    THIRDS_NOT_FOUND("THIRDS_NOT_FOUND", "No se encontraron terceros"),
    THIRD_STATE_NOT_CHANGED("THIRD_STATE_NOT_CHANGED", "No se pudo cambiar el estado del tercero"),
    THIRD_ALREADY_EXISTS("THIRD_ALREADY_EXISTS", "El tercero ya existe"),
    THIRD_TYPE_NOT_FOUND("THIRD_TYPE_NOT_FOUND", "Tipo de tercero no encontrado"),
    THIRD_TYPE_ALREADY_EXISTS("THIRD_TYPE_ALREADY_EXISTS", "El tipo de tercero ya existe"),
    TYPE_ID_NOT_FOUND("TYPE_ID_NOT_FOUND", "Tipo de identificación no encontrado"),
    TYPE_ID_ALREADY_EXISTS("TYPE_ID_ALREADY_EXISTS", "El tipo de identificación ya existe"),
    INVALID_THIRD_DATA("INVALID_THIRD_DATA", "Datos del tercero inválidos");

    private final String code;
    private final String message;

    ThirdsErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
