package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.domain.model.ThirdType;

public interface CreateThirdTypeUseCase {

    ThirdType createThirdType(ThirdType thirdType);
    
} 
