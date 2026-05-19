package com.thirdsmanagement.thirds.copy.application;

import com.thirdsmanagement.thirds.copy.application.output.ICopyJobLogRepositoryPort;
import com.thirdsmanagement.thirds.copy.application.services.CancelThirdsCopyService;
import com.thirdsmanagement.thirds.copy.domain.enums.CopyEstado;
import com.thirdsmanagement.thirds.copy.domain.exceptions.DuplicateCopyJobException;
import com.thirdsmanagement.thirds.copy.domain.models.CopyJobLog;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyCancelResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios para CancelThirdsCopyService.
 */
@ExtendWith(MockitoExtension.class)
class CancelThirdsCopyServiceTest {

    @Mock
    private ICopyJobLogRepositoryPort logRepo;

    @InjectMocks
    private CancelThirdsCopyService service;

    @Test
    void cancelarProcesoExistenteDebeRetornarEstadoCANCELADO() {
        String idProceso = UUID.randomUUID().toString();
        CopyJobLog log = CopyJobLog.builder()
                .idProceso(UUID.fromString(idProceso))
                .fase(2)
                .modulo("thirds")
                .estado(CopyEstado.EN_PROCESO)
                .fechaInicio(Instant.now())
                .build();

        when(logRepo.buscarPorIdProceso(anyString())).thenReturn(Optional.of(log));
        when(logRepo.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        CopyCancelResponseDto resultado = service.cancelar(idProceso);

        assertThat(resultado.getEstado()).isEqualTo("CANCELADO");
    }

    @Test
    void cancelarProcesoInexistenteLanzaExcepcion() {
        when(logRepo.buscarPorIdProceso(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancelar("proceso-inexistente"))
                .isInstanceOf(DuplicateCopyJobException.class);
    }
}
