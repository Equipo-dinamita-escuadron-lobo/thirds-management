package com.thirdsmanagement.thirds.application.ports.input;

import java.util.List;

import com.thirdsmanagement.thirds.domain.model.TypeId;

public interface ListTypeIdUseCase {

    List<TypeId> getAllTypeId(String entId);
    
} 
