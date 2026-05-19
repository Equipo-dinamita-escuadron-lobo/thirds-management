package com.thirdsmanagement.thirds.copy.application;

import com.thirdsmanagement.thirds.copy.application.services.CopyEquivalenceMapper;
import com.thirdsmanagement.thirds.copy.domain.models.CopyEquivalencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests para CopyEquivalenceMapper del bounded context copy de terceros.
 */
class CopyEquivalenceMapperTest {

    private CopyEquivalenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CopyEquivalenceMapper();
    }

    @Test
    void registrarYResolverNuevoId() {
        mapper.registrar("thirds", 1L, 100L);

        assertThat(mapper.resolverNuevoId("thirds", 1L)).isEqualTo(100L);
    }

    @Test
    void resolverNuevoIdSinRegistroRetornaNull() {
        assertThat(mapper.resolverNuevoId("thirds", 999L)).isNull();
    }

    @Test
    void limpiarEliminaTodasLasEquivalencias() {
        mapper.registrar("thirds", 1L, 100L);
        mapper.limpiar();

        assertThat(mapper.resolverNuevoId("thirds", 1L)).isNull();
    }

    @Test
    void toListRetornaTodasLasEquivalencias() {
        mapper.registrar("type_id", 1L, 10L);
        mapper.registrar("third_type", 2L, 20L);

        List<CopyEquivalencia> lista = mapper.toList();

        assertThat(lista).hasSize(2);
        assertThat(lista).anyMatch(e -> e.getTabla().equals("type_id")
                && e.getIdViejo().equals("1") && e.getIdNuevo().equals("10"));
        assertThat(lista).anyMatch(e -> e.getTabla().equals("third_type")
                && e.getIdViejo().equals("2") && e.getIdNuevo().equals("20"));
    }

    @Test
    void toListVacioRetornaListaVacia() {
        assertThat(mapper.toList()).isEmpty();
    }

    @Test
    void equivalenciasDiferentesTablasMismoId() {
        mapper.registrar("thirds", 1L, 100L);
        mapper.registrar("third_type", 1L, 200L);

        assertThat(mapper.resolverNuevoId("thirds", 1L)).isEqualTo(100L);
        assertThat(mapper.resolverNuevoId("third_type", 1L)).isEqualTo(200L);
    }
}
