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
import com.thirdsmanagement.thirds.infrastructure.adapters.input.validation.ExcelFileValidator;
import com.thirdsmanagement.thirds.infrastructure.config.FileUploadProperties;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExcelFileValidatorUnitTest {

    @Mock
    private FileUploadProperties fileProperties;

    @InjectMocks
    private ExcelFileValidator validator;

    private Map<String, List<String>> allowedExtensions;
    private Map<String, List<String>> allowedMimeTypes;

    @BeforeEach
    void setUp() {
        allowedExtensions = new HashMap<>();
        allowedExtensions.put("excel", Arrays.asList(".xlsx", ".xls"));

        allowedMimeTypes = new HashMap<>();
        allowedMimeTypes.put("excel", Arrays.asList(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-excel"
        ));

        when(fileProperties.getAllowedExtensions()).thenReturn(allowedExtensions);
        when(fileProperties.getAllowedMimeTypes()).thenReturn(allowedMimeTypes);
        when(fileProperties.getMaxSize()).thenReturn(5L * 1024 * 1024); // 5MB
    }

    // ==================== validate - Casos exitosos ====================

    @Test
    @DisplayName("Debe validar correctamente archivo Excel .xlsx válido")
    void testValidateXlsxFileSuccess() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "terceros.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "contenido del archivo".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar correctamente archivo Excel .xls válido")
    void testValidateXlsFileSuccess() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "datos.xls",
                "application/vnd.ms-excel",
                "contenido del archivo".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo con nombre en mayúsculas")
    void testValidateFileWithUppercaseExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "TERCEROS.XLSX",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "contenido".getBytes()
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo con extensión mixta mayúsculas y minúsculas")
    void testValidateFileWithMixedCaseExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "datos.XlSx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "contenido".getBytes()
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
                "vacio.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new byte[0]
        );

        // Act & Assert
        FileValidationException exception = assertThrows(
                FileValidationException.class,
                () -> validator.validate(file)
        );

        assertTrue(exception.getMessage().contains("vacio.xlsx"));
        assertTrue(exception.getMessage().contains("está vacío"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando archivo tiene tamaño cero")
    void testValidateThrowsExceptionWhenFileSizeIsZero() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "archivo.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
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
        byte[] largeContent = new byte[(int) (6L * 1024 * 1024)]; // 6MB
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "grande.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                largeContent
        );

        // Act & Assert
        assertThrows(FileSizeExceededException.class, () -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo con tamaño exactamente en el límite")
    void testValidateFileExactlyAtSizeLimit() {
        // Arrange
        byte[] content = new byte[(int) (5L * 1024 * 1024)]; // Exactamente 5MB
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "limite.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                content
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo un byte menos del límite")
    void testValidateFileOneByteUnderLimit() {
        // Arrange
        byte[] content = new byte[(int) (5L * 1024 * 1024) - 1]; // 5MB - 1 byte
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "casi-limite.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                content
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    // ==================== validateExtension ====================

    @Test
    @DisplayName("Debe lanzar excepción cuando extensión es inválida")
    void testValidateThrowsExceptionWhenExtensionIsInvalid() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "documento.pdf",
                "application/pdf",
                "contenido".getBytes()
        );

        // Act & Assert
        FileValidationException exception = assertThrows(
                FileValidationException.class,
                () -> validator.validate(file)
        );

        assertTrue(exception.getMessage().contains("documento.pdf"));
        assertTrue(exception.getMessage().contains("extensión no válida"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando archivo no tiene extensión")
    void testValidateThrowsExceptionWhenFileHasNoExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "archivo",
                "application/octet-stream",
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
                "application/octet-stream",
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
                "datos.txt",
                "text/plain",
                "contenido".getBytes()
        );

        // Act & Assert
        assertThrows(FileValidationException.class, () -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe lanzar excepción para extensión csv")
    void testValidateThrowsExceptionForCsvExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "datos.csv",
                "text/csv",
                "contenido".getBytes()
        );

        // Act & Assert
        assertThrows(FileValidationException.class, () -> validator.validate(file));
    }

    // ==================== Casos edge y combinaciones ====================

    @Test
    @DisplayName("Debe validar archivo con nombre complejo con múltiples puntos")
    void testValidateFileWithMultipleDotsInName() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "archivo.backup.2024.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
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
                "datos_terceros-2024 (final).xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
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
                "mis terceros.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
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
                "archivo.xlsx.txt",
                "text/plain",
                "contenido".getBytes()
        );

        // Act & Assert
        assertThrows(FileValidationException.class, () -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo muy pequeño pero válido")
    void testValidateVerySmallValidFile() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "mini.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new byte[1]
        );

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    @DisplayName("Debe validar archivo con extensión precedida de múltiples puntos")
    void testValidateFileWithMultipleDotsBeforeExtension() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "archivo...xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
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
                ".txt",
                "text/plain",
                "contenido".getBytes()
        );

        // Act & Assert
        assertThrows(FileValidationException.class, () -> validator.validate(file));
    }
}
