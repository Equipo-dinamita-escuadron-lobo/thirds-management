package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TenantId;
import org.hibernate.annotations.UpdateTimestamp;

import com.thirdsmanagement.thirds.domain.model.PersonClassification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
 * La tabla tiene una clave primaria autoincrementable id.
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ti_id")
    private Long id;

    @Column(name = "ti_code")
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

    @Builder.Default
    @Column(name = "ti_status")
    private Boolean status = true;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "ti_classification")
    private PersonClassification classification = PersonClassification.NATURAL_PERSON;
}
