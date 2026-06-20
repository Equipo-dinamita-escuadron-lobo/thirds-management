package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request;

import com.thirdsmanagement.thirds.domain.enums.ExportableField;
import com.thirdsmanagement.thirds.domain.model.ExportConfiguration;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * @brief DTO para solicitud de exportación de terceros a Excel
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdExportRequest {

    @NotBlank(message = "El identificador de entidad es obligatorio")
    private String entId;

    private Boolean status;

    private List<Long> thirdIds;
    
    private String companyName;

    @Builder.Default
    private Set<ExportableField> optionalFields = Set.of();

    public ExportConfiguration getExportConfiguration() {
        return ExportConfiguration.builder()
                .includeFields(optionalFields)
                .build();
    }
}
