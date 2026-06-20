package com.thirdsmanagement.thirds.unit.infrastructure.adapters.output.persistence.messageBroker.aspect;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.messageBroker.aspect.JwtTokenService;
import com.thirdsmanagement.thirds.infrastructure.security.IJwtUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtTokenServiceUnitTest {

    @Mock
    private IJwtUtils jwtUtils;

    @InjectMocks
    private JwtTokenService jwtTokenService;

    @AfterEach
    void tearDown() {
        jwtTokenService.clearRabbitContext();
    }

    @Test
    @DisplayName("Debe establecer token JWT para contexto RabbitMQ")
    void testSetRabbitJwtToken() {
        // Arrange
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";

        // Act
        jwtTokenService.setRabbitJwtToken(token);

        // Assert
        assertTrue(jwtTokenService.isInRabbitContext());
    }

    @Test
    @DisplayName("Debe establecer tenant ID para contexto RabbitMQ")
    void testSetRabbitTenantId() {
        // Arrange
        String tenantId = "TENANT123";

        // Act
        jwtTokenService.setRabbitTenantId(tenantId);

        // Assert
        assertDoesNotThrow(() -> jwtTokenService.getTenantId());
    }

    @Test
    @DisplayName("Debe obtener token desde contexto RabbitMQ cuando está disponible")
    void testGetTokenFromRabbitContext() {
        // Arrange
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.rabbit";
        jwtTokenService.setRabbitJwtToken(expectedToken);

        // Act
        String actualToken = jwtTokenService.getToken();

        // Assert
        assertEquals(expectedToken, actualToken);
        verify(jwtUtils, never()).getToken();
    }

    @Test
    @DisplayName("Debe obtener token desde contexto HTTP cuando RabbitMQ no está disponible")
    void testGetTokenFromHttpContext() {
        // Arrange
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.http";
        when(jwtUtils.getToken()).thenReturn(expectedToken);

        // Act
        String actualToken = jwtTokenService.getToken();

        // Assert
        assertEquals(expectedToken, actualToken);
        verify(jwtUtils).getToken();
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no hay token disponible en ningún contexto")
    void testGetTokenThrowsExceptionWhenNoContextAvailable() {
        // Arrange
        when(jwtUtils.getToken()).thenThrow(new RuntimeException("No token available"));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> jwtTokenService.getToken());
        
        assertTrue(exception.getMessage().contains("No hay token JWT disponible"));
    }

    @Test
    @DisplayName("Debe obtener tenant ID desde contexto RabbitMQ cuando está disponible")
    void testGetTenantIdFromRabbitContext() {
        // Arrange
        String expectedTenantId = "RABBIT_TENANT";
        jwtTokenService.setRabbitTenantId(expectedTenantId);

        // Act
        String actualTenantId = jwtTokenService.getTenantId();

        // Assert
        assertEquals(expectedTenantId, actualTenantId);
        verify(jwtUtils, never()).getId();
    }

    @Test
    @DisplayName("Debe obtener tenant ID desde contexto HTTP cuando RabbitMQ no está disponible")
    void testGetTenantIdFromHttpContext() {
        // Arrange
        String expectedTenantId = "HTTP_TENANT";
        when(jwtUtils.getId()).thenReturn(expectedTenantId);

        // Act
        String actualTenantId = jwtTokenService.getTenantId();

        // Assert
        assertEquals(expectedTenantId, actualTenantId);
        verify(jwtUtils).getId();
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no hay tenant ID disponible en ningún contexto")
    void testGetTenantIdThrowsExceptionWhenNoContextAvailable() {
        // Arrange
        when(jwtUtils.getId()).thenThrow(new RuntimeException("No tenant ID available"));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> jwtTokenService.getTenantId());
        
        assertTrue(exception.getMessage().contains("No hay tenant ID disponible"));
    }

    @Test
    @DisplayName("Debe limpiar contexto RabbitMQ correctamente")
    void testClearRabbitContext() {
        // Arrange
        jwtTokenService.setRabbitJwtToken("token123");
        jwtTokenService.setRabbitTenantId("tenant456");
        assertTrue(jwtTokenService.isInRabbitContext());

        // Act
        jwtTokenService.clearRabbitContext();

        // Assert
        assertFalse(jwtTokenService.isInRabbitContext());
    }

    @Test
    @DisplayName("Debe retornar false cuando no está en contexto RabbitMQ")
    void testIsInRabbitContextReturnsFalseWhenNotSet() {
        // Act
        boolean result = jwtTokenService.isInRabbitContext();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar true cuando está en contexto RabbitMQ")
    void testIsInRabbitContextReturnsTrueWhenSet() {
        // Arrange
        jwtTokenService.setRabbitJwtToken("token");

        // Act
        boolean result = jwtTokenService.isInRabbitContext();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe priorizar contexto RabbitMQ sobre HTTP para token")
    void testRabbitContextHasPriorityOverHttpForToken() {
        // Arrange
        String rabbitToken = "rabbit_token";
        String httpToken = "http_token";
        
        jwtTokenService.setRabbitJwtToken(rabbitToken);
        when(jwtUtils.getToken()).thenReturn(httpToken);

        // Act
        String token = jwtTokenService.getToken();

        // Assert
        assertEquals(rabbitToken, token);
        verify(jwtUtils, never()).getToken();
    }

    @Test
    @DisplayName("Debe priorizar contexto RabbitMQ sobre HTTP para tenant ID")
    void testRabbitContextHasPriorityOverHttpForTenantId() {
        // Arrange
        String rabbitTenantId = "rabbit_tenant";
        String httpTenantId = "http_tenant";
        
        jwtTokenService.setRabbitTenantId(rabbitTenantId);
        when(jwtUtils.getId()).thenReturn(httpTenantId);

        // Act
        String tenantId = jwtTokenService.getTenantId();

        // Assert
        assertEquals(rabbitTenantId, tenantId);
        verify(jwtUtils, never()).getId();
    }

    @Test
    @DisplayName("Debe manejar limpieza múltiple de contexto sin errores")
    void testMultipleClearRabbitContextCalls() {
        // Arrange
        jwtTokenService.setRabbitJwtToken("token");
        jwtTokenService.setRabbitTenantId("tenant");

        // Act & Assert
        assertDoesNotThrow(() -> {
            jwtTokenService.clearRabbitContext();
            jwtTokenService.clearRabbitContext();
            jwtTokenService.clearRabbitContext();
        });
        
        assertFalse(jwtTokenService.isInRabbitContext());
    }

    @Test
    @DisplayName("Debe establecer solo token sin tenant ID")
    void testSetOnlyTokenWithoutTenantId() {
        // Arrange
        String token = "token_only";
        when(jwtUtils.getId()).thenReturn("http_tenant");

        // Act
        jwtTokenService.setRabbitJwtToken(token);

        // Assert
        assertTrue(jwtTokenService.isInRabbitContext());
        assertEquals(token, jwtTokenService.getToken());
        assertEquals("http_tenant", jwtTokenService.getTenantId());
    }

    @Test
    @DisplayName("Debe establecer solo tenant ID sin token")
    void testSetOnlyTenantIdWithoutToken() {
        // Arrange
        String tenantId = "tenant_only";
        when(jwtUtils.getToken()).thenReturn("http_token");

        // Act
        jwtTokenService.setRabbitTenantId(tenantId);

        // Assert
        assertFalse(jwtTokenService.isInRabbitContext());
        assertEquals("http_token", jwtTokenService.getToken());
        assertEquals(tenantId, jwtTokenService.getTenantId());
    }
}
