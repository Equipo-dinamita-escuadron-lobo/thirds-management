package com.thirdsmanagement.thirds.infrastructure.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

/**
 * Utilidad para generar nombres de archivos Excel de terceros.
 */
@Component
public class ExcelFileNameGenerator {
    
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
     * @param companyName Nombre de la empresa (opcional)
     * @param status Estado de los terceros exportados (true=activos, false=inactivos, null=todos)
     * @return nombre del archivo con timestamp, empresa y estado si aplica
     */
    public String generateExportFileName(String entId, String companyName, Boolean status) {
        String timestamp = generateTimestamp();
        StringBuilder fileName = new StringBuilder("Terceros");
        
        // Agregar nombre de empresa si se proporciona
        if (companyName != null && !companyName.trim().isEmpty()) {
            String normalizedCompanyName = normalizeForFileName(companyName);
            fileName.append("_").append(normalizedCompanyName);
        }
        
        // Agregar estado si se especifica
        if (status != null) {
            String statusText = status ? "activos" : "inactivos";
            fileName.append("_").append(statusText);
        }
        
        fileName.append("_").append(timestamp).append(".xlsx");
        return fileName.toString();
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
