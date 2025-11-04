package com.thirdsmanagement.thirds.application.service.third;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.application.service.typeId.TypeIdLoaderService;
import com.thirdsmanagement.thirds.domain.event.ThirdCreatedEvent;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdAlreadyExistsException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeForeignKeyViolationException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdInvalidDataException;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.utils.StringNormalizer;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateThirdService implements CreateThirdUseCase {

    private final ThirdOutputPort thirdOutputPort;
    private final ThirdEventPublisher thirdEventPublisher;
    private final ThirdRepository thirdRepository;
    private final ThirdGeographyValidationService geographyValidationService;
    private final ThirdValidationService thirdValidationService;
    private final TypeIdLoaderService typeIdLoaderService;
    private final IdOutputPort idOutputPort;

    /**
     * @brief Crea un tercero con validación geográfica desde códigos proporcionados
     * @param third tercero a crear
     * @param countryCode código del país
     * @param stateCode código del departamento
     * @param cityCode código de la ciudad
     * @return tercero creado con geografía validada
     */
    @Override
    @Transactional
    public Third createThird(Third third, String countryCode, String stateCode, String cityCode) {
       
        thirdValidationService.validatePersonTypeConsistency(third);

        Third thirdWithCompleteTypeId = typeIdLoaderService.loadCompleteTypeId(third);
        thirdValidationService.validateTypeIdPersonTypeCompatibility(thirdWithCompleteTypeId);

        if (thirdWithCompleteTypeId.getTypeId() != null && !Boolean.TRUE.equals(thirdWithCompleteTypeId.getTypeId().getStatus())) {
            throw new TypeIdInvalidDataException("El tipo de identificación seleccionado está inactivo");
        }

        thirdValidationService.validateNitFormat(thirdWithCompleteTypeId);

        thirdValidationService.validateVerificationDigit(thirdWithCompleteTypeId);

        validateThirdTypesExistAndActive(third);

        Country country = null;
        State state = null;
        City city = null;
        
        if (countryCode != null && stateCode != null && cityCode != null) {
            Object[] geography = geographyValidationService.validateAndGetGeography(countryCode, stateCode, cityCode);
            country = (Country) geography[0];
            state = (State) geography[1];
            city = (City) geography[2];
        }

        Third normalizedThird = Third.builder()
                .entId(thirdWithCompleteTypeId.getEntId())
                .personType(thirdWithCompleteTypeId.getPersonType())
                .typeId(thirdWithCompleteTypeId.getTypeId())
                .thirdTypes(third.getThirdTypes())
                .names(third.getNames() != null ? StringNormalizer.normalizePreservingCase(third.getNames()) : null)
                .lastNames(third.getLastNames() != null ? StringNormalizer.normalizePreservingCase(third.getLastNames())
                        : null)
                .socialReason(third.getSocialReason() != null
                        ? StringNormalizer.normalizePreservingCase(third.getSocialReason())
                        : null)
                .gender(third.getGender())
                .idNumber(third.getIdNumber())
                .verificationNumber(third.getVerificationNumber())
                .state(third.getState() != null ? third.getState() : true)
                .address(third.getAddress())
                .phoneNumber(third.getPhoneNumber())
                .email(third.getEmail())
                .country(country)
                .province(state)
                .city(city)
                .build();

        validateDuplicateThird(normalizedThird.getIdNumber(), normalizedThird.getEntId());

        Third createdThird = thirdOutputPort.saveThird(normalizedThird);
        thirdEventPublisher.publishThirdCreatedEvent(new ThirdCreatedEvent(createdThird.getThId()));

        return createdThird;
    }

    /**
     * @brief Valida que no exista un tercero duplicado con el mismo número de identificación
     * @param idNumber número de identificación
     * @param entId identificador de la entidad
     * @throws ThirdAlreadyExistsException si ya existe un tercero con el mismo número de identificación
     */
    private void validateDuplicateThird(Long idNumber, String entId) {
        if (thirdRepository.existThirdBy(idNumber, entId)) {
            throw new ThirdAlreadyExistsException(idNumber.toString());
        }
    }

    /**
     * @brief Valida que todos los ThirdTypes existen en el sistema y están activos
     * @param third tercero que contiene los ThirdTypes a validar
     * @throws ThirdTypeForeignKeyViolationException si algún ThirdType no existe
     * @throws ThirdInvalidDataException si algún ThirdType está inactivo
     */
    private void validateThirdTypesExistAndActive(Third third) {
        if (third.getThirdTypes() == null || third.getThirdTypes().isEmpty()) {
            throw new ThirdInvalidDataException("Los tipos de tercero no pueden estar vacíos");
        }

        for (ThirdType thirdType : third.getThirdTypes()) {
            if (thirdType.getThirdTypeId() == null) {
                throw new ThirdInvalidDataException("El ID del tipo de tercero no puede ser null");
            }

            if (!idOutputPort.existsThirdTypeById(thirdType.getThirdTypeId())) {
                throw new ThirdTypeForeignKeyViolationException(thirdType.getThirdTypeId().toString());
            }

            // Validar que el ThirdType esté activo
            ThirdType completeThirdType = idOutputPort.getThirdTypeById(thirdType.getThirdTypeId());
            if (!Boolean.TRUE.equals(completeThirdType.getStatus())) {
                throw new ThirdInvalidDataException("El tipo de tercero '" + completeThirdType.getThirdTypeName() + "' está inactivo");
            }
        }
    }
}
