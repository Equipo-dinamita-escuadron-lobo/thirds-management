package com.thirdsmanagement.thirds.application.ports.output;

import java.util.ArrayList;
import java.util.List;

import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;

public interface IdOutputPort {
    ThirdType saveThirdType(ThirdType thirdType);
    List<ThirdType> getALLThirdTypes(String entId);
    TypeId saveTypeId(TypeId typeId);
    List<TypeId> getAllTypeIds(String entId);

}
