package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response;

import org.springframework.data.domain.Page;

import com.thirdsmanagement.thirds.domain.model.Third;

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
public class ThirdsEnterpriseListResponse {
    private Page<Third> results;
}
