package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity;

import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.identifiers.ThirdsAndTypeId;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


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
