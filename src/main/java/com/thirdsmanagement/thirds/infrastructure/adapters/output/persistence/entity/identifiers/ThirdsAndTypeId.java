package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.identifiers;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class ThirdsAndTypeId implements Serializable{
    private Long thId;
    private Long ttId;
}
