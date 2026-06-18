package com.thirdsmanagement.thirds.unitAudit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.audit.annotation.Auditable;
import com.thirdsmanagement.thirds.infrastructure.audit.annotation.OperationType;
import com.thirdsmanagement.thirds.infrastructure.audit.aspect.AuditAspect;
import com.thirdsmanagement.thirds.infrastructure.audit.builder.AuditEventBuilder;
import com.thirdsmanagement.thirds.infrastructure.audit.builder.OperationEventDto;
import com.thirdsmanagement.thirds.infrastructure.audit.publisher.AuditEventPublisher;

@ExtendWith(MockitoExtension.class)
class AuditAspectTest {

    @Mock
    private AuditEventBuilder auditEventBuilder;

    @Mock
    private AuditEventPublisher auditEventPublisher;

    @Mock
    private ThirdOutputPort thirdOutputPort;

    @Mock
    private IdOutputPort idOutputPort;

    @Mock
    private ProceedingJoinPoint joinPoint;

    private TestableAuditAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new TestableAuditAspect(
                auditEventBuilder,
                auditEventPublisher,
                thirdOutputPort,
                idOutputPort);
    }

    // Metodo captureBeforeData
    @Test
    @DisplayName("UPDATE - debe invocar fetchCurrentState y retornar datos")
    void update_invocaFetchCurrentState() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "TYPE_ID");
        TypeId typeId = new TypeId();
        typeId.setId(1L);
        typeId.setEntId("ENT-001");
        typeId.setTypeId("CC");
        typeId.setTypeIdname("Cédula");
        Object[] args = { typeId };

        when(idOutputPort.getTypeIdById(1L)).thenReturn(typeId);

        Map<String, Object> result = aspect.testCaptureBeforeData(auditable, args);

        assertNotNull(result);
        assertEquals(1L, result.get("id"));
    }

    @Test
    @DisplayName("DELETE - debe invocar fetchCurrentState y retornar datos")
    void delete_invocaFetchCurrentState() {
        Auditable auditable = mockAuditable(OperationType.DELETE, "THIRD_TYPE");
        ThirdType thirdType = new ThirdType();
        thirdType.setThirdTypeId(5L);
        thirdType.setEntId("ENT-001");
        thirdType.setThirdTypeName("Proveedor");
        Object[] args = { thirdType };

        when(idOutputPort.getThirdTypeById(5L)).thenReturn(thirdType);

        Map<String, Object> result = aspect.testCaptureBeforeData(auditable, args);

        assertNotNull(result);
        assertEquals(5L, result.get("id"));
    }

    @Test
    @DisplayName("INACTIVATE - debe invocar fetchCurrentState y retornar datos")
    void inactivate_invocaFetchCurrentState() {
        Auditable auditable = mockAuditable(OperationType.INACTIVATE, "THIRD");
        Long thirdId = 10L;
        String entId = "ENT-001";
        Object[] args = { thirdId, entId };

        Third third = buildThird(10L, "ENT-001", true);
        when(thirdOutputPort.getThirdById(10L, "ENT-001")).thenReturn(Optional.of(third));

        Map<String, Object> result = aspect.testCaptureBeforeData(auditable, args);

        assertNotNull(result);
        assertEquals(10L, result.get("id"));
    }

    @Test
    @DisplayName("CREATE - debe retornar null sin consultar repositorios")
    void create_retornaNull() {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD");
        Object[] args = { new Third() };

        Map<String, Object> result = aspect.testCaptureBeforeData(auditable, args);

        assertNull(result);
        verifyNoInteractions(thirdOutputPort, idOutputPort);
    }

    @Test
    @DisplayName("Excepción en fetchCurrentState - debe retornar null sin propagar")
    void excepcionEnFetch_retornaNull() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "TYPE_ID");
        TypeId typeId = new TypeId();
        typeId.setId(99L);
        Object[] args = { typeId };

        when(idOutputPort.getTypeIdById(99L)).thenThrow(new RuntimeException("DB caída"));

        Map<String, Object> result = aspect.testCaptureBeforeData(auditable, args);

        assertNull(result);
    }

    // Metodo fetchCurrentState
    @Test
    @DisplayName("THIRD con Long en args[0] - busca por id y entId como Long y String")
    void third_argsLongString_retornaMapa() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "THIRD");
        Third third = buildThird(1L, "ENT-001", true);
        Object[] args = { 1L, "ENT-001" };

        when(thirdOutputPort.getThirdById(1L, "ENT-001")).thenReturn(Optional.of(third));

        Map<String, Object> result = aspect.testFetchCurrentState(auditable, args);

        assertNotNull(result);
        assertEquals(1L, result.get("id"));
        assertEquals("ENT-001", result.get("entId"));
    }

    @Test
    @DisplayName("THIRD con objeto Third en args[0] - extrae id y entId del objeto")
    void third_argsThirdObject_retornaMapa() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "THIRD");
        Third third = buildThird(2L, "ENT-002", false);
        Object[] args = { third };

        when(thirdOutputPort.getThirdById(2L, "ENT-002")).thenReturn(Optional.of(third));

        Map<String, Object> result = aspect.testFetchCurrentState(auditable, args);

        assertNotNull(result);
        assertEquals(2L, result.get("id"));
    }

    @Test
    @DisplayName("THIRD no encontrado - retorna null")
    void third_noEncontrado_retornaNull() {
        Auditable auditable = mockAuditable(OperationType.DELETE, "THIRD");
        Object[] args = { 99L, "ENT-001" };

        when(thirdOutputPort.getThirdById(99L, "ENT-001")).thenReturn(Optional.empty());

        Map<String, Object> result = aspect.testFetchCurrentState(auditable, args);

        assertNull(result);
    }

    // TYPE_ID

    @Test
    @DisplayName("TYPE_ID con Long en args[0] - busca por id directamente")
    void typeId_argsLong_retornaMapa() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "TYPE_ID");
        TypeId typeId = buildTypeId(3L, "ENT-001");
        Object[] args = { 3L, "ENT-001" };

        when(idOutputPort.getTypeIdById(3L)).thenReturn(typeId);

        Map<String, Object> result = aspect.testFetchCurrentState(auditable, args);

        assertNotNull(result);
        assertEquals(3L, result.get("id"));
    }

    @Test
    @DisplayName("TYPE_ID con objeto TypeId en args[0] - extrae id del objeto")
    void typeId_argsTypeIdObject_retornaMapa() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "TYPE_ID");
        TypeId typeId = buildTypeId(4L, "ENT-002");
        Object[] args = { typeId };

        when(idOutputPort.getTypeIdById(4L)).thenReturn(typeId);

        Map<String, Object> result = aspect.testFetchCurrentState(auditable, args);

        assertNotNull(result);
        assertEquals(4L, result.get("id"));
    }

    @Test
    @DisplayName("TYPE_ID no encontrado (null) - retorna null")
    void typeId_noEncontrado_retornaNull() {
        Auditable auditable = mockAuditable(OperationType.DELETE, "TYPE_ID");
        Object[] args = { 99L, "ENT-001" };

        when(idOutputPort.getTypeIdById(99L)).thenReturn(null);

        Map<String, Object> result = aspect.testFetchCurrentState(auditable, args);

        assertNull(result);
    }

    // THIRD_TYPE
    @Test
    @DisplayName("THIRD_TYPE con Long en args[0] - busca por id directamente")
    void thirdType_argsLong_retornaMapa() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "THIRD_TYPE");
        ThirdType thirdType = buildThirdType(5L, "ENT-001");
        Object[] args = { 5L, "ENT-001" };

        when(idOutputPort.getThirdTypeById(5L)).thenReturn(thirdType);

        Map<String, Object> result = aspect.testFetchCurrentState(auditable, args);

        assertNotNull(result);
        assertEquals(5L, result.get("id"));
    }

    @Test
    @DisplayName("THIRD_TYPE con objeto ThirdType en args[0] - extrae id del objeto")
    void thirdType_argsThirdTypeObject_retornaMapa() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "THIRD_TYPE");
        ThirdType thirdType = buildThirdType(6L, "ENT-003");
        Object[] args = { thirdType };

        when(idOutputPort.getThirdTypeById(6L)).thenReturn(thirdType);

        Map<String, Object> result = aspect.testFetchCurrentState(auditable, args);

        assertNotNull(result);
        assertEquals(6L, result.get("id"));
    }

    @Test
    @DisplayName("THIRD_TYPE no encontrado (null) - retorna null")
    void thirdType_noEncontrado_retornaNull() {
        Auditable auditable = mockAuditable(OperationType.DELETE, "THIRD_TYPE");
        Object[] args = { 99L, "ENT-001" };

        when(idOutputPort.getThirdTypeById(99L)).thenReturn(null);

        Map<String, Object> result = aspect.testFetchCurrentState(auditable, args);

        assertNull(result);
    }

    @Test
    @DisplayName("affectedTable desconocida - retorna null")
    void tablaDesconocida_retornaNull() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "UNKNOWN_TABLE");
        Object[] args = { 1L, "ENT-001" };

        Map<String, Object> result = aspect.testFetchCurrentState(auditable, args);

        assertNull(result);
        verifyNoInteractions(thirdOutputPort, idOutputPort);
    }

    // Metodo resolveFinalOperationType
    @Test
    @DisplayName("UPDATE + diff solo tiene 'state' con before=false after=true → ACTIVATE")
    void update_diffSoloState_afterTrue_retornaActivate() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "THIRD");
        Map<String, Object> stateChange = new LinkedHashMap<>();
        stateChange.put("before", false);
        stateChange.put("after", true);
        Map<String, Object> diff = Map.of("state", stateChange);

        OperationType result = aspect.testResolveFinalOperationType(auditable, null, diff);

        assertEquals(OperationType.ACTIVATE, result);
    }

    @Test
    @DisplayName("UPDATE + diff solo tiene 'state' con before=true after=false → INACTIVATE")
    void update_diffSoloState_afterFalse_retornaInactivate() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "THIRD");
        Map<String, Object> stateChange = new LinkedHashMap<>();
        stateChange.put("before", true);
        stateChange.put("after", false);
        Map<String, Object> diff = Map.of("state", stateChange);

        OperationType result = aspect.testResolveFinalOperationType(auditable, null, diff);

        assertEquals(OperationType.INACTIVATE, result);
    }

    @Test
    @DisplayName("UPDATE + diff solo tiene 'state' con before == after → retorna null (sin cambio real)")
    void update_diffSoloState_beforeEqualsAfter_retornaNull() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "THIRD");
        Map<String, Object> stateChange = new LinkedHashMap<>();
        stateChange.put("before", true);
        stateChange.put("after", true);
        Map<String, Object> diff = Map.of("state", stateChange);

        OperationType result = aspect.testResolveFinalOperationType(auditable, null, diff);

        assertNull(result);
    }

    @Test
    @DisplayName("UPDATE + diff tiene más de un campo → retorna UPDATE sin reclasificar")
    void update_diffVariosCampos_retornaUpdate() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "TYPE_ID");
        Map<String, Object> stateChange = new LinkedHashMap<>();
        stateChange.put("before", true);
        stateChange.put("after", false);
        Map<String, Object> diff = new LinkedHashMap<>();
        diff.put("state", stateChange);
        diff.put("typeIdname", Map.of("before", "Cédula", "after", "Pasaporte"));

        OperationType result = aspect.testResolveFinalOperationType(auditable, null, diff);

        assertEquals(OperationType.UPDATE, result);
    }

    @Test
    @DisplayName("UPDATE + diff es null → retorna UPDATE")
    void update_diffNull_retornaUpdate() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "THIRD");

        OperationType result = aspect.testResolveFinalOperationType(auditable, null, null);

        assertEquals(OperationType.UPDATE, result);
    }

    @Test
    @DisplayName("UPDATE + diff vacío → retorna UPDATE")
    void update_diffVacio_retornaUpdate() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "THIRD");

        OperationType result = aspect.testResolveFinalOperationType(auditable, null, Map.of());

        assertEquals(OperationType.UPDATE, result);
    }

    @Test
    @DisplayName("INACTIVATE + beforeData con state=true → retorna INACTIVATE")
    void inactivate_beforeDataStateTrue_retornaInactivate() {
        Auditable auditable = mockAuditable(OperationType.INACTIVATE, "THIRD");
        Map<String, Object> beforeData = new LinkedHashMap<>();
        beforeData.put("state", true);

        OperationType result = aspect.testResolveFinalOperationType(auditable, beforeData, null);

        assertEquals(OperationType.INACTIVATE, result);
    }

    @Test
    @DisplayName("INACTIVATE + beforeData con state=false (ya inactivo) → retorna ACTIVATE")
    void inactivate_beforeDataStateFalse_retornaActivate() {
        Auditable auditable = mockAuditable(OperationType.INACTIVATE, "THIRD");
        Map<String, Object> beforeData = new LinkedHashMap<>();
        beforeData.put("state", false);

        OperationType result = aspect.testResolveFinalOperationType(auditable, beforeData, null);

        assertEquals(OperationType.ACTIVATE, result);
    }

    @Test
    @DisplayName("INACTIVATE + beforeData null → retorna INACTIVATE (fallthrough al default)")
    void inactivate_beforeDataNull_retornaInactivate() {
        Auditable auditable = mockAuditable(OperationType.INACTIVATE, "THIRD");

        OperationType result = aspect.testResolveFinalOperationType(auditable, null, null);

        assertEquals(OperationType.INACTIVATE, result);
    }

    @Test
    @DisplayName("CREATE → retorna CREATE directamente")
    void create_retornaCreate() {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD");

        OperationType result = aspect.testResolveFinalOperationType(auditable, null, null);

        assertEquals(OperationType.CREATE, result);
    }

    @Test
    @DisplayName("DELETE → retorna DELETE directamente")
    void delete_retornaDelete() {
        Auditable auditable = mockAuditable(OperationType.DELETE, "THIRD");

        OperationType result = aspect.testResolveFinalOperationType(auditable, null, null);

        assertEquals(OperationType.DELETE, result);
    }

    @Test
    @DisplayName("ACTIVATE → retorna ACTIVATE directamente")
    void activate_retornaActivate() {
        Auditable auditable = mockAuditable(OperationType.ACTIVATE, "THIRD");

        OperationType result = aspect.testResolveFinalOperationType(auditable, null, null);

        assertEquals(OperationType.ACTIVATE, result);
    }

    // Metodo builddiff
    @Test
    @DisplayName("Campos distintos → incluye entry con before y after")
    void camposCambiados_incluyeDiff() {
        Map<String, Object> before = Map.of("typeIdname", "Cédula", "state", true);
        Map<String, Object> after = Map.of("typeIdname", "Pasaporte", "state", true);

        Map<String, Object> diff = aspect.testBuildDiff(before, after);

        assertTrue(diff.containsKey("typeIdname"));
        Map<?, ?> change = (Map<?, ?>) diff.get("typeIdname");
        assertEquals("Cédula", change.get("before"));
        assertEquals("Pasaporte", change.get("after"));
    }

    @Test
    @DisplayName("Campos iguales → no se incluyen en el diff")
    void camposIguales_noApareceEnDiff() {
        Map<String, Object> before = Map.of("typeIdname", "Cédula", "state", true);
        Map<String, Object> after = Map.of("typeIdname", "Cédula", "state", true);

        Map<String, Object> diff = aspect.testBuildDiff(before, after);

        assertTrue(diff.isEmpty());
    }

    @Test
    @DisplayName("before null → retorna mapa vacío")
    void beforeNull_retornaVacio() {
        Map<String, Object> diff = aspect.testBuildDiff(null, Map.of("state", true));

        assertTrue(diff.isEmpty());
    }

    @Test
    @DisplayName("after null → retorna mapa vacío")
    void afterNull_retornaVacio() {
        Map<String, Object> diff = aspect.testBuildDiff(Map.of("state", true), null);

        assertTrue(diff.isEmpty());
    }

    @Test
    @DisplayName("ambos null → retorna mapa vacío")
    void ambosNull_retornaVacio() {
        Map<String, Object> diff = aspect.testBuildDiff(null, null);

        assertTrue(diff.isEmpty());
    }

    @Test
    @DisplayName("campo en after que no existe en before → before es null en el entry")
    void campoNuevoEnAfter_beforeEsNull() {
        Map<String, Object> before = new LinkedHashMap<>();
        before.put("typeIdname", "Cédula");
        Map<String, Object> after = new LinkedHashMap<>();
        after.put("typeIdname", "Cédula");
        after.put("classification", "NATURAL"); // campo nuevo

        Map<String, Object> diff = aspect.testBuildDiff(before, after);

        assertTrue(diff.containsKey("classification"));
        Map<?, ?> change = (Map<?, ?>) diff.get("classification");
        assertNull(change.get("before"));
        assertEquals("NATURAL", change.get("after"));
    }

    @Test
    @DisplayName("múltiples campos cambiados → todos aparecen en el diff")
    void variosCarmbios_todosEnDiff() {
        Map<String, Object> before = new LinkedHashMap<>();
        before.put("typeIdname", "Cédula");
        before.put("state", true);
        before.put("classification", "NATURAL");

        Map<String, Object> after = new LinkedHashMap<>();
        after.put("typeIdname", "Pasaporte");
        after.put("state", false);
        after.put("classification", "JURIDICA");

        Map<String, Object> diff = aspect.testBuildDiff(before, after);

        assertEquals(3, diff.size());
        assertTrue(diff.containsKey("typeIdname"));
        assertTrue(diff.containsKey("state"));
        assertTrue(diff.containsKey("classification"));
    }

    @Test
    @DisplayName("preserva orden de inserción (LinkedHashMap)")
    void preservaOrden() {
        Map<String, Object> before = new LinkedHashMap<>();
        before.put("a", 1);
        before.put("b", 2);
        before.put("c", 3);

        Map<String, Object> after = new LinkedHashMap<>();
        after.put("a", 10);
        after.put("b", 20);
        after.put("c", 30);

        Map<String, Object> diff = aspect.testBuildDiff(before, after);

        List<String> keys = new ArrayList<>(diff.keySet());
        assertEquals(List.of("a", "b", "c"), keys);
    }

    // Metodo buildcontext
    @Test
    @DisplayName("Third - extrae solo los campos definidos en CONTEXT_FIELDS")
    void third_extraeCamposContexto() {
        Map<String, Object> beforeData = new LinkedHashMap<>();
        beforeData.put("id", 1L);
        beforeData.put("entId", "ENT-001");
        beforeData.put("idNumber", "123456");
        beforeData.put("socialReason", "Empresa S.A.");
        beforeData.put("names", "Juan");
        beforeData.put("lastNames", "Pérez");
        beforeData.put("state", true);

        Map<String, Object> context = aspect.testBuildContext(Third.class, beforeData);

        assertAll(
                () -> assertEquals(4, context.size()),
                () -> assertTrue(context.containsKey("idNumber")),
                () -> assertTrue(context.containsKey("socialReason")),
                () -> assertTrue(context.containsKey("names")),
                () -> assertTrue(context.containsKey("lastNames")),
                () -> assertFalse(context.containsKey("id")),
                () -> assertFalse(context.containsKey("state")));
    }

    @Test
    @DisplayName("Third - campos null en beforeData no se incluyen en el contexto")
    void third_camposNullNoSeIncluyen() {
        Map<String, Object> beforeData = new LinkedHashMap<>();
        beforeData.put("idNumber", "123456");
        beforeData.put("socialReason", null);
        beforeData.put("names", "Juan");
        beforeData.put("lastNames", null);

        Map<String, Object> context = aspect.testBuildContext(Third.class, beforeData);

        assertEquals(2, context.size());
        assertTrue(context.containsKey("idNumber"));
        assertTrue(context.containsKey("names"));
        assertFalse(context.containsKey("socialReason"));
        assertFalse(context.containsKey("lastNames"));
    }

    @Test
    @DisplayName("TypeId - extrae typeId y typeIdname")
    void typeId_extraeCamposContexto() {
        Map<String, Object> beforeData = new LinkedHashMap<>();
        beforeData.put("id", 3L);
        beforeData.put("entId", "ENT-001");
        beforeData.put("typeId", "CC");
        beforeData.put("typeIdname", "Cédula de ciudadanía");
        beforeData.put("state", true);

        Map<String, Object> context = aspect.testBuildContext(TypeId.class, beforeData);

        assertAll(
                () -> assertEquals(2, context.size()),
                () -> assertTrue(context.containsKey("typeId")),
                () -> assertTrue(context.containsKey("typeIdname")),
                () -> assertFalse(context.containsKey("id")),
                () -> assertFalse(context.containsKey("state")));
    }

    @Test
    @DisplayName("ThirdType - extrae solo thirdTypeName")
    void thirdType_extraeCamposContexto() {
        Map<String, Object> beforeData = new LinkedHashMap<>();
        beforeData.put("id", 5L);
        beforeData.put("entId", "ENT-001");
        beforeData.put("thirdTypeName", "Proveedor");
        beforeData.put("state", true);

        Map<String, Object> context = aspect.testBuildContext(ThirdType.class, beforeData);

        assertAll(
                () -> assertEquals(1, context.size()),
                () -> assertTrue(context.containsKey("thirdTypeName")),
                () -> assertFalse(context.containsKey("state")));
    }

    @Test
    @DisplayName("entityClass null → retorna mapa vacío")
    void entityClassNull_retornaVacio() {
        Map<String, Object> beforeData = Map.of("idNumber", "123456");

        Map<String, Object> context = aspect.testBuildContext(null, beforeData);

        assertTrue(context.isEmpty());
    }

    @Test
    @DisplayName("beforeData null → retorna mapa vacío")
    void beforeDataNull_retornaVacio() {
        Map<String, Object> context = aspect.testBuildContext(Third.class, null);

        assertTrue(context.isEmpty());
    }

    @Test
    @DisplayName("entityClass desconocida → retorna mapa vacío")
    void entityClassDesconocida_retornaVacio() {
        Map<String, Object> beforeData = Map.of("someField", "someValue");

        Map<String, Object> context = aspect.testBuildContext(String.class, beforeData);

        assertTrue(context.isEmpty());
    }

    @Test
    @DisplayName("preserva orden de inserción (LinkedHashMap)")
    void preservaOrdenDeInsercion() {
        Map<String, Object> beforeData = new LinkedHashMap<>();
        beforeData.put("lastNames", "Pérez");
        beforeData.put("names", "Juan");
        beforeData.put("idNumber", "123456");
        beforeData.put("socialReason", "Empresa S.A.");

        Map<String, Object> context = aspect.testBuildContext(Third.class, beforeData);

        List<String> keys = new ArrayList<>(context.keySet());
        assertEquals(List.of("idNumber", "socialReason", "names", "lastNames"), keys);
    }

    // metodo resolve enterpriseid
    @Test
    @DisplayName("CREATE + result es Third → retorna entId del Third")
    void create_resultThird_retornaEntId() {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD");
        Third third = buildThird(1L, "ENT-001", true);

        String result = aspect.testResolveEnterpriseId(auditable, new Object[] {}, third, null);

        assertEquals("ENT-001", result);
    }

    @Test
    @DisplayName("CREATE + result es TypeId → retorna entId del TypeId")
    void create_resultTypeId_retornaEntId() {
        Auditable auditable = mockAuditable(OperationType.CREATE, "TYPE_ID");
        TypeId typeId = buildTypeId(2L, "ENT-002");

        String result = aspect.testResolveEnterpriseId(auditable, new Object[] {}, typeId, null);

        assertEquals("ENT-002", result);
    }

    @Test
    @DisplayName("CREATE + result es ThirdType → retorna entId del ThirdType")
    void create_resultThirdType_retornaEntId() {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD_TYPE");
        ThirdType thirdType = buildThirdType(3L, "ENT-003");

        String result = aspect.testResolveEnterpriseId(auditable, new Object[] {}, thirdType, null);

        assertEquals("ENT-003", result);
    }

    @Test
    @DisplayName("CREATE + result no es ninguna entidad conocida → retorna UNKNOWN")
    void create_resultDesconocido_retornaUnknown() {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD");

        String result = aspect.testResolveEnterpriseId(auditable, new Object[] {}, "resultado_inesperado", null);

        assertEquals("UNKNOWN", result);
    }

    @Test
    @DisplayName("UPDATE + beforeData con entId → retorna entId de beforeData")
    void update_beforeDataConEntId_retornaEntId() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "THIRD");
        Map<String, Object> beforeData = Map.of("entId", "ENT-005", "id", 1L);

        String result = aspect.testResolveEnterpriseId(auditable, new Object[] {}, null, beforeData);

        assertEquals("ENT-005", result);
    }

    @Test
    @DisplayName("DELETE + beforeData con entId → retorna entId de beforeData")
    void delete_beforeDataConEntId_retornaEntId() {
        Auditable auditable = mockAuditable(OperationType.DELETE, "TYPE_ID");
        Map<String, Object> beforeData = Map.of("entId", "ENT-006", "id", 2L);

        String result = aspect.testResolveEnterpriseId(auditable, new Object[] {}, null, beforeData);

        assertEquals("ENT-006", result);
    }

    @Test
    @DisplayName("INACTIVATE + beforeData con entId → retorna entId de beforeData")
    void inactivate_beforeDataConEntId_retornaEntId() {
        Auditable auditable = mockAuditable(OperationType.INACTIVATE, "THIRD");
        Map<String, Object> beforeData = Map.of("entId", "ENT-007", "id", 3L);

        String result = aspect.testResolveEnterpriseId(auditable, new Object[] {}, null, beforeData);

        assertEquals("ENT-007", result);
    }

    @Test
    @DisplayName("UPDATE + beforeData null → retorna UNKNOWN")
    void update_beforeDataNull_retornaUnknown() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "THIRD");

        String result = aspect.testResolveEnterpriseId(auditable, new Object[] {}, null, null);

        assertEquals("UNKNOWN", result);
    }

    // Metodo resolveregisterid
    @Test
    @DisplayName("CREATE + result es Third → retorna thId como String")
    void create_resultThird_retornaThId() {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD");
        Third third = buildThird(10L, "ENT-001", true);

        String result = aspect.testResolveRegisterId(auditable, new Object[] {}, third, null);

        assertEquals("10", result);
    }

    @Test
    @DisplayName("CREATE + result es TypeId → retorna id como String")
    void create_resultTypeId_retornaId() {
        Auditable auditable = mockAuditable(OperationType.CREATE, "TYPE_ID");
        TypeId typeId = buildTypeId(20L, "ENT-001");

        String result = aspect.testResolveRegisterId(auditable, new Object[] {}, typeId, null);

        assertEquals("20", result);
    }

    @Test
    @DisplayName("CREATE + result es ThirdType → retorna thirdTypeId como String")
    void create_resultThirdType_retornaThirdTypeId() {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD_TYPE");
        ThirdType thirdType = buildThirdType(30L, "ENT-001");

        String result = aspect.testResolveRegisterId(auditable, new Object[] {}, thirdType, null);

        assertEquals("30", result);
    }

    @Test
    @DisplayName("CREATE + result no es entidad conocida → retorna UNKNOWN")
    void resolveRegisterId_create_resultDesconocido_retornaUnknown() {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD");
        String result = aspect.testResolveRegisterId(auditable, new Object[] {}, "inesperado", null);
        assertEquals("UNKNOWN", result);
    }

    @Test
    @DisplayName("UPDATE + beforeData con id → retorna id como String")
    void update_beforeDataConId_retornaId() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "THIRD");
        Map<String, Object> beforeData = Map.of("id", 5L, "entId", "ENT-001");

        String result = aspect.testResolveRegisterId(auditable, new Object[] {}, null, beforeData);

        assertEquals("5", result);
    }

    @Test
    @DisplayName("DELETE + beforeData con id → retorna id como String")
    void delete_beforeDataConId_retornaId() {
        Auditable auditable = mockAuditable(OperationType.DELETE, "TYPE_ID");
        Map<String, Object> beforeData = Map.of("id", 7L, "entId", "ENT-002");

        String result = aspect.testResolveRegisterId(auditable, new Object[] {}, null, beforeData);

        assertEquals("7", result);
    }

    @Test
    @DisplayName("INACTIVATE + beforeData con id → retorna id como String")
    void inactivate_beforeDataConId_retornaId() {
        Auditable auditable = mockAuditable(OperationType.INACTIVATE, "THIRD");
        Map<String, Object> beforeData = Map.of("id", 9L, "entId", "ENT-003");

        String result = aspect.testResolveRegisterId(auditable, new Object[] {}, null, beforeData);

        assertEquals("9", result);
    }

    @Test
    @DisplayName("UPDATE + beforeData null → retorna UNKNOWN")
    void resolveRegisterId_update_beforeDataNull_retornaUnknown() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "THIRD");

        String result = aspect.testResolveRegisterId(auditable, new Object[] {}, null, null);

        assertEquals("UNKNOWN", result);
    }

    // Metodo buildataobject
    @Test
    @DisplayName("CREATE + result Third → dataObject tiene 'entity' con campos del tercero")
    void create_resultThird_tieneEntity() {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD");
        Third third = buildThird(1L, "ENT-001", true);

        Map<String, Object> data = aspect.testBuildDataObject(
                OperationType.CREATE, new Object[] {}, third, auditable, null, null);

        assertTrue(data.containsKey("entity"));
        Map<?, ?> entity = (Map<?, ?>) data.get("entity");
        assertEquals(1L, entity.get("id"));
        assertEquals("ENT-001", entity.get("entId"));
    }

    @Test
    @DisplayName("CREATE + result TypeId → dataObject tiene 'entity' con campos del typeId")
    void create_resultTypeId_tieneEntity() {
        Auditable auditable = mockAuditable(OperationType.CREATE, "TYPE_ID");
        TypeId typeId = buildTypeId(2L, "ENT-002");

        Map<String, Object> data = aspect.testBuildDataObject(
                OperationType.CREATE, new Object[] {}, typeId, auditable, null, null);

        assertTrue(data.containsKey("entity"));
        Map<?, ?> entity = (Map<?, ?>) data.get("entity");
        assertEquals(2L, entity.get("id"));
    }

    @Test
    @DisplayName("CREATE + result ThirdType → dataObject tiene 'entity' con campos del thirdType")
    void create_resultThirdType_tieneEntity() {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD_TYPE");
        ThirdType thirdType = buildThirdType(3L, "ENT-003");

        Map<String, Object> data = aspect.testBuildDataObject(
                OperationType.CREATE, new Object[] {}, thirdType, auditable, null, null);

        assertTrue(data.containsKey("entity"));
        Map<?, ?> entity = (Map<?, ?>) data.get("entity");
        assertEquals(3L, entity.get("id"));
    }

    @Test
    @DisplayName("CREATE + result no es entidad conocida → dataObject vacío")
    void create_resultDesconocido_retornaVacio() {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD");

        Map<String, Object> data = aspect.testBuildDataObject(
                OperationType.CREATE, new Object[] {}, "inesperado", auditable, null, null);

        assertTrue(data.isEmpty());
    }

    @Test
    @DisplayName("UPDATE + diff con cambios → dataObject tiene 'context' y 'changes'")
    void update_conDiff_tieneContextYChanges() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "TYPE_ID");
        Map<String, Object> beforeData = new LinkedHashMap<>();
        beforeData.put("id", 3L);
        beforeData.put("entId", "ENT-001");
        beforeData.put("typeId", "CC");
        beforeData.put("typeIdname", "Cédula");
        beforeData.put("state", true);

        Map<String, Object> nameChange = new LinkedHashMap<>();
        nameChange.put("before", "Cédula");
        nameChange.put("after", "Pasaporte");
        Map<String, Object> diff = Map.of("typeIdname", nameChange);

        Map<String, Object> data = aspect.testBuildDataObject(
                OperationType.UPDATE, new Object[] {}, null, auditable, beforeData, diff);

        assertTrue(data.containsKey("context"));
        assertTrue(data.containsKey("changes"));
        Map<?, ?> context = (Map<?, ?>) data.get("context");
        assertTrue(context.containsKey("typeId"));
        assertTrue(context.containsKey("typeIdname"));
    }

    @Test
    @DisplayName("UPDATE + beforeData sin campos de contexto → dataObject no tiene 'context'")
    void update_sinCamposContexto_noTieneContext() {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "TYPE_ID");
        // beforeData sin typeId ni typeIdname
        Map<String, Object> beforeData = Map.of("id", 3L, "entId", "ENT-001");

        Map<String, Object> stateChange = new LinkedHashMap<>();
        stateChange.put("before", true);
        stateChange.put("after", false);
        Map<String, Object> diff = Map.of("state", stateChange);

        Map<String, Object> data = aspect.testBuildDataObject(
                OperationType.UPDATE, new Object[] {}, null, auditable, beforeData, diff);

        assertFalse(data.containsKey("context"));
        assertTrue(data.containsKey("changes"));
    }

    @Test
    @DisplayName("INACTIVATE + beforeData state=true → dataObject tiene context y changes con state")
    void inactivate_beforeDataStateTrue_tieneChanges() {
        Auditable auditable = mockAuditable(OperationType.INACTIVATE, "THIRD_TYPE");
        Map<String, Object> beforeData = new LinkedHashMap<>();
        beforeData.put("id", 5L);
        beforeData.put("entId", "ENT-001");
        beforeData.put("thirdTypeName", "Proveedor");
        beforeData.put("state", true);

        Map<String, Object> data = aspect.testBuildDataObject(
                OperationType.INACTIVATE, new Object[] {}, null, auditable, beforeData, null);

        assertTrue(data.containsKey("changes"));
        Map<?, ?> changes = (Map<?, ?>) data.get("changes");
        assertTrue(changes.containsKey("state"));
        Map<?, ?> stateChange = (Map<?, ?>) changes.get("state");
        assertEquals(true, stateChange.get("before"));
        assertEquals(false, stateChange.get("after"));
    }

    @Test
    @DisplayName("ACTIVATE + beforeData state=false → dataObject tiene changes con state invertido")
    void activate_beforeDataStateFalse_tieneChanges() {
        Auditable auditable = mockAuditable(OperationType.ACTIVATE, "THIRD_TYPE");
        Map<String, Object> beforeData = new LinkedHashMap<>();
        beforeData.put("id", 5L);
        beforeData.put("entId", "ENT-001");
        beforeData.put("thirdTypeName", "Proveedor");
        beforeData.put("state", false);

        Map<String, Object> data = aspect.testBuildDataObject(
                OperationType.ACTIVATE, new Object[] {}, null, auditable, beforeData, null);

        assertTrue(data.containsKey("changes"));
        Map<?, ?> changes = (Map<?, ?>) data.get("changes");
        Map<?, ?> stateChange = (Map<?, ?>) changes.get("state");
        assertEquals(false, stateChange.get("before"));
        assertEquals(true, stateChange.get("after"));
    }

    @Test
    @DisplayName("INACTIVATE + stateBefore == stateAfter (via diff) → retorna mapa vacío")
    void inactivate_sinCambioReal_retornaVacio() {
        Auditable auditable = mockAuditable(OperationType.INACTIVATE, "THIRD");
        Map<String, Object> beforeData = new LinkedHashMap<>();
        beforeData.put("id", 1L);
        beforeData.put("entId", "ENT-001");
        beforeData.put("state", true);

        // diff indica que el after también es true → sin cambio real
        Map<String, Object> stateEntry = new LinkedHashMap<>();
        stateEntry.put("before", true);
        stateEntry.put("after", true);
        Map<String, Object> diff = Map.of("state", stateEntry);

        Map<String, Object> data = aspect.testBuildDataObject(
                OperationType.INACTIVATE, new Object[] {}, null, auditable, beforeData, diff);

        assertTrue(data.isEmpty());
    }

    @Test
    @DisplayName("ACTIVATE + beforeData null → retorna mapa vacío")
    void activate_beforeDataNull_retornaVacio() {
        Auditable auditable = mockAuditable(OperationType.ACTIVATE, "THIRD");

        Map<String, Object> data = aspect.testBuildDataObject(
                OperationType.ACTIVATE, new Object[] {}, null, auditable, null, null);

        assertTrue(data.isEmpty());
    }

    @Test
    @DisplayName("DELETE + beforeData presente → dataObject tiene 'entity' con beforeData")
    void delete_conBeforeData_tieneEntity() {
        Auditable auditable = mockAuditable(OperationType.DELETE, "THIRD");
        Map<String, Object> beforeData = new LinkedHashMap<>();
        beforeData.put("id", 8L);
        beforeData.put("entId", "ENT-001");
        beforeData.put("idNumber", "999888");

        Map<String, Object> data = aspect.testBuildDataObject(
                OperationType.DELETE, new Object[] { 8L, "ENT-001" }, null, auditable, beforeData, null);

        assertTrue(data.containsKey("entity"));
        Map<?, ?> entity = (Map<?, ?>) data.get("entity");
        assertEquals(8L, entity.get("id"));
    }

    @Test
    @DisplayName("DELETE + beforeData null → entity tiene fallback con args[0]")
    void delete_beforeDataNull_entityConArg0() {
        Auditable auditable = mockAuditable(OperationType.DELETE, "THIRD");
        Object[] args = { 42L, "ENT-001" };

        Map<String, Object> data = aspect.testBuildDataObject(
                OperationType.DELETE, args, null, auditable, null, null);

        assertTrue(data.containsKey("entity"));
        Map<?, ?> entity = (Map<?, ?>) data.get("entity");
        assertEquals(42L, entity.get("id"));
    }

    // Metodo audit
    @Test
    @DisplayName("CREATE Third - ejecuta negocio, construye evento y lo publica")
    void create_third_publicaEvento() throws Throwable {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD");
        Third third = buildThird(1L, "ENT-001", true);

        when(joinPoint.getArgs()).thenReturn(new Object[] {});
        when(joinPoint.proceed()).thenReturn(third);

        OperationEventDto dto = OperationEventDto.builder()
                .enterpriseId("ENT-001")
                .operationType("CREATE")
                .build();
        when(auditEventBuilder.build(any(), any(), any(), any(), any())).thenReturn(dto);

        Object result = aspect.audit(joinPoint, auditable);

        assertEquals(third, result);
        verify(auditEventBuilder).build(
                eq(auditable),
                eq(OperationType.CREATE),
                eq("ENT-001"),
                eq("1"),
                argThat(data -> data.containsKey("entity")));
        verify(auditEventPublisher).publish(dto);
    }

    @Test
    @DisplayName("CREATE TypeId - ejecuta negocio, construye evento y lo publica")
    void create_typeId_publicaEvento() throws Throwable {
        Auditable auditable = mockAuditable(OperationType.CREATE, "TYPE_ID");
        TypeId typeId = buildTypeId(2L, "ENT-002");

        when(joinPoint.getArgs()).thenReturn(new Object[] {});
        when(joinPoint.proceed()).thenReturn(typeId);

        OperationEventDto dto = OperationEventDto.builder()
                .enterpriseId("ENT-002")
                .operationType("CREATE")
                .build();
        when(auditEventBuilder.build(any(), any(), any(), any(), any())).thenReturn(dto);

        Object result = aspect.audit(joinPoint, auditable);

        assertEquals(typeId, result);
        verify(auditEventPublisher).publish(dto);
    }

    @Test
    @DisplayName("CREATE ThirdType - ejecuta negocio, construye evento y lo publica")
    void create_thirdType_publicaEvento() throws Throwable {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD_TYPE");
        ThirdType thirdType = buildThirdType(3L, "ENT-003");

        when(joinPoint.getArgs()).thenReturn(new Object[] {});
        when(joinPoint.proceed()).thenReturn(thirdType);

        OperationEventDto dto = OperationEventDto.builder()
                .enterpriseId("ENT-003")
                .operationType("CREATE")
                .build();
        when(auditEventBuilder.build(any(), any(), any(), any(), any())).thenReturn(dto);

        Object result = aspect.audit(joinPoint, auditable);

        assertEquals(thirdType, result);
        verify(auditEventPublisher).publish(dto);
    }

    @Test
    @DisplayName("DELETE Third - captura beforeData, ejecuta negocio y publica")
    void delete_third_publicaEvento() throws Throwable {
        Auditable auditable = mockAuditable(OperationType.DELETE, "THIRD");
        Third third = buildThird(5L, "ENT-001", true);
        Object[] args = { 5L, "ENT-001" };

        when(joinPoint.getArgs()).thenReturn(args);
        when(thirdOutputPort.getThirdById(5L, "ENT-001")).thenReturn(Optional.of(third));
        when(joinPoint.proceed()).thenReturn(true);

        OperationEventDto dto = OperationEventDto.builder().build();
        when(auditEventBuilder.build(any(), any(), any(), any(), any())).thenReturn(dto);

        Object result = aspect.audit(joinPoint, auditable);

        assertEquals(true, result);
        verify(auditEventBuilder).build(
                eq(auditable),
                eq(OperationType.DELETE),
                eq("ENT-001"),
                eq("5"),
                argThat(data -> data.containsKey("entity")));
        verify(auditEventPublisher).publish(dto);
    }

    @Test
    @DisplayName("UPDATE TypeId - calcula diff, construye evento con changes y lo publica")
    void update_typeId_publicaEventoConDiff() throws Throwable {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "TYPE_ID");

        TypeId antes = buildTypeId(3L, "ENT-001");
        antes.setTypeIdname("Cédula");

        TypeId despues = buildTypeId(3L, "ENT-001");
        despues.setTypeIdname("Pasaporte");

        Object[] args = { despues };

        when(joinPoint.getArgs()).thenReturn(args);
        when(idOutputPort.getTypeIdById(3L)).thenReturn(antes, despues);
        when(joinPoint.proceed()).thenReturn(despues);

        OperationEventDto dto = OperationEventDto.builder().build();
        when(auditEventBuilder.build(any(), any(), any(), any(), any())).thenReturn(dto);

        Object result = aspect.audit(joinPoint, auditable);

        assertEquals(despues, result);
        verify(auditEventBuilder).build(
                eq(auditable),
                eq(OperationType.UPDATE),
                any(),
                any(),
                argThat(data -> data.containsKey("changes")));
        verify(auditEventPublisher).publish(dto);
    }

    @Test
    @DisplayName("INACTIVATE Third activo → resuelve INACTIVATE y publica")
    void inactivate_thirdActivo_publicaInactivate() throws Throwable {
        Auditable auditable = mockAuditable(OperationType.INACTIVATE, "THIRD");
        Third third = buildThird(10L, "ENT-001", true); // state = true
        Object[] args = { 10L, "ENT-001" };

        when(joinPoint.getArgs()).thenReturn(args);
        when(thirdOutputPort.getThirdById(10L, "ENT-001")).thenReturn(Optional.of(third));
        when(joinPoint.proceed()).thenReturn(true);

        OperationEventDto dto = OperationEventDto.builder().build();
        when(auditEventBuilder.build(any(), any(), any(), any(), any())).thenReturn(dto);

        aspect.audit(joinPoint, auditable);

        verify(auditEventBuilder).build(
                eq(auditable),
                eq(OperationType.INACTIVATE),
                any(), any(), any());
        verify(auditEventPublisher).publish(dto);
    }

    @Test
    @DisplayName("INACTIVATE Third inactivo → resuelve ACTIVATE y publica")
    void inactivate_thirdInactivo_publicaActivate() throws Throwable {
        Auditable auditable = mockAuditable(OperationType.INACTIVATE, "THIRD");
        Third third = buildThird(10L, "ENT-001", false);
        Object[] args = { 10L, "ENT-001" };

        when(joinPoint.getArgs()).thenReturn(args);
        when(thirdOutputPort.getThirdById(10L, "ENT-001")).thenReturn(Optional.of(third));
        when(joinPoint.proceed()).thenReturn(true);

        OperationEventDto dto = OperationEventDto.builder().build();
        when(auditEventBuilder.build(any(), any(), any(), any(), any())).thenReturn(dto);

        aspect.audit(joinPoint, auditable);

        verify(auditEventBuilder).build(
                eq(auditable),
                eq(OperationType.ACTIVATE),
                any(), any(), any());
        verify(auditEventPublisher).publish(dto);
    }

    @Test
    @DisplayName("Excepción en joinPoint.proceed() → se relanza y NO se publica evento")
    void negocioFalla_relanzaExcepcionSinAuditar() throws Throwable {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD");

        when(joinPoint.getArgs()).thenReturn(new Object[] {});
        when(joinPoint.proceed()).thenThrow(new RuntimeException("Fallo de negocio"));

        assertThrows(RuntimeException.class, () -> aspect.audit(joinPoint, auditable));

        verifyNoInteractions(auditEventBuilder, auditEventPublisher);
    }

    @Test
    @DisplayName("Excepción checked en joinPoint.proceed() → se relanza sin auditar")
    void negocioFalla_excepcionChecked_seRelanza() throws Throwable {
        Auditable auditable = mockAuditable(OperationType.DELETE, "TYPE_ID");

        when(joinPoint.getArgs()).thenReturn(new Object[] { 1L, "ENT-001" });
        when(joinPoint.proceed()).thenThrow(new Exception("Error de infraestructura"));

        assertThrows(Exception.class, () -> aspect.audit(joinPoint, auditable));

        verifyNoInteractions(auditEventBuilder, auditEventPublisher);
    }

    @Test
    @DisplayName("Error en auditEventBuilder → no propaga excepción, retorna result del negocio")
    void errorEnBuilder_noRompeNegocio() throws Throwable {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD");
        Third third = buildThird(1L, "ENT-001", true);

        when(joinPoint.getArgs()).thenReturn(new Object[] {});
        when(joinPoint.proceed()).thenReturn(third);
        when(auditEventBuilder.build(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Builder explotó"));

        Object result = assertDoesNotThrow(() -> aspect.audit(joinPoint, auditable));

        assertEquals(third, result);
        verifyNoInteractions(auditEventPublisher);
    }

    @Test
    @DisplayName("CREATE result desconocido → dataObject vacío, no publica evento")
    void create_dataObjectVacio_noPublica() throws Throwable {
        Auditable auditable = mockAuditable(OperationType.CREATE, "THIRD");

        when(joinPoint.getArgs()).thenReturn(new Object[] {});
        // result no es Third, TypeId ni ThirdType → dataObject queda vacío
        when(joinPoint.proceed()).thenReturn("resultado_inesperado");

        aspect.audit(joinPoint, auditable);

        verifyNoInteractions(auditEventBuilder, auditEventPublisher);
    }

    @Test
    @DisplayName("UPDATE sin cambios reales → changes vacío, no publica evento")
    void update_sinCambiosReales_noPublica() throws Throwable {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "TYPE_ID");
        TypeId typeId = buildTypeId(3L, "ENT-001");
        typeId.setTypeIdname("Cédula");
        Object[] args = { typeId };

        when(joinPoint.getArgs()).thenReturn(args);
        when(idOutputPort.getTypeIdById(3L)).thenReturn(typeId);
        when(joinPoint.proceed()).thenReturn(typeId);

        aspect.audit(joinPoint, auditable);

        verifyNoInteractions(auditEventBuilder, auditEventPublisher);
    }

    @Test
    @DisplayName("UPDATE con diff solo state before==after → resolvedType null, no publica")
    void update_resolvedTypeNull_noPublica() throws Throwable {
        Auditable auditable = mockAuditable(OperationType.UPDATE, "TYPE_ID");

        TypeId antes = buildTypeId(3L, "ENT-001");
        antes.setTypeIdname("Cédula");
        when(idOutputPort.getTypeIdById(3L)).thenReturn(antes);

        Object[] args = { antes };
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.proceed()).thenReturn(antes);

        aspect.audit(joinPoint, auditable);

        verifyNoInteractions(auditEventBuilder, auditEventPublisher);
    }

    // Helper
    private Auditable mockAuditable(OperationType type, String table) {
        Auditable a = mock(Auditable.class);
        lenient().when(a.operationType()).thenReturn(type);
        lenient().when(a.affectedTable()).thenReturn(table);
        lenient().when(a.idArgIndex()).thenReturn(0);
        lenient().when(a.enterpriseIdArgIndex()).thenReturn(1);
        return a;
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

    // Sub clase
    private static class TestableAuditAspect extends AuditAspect {
        public TestableAuditAspect(AuditEventBuilder auditEventBuilder, AuditEventPublisher auditEventPublisher,
                ThirdOutputPort thirdOutputPort,
                IdOutputPort idOutputPort) {
            super(auditEventBuilder, auditEventPublisher, thirdOutputPort, idOutputPort);
        }

        public Map<String, Object> testCaptureBeforeData(Auditable auditable, Object[] args) {
            return super.captureBeforeData(auditable, args);
        }

        public Map<String, Object> testFetchCurrentState(Auditable auditable, Object[] args) {
            return super.fetchCurrentState(auditable, args);
        }

        public OperationType testResolveFinalOperationType(Auditable auditable, Map<String, Object> beforeData,
                Map<String, Object> diff) {
            return super.resolveFinalOperationType(auditable, beforeData, diff);
        }

        public Map<String, Object> testBuildDataObject(OperationType resolvedType, Object[] args, Object result,
                Auditable auditable,
                Map<String, Object> beforeData, Map<String, Object> diff) {
            return super.buildDataObject(resolvedType, args, result, auditable, beforeData, diff);
        }

        public Map<String, Object> testBuildDiff(Map<String, Object> before, Map<String, Object> after) {
            return super.buildDiff(before, after);
        }

        public Map<String, Object> testBuildContext(Class<?> entityClass, Map<String, Object> data) {
            return super.buildContext(entityClass, data);
        }

        public String testResolveEnterpriseId(Auditable auditable, Object[] args, Object result,
                Map<String, Object> beforeData) {
            return super.resolveEnterpriseId(auditable, args, result, beforeData);
        }

        public String testResolveRegisterId(Auditable auditable, Object[] args, Object result,
                Map<String, Object> beforeData) {
            return super.resolveRegisterId(auditable, args, result, beforeData);
        }
    }

}
