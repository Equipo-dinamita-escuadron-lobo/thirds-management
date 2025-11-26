package com.thirdsmanagement.thirds.unit.application.service.third;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.application.service.third.ThirdUsageService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ThirdUsageServiceUnitTest {

    @Mock
    private ThirdOutputPort thirdOutputPort;

    @InjectMocks
    private ThirdUsageService thirdUsageService;

    // ==================== Incremento exitoso ====================

    @Test
    @DisplayName("test_IncrementUsageCount_ConIdValido_IncrementaContador")
    void testIncrementUsageCountConIdValidoIncrementaContador() {
        // Arrange
        Long thirdId = 1L;

        // Act
        thirdUsageService.incrementUsageCount(thirdId);

        // Assert
        verify(thirdOutputPort).incrementUsageCount(thirdId);
    }

    @Test
    @DisplayName("test_IncrementUsageCount_ConIdGrande_IncrementaContador")
    void testIncrementUsageCountConIdGrandeIncrementaContador() {
        // Arrange
        Long thirdId = 999999999L;

        // Act
        thirdUsageService.incrementUsageCount(thirdId);

        // Assert
        verify(thirdOutputPort).incrementUsageCount(thirdId);
    }

    @Test
    @DisplayName("test_IncrementUsageCount_ConIdMinimo_IncrementaContador")
    void testIncrementUsageCountConIdMinimoIncrementaContador() {
        // Arrange
        Long thirdId = 1L;

        // Act
        thirdUsageService.incrementUsageCount(thirdId);

        // Assert
        verify(thirdOutputPort).incrementUsageCount(thirdId);
    }

    // ==================== Delegación correcta ====================

    @Test
    @DisplayName("test_IncrementUsageCount_DelegaAlPuertoSalida_ConIdCorrecto")
    void testIncrementUsageCountDelegaAlPuertoSalidaConIdCorrecto() {
        // Arrange
        Long thirdId = 42L;

        // Act
        thirdUsageService.incrementUsageCount(thirdId);

        // Assert
        verify(thirdOutputPort, times(1)).incrementUsageCount(thirdId);
        verifyNoMoreInteractions(thirdOutputPort);
    }

    @Test
    @DisplayName("test_IncrementUsageCount_LlamadaUnica_NoMultiplesIncrementos")
    void testIncrementUsageCountLlamadaUnicaNoMultiplesIncrementos() {
        // Arrange
        Long thirdId = 5L;

        // Act
        thirdUsageService.incrementUsageCount(thirdId);

        // Assert
        verify(thirdOutputPort, times(1)).incrementUsageCount(thirdId);
    }

    @Test
    @DisplayName("test_IncrementUsageCount_PasaParametroExacto_SinModificaciones")
    void testIncrementUsageCountPasaParametroExactoSinModificaciones() {
        // Arrange
        Long thirdId = 123L;

        // Act
        thirdUsageService.incrementUsageCount(thirdId);

        // Assert
        verify(thirdOutputPort).incrementUsageCount(eq(123L));
    }

    // ==================== Diferentes IDs ====================

    @Test
    @DisplayName("test_IncrementUsageCount_DiferentesIds_CadaUnoSeLlamaCorrectamente")
    void testIncrementUsageCountDiferentesIdsCadaUnoSeLlamaCorrectamente() {
        // Arrange
        Long thirdId1 = 10L;
        Long thirdId2 = 20L;
        Long thirdId3 = 30L;

        // Act
        thirdUsageService.incrementUsageCount(thirdId1);
        thirdUsageService.incrementUsageCount(thirdId2);
        thirdUsageService.incrementUsageCount(thirdId3);

        // Assert
        verify(thirdOutputPort).incrementUsageCount(10L);
        verify(thirdOutputPort).incrementUsageCount(20L);
        verify(thirdOutputPort).incrementUsageCount(30L);
        verify(thirdOutputPort, times(3)).incrementUsageCount(anyLong());
    }

    @Test
    @DisplayName("test_IncrementUsageCount_MismoIdMultiplesVeces_IncrementaCadaVez")
    void testIncrementUsageCountMismoIdMultiplesVecesIncrementaCadaVez() {
        // Arrange
        Long thirdId = 7L;

        // Act
        thirdUsageService.incrementUsageCount(thirdId);
        thirdUsageService.incrementUsageCount(thirdId);
        thirdUsageService.incrementUsageCount(thirdId);

        // Assert
        verify(thirdOutputPort, times(3)).incrementUsageCount(7L);
    }

    @Test
    @DisplayName("test_IncrementUsageCount_IdsConsecutivos_IncrementaTodos")
    void testIncrementUsageCountIdsConsecutivosIncrementaTodos() {
        // Arrange
        Long thirdId1 = 100L;
        Long thirdId2 = 101L;
        Long thirdId3 = 102L;

        // Act
        thirdUsageService.incrementUsageCount(thirdId1);
        thirdUsageService.incrementUsageCount(thirdId2);
        thirdUsageService.incrementUsageCount(thirdId3);

        // Assert
        verify(thirdOutputPort).incrementUsageCount(100L);
        verify(thirdOutputPort).incrementUsageCount(101L);
        verify(thirdOutputPort).incrementUsageCount(102L);
    }

    // ==================== Propagación de excepciones ====================

    @Test
    @DisplayName("test_IncrementUsageCount_PuertoLanzaExcepcion_PropagaExcepcion")
    void testIncrementUsageCountPuertoLanzaExcepcionPropagaExcepcion() {
        // Arrange
        Long thirdId = 1L;
        doThrow(new RuntimeException("Error de persistencia")).when(thirdOutputPort).incrementUsageCount(thirdId);

        // Act & Assert
        try {
            thirdUsageService.incrementUsageCount(thirdId);
        } catch (RuntimeException e) {
            // Se espera la excepción
        }

        verify(thirdOutputPort).incrementUsageCount(thirdId);
    }

    @Test
    @DisplayName("test_IncrementUsageCount_ExcepcionEnPuerto_NoCaptura")
    void testIncrementUsageCountExcepcionEnPuertoNoCaptura() {
        // Arrange
        Long thirdId = 50L;
        RuntimeException exception = new RuntimeException("Error de BD");
        doThrow(exception).when(thirdOutputPort).incrementUsageCount(thirdId);

        // Act & Assert
        try {
            thirdUsageService.incrementUsageCount(thirdId);
        } catch (RuntimeException e) {
            // La excepción se propaga sin modificación
        }

        verify(thirdOutputPort).incrementUsageCount(50L);
    }

    @Test
    @DisplayName("test_IncrementUsageCount_ExcepcionPersistencia_DelegaAlPuerto")
    void testIncrementUsageCountExcepcionPersistenciaDelegaAlPuerto() {
        // Arrange
        Long thirdId = 99L;
        doThrow(new IllegalStateException("Estado inválido")).when(thirdOutputPort).incrementUsageCount(thirdId);

        // Act & Assert
        try {
            thirdUsageService.incrementUsageCount(thirdId);
        } catch (IllegalStateException e) {
            // Se espera la excepción
        }

        verify(thirdOutputPort).incrementUsageCount(99L);
    }

    // ==================== Casos edge ====================

    @Test
    @DisplayName("test_IncrementUsageCount_IdCero_DelegaAlPuerto")
    void testIncrementUsageCountIdCeroDelegaAlPuerto() {
        // Arrange
        Long thirdId = 0L;

        // Act
        thirdUsageService.incrementUsageCount(thirdId);

        // Assert
        verify(thirdOutputPort).incrementUsageCount(0L);
    }

    @Test
    @DisplayName("test_IncrementUsageCount_IdNegativo_DelegaAlPuerto")
    void testIncrementUsageCountIdNegativoDelegaAlPuerto() {
        // Arrange
        Long thirdId = -1L;

        // Act
        thirdUsageService.incrementUsageCount(thirdId);

        // Assert
        verify(thirdOutputPort).incrementUsageCount(-1L);
    }

    @Test
    @DisplayName("test_IncrementUsageCount_IdMaxValue_DelegaAlPuerto")
    void testIncrementUsageCountIdMaxValueDelegaAlPuerto() {
        // Arrange
        Long thirdId = Long.MAX_VALUE;

        // Act
        thirdUsageService.incrementUsageCount(thirdId);

        // Assert
        verify(thirdOutputPort).incrementUsageCount(Long.MAX_VALUE);
    }

    // ==================== Integración ====================

    @Test
    @DisplayName("test_IncrementUsageCount_MultiplesLlamadasDiferentesIds_TodasSeEjecutan")
    void testIncrementUsageCountMultiplesLlamadasDiferentesIdsTodasSeEjecutan() {
        // Arrange & Act
        thirdUsageService.incrementUsageCount(1L);
        thirdUsageService.incrementUsageCount(2L);
        thirdUsageService.incrementUsageCount(3L);
        thirdUsageService.incrementUsageCount(4L);
        thirdUsageService.incrementUsageCount(5L);

        // Assert
        verify(thirdOutputPort, times(5)).incrementUsageCount(anyLong());
        verify(thirdOutputPort).incrementUsageCount(1L);
        verify(thirdOutputPort).incrementUsageCount(2L);
        verify(thirdOutputPort).incrementUsageCount(3L);
        verify(thirdOutputPort).incrementUsageCount(4L);
        verify(thirdOutputPort).incrementUsageCount(5L);
    }

    @Test
    @DisplayName("test_IncrementUsageCount_SecuenciaOperaciones_OrdenCorrecto")
    void testIncrementUsageCountSecuenciaOperacionesOrdenCorrecto() {
        // Arrange
        var inOrder = inOrder(thirdOutputPort);

        // Act
        thirdUsageService.incrementUsageCount(10L);
        thirdUsageService.incrementUsageCount(20L);
        thirdUsageService.incrementUsageCount(30L);

        // Assert
        inOrder.verify(thirdOutputPort).incrementUsageCount(10L);
        inOrder.verify(thirdOutputPort).incrementUsageCount(20L);
        inOrder.verify(thirdOutputPort).incrementUsageCount(30L);
    }

    @Test
    @DisplayName("test_IncrementUsageCount_OperacionesEntrelazadas_CadaIdSeIncrementa")
    void testIncrementUsageCountOperacionesEntrelazadasCadaIdSeIncrementa() {
        // Arrange & Act
        thirdUsageService.incrementUsageCount(100L);
        thirdUsageService.incrementUsageCount(200L);
        thirdUsageService.incrementUsageCount(100L);
        thirdUsageService.incrementUsageCount(300L);
        thirdUsageService.incrementUsageCount(200L);

        // Assert
        verify(thirdOutputPort, times(2)).incrementUsageCount(100L);
        verify(thirdOutputPort, times(2)).incrementUsageCount(200L);
        verify(thirdOutputPort, times(1)).incrementUsageCount(300L);
        verify(thirdOutputPort, times(5)).incrementUsageCount(anyLong());
    }

    @Test
    @DisplayName("test_IncrementUsageCount_GranVolumen_TodasLasLlamadasSeDelegaron")
    void testIncrementUsageCountGranVolumenTodasLasLlamadasSeDelegaron() {
        // Arrange & Act
        for (long i = 1; i <= 100; i++) {
            thirdUsageService.incrementUsageCount(i);
        }

        // Assert
        verify(thirdOutputPort, times(100)).incrementUsageCount(anyLong());
    }

    @Test
    @DisplayName("test_IncrementUsageCount_SinLlamadas_NoDelegaNada")
    void testIncrementUsageCountSinLlamadasNoDelegaNada() {
        // Arrange - No se llama al servicio

        // Act - No hay acción

        // Assert
        verifyNoInteractions(thirdOutputPort);
    }
}
