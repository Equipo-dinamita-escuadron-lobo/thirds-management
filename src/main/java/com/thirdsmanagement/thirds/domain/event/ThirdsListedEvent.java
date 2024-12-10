package com.thirdsmanagement.thirds.domain.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento de listado de terceros.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThirdsListedEvent {
    /**
     * Identificador de la entidad.
     */
    private Long entId;

    /**
     * Fecha de listado de terceros.
     */
    private LocalDateTime date;

    /**
     * Constructor de la clase.
     * @param entId Identificador de la entidad.
     */
    public ThirdsListedEvent(Long entId){
        this.entId = entId;
        this.date = LocalDateTime.now();
    }

}
