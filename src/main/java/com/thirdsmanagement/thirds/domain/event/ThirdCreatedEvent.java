package com.thirdsmanagement.thirds.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Evento de dominio que se dispara cuando se crea un nuevo tercero en el sistema.
 *
 * Este evento encapsula toda la información relevante sobre la creación de un tercero,
 * permitiendo a otros componentes del sistema reaccionar ante este cambio.
 *
 * Patrón: Domain Event
 *
 * @see Third
 * @see ThirdUpdateEvent
 * @see ThirdStateUpdateEvent
 */
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = {"eventId", "thId"})
@ToString(of = {"eventId", "thId", "date"})
@NoArgsConstructor
@AllArgsConstructor
public class ThirdCreatedEvent {

    /**
     * Identificador único del evento.
     * Se genera automáticamente para garantizar unicidad.
     */
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    /**
     * Identificador del tercero que fue creado.
     */
    @NotNull(message = "El ID del tercero no puede ser null")
    private Long thId;

    /**
     * Identificador de la empresa a la que pertenece el tercero.
     */
    private String entId;

    /**
     * Fecha y hora en que se creó el tercero.
     */
    @Builder.Default
    @PastOrPresent(message = "La fecha de creación debe ser en el pasado o presente")
    private LocalDateTime date = LocalDateTime.now();

    /**
     * Tipo de persona del tercero creado.
     */
    private String personType;

    /**
     * Constructor principal del evento.
     *
     * @param thId identificador del tercero creado
     * @param entId identificador de la empresa
     * @param personType tipo de persona (Natural/Jurídica)
     * @throws IllegalArgumentException si el ID del tercero es null
     */
    public ThirdCreatedEvent(Long thId, String entId, String personType) {
        if (thId == null) {
            throw new IllegalArgumentException("El ID del tercero no puede ser null");
        }

        this.eventId = UUID.randomUUID().toString();
        this.thId = thId;
        this.entId = entId;
        this.personType = personType;
        this.date = LocalDateTime.now();
    }

    /**
     * Constructor simplificado del evento.
     *
     * @param thId identificador del tercero creado
     * @throws IllegalArgumentException si el ID del tercero es null
     */
    public ThirdCreatedEvent(Long thId) {
        this(thId, null, null);
    }

    /**
     * Obtiene el tipo del evento para categorización.
     *
     * @return tipo del evento
     */
    public String getEventType() {
        return "THIRD_CREATED";
    }

    /**
     * Verifica si el evento es válido.
     *
     * @return true si el evento tiene todos los campos requeridos
     */
    public boolean isValid() {
        return thId != null && eventId != null && date != null;
    }

    /**
     * Obtiene una descripción del evento para logging.
     *
     * @return descripción del evento
     */
    public String getEventDescription() {
        return String.format("Tercero creado - ID: %s, Empresa: %s, Tipo: %s",
                           thId, entId != null ? entId : "N/A",
                           personType != null ? personType : "N/A");
    }
}
