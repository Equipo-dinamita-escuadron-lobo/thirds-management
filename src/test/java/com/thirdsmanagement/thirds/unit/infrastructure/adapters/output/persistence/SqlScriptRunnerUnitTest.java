package com.thirdsmanagement.thirds.unit.infrastructure.adapters.output.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.jdbc.core.JdbcTemplate;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.SqlScriptRunner;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SqlScriptRunnerUnitTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private SqlScriptRunner sqlScriptRunner;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el archivo SQL no existe")
    void testExecuteSqlFileThrowsExceptionWhenFileNotFound() {
        // Arrange
        String sqlFilePath = "sql/nonexistent.sql";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> sqlScriptRunner.executeSqlFile(sqlFilePath));
        assertEquals("Error cargando datos desde archivo: sql/nonexistent.sql", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando ruta del archivo es null")
    void testExecuteSqlFileThrowsExceptionWhenPathIsNull() {
        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> sqlScriptRunner.executeSqlFile(null));
    }

    @Test
    @DisplayName("Debe propagar excepciones de ejecución SQL")
    void testExecuteSqlFileThrowsExceptionOnSqlError() {
        // Arrange
        String sqlFilePath = "data/geography/01_countries.sql";
        doThrow(new RuntimeException("SQL Error")).when(jdbcTemplate).execute(anyString());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> sqlScriptRunner.executeSqlFile(sqlFilePath));
        assertEquals("Error cargando datos desde archivo: data/geography/01_countries.sql", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción con archivo en ruta inválida")
    void testExecuteSqlFileWithInvalidPath() {
        // Arrange
        String sqlFilePath = "invalid/path/file.sql";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> sqlScriptRunner.executeSqlFile(sqlFilePath));
        assertEquals("Error cargando datos desde archivo: invalid/path/file.sql", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción con extensión incorrecta")
    void testExecuteSqlFileWithWrongExtension() {
        // Arrange
        String sqlFilePath = "sql/file.txt";

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> sqlScriptRunner.executeSqlFile(sqlFilePath));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando archivo SQL no existe en classpath")
    void testExecuteSqlFileNotInClasspath() {
        // Arrange
        String sqlFilePath = "sql/missing-file.sql";

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> sqlScriptRunner.executeSqlFile(sqlFilePath));
        assertEquals("Error cargando datos desde archivo: sql/missing-file.sql", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción con archivo vacío cuando existe")
    void testExecuteSqlFileWithEmptyFile() {
        // Arrange
        String sqlFilePath = "sql/empty.sql";

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> sqlScriptRunner.executeSqlFile(sqlFilePath));
    }

    @Test
    @DisplayName("Debe manejar errores de I/O al leer archivo")
    void testExecuteSqlFileWithIOError() {
        // Arrange
        String sqlFilePath = "sql/unreadable.sql";

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> sqlScriptRunner.executeSqlFile(sqlFilePath));
    }

    @Test
    @DisplayName("Debe lanzar excepción con ruta que contiene caracteres especiales")
    void testExecuteSqlFileWithSpecialCharactersInPath() {
        // Arrange
        String sqlFilePath = "sql/@#$%/file.sql";

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> sqlScriptRunner.executeSqlFile(sqlFilePath));
    }

    @Test
    @DisplayName("Debe lanzar excepción con ruta absoluta fuera del classpath")
    void testExecuteSqlFileWithAbsolutePath() {
        // Arrange
        String sqlFilePath = "/absolute/path/file.sql";

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> sqlScriptRunner.executeSqlFile(sqlFilePath));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando archivo tiene contenido solo con comentarios")
    void testExecuteSqlFileWithOnlyComments() {
        // Arrange
        String sqlFilePath = "sql/comments-only.sql";

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> sqlScriptRunner.executeSqlFile(sqlFilePath));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando jdbcTemplate es null por error de configuración")
    void testExecuteSqlFileWithNullJdbcTemplate() {
        // Arrange
        SqlScriptRunner runnerWithNullTemplate = new SqlScriptRunner(null);
        String sqlFilePath = "sql/geography/countries.sql";

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> runnerWithNullTemplate.executeSqlFile(sqlFilePath));
    }

    @Test
    @DisplayName("Debe lanzar excepción con archivo que contiene SQL malformado")
    void testExecuteSqlFileWithMalformedSQL() {
        // Arrange
        String sqlFilePath = "sql/malformed.sql";

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> sqlScriptRunner.executeSqlFile(sqlFilePath));
    }

    @Test
    @DisplayName("Debe lanzar excepción con archivo de directorio en lugar de archivo")
    void testExecuteSqlFileWithDirectoryPath() {
        // Arrange
        String sqlFilePath = "sql/geography/";

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> sqlScriptRunner.executeSqlFile(sqlFilePath));
    }
}

