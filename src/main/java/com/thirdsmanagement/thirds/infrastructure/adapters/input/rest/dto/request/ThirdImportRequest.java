package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO para solicitudes de importación masiva de terceros.
 * Contiene el archivo Excel y las configuraciones de importación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThirdImportRequest {

    /**
     * Identificador de la entidad.
     */
    @NotBlank(message = "El identificador de entidad es obligatorio")
    @Size(max = 50, message = "El ID de la empresa no puede exceder los 50 caracteres")
    private String entId;

    /**
     * Archivo Excel con los terceros a importar.
     */
    @NotNull(message = "El archivo Excel es obligatorio")
    private MultipartFile excelFile;

    /**
     * Nombre del archivo para referencia en reportes.
     */
    private String fileName;
}
