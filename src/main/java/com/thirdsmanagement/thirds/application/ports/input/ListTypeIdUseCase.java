package com.thirdsmanagement.thirds.application.ports.input;

import java.util.ArrayList;

import com.thirdsmanagement.thirds.domain.model.TypeId;

public interface ListTypeIdUseCase {

    ArrayList<TypeId> getAllTypeId(Long entId);
    
} 
