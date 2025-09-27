package com.thirdsmanagement.thirds.application.service;

import java.util.List;

import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.geography.CountryNotFoundException;
import com.thirdsmanagement.thirds.domain.exceptions.geography.GeographyInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.geography.StateNotFoundException;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio de validación geográfica para operaciones de terceros.
 * Valida la jerarquía geográfica: País → Estado/Departamento → Ciudad
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ThirdGeographyValidationService {

    private final GeographyOutputPort geographyOutputPort;

    /**
     * Valida y obtiene la información geográfica completa basada en los códigos
     * proporcionados.
     * 
     * @param countryCode código del país
     * @param stateCode   código del estado/departamento
     * @param cityCode    código de la ciudad
     * @return array con [Country, State, City] validados
     * @throws CountryNotFoundException      si el país no existe
     * @throws StateNotFoundException        si el estado no existe
     * @throws GeographyInvalidDataException si la jerarquía es inválida
     */
    public Object[] validateAndGetGeography(String countryCode, String stateCode, String cityCode) {

        // Validar que se proporcionen todos los códigos requeridos
        if (countryCode == null || countryCode.trim().isEmpty()) {
            throw new GeographyInvalidDataException("El código del país es obligatorio");
        }

        if (stateCode == null || stateCode.trim().isEmpty()) {
            throw new GeographyInvalidDataException("El código del estado/departamento es obligatorio");
        }

        if (cityCode == null || cityCode.trim().isEmpty()) {
            throw new GeographyInvalidDataException("El código de la ciudad es obligatorio");
        }

        // Validar país
        if (!geographyOutputPort.existsActiveCountry(countryCode.trim().toUpperCase())) {
            throw new CountryNotFoundException(countryCode);
        }

        // Validar estado/departamento
        if (!geographyOutputPort.existsActiveState(stateCode.trim(), countryCode.trim().toUpperCase())) {
            throw new StateNotFoundException(stateCode);
        }

        // Validar ciudad
        if (!geographyOutputPort.existsActiveCity(cityCode.trim(), stateCode.trim(),
                countryCode.trim().toUpperCase())) {
            throw new GeographyInvalidDataException(
                    String.format("La ciudad con código '%s' no existe en el estado '%s' del país '%s'",
                            cityCode, stateCode, countryCode));
        }

        // Obtener las entidades geográficas
        List<Country> countries = geographyOutputPort.getAllActiveCountries();
        Country country = countries.stream()
                .filter(c -> c.getCountryCode().equals(countryCode.trim().toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new CountryNotFoundException(countryCode));

        List<State> states = geographyOutputPort.getStatesByCountry(countryCode.trim().toUpperCase());
        State state = states.stream()
                .filter(s -> s.getStateCode().equals(stateCode.trim()))
                .findFirst()
                .orElseThrow(() -> new StateNotFoundException(stateCode, countryCode));

        List<City> cities = geographyOutputPort.getCitiesByState(stateCode.trim(), countryCode.trim().toUpperCase());
        City city = cities.stream()
                .filter(c -> c.getCityCode().equals(cityCode.trim()))
                .findFirst()
                .orElseThrow(() -> new GeographyInvalidDataException(
                        String.format("La ciudad con código '%s' no existe en el estado '%s' del país '%s'",
                                cityCode, stateCode, countryCode)));

        // Validar jerarquía
        validateGeographyHierarchy(country, state, city);

        return new Object[] { country, state, city };
    }

    /**
     * Valida que la jerarquía geográfica sea consistente.
     * 
     * @param country país
     * @param state   estado/departamento
     * @param city    ciudad
     * @throws GeographyInvalidDataException si la jerarquía es inconsistente
     */
    private void validateGeographyHierarchy(Country country, State state, City city) {
        // Validar que el estado pertenezca al país
        if (!country.getCountryCode().equals(state.getCountryCode())) {
            throw new GeographyInvalidDataException(
                    String.format("El estado '%s' no pertenece al país '%s'",
                            state.getStateName(), country.getCountryName()));
        }

        // Validar que la ciudad pertenezca al estado y país
        if (!state.getStateCode().equals(city.getStateCode()) ||
                !country.getCountryCode().equals(city.getCountryCode())) {
            throw new GeographyInvalidDataException(
                    String.format("La ciudad '%s' no pertenece al estado '%s' del país '%s'",
                            city.getCityName(), state.getStateName(), country.getCountryName()));
        }
    }

    /**
     * Valida si existe un país por su código.
     * 
     * @param countryCode código del país
     * @return true si existe, false si no existe
     */
    public boolean existsCountry(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            return false;
        }
        return geographyOutputPort.existsActiveCountry(countryCode.trim().toUpperCase());
    }

    /**
     * Valida si existe un estado en un país específico.
     * 
     * @param stateCode   código del estado
     * @param countryCode código del país
     * @return true si existe, false si no existe
     */
    public boolean existsState(String stateCode, String countryCode) {
        if (stateCode == null || stateCode.trim().isEmpty() ||
                countryCode == null || countryCode.trim().isEmpty()) {
            return false;
        }
        return geographyOutputPort.existsActiveState(stateCode.trim(), countryCode.trim().toUpperCase());
    }

    /**
     * Valida si existe una ciudad en un estado y país específicos.
     * 
     * @param cityCode    código de la ciudad
     * @param stateCode   código del estado
     * @param countryCode código del país
     * @return true si existe, false si no existe
     */
    public boolean existsCity(String cityCode, String stateCode, String countryCode) {
        if (cityCode == null || cityCode.trim().isEmpty() ||
                stateCode == null || stateCode.trim().isEmpty() ||
                countryCode == null || countryCode.trim().isEmpty()) {
            return false;
        }
        return geographyOutputPort.existsActiveCity(cityCode.trim(), stateCode.trim(),
                countryCode.trim().toUpperCase());
    }
}
