package com.thirdsmanagement.thirds.unit.application.service.geography;

import com.thirdsmanagement.thirds.application.service.geography.GeographyFileDiscoveryService;
import com.thirdsmanagement.thirds.infrastructure.config.GeographyDataConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para GeographyFileDiscoveryService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GeographyFileDiscoveryServiceUnitTest {

    @Mock
    private GeographyDataConfig config;

    @Mock
    private ResourcePatternResolver resourceResolver;

    private GeographyFileDiscoveryService geographyFileDiscoveryService;

    @BeforeEach
    void setUp() {
        when(config.getBaseDirectory()).thenReturn("data/geography");
        when(config.getCountriesFile()).thenReturn("01_countries.sql");
        when(config.getStatesFile()).thenReturn("02_states.sql");
        when(config.getCitiesPattern()).thenReturn("**/*cities*.sql");
        when(config.getCountryDirectories()).thenReturn(List.of("colombia"));
        geographyFileDiscoveryService = new GeographyFileDiscoveryService(config, resourceResolver);
    }

    @Test
    @DisplayName("Debe descubrir archivos de geografía correctamente cuando todos existen")
    void testDiscoverGeographyFiles_WithAllFilesPresent() throws IOException {
        // Arrange
        Resource[] cityResources = createMockResources("cities01.sql", "cities02.sql", "cities03.sql");
        // Mockear archivos existentes para países y estados
        Resource existentResource = createMockResource(true);

        when(resourceResolver.getResource("classpath:data/geography/01_countries.sql")).thenReturn(existentResource);
        when(resourceResolver.getResource("classpath:data/geography/colombia/02_states.sql")).thenReturn(existentResource);
        when(resourceResolver.getResources("classpath:data/geography/colombia/**/*cities*.sql")).thenReturn(cityResources);

        // Act
        List<String> result = geographyFileDiscoveryService.discoverGeographyFiles();

        // Assert
        assertNotNull(result);
        assertTrue(result.size() > 0);
        verify(resourceResolver).getResources("classpath:data/geography/colombia/**/*cities*.sql");
    }

    @Test
    @DisplayName("Debe retornar lista con solo archivos existentes")
    void testDiscoverGeographyFiles_WithPartialFiles() throws IOException {
        // Arrange
        Resource nonExistentResource = createMockResource(false);
        Resource existentResource = createMockResource(true);
        Resource[] cityResources = createMockResources("cities01.sql", "cities02.sql");

        // País existe, estado no existe, ciudades existen
        when(resourceResolver.getResource("classpath:data/geography/01_countries.sql")).thenReturn(existentResource);
        when(resourceResolver.getResource("classpath:data/geography/colombia/02_states.sql")).thenReturn(nonExistentResource);
        when(resourceResolver.getResources("classpath:data/geography/colombia/**/*cities*.sql")).thenReturn(cityResources);

        // Act
        List<String> result = geographyFileDiscoveryService.discoverGeographyFiles();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size()); // 1 país + 0 estados + 2 ciudades
        assertTrue(result.contains("data/geography/01_countries.sql"));
        assertTrue(result.stream().anyMatch(path -> path.contains("cities01.sql")));
        assertTrue(result.stream().anyMatch(path -> path.contains("cities02.sql")));
    }

    @Test
    @DisplayName("Debe manejar IOException correctamente")
    void testDiscoverGeographyFiles_WithIOException() throws IOException {
        // Arrange
        Resource existentResource = createMockResource(true);

        // Mock archivos existentes pero getResources lanza excepción
        when(resourceResolver.getResource("classpath:data/geography/01_countries.sql")).thenReturn(existentResource);
        when(resourceResolver.getResource("classpath:data/geography/colombia/02_states.sql")).thenReturn(existentResource);
        when(resourceResolver.getResources("classpath:data/geography/colombia/**/*cities*.sql"))
            .thenThrow(new IOException("Test error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            geographyFileDiscoveryService.discoverGeographyFiles();
        });

        assertEquals("Error en descubrimiento de archivos", exception.getMessage());
        assertTrue(exception.getCause() instanceof IOException);
    }

    @Test
    @DisplayName("Debe ordenar archivos de ciudades correctamente")
    void testDiscoverGeographyFiles_WithCityFilesOrdering() throws IOException {
        // Arrange
        Resource[] cityResources = createMockResources("cities03.sql", "cities01.sql", "cities02.sql");
        // Mockear archivos inexistentes para países y estados
        Resource nonExistentResource = createMockResource(false);

        when(resourceResolver.getResource("classpath:data/geography/01_countries.sql")).thenReturn(nonExistentResource);
        when(resourceResolver.getResource("classpath:data/geography/colombia/02_states.sql")).thenReturn(nonExistentResource);
        when(resourceResolver.getResources("classpath:data/geography/colombia/**/*cities*.sql")).thenReturn(cityResources);

        // Act
        List<String> result = geographyFileDiscoveryService.discoverGeographyFiles();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.get(0).contains("cities01"));
        assertTrue(result.get(1).contains("cities02"));
        assertTrue(result.get(2).contains("cities03"));
    }

    @Test
    @DisplayName("Debe manejar múltiples directorios de países")
    void testDiscoverGeographyFiles_WithMultipleCountryDirectories() throws IOException {
        // Arrange
        when(config.getCountryDirectories()).thenReturn(List.of("colombia", "mexico"));
        Resource existentResource = createMockResource(true);
        Resource[] cityResourcesColombia = createMockResources("cities_colombia.sql");
        Resource[] cityResourcesMexico = createMockResources("cities_mexico.sql");

        // Mock archivos existentes para ambos países
        when(resourceResolver.getResource("classpath:data/geography/01_countries.sql")).thenReturn(existentResource);
        when(resourceResolver.getResource("classpath:data/geography/colombia/02_states.sql")).thenReturn(existentResource);
        when(resourceResolver.getResource("classpath:data/geography/mexico/02_states.sql")).thenReturn(existentResource);
        when(resourceResolver.getResources("classpath:data/geography/colombia/**/*cities*.sql")).thenReturn(cityResourcesColombia);
        when(resourceResolver.getResources("classpath:data/geography/mexico/**/*cities*.sql")).thenReturn(cityResourcesMexico);

        // Act
        List<String> result = geographyFileDiscoveryService.discoverGeographyFiles();

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size()); // 1 país + 2 estados + 2 ciudades
        assertTrue(result.contains("data/geography/01_countries.sql"));
        assertTrue(result.contains("data/geography/colombia/02_states.sql"));
        assertTrue(result.contains("data/geography/mexico/02_states.sql"));
        assertTrue(result.stream().anyMatch(path -> path.contains("cities_colombia.sql")));
        assertTrue(result.stream().anyMatch(path -> path.contains("cities_mexico.sql")));
    }

    private Resource[] createMockResources(String... fileNames) {
        Resource[] resources = new Resource[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            resources[i] = createMockResource(fileNames[i], true);
        }
        return resources;
    }

    private Resource createMockResource(boolean exists) {
        return createMockResource("dummy", exists);
    }

    private Resource createMockResource(String fileName, boolean exists) {
        Resource mockResource = mock(Resource.class);
        when(mockResource.exists()).thenReturn(exists);
        when(mockResource.getFilename()).thenReturn(fileName);
        when(mockResource.getDescription()).thenReturn("file:" + fileName);
        // Stubbar getURI() para que extractRelativePath() funcione
        try {
            java.net.URI mockUri = new java.net.URI("file:/data/geography/colombia/" + fileName);
            when(mockResource.getURI()).thenReturn(mockUri);
        } catch (Exception e) {
            // Ignorar, no debería suceder
        }
        return mockResource;
    }
}
