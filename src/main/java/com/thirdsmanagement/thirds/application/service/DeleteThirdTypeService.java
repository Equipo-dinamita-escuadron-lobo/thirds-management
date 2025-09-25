package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.DeleteThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeInUseException;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeNotFound;
import com.thirdsmanagement.thirds.domain.model.ThirdType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para eliminar tipos de tercero.
 * Implementa las validaciones de negocio necesarias antes de la eliminación.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteThirdTypeService implements DeleteThirdTypeUseCase {

    private final IdOutputPort idOutputPort;

    /**
     * Elimina un tipo de tercero del sistema con validaciones completas.
     * 
     * @param thirdTypeId el ID del tipo de tercero a eliminar
     * @param entId el ID de la empresa
     * @return true si se eliminó correctamente
     * @throws ThirdTypeNotFound si el tipo de tercero no existe
     * @throws ThirdTypeInUseException si el tipo de tercero está siendo utilizado
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    @Override
    @Transactional
    public boolean deleteThirdType(Long thirdTypeId, String entId) {
        
        // Validar parámetros de entrada
        validateInputParameters(thirdTypeId, entId);
        
        // Verificar que el tipo de tercero existe
        ThirdType thirdType = validateThirdTypeExists(thirdTypeId, entId);
        
        // Verificar que el tipo de tercero no esté siendo utilizado
        validateThirdTypeNotInUse(thirdTypeId, entId, thirdType.getThirdTypeName());
        
        // Proceder con la eliminación
        boolean deleted = idOutputPort.deleteThirdType(thirdTypeId, entId);
        
              
        return deleted;
    }

    /**
     * Valida que los parámetros de entrada no sean null o inválidos.
     * 
     * @param thirdTypeId el ID del tipo de tercero
     * @param entId el ID de la empresa
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    private void validateInputParameters(Long thirdTypeId, String entId) {
        if (thirdTypeId == null) {
            throw new IllegalArgumentException("El ID del tipo de tercero no puede ser null");
        }
        
        if (entId == null || entId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la empresa no puede ser null o vacío");
        }
    }

    /**
     * Valida que el tipo de tercero existe en el sistema.
     * 
     * @param thirdTypeId el ID del tipo de tercero
     * @param entId el ID de la empresa
     * @return el tipo de tercero encontrado
     * @throws ThirdTypeNotFound si el tipo de tercero no existe
     */
    private ThirdType validateThirdTypeExists(Long thirdTypeId, String entId) {
        if (!idOutputPort.existsThirdTypeById(thirdTypeId)) {
            throw new ThirdTypeNotFound("El tipo de tercero con ID " + thirdTypeId + " no existe");
        }
        
        ThirdType thirdType = idOutputPort.getThirdTypeById(thirdTypeId);
        if (thirdType == null || !entId.equals(thirdType.getEntId())) {
            throw new ThirdTypeNotFound("El tipo de tercero con ID " + thirdTypeId + " no existe para la empresa " + entId);
        }
        
        return thirdType;
    }

    /**
     * Valida que el tipo de tercero no esté siendo utilizado por terceros existentes.
     * 
     * @param thirdTypeId el ID del tipo de tercero
     * @param entId el ID de la empresa
     * @param thirdTypeName el nombre del tipo de tercero (para mensajes de error)
     * @throws ThirdTypeInUseException si el tipo de tercero está siendo utilizado
     */
    private void validateThirdTypeNotInUse(Long thirdTypeId, String entId, String thirdTypeName) {
        boolean isInUse = idOutputPort.isThirdTypeInUse(thirdTypeId, entId);
        
        if (isInUse) {
            throw new ThirdTypeInUseException(thirdTypeName);
        }
    }
}
