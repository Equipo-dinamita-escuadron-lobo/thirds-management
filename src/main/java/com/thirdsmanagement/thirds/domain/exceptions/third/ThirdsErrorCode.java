package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.ErrorCodeDefinition;

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
    INVALID_THIRD_DATA("INVALID_THIRD_DATA", "Datos del tercero inválidos"),
    THIRD_FOREIGN_KEY_VIOLATION("THIRD_FOREIGN_KEY_VIOLATION", "Referencia a tipo de tercero o identificación inválida"),
    PDF_RUT_INVALID_FORMAT("PDF_RUT_INVALID_FORMAT", "El archivo PDF no tiene el formato válido de RUT de la DIAN");

    private final String code;
    private final String message;

    ThirdsErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
