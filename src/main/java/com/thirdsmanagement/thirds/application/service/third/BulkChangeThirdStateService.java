package com.thirdsmanagement.thirds.application.service.third;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdsmanagement.thirds.application.ports.input.BulkChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @brief Servicio para cambiar el estado de múltiples terceros de forma masiva
 *
 * Implementa operaciones transaccionales para garantizar consistencia.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BulkChangeThirdStateService implements BulkChangeThirdStateUseCase {

    private final ThirdOutputPort thirdOutputPort;

    /**
     * @brief Cambia el estado de todos los terceros de una empresa
     * @param entId identificador de la empresa
     * @param newState nuevo estado (true para activo, false para inactivo)
     * @return cantidad de terceros actualizados
     */
    @Override
    @Transactional
    public int changeAllThirdsState(String entId, Boolean newState) {
        return thirdOutputPort.bulkUpdateThirdState(entId, newState);
    }
}
