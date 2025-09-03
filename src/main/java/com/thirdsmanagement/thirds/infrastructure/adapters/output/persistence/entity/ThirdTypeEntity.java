package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa la entidad de la tabla third_type.
 * Contiene la información de un tipo de tercero.
 * La tabla tiene una clave primaria identificada por tt_id.
 * La tabla tiene una relación muchos a muchos con la tabla thirds.
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
    @Column(name = "tt_id")
    private Long ttId;

    @Column(name = "tt_name")
    private String ttName;

    @Column(name = "tt_entid")
    private String ttentId;

    @ManyToMany(mappedBy = "thirdTypes")
    private Set<ThirdEntity> thirds;
}
