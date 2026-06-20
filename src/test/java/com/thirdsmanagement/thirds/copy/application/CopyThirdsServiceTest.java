package com.thirdsmanagement.thirds.copy.application;

import com.thirdsmanagement.thirds.copy.application.output.ICopyJobLogRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.IThirdSourceRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.IThirdTargetRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.IThirdTypeSourceRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.IThirdTypeTargetRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.IThirdsAndTypesSourceRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.IThirdsAndTypesTargetRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.ITypeIdSourceRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.output.ITypeIdTargetRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.services.CopyThirdsService;
import com.thirdsmanagement.thirds.copy.domain.enums.CopyEstado;
import com.thirdsmanagement.thirds.copy.domain.models.CopyJobLog;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyEquivalenciaDto;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyPhaseRequestDto;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyPhaseResponseDto;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdsAndTypesEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para CopyThirdsService.
 */
@ExtendWith(MockitoExtension.class)
class CopyThirdsServiceTest {

    @Mock
    private ICopyJobLogRepositoryPort logRepo;

    @Mock
    private ITypeIdSourceRepositoryPort typeIdSource;

    @Mock
    private ITypeIdTargetRepositoryPort typeIdTarget;

    @Mock
    private IThirdTypeSourceRepositoryPort thirdTypeSource;

    @Mock
    private IThirdTypeTargetRepositoryPort thirdTypeTarget;

    @Mock
    private IThirdSourceRepositoryPort thirdSource;

    @Mock
    private IThirdTargetRepositoryPort thirdTarget;

    @Mock
    private IThirdsAndTypesSourceRepositoryPort thirdsAndTypesSource;

    @Mock
    private IThirdsAndTypesTargetRepositoryPort thirdsAndTypesTarget;

    @InjectMocks
    private CopyThirdsService service;

