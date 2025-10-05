package com.thirdsmanagement.thirds.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thirdsmanagement.thirds.application.ports.input.BulkChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para cambiar el estado de múltiples terceros de forma masiva.
 * Implementa operaciones transaccionales para garantizar consistencia.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BulkChangeThirdStateService implements BulkChangeThirdStateUseCase {

    private final ThirdOutputPort thirdOutputPort;

    /**
     * Cambia el estado de todos los terceros de una empresa.
     * Operación transaccional que garantiza atomicidad.
     * Bean Validation en el controlador garantiza que los parámetros son válidos.
     * 
     * @param entId ID de la empresa
     * @param newState Nuevo estado (true para activo, false para inactivo)
     * @return Cantidad de terceros actualizados
     */
    @Override
    @Transactional
    public int changeAllThirdsState(String entId, Boolean newState) {
        return thirdOutputPort.bulkUpdateThirdState(entId, newState);
    }
}
