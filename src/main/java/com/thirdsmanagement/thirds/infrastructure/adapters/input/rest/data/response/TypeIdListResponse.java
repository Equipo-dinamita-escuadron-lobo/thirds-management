package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response;

import java.util.List;

import com.thirdsmanagement.thirds.domain.model.TypeId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa la respuesta de la lista de tipos de identificación.
 * Contiene una lista de tipos de identificación.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeIdListResponse {
    private List<TypeId> typeId;
}
