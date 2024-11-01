package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence;
import java.util.ArrayList;
import java.util.List;

import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.IdPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IdPersistenceAdapter implements IdOutputPort {
    
    @PersistenceContext
    private EntityManager entityManager;

    private final ThirdRepository thirdRepository;
    private final ThirdTypeRepository thirdTypeRepository;
    private final TypeIdRepository typeIdRepository;

    private final IdPersistenceMapper idPersistenceMapper;
    
    @Override
    public ThirdType saveThirdType(ThirdType thirdType) {
        ThirdTypeEntity thirdTypeEntity = idPersistenceMapper.toThirdType(thirdType);
        thirdTypeRepository.save(thirdTypeEntity);
        ThirdType result = idPersistenceMapper.toThirdTypeEntity(thirdTypeEntity);
        return result;

    }

    @Override
    public List<ThirdType> getALLThirdTypes(String entId) {
        return idPersistenceMapper.toThirdTypeEntitys(thirdTypeRepository.findAllByTtentId(entId));
    }

    @Override
    public TypeId saveTypeId(TypeId typeId) {
       TypeIdEntity typeIdEntity = idPersistenceMapper.toTypeId(typeId);
       typeIdRepository.save(typeIdEntity);
       TypeId result = idPersistenceMapper.toTypeIdEntity(typeIdEntity);
       return result;
    }

    @Override
    public List<TypeId> getAllTypeIds(String entId) {
        return idPersistenceMapper.toTypeIdEntititys(typeIdRepository.findAllByTientId(entId));
       
    }
    
}
