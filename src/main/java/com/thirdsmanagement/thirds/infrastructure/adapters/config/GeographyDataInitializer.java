package com.thirdsmanagement.thirds.infrastructure.adapters.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.thirdsmanagement.thirds.application.service.GeographyFileDiscoveryService;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.SqlExecutionService;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.CountryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Inicializador de datos geográficos refactorizado.
 * Aplica principios SOLID y es altamente mantenible y escalable.
 * 
 * Principios aplicados:
 * - Single Responsibility: Solo coordina la carga inicial
 * - Open/Closed: Extensible sin modificar código existente
 * - Dependency Inversion: Depende de abstracciones, no implementaciones
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GeographyDataInitializer implements CommandLineRunner {

    private final CountryRepository countryRepository;
    private final GeographyFileDiscoveryService fileDiscoveryService;
    private final SqlExecutionService sqlExecutionService;

    @Override
    public void run(String... args) throws Exception {
        if (shouldLoadGeographyData()) {
            loadGeographyData();
        } else {
            log.info("Los datos geográficos ya están cargados en la base de datos.");
        }
    }

    /**
     * Determina si se deben cargar los datos geográficos
     */
    private boolean shouldLoadGeographyData() {
        return countryRepository.count() == 0;
    }

    /**
     * Coordina la carga de datos geográficos usando los servicios especializados.
     * Aplica el principio de composición sobre herencia.
     */
    private void loadGeographyData() {
        try {
            log.info("Iniciando carga automática de datos geográficos...");
            
            List<String> discoveredFiles = fileDiscoveryService.discoverGeographyFiles();
            
            if (discoveredFiles.isEmpty()) {
                log.warn("No se encontraron archivos SQL de geografía para cargar");
                return;
            }
            
            executeGeographyFiles(discoveredFiles);
            
            log.info("Datos geográficos cargados exitosamente. {} archivos procesados.", discoveredFiles.size());
            
        } catch (Exception e) {
            log.error("Error en la carga de datos geográficos: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo en la inicialización de datos geográficos", e);
        }
    }

    /**
     * Ejecuta la lista de archivos SQL descubiertos
     */
    private void executeGeographyFiles(List<String> sqlFiles) {
        for (String sqlFile : sqlFiles) {
            try {
                log.info("Procesando archivo: {}", sqlFile);
                sqlExecutionService.executeSqlFile(sqlFile);
            } catch (Exception e) {
                log.error("Error procesando archivo {}: {}", sqlFile, e.getMessage());
                throw new RuntimeException("Error en archivo: " + sqlFile, e);
            }
        }
    }
}
