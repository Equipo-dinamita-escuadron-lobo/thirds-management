package com.thirdsmanagement.thirds.copy.infrastructure.persistence;

import com.thirdsmanagement.thirds.copy.domain.enums.CopyEstado;
import com.thirdsmanagement.thirds.copy.domain.models.CopyJobLog;
import com.thirdsmanagement.thirds.copy.application.output.ICopyJobLogRepositoryPort;
import com.thirdsmanagement.thirds.infrastructure.config.GeographyDataInitializer;
import com.thirdsmanagement.thirds.infrastructure.multitenancy.utils.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de persistencia para el repositorio del log de idempotencia de copia.
 * Usa @SpringBootTest por la configuración multi-tenant de thirds-management.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CopyJobLogPersistenceTest {

    /** Mock del JwtDecoder de Spring Security para evitar llamada remota a Keycloak en tests */
    @MockBean
    private JwtDecoder jwtDecoder;

    /** Mock del GeographyDataInitializer para evitar ejecución de SQL PostgreSQL-específico en H2 */
    @MockBean
    private GeographyDataInitializer geographyDataInitializer;

    @Autowired
    private ICopyJobLogRepositoryPort logRepo;

    @BeforeEach
    void configurarTenant() {
        TenantContext.setTenantId("test-tenant");
    }

    @AfterEach
    void limpiarTenant() {
        TenantContext.clear();
    }

    @Test
    void guardarYRecuperarLogDebeRetornarMismosValores() {
        UUID idProceso = UUID.randomUUID();
        CopyJobLog log = CopyJobLog.builder()
                .idProceso(idProceso)
                .fase(2)
                .modulo("thirds")
                .estado(CopyEstado.EN_PROCESO)
                .fechaInicio(Instant.now())
                .build();

        CopyJobLog guardado = logRepo.guardar(log);

        assertThat(guardado.getIdProceso()).isEqualTo(idProceso);
        assertThat(guardado.getEstado()).isEqualTo(CopyEstado.EN_PROCESO);
        assertThat(guardado.getFase()).isEqualTo(2);
        assertThat(guardado.getModulo()).isEqualTo("thirds");
    }

    @Test
    void buscarPorIdProcesoYFaseDebeEncontrarLogGuardado() {
        UUID idProceso = UUID.randomUUID();
        CopyJobLog log = CopyJobLog.builder()
                .idProceso(idProceso)
                .fase(2)
                .modulo("thirds")
                .estado(CopyEstado.COMPLETADO)
                .registrosProcesados(10)
                .equivalenciasGeneradas(10)
                .fechaInicio(Instant.now())
                .build();

        logRepo.guardar(log);

        Optional<CopyJobLog> encontrado = logRepo.buscarPorIdProcesoYFase(idProceso.toString(), 2);

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEstado()).isEqualTo(CopyEstado.COMPLETADO);
        assertThat(encontrado.get().getRegistrosProcesados()).isEqualTo(10);
    }

    @Test
    void buscarProcesoInexistenteDebeRetornarVacio() {
        Optional<CopyJobLog> resultado = logRepo.buscarPorIdProceso("proceso-inexistente");

        assertThat(resultado).isEmpty();
    }

    @Test
    void eliminarPorIdProcesoDebeEliminarTodosLosLogsDelProceso() {
        UUID idProceso = UUID.randomUUID();
        CopyJobLog log = CopyJobLog.builder()
                .idProceso(idProceso)
                .fase(2)
                .modulo("thirds")
                .estado(CopyEstado.COMPLETADO)
                .fechaInicio(Instant.now())
                .build();

        logRepo.guardar(log);
        logRepo.eliminarPorIdProceso(idProceso.toString());

        Optional<CopyJobLog> resultado = logRepo.buscarPorIdProceso(idProceso.toString());
        assertThat(resultado).isEmpty();
    }
}
