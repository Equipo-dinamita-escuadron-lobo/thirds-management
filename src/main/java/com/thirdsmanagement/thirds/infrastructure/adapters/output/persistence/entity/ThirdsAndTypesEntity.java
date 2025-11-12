package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity;

import org.hibernate.annotations.TenantId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @brief Entidad intermedia para relación muchos a muchos terceros-tipos con multi-tenancy
 */
@Entity
@Table(name = "thirds_and_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ThirdsAndTypesId.class)
public class ThirdsAndTypesEntity {

    @Id
    @Column(name = "th_id")
    private Long thId;

    @Id
    @Column(name = "tt_id")
    private Long ttId;

    @TenantId
    @Column(name = "tenant_id")
    private String tenantId;

    @ManyToOne
    @JoinColumn(name = "th_id", insertable = false, updatable = false)
    private ThirdEntity third;

    @ManyToOne
    @JoinColumn(name = "tt_id", insertable = false, updatable = false)
    private ThirdTypeEntity thirdType;
}
