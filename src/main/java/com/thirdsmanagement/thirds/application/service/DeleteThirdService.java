package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.DeleteThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdNotFound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para eliminar terceros.
 * Implementa las validaciones de negocio necesarias antes de la eliminación.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteThirdService implements DeleteThirdUseCase {

    private final ThirdOutputPort thirdOutputPort;

    /**
     * Elimina un tercero del sistema con validaciones completas.
     * 
     * @param thirdId el ID del tercero a eliminar
     * @param entId   el ID de la empresa
     * @return true si se eliminó correctamente
     * @throws ThirdNotFound si el tercero no existe
     */
    @Override
    @Transactional
    public boolean deleteThird(Long thirdId, String entId) {
        // Verificar que el tercero existe (validación de negocio)
        validateThirdExists(thirdId, entId);

        // Proceder con la eliminación directa (el tercero es la entidad raíz)
        return thirdOutputPort.deleteThird(thirdId, entId);
    }

    /**
     * Valida que el tercero existe en el sistema (validación de negocio).
     * 
     * @param thirdId el ID del tercero
     * @param entId   el ID de la empresa
     * @throws ThirdNotFound si el tercero no existe
     */
    private void validateThirdExists(Long thirdId, String entId) {
        if (!thirdOutputPort.existThirdById(thirdId, entId)) {
            throw new ThirdNotFound("El tercero con ID " + thirdId + " no existe para la empresa " + entId);
        }
    }

}
