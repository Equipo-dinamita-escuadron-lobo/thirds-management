package com.thirdsmanagement.thirds.application.service.third;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdsmanagement.thirds.application.ports.input.IThirdUsagePort;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.model.Third;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @brief Servicio para manejo del contador de uso de terceros
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ThirdUsageService implements IThirdUsagePort {

    private final ThirdOutputPort thirdOutputPort;

    @Transactional
    @Override
    public void incrementUsageCount(Long thirdId) {
        log.debug("Incrementando contador de uso para tercero con ID: {}", thirdId);

        Third third = thirdOutputPort.findById(thirdId);
        if (third != null) {
            third.incrementUsageCount();
            thirdOutputPort.updateThird(third);
            log.debug("Contador de uso incrementado para tercero con ID: {}", thirdId);
        } else {
            log.warn("Tercero con ID {} no encontrado, no se puede incrementar el contador de uso", thirdId);
        }
    }
}
