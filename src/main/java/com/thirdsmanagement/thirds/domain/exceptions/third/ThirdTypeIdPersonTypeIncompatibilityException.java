package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;
import com.thirdsmanagement.thirds.domain.model.ePersonType;

/**
 * Excepción que se lanza cuando hay incompatibilidad entre el tipo de identificación y el tipo de persona.
 */
public class ThirdTypeIdPersonTypeIncompatibilityException extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public ThirdTypeIdPersonTypeIncompatibilityException() {
        super(ThirdsErrorCode.THIRD_TYPE_ID_PERSON_TYPE_INCOMPATIBILITY);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public ThirdTypeIdPersonTypeIncompatibilityException(String customMessage) {
        super(ThirdsErrorCode.THIRD_TYPE_ID_PERSON_TYPE_INCOMPATIBILITY, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public ThirdTypeIdPersonTypeIncompatibilityException(String customMessage, Throwable cause) {
        super(ThirdsErrorCode.THIRD_TYPE_ID_PERSON_TYPE_INCOMPATIBILITY, customMessage, cause);
    }

    /**
     * Crea una excepción para tipo de identificación no válido para persona natural.
     * @param typeIdCode código del tipo de identificación
     * @return la excepción configurada
     */
    public static ThirdTypeIdPersonTypeIncompatibilityException forNaturalPersonInvalidTypeId(String typeIdCode) {
        return new ThirdTypeIdPersonTypeIncompatibilityException(
            "El tipo de identificación '" + typeIdCode + "' no es válido para personas naturales"
        );
    }

    /**
     * Crea una excepción para tipo de identificación no válido para persona jurídica.
     * @param typeIdCode código del tipo de identificación
     * @return la excepción configurada
     */
    public static ThirdTypeIdPersonTypeIncompatibilityException forLegalEntityInvalidTypeId(String typeIdCode) {
        return new ThirdTypeIdPersonTypeIncompatibilityException(
            "El tipo de identificación '" + typeIdCode + "' no es válido para personas jurídicas"
        );
    }

    /**
     * Crea una excepción genérica para incompatibilidad.
     * @param typeIdCode código del tipo de identificación
     * @param personType tipo de persona
     * @return la excepción configurada
     */
    public static ThirdTypeIdPersonTypeIncompatibilityException forIncompatibility(String typeIdCode, ePersonType personType) {
        String personTypeDesc = personType.isNatural() ? "persona natural" : "persona jurídica";
        return new ThirdTypeIdPersonTypeIncompatibilityException(
            "El tipo de identificación '" + typeIdCode + "' no es compatible con " + personTypeDesc
        );
    }
}
