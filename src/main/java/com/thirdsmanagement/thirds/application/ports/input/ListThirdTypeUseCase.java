package com.thirdsmanagement.thirds.application.ports.input;

import java.util.ArrayList;
import java.util.List;

import com.thirdsmanagement.thirds.domain.model.ThirdType;

public interface ListThirdTypeUseCase {

    List<ThirdType> getAllThirdTypes(String entId);
    
} 
