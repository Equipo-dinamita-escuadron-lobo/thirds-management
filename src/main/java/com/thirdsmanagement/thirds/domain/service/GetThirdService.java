package com.thirdsmanagement.thirds.domain.service;

import com.thirdsmanagement.thirds.application.ports.input.GetThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.exception.ThirdNotFound;
import com.thirdsmanagement.thirds.domain.model.Third;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetThirdService implements GetThirdUseCase{

    private final ThirdOutputPort thirdOutputPort;

    @Override
    public Third getThirdById(Long id) {
        return thirdOutputPort.getThirdById(id).orElseThrow(()-> new ThirdNotFound("Third not found with id " + id));
    }
}
