package com.thirdsmanagement.thirds.application.service.thirdType;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeNameAlreadyExistsException;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.infrastructure.audit.annotation.Auditable;
import com.thirdsmanagement.thirds.infrastructure.audit.annotation.OperationType;
import com.thirdsmanagement.thirds.domain.utils.StringNormalizer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateThirdTypeService implements CreateThirdTypeUseCase {

    private final IdOutputPort idOutputPort;
    private final ThirdTypeRepository thirdTypeRepository;

    @Auditable(operationType = OperationType.CREATE, affectedTable = "THIRD_TYPE")
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

        return createdThirdType;
    }

    /**
     * @brief Valida que no exista un tipo de tercero con el mismo nombre
     * @param normalizedThirdTypeName nombre del tipo de tercero ya normalizado
     * @param entId identificador de la entidad
     * @throws ThirdTypeNameAlreadyExistsException si ya existe un tipo de tercero con el mismo nombre
     */
    private void validateDuplicateThirdTypeName(String normalizedThirdTypeName, String entId) {
        if (thirdTypeRepository.existsByTtNameIgnoreCaseAndTtentId(normalizedThirdTypeName, entId)) {
            throw new ThirdTypeNameAlreadyExistsException(normalizedThirdTypeName);
        }
    }

}
