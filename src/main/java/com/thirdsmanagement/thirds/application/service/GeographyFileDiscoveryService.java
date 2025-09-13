package com.thirdsmanagement.thirds.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.thirdsmanagement.thirds.infrastructure.adapters.config.GeographyDataConfig;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para descubrimiento automático de archivos SQL de geografía.
 * Aplica el principio de Single Responsibility.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GeographyFileDiscoveryService {
    
    private final GeographyDataConfig config;
    private final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    
    /**
     * Descubre y ordena todos los archivos SQL de geografía automáticamente
     */
    public List<String> discoverGeographyFiles() {
        List<String> allFiles = new ArrayList<>();
        
        try {
            // 1. Archivos de países (fase 1)
            allFiles.addAll(discoverCountryFiles());
            
            // 2. Archivos de estados (fase 2) 
            allFiles.addAll(discoverStateFiles());
            
            // 3. Archivos de ciudades (fase 3) - ordenados automáticamente
            allFiles.addAll(discoverCityFiles());
            
            log.info("Descubiertos {} archivos SQL de geografía", allFiles.size());
            return allFiles;
            
        } catch (IOException e) {
            log.error("Error descubriendo archivos de geografía", e);
            throw new RuntimeException("Error en descubrimiento de archivos", e);
        }
    }
    
    private List<String> discoverCountryFiles() throws IOException {
        return config.getPhases().getCountries().stream()
            .map(file -> config.getBaseDirectory() + "/" + file)
            .filter(this::resourceExists)
            .collect(Collectors.toList());
    }
    
    private List<String> discoverStateFiles() throws IOException {
        List<String> stateFiles = new ArrayList<>();
        
        for (Map.Entry<String, GeographyDataConfig.CountryConfig> entry : config.getCountries().entrySet()) {
            if (entry.getValue().isEnabled()) {
                String countryDir = entry.getValue().getDirectory();
                for (String stateFile : config.getPhases().getStates()) {
                    String fullPath = config.getBaseDirectory() + "/" + countryDir + "/" + stateFile;
                    if (resourceExists(fullPath)) {
                        stateFiles.add(fullPath);
                    }
                }
            }
        }
        
        return stateFiles;
    }
    
    private List<String> discoverCityFiles() throws IOException {
        List<String> cityFiles = new ArrayList<>();
        
        for (Map.Entry<String, GeographyDataConfig.CountryConfig> entry : config.getCountries().entrySet()) {
            if (entry.getValue().isEnabled()) {
                String countryDir = entry.getValue().getDirectory();
                String pattern = config.getBaseDirectory() + "/" + countryDir + "/" + config.getPhases().getCitiesPattern();
                
                Resource[] resources = resourceResolver.getResources("classpath:" + pattern);
                
                // Ordenar archivos por nombre para mantener consistencia
                Arrays.stream(resources)
                    .map(resource -> extractRelativePath(resource, config.getBaseDirectory()))
                    .filter(Objects::nonNull)
                    .sorted()
                    .forEach(cityFiles::add);
            }
        }
        
        return cityFiles;
    }
    
    private boolean resourceExists(String path) {
        try {
            Resource resource = resourceResolver.getResource("classpath:" + path);
            return resource.exists();
        } catch (Exception e) {
            return false;
        }
    }
    
    private String extractRelativePath(Resource resource, String baseDir) {
        try {
            String fullPath = resource.getURI().toString();
            int baseIndex = fullPath.indexOf(baseDir);
            return baseIndex >= 0 ? fullPath.substring(baseIndex) : null;
        } catch (Exception e) {
            log.warn("Error extrayendo ruta relativa de recurso: {}", resource.getDescription());
            return null;
        }
    }
}
