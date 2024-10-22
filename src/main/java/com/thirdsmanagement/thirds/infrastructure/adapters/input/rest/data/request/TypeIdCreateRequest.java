package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeIdCreateRequest {
    @NotNull(message = "Enterprise ID not be empty") 
    private String entId;
    private String typeId;
    @NotNull(message = "TypeIdName not be empty") 
    private String typeIdname;
}
