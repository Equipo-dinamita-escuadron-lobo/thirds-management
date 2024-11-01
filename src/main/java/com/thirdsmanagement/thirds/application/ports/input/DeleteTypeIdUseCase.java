package com.thirdsmanagement.thirds.application.ports.input;



import org.springframework.http.ResponseEntity;

public interface DeleteTypeIdUseCase {
    ResponseEntity<String> deleteTypeId(Long entId);
}