package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence;

import java.util.List;

import org.springframework.stereotype.Component;

import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.GeographyPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.CityRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.CountryRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.StateRepository;

import lombok.RequiredArgsConstructor;

/**
 * @brief Adaptador de persistencia para consultas geográficas optimizadas
 *
 * Implementa GeographyOutputPort para acceso eficiente a datos geográficos.
 * Utiliza consultas especializadas de los repositorios geográficos para obtener
 * países, estados y ciudades con ordenamiento.
 */
@Component
@RequiredArgsConstructor
public class GeographyPersistenceAdapter implements GeographyOutputPort {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final GeographyPersistenceMapper geographyMapper;

    @Override
    public List<Country> getAllActiveCountries() {
        return geographyMapper.toCountryList(countryRepository.findAllCountries());
    }

    @Override
    public List<State> getStatesByCountry(String countryCode) {
        return geographyMapper.toStateList(stateRepository.findByCountryCodeOrderByStateName(countryCode));
    }

    @Override
    public List<City> getCitiesByState(String stateCode, String countryCode) {
        return geographyMapper.toCityList(cityRepository.findByStateCodeAndCountryCodeOrderByCityName(stateCode, countryCode));
    }

    @Override
    public boolean existsActiveCountry(String countryCode) {
        return countryRepository.existsByCountryCode(countryCode);
    }

    @Override
    public boolean existsActiveState(String stateCode, String countryCode) {
        return stateRepository.existsByStateCodeAndCountryCode(stateCode, countryCode);
    }

    @Override
    public boolean existsActiveCity(String cityCode, String stateCode, String countryCode) {
        return cityRepository.existsByCityCodeAndStateCodeAndCountryCode(cityCode, stateCode, countryCode);
    }
}
