package com.thirdsmanagement.thirds.domain.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeForeignKeyViolationException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdForeignKeyViolationException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Proporciona respuestas consistentes y descriptivas para diferentes tipos de errores.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de negocio y resuelve el estado HTTP según el código de error.
     */
    @ExceptionHandler(BaseBusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessExceptions(
            BaseBusinessException ex, WebRequest request) {

        String errorCode = ex.getErrorCode().getCode();
        HttpStatus status = mapStatusFromErrorCode(errorCode);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .code(errorCode)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Maneja errores de validación de payload (Bean Validation en @RequestBody con @Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage(),
                        (msg1, msg2) -> msg1
                ));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Error de validación de campos")
                .code(ErrorCode.GENERIC_ERROR.getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(Map.of(
                "error", errorResponse,
                "fieldErrors", fieldErrors
        ), status);
    }

    /**
     * Maneja errores de validación a nivel de parámetros (e.g., @RequestParam, @PathVariable).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> violations = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (msg1, msg2) -> msg1
                ));

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Error de validación")
                .code(ErrorCode.GENERIC_ERROR.getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(Map.of(
                "error", errorResponse,
                "violations", violations
        ), status);
    }

    /**
     * Maneja excepciones de integridad de datos, especialmente violaciones de clave foránea.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, WebRequest request) {

        String message = ex.getMessage();
        String errorCode = ErrorCode.GENERIC_ERROR.getCode();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Detectar violaciones de clave foránea específicas
        if (message != null) {
            if (message.contains("fk_thirds_third_types") || message.contains("tt_id")) {
                // Extraer el ID del tipo de tercero del mensaje de error si es posible
                String thirdTypeId = extractIdFromConstraintMessage(message, "tt_id");
                if (thirdTypeId == null) {
                    thirdTypeId = "desconocido";
                }
                
                ThirdTypeForeignKeyViolationException customEx = 
                    new ThirdTypeForeignKeyViolationException(thirdTypeId, ex);
                return handleBusinessExceptions(customEx, request);
            }
            
            if (message.contains("fk_thirds_type_id") || message.contains("ti_id")) {
                // Extraer el ID del tipo de identificación del mensaje de error si es posible
                String typeId = extractIdFromConstraintMessage(message, "ti_id");
                if (typeId == null) {
                    typeId = "desconocido";
                }
                
                TypeIdForeignKeyViolationException customEx = 
                    new TypeIdForeignKeyViolationException(typeId, ex);
                return handleBusinessExceptions(customEx, request);
            }
        }

        // Para otras violaciones de integridad de datos
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Error de integridad de datos: " + (message != null ? message : "Violación de restricción"))
                .code(errorCode)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Extrae el ID de una restricción de clave foránea del mensaje de error.
     */
    private String extractIdFromConstraintMessage(String message, String columnName) {
        try {
            // Buscar patrones comunes en mensajes de error de PostgreSQL/MySQL
            String[] patterns = {
                "\\(" + columnName + "\\)=\\(([^)]+)\\)",
                "'" + columnName + "'='([^']+)'",
                columnName + "=([^\\s,)]+)"
            };
            
            for (String pattern : patterns) {
                java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
                java.util.regex.Matcher m = p.matcher(message);
                if (m.find()) {
                    return m.group(1);
                }
            }
        } catch (Exception e) {
            // Si no se puede extraer, devolver null
        }
        return null;
    }

    private HttpStatus mapStatusFromErrorCode(String code) {
        if (code == null) {
            return HttpStatus.BAD_REQUEST;
        }
        String upper = code.toUpperCase();
        if (upper.endsWith("_NOT_FOUND") || upper.contains("NOT_FOUND")) {
            return HttpStatus.NOT_FOUND;
        }
        if (upper.endsWith("_ALREADY_EXISTS") || upper.contains("DUPLICATE") || upper.contains("ASSOCIATED")) {
            return HttpStatus.CONFLICT;
        }
        if (upper.contains("PDF_RUT_INVALID_FORMAT") || upper.contains("INVALID_FORMAT")) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.BAD_REQUEST;
    }

    /**
     * Maneja excepciones generales no específicas.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ha ocurrido un error interno del servidor")
                .code(ErrorCode.GENERIC_ERROR.getCode())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}