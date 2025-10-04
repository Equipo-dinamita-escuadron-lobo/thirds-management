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
     * 
     * @param entId ID de la empresa
     * @param newState Nuevo estado (true para activo, false para inactivo)
     * @return Cantidad de terceros actualizados
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    @Override
    @Transactional
    public int changeAllThirdsState(String entId, Boolean newState) {
        // Validar parámetros de entrada
        validateParameters(entId, newState);
        
        log.info("Iniciando cambio de estado masivo para empresa: {} - Nuevo estado: {}", entId, newState);
        
        // Ejecutar cambio de estado masivo
        int updatedCount = thirdOutputPort.bulkUpdateThirdState(entId, newState);
        
        log.info("Cambio de estado masivo completado. Terceros actualizados: {}", updatedCount);
        
        return updatedCount;
    }

    /**
     * Valida los parámetros de entrada.
     * 
     * @param entId ID de la empresa
     * @param newState Nuevo estado
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    private void validateParameters(String entId, Boolean newState) {
        if (entId == null || entId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la empresa es obligatorio");
        }
        
        if (newState == null) {
            throw new IllegalArgumentException("El nuevo estado es obligatorio");
        }
    }
}
