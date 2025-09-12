package com.thirdsmanagement.thirds.domain.exceptions.geography;

import com.thirdsmanagement.thirds.domain.exceptions.ErrorCodeDefinition;
import lombok.Getter;

/**
 * Códigos de error específicos del dominio geográfico.
 */
@Getter
public enum GeographyErrorCode implements ErrorCodeDefinition {

    COUNTRY_NOT_FOUND("COUNTRY_NOT_FOUND", "País no encontrado"),
    STATE_NOT_FOUND("STATE_NOT_FOUND", "Estado/departamento no encontrado"),
    CITY_NOT_FOUND("CITY_NOT_FOUND", "Ciudad no encontrada o inactiva"),
    INVALID_COUNTRY_CODE("INVALID_COUNTRY_CODE", "Código de país inválido"),
    INVALID_STATE_CODE("INVALID_STATE_CODE", "Código de estado inválido"),
    INVALID_CITY_CODE("INVALID_CITY_CODE", "Código de ciudad inválido"),
    GEOGRAPHY_HIERARCHY_VIOLATION("GEOGRAPHY_HIERARCHY_VIOLATION", "Violación de jerarquía geográfica");

    private final String code;
    private final String message;

    GeographyErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
