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
import com.thirdsmanagement.thirds.domain.utils.StringNormalizer;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdInvalidDataException;
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
     * Actualiza un tercero existente en el sistema.
     * 
     * @param third el tercero con los datos actualizados
     * @return el tercero actualizado
     * @throws IllegalArgumentException si el tercero es null o no tiene ID válido
     */
    @Override
    @Transactional
    public Third updateThird(Third third) {
        if (third == null) {
            throw new ThirdInvalidDataException("El tercero no puede ser null");
        }
        
        if (third.getThId() == null) {
            throw new ThirdInvalidDataException("El ID del tercero no puede ser null para actualizar");
        }
        
        // Validar que el tercero existe
        if (!thirdOutputPort.existThirdById(third.getThId(), third.getEntId())) {
            throw new ThirdInvalidDataException("El tercero con ID " + third.getThId() + " no existe");
        }
        
        // Validar datos básicos y consistencia de tipo de persona
        thirdValidationService.validatePersonTypeConsistency(third);
        
        // Validar que el TypeId existe
        validateTypeIdExists(third);
        
        // Validar que los ThirdTypes existen
        validateThirdTypesExist(third);
        
        // Normalize names
        Third normalizedThird = Third.builder()
                .thId(third.getThId())
                .entId(third.getEntId())
                .personType(third.getPersonType())
                .typeId(third.getTypeId())
                .thirdTypes(third.getThirdTypes())
                .names(third.getNames() != null ? StringNormalizer.normalizePreservingCase(third.getNames()) : null)
                .lastNames(third.getLastNames() != null ? StringNormalizer.normalizePreservingCase(third.getLastNames()) : null)
                .socialReason(third.getSocialReason() != null ? StringNormalizer.normalizePreservingCase(third.getSocialReason()) : null)
                .gender(third.getGender())
                .idNumber(third.getIdNumber())
                .verificationNumber(third.getVerificationNumber())
                .state(third.getState() != null ? third.getState() : true)
                .address(third.getAddress())
                .phoneNumber(third.getPhoneNumber())
                .email(third.getEmail())
                .country(third.getCountry())
                .province(third.getProvince())
                .city(third.getCity())
                .build();
        
        // Actualizar el tercero
        Third updatedThird = thirdOutputPort.updateThird(normalizedThird);
        
        // Publicar evento de actualización
        thirdEventPublisher.publishThirdUpdateEvent(new ThirdUpdateEvent(updatedThird.getThId()));

        return updatedThird;
    }
    
    /**
     * Updates a Third with geography validation from request codes.
     * @param third the Third object to update
     * @param countryCode country code
     * @param stateCode state code  
     * @param cityCode city code
     * @return the updated Third with validated geography
     */
    @Transactional
    public Third updateThirdWithGeography(Third third, String countryCode, String stateCode, String cityCode) {
        if (third == null) {
            throw new IllegalArgumentException("El tercero no puede ser null");
        }
        
        if (third.getThId() == null) {
            throw new IllegalArgumentException("El ID del tercero no puede ser null para actualizar");
        }
        
        // Validar datos básicos y consistencia de tipo de persona
        thirdValidationService.validatePersonTypeConsistency(third);
        
        // Geography validation and retrieval
        Object[] geography = geographyValidationService.validateAndGetGeography(countryCode, stateCode, cityCode);
        Country country = (Country) geography[0];
        State state = (State) geography[1];
        City city = (City) geography[2];
        
        // Normalization of names and geography assignment
        Third normalizedThird = Third.builder()
                .thId(third.getThId())
                .entId(third.getEntId())
                .personType(third.getPersonType())
                .typeId(third.getTypeId())
                .thirdTypes(third.getThirdTypes())
                .names(third.getNames() != null ? StringNormalizer.normalizePreservingCase(third.getNames()) : null)
                .lastNames(third.getLastNames() != null ? StringNormalizer.normalizePreservingCase(third.getLastNames()) : null)
                .socialReason(third.getSocialReason() != null ? StringNormalizer.normalizePreservingCase(third.getSocialReason()) : null)
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
     * Valida que el TypeId existe en el sistema.
     * @param third el tercero que contiene el TypeId a validar
     * @throws TypeIdForeignKeyViolationException si el TypeId no existe
     */
    private void validateTypeIdExists(Third third) {
        if (third.getTypeId() == null || third.getTypeId().getId() == null) {
            throw new ThirdInvalidDataException("El tipo de identificación no puede ser null");
        }
        
        if (!idOutputPort.existsTypeIdById(third.getTypeId().getId())) {
            throw new TypeIdForeignKeyViolationException(third.getTypeId().getId().toString());
        }
    }
    
    /**
     * Valida que todos los ThirdTypes existen en el sistema.
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
