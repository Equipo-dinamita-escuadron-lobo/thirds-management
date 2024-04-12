package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.eThirdType;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.ThirdPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ThirdPersistenceAdapter implements ThirdOutputPort{

    @PersistenceContext
    private EntityManager entityManager;

    private final ThirdRepository thirdRepository;
    private final ThirdTypeRepository thirdTypeRepository;
    private final TypeIdRepository typeIdRepository;

    private final ThirdPersistenceMapper thirdPersistenceMapper;

    @Override
    public Third saveThird(Third third) {

        ThirdEntity thirdEntity = thirdPersistenceMapper.toThirdEntity(third);

        TypeIdEntity typeIdEntity = typeIdRepository.getReferenceById(third.getTypeId().name());

        List<ThirdTypeEntity> thirdTypeEntities = thirdTypeRepository.findAll();

        if(typeIdEntity != null){
            for(eThirdType tt : third.getThirdTypes()){
                for(ThirdTypeEntity tte : thirdTypeEntities){
                    if(tte.getTtName().equals(tt.name())){
                        thirdEntity.getThirdTypes().add(tte);
                    }
                }
            }
            thirdEntity = thirdRepository.save(thirdEntity);
        }

        Third result = thirdPersistenceMapper.toThird(thirdEntity);

        return result;
    }

    @Override
    public Optional<Third> getThirdById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getThirdById'");
    }

    @Override
    public Optional<Third> getThirdByName(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getThirdByName'");
    }

    @Override
    public Optional<Third> getThirdByNIT(Long NIT) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getThirdByNIT'");
    }

    @Override
    public boolean changeThirdState(Third third) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changeThirdState'");
    }

    @Override
    public Page<Third> getAllThirdsBy(Long entId, Pageable page) {
        System.out.println("\n Entrando a getAllThirdsBy \n");
        Page<ThirdEntity> pageEntities = thirdRepository.getThirdsBy(entId, page);
        Page<Third> pageThirds = pageEntities.map(this::convertToThird);

        return pageThirds;
    }

    @Override
    public Page<Third> getAllInactiveThirdsBy(Long entId, Pageable page) {
        System.out.println("\n Entrando a getAllInactiveThirdsBy \n");
        Page<ThirdEntity> pageEntities = thirdRepository.getInactiveThirdsBy(entId, page);
        Page<Third> pageThirds = pageEntities.map(this::convertToThird);

        return pageThirds;
    }

    @Override
    public Page<Third> getAllProvidersBy(Long entId, Pageable page) {
        System.out.println("\n Entrando a getAllProvidersBy \n");
        Page<ThirdEntity> pageEntities = thirdRepository.getProvidersBy(entId, page);
        Page<Third> pageThirds = pageEntities.map(this::convertToThird);

        return pageThirds;
    }

    @Override
    public Page<Third> getAllCustomersBy(Long entId, Pageable page) {
        System.out.println("\n Entrando a getAllCustomersBy \n");
        Page<ThirdEntity> pageEntities = thirdRepository.getCustomersBy(entId, page);
        Page<Third> pageThirds = pageEntities.map(this::convertToThird);

        return pageThirds;
    }

    private Third convertToThird(ThirdEntity thirdEntity) {

        System.out.println("\n Entrando a convertToThird \n");

        Third obj = this.thirdPersistenceMapper.toThird(thirdEntity);

        for(ThirdTypeEntity tt : thirdEntity.getThirdTypes()){
            obj.getThirdTypes().add(eThirdType.valueOf(tt.getTtName()));
        }

        return obj;
    }
}
