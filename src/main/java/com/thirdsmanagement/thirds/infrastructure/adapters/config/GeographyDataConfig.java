package com.thirdsmanagement.thirds.infrastructure.adapters.config;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Configuración simplificada para la carga de datos geográficos.
 * Contiene valores fijos y constantes para el descubrimiento automático de archivos.
 */
@Component
public class GeographyDataConfig {
    
    /**
     * Directorio base para archivos de geografía
     */
    public static final String BASE_DIRECTORY = "data/geography";
    
    /**
     * Archivo de países
     */
    public static final String COUNTRIES_FILE = "01_countries.sql";
    
    /**
     * Archivo de estados por país
     */
    public static final String STATES_FILE = "02_states.sql";
    
    /**
     * Patrón para archivos de ciudades
     */
    public static final String CITIES_PATTERN = "**/*cities*.sql";
    
    /**
     * Directorios de países soportados
     */
    public static final List<String> COUNTRY_DIRECTORIES = List.of(
        "colombia",
        "usa", 
        "mexico"
    );
    
    public String getBaseDirectory() {
        return BASE_DIRECTORY;
    }
    
    public String getCountriesFile() {
        return COUNTRIES_FILE;
    }
    
    public String getStatesFile() {
        return STATES_FILE;
    }
    
    public String getCitiesPattern() {
        return CITIES_PATTERN;
    }
    
    public List<String> getCountryDirectories() {
        return COUNTRY_DIRECTORIES;
    }
}
