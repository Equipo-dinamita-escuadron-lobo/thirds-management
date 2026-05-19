package com.thirdsmanagement.thirds.copy.infrastructure.adapters.output.persistence.jpa;

import com.thirdsmanagement.thirds.copy.domain.enums.CopyEstado;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Entidad JPA para el log de idempotencia del proceso de copia de terceros.
 * Restricción única: (id_proceso, fase, modulo) garantiza idempotencia.
 */
@Entity
@Table(
        name = "copy_job_log",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_copy_job_log_proceso_fase_modulo",
                columnNames = {"id_proceso", "fase", "modulo"}
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CopyJobLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** UUID del proceso coordinado por el orquestador */
    @Column(name = "id_proceso", nullable = false, length = 36)
    private String idProceso;

    /** Número de fase */
    @Column(name = "fase", nullable = false)
    private int fase;

    /** Nombre del módulo ('thirds') */
    @Column(name = "modulo", nullable = false, length = 50)
    private String modulo;

    /** Estado del proceso */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 50)
    private CopyEstado estado;

    /** Cantidad de registros procesados */
    @Builder.Default
    @Column(name = "registros_procesados", nullable = false)
    private int registrosProcesados = 0;

    /** Cantidad de equivalencias generadas */
    @Builder.Default
    @Column(name = "equivalencias_generadas", nullable = false)
    private int equivalenciasGeneradas = 0;

    /** Advertencias en formato JSON */
    @Column(name = "advertencias", columnDefinition = "TEXT")
    private String advertencias;

    /** Mensaje descriptivo del resultado */
    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    /** Fecha y hora de inicio */
    @Column(name = "fecha_inicio", nullable = false)
    private Instant fechaInicio;

    /** Fecha y hora de finalización */
    @Column(name = "fecha_fin")
    private Instant fechaFin;
}
