package com.thirdsmanagement.thirds.application.service.third;

import org.springframework.stereotype.Service;

import com.thirdsmanagement.thirds.application.ports.input.IThirdUsagePort;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;

import lombok.RequiredArgsConstructor;

/**
 * @brief Servicio para manejo del contador de uso de terceros
 * 
 * Implementa el caso de uso de incremento de contador de uso.
 * Mantiene la separación arquitectónica entre adaptadores de entrada (listeners)
 * y adaptadores de salida (persistencia).
 */
@Service
@RequiredArgsConstructor
public class ThirdUsageService implements IThirdUsagePort {

    private final ThirdOutputPort thirdOutputPort;

    @Override
    public void incrementUsageCount(Long thirdId) {
        thirdOutputPort.incrementUsageCount(thirdId);
    }
}
