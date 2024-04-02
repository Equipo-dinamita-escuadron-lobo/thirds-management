package com.thirdsmanagement.thirds.application.ports.input;

import com.thirdsmanagement.thirds.domain.model.Third;

public interface ChangeThirdStateUseCase {
    boolean changeThirdState(Third third);
}
