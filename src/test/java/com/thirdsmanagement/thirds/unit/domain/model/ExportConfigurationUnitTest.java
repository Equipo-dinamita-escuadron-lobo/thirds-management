package com.thirdsmanagement.thirds.unit.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.domain.enums.ExportableField;
import com.thirdsmanagement.thirds.domain.model.ExportConfiguration;

/**
 * Tests unitarios para ExportConfiguration (configuración de exportación)
 */
class ExportConfigurationUnitTest {

    private ExportConfiguration configWithFields;
    private ExportConfiguration emptyConfig;

    @BeforeEach
    void setUp() {
        // Arrange - Configuración con algunos campos
        configWithFields = ExportConfiguration.builder()
            .includeField(ExportableField.GENDER)
            .includeField(ExportableField.COUNTRY)
            .build();

        // Configuración vacía
        emptyConfig = ExportConfiguration.builder().build();
    }

    @Test
    @DisplayName("Debe retornar true cuando campo está incluido")
    void testIncludes_WithIncludedField() {
        // Act
        boolean genderIncluded = configWithFields.includes(ExportableField.GENDER);
        boolean countryIncluded = configWithFields.includes(ExportableField.COUNTRY);

        // Assert
        assertTrue(genderIncluded);
        assertTrue(countryIncluded);
    }

    @Test
    @DisplayName("Debe retornar false cuando campo no está incluido")
    void testIncludes_WithNotIncludedField() {
        // Act
        boolean stateIncluded = configWithFields.includes(ExportableField.STATE);
        boolean cityIncluded = configWithFields.includes(ExportableField.CITY);

        // Assert
        assertFalse(stateIncluded);
        assertFalse(cityIncluded);
    }

    @Test
    @DisplayName("Debe retornar false para todos los campos en configuración vacía")
    void testIncludes_WithEmptyConfiguration() {
        // Act
        boolean anyIncluded = false;
        for (ExportableField field : ExportableField.values()) {
            if (emptyConfig.includes(field)) {
                anyIncluded = true;
                break;
            }
        }

        // Assert
        assertFalse(anyIncluded);
    }

    @Test
    @DisplayName("Debe incluir campo individual con includeField")
    void testBuilder_IncludeField() {
        // Act
        ExportConfiguration config = ExportConfiguration.builder()
            .includeField(ExportableField.GENDER)
            .build();

        // Assert
        assertTrue(config.includes(ExportableField.GENDER));
        assertFalse(config.includes(ExportableField.COUNTRY));
    }

    @Test
    @DisplayName("Debe incluir múltiples campos con includeFields")
    void testBuilder_IncludeFields() {
        // Arrange
        Set<ExportableField> fieldsToInclude = EnumSet.of(
            ExportableField.GENDER,
            ExportableField.STATE
        );

        // Act
        ExportConfiguration config = ExportConfiguration.builder()
            .includeFields(fieldsToInclude)
            .build();

        // Assert
        assertTrue(config.includes(ExportableField.GENDER));
        assertTrue(config.includes(ExportableField.STATE));
        assertFalse(config.includes(ExportableField.COUNTRY));
    }

    @Test
    @DisplayName("Debe incluir todos los campos con includeAllFields")
    void testBuilder_IncludeAllFields() {
        // Act
        ExportConfiguration config = ExportConfiguration.builder()
            .includeAllFields()
            .build();

        // Assert
        for (ExportableField field : ExportableField.values()) {
            assertTrue(config.includes(field), "Debe incluir " + field);
        }
    }

    @Test
    @DisplayName("Debe excluir campo específico con excludeField")
    void testBuilder_ExcludeField() {
        // Act
        ExportConfiguration config = ExportConfiguration.builder()
            .includeAllFields()
            .excludeField(ExportableField.GENDER)
            .build();

        // Assert
        assertFalse(config.includes(ExportableField.GENDER));
        assertTrue(config.includes(ExportableField.COUNTRY));
        assertTrue(config.includes(ExportableField.STATE));
        assertTrue(config.includes(ExportableField.CITY));
    }

    @Test
    @DisplayName("Debe limpiar todos los campos con clear")
    void testBuilder_Clear() {
        // Act
        ExportConfiguration config = ExportConfiguration.builder()
            .includeAllFields()
            .clear()
            .build();

        // Assert
        for (ExportableField field : ExportableField.values()) {
            assertFalse(config.includes(field), "No debe incluir " + field + " después de clear");
        }
    }

