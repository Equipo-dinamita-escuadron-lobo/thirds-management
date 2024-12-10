package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.DeleteThirdTypeUseCase;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.identifiers.ThirdsAndTypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdsAndTypesRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLOutput;

/**
 * Clase de servicio para eliminar un tipo de tercero.
 * Implementa la interfaz {@link DeleteThirdTypeUseCase}.
 * Utiliza {@link ThirdTypeRepository} y {@link ThirdsAndTypesRepository} para las operaciones de persistencia. 
 * Este servicio proporciona un método para eliminar un tercer tipo por su ID. 
 * Primero comprueba si el tipo de tercero está siendo utilizado por un tercero.
 * Si se está utilizando el tipo de tercero, devuelve una respuesta de error.
 * Si no se utiliza el tipo de tercero, elimina el tipo de tercero y devuelve una respuesta correcta. 
 */
@AllArgsConstructor
public class DeleteTypeThirdService implements DeleteThirdTypeUseCase {

    private final ThirdTypeRepository thirdTypeRepository;
    private final ThirdsAndTypesRepository thirdsAndTypesRepository;
    //lo que creo nuevo

    @Override
    @Transactional
    public ResponseEntity<String> deleteThirdTypeUseCase(Long entId) {

        //esto tambien lo hice

        // Verificar si el tipo de tercero está siendo usado
        boolean isUsed = thirdsAndTypesRepository.existsByThirdType_TtId(entId);


        if (isUsed) {

            return ResponseEntity.ok("error");
        } else {
            thirdTypeRepository.deleteById(entId);
            return ResponseEntity.ok("success");
        }
       
    }
}
