package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.event.ThirdCreatedEvent;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdAlreadyExistsException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdForeignKeyViolationException;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.TypeId;
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
    private final IdOutputPort idOutputPort;

    /**
     * Creates a Third with geography validation from request codes.
     * 
     * @param third       the Third object with geography codes
     * @param countryCode country code
     * @param stateCode   state code
     * @param cityCode    city code
     * @return the created Third with validated geography
     */
    @Override
    @Transactional
    public Third createThird(Third third, String countryCode, String stateCode, String cityCode) {
        // Basic validation
        thirdValidationService.validatePersonTypeConsistency(third);

        // Cargar TypeId completo si es necesario y validar compatibilidad
        Third thirdWithCompleteTypeId = loadCompleteTypeIdIfNeeded(third);
        thirdValidationService.validateTypeIdPersonTypeCompatibility(thirdWithCompleteTypeId);

        // Validar formato de NIT para personas jurídicas
        thirdValidationService.validateNitFormat(thirdWithCompleteTypeId);

        // Validar dígito de verificación según tipo de persona
        thirdValidationService.validateVerificationDigit(thirdWithCompleteTypeId);

        // Geography validation and retrieval
        Object[] geography = geographyValidationService.validateAndGetGeography(countryCode, stateCode, cityCode);
        Country country = (Country) geography[0];
        State state = (State) geography[1];
        City city = (City) geography[2];

        // Normalization of names and geography assignment usando el tercero con TypeId
        // completo
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

        // Duplicate validation and save
        validateDuplicateThird(normalizedThird.getIdNumber(), normalizedThird.getEntId());

        Third createdThird = thirdOutputPort.saveThird(normalizedThird);
        thirdEventPublisher.publishThirdCreatedEvent(new ThirdCreatedEvent(createdThird.getThId()));

        return createdThird;
    }

    /**
     * Valida que no exista un tercero duplicado con el mismo número de
     * identificación.
     * 
     * @param idNumber el número de identificación
     * @param entId    el ID de la entidad
     * @throws ThirdAlreadyExistsException si ya existe un tercero con el mismo
     *                                     número de identificación
     */
    private void validateDuplicateThird(Long idNumber, String entId) {
        if (thirdRepository.existThirdBy(idNumber, entId)) {
            throw new ThirdAlreadyExistsException(idNumber.toString());
        }
    }

    /**
     * Carga el TypeId completo desde la base de datos si es necesario.
     * 
     * @param third el tercero que puede tener un TypeId incompleto
     * @return el tercero con el TypeId completo cargado
     */
    private Third loadCompleteTypeIdIfNeeded(Third third) {
        if (third.getTypeId() == null || third.getTypeId().getId() == null) {
            return third; // No hay TypeId para cargar
        }

        // Si el TypeId no tiene código (typeId), cargarlo desde la base de datos
        if (third.getTypeId().getTypeId() == null || third.getTypeId().getTypeId().trim().isEmpty()) {
            TypeId completeTypeId = idOutputPort.getTypeIdById(third.getTypeId().getId());
            if (completeTypeId == null) {
                // El TypeId no existe - lanzar excepción específica
                throw new TypeIdForeignKeyViolationException(third.getTypeId().getId().toString());
            }

            // Crear un nuevo Third con el TypeId completo
            return Third.builder()
                    .entId(third.getEntId())
                    .personType(third.getPersonType())
                    .typeId(completeTypeId) // TypeId completo con código y clasificación
                    .thirdTypes(third.getThirdTypes())
                    .names(third.getNames())
                    .lastNames(third.getLastNames())
                    .socialReason(third.getSocialReason())
                    .gender(third.getGender())
                    .idNumber(third.getIdNumber())
                    .verificationNumber(third.getVerificationNumber())
                    .state(third.getState())
                    .address(third.getAddress())
                    .phoneNumber(third.getPhoneNumber())
                    .email(third.getEmail())
                    .country(third.getCountry())
                    .province(third.getProvince())
                    .city(third.getCity())
                    .build();
        }

        return third;
    }
}
