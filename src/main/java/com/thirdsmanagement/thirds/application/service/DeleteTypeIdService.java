package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.DeleteTypeIdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Servicio para eliminar un tipo de identificación.
 *
 * Esta clase proporciona la funcionalidad necesaria para eliminar un tipo de identificación
 * en el sistema de gestión de terceros.
 */
@Service
@AllArgsConstructor
public class DeleteTypeIdService implements DeleteTypeIdUseCase {

    private final IdOutputPort idOutputPort;
    private final TypeIdRepository typeIdRepository;

    @Override
    public ResponseEntity<String> deleteTypeId(Long entId) {
        try {
            // Implementar lógica de eliminación
            typeIdRepository.deleteById(entId.toString());
            return ResponseEntity.ok("Tipo de identificación eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al eliminar el tipo de identificación");
        }
    }
}
