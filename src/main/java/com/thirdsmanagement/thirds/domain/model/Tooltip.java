package com.thirdsmanagement.thirds.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tooltip")  // Optional, specify table name if different from class name
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tooltip {

    @Id
    private String entId;  // This will serve as the primary key

    private String tip;    // Another column in the table
}
