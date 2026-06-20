package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.TenantId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @brief Entidad JPA para tipos de tercero con multi-tenancy
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "THIRD_TYPE")
public class ThirdTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tt_id")
    private Long ttId;

    @Column(name = "tt_name")
    private String ttName;

    @Column(name = "tt_entid")
    private String ttentId;

    @TenantId
    @Column(name = "tenant_id")
    private String tenantId;

    @Builder.Default
    @Column(name = "tt_status")
    private Boolean status = true;
}
