package com.thirdsmanagement.thirds.application.service.thirdType;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.thirdsmanagement.thirds.application.ports.input.ListThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.ThirdType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListThirdTypeService implements ListThirdTypeUseCase {

    private final IdOutputPort idOutputPort;

    @Override
    public List<ThirdType> getAllThirdTypes(String entId) {
        return idOutputPort.getALLThirdTypes(entId);
    }

  
    @Override
    public Page<ThirdType> getAllThirdTypesWithSort(String entId, int page, int size, String sortField, String sortOrder) {
        return idOutputPort.getAllThirdTypesWithSort(entId, page, size, sortField, sortOrder);
    }
  
    @Override
    public Page<ThirdType> findThirdTypesByEntIdAndSearch(String entId, String search, int page, int size, String sortField, String sortOrder) {
        return idOutputPort.findThirdTypesByEntIdAndSearch(entId, search, page, size, sortField, sortOrder);
    }
   
    @Override
    public long countThirdTypesByEntId(String entId) {
        return idOutputPort.countThirdTypesByEntId(entId);
    }
   
    @Override
    public long countThirdTypesByEntIdAndSearch(String entId, String search) {
        return idOutputPort.countThirdTypesByEntIdAndSearch(entId, search);
    }
   
    @Override
    public long countActiveThirdTypesByEntId(String entId) {
        return idOutputPort.countActiveThirdTypesByEntId(entId);
    }
   
    @Override
    public Page<ThirdType> getAllActiveThirdTypes(String entId, int page, int size) {
        return idOutputPort.getAllActiveThirdTypes(entId, page, size);
    }

}
