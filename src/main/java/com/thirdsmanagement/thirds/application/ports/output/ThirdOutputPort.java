package com.thirdsmanagement.thirds.application.ports.output;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thirdsmanagement.thirds.domain.model.Third;

public interface ThirdOutputPort {
    Third saveThird(Third third);
    Third updateThird(Third third);
    Optional<Third> getThirdById(Long id);
    boolean existThirdById(long id);
    void deleteThirdById(Long id);

    boolean changeThirdState(Long thId);

    Page<Third> getAllThirdsBy(String entId, Pageable page);
    Page<Third> getAllInactiveThirdsBy(String entId, Pageable page);
    Page<Third> getAllProvidersBy(String entId,Pageable page);
    Page<Third> getAllCustomersBy(String entId,Pageable page);
    
}
