package com.thirdsmanagement.thirds.unit.domain.model;

import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.domain.model.ThirdExcelData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias para ThirdExcelData")
class ThirdExcelDataUnitTest {

    private ThirdExcelData thirdExcelData;

    @BeforeEach
    void setUp() {
        thirdExcelData = ThirdExcelData.builder()
                .rowNumber(1)
                .entId("TEST001")
                .typeIdName("CC")
                .idNumber(123456789L)
                .verificationNumber(1L)
                .personType(ePersonType.Natural)
                .names("Juan")
                .lastNames("Pérez")
                .socialReason(null)
                .gender(eThirdGender.Masculino)
                .state(true)
                .countryName("Colombia")
                .stateName("Cundinamarca")
                .cityName("Bogotá")
                .address("Calle 123 # 45-67")
                .phoneNumber("3001234567")
                .email("juan.perez@email.com")
                .thirdTypesNames(Set.of("Cliente", "Proveedor"))
                .build();
    }

    @Test
    @DisplayName("Debe retornar true cuando todos los campos requeridos están presentes")
    void testHasRequiredFields_WithAllRequiredFields() {
        // Act
        boolean hasRequiredFields = thirdExcelData.hasRequiredFields();

        // Assert
        assertTrue(hasRequiredFields);
    }

    @Test
    @DisplayName("Debe retornar false cuando falta typeIdName")
    void testHasRequiredFields_WithoutTypeIdName() {
        // Arrange
        thirdExcelData.setTypeIdName(null);

        // Act
        boolean hasRequiredFields = thirdExcelData.hasRequiredFields();

        // Assert
        assertFalse(hasRequiredFields);
    }

    @Test
    @DisplayName("Debe retornar false cuando typeIdName está vacío")
    void testHasRequiredFields_WithEmptyTypeIdName() {
        // Arrange
        thirdExcelData.setTypeIdName("");

        // Act
        boolean hasRequiredFields = thirdExcelData.hasRequiredFields();

        // Assert
        assertFalse(hasRequiredFields);
    }

    @Test
    @DisplayName("Debe retornar false cuando falta idNumber")
    void testHasRequiredFields_WithoutIdNumber() {
        // Arrange
        thirdExcelData.setIdNumber(null);

        // Act
        boolean hasRequiredFields = thirdExcelData.hasRequiredFields();

        // Assert
        assertFalse(hasRequiredFields);
    }

    @Test
    @DisplayName("Debe retornar false cuando falta personType")
    void testHasRequiredFields_WithoutPersonType() {
        // Arrange
        thirdExcelData.setPersonType(null);

        // Act
        boolean hasRequiredFields = thirdExcelData.hasRequiredFields();

        // Assert
        assertFalse(hasRequiredFields);
    }

    @Test
    @DisplayName("Debe retornar false cuando falta address")
    void testHasRequiredFields_WithoutAddress() {
        // Arrange
        thirdExcelData.setAddress(null);

        // Act
        boolean hasRequiredFields = thirdExcelData.hasRequiredFields();

        // Assert
        assertFalse(hasRequiredFields);
    }

    @Test
    @DisplayName("Debe retornar false cuando address está vacío")
    void testHasRequiredFields_WithEmptyAddress() {
        // Arrange
        thirdExcelData.setAddress("");

        // Act
        boolean hasRequiredFields = thirdExcelData.hasRequiredFields();

        // Assert
        assertFalse(hasRequiredFields);
    }

    @Test
    @DisplayName("Debe retornar false cuando falta phoneNumber")
    void testHasRequiredFields_WithoutPhoneNumber() {
        // Arrange
        thirdExcelData.setPhoneNumber(null);

        // Act
        boolean hasRequiredFields = thirdExcelData.hasRequiredFields();

        // Assert
        assertFalse(hasRequiredFields);
    }

    @Test
    @DisplayName("Debe retornar false cuando phoneNumber está vacío")
    void testHasRequiredFields_WithEmptyPhoneNumber() {
        // Arrange
        thirdExcelData.setPhoneNumber("");

        // Act
        boolean hasRequiredFields = thirdExcelData.hasRequiredFields();

        // Assert
        assertFalse(hasRequiredFields);
    }

    @Test
    @DisplayName("Debe retornar false cuando falta email")
    void testHasRequiredFields_WithoutEmail() {
        // Arrange
        thirdExcelData.setEmail(null);

        // Act
        boolean hasRequiredFields = thirdExcelData.hasRequiredFields();

        // Assert
        assertFalse(hasRequiredFields);
    }

    @Test
    @DisplayName("Debe retornar false cuando email está vacío")
    void testHasRequiredFields_WithEmptyEmail() {
        // Arrange
        thirdExcelData.setEmail("");

        // Act
        boolean hasRequiredFields = thirdExcelData.hasRequiredFields();

        // Assert
        assertFalse(hasRequiredFields);
    }

