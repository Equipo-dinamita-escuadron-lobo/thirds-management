package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response;

import org.springframework.data.domain.Page;

import com.thirdsmanagement.thirds.domain.model.Third;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase que representa la respuesta de la lista de terceros.
 * Contiene una lista de terceros.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThirdsEnterpriseListResponse {
    private Page<Third> results;
}
