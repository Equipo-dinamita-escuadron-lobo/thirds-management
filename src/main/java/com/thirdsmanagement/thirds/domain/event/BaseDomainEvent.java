package com.thirdsmanagement.thirds.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Clase base abstracta para todos los eventos de dominio en el sistema de terceros.
 *
 * Esta clase proporciona funcionalidad común para todos los eventos de dominio,
 * incluyendo identificadores únicos, timestamps y métodos de validación.
 *
 * Patrón: Domain Event Base Class
 *
 * @author Sistema de Gestión de Terceros
 * @version 1.0
 * @since 2024
 */
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDomainEvent {

    /**
     * Identificador único del evento.
     * Se genera automáticamente para garantizar unicidad a través del sistema.
     */
    @NotNull(message = "El ID del evento no puede ser null")
    @lombok.Builder.Default
    private String eventId = UUID.randomUUID().toString();

    /**
     * Fecha y hora en que ocurrió el evento.
     */
    @NotNull(message = "La fecha del evento no puede ser null")
    @PastOrPresent(message = "La fecha del evento debe ser en el pasado o presente")
    @lombok.Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Versión del evento para manejo de cambios en el esquema.
     */
    @lombok.Builder.Default
    private int version = 1;

    /**
     * Obtiene el tipo del evento para categorización y enrutamiento.
     *
     * @return tipo del evento como cadena
     */
    public abstract String getEventType();

    /**
     * Verifica si el evento es válido.
     *
     * @return true si el evento tiene todos los campos requeridos válidos
     */
    public boolean isValid() {
        return eventId != null && !eventId.trim().isEmpty() &&
               timestamp != null && timestamp.isBefore(LocalDateTime.now().plusSeconds(1));
    }

    /**
     * Obtiene una descripción del evento para logging y auditoría.
     *
     * @return descripción del evento
     */
    public abstract String getEventDescription();

    /**
     * Verifica si el evento ocurrió dentro de un período de tiempo específico.
     *
     * @param since fecha desde la cual verificar
     * @return true si el evento ocurrió después de la fecha especificada
     */
    public boolean occurredAfter(LocalDateTime since) {
        return timestamp != null && timestamp.isAfter(since);
    }

    /**
     * Verifica si el evento ocurrió antes de una fecha específica.
     *
     * @param before fecha límite
     * @return true si el evento ocurrió antes de la fecha especificada
     */
    public boolean occurredBefore(LocalDateTime before) {
        return timestamp != null && timestamp.isBefore(before);
    }

    /**
     * Obtiene la edad del evento en minutos.
     *
     * @return edad del evento en minutos
     */
    public long getAgeInMinutes() {
        if (timestamp == null) {
            return 0;
        }
        return java.time.Duration.between(timestamp, LocalDateTime.now()).toMinutes();
    }

    /**
     * Formatea el timestamp del evento como cadena ISO.
     *
     * @return timestamp en formato ISO 8601
     */
    public String getFormattedTimestamp() {
        return timestamp != null ? timestamp.toString() : "N/A";
    }

    @Override
    public String toString() {
        return String.format("%s[eventId=%s, timestamp=%s, version=%d]",
                           getEventType(), eventId, getFormattedTimestamp(), version);
    }
}
