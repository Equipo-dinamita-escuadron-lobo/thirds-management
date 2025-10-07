package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request;

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
 * DTO para solicitudes de exportación de terceros.
 * Utiliza el patrón Builder para construcción flexible de configuraciones de exportación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdExportRequest {

    /**
     * Identificador de la entidad.
     */
    @NotBlank(message = "El identificador de entidad es obligatorio")
    private String entId;

    /**
     * Estado de los terceros a exportar (true para activos, false para inactivos).
     * Si es null, se exportan todos independientemente del estado.
     */
    private Boolean status;

    /**
     * Lista de IDs específicos de terceros a exportar.
     * Si está vacía o es null, se aplican los otros filtros.
     */
    private List<Long> thirdIds;

    /**
     * Campos opcionales a incluir en la exportación.
     * Utiliza ExportableField enum para mayor flexibilidad y extensibilidad.
     */
    @Builder.Default
    private Set<ExportableField> optionalFields = Set.of();

    /**
     * Obtiene la configuración de exportación construida.
     * 
     * @return ExportConfiguration configurada con los campos opcionales
     */
    public ExportConfiguration getExportConfiguration() {
        return ExportConfiguration.builder()
                .includeFields(optionalFields)
                .build();
    }
}
