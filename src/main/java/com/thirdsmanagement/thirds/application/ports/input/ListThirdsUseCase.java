package com.thirdsmanagement.thirds.application.ports.input;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thirdsmanagement.thirds.domain.model.Third;

public interface ListThirdsUseCase {
    Page<Third> getAllThirdsBy(String entId,Pageable pageable);
    Page<Third> getAllInactiveThirdsBy(String entId,Pageable pageable);
    Page<Third> getAllProvidersBy(String entId,Pageable pageable);
    Page<Third> getAllCustomersBy(String entId,Pageable pageable);
}
