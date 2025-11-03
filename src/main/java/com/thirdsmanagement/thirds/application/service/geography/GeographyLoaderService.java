package com.thirdsmanagement.thirds.application.service.geography;

import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to load complete geography data from codes.
 * Used by persistence layer to reconstruct full geography objects from database
 * strings.
 */
@Service
@RequiredArgsConstructor
public class GeographyLoaderService {

    private final GeographyOutputPort geographyOutputPort;

    /**
     * Loads complete Country object from country code.
     * 
     * @param countryCode the country code
     * @return complete Country object or null if not found
     */
    public Country loadCountryByCode(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            return null;
        }

        try {
            List<Country> countries = geographyOutputPort.getAllActiveCountries();
            return countries.stream()
                    .filter(country -> country.getCountryCode().equals(countryCode.trim().toUpperCase()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            // If geography service fails, return basic object with code only
            return Country.builder()
                    .countryCode(countryCode.trim().toUpperCase())
                    .countryName("")
                    .build();
        }
    }

    /**
     * Loads complete State object from state and country codes.
     * 
     * @param stateCode   the state code
     * @param countryCode the country code
     * @return complete State object or null if not found
     */
    public State loadStateByCode(String stateCode, String countryCode) {
        if (stateCode == null || stateCode.trim().isEmpty() ||
                countryCode == null || countryCode.trim().isEmpty()) {
            return null;
        }

        try {
            // Load the country first
            Country country = loadCountryByCode(countryCode);

            // Load states for the country
            List<State> states = geographyOutputPort.getStatesByCountry(countryCode.trim().toUpperCase());
            State state = states.stream()
                    .filter(s -> s.getStateCode().equals(stateCode.trim()))
                    .findFirst()
                    .orElse(null);

            if (state != null && country != null) {
                // Set the country reference in the state
                return State.builder()
                        .stateCode(state.getStateCode())
                        .stateName(state.getStateName())
                        .countryCode(state.getCountryCode())
                        .country(country)
                        .build();
            }

            return null;
        } catch (Exception e) {
            // If geography service fails, return basic object with codes only
            return State.builder()
                    .stateCode(stateCode.trim())
                    .stateName("")
                    .countryCode(countryCode.trim().toUpperCase())
                    .country(loadCountryByCode(countryCode))
                    .build();
        }
    }

    /**
     * Loads complete City object from city, state and country codes.
     * 
     * @param cityCode    the city code
     * @param stateCode   the state code
     * @param countryCode the country code
     * @return complete City object or null if not found
     */
    public City loadCityByCode(String cityCode, String stateCode, String countryCode) {
        if (cityCode == null || cityCode.trim().isEmpty() ||
                stateCode == null || stateCode.trim().isEmpty() ||
                countryCode == null || countryCode.trim().isEmpty()) {
            return null;
        }

        try {
            // Load the state first (which includes country)
            State state = loadStateByCode(stateCode, countryCode);

            // Load cities for the state
            List<City> cities = geographyOutputPort.getCitiesByState(stateCode.trim(),
                    countryCode.trim().toUpperCase());
            City city = cities.stream()
                    .filter(c -> c.getCityCode().equals(cityCode.trim()))
                    .findFirst()
                    .orElse(null);

            if (city != null && state != null) {
                // Set the state reference in the city
                return City.builder()
                        .cityCode(city.getCityCode())
                        .cityName(city.getCityName())
                        .stateCode(city.getStateCode())
                        .countryCode(city.getCountryCode())
                        .state(state)
                        .build();
            }

            return null;
        } catch (Exception e) {
            // If geography service fails, return basic object with codes only
            return City.builder()
                    .cityCode(cityCode.trim())
                    .cityName("")
                    .stateCode(stateCode.trim())
                    .countryCode(countryCode.trim().toUpperCase())
                    .state(loadStateByCode(stateCode, countryCode))
                    .build();
        }
    }
}