    @Test
    @DisplayName("Debe manejar null en includeField sin errores")
    void testBuilder_IncludeField_WithNull() {
        // Act
        ExportConfiguration config = ExportConfiguration.builder()
            .includeField(null)
            .includeField(ExportableField.GENDER)
            .build();

        // Assert
        assertTrue(config.includes(ExportableField.GENDER));
        assertEquals(1, config.getIncludedFields().size());
    }

    @Test
    @DisplayName("Debe manejar null en includeFields sin errores")
    void testBuilder_IncludeFields_WithNull() {
        // Act
        ExportConfiguration config = ExportConfiguration.builder()
            .includeFields(null)
            .includeField(ExportableField.GENDER)
            .build();

        // Assert
        assertTrue(config.includes(ExportableField.GENDER));
        assertEquals(1, config.getIncludedFields().size());
    }

    @Test
    @DisplayName("Debe manejar null en excludeField sin errores")
    void testBuilder_ExcludeField_WithNull() {
        // Act
        ExportConfiguration config = ExportConfiguration.builder()
            .includeAllFields()
            .excludeField(null)
            .build();

        // Assert - Todos los campos deben seguir incluidos
        for (ExportableField field : ExportableField.values()) {
            assertTrue(config.includes(field));
        }
    }

    @Test
    @DisplayName("Debe retornar set inmutable desde getIncludedFields")
    void testGetIncludedFields_ReturnsImmutableSet() {
        // Arrange
        Set<ExportableField> includedFields = configWithFields.getIncludedFields();

        // Act & Assert - No debe permitir modificaciones
        assertThrows(UnsupportedOperationException.class, () -> {
            includedFields.add(ExportableField.STATE);
        });
    }

    @Test
    @DisplayName("Debe crear configuraciones independientes con builder")
    void testBuilder_CreatesIndependentConfigurations() {
        // Act
        ExportConfiguration config1 = ExportConfiguration.builder()
            .includeField(ExportableField.GENDER)
            .build();

        ExportConfiguration config2 = ExportConfiguration.builder()
            .includeField(ExportableField.COUNTRY)
            .build();

        // Assert
        assertTrue(config1.includes(ExportableField.GENDER));
        assertFalse(config1.includes(ExportableField.COUNTRY));

        assertFalse(config2.includes(ExportableField.GENDER));
        assertTrue(config2.includes(ExportableField.COUNTRY));
    }

    @Test
    @DisplayName("Debe permitir encadenamiento fluido en builder")
    void testBuilder_FluentInterface() {
        // Act
        ExportConfiguration config = ExportConfiguration.builder()
            .includeField(ExportableField.GENDER)
            .includeField(ExportableField.COUNTRY)
            .excludeField(ExportableField.GENDER)
            .includeAllFields()
            .excludeField(ExportableField.STATE)
            .build();

        // Assert
        assertTrue(config.includes(ExportableField.GENDER)); // Re-incluido por includeAllFields
        assertTrue(config.includes(ExportableField.COUNTRY));
        assertTrue(config.includes(ExportableField.CITY));
        assertFalse(config.includes(ExportableField.STATE)); // Excluido específicamente
    }

    @Test
    @DisplayName("Debe crear configuración vacía por defecto")
    void testBuilder_DefaultEmptyConfiguration() {
        // Act
        ExportConfiguration config = ExportConfiguration.builder().build();

        // Assert
        assertNotNull(config.getIncludedFields());
        assertTrue(config.getIncludedFields().isEmpty());

        for (ExportableField field : ExportableField.values()) {
            assertFalse(config.includes(field));
        }
    }

    @Test
    @DisplayName("Debe mantener orden de operaciones en builder")
    void testBuilder_OperationOrder() {
        // Act
        ExportConfiguration config = ExportConfiguration.builder()
            .includeAllFields()      // Incluye todos
            .excludeField(ExportableField.GENDER)  // Excluye GENDER
            .includeField(ExportableField.GENDER)  // Re-incluye GENDER
            .clear()                 // Limpia todo
            .includeField(ExportableField.STATE)   // Incluye solo STATE
            .build();

        // Assert
        assertFalse(config.includes(ExportableField.GENDER));
        assertFalse(config.includes(ExportableField.COUNTRY));
        assertFalse(config.includes(ExportableField.CITY));
        assertTrue(config.includes(ExportableField.STATE));
        assertEquals(1, config.getIncludedFields().size());
    }
}
