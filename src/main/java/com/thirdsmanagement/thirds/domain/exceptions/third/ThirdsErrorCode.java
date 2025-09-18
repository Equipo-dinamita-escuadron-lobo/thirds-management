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
    PDF_RUT_INVALID_FORMAT("PDF_RUT_INVALID_FORMAT", "El archivo PDF no tiene el formato válido de RUT de la DIAN"),
    PDF_RUT_INVALID_FILE_TYPE("PDF_RUT_INVALID_FILE_TYPE", "El archivo debe ser de tipo PDF"),
    THIRD_EXPORT_NO_DATA("THIRD_EXPORT_NO_DATA", "No hay terceros registrados para exportar en esta entidad"),
    THIRD_EXPORT_ERROR("THIRD_EXPORT_ERROR", "Error al generar archivo de exportación"),
    EXCEL_VALIDATION_ERROR("EXCEL_VALIDATION_ERROR", "Error al aplicar validaciones de datos en Excel"),
    EXCEL_REFERENCE_SHEET_ERROR("EXCEL_REFERENCE_SHEET_ERROR", "Error al crear hoja de referencia en Excel"),
    THIRD_PERSON_TYPE_VALIDATION_ERROR("THIRD_PERSON_TYPE_VALIDATION_ERROR", "Error de validación de tipo de persona");

    private final String code;
    private final String message;

    ThirdsErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
