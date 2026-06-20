package com.thirdsmanagement.thirds.integrationAudit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.audit.aspect.AuditAspect;
import com.thirdsmanagement.thirds.infrastructure.audit.builder.AuditEventBuilder;
import com.thirdsmanagement.thirds.infrastructure.audit.builder.OperationEventDto;
import com.thirdsmanagement.thirds.infrastructure.audit.publisher.AuditEventPublisher;
import com.thirdsmanagement.thirds.infrastructure.security.IJwtUtils;

@SpringBootTest(classes = {
        AuditAspect.class,
        AuditEventBuilder.class,
        TestThirdsAuditService.class
})
@EnableAspectJAutoProxy
public class AuditAspectIntegrationTest {

    @MockBean
    private AuditEventPublisher auditEventPublisher;
    @MockBean
    private IJwtUtils jwtUtils;
    @MockBean
    private ThirdOutputPort thirdOutputPort;
    @MockBean
    private IdOutputPort idOutputPort;

    @Autowired
    private TestThirdsAuditService service;

    @BeforeEach
    void setUp() {
        when(jwtUtils.getId()).thenReturn("user-1");
        when(jwtUtils.getUsername()).thenReturn("freider");
        when(jwtUtils.getRealmRoles()).thenReturn(List.of("ADMIN"));
    }

    @Test
    @DisplayName("Debe interceptar CREATE Third y publicar evento")
    void should_intercept_create_third() {
        Third third = buildThird(1L, "ENT-001", true);

        service.createThird(third);

        ArgumentCaptor<OperationEventDto> captor = ArgumentCaptor.forClass(OperationEventDto.class);
        verify(auditEventPublisher).publish(captor.capture());
        OperationEventDto dto = captor.getValue();

        assertAll(
                () -> assertEquals("CREATE", dto.getOperationType()),
                () -> assertEquals("THIRD", dto.getAffectedTable()),
                () -> assertEquals("ENT-001", dto.getEnterpriseId()),
                () -> assertEquals("1", dto.getRegisterId()),
                () -> assertNotNull(dto.getDataObject()));
    }

    @Test
    @DisplayName("Debe interceptar UPDATE TypeId y generar diff")
    void should_intercept_update_typeid_and_generate_diff() {
        TypeId antes = buildTypeId(3L, "ENT-001");
        antes.setTypeIdname("Cédula");

        TypeId despues = buildTypeId(3L, "ENT-001");
        despues.setTypeIdname("Pasaporte");

        when(idOutputPort.getTypeIdById(3L))
                .thenReturn(antes)
                .thenReturn(despues);

        service.updateTypeId(despues);

        ArgumentCaptor<OperationEventDto> captor = ArgumentCaptor.forClass(OperationEventDto.class);
        verify(auditEventPublisher).publish(captor.capture());
        OperationEventDto dto = captor.getValue();
        Map<?, ?> changes = (Map<?, ?>) dto.getDataObject().get("changes");

        assertAll(
                () -> assertEquals("UPDATE", dto.getOperationType()),
                () -> assertTrue(changes.containsKey("typeIdname")));
    }

    @Test
    @DisplayName("Debe interceptar DELETE ThirdType y publicar evento")
    void should_intercept_delete_thirdtype() {
        ThirdType before = buildThirdType(5L, "ENT-001");

        when(idOutputPort.getThirdTypeById(5L)).thenReturn(before);

        service.deleteThirdType(5L, "ENT-001");

        ArgumentCaptor<OperationEventDto> captor = ArgumentCaptor.forClass(OperationEventDto.class);
        verify(auditEventPublisher).publish(captor.capture());
        OperationEventDto dto = captor.getValue();

        assertAll(
                () -> assertEquals("DELETE", dto.getOperationType()),
                () -> assertEquals("THIRD_TYPE", dto.getAffectedTable()));
    }

    @Test
    @DisplayName("Debe resolver ACTIVATE cuando Third estaba inactivo")
    void should_resolve_activate_when_third_was_inactive() {
        Third before = buildThird(10L, "ENT-001", false); // inactivo

        when(thirdOutputPort.getThirdById(10L, "ENT-001"))
                .thenReturn(Optional.of(before));

        service.changeThirdState(10L, "ENT-001");

        ArgumentCaptor<OperationEventDto> captor = ArgumentCaptor.forClass(OperationEventDto.class);
        verify(auditEventPublisher).publish(captor.capture());

        assertEquals("ACTIVATE", captor.getValue().getOperationType());
    }

    // Helpers de entidades
    private Third buildThird(Long id, String entId, boolean state) {
        Third t = new Third();
        t.setThId(id);
        t.setEntId(entId);
        t.setIdNumber(123456L);
        t.setState(state);
        Country country = new Country();
        country.setCountryName("Colombia");
        State province = new State();
        province.setStateName("Cauca");
        City city = new City();
        city.setCityName("Popayán");
        t.setCountry(country);
        t.setProvince(province);
        t.setCity(city);
        return t;
    }

    private TypeId buildTypeId(Long id, String entId) {
        TypeId ti = new TypeId();
        ti.setId(id);
        ti.setEntId(entId);
        ti.setTypeId("CC");
        ti.setTypeIdname("Cédula de ciudadanía");
        ti.setStatus(true);
        return ti;
    }

    private ThirdType buildThirdType(Long id, String entId) {
        ThirdType tt = new ThirdType();
        tt.setThirdTypeId(id);
        tt.setEntId(entId);
        tt.setThirdTypeName("Proveedor");
        tt.setStatus(true);
        return tt;
    }
}
