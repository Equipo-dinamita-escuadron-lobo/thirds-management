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
    @DisplayName("test_ValidatePersonTypeConsistency_PersonaNaturalCompleta_ValidaCorrectamente")
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
    @DisplayName("test_ValidatePersonTypeConsistency_PersonaNaturalSinGenero_ValidaCorrectamente")
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
    @DisplayName("test_ValidatePersonTypeConsistency_PersonaNaturalSinNombres_LanzaExcepcion")
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
    @DisplayName("test_ValidatePersonTypeConsistency_PersonaNaturalSinApellidos_LanzaExcepcion")
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
    @DisplayName("test_ValidatePersonTypeConsistency_PersonaNaturalConRazonSocial_LanzaExcepcion")
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
    @DisplayName("test_ValidatePersonTypeConsistency_PersonaNaturalNombresVacios_LanzaExcepcion")
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
    @DisplayName("test_ValidatePersonTypeConsistency_PersonaNaturalApellidosVacios_LanzaExcepcion")
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
    @DisplayName("test_ValidatePersonTypeConsistency_PersonaJuridicaCompleta_ValidaCorrectamente")
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
    @DisplayName("test_ValidatePersonTypeConsistency_PersonaJuridicaSinRazonSocial_LanzaExcepcion")
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
    @DisplayName("test_ValidatePersonTypeConsistency_PersonaJuridicaConNombres_LanzaExcepcion")
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
    @DisplayName("test_ValidatePersonTypeConsistency_PersonaJuridicaConApellidos_LanzaExcepcion")
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
    @DisplayName("test_ValidatePersonTypeConsistency_PersonaJuridicaConGenero_LanzaExcepcion")
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
    @DisplayName("test_ValidatePersonTypeConsistency_PersonaJuridicaRazonSocialVacia_LanzaExcepcion")
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
    @DisplayName("test_ValidatePersonTypeConsistency_TipoPersonaNull_LanzaExcepcion")
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
    @DisplayName("test_ValidateTypeIdPersonTypeCompatibility_PersonaNaturalConCedula_ValidaCorrectamente")
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
    @DisplayName("test_ValidateTypeIdPersonTypeCompatibility_PersonaNaturalConNIT_LanzaExcepcion")
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
    @DisplayName("test_ValidateTypeIdPersonTypeCompatibility_PersonaNaturalSinTypeId_NoValidaNiLanzaExcepcion")
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
    @DisplayName("test_ValidateTypeIdPersonTypeCompatibility_PersonaJuridicaConNIT_ValidaCorrectamente")
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
    @DisplayName("test_ValidateTypeIdPersonTypeCompatibility_PersonaJuridicaConCedula_LanzaExcepcion")
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
    @DisplayName("test_ValidateTypeIdPersonTypeCompatibility_PersonaJuridicaSinTypeId_NoValidaNiLanzaExcepcion")
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
    @DisplayName("test_ValidateTypeIdPersonTypeCompatibility_TypeIdCodigoNull_LanzaExcepcion")
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
    @DisplayName("test_ValidateTypeIdPersonTypeCompatibility_TypeIdCodigoVacio_LanzaExcepcion")
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
    @DisplayName("test_ValidateTypeIdPersonTypeCompatibility_TipoPersonaNull_NoValidaNiLanzaExcepcion")
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
    @DisplayName("test_ValidateNitFormat_NITValido9DigitosEmpiezaCon8_ValidaCorrectamente")
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
    @DisplayName("test_ValidateNitFormat_NITValido9DigitosEmpiezaCon9_ValidaCorrectamente")
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
    @DisplayName("test_ValidateNitFormat_PersonaNatural_NoValidaNIT")
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
    @DisplayName("test_ValidateNitFormat_PersonaJuridicaSinNIT_NoValida")
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
    @DisplayName("test_ValidateNitFormat_NITMenosDe9Digitos_LanzaExcepcion")
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
    @DisplayName("test_ValidateNitFormat_NITMasDe9Digitos_LanzaExcepcion")
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
    @DisplayName("test_ValidateNitFormat_NITNoEmpiezaCon8Ni9_LanzaExcepcion")
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
    @DisplayName("test_ValidateNitFormat_NITSinNumeroIdentificacion_LanzaExcepcion")
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
    @DisplayName("test_ValidateNitFormat_TipoPersonaNull_NoValida")
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
    @DisplayName("test_ValidateNitFormat_TypeIdNull_NoValida")
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
    @DisplayName("test_ValidateVerificationDigit_PersonaNaturalSinDigitoVerificacion_ValidaCorrectamente")
    void testValidateVerificationDigitPersonaNaturalSinDigitoVerificacionValidaCorrectamente() {
        // Arrange
        Third third = Third.builder()
                .personType(ePersonType.Natural)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateVerificationDigit(third));
    }

    @Test
    @DisplayName("test_ValidateVerificationDigit_PersonaNaturalConDigitoVerificacion_LanzaExcepcion")
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
    @DisplayName("test_ValidateVerificationDigit_PersonaJuridicaConNITYDigitoVerificacion_ValidaCorrectamente")
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
    @DisplayName("test_ValidateVerificationDigit_PersonaJuridicaConNITSinDigitoVerificacion_LanzaExcepcion")
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
    @DisplayName("test_ValidateVerificationDigit_PersonaJuridicaSinNITSinDigitoVerificacion_ValidaCorrectamente")
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
    @DisplayName("test_ValidateVerificationDigit_PersonaJuridicaSinNITConDigitoVerificacion_ValidaCorrectamente")
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
    @DisplayName("test_ValidateVerificationDigit_TipoPersonaNull_NoValida")
    void testValidateVerificationDigitTipoPersonaNullNoValida() {
        // Arrange
        Third third = Third.builder()
                .verificationNumber(5L)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> thirdValidationService.validateVerificationDigit(third));
    }

    @Test
    @DisplayName("test_ValidateVerificationDigit_PersonaJuridicaTypeIdNull_NoValida")
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
    @DisplayName("test_TodasLasValidaciones_PersonaNaturalCompleta_ValidanCorrectamente")
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
    @DisplayName("test_TodasLasValidaciones_PersonaJuridicaCompleta_ValidanCorrectamente")
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
    @DisplayName("test_TodasLasValidaciones_PersonaNaturalIncompleta_FallaEnPrimeraValidacion")
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
    @DisplayName("test_TodasLasValidaciones_PersonaJuridicaConNITInvalido_FallaEnValidacionNIT")
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
