package com.thirdsmanagement.thirds.domain.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento de actualización de estado de tercero.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThirdStateUpdateEvent {
    /**
     * Identificador del tercero.
     */
    private Long thId;

    /**
     * Fecha de actualización de estado.
     */
    private LocalDateTime date;

    /**
     * Constructor de la clase.
     * @param thId Identificador del tercero.
     */
    public ThirdStateUpdateEvent(Long thId){
        this.thId = thId;
        this.date = LocalDateTime.now();
    }
}
