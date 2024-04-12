package com.thirdsmanagement.thirds.application.ports.input;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thirdsmanagement.thirds.domain.model.Third;

public interface ListThirdsUseCase {
    Page<Third> getAllThirdsBy(Long entId,Pageable pageable);
    Page<Third> getAllInactiveThirdsBy(Long entId,Pageable pageable);
    Page<Third> getAllProvidersBy(Long entId,Pageable pageable);
    Page<Third> getAllCustomersBy(Long entId,Pageable pageable);
}
