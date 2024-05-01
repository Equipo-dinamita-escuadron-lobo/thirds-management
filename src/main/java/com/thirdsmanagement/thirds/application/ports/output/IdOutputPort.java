package com.thirdsmanagement.thirds.application.ports.output;

import java.util.ArrayList;
import java.util.List;

import com.thirdsmanagement.thirds.domain.model.ThirdType;

public interface IdOutputPort {
    ThirdType saveThirdType(ThirdType thirdType);
    List<ThirdType> getALLThirdTypes(long entId);

}
