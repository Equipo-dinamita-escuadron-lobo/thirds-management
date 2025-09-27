package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.UpdateThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.event.ThirdUpdateEvent;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.domain.utils.StringNormalizer;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdNotFound;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdForeignKeyViolationException;
import com.thirdsmanagement.thirds.domain.exceptions.thirdType.ThirdTypeForeignKeyViolationException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateThirdService implements UpdateThirdUseCase {

    private final ThirdOutputPort thirdOutputPort;
    private final ThirdEventPublisher thirdEventPublisher;
    private final ThirdGeographyValidationService geographyValidationService;
    private final ThirdValidationService thirdValidationService;
    private final IdOutputPort idOutputPort;

    /**
     * Actualiza un tercero existente en el sistema con validación opcional de
     * geografía.
     * Este método centraliza toda la lógica de actualización y puede manejar tanto
     * actualizaciones simples como actualizaciones con validación geográfica.
     * 
     * @param third       el tercero con los datos actualizados
     * @param countryCode código del país (opcional, puede ser null)
     * @param stateCode   código del estado/departamento (opcional, puede ser null)
     * @param cityCode    código de la ciudad (opcional, puede ser null)
     * @return el tercero actualizado
     * @throws IllegalArgumentException si el tercero es null o no tiene ID válido
     * @throws ThirdNotFound            si el tercero no existe
     */
    @Override
    @Transactional
    public Third updateThirdWithGeography(Third third, String countryCode, String stateCode, String cityCode) {
        if (third == null) {
            throw new IllegalArgumentException("El tercero no puede ser null");
        }

        if (third.getThId() == null) {
            throw new IllegalArgumentException("El ID del tercero no puede ser null para actualizar");
        }

        // Validar que el tercero existe
        if (!thirdOutputPort.existThirdById(third.getThId(), third.getEntId())) {
            throw new ThirdNotFound("El tercero con ID " + third.getThId() + " no existe");
        }

        // Validar datos básicos y consistencia de tipo de persona
        thirdValidationService.validatePersonTypeConsistency(third);

        // Validar que el TypeId existe y cargarlo completo si es necesario
        Third thirdWithCompleteTypeId = validateAndLoadCompleteTypeId(third);

        // Validar compatibilidad entre TypeId y PersonType
        thirdValidationService.validateTypeIdPersonTypeCompatibility(thirdWithCompleteTypeId);

        // Validar formato de NIT para personas jurídicas
        thirdValidationService.validateNitFormat(thirdWithCompleteTypeId);

        // Validar que los ThirdTypes existen
        validateThirdTypesExist(third);

        // Geography validation and retrieval (solo si se proporcionan códigos)
        Country country = third.getCountry();
        State state = third.getProvince();
        City city = third.getCity();

        if (countryCode != null && stateCode != null && cityCode != null) {
            Object[] geography = geographyValidationService.validateAndGetGeography(countryCode, stateCode, cityCode);
            country = (Country) geography[0];
            state = (State) geography[1];
            city = (City) geography[2];
        }

        // Normalization of names and geography assignment usando el tercero con TypeId
        // completo
        Third normalizedThird = Third.builder()
                .thId(thirdWithCompleteTypeId.getThId())
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

        // Update the third
        Third updatedThird = thirdOutputPort.updateThird(normalizedThird);

        // Publish update event
        thirdEventPublisher.publishThirdUpdateEvent(new ThirdUpdateEvent(updatedThird.getThId()));

        return updatedThird;
    }

    /**
     * Valida que el TypeId existe y lo carga completo desde la base de datos.
     * 
     * @param third el tercero que contiene el TypeId a validar
     * @return el tercero con el TypeId completo cargado
     * @throws TypeIdForeignKeyViolationException si el TypeId no existe
     */
    private Third validateAndLoadCompleteTypeId(Third third) {
        if (third.getTypeId() == null || third.getTypeId().getId() == null) {
            throw new ThirdInvalidDataException("El tipo de identificación no puede ser null");
        }

        if (!idOutputPort.existsTypeIdById(third.getTypeId().getId())) {
            throw new TypeIdForeignKeyViolationException(third.getTypeId().getId().toString());
        }

        // Si el TypeId no tiene código (typeId), cargarlo desde la base de datos
        if (third.getTypeId().getTypeId() == null || third.getTypeId().getTypeId().trim().isEmpty()) {
            TypeId completeTypeId = idOutputPort.getTypeIdById(third.getTypeId().getId());
            if (completeTypeId == null) {
                throw new TypeIdForeignKeyViolationException(third.getTypeId().getId().toString());
            }

            // Crear un nuevo Third con el TypeId completo
            return Third.builder()
                    .thId(third.getThId())
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

    /**
     * Valida que todos los ThirdTypes existen en el sistema.
     * 
     * @param third el tercero que contiene los ThirdTypes a validar
     * @throws ThirdTypeForeignKeyViolationException si algún ThirdType no existe
     */
    private void validateThirdTypesExist(Third third) {
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
        }
    }
}
