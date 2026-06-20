package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity;

import org.hibernate.annotations.TenantId;

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
 * @brief Entidad JPA para tipos de identificación con clasificación de persona
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

    @Builder.Default
    @Column(name = "ti_status")
    private Boolean status = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "ti_classification")
    private PersonClassification classification;
}
