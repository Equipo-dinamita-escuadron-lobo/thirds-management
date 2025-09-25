package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.DeleteTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdInUseException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdNotFound;
import com.thirdsmanagement.thirds.domain.model.TypeId;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para eliminar tipos de identificación.
 * Implementa las validaciones de negocio necesarias antes de la eliminación.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteTypeIdService implements DeleteTypeIdUseCase {

    private final IdOutputPort idOutputPort;

    /**
     * Elimina un tipo de identificación del sistema con validaciones completas.
     * 
     * @param typeIdId el ID del tipo de identificación a eliminar
     * @param entId el ID de la empresa
     * @return true si se eliminó correctamente
     * @throws TypeIdNotFound si el tipo de identificación no existe
     * @throws TypeIdInUseException si el tipo de identificación está siendo utilizado
     * @throws IllegalArgumentException si los parámetros son inválidos
     */
    @Override
    @Transactional
    public boolean deleteTypeId(Long typeIdId, String entId) {
        log.info("Iniciando eliminación de tipo de identificación con ID: {} para empresa: {}", typeIdId, entId);
        
        // Validar parámetros de entrada
        validateInputParameters(typeIdId, entId);
        
        // Verificar que el tipo de identificación existe
        TypeId typeId = validateTypeIdExists(typeIdId, entId);
        
        // Verificar que el tipo de identificación no esté siendo utilizado
        validateTypeIdNotInUse(typeIdId, entId, typeId.getTypeId());
        
        // Proceder con la eliminación
        boolean deleted = idOutputPort.deleteTypeId(typeIdId, entId);
        
        if (deleted) {
            log.info("Tipo de identificación '{}' con ID {} eliminado exitosamente para empresa: {}", 
                    typeId.getTypeId(), typeIdId, entId);
        } else {
            log.warn("No se pudo eliminar el tipo de identificación con ID: {} para empresa: {}", typeIdId, entId);
        }
        
        return deleted;
    }

    /**
     * Valida que los parámetros de entrada no sean null o inválidos.
     * 
     * @param typeIdId el ID del tipo de identificación
     * @param entId el ID de la empresa
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    private void validateInputParameters(Long typeIdId, String entId) {
        if (typeIdId == null) {
            throw new IllegalArgumentException("El ID del tipo de identificación no puede ser null");
        }
        
        if (entId == null || entId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la empresa no puede ser null o vacío");
        }
    }

    /**
     * Valida que el tipo de identificación existe en el sistema.
     * 
     * @param typeIdId el ID del tipo de identificación
     * @param entId el ID de la empresa
     * @return el tipo de identificación encontrado
     * @throws TypeIdNotFound si el tipo de identificación no existe
     */
    private TypeId validateTypeIdExists(Long typeIdId, String entId) {
        if (!idOutputPort.existsTypeIdById(typeIdId)) {
            throw new TypeIdNotFound("El tipo de identificación con ID " + typeIdId + " no existe");
        }
        
        TypeId typeId = idOutputPort.getTypeIdById(typeIdId);
        if (typeId == null || !entId.equals(typeId.getEntId())) {
            throw new TypeIdNotFound("El tipo de identificación con ID " + typeIdId + " no existe para la empresa " + entId);
        }
        
        return typeId;
    }

    /**
     * Valida que el tipo de identificación no esté siendo utilizado por terceros existentes.
     * 
     * @param typeIdId el ID del tipo de identificación
     * @param entId el ID de la empresa
     * @param typeIdName el nombre del tipo de identificación (para mensajes de error)
     * @throws TypeIdInUseException si el tipo de identificación está siendo utilizado
     */
    private void validateTypeIdNotInUse(Long typeIdId, String entId, String typeIdName) {
        boolean isInUse = idOutputPort.isTypeIdInUse(typeIdId, entId);
        
        if (isInUse) {
            log.warn("Intento de eliminar tipo de identificación '{}' (ID: {}) que está en uso para empresa: {}", 
                    typeIdName, typeIdId, entId);
            throw new TypeIdInUseException(typeIdName);
        }
        
        log.debug("Tipo de identificación '{}' (ID: {}) no está en uso, puede ser eliminado", typeIdName, typeIdId);
    }
}
