package com.thirdsmanagement.thirds.unit.domain.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;

/**
 * Tests unitarios para el enum ImportErrorType (tipo de error de importación)
 */
class ImportErrorTypeUnitTest {

    @Test
    @DisplayName("Debe retornar true para errores recuperables")
    void testIsRecoverable_WithRecoverableErrors() {
        // Act
        boolean validationRecoverable = ImportErrorType.VALIDATION_ERROR.isRecoverable();
        boolean formatRecoverable = ImportErrorType.FORMAT_ERROR.isRecoverable();
        boolean referenceRecoverable = ImportErrorType.REFERENCE_ERROR.isRecoverable();
        boolean businessRecoverable = ImportErrorType.BUSINESS_RULE_ERROR.isRecoverable();
        boolean duplicateRecoverable = ImportErrorType.DUPLICATE_ERROR.isRecoverable();

        // Assert
        assertTrue(validationRecoverable);
        assertTrue(formatRecoverable);
        assertTrue(referenceRecoverable);
        assertTrue(businessRecoverable);
        assertTrue(duplicateRecoverable);
    }

    @Test
    @DisplayName("Debe retornar false para errores no recuperables")
    void testIsRecoverable_WithNonRecoverableErrors() {
        // Act
        boolean systemRecoverable = ImportErrorType.SYSTEM_ERROR.isRecoverable();

        // Assert
        assertFalse(systemRecoverable);
    }

    @Test
    @DisplayName("Debe retornar true para errores que requieren acción del usuario")
    void testRequiresUserAction_WithUserActionErrors() {
        // Act
        boolean validationRequiresAction = ImportErrorType.VALIDATION_ERROR.requiresUserAction();
        boolean formatRequiresAction = ImportErrorType.FORMAT_ERROR.requiresUserAction();
        boolean referenceRequiresAction = ImportErrorType.REFERENCE_ERROR.requiresUserAction();
        boolean businessRequiresAction = ImportErrorType.BUSINESS_RULE_ERROR.requiresUserAction();
        boolean duplicateRequiresAction = ImportErrorType.DUPLICATE_ERROR.requiresUserAction();

        // Assert
        assertTrue(validationRequiresAction);
        assertTrue(formatRequiresAction);
        assertTrue(referenceRequiresAction);
        assertTrue(businessRequiresAction);
        assertTrue(duplicateRequiresAction);
    }

    @Test
    @DisplayName("Debe retornar false para errores que no requieren acción del usuario")
    void testRequiresUserAction_WithNonUserActionErrors() {
        // Act
        boolean systemRequiresAction = ImportErrorType.SYSTEM_ERROR.requiresUserAction();

        // Assert
        assertFalse(systemRequiresAction);
    }

    @Test
    @DisplayName("Debe retornar prioridades correctas")
    void testGetPriority() {
        // Act
        int systemPriority = ImportErrorType.SYSTEM_ERROR.getPriority();
        int businessPriority = ImportErrorType.BUSINESS_RULE_ERROR.getPriority();
        int referencePriority = ImportErrorType.REFERENCE_ERROR.getPriority();
        int validationPriority = ImportErrorType.VALIDATION_ERROR.getPriority();
        int formatPriority = ImportErrorType.FORMAT_ERROR.getPriority();
        int duplicatePriority = ImportErrorType.DUPLICATE_ERROR.getPriority();

        // Assert
        assertEquals(1, systemPriority);
        assertEquals(2, businessPriority);
        assertEquals(3, referencePriority);
        assertEquals(4, validationPriority);
        assertEquals(5, formatPriority);
        assertEquals(6, duplicatePriority);
    }

    @Test
    @DisplayName("Debe tener valores correctos de descripción")
    void testGetDescription() {
        // Act
        String validationDesc = ImportErrorType.VALIDATION_ERROR.getDescription();
        String formatDesc = ImportErrorType.FORMAT_ERROR.getDescription();
        String referenceDesc = ImportErrorType.REFERENCE_ERROR.getDescription();
        String businessDesc = ImportErrorType.BUSINESS_RULE_ERROR.getDescription();
        String duplicateDesc = ImportErrorType.DUPLICATE_ERROR.getDescription();
        String systemDesc = ImportErrorType.SYSTEM_ERROR.getDescription();

        // Assert
        assertEquals("Error de Validación", validationDesc);
        assertEquals("Error de Formato", formatDesc);
        assertEquals("Error de Referencia", referenceDesc);
        assertEquals("Error de Regla de Negocio", businessDesc);
        assertEquals("Error de Duplicado", duplicateDesc);
        assertEquals("Error del Sistema", systemDesc);
    }

