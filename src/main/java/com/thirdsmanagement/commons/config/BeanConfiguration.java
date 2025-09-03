package com.thirdsmanagement.commons.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thirdsmanagement.thirds.application.service.ChangeThirdStateService;
import com.thirdsmanagement.thirds.application.service.CreateThirdService;
import com.thirdsmanagement.thirds.application.service.CreateThirdTypeService;
import com.thirdsmanagement.thirds.application.service.CreateTypeIdService;
import com.thirdsmanagement.thirds.application.service.DeleteTypeThirdService;
import com.thirdsmanagement.thirds.application.service.GetThirdService;
import com.thirdsmanagement.thirds.application.service.ListThirdTypeService;
import com.thirdsmanagement.thirds.application.service.ListThirdsService;
import com.thirdsmanagement.thirds.application.service.ListTypeIdService;
import com.thirdsmanagement.thirds.application.service.UpdateThirdService;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.eventpublisher.IdEventPublisherAdapter;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.eventpublisher.ThirdEventPublisherAdapter;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.IdPersistenceAdapter;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.ThirdPersistenceAdapter;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.IdPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.ThirdPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdsAndTypesRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;

/**
 * Clase de configuración de beans.
 */
@Configuration
public class BeanConfiguration {
    /**
     * Crea un adaptador de persistencia de tercero.
     * @param thirdRepository Repositorio de tercero.
     * @param thirdTypeRepository Repositorio de tipo de tercero.
     * @param typeIdRepository Repositorio de tipo de identificación.
     * @param thirdPersistenceMapper Mapeador de persistencia de tercero.
     * @return Adaptador de persistencia de tercero.
     */
    @Bean
    public ThirdPersistenceAdapter thirdPersistenceAdapter(ThirdRepository thirdRepository,ThirdTypeRepository thirdTypeRepository, TypeIdRepository typeIdRepository,ThirdPersistenceMapper thirdPersistenceMapper){
        return new ThirdPersistenceAdapter(thirdRepository, thirdTypeRepository, typeIdRepository, thirdPersistenceMapper);
    }

    /**
     * Crea un adaptador de persistencia de identificación.
     * @param thirdRepository Repositorio de tercero.
     * @param thirdTypeRepository Repositorio de tipo de tercero.
     * @param typeIdRepository Repositorio de tipo de identificación.
     * @param idPersistenceMapper Mapeador de persistencia de identificación.
     * @return Adaptador de persistencia de identificación.
     */
    @Bean
    public IdPersistenceAdapter IdPersistenceAdapter(ThirdRepository thirdRepository,ThirdTypeRepository thirdTypeRepository, TypeIdRepository typeIdRepository,IdPersistenceMapper idPersistenceMapper){
        return new IdPersistenceAdapter(thirdRepository, thirdTypeRepository, typeIdRepository, idPersistenceMapper);
    }

    /**
     * Crea un adaptador de publicador de eventos de tercero.
     * @param applicationEventPublisher Publicador de eventos de aplicación.
     * @return Adaptador de publicador de eventos de tercero.
     */
    @Bean
    public ThirdEventPublisherAdapter thirdEventPublisherAdapter(ApplicationEventPublisher applicationEventPublisher){
        return new ThirdEventPublisherAdapter(applicationEventPublisher);
    }

    /**
     * Crea un adaptador de publicador de eventos de identificación.
     * @param applicationEventPublisher Publicador de eventos de aplicación.
     * @return Adaptador de publicador de eventos de identificación.
     */
    @Bean
    public IdEventPublisherAdapter idEventPublisherAdapter(ApplicationEventPublisher applicationEventPublisher){
        return new IdEventPublisherAdapter(applicationEventPublisher);
    }

    /**
     * Crea un servicio de creación de tipo de tercero.
     * @param idPersistenceAdapter Adaptador de persistencia de identificación.
     * @return Servicio de creación de tipo de tercero.
     */
    @Bean
    public CreateTypeIdService createTypeIdService(IdPersistenceAdapter idPersistenceAdapter){
        return new  CreateTypeIdService(idPersistenceAdapter);
    }

