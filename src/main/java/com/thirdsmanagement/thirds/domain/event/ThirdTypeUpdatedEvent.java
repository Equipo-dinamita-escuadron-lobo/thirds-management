package com.thirdsmanagement.thirds.domain.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento de actualización de tipo de tercero.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThirdTypeUpdatedEvent {
    /**
     * Identificador del tipo de tercero.
     */
    private Long thirdTypeId;

    /**
     * Nombre del tipo de tercero.
     */
    private String thirdTypeName;

    /**
     * ID de la entidad.
     */
    private String entId;

    /**
     * Fecha de actualización del tipo de tercero.
     */
    private LocalDateTime date;

    /**
     * Constructor de la clase.
     * @param id Identificador del tipo de tercero.
     * @param name Nombre del tipo de tercero.
     * @param entId ID de la entidad.
     */
    public ThirdTypeUpdatedEvent(Long id, String name, String entId) {
        this.thirdTypeId = id;
        this.thirdTypeName = name;
        this.entId = entId;
        this.date = LocalDateTime.now();
    }
}