    @Test
    @DisplayName("Debe retornar true para persona natural con datos válidos")
    void testIsValidNaturalPerson_WithValidData() {
        // Act
        boolean isValid = thirdExcelData.isValidNaturalPerson();

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Debe retornar false cuando personType no es Natural")
    void testIsValidNaturalPerson_WithWrongPersonType() {
        // Arrange
        thirdExcelData.setPersonType(ePersonType.Juridica);

        // Act
        boolean isValid = thirdExcelData.isValidNaturalPerson();

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar false cuando falta names")
    void testIsValidNaturalPerson_WithoutNames() {
        // Arrange
        thirdExcelData.setNames(null);

        // Act
        boolean isValid = thirdExcelData.isValidNaturalPerson();

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar false cuando names está vacío")
    void testIsValidNaturalPerson_WithEmptyNames() {
        // Arrange
        thirdExcelData.setNames("");

        // Act
        boolean isValid = thirdExcelData.isValidNaturalPerson();

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar false cuando falta lastNames")
    void testIsValidNaturalPerson_WithoutLastNames() {
        // Arrange
        thirdExcelData.setLastNames(null);

        // Act
        boolean isValid = thirdExcelData.isValidNaturalPerson();

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar false cuando lastNames está vacío")
    void testIsValidNaturalPerson_WithEmptyLastNames() {
        // Arrange
        thirdExcelData.setLastNames("");

        // Act
        boolean isValid = thirdExcelData.isValidNaturalPerson();

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar true cuando falta gender (opcional)")
    void testIsValidNaturalPerson_WithoutGender() {
        // Arrange
        thirdExcelData.setGender(null);

        // Act
        boolean isValid = thirdExcelData.isValidNaturalPerson();

        // Assert
        assertTrue(isValid, "El género es opcional para personas naturales");
    }

    @Test
    @DisplayName("Debe retornar false cuando tiene socialReason (persona natural no debe tenerlo)")
    void testIsValidNaturalPerson_WithSocialReason() {
        // Arrange
        thirdExcelData.setSocialReason("Empresa S.A.");

        // Act
        boolean isValid = thirdExcelData.isValidNaturalPerson();

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar true para persona jurídica con datos válidos")
    void testIsValidLegalEntity_WithValidData() {
        // Arrange
        thirdExcelData.setPersonType(ePersonType.Juridica);
        thirdExcelData.setSocialReason("Empresa S.A.");
        thirdExcelData.setNames(null);
        thirdExcelData.setLastNames(null);
        thirdExcelData.setGender(null);

        // Act
        boolean isValid = thirdExcelData.isValidLegalEntity();

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Debe retornar false cuando personType no es Jurídica")
    void testIsValidLegalEntity_WithWrongPersonType() {
        // Arrange
        thirdExcelData.setPersonType(ePersonType.Natural);

        // Act
        boolean isValid = thirdExcelData.isValidLegalEntity();

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar false cuando falta socialReason")
    void testIsValidLegalEntity_WithoutSocialReason() {
        // Arrange
        thirdExcelData.setPersonType(ePersonType.Juridica);
        thirdExcelData.setSocialReason(null);
        thirdExcelData.setNames(null);
        thirdExcelData.setLastNames(null);
        thirdExcelData.setGender(null);

        // Act
        boolean isValid = thirdExcelData.isValidLegalEntity();

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar false cuando socialReason está vacío")
    void testIsValidLegalEntity_WithEmptySocialReason() {
        // Arrange
        thirdExcelData.setPersonType(ePersonType.Juridica);
        thirdExcelData.setSocialReason("");
        thirdExcelData.setNames(null);
        thirdExcelData.setLastNames(null);
        thirdExcelData.setGender(null);

        // Act
        boolean isValid = thirdExcelData.isValidLegalEntity();

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar false cuando tiene names (persona jurídica no debe tenerlo)")
    void testIsValidLegalEntity_WithNames() {
        // Arrange
        thirdExcelData.setPersonType(ePersonType.Juridica);
        thirdExcelData.setSocialReason("Empresa S.A.");
        thirdExcelData.setNames("Juan");
        thirdExcelData.setLastNames(null);
        thirdExcelData.setGender(null);

        // Act
        boolean isValid = thirdExcelData.isValidLegalEntity();

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar false cuando tiene lastNames (persona jurídica no debe tenerlo)")
    void testIsValidLegalEntity_WithLastNames() {
        // Arrange
        thirdExcelData.setPersonType(ePersonType.Juridica);
        thirdExcelData.setSocialReason("Empresa S.A.");
        thirdExcelData.setNames(null);
        thirdExcelData.setLastNames("Pérez");
        thirdExcelData.setGender(null);

        // Act
        boolean isValid = thirdExcelData.isValidLegalEntity();

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe retornar true cuando tiene gender (opcional para persona jurídica)")
    void testIsValidLegalEntity_WithGender() {
        // Arrange
        thirdExcelData.setPersonType(ePersonType.Juridica);
        thirdExcelData.setSocialReason("Empresa S.A.");
        thirdExcelData.setNames(null);
        thirdExcelData.setLastNames(null);
        thirdExcelData.setGender(eThirdGender.Masculino);

        // Act
        boolean isValid = thirdExcelData.isValidLegalEntity();

        // Assert
        assertTrue(isValid, "El género es opcional para personas jurídicas");
    }

    @Test
    @DisplayName("Debe validar que datos demográficos son opcionales")
    void testDemographicDataIsOptional() {
        // Arrange - Persona natural sin género, país, ciudad, departamento
        thirdExcelData.setGender(null);
        thirdExcelData.setCountryName(null);
        thirdExcelData.setStateName(null);
        thirdExcelData.setCityName(null);

        // Act
        boolean isValidNatural = thirdExcelData.isValidNaturalPerson();

        // Assert
        assertTrue(isValidNatural, "Los datos demográficos son opcionales para personas naturales");

        // Arrange - Persona jurídica con género (también opcional)
        thirdExcelData.setPersonType(ePersonType.Juridica);
        thirdExcelData.setSocialReason("Empresa S.A.");
        thirdExcelData.setNames(null);
        thirdExcelData.setLastNames(null);
        thirdExcelData.setGender(eThirdGender.Femenino);

        // Act
        boolean isValidLegal = thirdExcelData.isValidLegalEntity();

        // Assert
        assertTrue(isValidLegal, "Los datos demográficos son opcionales para personas jurídicas");
    }
}
