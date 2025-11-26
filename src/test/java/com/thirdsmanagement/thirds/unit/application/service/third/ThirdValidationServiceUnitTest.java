package com.thirdsmanagement.thirds.unit.application.service.third;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.thirdsmanagement.thirds.application.service.third.ThirdValidationService;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdNitInvalidFormatException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdPersonTypeValidationException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdTypeIdPersonTypeIncompatibilityException;
import com.thirdsmanagement.thirds.domain.exceptions.third.VerificationDigitNotAllowedException;
import com.thirdsmanagement.thirds.domain.exceptions.third.VerificationDigitRequiredException;
import com.thirdsmanagement.thirds.domain.model.PersonClassification;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.TypeId;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ThirdValidationServiceUnitTest {

    @InjectMocks
    private ThirdValidationService thirdValidationService;

    // ==================== validatePersonTypeConsistency - Persona Natural ====================

    @Test
    @DisplayName("Debe validar correctamente persona natural completa")
    void testValidatePersonTypeConsistencyPersonaNaturalCompletaValidaCorrectamente() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .names("Juan")
                .lastNames("Pérez")
                .gender(eThirdGender.Masculino)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validatePersonTypeConsistency(third));
    }

    @Test
    @DisplayName("Debe validar correctamente persona natural sin genero")
    void testValidatePersonTypeConsistencyPersonaNaturalSinGeneroValidaCorrectamente() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .names("María")
                .lastNames("González")
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validatePersonTypeConsistency(third));
    }

    @Test
    @DisplayName("Debe lanzar excepci�n cuando persona natural no tiene nombres")
    void testValidatePersonTypeConsistencyPersonaNaturalSinNombresLanzaExcepcion() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .lastNames("López")
                .build();

        // Act & Assert
        assertThrows(
                ThirdPersonTypeValidationException.class,
                () -> thirdValidationService.validatePersonTypeConsistency(third)
        );
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando persona natural no tiene apellidos")
    void testValidatePersonTypeConsistencyPersonaNaturalSinApellidosLanzaExcepcion() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .names("Carlos")
                .build();

        // Act & Assert
        assertThrows(
                ThirdPersonTypeValidationException.class,
                () -> thirdValidationService.validatePersonTypeConsistency(third)
        );
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando persona natural tiene raz�n social")
    void testValidatePersonTypeConsistencyPersonaNaturalConRazonSocialLanzaExcepcion() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .names("Ana")
                .lastNames("Rodríguez")
                .socialReason("Empresa XYZ S.A.S.")
                .build();

        // Act & Assert
        assertThrows(
                ThirdPersonTypeValidationException.class,
                () -> thirdValidationService.validatePersonTypeConsistency(third)
        );
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando persona natural tiene nombres vacios")
    void testValidatePersonTypeConsistencyPersonaNaturalNombresVaciosLanzaExcepcion() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .names("   ")
                .lastNames("Martínez")
                .build();

        // Act & Assert
        assertThrows(
                ThirdPersonTypeValidationException.class,
                () -> thirdValidationService.validatePersonTypeConsistency(third)
        );
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando persona natural tiene apellidos vacios")
    void testValidatePersonTypeConsistencyPersonaNaturalApellidosVaciosLanzaExcepcion() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .names("Luis")
                .lastNames("   ")
                .build();

        // Act & Assert
        assertThrows(
                ThirdPersonTypeValidationException.class,
                () -> thirdValidationService.validatePersonTypeConsistency(third)
        );
    }

    // ==================== validatePersonTypeConsistency - Persona Jurídica ====================

    @Test
    @DisplayName("Debe validar correctamente persona jur�dica completa")
    void testValidatePersonTypeConsistencyPersonaJuridicaCompletaValidaCorrectamente() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .socialReason("Empresa ABC S.A.S.")
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validatePersonTypeConsistency(third));
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando persona juridica no tiene razon social")
    void testValidatePersonTypeConsistencyPersonaJuridicaSinRazonSocialLanzaExcepcion() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .build();

        // Act & Assert
        assertThrows(
                ThirdPersonTypeValidationException.class,
                () -> thirdValidationService.validatePersonTypeConsistency(third)
        );
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando persona juridica tiene nombres")
    void testValidatePersonTypeConsistencyPersonaJuridicaConNombresLanzaExcepcion() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .socialReason("Empresa DEF S.A.S.")
                .names("Juan")
                .build();

        // Act & Assert
        ThirdPersonTypeValidationException exception = assertThrows(
                ThirdPersonTypeValidationException.class,
                () -> thirdValidationService.validatePersonTypeConsistency(third)
        );
        assertTrue(exception.getMessage().contains("nombres"));
    }

    @Test
    @DisplayName("Debe lanzar excepci�n cuando persona jur�dica tiene apellidos")
    void testValidatePersonTypeConsistencyPersonaJuridicaConApellidosLanzaExcepcion() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .socialReason("Empresa GHI S.A.S.")
                .lastNames("Pérez")
                .build();

        // Act & Assert
        ThirdPersonTypeValidationException exception = assertThrows(
                ThirdPersonTypeValidationException.class,
                () -> thirdValidationService.validatePersonTypeConsistency(third)
        );
        assertTrue(exception.getMessage().contains("apellidos"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando persona jurídica tiene género")
    void testValidatePersonTypeConsistencyPersonaJuridicaConGeneroLanzaExcepcion() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .socialReason("Empresa JKL S.A.S.")
                .gender(eThirdGender.Femenino)
                .build();

        // Act & Assert
        ThirdPersonTypeValidationException exception = assertThrows(
                ThirdPersonTypeValidationException.class,
                () -> thirdValidationService.validatePersonTypeConsistency(third)
        );
        assertTrue(exception.getMessage().contains("género"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando persona jurídica tiene razón social vacía")
    void testValidatePersonTypeConsistencyPersonaJuridicaRazonSocialVaciaLanzaExcepcion() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .socialReason("   ")
                .build();

        // Act & Assert
        assertThrows(
                ThirdPersonTypeValidationException.class,
                () -> thirdValidationService.validatePersonTypeConsistency(third)
        );
    }

    // ==================== validatePersonTypeConsistency - Casos especiales ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando tipo de persona es null")
    void testValidatePersonTypeConsistencyTipoPersonaNullLanzaExcepcion() {
        // Arrange
        Third third = Third.builder()
                .names("Pedro")
                .lastNames("Sánchez")
                .build();

        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(
                ThirdInvalidDataException.class,
                () -> thirdValidationService.validatePersonTypeConsistency(third)
        );
        assertTrue(exception.getMessage().contains("tipo de persona"));
    }

    // ==================== validateTypeIdPersonTypeCompatibility - Persona Natural ====================

    @Test
    @DisplayName("Debe validar correctamente persona natural con cédula")
    void testValidateTypeIdPersonTypeCompatibilityPersonaNaturalConCedulaValidaCorrectamente() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("CC")
                .classification(PersonClassification.NATURAL_PERSON)
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateTypeIdPersonTypeCompatibility(third));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando persona natural tiene NIT")
    void testValidateTypeIdPersonTypeCompatibilityPersonaNaturalConNITLanzaExcepcion() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("NIT")
                .classification(PersonClassification.LEGAL_ENTITY)
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .build();

        // Act & Assert
        ThirdTypeIdPersonTypeIncompatibilityException exception = assertThrows(
                ThirdTypeIdPersonTypeIncompatibilityException.class,
                () -> thirdValidationService.validateTypeIdPersonTypeCompatibility(third)
        );
        assertTrue(exception.getMessage().contains("NIT"));
    }

    @Test
    @DisplayName("Debe validar correctamente persona natural sin tipo de identificación")
    void testValidateTypeIdPersonTypeCompatibilityPersonaNaturalSinTypeIdNoValidaNiLanzaExcepcion() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateTypeIdPersonTypeCompatibility(third));
    }

    // ==================== validateTypeIdPersonTypeCompatibility - Persona Jurídica ====================

    @Test
    @DisplayName("Debe validar correctamente persona jurídica con NIT")
    void testValidateTypeIdPersonTypeCompatibilityPersonaJuridicaConNITValidaCorrectamente() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("NIT")
                .classification(PersonClassification.LEGAL_ENTITY)
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateTypeIdPersonTypeCompatibility(third));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando persona jurídica tiene cédula")
    void testValidateTypeIdPersonTypeCompatibilityPersonaJuridicaConCedulaLanzaExcepcion() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("CC")
                .classification(PersonClassification.NATURAL_PERSON)
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .build();

        // Act & Assert
        ThirdTypeIdPersonTypeIncompatibilityException exception = assertThrows(
                ThirdTypeIdPersonTypeIncompatibilityException.class,
                () -> thirdValidationService.validateTypeIdPersonTypeCompatibility(third)
        );
        assertTrue(exception.getMessage().contains("CC"));
    }

    @Test
    @DisplayName("Debe validar correctamente persona jurídica sin tipo de identificación")
    void testValidateTypeIdPersonTypeCompatibilityPersonaJuridicaSinTypeIdNoValidaNiLanzaExcepcion() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateTypeIdPersonTypeCompatibility(third));
    }

    // ==================== validateTypeIdPersonTypeCompatibility - Casos especiales ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando código de tipo de identificación es nulo")
    void testValidateTypeIdPersonTypeCompatibilityTypeIdCodigoNullLanzaExcepcion() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId(null)
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .build();

        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(
                ThirdInvalidDataException.class,
                () -> thirdValidationService.validateTypeIdPersonTypeCompatibility(third)
        );
        assertTrue(exception.getMessage().contains("código del tipo de identificación"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando código de tipo de identificación está vacío")
    void testValidateTypeIdPersonTypeCompatibilityTypeIdCodigoVacioLanzaExcepcion() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("   ")
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .build();

        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(
                ThirdInvalidDataException.class,
                () -> thirdValidationService.validateTypeIdPersonTypeCompatibility(third)
        );
        assertTrue(exception.getMessage().contains("código del tipo de identificación"));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando tipo de persona es nulo")
    void testValidateTypeIdPersonTypeCompatibilityTipoPersonaNullNoValidaNiLanzaExcepcion() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("CC")
                .build();

        Third third = Third.builder()
                .typeId(typeId)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateTypeIdPersonTypeCompatibility(third));
    }

    // ==================== validateNitFormat - Validaciones exitosas ====================

    @Test
    @DisplayName("Debe validar correctamente NIT válido de 9 dígitos que empieza con 8")
    void testValidateNitFormatNITValido9DigitosEmpiezaCon8ValidaCorrectamente() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("NIT")
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .idNumber(800123456L)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateNitFormat(third));
    }

    @Test
    @DisplayName("Debe validar correctamente NIT válido de 9 dígitos que empieza con 9")
    void testValidateNitFormatNITValido9DigitosEmpiezaCon9ValidaCorrectamente() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("NIT")
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .idNumber(900123456L)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateNitFormat(third));
    }

    @Test
    @DisplayName("Debe validar correctamente sin validar NIT para persona natural")
    void testValidateNitFormatPersonaNaturalNoValidaNIT() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("CC")
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .idNumber(1234567890L)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateNitFormat(third));
    }

    @Test
    @DisplayName("Debe validar correctamente persona jurídica sin NIT")
    void testValidateNitFormatPersonaJuridicaSinNITNoValida() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("CE")
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .idNumber(123456789L)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateNitFormat(third));
    }

    // ==================== validateNitFormat - Validaciones con errores ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando NIT tiene menos de 9 dígitos")
    void testValidateNitFormatNITMenosDe9DigitosLanzaExcepcion() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("NIT")
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .idNumber(80012345L)
                .build();

        // Act & Assert
        ThirdNitInvalidFormatException exception = assertThrows(
                ThirdNitInvalidFormatException.class,
                () -> thirdValidationService.validateNitFormat(third)
        );
        assertTrue(exception.getMessage().contains("9 dígitos"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando NIT tiene más de 9 dígitos")
    void testValidateNitFormatNITMasDe9DigitosLanzaExcepcion() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("NIT")
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .idNumber(8001234567L)
                .build();

        // Act & Assert
        ThirdNitInvalidFormatException exception = assertThrows(
                ThirdNitInvalidFormatException.class,
                () -> thirdValidationService.validateNitFormat(third)
        );
        assertTrue(exception.getMessage().contains("9 dígitos"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando NIT no empieza con 8 ni 9")
    void testValidateNitFormatNITNoEmpiezaCon8Ni9LanzaExcepcion() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("NIT")
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .idNumber(700123456L)
                .build();

        // Act & Assert
        ThirdNitInvalidFormatException exception = assertThrows(
                ThirdNitInvalidFormatException.class,
                () -> thirdValidationService.validateNitFormat(third)
        );
        assertTrue(exception.getMessage().contains("empezar por 8 o 9"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando NIT no tiene número de identificación")
    void testValidateNitFormatNITSinNumeroIdentificacionLanzaExcepcion() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("NIT")
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .build();

        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(
                ThirdInvalidDataException.class,
                () -> thirdValidationService.validateNitFormat(third)
        );
        assertTrue(exception.getMessage().contains("número de identificación es obligatorio"));
    }

    // ==================== validateNitFormat - Casos especiales ====================

    @Test
    @DisplayName("Debe validar correctamente cuando tipo de persona es nulo")
    void testValidateNitFormatTipoPersonaNullNoValida() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("NIT")
                .build();

        Third third = Third.builder()
                .typeId(typeId)
                .idNumber(800123456L)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateNitFormat(third));
    }

    @Test
    @DisplayName("Debe validar correctamente cuando tipo de identificación es nulo")
    void testValidateNitFormatTypeIdNullNoValida() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .idNumber(800123456L)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateNitFormat(third));
    }

    // ==================== validateVerificationDigit - Persona Natural ====================

    @Test
    @DisplayName("Debe validar correctamente persona natural sin dígito de verificación")
    void testValidateVerificationDigitPersonaNaturalSinDigitoVerificacionValidaCorrectamente() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateVerificationDigit(third));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando persona natural tiene dígito de verificación")
    void testValidateVerificationDigitPersonaNaturalConDigitoVerificacionLanzaExcepcion() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .verificationNumber(5L)
                .build();

        // Act & Assert
        VerificationDigitNotAllowedException exception = assertThrows(
                VerificationDigitNotAllowedException.class,
                () -> thirdValidationService.validateVerificationDigit(third)
        );
        assertTrue(exception.getMessage().contains("Natural"));
    }

    // ==================== validateVerificationDigit - Persona Jurídica con NIT ====================

    @Test
    @DisplayName("Debe validar correctamente persona jurídica con NIT y dígito de verificación")
    void testValidateVerificationDigitPersonaJuridicaConNITYDigitoVerificacionValidaCorrectamente() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("NIT")
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .verificationNumber(5L)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateVerificationDigit(third));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando persona jurídica con NIT no tiene dígito de verificación")
    void testValidateVerificationDigitPersonaJuridicaConNITSinDigitoVerificacionLanzaExcepcion() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("NIT")
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .build();

        // Act & Assert
        VerificationDigitRequiredException exception = assertThrows(
                VerificationDigitRequiredException.class,
                () -> thirdValidationService.validateVerificationDigit(third)
        );
        assertTrue(exception.getMessage().contains("NIT"));
    }

    @Test
    @DisplayName("Debe validar correctamente persona jurídica sin NIT y sin dígito de verificación")
    void testValidateVerificationDigitPersonaJuridicaSinNITSinDigitoVerificacionValidaCorrectamente() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("CE")
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateVerificationDigit(third));
    }

    @Test
    @DisplayName("Debe validar correctamente persona jurídica sin NIT con dígito de verificación")
    void testValidateVerificationDigitPersonaJuridicaSinNITConDigitoVerificacionValidaCorrectamente() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("CE")
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .verificationNumber(3L)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateVerificationDigit(third));
    }

    // ==================== validateVerificationDigit - Casos especiales ====================

    @Test
    @DisplayName("Debe validar correctamente cuando tipo de persona es nulo")
    void testValidateVerificationDigitTipoPersonaNullNoValida() {
        // Arrange
        Third third = Third.builder()
                .verificationNumber(5L)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateVerificationDigit(third));
    }

    @Test
    @DisplayName("Debe validar correctamente persona jurídica cuando tipo de identificación es nulo")
    void testValidateVerificationDigitPersonaJuridicaTypeIdNullNoValida() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateVerificationDigit(third));
    }

    // ==================== Integración - Múltiples validaciones ====================

    @Test
    @DisplayName("Debe validar correctamente todas las validaciones de persona natural completa")
    void testTodasLasValidacionesPersonaNaturalCompletaValidanCorrectamente() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("CC")
                .classification(PersonClassification.NATURAL_PERSON)
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .typeId(typeId)
                .names("Pedro")
                .lastNames("Ramírez")
                .idNumber(1234567890L)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> {
            thirdValidationService.validatePersonTypeConsistency(third);
            thirdValidationService.validateTypeIdPersonTypeCompatibility(third);
            thirdValidationService.validateNitFormat(third);
            thirdValidationService.validateVerificationDigit(third);
        });
    }

    @Test
    @DisplayName("Debe validar correctamente todas las validaciones de persona jurídica completa")
    void testTodasLasValidacionesPersonaJuridicaCompletaValidanCorrectamente() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("NIT")
                .classification(PersonClassification.LEGAL_ENTITY)
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .socialReason("Empresa XYZ S.A.S.")
                .idNumber(800123456L)
                .verificationNumber(5L)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> {
            thirdValidationService.validatePersonTypeConsistency(third);
            thirdValidationService.validateTypeIdPersonTypeCompatibility(third);
            thirdValidationService.validateNitFormat(third);
            thirdValidationService.validateVerificationDigit(third);
        });
    }

    @Test
    @DisplayName("Debe fallar en primera validación cuando persona natural está incompleta")
    void testTodasLasValidacionesPersonaNaturalIncompletaFallaEnPrimeraValidacion() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .names("Carlos")
                .build();

        // Act & Assert
        assertThrows(
                ThirdPersonTypeValidationException.class,
                () -> thirdValidationService.validatePersonTypeConsistency(third)
        );
    }

    @Test
    @DisplayName("Debe fallar en validación de NIT cuando persona jurídica tiene NIT inválido")
    void testTodasLasValidacionesPersonaJuridicaConNITInvalidoFallaEnValidacionNIT() {
        // Arrange
        TypeId typeId = TypeId.builder()
                .typeId("NIT")
                .classification(PersonClassification.LEGAL_ENTITY)
                .build();

        Third third = Third.builder()
                .personType(ePersonType.Juridica)
                .typeId(typeId)
                .socialReason("Empresa ABC S.A.S.")
                .idNumber(123456L)
                .verificationNumber(5L)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validatePersonTypeConsistency(third));
        assertDoesNotThrow(() -> thirdValidationService.validateTypeIdPersonTypeCompatibility(third));
        assertThrows(
                ThirdNitInvalidFormatException.class,
                () -> thirdValidationService.validateNitFormat(third)
        );
    }
}
