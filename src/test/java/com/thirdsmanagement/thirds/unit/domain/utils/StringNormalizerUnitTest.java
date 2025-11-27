package com.thirdsmanagement.thirds.unit.domain.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.thirdsmanagement.thirds.domain.utils.StringNormalizer;

/**
 * Tests unitarios para StringNormalizer
 */
class StringNormalizerUnitTest {

    // ========== Constructor Tests ==========

    @Test
    @DisplayName("Debe lanzar excepción al intentar instanciar StringNormalizer")
    void testConstructorThrowsException() throws Exception {
        java.lang.reflect.Constructor<StringNormalizer> constructor = 
            StringNormalizer.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        
        java.lang.reflect.InvocationTargetException exception = 
            assertThrows(java.lang.reflect.InvocationTargetException.class, 
                () -> constructor.newInstance());
        
        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
        assertEquals("StringNormalizer es una clase de utilidad y no debe ser instanciada", 
            exception.getCause().getMessage());
    }

    // ========== normalize(String) Tests ==========

    @Test
    @DisplayName("Debe normalizar texto eliminando acentos y capitalizando primera letra")
    void testNormalize_WithAccents() {
        String result = StringNormalizer.normalize("josé maría");

        assertEquals("Jose maria", result);
    }

    @Test
    @DisplayName("Debe devolver null cuando normalize recibe null")
    void testNormalize_WithNull() {
        String result = StringNormalizer.normalize(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Debe devolver cadena vacía cuando normalize recibe cadena vacía")
    void testNormalize_WithEmptyString() {
        String result = StringNormalizer.normalize("   ");

        assertEquals("   ", result);
    }

    @Test
    @DisplayName("Debe normalizar texto con ñ preservándola")
    void testNormalize_WithEnye() {
        String result = StringNormalizer.normalize("niño español");

        assertEquals("Niño español", result);
    }

    // ========== normalizePreservingCase(String) Tests ==========

    @Test
    @DisplayName("Debe normalizar preservando mayúsculas y minúsculas")
    void testNormalizePreservingCase_WithMixedCase() {
        String result = StringNormalizer.normalizePreservingCase("José María");

        assertEquals("Jose Maria", result);
    }

    @Test
    @DisplayName("Debe devolver null cuando normalizePreservingCase recibe null")
    void testNormalizePreservingCase_WithNull() {
        String result = StringNormalizer.normalizePreservingCase(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Debe devolver cadena vacía cuando normalizePreservingCase recibe cadena vacía")
    void testNormalizePreservingCase_WithEmptyString() {
        String result = StringNormalizer.normalizePreservingCase("  ");

        assertEquals("  ", result);
    }

    @Test
    @DisplayName("Debe preservar ñ al normalizar preservando case")
    void testNormalizePreservingCase_WithEnye() {
        String result = StringNormalizer.normalizePreservingCase("NIÑO español");

        assertEquals("NIÑO español", result);
    }

    // ========== normalizeForComparison(String) Tests ==========

    @Test
    @DisplayName("Debe normalizar para comparación eliminando acentos y convirtiendo a minúsculas")
    void testNormalizeForComparison_WithAccentsAndUppercase() {
        String result = StringNormalizer.normalizeForComparison("JOSÉ María");

        assertEquals("jose maria", result);
    }

    @Test
    @DisplayName("Debe devolver null cuando normalizeForComparison recibe null")
    void testNormalizeForComparison_WithNull() {
        String result = StringNormalizer.normalizeForComparison(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Debe devolver cadena vacía cuando normalizeForComparison recibe cadena vacía")
    void testNormalizeForComparison_WithEmptyString() {
        String result = StringNormalizer.normalizeForComparison("   ");

        assertEquals("   ", result);
    }

    @Test
    @DisplayName("Debe normalizar ñ para comparación")
    void testNormalizeForComparison_WithEnye() {
        String result = StringNormalizer.normalizeForComparison("NIÑO");

        assertEquals("niño", result);
    }

    // ========== normalizeCode(String) Tests ==========

    @Test
    @DisplayName("Debe normalizar código eliminando caracteres especiales")
    void testNormalizeCode_WithSpecialCharacters() {
        String result = StringNormalizer.normalizeCode("abc-123.xyz");

        assertEquals("ABC123XYZ", result);
    }

    @Test
    @DisplayName("Debe devolver null cuando normalizeCode recibe null")
    void testNormalizeCode_WithNull() {
        String result = StringNormalizer.normalizeCode(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Debe devolver cadena vacía cuando normalizeCode recibe cadena vacía")
    void testNormalizeCode_WithEmptyString() {
        String result = StringNormalizer.normalizeCode("  ");

        assertEquals("  ", result);
    }

    @Test
    @DisplayName("Debe preservar ñ en normalizeCode")
    void testNormalizeCode_WithEnye() {
        String result = StringNormalizer.normalizeCode("niño-123");

        assertEquals("NIÑO123", result);
    }

    // ========== normalizeForExcelNamedRange(String) Tests ==========

    @Test
    @DisplayName("Debe normalizar para rango con nombre de Excel reemplazando espacios")
    void testNormalizeForExcelNamedRange_WithSpaces() {
        String result = StringNormalizer.normalizeForExcelNamedRange("Mi Rango");

        assertEquals("Mi_Rango", result);
    }

    @Test
    @DisplayName("Debe devolver Unknown cuando normalizeForExcelNamedRange recibe null")
    void testNormalizeForExcelNamedRange_WithNull() {
        String result = StringNormalizer.normalizeForExcelNamedRange(null);

        assertEquals("Unknown", result);
    }

    @Test
    @DisplayName("Debe devolver Unknown cuando normalizeForExcelNamedRange recibe cadena vacía")
    void testNormalizeForExcelNamedRange_WithEmptyString() {
        String result = StringNormalizer.normalizeForExcelNamedRange("   ");

        assertEquals("Unknown", result);
    }

    @Test
    @DisplayName("Debe normalizar acentos para Excel named range")
    void testNormalizeForExcelNamedRange_WithAccents() {
        String result = StringNormalizer.normalizeForExcelNamedRange("José María, año");

        assertEquals("Jose_Maria__ano", result);
    }

    // ========== normalizeForExcel(String) Tests ==========

    @Test
    @DisplayName("Debe normalizar para Excel reemplazando espacios y acentos")
    void testNormalizeForExcel_WithSpacesAndAccents() {
        String result = StringNormalizer.normalizeForExcel("Nombre Completo");

        assertEquals("Nombre_Completo", result);
    }

    @Test
    @DisplayName("Debe devolver Unknown cuando normalizeForExcel recibe null")
    void testNormalizeForExcel_WithNull() {
        String result = StringNormalizer.normalizeForExcel(null);

        assertEquals("Unknown", result);
    }

    @Test
    @DisplayName("Debe devolver Unknown cuando normalizeForExcel recibe cadena vacía")
    void testNormalizeForExcel_WithEmptyString() {
        String result = StringNormalizer.normalizeForExcel("  ");

        assertEquals("Unknown", result);
    }

    @Test
    @DisplayName("Debe normalizar todos los acentos para Excel")
    void testNormalizeForExcel_WithAllAccents() {
        String result = StringNormalizer.normalizeForExcel("áéíóú ÁÉÍÓÚ ñÑ");

        assertEquals("aeiou_AEIOU_nN", result);
    }

    @Test
    @DisplayName("Debe generar nombre con hashcode cuando normalizeForExcel produce cadena vacía")
    void testNormalizeForExcel_WithOnlySpecialCharacters() {
        String result = StringNormalizer.normalizeForExcel("@#$%");

        assertTrue(result.startsWith("Item_"));
    }

    @Test
    @DisplayName("Debe truncar cuando normalizeForExcel excede 255 caracteres")
    void testNormalizeForExcel_WithLongString() {
        String longString = "a".repeat(300);
        String result = StringNormalizer.normalizeForExcel(longString);

        assertTrue(result.length() <= 255);
        assertTrue(result.contains("_"));
    }

    @Test
    @DisplayName("Debe eliminar múltiples guiones bajos consecutivos")
    void testNormalizeForExcel_WithMultipleSpaces() {
        String result = StringNormalizer.normalizeForExcel("Nombre   con    espacios");

        assertEquals("Nombre_con_espacios", result);
    }

    @Test
    @DisplayName("Debe eliminar guiones bajos al inicio y final")
    void testNormalizeForExcel_WithLeadingTrailingSpaces() {
        String result = StringNormalizer.normalizeForExcel("  Nombre  ");

        assertEquals("Nombre", result);
    }

    // ========== buildExcelNormalizationFormula(String) Tests ==========

    @Test
    @DisplayName("Debe construir fórmula de normalización para Excel")
    void testBuildExcelNormalizationFormula_WithCellReference() {
        String result = StringNormalizer.buildExcelNormalizationFormula("A1");

        assertTrue(result.contains("SUBSTITUTE"));
        assertTrue(result.contains("A1"));
    }

    @Test
    @DisplayName("Debe devolver Unknown cuando buildExcelNormalizationFormula recibe null")
    void testBuildExcelNormalizationFormula_WithNull() {
        String result = StringNormalizer.buildExcelNormalizationFormula(null);

        assertEquals("\"Unknown\"", result);
    }

    @Test
    @DisplayName("Debe devolver Unknown cuando buildExcelNormalizationFormula recibe cadena vacía")
    void testBuildExcelNormalizationFormula_WithEmptyString() {
        String result = StringNormalizer.buildExcelNormalizationFormula("  ");

        assertEquals("\"Unknown\"", result);
    }

    @Test
    @DisplayName("Debe construir fórmula con sustituciones anidadas")
    void testBuildExcelNormalizationFormula_ContainsAllSubstitutions() {
        String result = StringNormalizer.buildExcelNormalizationFormula("B2");

        assertTrue(result.contains("\" \",\"_\""));
        assertTrue(result.contains("\"ñ\",\"n\""));
        assertTrue(result.contains("\"ó\",\"o\""));
        assertTrue(result.contains("\"á\",\"a\""));
    }

    // ========== buildNormalizedIndirectFormula(String, String) Tests ==========

    @Test
    @DisplayName("Debe construir fórmula INDIRECT con prefijo")
    void testBuildNormalizedIndirectFormula_WithPrefix() {
        String result = StringNormalizer.buildNormalizedIndirectFormula("A1", "Estados_");

        assertTrue(result.contains("INDIRECT"));
        assertTrue(result.contains("Estados_"));
    }

    @Test
    @DisplayName("Debe construir fórmula INDIRECT sin prefijo")
    void testBuildNormalizedIndirectFormula_WithoutPrefix() {
        String result = StringNormalizer.buildNormalizedIndirectFormula("A1", null);

        assertTrue(result.contains("INDIRECT"));
        assertFalse(result.contains("Estados_"));
    }

    @Test
    @DisplayName("Debe construir fórmula INDIRECT con prefijo vacío")
    void testBuildNormalizedIndirectFormula_WithEmptyPrefix() {
        String result = StringNormalizer.buildNormalizedIndirectFormula("A1", "");

        assertTrue(result.contains("INDIRECT"));
        assertTrue(result.startsWith("INDIRECT(SUBSTITUTE"));
    }

    @Test
    @DisplayName("Debe manejar null en cellReference para buildNormalizedIndirectFormula")
    void testBuildNormalizedIndirectFormula_WithNullCellReference() {
        String result = StringNormalizer.buildNormalizedIndirectFormula(null, "Prefix_");

        assertTrue(result.contains("Unknown"));
    }

    // ========== normalizeHeaderName(String) Tests ==========

    @Test
    @DisplayName("Debe eliminar texto entre paréntesis del encabezado")
    void testNormalizeHeaderName_WithParentheses() {
        String result = StringNormalizer.normalizeHeaderName("Nombre (Requerido)");

        assertEquals("Nombre", result);
    }

    @Test
    @DisplayName("Debe devolver null cuando normalizeHeaderName recibe null")
    void testNormalizeHeaderName_WithNull() {
        String result = StringNormalizer.normalizeHeaderName(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Debe eliminar saltos de línea del encabezado")
    void testNormalizeHeaderName_WithLineBreaks() {
        String result = StringNormalizer.normalizeHeaderName("Nombre\nCompleto");

        assertEquals("Nombre Completo", result);
    }

    @Test
    @DisplayName("Debe normalizar múltiples espacios en encabezado")
    void testNormalizeHeaderName_WithMultipleSpaces() {
        String result = StringNormalizer.normalizeHeaderName("Nombre   Completo");

        assertEquals("Nombre Completo", result);
    }

    @Test
    @DisplayName("Debe eliminar paréntesis y saltos de línea en una sola operación")
    void testNormalizeHeaderName_WithParenthesesAndLineBreaks() {
        String result = StringNormalizer.normalizeHeaderName("Nombre\n(Requerido)\nCompleto");

        assertEquals("NombreCompleto", result);
    }

    @Test
    @DisplayName("Debe eliminar retorno de carro del encabezado")
    void testNormalizeHeaderName_WithCarriageReturn() {
        String result = StringNormalizer.normalizeHeaderName("Nombre\r\nCompleto");

        assertEquals("Nombre Completo", result);
    }

    // ========== capitalizeFirstLetter(String) Tests (via normalize) ==========

    @Test
    @DisplayName("Debe capitalizar primera letra cuando hay solo una letra")
    void testCapitalizeFirstLetter_SingleCharacter() {
        String result = StringNormalizer.normalize("a");

        assertEquals("A", result);
    }

    @Test
    @DisplayName("Debe capitalizar primera letra de texto normal")
    void testCapitalizeFirstLetter_NormalText() {
        String result = StringNormalizer.normalize("hola mundo");

        assertEquals("Hola mundo", result);
    }

    @Test
    @DisplayName("Debe manejar cadena vacía en capitalizeFirstLetter")
    void testCapitalizeFirstLetter_EmptyString() {
        String result = StringNormalizer.normalize("");

        assertEquals("", result);
    }

    // ========== removeAccents(String) Tests (via other methods) ==========

    @Test
    @DisplayName("Debe eliminar acentos graves")
    void testRemoveAccents_WithGraveAccents() {
        String result = StringNormalizer.normalizeForComparison("àèìòù");

        assertEquals("aeiou", result);
    }

    @Test
    @DisplayName("Debe eliminar acentos circunflejos")
    void testRemoveAccents_WithCircumflexAccents() {
        String result = StringNormalizer.normalizeForComparison("âêîôû");

        assertEquals("aeiou", result);
    }

    @Test
    @DisplayName("Debe preservar Ñ mayúscula al eliminar acentos")
    void testRemoveAccents_PreserveUppercaseEnye() {
        String result = StringNormalizer.normalizePreservingCase("ESPAÑA");

        assertEquals("ESPAÑA", result);
    }

    @Test
    @DisplayName("Debe devolver null cuando removeAccents recibe null")
    void testRemoveAccents_WithNull() {
        String result = StringNormalizer.normalizeForComparison(null);

        assertNull(result);
    }
}
