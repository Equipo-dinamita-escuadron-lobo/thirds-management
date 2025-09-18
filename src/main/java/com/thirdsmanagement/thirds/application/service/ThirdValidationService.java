package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdPersonTypeValidationException;
import com.thirdsmanagement.thirds.domain.model.Third;

import org.springframework.stereotype.Component;

/**
 * Servicio para validaciones comunes de terceros.
 */
@Component
public class ThirdValidationService {

    /**
     * Valida los datos básicos del tercero.
     * 
     * @param third el tercero a validar
     * @throws ThirdInvalidDataException si los datos son inválidos
     */
    public void validateThirdData(Third third) {
        if (third == null) {
            throw new ThirdInvalidDataException("El tercero no puede ser null");
        }
        
        if (third.getEntId() == null || third.getEntId().trim().isEmpty()) {
            throw new ThirdInvalidDataException("El ID de entidad no puede estar vacío");
        }
        
        if (third.getIdNumber() == null) {
            throw new ThirdInvalidDataException("El número de identificación no puede estar vacío");
        }
        
        if (third.getTypeId() == null || third.getTypeId().getId() == null) {
            throw new ThirdInvalidDataException("El tipo de identificación no puede estar vacío");
        }
        
        if (third.getThirdTypes() == null || third.getThirdTypes().isEmpty()) {
            throw new ThirdInvalidDataException("Los tipos de tercero no pueden estar vacíos");
        }
        
        // Validar consistencia entre tipo de persona y campos requeridos
        validatePersonTypeConsistency(third);
    }
    
    /**
     * Valida la consistencia entre el tipo de persona y los campos requeridos.
     * 
     * @param third el tercero a validar
     * @throws ThirdPersonTypeValidationException si hay inconsistencias
     */
    public void validatePersonTypeConsistency(Third third) {
        if (third.getPersonType() == null) {
            throw new ThirdInvalidDataException("El tipo de persona no puede estar vacío");
        }
        
        boolean hasNames = third.getNames() != null && !third.getNames().trim().isEmpty();
        boolean hasLastNames = third.getLastNames() != null && !third.getLastNames().trim().isEmpty();
        boolean hasGender = third.getGender() != null;
        boolean hasSocialReason = third.getSocialReason() != null && !third.getSocialReason().trim().isEmpty();
        
        if (third.getPersonType().isNatural()) {
            // Para persona natural: nombres, apellidos y género son obligatorios
            if (!hasNames || !hasLastNames || !hasGender) {
                throw ThirdPersonTypeValidationException.forNaturalPersonMissingFields();
            }
            
            // Para persona natural: razón social NO debe estar presente
            if (hasSocialReason) {
                throw ThirdPersonTypeValidationException.forNaturalPersonWithForbiddenFields();
            }
        } else if (third.getPersonType().isJuridica()) {
            // Para persona jurídica: razón social es obligatoria
            if (!hasSocialReason) {
                throw ThirdPersonTypeValidationException.forLegalEntityMissingFields();
            }
            
            // Para persona jurídica: nombres, apellidos y género NO deben estar presentes
            if (hasNames || hasLastNames || hasGender) {
                throw ThirdPersonTypeValidationException.forLegalEntityWithForbiddenFields();
            }
        }
    }
}
