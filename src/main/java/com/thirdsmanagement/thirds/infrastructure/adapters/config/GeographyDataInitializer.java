package com.thirdsmanagement.thirds.infrastructure.adapters.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.thirdsmanagement.thirds.application.service.GeographyFileDiscoveryService;
import com.thirdsmanagement.thirds.domain.exceptions.geography.GeographyDataInitializationException;
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
            List<String> discoveredFiles = fileDiscoveryService.discoverGeographyFiles();

            if (discoveredFiles.isEmpty()) {
                return;
            }
            executeGeographyFiles(discoveredFiles);

        } catch (Exception e) {
            throw new GeographyDataInitializationException("Fallo en la inicialización de datos geográficos", e);
        }
    }

    /**
     * Ejecuta la lista de archivos SQL descubiertos
     */
    private void executeGeographyFiles(List<String> sqlFiles) {
        for (String sqlFile : sqlFiles) {
            try {
                sqlExecutionService.executeSqlFile(sqlFile);
            } catch (Exception e) {
                throw new GeographyDataInitializationException(
                        "Error al procesar archivo de datos geográficos: " + sqlFile, e);
            }
        }
    }
}
