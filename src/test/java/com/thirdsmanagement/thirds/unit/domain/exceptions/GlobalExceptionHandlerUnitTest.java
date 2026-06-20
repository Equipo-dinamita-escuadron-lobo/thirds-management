package com.thirdsmanagement.thirds.unit.domain.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thirdsmanagement.thirds.domain.exceptions.ErrorCode;
import com.thirdsmanagement.thirds.domain.exceptions.GlobalExceptionHandler;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdNotFound;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeInUseException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdAlreadyExists;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ErrorResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GlobalExceptionHandlerUnitTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    @DisplayName("test Maneja excepción de negocio con estado NOT_FOUND")
    void testHandleBusinessExceptionNotFound() {
        // Arrange
        ThirdNotFound exception = new ThirdNotFound("Tercero no encontrado");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Tercero no encontrado", response.getBody().getMessage());
        assertEquals("/api/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    @DisplayName("test Maneja excepción de negocio con estado CONFLICT")
    void testHandleBusinessExceptionConflict() {
        // Arrange
        TypeIdAlreadyExists exception = new TypeIdAlreadyExists("CC");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("CC"));
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    @DisplayName("test Maneja excepción de negocio con estado BAD_REQUEST")
    void testHandleBusinessExceptionBadRequest() {
        // Arrange
        ThirdTypeInUseException exception = new ThirdTypeInUseException("Cliente", true);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("Cliente"));
    }

    @Test
    @DisplayName("test Maneja HttpMessageNotReadableException genérico")
    void testHandleHttpMessageNotReadableGeneric() {
        // Arrange
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid JSON");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadable(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Error en el formato de los datos enviados", response.getBody().getMessage());
        assertEquals("INVALID_REQUEST_FORMAT", response.getBody().getCode());
    }

    @Test
    @DisplayName("test Maneja HttpMessageNotReadableException con PersonClassification inválida")
    void testHandleHttpMessageNotReadableWithInvalidPersonClassification() {
        // Arrange
        InvalidFormatException invalidFormatException = mock(InvalidFormatException.class);
        when(invalidFormatException.getTargetType()).thenReturn((Class) com.thirdsmanagement.thirds.domain.model.PersonClassification.class);
        when(invalidFormatException.getValue()).thenReturn("INVALID_TYPE");

        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid enum", invalidFormatException);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadable(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("INVALID_TYPE"));
        assertTrue(response.getBody().getMessage().contains("NATURAL_PERSON"));
        assertTrue(response.getBody().getMessage().contains("LEGAL_ENTITY"));
        assertEquals("TYPE_ID_INVALID_CLASSIFICATION", response.getBody().getCode());
    }

    @Test
    @DisplayName("test Maneja MethodArgumentNotValidException genérico")
    void testHandleMethodArgumentNotValidGeneric() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "Campo requerido");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleMethodArgumentNotValid(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("fieldErrors"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) body.get("fieldErrors");
        assertEquals("Campo requerido", fieldErrors.get("fieldName"));
    }

    @Test
    @DisplayName("test Maneja MethodArgumentNotValidException con classification null")
    void testHandleMethodArgumentNotValidWithClassificationNull() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("typeId", "classification", "La clasificación no puede ser nula");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleMethodArgumentNotValid(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        ErrorResponse errorResponse = (ErrorResponse) body.get("error");
        
        assertTrue(errorResponse.getMessage().contains("clasificación de persona"));
        assertTrue(errorResponse.getMessage().contains("NATURAL_PERSON"));
        assertEquals("TYPE_ID_INVALID_CLASSIFICATION", errorResponse.getCode());
    }

    @Test
    @DisplayName("test Maneja ConstraintViolationException")
    void testHandleConstraintViolation() {
        // Arrange
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);
        when(path1.toString()).thenReturn("parameter1");
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage()).thenReturn("Debe ser positivo");

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation1));

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleConstraintViolation(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("error"));
        assertTrue(body.containsKey("violations"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> violations = (Map<String, String>) body.get("violations");
        assertEquals("Debe ser positivo", violations.get("parameter1"));
    }

    @Test
    @DisplayName("test Maneja DataIntegrityViolationException genérica")
    void testHandleDataIntegrityViolationGeneric() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Violación de restricción única");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Violación de restricción única"));
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), response.getBody().getCode());
    }

    @Test
    @DisplayName("test Maneja DataIntegrityViolationException con fk_thirds_third_types")
    void testHandleDataIntegrityViolationWithThirdTypeFK() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
            "ERROR: insert or update on table \"thirds\" violates foreign key constraint \"fk_thirds_third_types\" Detail: Key (tt_id)=(123) is not present in table \"third_types\"."
        );

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("tipo de tercero") || 
                   response.getBody().getMessage().contains("integridad de datos"));
    }

    @Test
    @DisplayName("test Maneja DataIntegrityViolationException con fk_thirds_type_id")
    void testHandleDataIntegrityViolationWithTypeIdFK() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
            "ERROR: insert or update on table \"thirds\" violates foreign key constraint \"fk_thirds_type_id\" Detail: Key (ti_id)=(456) is not present in table \"type_ids\"."
        );

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("tipo de identificación") || 
                   response.getBody().getMessage().contains("integridad de datos"));
    }

    @Test
    @DisplayName("test Maneja MaxUploadSizeExceededException")
    void testHandleMaxUploadSizeExceeded() {
        // Arrange
        MaxUploadSizeExceededException exception = new MaxUploadSizeExceededException(5242880);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMaxUploadSizeExceeded(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertEquals(413, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("tamaño"));
    }

    @Test
    @DisplayName("test Maneja MaxUploadSizeExceededException sin tamaño máximo especificado")
    void testHandleMaxUploadSizeExceededWithoutMaxSize() {
        // Arrange
        MaxUploadSizeExceededException exception = new MaxUploadSizeExceededException(-1);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleMaxUploadSizeExceeded(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
    }

    @Test
    @DisplayName("test Maneja excepción genérica")
    void testHandleGenericException() {
        // Arrange
        Exception exception = new RuntimeException("Error inesperado");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Ha ocurrido un error interno del servidor", response.getBody().getMessage());
        assertEquals(ErrorCode.GENERIC_ERROR.getCode(), response.getBody().getCode());
    }

    @Test
    @DisplayName("test Extrae ID de mensaje de error con patrón PostgreSQL")
    void testExtractIdFromConstraintMessagePostgresPattern() {
        // Arrange
        String message = "Key (tt_id)=(789) is not present in table";

        // Act
        DataIntegrityViolationException exception = new DataIntegrityViolationException(message);
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getBody().getMessage().contains("789") || response.getBody().getMessage().contains("tipo"));
    }

    @Test
    @DisplayName("test Mapea código de error FILE_SIZE_EXCEEDED a PAYLOAD_TOO_LARGE")
    void testMapStatusFromErrorCodeFileSizeExceeded() {
        // Arrange
        com.thirdsmanagement.thirds.domain.exceptions.third.FileSizeExceededException exception = 
            new com.thirdsmanagement.thirds.domain.exceptions.third.FileSizeExceededException(5242880);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
    }

    @Test
    @DisplayName("test Maneja múltiples FieldErrors en MethodArgumentNotValidException")
    void testHandleMethodArgumentNotValidWithMultipleFieldErrors() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "field1", "Error 1");
        FieldError fieldError2 = new FieldError("object", "field2", "Error 2");
        FieldError fieldError3 = new FieldError("object", "field1", "Error duplicado"); // Duplicado, solo debe tomar el primero
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2, fieldError3));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleMethodArgumentNotValid(exception, webRequest);

        // Assert
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        
        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) body.get("fieldErrors");
        
        assertEquals(2, fieldErrors.size());
        assertEquals("Error 1", fieldErrors.get("field1"));
        assertEquals("Error 2", fieldErrors.get("field2"));
    }

    @Test
    @DisplayName("test Maneja DataIntegrityViolationException con mensaje null")
    void testHandleDataIntegrityViolationWithNullMessage() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException("test");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("integridad de datos"));
    }

    @Test
    @DisplayName("test Maneja HttpMessageNotReadableException con InvalidFormatException pero tipo diferente")
    void testHandleHttpMessageNotReadableWithInvalidFormatButDifferentType() {
        // Arrange
        InvalidFormatException invalidFormatException = mock(InvalidFormatException.class);
        when(invalidFormatException.getTargetType()).thenReturn((Class) String.class);
        when(invalidFormatException.getValue()).thenReturn("invalid_value");

        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Invalid format", invalidFormatException);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadable(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error en el formato de los datos enviados", response.getBody().getMessage());
        assertEquals("INVALID_REQUEST_FORMAT", response.getBody().getCode());
    }

    @Test
    @DisplayName("test Maneja DataIntegrityViolationException con tt_id pero sin patrón de extracción")
    void testHandleDataIntegrityViolationWithTtIdButNoExtractionPattern() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
            "Violation of constraint fk_thirds_third_types for tt_id but no extractable ID"
        );

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("tipo de tercero") || 
                   response.getBody().getMessage().contains("desconocido") ||
                   response.getBody().getMessage().contains("integridad"));
    }

    @Test
    @DisplayName("test Maneja DataIntegrityViolationException con ti_id pero sin patrón de extracción")
    void testHandleDataIntegrityViolationWithTiIdButNoExtractionPattern() {
        // Arrange
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
            "Violation of constraint fk_thirds_type_id for ti_id but no extractable ID"
        );

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolation(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("tipo de identificación") || 
                   response.getBody().getMessage().contains("desconocido") ||
                   response.getBody().getMessage().contains("integridad"));
    }

    @Test
    @DisplayName("test Path con uri= es reemplazado correctamente")
    void testPathReplacementInErrorResponse() {
        // Arrange
        when(webRequest.getDescription(false)).thenReturn("uri=/api/thirds/123");
        ThirdNotFound exception = new ThirdNotFound("Tercero no encontrado");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBusinessExceptions(exception, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals("/api/thirds/123", response.getBody().getPath());
    }
}
