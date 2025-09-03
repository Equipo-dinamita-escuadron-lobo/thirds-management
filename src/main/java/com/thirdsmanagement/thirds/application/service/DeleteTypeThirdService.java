package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.DeleteThirdTypeUseCase;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdsAndTypesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteTypeThirdService implements DeleteThirdTypeUseCase {

    private final ThirdTypeRepository thirdTypeRepository;
    private final ThirdsAndTypesRepository thirdsAndTypesRepository;

    /**
     * Elimina un tipo de tercero del sistema.
     * 
     * Verifica primero si el tipo de tercero está siendo utilizado por algún tercero
     * antes de proceder con la eliminación para mantener la integridad referencial.
     * 
     * @param entId el ID del tipo de tercero a eliminar
     * @return ResponseEntity con el resultado de la operación
     * @throws IllegalArgumentException si el ID es null o inválido
     */
    @Override
    @Transactional
    public ResponseEntity<String> deleteThirdTypeUseCase(Long entId) {
        if (entId == null || entId <= 0) {
            throw new IllegalArgumentException("El ID del tipo de tercero debe ser válido y mayor que 0");
        }

        // Verificar si el tipo de tercero está siendo utilizado
        boolean isUsed = thirdsAndTypesRepository.existsByThirdType_TtId(entId);

        if (isUsed) {
            return ResponseEntity.badRequest()
                    .body("No se puede eliminar el tipo de tercero porque está siendo utilizado");
        }
        
        try {
            thirdTypeRepository.deleteById(entId);
            return ResponseEntity.ok("Tipo de tercero eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error interno al eliminar el tipo de tercero");
        }
    }
}
