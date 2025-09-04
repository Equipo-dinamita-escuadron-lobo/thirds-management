package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.DeleteTypeIdUseCase;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteTypeIdService implements DeleteTypeIdUseCase {

    private final TypeIdRepository typeIdRepository;

    /**
     * Elimina un tipo de identificación del sistema.
     * 
     * @param entId el ID del tipo de identificación a eliminar
     * @return ResponseEntity con el resultado de la operación
     * @throws IllegalArgumentException si el ID es null o inválido
     */
    @Override
    public ResponseEntity<String> deleteTypeId(Long entId) {
        if (entId == null || entId <= 0) {
            throw new IllegalArgumentException("El ID del tipo de identificación debe ser válido y mayor que 0");
        }
        
        try {
            // Verificar si existe antes de eliminar
            if (!typeIdRepository.existsById(entId.toString())) {
                return ResponseEntity.notFound().build();
            }
            
            typeIdRepository.deleteById(entId.toString());
            return ResponseEntity.ok("Tipo de identificación eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error interno al eliminar el tipo de identificación");
        }
    }
}
