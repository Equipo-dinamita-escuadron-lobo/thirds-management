package com.thirdsmanagement.thirds.infrastructure.adapters.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thirdsmanagement.thirds.domain.service.CreateThirdService;
import com.thirdsmanagement.thirds.domain.service.ListThirdsService;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.eventpublisher.ThirdEventPublisherAdapter;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.ThirdPersistenceAdapter;
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
    public ThirdEventPublisherAdapter thirdEventPublisherAdapter(ApplicationEventPublisher applicationEventPublisher){
        return new ThirdEventPublisherAdapter(applicationEventPublisher);
    }

    @Bean
    public CreateThirdService createThirdService(ThirdPersistenceAdapter thirdPersistenceAdapter, ThirdEventPublisherAdapter thirdEventPublisherAdapter) {
        return new CreateThirdService(thirdPersistenceAdapter, thirdEventPublisherAdapter);
    }

    @Bean
    public ListThirdsService listThirdsService(ThirdPersistenceAdapter thirdPersistenceAdapter) {
        return new ListThirdsService(thirdPersistenceAdapter);
    }

    //Crear los otros Bean con los otros servicios
}
