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
 * Evento de dominio que se dispara cuando se actualiza un tercero existente en el sistema.
 *
 * Este evento informa sobre modificaciones realizadas a un tercero, permitiendo
 * a otros componentes del sistema mantenerse sincronizados y reaccionar ante cambios.
 *
 * Patrón: Domain Event
 *
 * @see Third
 * @see ThirdCreatedEvent
 * @see ThirdStateUpdateEvent
 */
@Builder
@Getter
@Setter
@EqualsAndHashCode(of = {"eventId", "thId"})
@ToString(of = {"eventId", "thId", "date"})
@NoArgsConstructor
@AllArgsConstructor
public class ThirdUpdateEvent {

    /**
     * Identificador único del evento.
     * Se genera automáticamente para garantizar unicidad.
     */
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    /**
     * Identificador del tercero que fue actualizado.
     */
    @NotNull(message = "El ID del tercero no puede ser null")
    private Long thId;

    /**
     * Identificador de la empresa a la que pertenece el tercero.
     */
    private String entId;

    /**
     * Fecha y hora en que se actualizó el tercero.
     */
    @Builder.Default
    @PastOrPresent(message = "La fecha de actualización debe ser en el pasado o presente")
    private LocalDateTime date = LocalDateTime.now();

    /**
     * Lista de campos que fueron modificados en la actualización.
     * Útil para auditoría y notificaciones selectivas.
     */
    private java.util.List<String> modifiedFields;

    /**
     * Usuario que realizó la actualización.
     */
    private String updatedBy;

    /**
     * Constructor principal del evento.
     *
     * @param thId identificador del tercero actualizado
     * @param entId identificador de la empresa
     * @param modifiedFields lista de campos modificados
     * @param updatedBy usuario que realizó la actualización
     * @throws IllegalArgumentException si el ID del tercero es null
     */
    public ThirdUpdateEvent(Long thId, String entId, java.util.List<String> modifiedFields, String updatedBy) {
        if (thId == null) {
            throw new IllegalArgumentException("El ID del tercero no puede ser null");
        }

        this.eventId = UUID.randomUUID().toString();
        this.thId = thId;
        this.entId = entId;
        this.modifiedFields = modifiedFields != null ? java.util.List.copyOf(modifiedFields) : null;
        this.updatedBy = updatedBy;
        this.date = LocalDateTime.now();
    }

    /**
     * Constructor simplificado del evento.
     *
     * @param thId identificador del tercero actualizado
     * @throws IllegalArgumentException si el ID del tercero es null
     */
    public ThirdUpdateEvent(Long thId) {
        this(thId, null, null, null);
    }

    /**
     * Obtiene el tipo del evento para categorización.
     *
     * @return tipo del evento
     */
    public String getEventType() {
        return "THIRD_UPDATED";
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
     * Verifica si se modificaron campos específicos.
     *
     * @param fieldName nombre del campo a verificar
     * @return true si el campo fue modificado
     */
    public boolean wasFieldModified(String fieldName) {
        return modifiedFields != null && modifiedFields.contains(fieldName);
    }

    /**
     * Obtiene una descripción del evento para logging.
     *
     * @return descripción del evento
     */
    public String getEventDescription() {
        String fieldsInfo = modifiedFields != null && !modifiedFields.isEmpty()
                          ? "Campos modificados: " + String.join(", ", modifiedFields)
                          : "Campos no especificados";

        return String.format("Tercero actualizado - ID: %s, Empresa: %s, Usuario: %s, %s",
                           thId, entId != null ? entId : "N/A",
                           updatedBy != null ? updatedBy : "Sistema",
                           fieldsInfo);
    }
}
