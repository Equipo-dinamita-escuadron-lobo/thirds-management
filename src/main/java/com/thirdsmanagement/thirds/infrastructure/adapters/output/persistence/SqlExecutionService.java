package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Servicio para ejecución de archivos SQL.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SqlExecutionService {
    
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Ejecuta un archivo SQL desde el classpath
     */
    public void executeSqlFile(String sqlFilePath) {
        try {
            log.debug("Ejecutando archivo SQL: {}", sqlFilePath);
            
            ClassPathResource resource = new ClassPathResource(sqlFilePath);
            validateResource(resource, sqlFilePath);
            
            String sqlContent = readSqlContent(resource);
            executeSqlStatements(sqlContent);
            
            log.debug("Archivo {} ejecutado exitosamente", sqlFilePath);
            
        } catch (Exception e) {
            log.error("Error ejecutando archivo SQL {}: {}", sqlFilePath, e.getMessage());
            throw new RuntimeException("Error cargando datos desde archivo: " + sqlFilePath, e);
        }
    }
    
    private void validateResource(ClassPathResource resource, String sqlFilePath) throws IOException {
        if (!resource.exists()) {
            throw new IOException("Archivo SQL no encontrado: " + sqlFilePath);
        }
    }
    
    private String readSqlContent(ClassPathResource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            StringBuilder sqlContent = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (isValidSqlLine(line)) {
                    sqlContent.append(line).append("\n");
                }
            }
            
            return sqlContent.toString().trim();
        }
    }
    
    private boolean isValidSqlLine(String line) {
        return !line.isEmpty() && !line.startsWith("--");
    }
    
    private void executeSqlStatements(String sqlContent) {
        if (sqlContent.isEmpty()) {
            return;
        }
        
        // Dividir por punto y coma, respetando strings
        String[] statements = sqlContent.split(";(?=([^']*'[^']*')*[^']*$)");
        
        for (String statement : statements) {
            String trimmedStatement = statement.trim();
            if (!trimmedStatement.isEmpty()) {
                jdbcTemplate.execute(trimmedStatement);
            }
        }
    }
}
