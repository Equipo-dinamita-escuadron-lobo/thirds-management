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
 * Evento de dominio que se dispara cuando cambia el estado de un tercero.
 *
 * Este evento informa sobre activaciones o desactivaciones de terceros,
 * permitiendo a otros componentes del sistema reaccionar ante estos cambios de estado.
 *
 * Patrón: Domain Event
 *
 * @see Third
 * @see ThirdCreatedEvent
 * @see ThirdUpdateEvent
 */
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = {"eventId", "thId"})
@ToString(of = {"eventId", "thId", "newState", "date"})
@NoArgsConstructor
@AllArgsConstructor
public class ThirdStateUpdateEvent {

    /**
     * Identificador único del evento.
     * Se genera automáticamente para garantizar unicidad.
     */
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    /**
     * Identificador del tercero cuyo estado cambió.
     */
    @NotNull(message = "El ID del tercero no puede ser null")
    private Long thId;

    /**
     * Identificador de la empresa a la que pertenece el tercero.
     */
    private String entId;

    /**
     * Nuevo estado del tercero: true (activo) o false (inactivo).
     */
    @NotNull(message = "El nuevo estado no puede ser null")
    private Boolean newState;

    /**
     * Estado anterior del tercero antes del cambio.
     */
    private Boolean previousState;

    /**
     * Fecha y hora en que se cambió el estado del tercero.
     */
    @Builder.Default
    @PastOrPresent(message = "La fecha de cambio de estado debe ser en el pasado o presente")
    private LocalDateTime date = LocalDateTime.now();

    /**
     * Usuario que realizó el cambio de estado.
     */
    private String changedBy;

    /**
     * Constructor principal del evento.
     *
     * @param thId identificador del tercero
     * @param entId identificador de la empresa
     * @param newState nuevo estado del tercero
     * @param previousState estado anterior del tercero
     * @param changedBy usuario que realizó el cambio
     * @throws IllegalArgumentException si el ID del tercero es null
     */
    public ThirdStateUpdateEvent(Long thId, String entId, Boolean newState, Boolean previousState, String changedBy) {
        if (thId == null) {
            throw new IllegalArgumentException("El ID del tercero no puede ser null");
        }
        if (newState == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser null");
        }

        this.eventId = UUID.randomUUID().toString();
        this.thId = thId;
        this.entId = entId;
        this.newState = newState;
        this.previousState = previousState;
        this.changedBy = changedBy;
        this.date = LocalDateTime.now();
    }

    /**
     * Constructor simplificado del evento.
     *
     * @param thId identificador del tercero
     * @throws IllegalArgumentException si el ID del tercero es null
     */
    public ThirdStateUpdateEvent(Long thId) {
        if (thId == null) {
            throw new IllegalArgumentException("El ID del tercero no puede ser null");
        }
        
        this.eventId = UUID.randomUUID().toString();
        this.thId = thId;
        this.date = LocalDateTime.now();
        // No validamos newState como null en este constructor simplificado
    }

    /**
     * Constructor con estado nuevo.
     *
     * @param thId identificador del tercero
     * @param newState nuevo estado del tercero
     * @throws IllegalArgumentException si algún parámetro requerido es null
     */
    public ThirdStateUpdateEvent(Long thId, Boolean newState) {
        this(thId, null, newState, null, null);
    }

    /**
     * Obtiene el tipo del evento para categorización.
     *
     * @return tipo del evento
     */
    public String getEventType() {
        return "THIRD_STATE_UPDATED";
    }

    /**
     * Verifica si el evento representa una activación.
     *
     * @return true si el tercero fue activado
     */
    public boolean isActivation() {
        return Boolean.TRUE.equals(newState) &&
               (previousState == null || Boolean.FALSE.equals(previousState));
    }

    /**
     * Verifica si el evento representa una desactivación.
     *
     * @return true si el tercero fue desactivado
     */
    public boolean isDeactivation() {
        return Boolean.FALSE.equals(newState) &&
               (previousState == null || Boolean.TRUE.equals(previousState));
    }

    /**
     * Verifica si el evento es válido.
     *
     * @return true si el evento tiene todos los campos requeridos
     */
    public boolean isValid() {
        return thId != null && eventId != null && newState != null && date != null;
    }

    /**
     * Obtiene una descripción del evento para logging.
     *
     * @return descripción del evento
     */
    public String getEventDescription() {
        String action = Boolean.TRUE.equals(newState) ? "Activado" : "Desactivado";
        String previous = previousState != null ?
                         (Boolean.TRUE.equals(previousState) ? "activo" : "inactivo") : "desconocido";

        return String.format("Estado de tercero cambiado - ID: %s, Empresa: %s, Acción: %s (era %s), Usuario: %s",
                           thId, entId != null ? entId : "N/A",
                           action, previous,
                           changedBy != null ? changedBy : "Sistema");
    }
}
