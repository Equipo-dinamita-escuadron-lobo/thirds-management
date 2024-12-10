package com.thirdsmanagement.thirds.domain.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento de obtención de tercero.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThirdGetEvent {
    /**
     * Identificador del tercero.
     */
    private Long thId;

    /**
     * Fecha de obtención del tercero.
     */
    private LocalDateTime date;

    /**
     * Constructor de la clase.
     * @param thId Identificador del tercero.
     */
    public ThirdGetEvent(Long thId){
        this.thId = thId;
        this.date = LocalDateTime.now();
    }
}
