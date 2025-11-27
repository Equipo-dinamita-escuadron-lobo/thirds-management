package com.thirdsmanagement.thirds.unit.infrastructure.adapters.output.persistence.messageBroker;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.amqp.core.Message;

import com.rabbitmq.client.Channel;
import com.thirdsmanagement.thirds.application.ports.input.IThirdUsagePort;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.messageBroker.ThirdUsageListener;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.messageBroker.dto.EventDto;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.messageBroker.dto.ThirdUsageEventDto;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.messageBroker.enums.EventUsageType;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ThirdUsageListenerUnitTest {

    @Mock
    private IThirdUsagePort thirdUsagePort;

    @Mock
    private Channel channel;

    @Mock
    private Message message;

    private ThirdUsageListener listener;

    private EventDto<ThirdUsageEventDto, EventUsageType> validEvent;
    private ThirdUsageEventDto validData;
    private long deliveryTag;

    @BeforeEach
    void setUp() {
        listener = new ThirdUsageListener(thirdUsagePort);
        deliveryTag = 123L;

        validData = new ThirdUsageEventDto(1L, "ENT001", 5);
        validEvent = new EventDto<>(validData, EventUsageType.USED);
    }

    @Test
    @DisplayName("Debe manejar evento válido y enviar acknowledgment")
    void testHandleThirdEventWithValidEvent() throws Exception {
        // Arrange
        doNothing().when(thirdUsagePort).incrementUsageCount(1L);
        doNothing().when(channel).basicAck(deliveryTag, false);

        // Act
        listener.handleThirdEvent(validEvent, message, channel, deliveryTag);

        // Assert
        verify(thirdUsagePort, times(1)).incrementUsageCount(1L);
        verify(channel, times(1)).basicAck(deliveryTag, false);
    }

    @Test
    @DisplayName("Debe procesar evento con cantidad usada grande")
    void testHandleThirdEventWithLargeQuantity() throws Exception {
        // Arrange
        ThirdUsageEventDto largeData = new ThirdUsageEventDto(1L, "ENT001", 1000000);
        EventDto<ThirdUsageEventDto, EventUsageType> largeEvent = new EventDto<>(largeData, EventUsageType.USED);

        doNothing().when(thirdUsagePort).incrementUsageCount(1L);
        doNothing().when(channel).basicAck(deliveryTag, false);

        // Act
        listener.handleThirdEvent(largeEvent, message, channel, deliveryTag);

        // Assert
        verify(thirdUsagePort, times(1)).incrementUsageCount(1L);
        verify(channel, times(1)).basicAck(deliveryTag, false);
    }

    @Test
    @DisplayName("Debe procesar evento correctamente cuando enterpriseId es null")
    void testHandleThirdEventWithNullEnterpriseId() throws Exception {
        // Arrange
        ThirdUsageEventDto dataWithoutEnterprise = new ThirdUsageEventDto(1L, null, 5);
        EventDto<ThirdUsageEventDto, EventUsageType> eventWithoutEnterprise = new EventDto<>(dataWithoutEnterprise, EventUsageType.USED);

        doNothing().when(thirdUsagePort).incrementUsageCount(1L);
        doNothing().when(channel).basicAck(deliveryTag, false);

        // Act
        listener.handleThirdEvent(eventWithoutEnterprise, message, channel, deliveryTag);

        // Assert
        verify(thirdUsagePort, times(1)).incrementUsageCount(1L);
        verify(channel, times(1)).basicAck(deliveryTag, false);
    }

    @Test
    @DisplayName("No debe procesar cuando evento es null")
    void testHandleThirdEventWithNullEvent() throws Exception {
        // Arrange & Act
        listener.handleThirdEvent(null, message, channel, deliveryTag);

        // Assert
        verify(thirdUsagePort, never()).incrementUsageCount(anyLong());
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("No debe procesar cuando data del evento es null")
    void testHandleThirdEventWithNullData() throws Exception {
        // Arrange
        EventDto<ThirdUsageEventDto, EventUsageType> eventWithNullData = new EventDto<>(null, EventUsageType.USED);

        // Act
        listener.handleThirdEvent(eventWithNullData, message, channel, deliveryTag);

        // Assert
        verify(thirdUsagePort, never()).incrementUsageCount(anyLong());
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("No debe procesar cuando thirdId es null")
    void testHandleThirdEventWithNullThirdId() throws Exception {
        // Arrange
        ThirdUsageEventDto dataWithNullId = new ThirdUsageEventDto(null, "ENT001", 5);
        EventDto<ThirdUsageEventDto, EventUsageType> eventWithNullId = new EventDto<>(dataWithNullId, EventUsageType.USED);

        // Act
        listener.handleThirdEvent(eventWithNullId, message, channel, deliveryTag);

        // Assert
        verify(thirdUsagePort, never()).incrementUsageCount(anyLong());
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("No debe procesar cuando quantityUsed es null")
    void testHandleThirdEventWithNullQuantityUsed() throws Exception {
        // Arrange
        ThirdUsageEventDto dataWithNullQuantity = new ThirdUsageEventDto(1L, "ENT001", null);
        EventDto<ThirdUsageEventDto, EventUsageType> eventWithNullQuantity = new EventDto<>(dataWithNullQuantity, EventUsageType.USED);

        // Act
        listener.handleThirdEvent(eventWithNullQuantity, message, channel, deliveryTag);

        // Assert
        verify(thirdUsagePort, never()).incrementUsageCount(anyLong());
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("No debe procesar cuando quantityUsed es cero")
    void testHandleThirdEventWithZeroQuantityUsed() throws Exception {
        // Arrange
        ThirdUsageEventDto dataWithZeroQuantity = new ThirdUsageEventDto(1L, "ENT001", 0);
        EventDto<ThirdUsageEventDto, EventUsageType> eventWithZeroQuantity = new EventDto<>(dataWithZeroQuantity, EventUsageType.USED);

        // Act
        listener.handleThirdEvent(eventWithZeroQuantity, message, channel, deliveryTag);

        // Assert
        verify(thirdUsagePort, never()).incrementUsageCount(anyLong());
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("No debe procesar cuando quantityUsed es negativo")
    void testHandleThirdEventWithNegativeQuantityUsed() throws Exception {
        // Arrange
        ThirdUsageEventDto dataWithNegativeQuantity = new ThirdUsageEventDto(1L, "ENT001", -5);
        EventDto<ThirdUsageEventDto, EventUsageType> eventWithNegativeQuantity = new EventDto<>(dataWithNegativeQuantity, EventUsageType.USED);

        // Act
        listener.handleThirdEvent(eventWithNegativeQuantity, message, channel, deliveryTag);

        // Assert
        verify(thirdUsagePort, never()).incrementUsageCount(anyLong());
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("Debe enviar acknowledgment cuando procesamiento falla")
    void testHandleThirdEventWhenProcessingFails() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Processing error"))
                .when(thirdUsagePort).incrementUsageCount(1L);
        doNothing().when(channel).basicAck(deliveryTag, false);

        // Act
        listener.handleThirdEvent(validEvent, message, channel, deliveryTag);

        // Assert
        verify(thirdUsagePort, times(1)).incrementUsageCount(1L);
        verify(channel, times(1)).basicAck(deliveryTag, false);
    }

    @Test
    @DisplayName("Debe propagar excepción de base de datos")
    void testHandleThirdEventPropagatesDatabaseException() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Database error"))
                .when(thirdUsagePort).incrementUsageCount(1L);
        doNothing().when(channel).basicAck(deliveryTag, false);

        // Act
        listener.handleThirdEvent(validEvent, message, channel, deliveryTag);

        // Assert
        verify(thirdUsagePort, times(1)).incrementUsageCount(1L);
        verify(channel, times(1)).basicAck(deliveryTag, false);
    }

    @Test
    @DisplayName("Debe manejar múltiples eventos consecutivos")
    void testHandleMultipleConsecutiveEvents() throws Exception {
        // Arrange
        doNothing().when(thirdUsagePort).incrementUsageCount(anyLong());
        doNothing().when(channel).basicAck(anyLong(), anyBoolean());

        ThirdUsageEventDto data2 = new ThirdUsageEventDto(2L, "ENT002", 10);
        EventDto<ThirdUsageEventDto, EventUsageType> event2 = new EventDto<>(data2, EventUsageType.USED);

        ThirdUsageEventDto data3 = new ThirdUsageEventDto(3L, "ENT003", 15);
        EventDto<ThirdUsageEventDto, EventUsageType> event3 = new EventDto<>(data3, EventUsageType.USED);

        // Act
        listener.handleThirdEvent(validEvent, message, channel, deliveryTag);
        listener.handleThirdEvent(event2, message, channel, deliveryTag + 1);
        listener.handleThirdEvent(event3, message, channel, deliveryTag + 2);

        // Assert
        verify(thirdUsagePort, times(1)).incrementUsageCount(1L);
        verify(thirdUsagePort, times(1)).incrementUsageCount(2L);
        verify(thirdUsagePort, times(1)).incrementUsageCount(3L);
        verify(channel, times(3)).basicAck(anyLong(), eq(false));
    }

    @Test
    @DisplayName("Debe manejar eventos válidos e inválidos intercalados")
    void testHandleMixedValidAndInvalidEvents() throws Exception {
        // Arrange
        doNothing().when(thirdUsagePort).incrementUsageCount(anyLong());
        doNothing().when(channel).basicAck(anyLong(), anyBoolean());

        ThirdUsageEventDto invalidData = new ThirdUsageEventDto(null, "ENT002", 10);
        EventDto<ThirdUsageEventDto, EventUsageType> invalidEvent = new EventDto<>(invalidData, EventUsageType.USED);

        // Act
        listener.handleThirdEvent(validEvent, message, channel, deliveryTag);
        listener.handleThirdEvent(invalidEvent, message, channel, deliveryTag + 1);
        listener.handleThirdEvent(validEvent, message, channel, deliveryTag + 2);

        // Assert
        verify(thirdUsagePort, times(2)).incrementUsageCount(1L);
        verify(channel, times(2)).basicAck(anyLong(), eq(false));
    }

    @Test
    @DisplayName("Debe procesar eventos de diferentes terceros")
    void testHandleEventsFromDifferentThirds() throws Exception {
        // Arrange
        doNothing().when(thirdUsagePort).incrementUsageCount(anyLong());
        doNothing().when(channel).basicAck(anyLong(), anyBoolean());

        ThirdUsageEventDto data2 = new ThirdUsageEventDto(999L, "ENT999", 100);
        EventDto<ThirdUsageEventDto, EventUsageType> event2 = new EventDto<>(data2, EventUsageType.USED);

        // Act
        listener.handleThirdEvent(validEvent, message, channel, deliveryTag);
        listener.handleThirdEvent(event2, message, channel, deliveryTag + 1);

        // Assert
        verify(thirdUsagePort, times(1)).incrementUsageCount(1L);
        verify(thirdUsagePort, times(1)).incrementUsageCount(999L);
        verify(channel, times(2)).basicAck(anyLong(), eq(false));
    }

    @Test
    @DisplayName("Debe manejar evento con todos los campos mínimos requeridos")
    void testHandleEventWithMinimalRequiredFields() throws Exception {
        // Arrange
        ThirdUsageEventDto minimalData = new ThirdUsageEventDto();
        minimalData.setThirdId(1L);
        minimalData.setQuantityUsed(1);

        EventDto<ThirdUsageEventDto, EventUsageType> minimalEvent = new EventDto<>(minimalData, EventUsageType.USED);

        doNothing().when(thirdUsagePort).incrementUsageCount(1L);
        doNothing().when(channel).basicAck(deliveryTag, false);

        // Act
        listener.handleThirdEvent(minimalEvent, message, channel, deliveryTag);

        // Assert
        verify(thirdUsagePort, times(1)).incrementUsageCount(1L);
        verify(channel, times(1)).basicAck(deliveryTag, false);
    }

    @Test
    @DisplayName("Debe manejar correctamente mismo tercero múltiples veces")
    void testHandleSameThirdMultipleTimes() throws Exception {
        // Arrange
        doNothing().when(thirdUsagePort).incrementUsageCount(1L);
        doNothing().when(channel).basicAck(anyLong(), anyBoolean());

        // Act
        listener.handleThirdEvent(validEvent, message, channel, deliveryTag);
        listener.handleThirdEvent(validEvent, message, channel, deliveryTag + 1);
        listener.handleThirdEvent(validEvent, message, channel, deliveryTag + 2);

        // Assert
        verify(thirdUsagePort, times(3)).incrementUsageCount(1L);
        verify(channel, times(3)).basicAck(anyLong(), eq(false));
    }
}
