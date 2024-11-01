package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.domain.model.Third;

public interface CreateThirdUseCase {
    Third createThird(Third third);
}
