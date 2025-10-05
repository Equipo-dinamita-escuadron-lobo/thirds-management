package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción que se lanza cuando hay inconsistencias entre el tipo de persona
 * y los campos requeridos para crear un tercero.
 */
public class ThirdPersonTypeValidationException extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public ThirdPersonTypeValidationException() {
        super(ThirdsErrorCode.THIRD_PERSON_TYPE_VALIDATION_ERROR);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public ThirdPersonTypeValidationException(String customMessage) {
        super(ThirdsErrorCode.THIRD_PERSON_TYPE_VALIDATION_ERROR, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public ThirdPersonTypeValidationException(String customMessage, Throwable cause) {
        super(ThirdsErrorCode.THIRD_PERSON_TYPE_VALIDATION_ERROR, customMessage, cause);
    }

    /**
     * Constructor específico para persona natural sin campos requeridos.
     * @return nueva instancia de la excepción
     */
    public static ThirdPersonTypeValidationException forNaturalPersonMissingFields() {
        return new ThirdPersonTypeValidationException(
            "Para personas naturales son obligatorios: nombres y apellidos. " +
            "La razón social debe estar vacía."
        );
    }

    /**
     * Constructor específico para persona jurídica sin razón social.
     * @return nueva instancia de la excepción
     */
    public static ThirdPersonTypeValidationException forLegalEntityMissingFields() {
        return new ThirdPersonTypeValidationException(
            "Para personas jurídicas es obligatoria la razón social. " +
            "Los campos nombres, apellidos y género deben estar vacíos."
        );
    }

    /**
     * Constructor específico para persona natural con campos no permitidos.
     * @return nueva instancia de la excepción
     */
    public static ThirdPersonTypeValidationException forNaturalPersonWithForbiddenFields() {
        return new ThirdPersonTypeValidationException(
            "Para personas naturales no se permite el campo razón social. " +
            "Use nombres, apellidos y género."
        );
    }

    /**
     * Constructor específico para persona jurídica con campos no permitidos.
     * @return nueva instancia de la excepción
     */
    public static ThirdPersonTypeValidationException forLegalEntityWithForbiddenFields() {
        return new ThirdPersonTypeValidationException(
            "Para personas jurídicas no se permiten los campos nombres, apellidos o género. " +
            "Use únicamente razón social."
        );
    }

    /**
     * Constructor específico para persona jurídica con campo específico no permitido.
     * @param fieldName nombre del campo específico que no está permitido
     * @return nueva instancia de la excepción
     */
    public static ThirdPersonTypeValidationException forLegalEntityWithForbiddenField(String fieldName) {
        return new ThirdPersonTypeValidationException(
            String.format("Para personas jurídicas no se permite el campo %s. Use únicamente razón social.", fieldName)
        );
    }
}
