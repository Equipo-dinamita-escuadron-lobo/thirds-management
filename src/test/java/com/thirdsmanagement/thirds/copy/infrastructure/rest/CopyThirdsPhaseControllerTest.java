package com.thirdsmanagement.thirds.copy.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirdsmanagement.thirds.copy.application.input.ICancelThirdsCopyPort;
import com.thirdsmanagement.thirds.copy.application.input.ICleanupThirdsCopyPort;
import com.thirdsmanagement.thirds.copy.application.input.IExecuteThirdsCopyPhasePort;
import com.thirdsmanagement.thirds.copy.application.input.IGetThirdsCopyStatusPort;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.controller.CopyThirdsContractController;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.controller.CopyThirdsPhaseController;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyCancelResponseDto;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyPhaseRequestDto;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyPhaseResponseDto;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyStatusResponseDto;
import com.thirdsmanagement.thirds.infrastructure.multitenancy.interceptor.TenantInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de la capa REST del bounded context copy de thirds-management.
 * Cubre: CopyThirdsPhaseController (POST /phase)
 *        CopyThirdsContractController (GET /{id}/status, POST /{id}/cancel, DELETE /{id}/cleanup)
 */
@WebMvcTest(controllers = {
        CopyThirdsPhaseController.class,
        CopyThirdsContractController.class
})
@ActiveProfiles("test")
class CopyThirdsPhaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IExecuteThirdsCopyPhasePort executePort;

    @MockBean
    private IGetThirdsCopyStatusPort statusPort;

    @MockBean
    private ICancelThirdsCopyPort cancelPort;

    @MockBean
    private ICleanupThirdsCopyPort cleanupPort;

    @MockBean
    private TenantInterceptor tenantInterceptor;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void postPhaseDebeRetornar200ConEstadoCompletado() throws Exception {
        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(UUID.randomUUID())
                .fase(2)
                .entOrigen("emp-A")
                .entDestino("emp-B")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(Collections.emptyList())
                .build();

        CopyPhaseResponseDto respuesta = CopyPhaseResponseDto.builder()
                .estado("COMPLETADO")
                .registrosProcesados(3)
                .equivalenciasGeneradas(Collections.emptyList())
                .mensaje("Copia de terceros completada")
                .advertencias(Collections.emptyList())
                .build();

        when(executePort.ejecutar(any())).thenReturn(respuesta);

        mockMvc.perform(post("/api/thirds/copy/phase").with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADO"))
                .andExpect(jsonPath("$.registrosProcesados").value(3));
    }

    @Test
    void postPhaseIdempotenciaDebeRetornar200ConResultadoPrevio() throws Exception {
        UUID idProceso = UUID.randomUUID();
        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(idProceso)
                .fase(2)
                .entOrigen("emp-A")
                .entDestino("emp-B")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(Collections.emptyList())
                .build();

        CopyPhaseResponseDto respuesta = CopyPhaseResponseDto.builder()
                .estado("COMPLETADO")
                .registrosProcesados(3)
                .equivalenciasGeneradas(Collections.emptyList())
                .mensaje("Resultado previo reutilizado")
                .advertencias(Collections.emptyList())
                .build();

        when(executePort.ejecutar(any())).thenReturn(respuesta);

        mockMvc.perform(post("/api/thirds/copy/phase").with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));
    }

    @Test
    void getStatusDebeRetornar200ConEstadoDelProceso() throws Exception {
        String idProceso = UUID.randomUUID().toString();

        CopyStatusResponseDto respuesta = CopyStatusResponseDto.builder()
                .idProceso(idProceso)
                .estado("COMPLETADO")
                .fase(2)
                .modulo("thirds")
                .registrosProcesados(5)
                .equivalenciasGeneradas(5)
                .build();

        when(statusPort.obtenerEstado(anyString())).thenReturn(respuesta);

        mockMvc.perform(get("/api/thirds/copy/" + idProceso + "/status").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADO"))
                .andExpect(jsonPath("$.modulo").value("thirds"));
    }

    @Test
    void postCancelDebeRetornar200ConEstadoCancelado() throws Exception {
        String idProceso = UUID.randomUUID().toString();

        CopyCancelResponseDto respuesta = CopyCancelResponseDto.builder()
                .idProceso(idProceso)
                .estado("CANCELADO")
                .mensaje("Proceso de copia cancelado")
                .build();

        when(cancelPort.cancelar(anyString())).thenReturn(respuesta);

        mockMvc.perform(post("/api/thirds/copy/" + idProceso + "/cancel").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));
    }

    @Test
    void deleteCleanupDebeRetornar204() throws Exception {
        String idProceso = UUID.randomUUID().toString();

        mockMvc.perform(delete("/api/thirds/copy/" + idProceso + "/cleanup").with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void postPhaseConEquivalenciasPrevVacioNoProduzcoError() throws Exception {
        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(UUID.randomUUID())
                .fase(2)
                .entOrigen("emp-A")
                .entDestino("emp-B")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(null)
                .build();

        CopyPhaseResponseDto respuesta = CopyPhaseResponseDto.builder()
                .estado("COMPLETADO")
                .registrosProcesados(0)
                .equivalenciasGeneradas(Collections.emptyList())
                .mensaje("Sin datos para copiar")
                .advertencias(Collections.emptyList())
                .build();

        when(executePort.ejecutar(any())).thenReturn(respuesta);

        mockMvc.perform(post("/api/thirds/copy/phase").with(jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));
    }
}
