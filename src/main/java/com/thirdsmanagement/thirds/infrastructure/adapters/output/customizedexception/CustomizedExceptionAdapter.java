package com.thirdsmanagement.thirds.infrastructure.adapters.output.customizedexception;

import java.net.http.HttpHeaders;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.thirdsmanagement.commons.exceptions.thirds.ThirdNotFound;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.customizedexception.data.response.ExceptionResponse;

/**
 * Clase que maneja las excepciones personalizadas.
 */
@ControllerAdvice
@RestController
public class CustomizedExceptionAdapter  extends ResponseEntityExceptionHandler{
    /**
     * Maneja todas las excepciones.
     * @param ex Excepción.
     * @param request Solicitud web.
     * @return Respuesta de la excepción.
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExcpetions(Exception ex, WebRequest request){
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), Arrays.asList(request.getDescription(false)));
        
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja la excepción de tercero no encontrado.
     * @param ex Excepción.
     * @param request Solicitud web.
     * @return Respuesta de la excepción.
     */
    @ExceptionHandler(ThirdNotFound.class)
    public final ResponseEntity<Object> handleUserNotFoundException(ThirdNotFound ex, WebRequest request){
        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), Arrays.asList(request.getDescription(false)));

        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja la excepción de argumento no válido.
     * @param ex Excepción.
     * @param headers Cabeceras.
     * @param status Estado.
     * @param request Solicitud web.
     * @return Respuesta de la excepción.
     */
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request){
        List<String> errors = new ArrayList<String>();
        ex.getBindingResult().getAllErrors().stream().forEach(error -> {
            errors.add(error.getDefaultMessage());
        });

        ExceptionResponse exceptionResponse = new ExceptionResponse(LocalDateTime.now(), "Validation Failed", errors);
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
