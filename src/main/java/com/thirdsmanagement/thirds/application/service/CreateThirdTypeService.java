package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.ThirdTypeEventPublisher;
import com.thirdsmanagement.thirds.domain.event.ThirdTypeCreatedEvent;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeInvalidDataException;
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
     * @throws ThirdTypeInvalidDataException       si el tipo de tercero es null o
     *                                             tiene datos inválidos
     * @throws ThirdTypeNameAlreadyExistsException si ya existe un tipo de tercero
     *                                             con el mismo nombre
     */
    @Override
    @Transactional
    public ThirdType createThirdType(ThirdType thirdType) {
        // Validar datos de entrada
        validateThirdTypeData(thirdType);

        // Normalizar el nombre del tipo de tercero para almacenamiento
        ThirdType normalizedThirdType = ThirdType.builder()
                .thirdTypeName(StringNormalizer.normalizePreservingCase(thirdType.getThirdTypeName()))
                .entId(thirdType.getEntId())
                .status(thirdType.getStatus())
                .build();

        // Validar duplicados usando nombre normalizado
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
     * Valida los datos básicos del tipo de tercero.
     * 
     * @param thirdType el tipo de tercero a validar
     * @throws ThirdTypeInvalidDataException si los datos son inválidos
     */
    private void validateThirdTypeData(ThirdType thirdType) {
        if (thirdType == null) {
            throw new ThirdTypeInvalidDataException("El tipo de tercero no puede ser null");
        }

        if (thirdType.getThirdTypeName() == null || thirdType.getThirdTypeName().trim().isEmpty()) {
            throw new ThirdTypeInvalidDataException("El nombre del tipo de tercero no puede estar vacío");
        }

        if (thirdType.getEntId() == null || thirdType.getEntId().trim().isEmpty()) {
            throw new ThirdTypeInvalidDataException("El ID de entidad no puede estar vacío");
        }
    }

    /**
     * Valida que no exista un tipo de tercero con el mismo nombre
     * (case-insensitive).
     * Usa el nombre ya normalizado para validar contra la base de datos.
     * 
     * @param normalizedThirdTypeName el nombre del tipo de tercero ya normalizado
     * @param entId                   el ID de la entidad
     * @throws ThirdTypeNameAlreadyExistsException si ya existe un tipo de tercero
     *                                             con el mismo nombre
     */
    private void validateDuplicateThirdTypeName(String normalizedThirdTypeName, String entId) {
        if (thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId(normalizedThirdTypeName, entId)) {
            throw new ThirdTypeNameAlreadyExistsException(normalizedThirdTypeName);
        }
    }

}
