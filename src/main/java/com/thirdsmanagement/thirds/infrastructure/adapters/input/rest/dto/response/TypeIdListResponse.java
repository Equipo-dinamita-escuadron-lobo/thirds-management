package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response;

import java.util.List;

import com.thirdsmanagement.thirds.domain.model.TypeId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @brief DTO de respuesta para lista de tipos de identificación
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeIdListResponse {
    private List<TypeId> typeId;
}
