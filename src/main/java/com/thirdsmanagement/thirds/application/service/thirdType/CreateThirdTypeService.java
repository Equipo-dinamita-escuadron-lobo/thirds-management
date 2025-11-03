package com.thirdsmanagement.thirds.application.service.thirdType;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.ThirdTypeEventPublisher;
import com.thirdsmanagement.thirds.domain.event.ThirdTypeCreatedEvent;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeNameAlreadyExistsException;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.domain.utils.StringNormalizer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateThirdTypeService implements CreateThirdTypeUseCase {

    private final IdOutputPort idOutputPort;
    private final ThirdTypeRepository thirdTypeRepository;
    private final ThirdTypeEventPublisher thirdTypeEventPublisher;

    /**
     * Crea un nuevo tipo de tercero en el sistema.
     * 
     * @param thirdType el tipo de tercero a crear
     * @return el tipo de tercero creado con su ID asignado
     * @throws ThirdTypeNameAlreadyExistsException si ya existe un tipo de tercero con el mismo nombre
     */
    @Override
    @Transactional
    public ThirdType createThirdType(ThirdType thirdType) {
        // Normalizar el nombre del tipo de tercero para almacenamiento
        ThirdType normalizedThirdType = ThirdType.builder()
                .thirdTypeName(StringNormalizer.normalizePreservingCase(thirdType.getThirdTypeName()))
                .entId(thirdType.getEntId())
                .status(thirdType.getStatus())
                .build();

        // Validar duplicados usando nombre normalizado (validación de negocio)
        validateDuplicateThirdTypeName(normalizedThirdType.getThirdTypeName(), normalizedThirdType.getEntId());

        // Guardar el tipo de tercero
        ThirdType createdThirdType = idOutputPort.saveThirdType(normalizedThirdType);

        // Publicar evento de creación
        ThirdTypeCreatedEvent event = ThirdTypeCreatedEvent.builder()
                .thirdTypeId(createdThirdType.getThirdTypeId())
                .thirdTypeName(createdThirdType.getThirdTypeName())
                .entId(createdThirdType.getEntId())
                .build();

        thirdTypeEventPublisher.publishThirdTypeCreatedEvent(event);

        return createdThirdType;
    }

    /**
     * Valida que no exista un tipo de tercero con el mismo nombre (validación de negocio).
     * Usa el nombre ya normalizado para validar contra la base de datos (case-insensitive).
     * 
     * @param normalizedThirdTypeName el nombre del tipo de tercero ya normalizado
     * @param entId                   el ID de la entidad
     * @throws ThirdTypeNameAlreadyExistsException si ya existe un tipo de tercero con el mismo nombre
     */
    private void validateDuplicateThirdTypeName(String normalizedThirdTypeName, String entId) {
        if (thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId(normalizedThirdTypeName, entId)) {
            throw new ThirdTypeNameAlreadyExistsException(normalizedThirdTypeName);
        }
    }

}
