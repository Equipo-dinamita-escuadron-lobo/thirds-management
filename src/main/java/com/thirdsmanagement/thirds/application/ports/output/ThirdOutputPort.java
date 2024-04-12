package com.thirdsmanagement.thirds.application.ports.output;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thirdsmanagement.thirds.domain.model.Third;

public interface ThirdOutputPort {
    Third saveThird(Third third);

    Optional<Third> getThirdById(Long id);
    Optional<Third> getThirdByName(String name);
    Optional<Third> getThirdByNIT(Long NIT);

    boolean changeThirdState(Third third);

    Page<Third> getAllThirdsBy(Long entId, Pageable page);
    Page<Third> getAllInactiveThirdsBy(Long entId, Pageable page);
    Page<Third> getAllProvidersBy(Long entId,Pageable page);
    Page<Third> getAllCustomersBy(Long entId,Pageable page);
}
