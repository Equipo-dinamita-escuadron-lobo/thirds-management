package com.thirdsmanagement.thirds.domain.exceptions.third;

import com.thirdsmanagement.thirds.domain.exceptions.BaseBusinessException;

/**
 * Excepción lanzada cuando una persona jurídica con NIT no tiene
 * el dígito de verificación obligatorio.
 */
public class VerificationDigitRequiredException extends BaseBusinessException {

    public VerificationDigitRequiredException() {
        super(ThirdsErrorCode.THIRD_VERIFICATION_DIGIT_REQUIRED);
    }

    public VerificationDigitRequiredException(String typeIdCode) {
        super(ThirdsErrorCode.THIRD_VERIFICATION_DIGIT_REQUIRED,
                String.format("El dígito de verificación es obligatorio para personas jurídicas con tipo de identificación '%s'", typeIdCode));
    }
}
