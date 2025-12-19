package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.messageBroker;

import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.thirdsmanagement.thirds.application.ports.output.IThirdEventPublisher;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.messageBroker.dto.EventDto;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.messageBroker.dto.ThirdUpdatedEventDto;
import com.thirdsmanagement.thirds.infrastructure.config.rabbitConfig.RabbitThirdsEventsConfig;
import com.thirdsmanagement.thirds.infrastructure.security.IJwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ThirdEventPublisher implements IThirdEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final IJwtUtils jwtUtils;

    @Override
    public void publishThirdUpdatedEvent(Third third) {
        ThirdUpdatedEventDto eventDto = ThirdUpdatedEventDto.builder()
                .thirdId(third.getThId())
                .email(third.getEmail())
                .state(third.getState())
                .entId(third.getEntId())
                .build();


        //Logica para ver si se almacena el nombre o razon social
        if (third.getPersonType().isNatural()) {
            eventDto.setFullName(third.getNames() + " " + third.getLastNames());
        } else {
            eventDto.setFullName(third.getSocialReason());
        }
        
        EventDto<ThirdUpdatedEventDto, String> event = new EventDto<>(eventDto, "THIRD_UPDATED");

        log.info("Publishing third updated event: {}", eventDto.getThirdId());

        rabbitTemplate.convertAndSend(RabbitThirdsEventsConfig.THIRD_UPDATED_EXCHANGE, "", event, message -> {
            message.getMessageProperties().setHeaders(Map.of(
                    "x-jwt-token", jwtUtils.getToken()
            ));
            return message;
        });
    }

    
}
