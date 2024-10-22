package com.thirdsmanagement.thirds.application.ports.input;

import org.springframework.http.ResponseEntity;

public interface DeleteThirdTypeUseCase {
    ResponseEntity<String> deleteThirdTypeUseCase(Long entId);
}
