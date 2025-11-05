package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity;


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
 * @brief Entidad JPA para países en jerarquía geográfica
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "COUNTRIES")
public class CountryEntity {

    @Id
    @Column(name = "co_code", length = 3)
    private String countryCode;

    @Column(name = "co_name", length = 100, nullable = false)
    private String countryName;
}
