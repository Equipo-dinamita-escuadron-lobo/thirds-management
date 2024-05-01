package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.domain.model.TypeId;

public interface CreateTypeIdUseCase {
    TypeId createTypeId(TypeId typeId);
} 
