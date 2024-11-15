package com.thirdsmanagement.thirds.application.ports.input;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

import com.thirdsmanagement.thirds.domain.model.Third;

public interface ListThirdsUseCase {
    Page<Third> getAllThirdsBy(String entId,Pageable pageable);
    Page<Third> getAllInactiveThirdsBy(String entId,Pageable pageable);
    Page<Third> getAllProvidersBy(String entId,Pageable pageable);
    Page<Third> getAllCustomersBy(String entId,Pageable pageable);
    List<Third> getAllThirds(String entId);
}
