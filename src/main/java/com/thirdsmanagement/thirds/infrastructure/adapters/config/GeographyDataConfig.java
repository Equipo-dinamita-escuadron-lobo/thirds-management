package com.thirdsmanagement.thirds.infrastructure.adapters.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Configuración externa para la carga de datos geográficos.
 * Permite configurar rutas y orden de carga sin modificar código.
 */
@Data
@Component
@ConfigurationProperties(prefix = "geography.data")
public class GeographyDataConfig {
    
    /**
     * Directorio base para archivos de geografía
     */
    private String baseDirectory = "data/geography";
    
    /**
     * Configuración de carga por fases
     */
    private LoadPhases phases = new LoadPhases();
    
    /**
     * Configuración de países y sus directorios
     */
    private Map<String, CountryConfig> countries = Map.of(
        "colombia", new CountryConfig("colombia", true),
        "usa", new CountryConfig("usa", true),
        "mexico", new CountryConfig("mexico", true)
    );
    
    @Data
    public static class LoadPhases {
        private List<String> countries = List.of("01_countries.sql");
        private List<String> states = List.of("02_states.sql");
        private String citiesPattern = "**/*cities*.sql";
    }
    
    @Data
    public static class CountryConfig {
        private String directory;
        private boolean enabled;
        
        public CountryConfig() {}
        
        public CountryConfig(String directory, boolean enabled) {
            this.directory = directory;
            this.enabled = enabled;
        }
    }
}
