package com.thirdsmanagement.thirds.infrastructure.adapters.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thirdsmanagement.thirds.domain.service.ChangeThirdStateService;
import com.thirdsmanagement.thirds.domain.service.CreateThirdService;
import com.thirdsmanagement.thirds.domain.service.CreateThirdTypeService;
import com.thirdsmanagement.thirds.domain.service.GetThirdService;
import com.thirdsmanagement.thirds.domain.service.ListThirdTypeService;
import com.thirdsmanagement.thirds.domain.service.ListThirdsService;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.eventpublisher.IdEventPublisherAdapter;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.eventpublisher.ThirdEventPublisherAdapter;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.IdPersistenceAdapter;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.ThirdPersistenceAdapter;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.IdPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.ThirdPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;

@Configuration
public class BeanConfiguration {
    
    @Bean
    public ThirdPersistenceAdapter thirdPersistenceAdapter(ThirdRepository thirdRepository,ThirdTypeRepository thirdTypeRepository, TypeIdRepository typeIdRepository,ThirdPersistenceMapper thirdPersistenceMapper){
        return new ThirdPersistenceAdapter(thirdRepository, thirdTypeRepository, typeIdRepository, thirdPersistenceMapper);
    }

    @Bean
    public IdPersistenceAdapter IdPersistenceAdapter(ThirdRepository thirdRepository,ThirdTypeRepository thirdTypeRepository, TypeIdRepository typeIdRepository,IdPersistenceMapper idPersistenceMapper){
        return new IdPersistenceAdapter(thirdRepository, thirdTypeRepository, typeIdRepository, idPersistenceMapper);
    }

    @Bean
    public ThirdEventPublisherAdapter thirdEventPublisherAdapter(ApplicationEventPublisher applicationEventPublisher){
        return new ThirdEventPublisherAdapter(applicationEventPublisher);
    }

    @Bean
    public IdEventPublisherAdapter idEventPublisherAdapter(ApplicationEventPublisher applicationEventPublisher){
        return new IdEventPublisherAdapter(applicationEventPublisher);
    }

    @Bean
    public CreateThirdTypeService createThirdTypeService(IdPersistenceAdapter idPersistenceAdapter, IdEventPublisherAdapter idEventPublisherAdapter){
        return new CreateThirdTypeService(idPersistenceAdapter, idEventPublisherAdapter);
    }

    @Bean 
    public ListThirdTypeService listThirdsTypeService(IdPersistenceAdapter idPersistenceAdapter){
        return new ListThirdTypeService(idPersistenceAdapter);

    }
  

    @Bean
    public CreateThirdService createThirdService(ThirdPersistenceAdapter thirdPersistenceAdapter, ThirdEventPublisherAdapter thirdEventPublisherAdapter) {
        return new CreateThirdService(thirdPersistenceAdapter, thirdEventPublisherAdapter);
    }

    @Bean
    public ChangeThirdStateService changeThirdStateService(ThirdPersistenceAdapter thirdPersistenceAdapter, ThirdEventPublisherAdapter thirdEventPublisherAdapter){
        return new ChangeThirdStateService(thirdPersistenceAdapter, thirdEventPublisherAdapter);
    }

    @Bean
    public ListThirdsService listThirdsService(ThirdPersistenceAdapter thirdPersistenceAdapter) {
        return new ListThirdsService(thirdPersistenceAdapter);
    }

    @Bean
    public GetThirdService getThirdService(ThirdPersistenceAdapter thirdPersistenceAdapter){
        return new GetThirdService(thirdPersistenceAdapter);
    }
}