    @Test
    @DisplayName("Debe tener exactamente seis valores de enum")
    void testEnumValues() {
        // Act
        ImportErrorType[] values = ImportErrorType.values();

        // Assert
        assertEquals(6, values.length);
        assertEquals(ImportErrorType.VALIDATION_ERROR, values[0]);
        assertEquals(ImportErrorType.FORMAT_ERROR, values[1]);
        assertEquals(ImportErrorType.REFERENCE_ERROR, values[2]);
        assertEquals(ImportErrorType.BUSINESS_RULE_ERROR, values[3]);
        assertEquals(ImportErrorType.DUPLICATE_ERROR, values[4]);
        assertEquals(ImportErrorType.SYSTEM_ERROR, values[5]);
    }

    @Test
    @DisplayName("Debe validar comportamiento correcto de VALIDATION_ERROR")
    void testValidationError_Behavior() {
        // Arrange
        ImportErrorType errorType = ImportErrorType.VALIDATION_ERROR;

        // Act
        boolean isRecoverable = errorType.isRecoverable();
        boolean requiresAction = errorType.requiresUserAction();
        int priority = errorType.getPriority();

        // Assert
        assertTrue(isRecoverable, "Debe ser recuperable");
        assertTrue(requiresAction, "Debe requerir acción del usuario");
        assertEquals(4, priority, "Debe tener prioridad 4");
    }

    @Test
    @DisplayName("Debe validar comportamiento correcto de BUSINESS_RULE_ERROR")
    void testBusinessRuleError_Behavior() {
        // Arrange
        ImportErrorType errorType = ImportErrorType.BUSINESS_RULE_ERROR;

        // Act
        boolean isRecoverable = errorType.isRecoverable();
        boolean requiresAction = errorType.requiresUserAction();
        int priority = errorType.getPriority();

        // Assert
        assertTrue(isRecoverable, "Debe ser recuperable");
        assertTrue(requiresAction, "Debe requerir acción del usuario");
        assertEquals(2, priority, "Debe tener prioridad 2");
    }

    @Test
    @DisplayName("Debe validar comportamiento correcto de SYSTEM_ERROR")
    void testSystemError_Behavior() {
        // Arrange
        ImportErrorType errorType = ImportErrorType.SYSTEM_ERROR;

        // Act
        boolean isRecoverable = errorType.isRecoverable();
        boolean requiresAction = errorType.requiresUserAction();
        int priority = errorType.getPriority();

        // Assert
        assertFalse(isRecoverable, "No debe ser recuperable");
        assertFalse(requiresAction, "No debe requerir acción del usuario");
        assertEquals(1, priority, "Debe tener prioridad 1");
    }

    @Test
    @DisplayName("Debe validar comportamiento correcto de DUPLICATE_ERROR")
    void testDuplicateError_Behavior() {
        // Arrange
        ImportErrorType errorType = ImportErrorType.DUPLICATE_ERROR;

        // Act
        boolean isRecoverable = errorType.isRecoverable();
        boolean requiresAction = errorType.requiresUserAction();
        int priority = errorType.getPriority();

        // Assert
        assertTrue(isRecoverable, "Debe ser recuperable");
        assertTrue(requiresAction, "Debe requerir acción del usuario");
        assertEquals(6, priority, "Debe tener prioridad 6");
    }

    @Test
    @DisplayName("Debe ordenar correctamente por prioridad")
    void testPriorityOrdering() {
        // Arrange
        ImportErrorType[] errorTypes = ImportErrorType.values();

        // Act
        int systemPriority = ImportErrorType.SYSTEM_ERROR.getPriority();
        int duplicatePriority = ImportErrorType.DUPLICATE_ERROR.getPriority();

        // Assert - Verificar que SYSTEM_ERROR tiene la prioridad más alta (1)
        assertEquals(1, systemPriority);

        // Verificar que DUPLICATE_ERROR tiene la prioridad más baja (6)
        assertEquals(6, duplicatePriority);

        // Verificar orden descendente de prioridad
        for (int i = 0; i < errorTypes.length - 1; i++) {
            assertTrue(errorTypes[i].getPriority() <= errorTypes[i + 1].getPriority(),
                "Prioridades deben estar en orden ascendente");
        }
    }
}
