package com.thirdsmanagement.thirds.application.ports.output;

import java.util.List;

import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;

/**
 * Puerto de salida para operaciones geográficas.
 * Define los métodos para acceder a datos de países, estados y ciudades.
 */
public interface GeographyOutputPort {
    
    /**
     * Obtiene todos los países activos.
     * @return Lista de países activos ordenados por nombre
     */
    List<Country> getAllActiveCountries();
    
    /**
     * Obtiene todos los estados activos de un país específico.
     * @param countryCode Código del país
     * @return Lista de estados activos del país ordenados por nombre
     */
    List<State> getStatesByCountry(String countryCode);
    
    /**
     * Obtiene todas las ciudades activas de un estado específico.
     * @param stateCode Código del estado
     * @param countryCode Código del país
     * @return Lista de ciudades activas del estado ordenadas por nombre
     */
    List<City> getCitiesByState(String stateCode, String countryCode);
    
    /**
     * Verifica si existe un país activo con el código especificado.
     * @param countryCode Código del país
     * @return true si el país existe y está activo, false en caso contrario
     */
    boolean existsActiveCountry(String countryCode);
    
    /**
     * Verifica si existe un estado activo con el código especificado en un país.
     * @param stateCode Código del estado
     * @param countryCode Código del país
     * @return true si el estado existe y está activo, false en caso contrario
     */
    boolean existsActiveState(String stateCode, String countryCode);
    
    /**
     * Verifica si existe una ciudad activa con el código especificado en un estado.
     * @param cityCode Código de la ciudad
     * @param stateCode Código del estado
     * @param countryCode Código del país
     * @return true si la ciudad existe y está activa, false en caso contrario
     */
    boolean existsActiveCity(String cityCode, String stateCode, String countryCode);
}
