package com.thirdsmanagement.thirds.domain.service;
import java.util.ArrayList;
import java.util.List;

import com.thirdsmanagement.thirds.application.ports.input.ListThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.ThirdType;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ListThirdTypeService implements ListThirdTypeUseCase {
    
    private final IdOutputPort idOutputPort;
    
    @Override
    public List<ThirdType> getAllThirdTypes(Long entId) {
       
        List<ThirdType> result = idOutputPort.getALLThirdTypes(entId);
        return result;
    }
    
}