    @Test
    void ejecutarConDatosDebeRetornarCompletado() {
        UUID idProceso = UUID.randomUUID();
        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(idProceso)
                .fase(2)
                .entOrigen("emp-A")
                .entDestino("emp-B")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(Collections.emptyList())
                .build();

        // Repositorios devuelven una entidad cada uno
        TypeIdEntity tiOrigen = TypeIdEntity.builder().id(1L).tiId("CC").tiName("Cedula").tientId("emp-A").build();
        TypeIdEntity tiGuardado = TypeIdEntity.builder().id(10L).tiId("CC").tiName("Cedula").tientId("emp-B").build();
        ThirdTypeEntity ttOrigen = ThirdTypeEntity.builder().ttId(2L).ttName("Cliente").ttentId("emp-A").build();
        ThirdTypeEntity ttGuardado = ThirdTypeEntity.builder().ttId(20L).ttName("Cliente").ttentId("emp-B").build();
        ThirdEntity thOrigen = ThirdEntity.builder().thId(3L).entId("emp-A").names("Juan").typeId(tiOrigen).build();
        ThirdEntity thGuardado = ThirdEntity.builder().thId(30L).entId("emp-B").names("Juan").typeId(tiGuardado).build();

        when(logRepo.buscarPorIdProcesoYFase(anyString(), anyInt())).thenReturn(Optional.empty());
        when(logRepo.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(typeIdSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(List.of(tiOrigen));
        when(typeIdTarget.guardar(any())).thenReturn(tiGuardado);
        when(thirdTypeSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(List.of(ttOrigen));
        when(thirdTypeTarget.guardar(any())).thenReturn(ttGuardado);
        when(thirdSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(List.of(thOrigen));
        when(thirdTarget.guardar(any())).thenReturn(thGuardado);
        when(thirdsAndTypesSource.obtenerPorEmpresa(anyString())).thenReturn(Collections.emptyList());

        CopyPhaseResponseDto response = service.ejecutar(request);

        assertThat(response.getEstado()).isIn("COMPLETADO", "COMPLETADO_CON_ADVERTENCIAS");
        assertThat(response.getRegistrosProcesados()).isGreaterThan(0);
    }

    @Test
    void ejecutarSinDatosDebeRetornarCompletado() {
        UUID idProceso = UUID.randomUUID();
        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(idProceso)
                .fase(2)
                .entOrigen("emp-A")
                .entDestino("emp-B")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(Collections.emptyList())
                .build();

        when(logRepo.buscarPorIdProcesoYFase(anyString(), anyInt())).thenReturn(Optional.empty());
        when(logRepo.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(typeIdSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(Collections.emptyList());
        when(thirdTypeSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(Collections.emptyList());
        when(thirdSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(Collections.emptyList());
        when(thirdsAndTypesSource.obtenerPorEmpresa(anyString())).thenReturn(Collections.emptyList());

        CopyPhaseResponseDto response = service.ejecutar(request);

        assertThat(response.getEstado()).isEqualTo("COMPLETADO");
        assertThat(response.getRegistrosProcesados()).isZero();
    }

    @Test
    void ejecutarIdempotenciaDebeRetornarResultadoPrevio() {
        UUID idProceso = UUID.randomUUID();
        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(idProceso)
                .fase(2)
                .entOrigen("emp-A")
                .entDestino("emp-B")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(Collections.emptyList())
                .build();

        CopyJobLog logPrevio = CopyJobLog.builder()
                .idProceso(idProceso)
                .fase(2)
                .modulo("thirds")
                .estado(CopyEstado.COMPLETADO)
                .registrosProcesados(5)
                .equivalenciasGeneradas(5)
                .fechaInicio(Instant.now())
                .build();

        when(logRepo.buscarPorIdProcesoYFase(anyString(), anyInt())).thenReturn(Optional.of(logPrevio));

        CopyPhaseResponseDto response = service.ejecutar(request);

        assertThat(response.getEstado()).isEqualTo("COMPLETADO");
        assertThat(response.getRegistrosProcesados()).isEqualTo(5);
    }

    @Test
    void ejecutarDebeRechazarEntOrigenIgualAEntDestino() {
        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(UUID.randomUUID())
                .fase(2)
                .entOrigen("emp-A")
                .entDestino("emp-A")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(Collections.emptyList())
                .build();

        assertThatThrownBy(() -> service.ejecutar(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void ejecutarDebeRemapearFkTypeIdEnThird() {
        UUID idProceso = UUID.randomUUID();
        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(idProceso)
                .fase(2)
                .entOrigen("emp-A")
                .entDestino("emp-B")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(Collections.emptyList())
                .build();

        TypeIdEntity tiOrigen = TypeIdEntity.builder().id(7L).tiId("CC").tiName("Cedula").tientId("emp-A").build();
        TypeIdEntity tiGuardado = TypeIdEntity.builder().id(70L).tiId("CC").tiName("Cedula").tientId("emp-B").build();
        ThirdEntity thOrigen = ThirdEntity.builder()
                .thId(3L).entId("emp-A").names("Juan")
                .typeId(tiOrigen)
                .build();
        ThirdEntity thGuardado = ThirdEntity.builder().thId(30L).entId("emp-B").names("Juan").build();

        when(logRepo.buscarPorIdProcesoYFase(anyString(), anyInt())).thenReturn(Optional.empty());
        when(logRepo.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(typeIdSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(List.of(tiOrigen));
        when(typeIdTarget.guardar(any())).thenReturn(tiGuardado);
        when(thirdTypeSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(Collections.emptyList());
        when(thirdSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(List.of(thOrigen));
        when(thirdTarget.guardar(any())).thenAnswer(inv -> {
            ThirdEntity e = inv.getArgument(0);
            // Verificar que el typeId fue remapeado al nuevo ID
            assertThat(e.getTypeId()).isNotNull();
            assertThat(e.getTypeId().getId()).isEqualTo(70L);
            return thGuardado;
        });
        when(thirdsAndTypesSource.obtenerPorEmpresa(anyString())).thenReturn(Collections.emptyList());

        service.ejecutar(request);

        verify(thirdTarget).guardar(any());
    }

    @Test
    void ejecutarEquivalenciasPrevVacioNoDebeProducirError() {
        UUID idProceso = UUID.randomUUID();
        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(idProceso)
                .fase(2)
                .entOrigen("emp-A")
                .entDestino("emp-B")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(null) // null explícito en lugar de lista vacía
                .build();

        when(logRepo.buscarPorIdProcesoYFase(anyString(), anyInt())).thenReturn(Optional.empty());
        when(logRepo.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(typeIdSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(Collections.emptyList());
        when(thirdTypeSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(Collections.emptyList());
        when(thirdSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(Collections.emptyList());
        when(thirdsAndTypesSource.obtenerPorEmpresa(anyString())).thenReturn(Collections.emptyList());

        // No debe lanzar excepción con equivalenciasPrev null
        CopyPhaseResponseDto response = service.ejecutar(request);

        assertThat(response.getEstado()).isEqualTo("COMPLETADO");
    }

    @Test
    void ejecutarDebeGuardarLogAlInicioyAlFinalizar() {
        UUID idProceso = UUID.randomUUID();
        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(idProceso)
                .fase(2)
                .entOrigen("emp-A")
                .entDestino("emp-B")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(Collections.emptyList())
                .build();

        when(logRepo.buscarPorIdProcesoYFase(anyString(), anyInt())).thenReturn(Optional.empty());
        when(logRepo.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(typeIdSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(Collections.emptyList());
        when(thirdTypeSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(Collections.emptyList());
        when(thirdSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(Collections.emptyList());
        when(thirdsAndTypesSource.obtenerPorEmpresa(anyString())).thenReturn(Collections.emptyList());

        service.ejecutar(request);

        // Dos llamadas a guardar: inicio (EN_PROCESO) y fin (COMPLETADO)
        verify(logRepo, org.mockito.Mockito.times(2)).guardar(any(CopyJobLog.class));
    }

    @Test
    void ejecutarDebeRemapearAmbasFKsEnThirdsAndTypes() {
        UUID idProceso = UUID.randomUUID();
        CopyPhaseRequestDto request = CopyPhaseRequestDto.builder()
                .idProceso(idProceso)
                .fase(2)
                .entOrigen("emp-A")
                .entDestino("emp-B")
                .snapshotCorte(Instant.now())
                .equivalenciasPrev(Collections.emptyList())
                .build();

        TypeIdEntity tiGuardado = TypeIdEntity.builder().id(10L).tiId("CC").tiName("Cedula").tientId("emp-B").build();
        ThirdTypeEntity ttGuardado = ThirdTypeEntity.builder().ttId(20L).ttName("Cliente").ttentId("emp-B").build();
        ThirdEntity thOrigen = ThirdEntity.builder().thId(3L).entId("emp-A").names("Juan").build();
        ThirdEntity thGuardado = ThirdEntity.builder().thId(30L).entId("emp-B").names("Juan").build();

        // Relación M:M original: thId=3, ttId=2
        ThirdsAndTypesEntity tatOrigen = ThirdsAndTypesEntity.builder().thId(3L).ttId(2L).build();

        when(logRepo.buscarPorIdProcesoYFase(anyString(), anyInt())).thenReturn(Optional.empty());
        when(logRepo.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(typeIdSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(Collections.emptyList());
        when(thirdTypeSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(
                List.of(ThirdTypeEntity.builder().ttId(2L).ttName("Cliente").ttentId("emp-A").build()));
        when(thirdTypeTarget.guardar(any())).thenReturn(ttGuardado);
        when(thirdSource.obtenerPorEmpresaYCorte(anyString(), any())).thenReturn(List.of(thOrigen));
        when(thirdTarget.guardar(any())).thenReturn(thGuardado);
        when(thirdsAndTypesSource.obtenerPorEmpresa(anyString())).thenReturn(List.of(tatOrigen));
        when(thirdsAndTypesTarget.guardar(any())).thenAnswer(inv -> {
            ThirdsAndTypesEntity tat = inv.getArgument(0);
            // Verificar que thId fue remapeado de 3→30 y ttId de 2→20
            assertThat(tat.getThId()).isEqualTo(30L);
            assertThat(tat.getTtId()).isEqualTo(20L);
            return tat;
        });

        service.ejecutar(request);

        verify(thirdsAndTypesTarget).guardar(any());
    }
}
