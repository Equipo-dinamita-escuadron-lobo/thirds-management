package com.thirdsmanagement.thirds.domain.event;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento de creación de tipo de tercero.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThirdTypeCreatedEvent {
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
     * Fecha de creación del tipo de tercero.
     */
    private LocalDateTime date;

    /**
     * Constructor de la clase.
     * @param id Identificador del tipo de tercero.
     */
    public ThirdTypeCreatedEvent(Long id) {
        this.thirdTypeId = id;
        this.date = LocalDateTime.now();
    }
}

