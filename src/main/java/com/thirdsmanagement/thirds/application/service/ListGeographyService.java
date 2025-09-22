package com.thirdsmanagement.thirds.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.thirdsmanagement.thirds.application.ports.input.ListGeographyUseCase;
import com.thirdsmanagement.thirds.application.ports.output.GeographyOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.geography.CountryNotFoundException;
import com.thirdsmanagement.thirds.domain.exceptions.geography.GeographyInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.geography.StateNotFoundException;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;

import lombok.RequiredArgsConstructor;

/**
 * Servicio que implementa los casos de uso para listar información geográfica.
 * Proporciona validación de jerarquía geográfica y acceso a datos de países, estados y ciudades.
 */
@Service
@RequiredArgsConstructor
public class ListGeographyService implements ListGeographyUseCase {

    private final GeographyOutputPort geographyOutputPort;

    @Override
    public List<Country> getAllCountries() {
        return geographyOutputPort.getAllActiveCountries();
    }

    @Override
    public List<State> getStatesByCountry(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            throw new GeographyInvalidDataException("El código del país no puede ser null o vacío");
        }

        if (!geographyOutputPort.existsActiveCountry(countryCode)) {
            throw new CountryNotFoundException("El país con código '" + countryCode + "' no existe");
        }

        return geographyOutputPort.getStatesByCountry(countryCode);
    }

    @Override
    public List<City> getCitiesByState(String stateCode, String countryCode) {
        if (stateCode == null || stateCode.trim().isEmpty()) {
            throw new GeographyInvalidDataException("El código del estado no puede ser null o vacío");
        }
        
        if (countryCode == null || countryCode.trim().isEmpty()) {
            throw new GeographyInvalidDataException("El código del país no puede ser null o vacío");
        }

        if (!geographyOutputPort.existsActiveCountry(countryCode)) {
            throw new CountryNotFoundException("El país con código '" + countryCode + "' no existe");
        }

        if (!geographyOutputPort.existsActiveState(stateCode, countryCode)) {
            throw new StateNotFoundException("El estado con código '" + stateCode + "' no existe o no pertenece al país '" + countryCode + "'");
        }

        return geographyOutputPort.getCitiesByState(stateCode, countryCode);
    }
}
