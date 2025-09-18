package com.thirdsmanagement.thirds.domain.exceptions.typeId;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción que se lanza cuando la clasificación de persona es inválida o no se encuentra.
 * Esta excepción se utiliza cuando se proporciona un valor de clasificación que no es válido
 * o cuando no se puede determinar la clasificación apropiada para un tipo de identificación.
 */
public class PersonClassificationInvalidException extends BaseBusinessException {

    /**
     * Constructor por defecto.
     */
    public PersonClassificationInvalidException() {
        super(TypeIdErrorCode.TYPE_ID_INVALID_CLASSIFICATION);
    }

    /**
     * Constructor con mensaje personalizado.
     * @param customMessage mensaje personalizado de error
     */
    public PersonClassificationInvalidException(String customMessage) {
        super(TypeIdErrorCode.TYPE_ID_INVALID_CLASSIFICATION, customMessage);
    }

    /**
     * Constructor con mensaje personalizado y causa.
     * @param customMessage mensaje personalizado de error
     * @param cause causa del error
     */
    public PersonClassificationInvalidException(String customMessage, Throwable cause) {
        super(TypeIdErrorCode.TYPE_ID_INVALID_CLASSIFICATION, customMessage, cause);
    }

    /**
     * Constructor con causa.
     * @param cause causa del error
     */
    public PersonClassificationInvalidException(Throwable cause) {
        super(TypeIdErrorCode.TYPE_ID_INVALID_CLASSIFICATION, 
              "Error en la clasificación de persona: " + cause.getMessage(), cause);
    }

    /**
     * Constructor específico para clasificación no encontrada por código.
     * @param invalidCode código de clasificación inválido
     * @return nueva instancia de la excepción
     */
    public static PersonClassificationInvalidException forInvalidCode(String invalidCode) {
        return new PersonClassificationInvalidException(
            "La clasificación de persona '" + invalidCode + "' no es válida. " +
            "Valores válidos: NATURAL_PERSON, LEGAL_ENTITY, BOTH"
        );
    }

    /**
     * Constructor específico para clasificación nula.
     * @return nueva instancia de la excepción
     */
    public static PersonClassificationInvalidException forNullClassification() {
        return new PersonClassificationInvalidException(
            "La clasificación de persona no puede ser nula. " +
            "Debe especificar: NATURAL_PERSON, LEGAL_ENTITY o BOTH"
        );
    }
}
