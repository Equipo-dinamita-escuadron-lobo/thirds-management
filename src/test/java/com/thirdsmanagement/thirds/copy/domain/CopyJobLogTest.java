package com.thirdsmanagement.thirds.copy.domain;

import com.thirdsmanagement.thirds.copy.domain.enums.CopyEstado;
import com.thirdsmanagement.thirds.copy.domain.enums.CopyModulo;
import com.thirdsmanagement.thirds.copy.domain.exceptions.DuplicateCopyJobException;
import com.thirdsmanagement.thirds.copy.domain.models.CopyEquivalencia;
import com.thirdsmanagement.thirds.copy.domain.models.CopyJobLog;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitarios del dominio de copia de terceros.
 */
class CopyJobLogTest {

    @Test
    void builderDebeCrearCopyJobLogConCamposCorrectos() {
        UUID idProceso = UUID.randomUUID();
        CopyJobLog log = CopyJobLog.builder()
                .idProceso(idProceso)
                .fase(2)
                .modulo("thirds")
                .estado(CopyEstado.EN_PROCESO)
                .fechaInicio(Instant.now())
                .build();

        assertThat(log.getIdProceso()).isEqualTo(idProceso);
        assertThat(log.getFase()).isEqualTo(2);
        assertThat(log.getModulo()).isEqualTo("thirds");
        assertThat(log.getEstado()).isEqualTo(CopyEstado.EN_PROCESO);
        assertThat(log.getRegistrosProcesados()).isZero();
        assertThat(log.getEquivalenciasGeneradas()).isZero();
    }

    @Test
    void copyModuloDebeContenerTHIRDS() {
        assertThat(CopyModulo.THIRDS).isNotNull();
    }

    @Test
    void copyEstadoDebeContenerTodosLosEstados() {
        assertThat(CopyEstado.values()).contains(
                CopyEstado.EN_PROCESO,
                CopyEstado.COMPLETADO,
                CopyEstado.COMPLETADO_CON_ADVERTENCIAS,
                CopyEstado.FALLIDO,
                CopyEstado.ERROR_NO_REINTENTABLE,
                CopyEstado.CANCELADO
        );
    }

    @Test
    void copyEquivalenciaBuilderDebeCrearConCampos() {
        CopyEquivalencia eq = CopyEquivalencia.builder()
                .tabla("thirds")
                .idViejo("10")
                .idNuevo("20")
                .build();

        assertThat(eq.getTabla()).isEqualTo("thirds");
        assertThat(eq.getIdViejo()).isEqualTo("10");
        assertThat(eq.getIdNuevo()).isEqualTo("20");
    }

    @Test
    void equivalenciasDiferentesTablasSonDesiguales() {
        CopyEquivalencia eq1 = CopyEquivalencia.builder().tabla("thirds").idViejo("1").idNuevo("2").build();
        CopyEquivalencia eq2 = CopyEquivalencia.builder().tabla("third_type").idViejo("1").idNuevo("2").build();

        assertThat(eq1).isNotEqualTo(eq2);
    }

    @Test
    void duplicateCopyJobExceptionConIdProcesoyFase() {
        DuplicateCopyJobException ex = new DuplicateCopyJobException("abc-123", 2);
        assertThat(ex.getMessage()).contains("abc-123").contains("2");
    }

    @Test
    void duplicateCopyJobExceptionConSoloIdProceso() {
        DuplicateCopyJobException ex = new DuplicateCopyJobException("proceso-xyz");
        assertThat(ex.getMessage()).contains("proceso-xyz");
    }
}