    /**
     * Crea un servicio de listado de tipo de tercero.
     * @param idPersistenceAdapter Adaptador de persistencia de identificación.
     * @return Servicio de listado de tipo de tercero.
     */
    @Bean 
    public ListTypeIdService listTypeIdService(IdPersistenceAdapter idPersistenceAdapter){
        return new ListTypeIdService(idPersistenceAdapter);

    }
    
    /**
     * Crea un servicio de creación de tercero.
     * @param idPersistenceAdapter Adaptador de persistencia de identificación.
     * @return Servicio de creación de tercero.
     */
    @Bean
    public CreateThirdTypeService createThirdTypeService(IdPersistenceAdapter idPersistenceAdapter){
        return new CreateThirdTypeService(idPersistenceAdapter);
    }

    /**
     * Crea un servicio de listado de tipo de tercero.
     * @param idPersistenceAdapter Adaptador de persistencia de identificación.
     * @return Servicio de listado de tipo de tercero.
     */
    @Bean 
    public ListThirdTypeService listThirdsTypeService(IdPersistenceAdapter idPersistenceAdapter){
        return new ListThirdTypeService(idPersistenceAdapter);

    }
    
    /**
     * Crea un servicio de creación de tercero.
     * @param thirdPersistenceAdapter Adaptador de persistencia de tercero.
     * @param thirdEventPublisherAdapter Adaptador de publicador de eventos de tercero.
     * @return Servicio de creación de tercero.
     */
    @Bean
    public CreateThirdService createThirdService(ThirdPersistenceAdapter thirdPersistenceAdapter, ThirdEventPublisherAdapter thirdEventPublisherAdapter) {
        return new CreateThirdService(thirdPersistenceAdapter, thirdEventPublisherAdapter);
    }

    /**
     * Crea un servicio de actualización de tercero.
     * @param thirdPersistenceAdapter Adaptador de persistencia de tercero.
     * @param thirdEventPublisherAdapter Adaptador de publicador de eventos de tercero.
     * @return Servicio de actualización de tercero.
     */
    @Bean
    public UpdateThirdService updateThirdService(ThirdPersistenceAdapter thirdPersistenceAdapter, ThirdEventPublisherAdapter thirdEventPublisherAdapter){
        return new UpdateThirdService(thirdPersistenceAdapter,thirdEventPublisherAdapter);
    }

    /**
     * Crea un servicio de cambio de estado de tercero.
     * @param thirdPersistenceAdapter Adaptador de persistencia de tercero.
     * @param thirdEventPublisherAdapter Adaptador de publicador de eventos de tercero.
     * @return Servicio de cambio de estado de tercero.
     */
    @Bean
    public ChangeThirdStateService changeThirdStateService(ThirdPersistenceAdapter thirdPersistenceAdapter, ThirdEventPublisherAdapter thirdEventPublisherAdapter){
        return new ChangeThirdStateService(thirdPersistenceAdapter, thirdEventPublisherAdapter);
    }

    /**
     * Crea un servicio de listado de tercero.
     * @param thirdPersistenceAdapter Adaptador de persistencia de tercero.
     * @return Servicio de listado de tercero.
     */
    @Bean
    public ListThirdsService listThirdsService(ThirdPersistenceAdapter thirdPersistenceAdapter) {
        return new ListThirdsService(thirdPersistenceAdapter);
    }

    /**
     * Crea un servicio de eliminación de tipo de tercero.
     * @param thirdTypeRepository Repositorio de tipo de tercero.
     * @param thirdsAndTypesRepository Repositorio de tercero y tipo.
     * @return Servicio de eliminación de tipo de tercero.
     */
    @Bean
    public GetThirdService getThirdService(ThirdPersistenceAdapter thirdPersistenceAdapter){
        return new GetThirdService(thirdPersistenceAdapter);
    }

    /**
     * Crea un servicio de eliminación de tipo de tercero.
     * @param thirdTypeRepository Repositorio de tipo de tercero.
     * @param thirdsAndTypesRepository Repositorio de tercero y tipo.
     * @return Servicio de eliminación de tipo de tercero.
     */
    @Bean
    public DeleteTypeThirdService deleteTypeThirdService(ThirdTypeRepository thirdTypeRepository, ThirdsAndTypesRepository thirdsAndTypesRepository ) {
        return new DeleteTypeThirdService(thirdTypeRepository, thirdsAndTypesRepository);
    }
}
