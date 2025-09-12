package com.thirdsmanagement.thirds.application.ports.input;

import java.util.List;

import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;

/**
 * Caso de uso para listar información geográfica.
 * Permite obtener países, estados y ciudades con validación de jerarquía.
 */
public interface ListGeographyUseCase {
    
    /**
     * Obtiene todos los países activos.
     * @return Lista de países activos ordenados por nombre
     */
    List<Country> getAllCountries();
    
    /**
     * Obtiene todos los estados activos de un país específico.
     * @param countryCode Código del país
     * @return Lista de estados activos del país ordenados por nombre
     * @throws IllegalArgumentException si el país no existe o no está activo
     */
    List<State> getStatesByCountry(String countryCode);
    
    /**
     * Obtiene todas las ciudades activas de un estado específico.
     * @param stateCode Código del estado
     * @param countryCode Código del país
     * @return Lista de ciudades activas del estado ordenadas por nombre
     * @throws IllegalArgumentException si el estado no existe, no está activo o no pertenece al país
     */
    List<City> getCitiesByState(String stateCode, String countryCode);
}
