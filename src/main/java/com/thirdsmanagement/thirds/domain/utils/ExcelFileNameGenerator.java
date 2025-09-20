package com.thirdsmanagement.thirds.domain.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.ThirdType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Utilidad para generar nombres de archivos Excel de terceros.
 */
@Component
@RequiredArgsConstructor
public class ExcelFileNameGenerator {
    
    private final IdOutputPort idOutputPort;
    
    /**
     * Genera nombre de archivo para plantilla de terceros.
     * 
     * @return nombre del archivo con timestamp
     */
    public String generateTemplateFileName() {
        String timestamp = generateTimestamp();
        return "Plantilla_Terceros_" + timestamp + ".xlsx";
    }
    
    /**
     * Genera nombre de archivo para exportación de terceros.
     * 
     * @param entId ID de la empresa
     * @param thirdTypeId ID del tipo de tercero (opcional)
     * @return nombre del archivo con timestamp y tipo si aplica
     */
    public String generateExportFileName(String entId, Long thirdTypeId) {
        String timestamp = generateTimestamp();
        
        if (thirdTypeId != null) {
            String typeName = getThirdTypeName(thirdTypeId, entId);
            String fileTypeName = normalizeForFileName(typeName);
            return "Terceros_" + fileTypeName + "_" + timestamp + ".xlsx";
        } else {
            return "Terceros_" + timestamp + ".xlsx";
        }
    }
    
    /**
     * Obtiene el nombre del tipo de tercero.
     * 
     * @param thirdTypeId ID del tipo de tercero
     * @param entId ID de la empresa
     * @return nombre normalizado del tipo o fallback con ID
     */
    private String getThirdTypeName(Long thirdTypeId, String entId) {
        ThirdType thirdType = idOutputPort.getThirdTypeById(thirdTypeId, entId);
        return thirdType != null ? thirdType.getNormalizedName() : "Tipo_" + thirdTypeId;
    }
    
    /**
     * Normaliza el nombre para uso en archivos.
     * Reemplaza espacios por guiones bajos.
     * 
     * @param name nombre a normalizar
     * @return nombre normalizado para archivo
     */
    private String normalizeForFileName(String name) {
        return name.replace(" ", "_");
    }
    
    /**
     * Genera timestamp en formato yyyyMMdd_HHmmss.
     * 
     * @return timestamp formateado
     */
    private String generateTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }
}
