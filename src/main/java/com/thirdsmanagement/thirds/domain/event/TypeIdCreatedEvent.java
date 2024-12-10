package com.thirdsmanagement.thirds.domain.event;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento de creación de tipo.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TypeIdCreatedEvent {
    /**
     * Identificador del tipo de identificacion.
     */
    private String typeId;

    /**
     * Fecha de creación del tipo de identificación.
     */
    private LocalDateTime date;

    /**
     * Constructor de la clase.
     * @param id Identificador del tipo de identificación.
     */
    public TypeIdCreatedEvent(String id) {
        this.typeId = id;
        this.date = LocalDateTime.now();
    }
}
