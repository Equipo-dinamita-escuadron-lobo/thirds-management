package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción lanzada cuando se intenta agregar un dígito de verificación
 * a una persona natural (solo se permite para personas jurídicas).
 */
public class VerificationDigitNotAllowedException extends BaseBusinessException {

    public VerificationDigitNotAllowedException() {
        super(ThirdsErrorCode.THIRD_VERIFICATION_DIGIT_NOT_ALLOWED);
    }

    public VerificationDigitNotAllowedException(String personType) {
        super(ThirdsErrorCode.THIRD_VERIFICATION_DIGIT_NOT_ALLOWED,
                String.format("El dígito de verificación no se permite para personas de tipo '%s'. Solo se permite para personas jurídicas", personType));
    }
}
