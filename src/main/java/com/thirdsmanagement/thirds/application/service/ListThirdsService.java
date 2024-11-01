package com.thirdsmanagement.thirds.application.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thirdsmanagement.thirds.application.ports.input.ListThirdsUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.exception.ThirdsNotFound;
import com.thirdsmanagement.thirds.domain.model.Third;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ListThirdsService implements ListThirdsUseCase{

    private final ThirdOutputPort thirdOutputPort;

    @Override
    public Page<Third> getAllThirdsBy(String entId, Pageable pageable) {

        Page<Third> result = thirdOutputPort.getAllThirdsBy(entId, pageable);

        if(result.isEmpty()){
            throw new ThirdsNotFound("No thirds found for enterprise id "+ entId);            
        }

        return result;
    }

    @Override
    public Page<Third> getAllInactiveThirdsBy(String entId, Pageable pageable) {
        Page<Third> result = thirdOutputPort.getAllInactiveThirdsBy(entId, pageable);

        if(result.isEmpty()){
            throw new ThirdsNotFound("No inactive thirds found for enterprise id "+ entId);            
        }

        return result;
    }

    @Override
    public Page<Third> getAllProvidersBy(String entId, Pageable pageable) {
        Page<Third> result = thirdOutputPort.getAllProvidersBy(entId, pageable);

        if(result.isEmpty()){
            throw new ThirdsNotFound("No providers found for enterprise id "+ entId);            
        }

        return result;
    }

    @Override
    public Page<Third> getAllCustomersBy(String entId, Pageable pageable) {
        Page<Third> result = thirdOutputPort.getAllCustomersBy(entId, pageable);

        if(result.isEmpty()){
            throw new ThirdsNotFound("No customers found for enterprise id "+ entId);            
        }

        return result;
    }
    
}
