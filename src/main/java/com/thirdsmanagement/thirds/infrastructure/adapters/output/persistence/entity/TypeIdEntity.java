package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TenantId;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa la entidad de la tabla type_id.
 * Contiene la información de un tipo de identificación.
 * La tabla tiene una clave primaria identificada por ti_id.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "TYPE_ID")
public class TypeIdEntity {
    @Id
    @Column(name = "ti_id")
    private String tiId;

    @Column(name = "ti_name")
    private String tiName;

    @Column(name = "ti_entid")
    private String tientId;

    @TenantId
    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "ti_created_at")
    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column(name = "ti_updated_at")
    @UpdateTimestamp
    private LocalDateTime updateDate;
}
