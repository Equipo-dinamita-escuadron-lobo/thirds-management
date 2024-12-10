package com.thirdsmanagement.thirds.domain.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento de creación de tercero.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThirdCreatedEvent {
    /**
     * Identificador del tercero.
     */
    private Long thId;

    /**
     * Fecha de creación del tercero.
     */
    private LocalDateTime date;

    /**
     * Constructor de la clase.
     * 
     * @param id Identificador del tercero.
     */
    public ThirdCreatedEvent(Long id) {
        this.thId = id;
        this.date = LocalDateTime.now();
    }
}
