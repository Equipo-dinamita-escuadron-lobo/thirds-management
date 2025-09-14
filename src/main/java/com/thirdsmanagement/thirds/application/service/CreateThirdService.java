package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.CreateThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdEventPublisher;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.event.ThirdCreatedEvent;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdAlreadyExistsException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdInvalidDataException;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
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

    /**
     * Crea un nuevo tercero en el sistema.
     * 
     * @param third el tercero a crear
     * @return el tercero creado con su ID asignado
     * @throws ThirdInvalidDataException si el tercero es null o tiene datos inválidos
     * @throws ThirdAlreadyExistsException si ya existe un tercero con el mismo número de identificación
     */
    @Override
    @Transactional
    public Third createThird(Third third) {
        // Validar datos básicos
        validateThirdData(third);
        
        // Normalizar nombres
        Third normalizedThird = Third.builder()
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
        
        // Validar duplicados por número de identificación
        validateDuplicateThird(normalizedThird.getIdNumber(), normalizedThird.getEntId());
        
        // Guardar el tercero y obtener la entidad persistida con ID
        Third createdThird = thirdOutputPort.saveThird(normalizedThird);
        
        // Publicar evento de creación
        thirdEventPublisher.publishThirdCreatedEvent(new ThirdCreatedEvent(createdThird.getThId()));
        
        return createdThird;
    }
    
    /**
     * Creates a Third with geography validation from request codes.
     * @param third the Third object with geography codes
     * @param countryCode country code
     * @param stateCode state code  
     * @param cityCode city code
     * @return the created Third with validated geography
     */
    @Transactional
    public Third createThirdWithGeography(Third third, String countryCode, String stateCode, String cityCode) {
        // Basic validation
        validateThirdData(third);
        
        // Geography validation and retrieval
        Object[] geography = geographyValidationService.validateAndGetGeography(countryCode, stateCode, cityCode);
        Country country = (Country) geography[0];
        State state = (State) geography[1];
        City city = (City) geography[2];
        
        // Normalization of names and geography assignment
        Third normalizedThird = Third.builder()
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
        
        // Duplicate validation and save
        validateDuplicateThird(normalizedThird.getIdNumber(), normalizedThird.getEntId());
        
        Third createdThird = thirdOutputPort.saveThird(normalizedThird);
        thirdEventPublisher.publishThirdCreatedEvent(new ThirdCreatedEvent(createdThird.getThId()));
        
        return createdThird;
    }
    
    /**
     * Valida los datos básicos del tercero.
     * 
     * @param third el tercero a validar
     * @throws ThirdInvalidDataException si los datos son inválidos
     */
    private void validateThirdData(Third third) {
        if (third == null) {
            throw new ThirdInvalidDataException("El tercero no puede ser null");
        }
        
        if (third.getEntId() == null || third.getEntId().trim().isEmpty()) {
            throw new ThirdInvalidDataException("El ID de entidad no puede estar vacío");
        }
        
        if (third.getIdNumber() == null) {
            throw new ThirdInvalidDataException("El número de identificación no puede estar vacío");
        }
        
        if (third.getTypeId() == null || third.getTypeId().getTypeId() == null) {
            throw new ThirdInvalidDataException("El tipo de identificación no puede estar vacío");
        }
        
        if (third.getThirdTypes() == null || third.getThirdTypes().isEmpty()) {
            throw new ThirdInvalidDataException("Los tipos de tercero no pueden estar vacíos");
        }
    }
    
    /**
     * Valida que no exista un tercero con el mismo número de identificación.
     * 
     * @param idNumber el número de identificación
     * @param entId el ID de la entidad
     * @throws ThirdAlreadyExistsException si ya existe un tercero con el mismo número de identificación
     */
    private void validateDuplicateThird(Long idNumber, String entId) {
        if (thirdRepository.existThirdBy(idNumber, entId)) {
            throw new ThirdAlreadyExistsException(idNumber.toString());
        }
    }
}
