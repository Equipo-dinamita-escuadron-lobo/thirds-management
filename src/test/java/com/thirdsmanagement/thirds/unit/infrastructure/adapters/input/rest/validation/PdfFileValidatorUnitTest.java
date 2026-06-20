package com.thirdsmanagement.thirds.unit.infrastructure.adapters.input.rest.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.thirdsmanagement.thirds.domain.exceptions.third.FileValidationException;
import com.thirdsmanagement.thirds.domain.exceptions.third.FileSizeExceededException;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.validation.PdfFileValidator;
import com.thirdsmanagement.thirds.infrastructure.config.FileUploadProperties;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PdfFileValidatorUnitTest {

    @Mock
    private FileUploadProperties fileProperties;

    @InjectMocks
    private PdfFileValidator validator;

    private Map<String, List<String>> allowedExtensions;
    private Map<String, List<String>> allowedMimeTypes;

    @BeforeEach
    void setUp() {
        allowedExtensions = new HashMap<>();
        allowedExtensions.put("pdf", Arrays.asList(".pdf"));

        allowedMimeTypes = new HashMap<>();
        allowedMimeTypes.put("pdf", Arrays.asList("application/pdf"));

        when(fileProperties.getAllowedExtensions()).thenReturn(allowedExtensions);
        when(fileProperties.getAllowedMimeTypes()).thenReturn(allowedMimeTypes);
        when(fileProperties.getMaxSize()).thenReturn(10L * 1024 * 1024); // 10MB
    }

    // ==================== validate - Casos exitosos ====================

    @Test
    @DisplayName("Debe validar correctamente archivo PDF válido")
    void testValidatePdfFileSuccess() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "documento.pdf",
                "application/pdf",
                "contenido del PDF".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo PDF con nombre en mayúsculas")
    void testValidateFileWithUppercaseExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "DOCUMENTO.PDF",
                "application/pdf",
                "contenido".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo PDF con extensión mixta")
    void testValidateFileWithMixedCaseExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "archivo.PdF",
                "application/pdf",
                "contenido".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo PDF con nombre complejo")
    void testValidateFileWithComplexName() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "RUT_empresa_2024-11-26.pdf",
                "application/pdf",
                "contenido del PDF".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    // ==================== validateNotNull ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando archivo es null")
    void testValidateThrowsExceptionWhenFileIsNull() {
        // Arrange
        MultipartFile file = null;

        // Act & Assert
        FileValidationException exception = assertThrows(
                FileValidationException.class,
                () -> validator.validate(file)
        );

        assertEquals("El archivo no puede ser null", exception.getMessage());
    }

    // ==================== validateNotEmpty ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando archivo está vacío")
    void testValidateThrowsExceptionWhenFileIsEmpty() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "vacio.pdf",
                "application/pdf",
                new byte[0]
        );

        // Act & Assert
        FileValidationException exception = assertThrows(
                FileValidationException.class,
                () -> validator.validate(file)
        );

        assertTrue(exception.getMessage().contains("vacio.pdf"));
        assertTrue(exception.getMessage().contains("está vacío"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando archivo tiene tamaño cero")
    void testValidateThrowsExceptionWhenFileSizeIsZero() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "documento.pdf",
                "application/pdf",
                new byte[0]
        );

        // Act & Assert
        assertThrows(FileValidationException.class, () -> validator.validate(file));
    }

    // ==================== validateSize ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando archivo excede tamaño máximo")
    void testValidateThrowsExceptionWhenFileSizeExceeded() {
        // Arrange
        byte[] largeContent = new byte[(int) (11L * 1024 * 1024)]; // 11MB
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "grande.pdf",
                "application/pdf",
                largeContent
        );

        // Act & Assert
        assertThrows(FileSizeExceededException.class, () -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo con tamaño exactamente en el límite")
    void testValidateFileExactlyAtSizeLimit() {
        // Arrange
        byte[] content = new byte[(int) (10L * 1024 * 1024)]; // Exactamente 10MB
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "limite.pdf",
                "application/pdf",
                content
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo un byte menos del límite")
    void testValidateFileOneByteUnderLimit() {
        // Arrange
        byte[] content = new byte[(int) (10L * 1024 * 1024) - 1]; // 10MB - 1 byte
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "casi-limite.pdf",
                "application/pdf",
                content
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo muy pequeño pero válido")
    void testValidateVerySmallValidFile() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "mini.pdf",
                "application/pdf",
                new byte[1]
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    // ==================== validateExtension ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando extensión no es PDF")
    void testValidateThrowsExceptionWhenExtensionIsNotPdf() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "documento.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "contenido".getBytes()
        );

        // Act & Assert
        FileValidationException exception = assertThrows(
                FileValidationException.class,
                () -> validator.validate(file)
        );

        assertTrue(exception.getMessage().contains("documento.docx"));
        assertTrue(exception.getMessage().contains("extensión no válida"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando archivo no tiene extensión")
    void testValidateThrowsExceptionWhenFileHasNoExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "archivo",
                "application/pdf",
                "contenido".getBytes()
        );

        // Act & Assert
        assertThrows(FileValidationException.class, () -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando nombre de archivo es null")
    void testValidateThrowsExceptionWhenFilenameIsNull() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                null,
                "application/pdf",
                "contenido".getBytes()
        );

        // Act & Assert
        FileValidationException exception = assertThrows(
                FileValidationException.class,
                () -> validator.validate(file)
        );

        assertNotNull(exception);
        assertNotNull(exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción para extensión txt")
    void testValidateThrowsExceptionForTxtExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "documento.txt",
                "text/plain",
                "contenido".getBytes()
        );

        // Act & Assert
        assertThrows(FileValidationException.class, () -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe lanzar excepción para extensión xlsx")
    void testValidateThrowsExceptionForXlsxExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "datos.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "contenido".getBytes()
        );

        // Act & Assert
        assertThrows(FileValidationException.class, () -> validator.validate(file));
    }

    // ==================== validateMimeType ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando MIME type es null")
    void testValidateThrowsExceptionWhenMimeTypeIsNull() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "documento.pdf",
                null,
                "contenido".getBytes()
        );

        // Act & Assert
        FileValidationException exception = assertThrows(
                FileValidationException.class,
                () -> validator.validate(file)
        );

        assertTrue(exception.getMessage().contains("documento.pdf"));
        assertTrue(exception.getMessage().contains("tipo MIME no válido"));
        assertTrue(exception.getMessage().contains("null"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando MIME type es incorrecto")
    void testValidateThrowsExceptionWhenMimeTypeIsIncorrect() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "documento.pdf",
                "text/plain",
                "contenido".getBytes()
        );

        // Act & Assert
        FileValidationException exception = assertThrows(
                FileValidationException.class,
                () -> validator.validate(file)
        );

        assertTrue(exception.getMessage().contains("documento.pdf"));
        assertTrue(exception.getMessage().contains("text/plain"));
        assertTrue(exception.getMessage().contains("tipo MIME no válido"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando MIME type es image/jpeg")
    void testValidateThrowsExceptionForImageMimeType() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "imagen.pdf",
                "image/jpeg",
                "contenido".getBytes()
        );

        // Act & Assert
        FileValidationException exception = assertThrows(
                FileValidationException.class,
                () -> validator.validate(file)
        );

        assertTrue(exception.getMessage().contains("imagen.pdf"));
        assertTrue(exception.getMessage().contains("image/jpeg"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando MIME type es application/octet-stream")
    void testValidateThrowsExceptionForOctetStreamMimeType() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "archivo.pdf",
                "application/octet-stream",
                "contenido".getBytes()
        );

        // Act & Assert
        FileValidationException exception = assertThrows(
                FileValidationException.class,
                () -> validator.validate(file)
        );

        assertTrue(exception.getMessage().contains("archivo.pdf"));
        assertTrue(exception.getMessage().contains("application/octet-stream"));
    }

    // ==================== Casos edge y combinaciones ====================

    @Test
    @DisplayName("Debe validar archivo con nombre con múltiples puntos")
    void testValidateFileWithMultipleDotsInName() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "documento.backup.2024.pdf",
                "application/pdf",
                "contenido".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo con caracteres especiales en nombre")
    void testValidateFileWithSpecialCharactersInName() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "RUT_empresa-2024 (final).pdf",
                "application/pdf",
                "contenido".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo con espacios en nombre")
    void testValidateFileWithSpacesInName() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "mi documento RUT.pdf",
                "application/pdf",
                "contenido".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe lanzar excepción para archivo con doble extensión incorrecta")
    void testValidateThrowsExceptionForDoubleExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "archivo.pdf.txt",
                "text/plain",
                "contenido".getBytes()
        );

        // Act & Assert
        assertThrows(FileValidationException.class, () -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo con extensión precedida de múltiples puntos")
    void testValidateFileWithMultipleDotsBeforeExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "archivo...pdf",
                "application/pdf",
                "contenido".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando solo hay extensión en el nombre")
    void testValidateThrowsExceptionWhenOnlyExtensionInName() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                ".pdf",
                "application/pdf",
                "contenido".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo con nombre largo")
    void testValidateFileWithLongName() {
        // Arrange
        String longName = "documento_muy_largo_con_muchos_caracteres_en_el_nombre_para_probar_validacion_2024_11_26_final_version_2.pdf";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                longName,
                "application/pdf",
                "contenido".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando extensión correcta pero MIME type incorrecto")
    void testValidateThrowsExceptionWhenExtensionCorrectButMimeTypeWrong() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "documento.pdf",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "contenido".getBytes()
        );

        // Act & Assert
        FileValidationException exception = assertThrows(
                FileValidationException.class,
                () -> validator.validate(file)
        );

        assertTrue(exception.getMessage().contains("tipo MIME no válido"));
    }

    @Test
    @DisplayName("Debe validar archivo PDF con acentos en nombre")
    void testValidateFileWithAccentsInName() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "Declaración_de_renta_2024.pdf",
                "application/pdf",
                "contenido".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo PDF con números en nombre")
    void testValidateFileWithNumbersInName() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "RUT-123456789-0.pdf",
                "application/pdf",
                "contenido".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }
}
