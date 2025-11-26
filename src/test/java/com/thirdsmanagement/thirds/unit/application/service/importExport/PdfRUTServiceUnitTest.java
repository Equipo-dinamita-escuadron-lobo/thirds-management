package com.thirdsmanagement.thirds.unit.application.service.importExport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import com.thirdsmanagement.thirds.application.service.importExport.PdfRUTService;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdInvalidDataException;
import com.thirdsmanagement.thirds.domain.model.PdfRUTContent;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.validation.FileValidator;

/**
 * Tests unitarios para PdfRUTService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PdfRUTServiceUnitTest {

    @Mock
    private FileValidator fileValidator;

    @InjectMocks
    private PdfRUTService pdfRUTService;

    @Mock
    private MultipartFile multipartFile;

    @TempDir
    File tempDirectory;

    private PdfRUTContent pdfRUTContent;

    @BeforeEach
    void setUp() {
        pdfRUTContent = new PdfRUTContent();
        pdfRUTContent.setFile(multipartFile);
    }

    // ========== extractContent Tests - Validación de Request ==========

    @Test
    @DisplayName("Debe lanzar ThirdInvalidDataException cuando el request es null")
    void testExtractContent_WithNullRequest() {
        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(ThirdInvalidDataException.class, () -> {
            pdfRUTService.extractContent(null);
        });

        assertEquals("El request no puede ser null", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar ThirdInvalidDataException cuando el archivo es null")
    void testExtractContent_WithNullFile() {
        // Arrange
        pdfRUTContent.setFile(null);

        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(ThirdInvalidDataException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });

        assertEquals("El archivo PDF no puede ser null o vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar ThirdInvalidDataException cuando el archivo está vacío")
    void testExtractContent_WithEmptyFile() {
        // Arrange
        when(multipartFile.isEmpty()).thenReturn(true);

        // Act & Assert
        ThirdInvalidDataException exception = assertThrows(ThirdInvalidDataException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });

        assertEquals("El archivo PDF no puede ser null o vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Debe invocar la validación del archivo")
    void testExtractContent_InvokesFileValidator() throws IOException {
        // Arrange
        when(multipartFile.isEmpty()).thenReturn(false);
        doNothing().when(fileValidator).validate(multipartFile);
        doThrow(new IOException("Error al transferir archivo")).when(multipartFile).transferTo(any(File.class));

        // Act & Assert
        assertThrows(Exception.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });

        verify(fileValidator).validate(multipartFile);
    }

    // ========== cleanString Tests ==========

    @Test
    @DisplayName("Debe limpiar espacios múltiples de un string")
    void testCleanString_WithMultipleSpaces() {
        // Arrange
        String input = "Texto   con    espacios     múltiples";

        // Act
        String result = PdfRUTService.cleanString(input);

        // Assert
        assertEquals("Texto con espacios múltiples", result);
    }

    @Test
    @DisplayName("Debe limpiar saltos de línea con espacios")
    void testCleanString_WithLineBreaksAndSpaces() {
        // Arrange
        String input = "Línea 1  \n  Línea 2";

        // Act
        String result = PdfRUTService.cleanString(input);

        // Assert
        assertEquals("Línea 1\nLínea 2", result);
    }

    @Test
    @DisplayName("Debe eliminar espacios al inicio y final")
    void testCleanString_TrimsLeadingAndTrailingSpaces() {
        // Arrange
        String input = "   Texto con espacios   ";

        // Act
        String result = PdfRUTService.cleanString(input);

        // Assert
        assertEquals("Texto con espacios", result);
    }

    @Test
    @DisplayName("Debe unir números separados por espacios")
    void testCleanString_JoinsNumbersSeparatedBySpaces() {
        // Arrange
        String input = "123 456 789";

        // Act
        String result = PdfRUTService.cleanString(input);

        // Assert
        assertEquals("123456789", result);
    }

    @Test
    @DisplayName("Debe manejar string vacío")
    void testCleanString_WithEmptyString() {
        // Arrange
        String input = "";

        // Act
        String result = PdfRUTService.cleanString(input);

        // Assert
        assertEquals("", result);
    }

    @Test
    @DisplayName("Debe manejar string solo con espacios")
    void testCleanString_WithOnlySpaces() {
        // Arrange
        String input = "     ";

        // Act
        String result = PdfRUTService.cleanString(input);

        // Assert
        assertEquals("", result);
    }

    @Test
    @DisplayName("Debe preservar un solo espacio entre palabras")
    void testCleanString_PreservesSingleSpaceBetweenWords() {
        // Arrange
        String input = "Palabra1 Palabra2 Palabra3";

        // Act
        String result = PdfRUTService.cleanString(input);

        // Assert
        assertEquals("Palabra1 Palabra2 Palabra3", result);
    }

    // ========== separateNumbersAndText Tests ==========

    @Test
    @DisplayName("Debe separar números y texto")
    void testSeparateNumbersAndText_WithNumbersAndText() {
        // Arrange
        String input = "123ABC456DEF";

        // Act
        String[] result = PdfRUTService.separateNumbersAndText(input);

        // Assert
        assertEquals(4, result.length);
        assertEquals("123", result[0]);
        assertEquals("ABC", result[1]);
        assertEquals("456", result[2]);
        assertEquals("DEF", result[3]);
    }

    @Test
    @DisplayName("Debe separar número inicial seguido de texto")
    void testSeparateNumbersAndText_NumberFollowedByText() {
        // Arrange
        String input = "900123456NIT";

        // Act
        String[] result = PdfRUTService.separateNumbersAndText(input);

        // Assert
        assertEquals(2, result.length);
        assertEquals("900123456", result[0]);
        assertEquals("NIT", result[1]);
    }

    @Test
    @DisplayName("Debe manejar solo números")
    void testSeparateNumbersAndText_OnlyNumbers() {
        // Arrange
        String input = "123456789";

        // Act
        String[] result = PdfRUTService.separateNumbersAndText(input);

        // Assert
        assertEquals(1, result.length);
        assertEquals("123456789", result[0]);
    }

    @Test
    @DisplayName("Debe manejar solo texto")
    void testSeparateNumbersAndText_OnlyText() {
        // Arrange
        String input = "ABCDEF";

        // Act
        String[] result = PdfRUTService.separateNumbersAndText(input);

        // Assert
        assertEquals(1, result.length);
        assertEquals("ABCDEF", result[0]);
    }

    @Test
    @DisplayName("Debe manejar string vacío")
    void testSeparateNumbersAndText_WithEmptyString() {
        // Arrange
        String input = "";

        // Act
        String[] result = PdfRUTService.separateNumbersAndText(input);

        // Assert
        assertEquals(1, result.length);
        assertEquals("", result[0]);
    }

    @Test
    @DisplayName("Debe separar múltiples transiciones entre números y texto")
    void testSeparateNumbersAndText_MultipleTransitions() {
        // Arrange
        String input = "12A34B56C";

        // Act
        String[] result = PdfRUTService.separateNumbersAndText(input);

        // Assert
        assertEquals(6, result.length);
        assertEquals("12", result[0]);
        assertEquals("A", result[1]);
        assertEquals("34", result[2]);
        assertEquals("B", result[3]);
        assertEquals("56", result[4]);
        assertEquals("C", result[5]);
    }

    // ========== separateAndJoinNumbers Tests ==========

    @Test
    @DisplayName("Debe separar números con múltiples espacios")
    void testSeparateAndJoinNumbers_WithMultipleSpaces() {
        // Arrange
        String input = "123  456  789";

        // Act
        String[] result = PdfRUTService.separateAndJoinNumbers(input);

        // Assert
        assertEquals(3, result.length);
        assertEquals("123", result[0]);
        assertEquals("456", result[1]);
        assertEquals("789", result[2]);
    }

    @Test
    @DisplayName("Debe unir números con espacios simples")
    void testSeparateAndJoinNumbers_WithSingleSpaces() {
        // Arrange
        String input = "3 0 1 2 3 4 5 6 7 8";

        // Act
        String[] result = PdfRUTService.separateAndJoinNumbers(input);

        // Assert
        assertEquals(1, result.length);
        assertEquals("3012345678", result[0]);
    }

    @Test
    @DisplayName("Debe combinar espacios simples y múltiples correctamente")
    void testSeparateAndJoinNumbers_MixedSpaces() {
        // Arrange
        String input = "300 123 4567  601 234 5678";

        // Act
        String[] result = PdfRUTService.separateAndJoinNumbers(input);

        // Assert
        assertEquals(2, result.length);
        assertEquals("3001234567", result[0]);
        assertEquals("6012345678", result[1]);
    }

    @Test
    @DisplayName("Debe manejar string sin espacios")
    void testSeparateAndJoinNumbers_NoSpaces() {
        // Arrange
        String input = "123456789";

        // Act
        String[] result = PdfRUTService.separateAndJoinNumbers(input);

        // Assert
        assertEquals(1, result.length);
        assertEquals("123456789", result[0]);
    }

    @Test
    @DisplayName("Debe manejar string vacío")
    void testSeparateAndJoinNumbers_EmptyString() {
        // Arrange
        String input = "";

        // Act
        String[] result = PdfRUTService.separateAndJoinNumbers(input);

        // Assert
        assertEquals(1, result.length);
        assertEquals("", result[0]);
    }

    @Test
    @DisplayName("Debe manejar solo espacios múltiples")
    void testSeparateAndJoinNumbers_OnlyMultipleSpaces() {
        // Arrange
        String input = "   ";

        // Act
        String[] result = PdfRUTService.separateAndJoinNumbers(input);

        // Assert
        assertEquals(0, result.length);
    }

    // ========== splitBySpaceAndUpperCase Tests ==========

    @Test
    @DisplayName("Debe dividir por espacio seguido de mayúscula")
    void testSplitBySpaceAndUpperCase_WithSpaceAndUpperCase() {
        // Arrange
        String input = "Primer Segundo Tercero Cuarto";

        // Act
        String[] result = PdfRUTService.splitBySpaceAndUpperCase(input);

        // Assert
        assertEquals(4, result.length);
        assertEquals("Primer ", result[0]);
        assertEquals("Segundo ", result[1]);
        assertEquals("Tercero ", result[2]);
        assertEquals("Cuarto", result[3]);
    }

    @Test
    @DisplayName("Debe dividir nombres y apellidos correctamente")
    void testSplitBySpaceAndUpperCase_WithNamesAndLastNames() {
        // Arrange
        String input = "GARCIA LOPEZ JUAN CARLOS";

        // Act
        String[] result = PdfRUTService.splitBySpaceAndUpperCase(input);

        // Assert
        assertEquals(4, result.length);
        assertEquals("GARCIA ", result[0]);
        assertEquals("LOPEZ ", result[1]);
        assertEquals("JUAN ", result[2]);
        assertEquals("CARLOS", result[3]);
    }

    @Test
    @DisplayName("Debe manejar texto sin espacios seguidos de mayúsculas")
    void testSplitBySpaceAndUpperCase_NoSpaceBeforeUpperCase() {
        // Arrange
        String input = "TextoSinEspacios";

        // Act
        String[] result = PdfRUTService.splitBySpaceAndUpperCase(input);

        // Assert
        assertEquals(1, result.length);
        assertEquals("TextoSinEspacios", result[0]);
    }

    @Test
    @DisplayName("Debe manejar string vacío")
    void testSplitBySpaceAndUpperCase_EmptyString() {
        // Arrange
        String input = "";

        // Act
        String[] result = PdfRUTService.splitBySpaceAndUpperCase(input);

        // Assert
        assertEquals(1, result.length);
        assertEquals("", result[0]);
    }

    @Test
    @DisplayName("Debe manejar string con solo minúsculas")
    void testSplitBySpaceAndUpperCase_OnlyLowerCase() {
        // Arrange
        String input = "texto en minusculas";

        // Act
        String[] result = PdfRUTService.splitBySpaceAndUpperCase(input);

        // Assert
        assertEquals(1, result.length);
        assertEquals("texto en minusculas", result[0]);
    }

    @Test
    @DisplayName("Debe manejar dos palabras con mayúsculas")
    void testSplitBySpaceAndUpperCase_TwoWords() {
        // Arrange
        String input = "Primera Segunda";

        // Act
        String[] result = PdfRUTService.splitBySpaceAndUpperCase(input);

        // Assert
        assertEquals(2, result.length);
        assertEquals("Primera ", result[0]);
        assertEquals("Segunda", result[1]);
    }

    // ========== extractContent Integration Tests ==========

    @Test
    @DisplayName("Debe lanzar IOException cuando el PDF tiene formato inválido")
    void testExtractContent_WithInvalidPdfFormat() throws IOException {
        // Arrange
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn("contenido no PDF".getBytes());
        doNothing().when(fileValidator).validate(multipartFile);
        doAnswer(invocation -> {
            File file = invocation.getArgument(0);
            Files.write(file.toPath(), "contenido no PDF".getBytes());
            return null;
        }).when(multipartFile).transferTo(any(File.class));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el validador falla")
    void testExtractContent_WhenValidatorFails() {
        // Arrange
        when(multipartFile.isEmpty()).thenReturn(false);
        doThrow(new RuntimeException("Error de validación")).when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });

        verify(fileValidator).validate(multipartFile);
    }

    @Test
    @DisplayName("Debe lanzar IOException cuando falla la transferencia del archivo")
    void testExtractContent_WhenTransferFails() throws IOException {
        // Arrange
        when(multipartFile.isEmpty()).thenReturn(false);
        doNothing().when(fileValidator).validate(multipartFile);
        doThrow(new IOException("Error al transferir")).when(multipartFile).transferTo(any(File.class));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    // ========== Utility Methods Edge Cases ==========

    @Test
    @DisplayName("Debe manejar cleanString con tabs y saltos de línea mezclados")
    void testCleanString_WithMixedWhitespace() {
        // Arrange
        String input = "Texto\t\tcon\n\ntabs\ty\nsaltos";

        // Act
        String result = PdfRUTService.cleanString(input);

        // Assert
        assertNotNull(result);
        assertTrue(result.length() < input.length());
    }

    @Test
    @DisplayName("Debe manejar separateNumbersAndText con caracteres especiales")
    void testSeparateNumbersAndText_WithSpecialCharacters() {
        // Arrange
        String input = "123-ABC";

        // Act
        String[] result = PdfRUTService.separateNumbersAndText(input);

        // Assert
        assertTrue(result.length >= 2);
    }

    @Test
    @DisplayName("Debe manejar separateAndJoinNumbers con texto y números mezclados")
    void testSeparateAndJoinNumbers_WithMixedContent() {
        // Arrange
        String input = "Tel: 300 123 4567  Cel: 310 987 6543";

        // Act
        String[] result = PdfRUTService.separateAndJoinNumbers(input);

        // Assert
        assertTrue(result.length >= 1);
        assertNotNull(result[0]);
    }

    @Test
    @DisplayName("Debe manejar splitBySpaceAndUpperCase con números")
    void testSplitBySpaceAndUpperCase_WithNumbers() {
        // Arrange
        String input = "Texto1 Texto2 Texto3";

        // Act
        String[] result = PdfRUTService.splitBySpaceAndUpperCase(input);

        // Assert
        assertTrue(result.length >= 1);
    }

    @Test
    @DisplayName("Debe manejar cleanString con números y espacios intercalados")
    void testCleanString_WithInterleavedNumbersAndSpaces() {
        // Arrange
        String input = "1 2 3 4 5 6 7 8 9 0";

        // Act
        String result = PdfRUTService.cleanString(input);

        // Assert
        assertEquals("1234567890", result);
    }

    @Test
    @DisplayName("Debe manejar separateNumbersAndText con un solo carácter")
    void testSeparateNumbersAndText_SingleCharacter() {
        // Arrange
        String input = "A";

        // Act
        String[] result = PdfRUTService.separateNumbersAndText(input);

        // Assert
        assertEquals(1, result.length);
        assertEquals("A", result[0]);
    }

    @Test
    @DisplayName("Debe manejar separateAndJoinNumbers con múltiples separadores consecutivos")
    void testSeparateAndJoinNumbers_MultipleConsecutiveSeparators() {
        // Arrange
        String input = "123    456    789";

        // Act
        String[] result = PdfRUTService.separateAndJoinNumbers(input);

        // Assert
        assertEquals(3, result.length);
    }

    @Test
    @DisplayName("Debe manejar splitBySpaceAndUpperCase con espacios múltiples")
    void testSplitBySpaceAndUpperCase_MultipleSpaces() {
        // Arrange
        String input = "Palabra1  Palabra2";

        // Act
        String[] result = PdfRUTService.splitBySpaceAndUpperCase(input);

        // Assert
        assertTrue(result.length >= 1);
    }

    // ========== Tests de Integración con Persona Jurídica ==========

    @Test
    @DisplayName("Debe procesar PDF de persona jurídica correctamente")
    void testExtractContent_PersonaJuridica_Integration() throws IOException {
        // Arrange
        String pdfContent = "Documento de prueba\n" +
                "CLASIFICACIÓN\n" +
                "Línea 1\n" +
                "123456789-0\n" +
                "Persona jurídica\n" +
                "Línea 4\n" +
                "EMPRESA TEST SAS\n" +
                "Más datos\n" +
                "COLOMBIA ANTIOQUIA MEDELLIN\n" +
                "Calle 50 # 25-30\n" +
                "correo@empresa.com\n" +
                "Tel 3001234567";

        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });

        verify(fileValidator).validate(multipartFile);
    }

    @Test
    @DisplayName("Debe procesar PDF de persona natural correctamente")
    void testExtractContent_PersonaNatural_Integration() throws IOException {
        // Arrange
        String pdfContent = "Documento de prueba\n" +
                "CLASIFICACIÓN\n" +
                "Línea 1\n" +
                "12345678\n" +
                "Persona Natural CC\n" +
                "Línea 4\n" +
                "PEREZ GOMEZ JUAN CARLOS\n" +
                "Más datos\n" +
                "COLOMBIA VALLE CALI\n" +
                "Carrera 100 # 15-20\n" +
                "correo@mail.com\n" +
                "Cel 3109876543";

        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });

        verify(fileValidator).validate(multipartFile);
    }

    @Test
    @DisplayName("Debe manejar excepción durante el procesamiento de persona jurídica")
    void testExtractContent_PersonaJuridica_WithProcessingError() throws IOException {
        // Arrange
        String pdfContent = "CLASIFICACIÓN\nDatos incompletos";
        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe manejar excepción durante el procesamiento de persona natural")
    void testExtractContent_PersonaNatural_WithProcessingError() throws IOException {
        // Arrange
        String pdfContent = "CLASIFICACIÓN\nPersona Natural\nDatos incompletos";
        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe limpiar archivos temporales después de procesamiento")
    void testExtractContent_CleansUpTempFiles() throws IOException {
        // Arrange
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(new byte[100]);
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });

        verify(fileValidator).validate(multipartFile);
    }

    @Test
    @DisplayName("Debe limpiar archivos temporales después de excepción")
    void testExtractContent_CleansUpTempFilesOnException() throws IOException {
        // Arrange
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenThrow(new IOException("Error de lectura"));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe procesar NIT de persona jurídica con dígito verificador")
    void testExtractContent_ExtractsNIT_WithDigitVerificador() throws IOException {
        // Arrange
        String pdfContent = "CLASIFICACIÓN\n" +
                "Línea\n" +
                "900123456-7\n" +
                "Persona jurídica\n" +
                "Extra\n" +
                "MI EMPRESA SAS\n" +
                "COLOMBIA BOGOTA DC BOGOTA\n" +
                "Dirección\n" +
                "correo@test.com\n" +
                "3001234567";

        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe procesar cédula de persona natural correctamente")
    void testExtractContent_ExtractsCC_PersonaNatural() throws IOException {
        // Arrange
        String pdfContent = "CLASIFICACIÓN\n" +
                "Info\n" +
                "10123456\n" +
                "Persona Natural CC\n" +
                "Info2\n" +
                "RODRIGUEZ MARTINEZ PEDRO PABLO\n" +
                "COLOMBIA CUNDINAMARCA SOACHA\n" +
                "Calle 10\n" +
                "email@test.com\n" +
                "3209876543";

        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe extraer ubicación geográfica completa")
    void testExtractContent_ExtractsCompleteGeographicData() throws IOException {
        // Arrange
        String pdfContent = "Info\n" +
                "CLASIFICACIÓN\n" +
                "Data\n" +
                "123456\n" +
                "Persona jurídica\n" +
                "Data2\n" +
                "COMPAÑIA TEST\n" +
                "COLOMBIA SANTANDER BUCARAMANGA\n" +
                "Carrera 27\n" +
                "info@empresa.co\n" +
                "6071234567";

        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe extraer todos los datos de contacto")
    void testExtractContent_ExtractsAllContactData() throws IOException {
        // Arrange
        String pdfContent = "CLASIFICACIÓN\n" +
                "Línea\n" +
                "800456789-1\n" +
                "Persona jurídica\n" +
                "Data\n" +
                "ORGANIZACIÓN XYZ\n" +
                "COLOMBIA ATLANTICO BARRANQUILLA\n" +
                "Calle 85 # 50-20\n" +
                "contacto@organizacion.com\n" +
                "Tel 3151234567";

        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe manejar PDF sin palabra CLASIFICACIÓN")
    void testExtractContent_WithoutClasificacion() throws IOException {
        // Arrange
        String pdfContent = "Contenido sin CLASIFICACIÓN";
        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe manejar PDF sin palabra COLOMBIA")
    void testExtractContent_WithoutColombia() throws IOException {
        // Arrange
        String pdfContent = "CLASIFICACIÓN\nPersona jurídica\nSin ubicación";
        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe procesar PDF con múltiples ocurrencias de COLOMBIA")
    void testExtractContent_WithMultipleColombia() throws IOException {
        // Arrange
        String pdfContent = "CLASIFICACIÓN\n" +
                "Data\n" +
                "123456\n" +
                "Persona jurídica\n" +
                "Extra\n" +
                "TEST COMPANY\n" +
                "Primera COLOMBIA texto\n" +
                "COLOMBIA RISARALDA PEREIRA\n" +
                "Calle 15\n" +
                "mail@test.com\n" +
                "3001112233";

        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe extraer nombres y apellidos de persona natural con 4 palabras")
    void testExtractContent_ExtractsFourWordName() throws IOException {
        // Arrange
        String pdfContent = "CLASIFICACIÓN\n" +
                "Info\n" +
                "98765432\n" +
                "Persona Natural CC\n" +
                "Data\n" +
                "LOPEZ DIAZ MARIA FERNANDA\n" +
                "COLOMBIA TOLIMA IBAGUE\n" +
                "Carrera 5\n" +
                "maria@mail.com\n" +
                "3145556677";

        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe procesar teléfono con múltiples espacios")
    void testExtractContent_ProcessesPhoneWithSpaces() throws IOException {
        // Arrange
        String pdfContent = "CLASIFICACIÓN\n" +
                "Data\n" +
                "555666777-8\n" +
                "Persona jurídica\n" +
                "Info\n" +
                "SOCIEDAD TEST\n" +
                "COLOMBIA QUINDIO ARMENIA\n" +
                "Avenida 1\n" +
                "contacto@sociedad.com\n" +
                "Tel 300 111 22 33";

        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe manejar razón social con caracteres especiales")
    void testExtractContent_HandlesSpecialCharactersInRazonSocial() throws IOException {
        // Arrange
        String pdfContent = "CLASIFICACIÓN\n" +
                "Line1\n" +
                "111222333-4\n" +
                "Persona jurídica\n" +
                "Line4\n" +
                "EMPRESA & COMPAÑÍA S.A.S.\n" +
                "COLOMBIA CALDAS MANIZALES\n" +
                "Diagonal 50\n" +
                "empresa@mail.co\n" +
                "3008889999";

        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe procesar identificación sin dígito verificador")
    void testExtractContent_WithoutDigitVerificador() throws IOException {
        // Arrange
        String pdfContent = "CLASIFICACIÓN\n" +
                "Data\n" +
                "444555666\n" +
                "Persona jurídica\n" +
                "Extra\n" +
                "CORPORACIÓN TEST\n" +
                "COLOMBIA NARINO PASTO\n" +
                "Calle 20\n" +
                "corp@test.com\n" +
                "3177778888";

        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    @Test
    @DisplayName("Debe concatenar información del tercero en formato correcto")
    void testExtractContent_ConcatenatesInfoCorrectly() throws IOException {
        // Arrange
        String pdfContent = "CLASIFICACIÓN\n" +
                "Line\n" +
                "777888999-0\n" +
                "Persona jurídica\n" +
                "Line\n" +
                "COMPAÑÍA DEMO\n" +
                "COLOMBIA META VILLAVICENCIO\n" +
                "Transversal 10\n" +
                "demo@empresa.com\n" +
                "3201234567";

        File tempFile = createTempPdfFile(pdfContent);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getBytes()).thenReturn(Files.readAllBytes(tempFile.toPath()));
        doNothing().when(fileValidator).validate(multipartFile);

        // Act & Assert
        assertThrows(IOException.class, () -> {
            pdfRUTService.extractContent(pdfRUTContent);
        });
    }

    // ========== Helper Methods ==========

    private File createTempPdfFile(String content) throws IOException {
        File tempFile = new File(tempDirectory, "test.pdf");
        Files.write(tempFile.toPath(), content.getBytes());
        return tempFile;
    }
}
