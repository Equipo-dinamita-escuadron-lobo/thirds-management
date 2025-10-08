package com.thirdsmanagement.thirds.infrastructure.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.thirdsmanagement.thirds.domain.utils.ImportConstants;

import java.io.IOException;
import java.util.*;

/**
 * Utilidades para operaciones comunes con archivos Excel.
 * Centraliza funcionalidades de lectura, validación y manipulación
 * de archivos Excel para reutilización en diferentes módulos.
 */
public final class ExcelUtils {

    private ExcelUtils() {
        throw new UnsupportedOperationException("ExcelUtils es una clase de utilidad y no debe ser instanciada");
    }

    // ===== VALIDACIONES DE ARCHIVO =====
  
    /**
     * Verifica si un archivo tiene una extensión Excel válida.
     * 
     * @param fileName el nombre del archivo
     * @return true si tiene extensión válida, false en caso contrario
     */
    public static boolean isValidExcelExtension(String fileName) {
        if (fileName == null) {
            return false;
        }
        
        String lowerFileName = fileName.toLowerCase();
        return Arrays.stream(ImportConstants.SUPPORTED_EXTENSIONS)
                .anyMatch(lowerFileName::endsWith);
    }

    // ===== OPERACIONES DE LECTURA =====

    /**
     * Abre un workbook desde un MultipartFile.
     * 
     * @param file el archivo Excel
     * @return el workbook abierto
     * @throws IOException si hay error al leer el archivo
     */
    public static Workbook openWorkbook(MultipartFile file) throws IOException {
        return new XSSFWorkbook(file.getInputStream());
    }

    /**
     * Obtiene la primera hoja del workbook.
     * 
     * @param workbook el workbook Excel
     * @return la primera hoja
     * @throws IllegalArgumentException si el workbook no tiene hojas
     */
    public static Sheet getFirstSheet(Workbook workbook) {
        if (workbook.getNumberOfSheets() == 0) {
            throw new IllegalArgumentException("El archivo Excel no contiene hojas");
        }
        return workbook.getSheetAt(0);
    }

    /**
     * Detecta el mapeo de columnas basado en la fila de encabezados.
     * 
     * @param headerRow la fila de encabezados
     * @return mapa que asocia nombre de columna con su índice
     */
    public static Map<String, Integer> detectColumnMapping(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();
        
        if (headerRow == null) {
            return columnMap;
        }

        for (Cell cell : headerRow) {
            if (cell != null) {
                String headerValue = getCellValueAsString(cell);
                if (headerValue != null && !headerValue.trim().isEmpty()) {
                    columnMap.put(headerValue.trim(), cell.getColumnIndex());
                }
            }
        }

        return columnMap;
    }

    /**
     * Valida que todos los encabezados requeridos estén presentes.
     * 
     * @param columnMap el mapa de columnas detectado
     * @param requiredHeaders los encabezados requeridos
     * @return lista de encabezados faltantes (vacía si todos están presentes)
     */
    public static List<String> validateRequiredHeaders(Map<String, Integer> columnMap, String[] requiredHeaders) {
        List<String> missingHeaders = new ArrayList<>();
        
        for (String requiredHeader : requiredHeaders) {
            if (!columnMap.containsKey(requiredHeader)) {
                missingHeaders.add(requiredHeader);
            }
        }
        
        return missingHeaders;
    }

    // ===== OPERACIONES DE CELDA =====

    /**
     * Obtiene el valor de una celda como String, manejando diferentes tipos de datos.
     * 
     * @param cell la celda a leer
     * @return el valor como String o null si la celda está vacía
     */
    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                } else {
                    // Manejar números enteros sin decimales
                    double numValue = cell.getNumericCellValue();
                    if (numValue == Math.floor(numValue)) {
                        yield String.valueOf((long) numValue);
                    } else {
                        yield String.valueOf(numValue);
                    }
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue().trim();
                } catch (IllegalStateException e) {
                    // Si la fórmula no es un string, intentar como número
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> null;
        };
    }

    /**
     * Obtiene el valor de una celda como Long.
     * 
     * @param cell la celda a leer
     * @return el valor como Long o null si no se puede convertir
     */
    public static Long getCellValueAsLong(Cell cell) {
        String stringValue = getCellValueAsString(cell);
        if (stringValue == null || stringValue.isEmpty()) {
            return null;
        }

        try {
            return Long.valueOf(stringValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Obtiene el valor de una celda por nombre de columna.
     * 
     * @param row la fila que contiene la celda
     * @param columnName el nombre de la columna
     * @param columnMap el mapa de columnas
     * @return el valor como String o null si no se encuentra
     */
    public static String getCellValueByColumnName(Row row, String columnName, Map<String, Integer> columnMap) {
        Integer columnIndex = columnMap.get(columnName);
        if (columnIndex == null) {
            return null;
        }
        
        Cell cell = row.getCell(columnIndex);
        return getCellValueAsString(cell);
    }

    /**
     * Obtiene el valor de una celda como Long por nombre de columna.
     * 
     * @param row la fila que contiene la celda
     * @param columnName el nombre de la columna
     * @param columnMap el mapa de columnas
     * @return el valor como Long o null si no se encuentra o no se puede convertir
     */
    public static Long getCellValueAsLongByColumnName(Row row, String columnName, Map<String, Integer> columnMap) {
        Integer columnIndex = columnMap.get(columnName);
        if (columnIndex == null) {
            return null;
        }
        
        Cell cell = row.getCell(columnIndex);
        return getCellValueAsLong(cell);
    }

    // ===== OPERACIONES DE FILA =====

    /**
     * Verifica si una fila está completamente vacía.
     * 
     * @param row la fila a verificar
     * @return true si está vacía, false en caso contrario
     */
    public static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (Cell cell : row) {
            String cellValue = getCellValueAsString(cell);
            if (cellValue != null && !cellValue.trim().isEmpty()) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Cuenta las filas con datos en una hoja (excluyendo encabezados).
     * 
     * @param sheet la hoja Excel
     * @param startRow índice de la primera fila de datos (usualmente 1)
     * @return número de filas con datos
     */
    public static int countDataRows(Sheet sheet, int startRow) {
        int dataRowCount = 0;
        int lastRowNum = sheet.getLastRowNum();
        
        for (int i = startRow; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (!isRowEmpty(row)) {
                dataRowCount++;
            }
        }
        
        return dataRowCount;
    }

    // ===== UTILIDADES DE GENERACIÓN =====


    // ===== MANEJO DE ERRORES =====

    /**
     * Cierra un workbook de manera segura.
     * 
     * @param workbook el workbook a cerrar
     */
    public static void closeWorkbookSafely(Workbook workbook) {
        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException e) {
                // Log warning pero no lanzar excepción
                System.err.println("Warning: No se pudo cerrar el workbook correctamente: " + e.getMessage());
            }
        }
    }
}
