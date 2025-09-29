package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.domain.model.*;
import com.thirdsmanagement.thirds.domain.utils.StringNormalizer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio especializado en conversión de datos Excel a entidades de dominio.
 * Utiliza cache pre-cargado para evitar consultas N+1.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataConverter {

    /**
     * Convierte datos de Excel a entidad Third usando cache pre-cargado.
     */
    public Third convertWithCache(ThirdExcelData excelData, BatchValidationService.ReferenceDataCache cache) {
        if (!excelData.hasRequiredFields()) {
                        return null;
        }

        // Obtener TypeId desde cache
        TypeId typeId = getTypeIdFromCache(excelData.getTypeIdName(), cache);
        if (typeId == null) {            
            return null;
        }

        // Obtener ThirdTypes desde cache
        Set<ThirdType> thirdTypes = getThirdTypesFromCache(excelData.getThirdTypesNames(), cache);

        return Third.builder()
                .entId(excelData.getEntId())
                .typeId(typeId)
                .thirdTypes(thirdTypes)
                .personType(excelData.getPersonType())
                .names(normalizeString(excelData.getNames()))
                .lastNames(normalizeString(excelData.getLastNames()))
                .socialReason(normalizeString(excelData.getSocialReason()))
                .gender(excelData.getGender())
                .idNumber(excelData.getIdNumber())
                .verificationNumber(excelData.getVerificationNumber())
                .state(excelData.getState() != null ? excelData.getState() : true)
                .address(excelData.getAddress())
                .phoneNumber(excelData.getPhoneNumber())
                .email(excelData.getEmail())
                .build();
    }

    /**
     * Obtiene los códigos geográficos resueltos para un registro.
     */
    public GeographyData getGeographyData(ThirdExcelData excelData, BatchValidationService.ReferenceDataCache cache) {
        return resolveGeographyFromCache(excelData, cache);
    }

    /**
     * Obtiene TypeId desde cache evitando consultas individuales.
     */
    private TypeId getTypeIdFromCache(String typeIdName, BatchValidationService.ReferenceDataCache cache) {
        if (typeIdName == null || typeIdName.trim().isEmpty()) {
            return null;
        }
        return cache.getTypeId(typeIdName.trim().toUpperCase());
    }

    /**
     * Obtiene ThirdTypes desde cache evitando consultas individuales.
     */
    private Set<ThirdType> getThirdTypesFromCache(Set<String> typeNames, BatchValidationService.ReferenceDataCache cache) {
        if (typeNames == null || typeNames.isEmpty()) {
            return new HashSet<>();
        }

        return typeNames.stream()
                .map(name -> cache.getThirdType(name.trim()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Resuelve geografía desde cache.
     * Todos los campos geográficos son obligatorios.
     */
    private GeographyData resolveGeographyFromCache(ThirdExcelData excelData, BatchValidationService.ReferenceDataCache cache) {
        String countryCode = null;
        String stateCode = null;
        String cityCode = null;

      
        if (excelData.getCountryName() != null && !excelData.getCountryName().trim().isEmpty()) {
            if (cache.hasCountry(excelData.getCountryName().trim())) {                
                countryCode = "COL"; // Por ahora solo Colombia
            }
        }

        
        if (countryCode != null && 
            excelData.getStateName() != null && !excelData.getStateName().trim().isEmpty()) {
            State state = cache.getState(excelData.getStateName().trim());
            if (state != null) {
                stateCode = state.getStateCode();
            }
        }

       
        if (stateCode != null && 
            excelData.getCityName() != null && !excelData.getCityName().trim().isEmpty()) {
            if (cache.hasCity(excelData.getCityName().trim(), stateCode)) {
                City city = cache.getCity(excelData.getCityName().trim(), stateCode);
                if (city != null) {
                    cityCode = city.getCityCode();
                }
            }
        }

        return new GeographyData(countryCode, stateCode, cityCode);
    }

    /**
     * Normaliza strings preservando case cuando es necesario.
     */
    private String normalizeString(String value) {
        return value != null ? StringNormalizer.normalizePreservingCase(value) : null;
    }

    /**
     * Clase auxiliar para encapsular datos geográficos resueltos.
     */
    @Getter
    @AllArgsConstructor
    public static class GeographyData {
        private final String countryCode;
        private final String stateCode;
        private final String cityCode;
    }
}
