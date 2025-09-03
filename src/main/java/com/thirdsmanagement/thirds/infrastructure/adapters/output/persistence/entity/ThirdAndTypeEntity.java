package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.identifiers.ThirdsAndTypeId;
import jakarta.persistence.*;
import lombok.*;

/**
 * Clase que representa la entidad de la tabla thirds_and_types.
 * Contiene la relación entre un tercero y un tipo de tercero.
 * La tabla tiene una clave primaria compuesta por el identificador de un tercero y el identificador de un tipo de tercero.
 * La tabla tiene una relación muchos a uno con la tabla thirds y con la tabla third_types.
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "thirds_and_types") // Nombre de la tabla en la base de datos
public class ThirdAndTypeEntity {

    @EmbeddedId
    private ThirdsAndTypeId id;

    @ManyToOne
    @MapsId("thId") // Mapea thId de ThirdsAndTypeId
    @JoinColumn(name = "th_id", nullable = false)
    private ThirdEntity third;

    @ManyToOne
    @MapsId("ttId") // Mapea ttId de ThirdsAndTypeId
    @JoinColumn(name = "tt_id", nullable = false)
    private ThirdTypeEntity thirdType;


}
