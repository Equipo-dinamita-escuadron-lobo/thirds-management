package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa la tabla de ciudades.
 * Contiene la información básica de una ciudad para la jerarquía geográfica.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "CITIES")
@IdClass(CityEntityId.class)
public class CityEntity {

    @Id
    @Column(name = "ci_code", length = 10)
    private String cityCode;

    @Id
    @Column(name = "st_code", length = 10)
    private String stateCode;

    @Id
    @Column(name = "co_code", length = 3)
    private String countryCode;

    @Column(name = "ci_name", length = 100, nullable = false)
    private String cityName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "st_code", referencedColumnName = "st_code", insertable = false, updatable = false),
        @JoinColumn(name = "co_code", referencedColumnName = "co_code", insertable = false, updatable = false)
    })
    private StateEntity state;


    @Column(name = "ci_created_at")
    @CreationTimestamp
    private LocalDateTime creationDate;

    @Column(name = "ci_updated_at")
    @UpdateTimestamp
    private LocalDateTime updateDate;
}
