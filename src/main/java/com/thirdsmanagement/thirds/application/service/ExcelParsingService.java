package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdImportException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdsErrorCode;
import com.thirdsmanagement.thirds.domain.model.*;
import com.thirdsmanagement.thirds.domain.utils.ImportConstants;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ImportErrorDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Servicio especializado en el parseo de archivos Excel para importación de terceros.
 * Maneja la lectura, validación de formato y conversión de datos desde Excel.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelParsingService {


    /**
     * Valida el formato básico del archivo Excel.
     * 
     * @param file archivo Excel a validar
     * @throws ThirdImportException si el archivo no es válido
     */
    public void validateExcelFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw ThirdImportException.forEmptyFile("archivo no especificado");
        }

        if (file.getSize() > ImportConstants.MAX_FILE_SIZE) {
            throw ThirdImportException.forFileSizeExceeded(
                file.getOriginalFilename(), 
                file.getSize(), 
                ImportConstants.MAX_FILE_SIZE
            );
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
            throw ThirdImportException.forInvalidExcelFile(
                filename, 
                "El archivo debe tener extensión .xlsx"
            );
        }
    }

    /**
     * Parsea el archivo Excel y extrae los datos de terceros.
     * 
     * @param file archivo Excel a procesar
     * @param entId identificador de la entidad
     * @return lista de terceros parseados y lista de errores encontrados
     */
    public ExcelParsingResult parseExcelFile(MultipartFile file, String entId) {
        validateExcelFile(file);

        List<ThirdExcelData> thirdsData = new ArrayList<>();
        List<ImportErrorDetail> errors = new ArrayList<>();
        Map<String, Integer> columnMap = new HashMap<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            if (sheet.getPhysicalNumberOfRows() == 0) {
                throw ThirdImportException.forEmptyFile(file.getOriginalFilename());
            }

            // Detectar mapa de columnas dinámicamente
            columnMap = detectColumnMapping(sheet, errors);
            if (columnMap.isEmpty()) {
                throw ThirdImportException.forInvalidExcelFile(file.getOriginalFilename(), 
                        "No se pudieron detectar las columnas requeridas");
            }

            // Procesar filas de datos
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isEmptyRow(row)) {
                    continue;
                }

                ThirdExcelData thirdData = parseRow(row, rowIndex + 1, entId, columnMap, errors);
                if (thirdData != null) {
                    thirdsData.add(thirdData);
                }
            }

        } catch (IOException e) {
            throw new ThirdImportException(
                ThirdsErrorCode.EXCEL_VALIDATION_ERROR,
                "Error leyendo archivo Excel: " + e.getMessage(),
                e
            );
        }

        return ExcelParsingResult.builder()
                .thirdsData(thirdsData)
                .errors(errors)
                .totalRows(thirdsData.size())
                .columnMap(columnMap)
                .build();
    }

    /**
     * Detecta el mapeo de columnas basado en los encabezados del archivo.
     */
    private Map<String, Integer> detectColumnMapping(Sheet sheet, List<ImportErrorDetail> errors) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
                errors.add(ImportErrorDetail.builder()
                        .rowNumber(1)
                        .errorCode("MISSING_HEADERS")
                        .errorMessage("El archivo no contiene encabezados")
                        .errorType(ImportErrorDetail.ErrorType.FORMAT_ERROR)
                        .build());
            return new HashMap<>();
        }

        Map<String, Integer> columnMap = new HashMap<>();
        Set<String> foundHeaders = new HashSet<>();

        // Mapear todas las columnas encontradas
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String header = cell.getStringCellValue().trim();
                if (!header.isEmpty()) {
                    columnMap.put(header, i);
                    foundHeaders.add(header);
                }
            }
        }

        // Validar que existan los encabezados básicos requeridos
        for (String requiredHeader : ImportConstants.BASIC_REQUIRED_HEADERS) {
            if (!foundHeaders.contains(requiredHeader)) {
                errors.add(ImportErrorDetail.builder()
                        .rowNumber(1)
                        .columnName(requiredHeader)
                        .errorCode("MISSING_REQUIRED_HEADER")
                        .errorMessage("Falta el encabezado requerido: " + requiredHeader)
                        .errorType(ImportErrorDetail.ErrorType.FORMAT_ERROR)
                        .build());
            }
        }

        log.debug("Columnas detectadas: {}", columnMap.keySet());
        return columnMap;
    }

    /**
     * Parsea una fila individual del Excel usando el mapa de columnas detectado.
     */
    private ThirdExcelData parseRow(Row row, int rowNumber, String entId, Map<String, Integer> columnMap, List<ImportErrorDetail> errors) {
        try {
            ThirdExcelData.ThirdExcelDataBuilder builder = ThirdExcelData.builder()
                    .rowNumber(rowNumber)
                    .entId(entId);

            // Parsear campos básicos (siempre presentes)
            builder.typeIdName(getCellValueAsString(row, columnMap.get("Tipo Identificación")));
            builder.idNumber(getCellValueAsLong(row, columnMap.get("Número Identificación"), rowNumber, "Número Identificación", errors));
            builder.verificationNumber(getCellValueAsLong(row, columnMap.get("Dígito Verificación"), rowNumber, "Dígito Verificación", errors));
            builder.personType(parsePersonType(getCellValueAsString(row, columnMap.get("Tipo Persona")), rowNumber, errors));
            builder.names(getCellValueAsString(row, columnMap.get("Nombres")));
            builder.lastNames(getCellValueAsString(row, columnMap.get("Apellidos")));
            builder.socialReason(getCellValueAsString(row, columnMap.get("Razón Social")));
            builder.gender(parseGender(getCellValueAsString(row, columnMap.get("Género")), rowNumber, errors));
            builder.state(parseState(getCellValueAsString(row, columnMap.get("Estado")), rowNumber, errors));

            // Parsear campos opcionales (solo si están presentes)
            Integer typesColumn = columnMap.get(ImportConstants.OptionalHeaders.TYPES);
            if (typesColumn != null) {
                builder.thirdTypesNames(parseThirdTypes(getCellValueAsString(row, typesColumn)));
            }

            Integer countryColumn = columnMap.get(ImportConstants.OptionalHeaders.COUNTRY);
            if (countryColumn != null) {
                builder.countryName(getCellValueAsString(row, countryColumn));
            }

            Integer stateColumn = columnMap.get(ImportConstants.OptionalHeaders.STATE);
            if (stateColumn != null) {
                builder.stateName(getCellValueAsString(row, stateColumn));
            }

            Integer cityColumn = columnMap.get(ImportConstants.OptionalHeaders.CITY);
            if (cityColumn != null) {
                builder.cityName(getCellValueAsString(row, cityColumn));
            }

            Integer addressColumn = columnMap.get(ImportConstants.OptionalHeaders.ADDRESS);
            if (addressColumn != null) {
                builder.address(getCellValueAsString(row, addressColumn));
            }

            Integer phoneColumn = columnMap.get(ImportConstants.OptionalHeaders.PHONE);
            if (phoneColumn != null) {
                builder.phoneNumber(getCellValueAsString(row, phoneColumn));
            }

            Integer emailColumn = columnMap.get(ImportConstants.OptionalHeaders.EMAIL);
            if (emailColumn != null) {
                builder.email(getCellValueAsString(row, emailColumn));
            }

            return builder.build();

        } catch (Exception e) {
            errors.add(ImportErrorDetail.builder()
                    .rowNumber(rowNumber)
                    .errorCode("ROW_PARSING_ERROR")
                    .errorMessage("Error parseando fila: " + e.getMessage())
                    .errorType(ImportErrorDetail.ErrorType.FORMAT_ERROR)
                    .build());
            return null;
        }
    }

    /**
     * Obtiene el valor de una celda como String.
     */
    private String getCellValueAsString(Row row, Integer columnIndex) {
        if (columnIndex == null) {
            return null;
        }
        
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    /**
     * Obtiene el valor de una celda como Long.
     */
    private Long getCellValueAsLong(Row row, Integer columnIndex, int rowNumber, String fieldName, List<ImportErrorDetail> errors) {
        if (columnIndex == null) {
            return null;
        }
        
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }

        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (long) cell.getNumericCellValue();
                case STRING:
                    String value = cell.getStringCellValue().trim();
                    return value.isEmpty() ? null : Long.parseLong(value);
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            errors.add(ImportErrorDetail.builder()
                    .rowNumber(rowNumber)
                    .columnNumber(columnIndex)
                    .columnName(fieldName)
                    .fieldValue(cell.toString())
                    .errorCode("INVALID_NUMBER_FORMAT")
                    .errorMessage("Formato numérico inválido en " + fieldName)
                    .errorType(ImportErrorDetail.ErrorType.FORMAT_ERROR)
                    .build());
            return null;
        }
    }

    /**
     * Parsea el tipo de persona desde String.
     */
    private ePersonType parsePersonType(String value, int rowNumber, List<ImportErrorDetail> errors) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            String normalizedValue = value.trim().toUpperCase();
            switch (normalizedValue) {
                case "NATURAL":
                    return ePersonType.Natural;
                case "JURIDICA":
                case "JURÍDICA":
                    return ePersonType.Juridica;
                default:
                errors.add(ImportErrorDetail.builder()
                        .rowNumber(rowNumber)
                        .columnName("Tipo Persona")
                        .fieldValue(value)
                        .errorCode("INVALID_PERSON_TYPE")
                        .errorMessage("Tipo de persona inválido: " + value)
                        .errorType(ImportErrorDetail.ErrorType.VALIDATION_ERROR)
                        .build());
                    return null;
            }
        } catch (Exception e) {
            errors.add(ImportErrorDetail.builder()
                    .rowNumber(rowNumber)
                    .columnName("Tipo Persona")
                    .fieldValue(value)
                    .errorCode("PERSON_TYPE_PARSING_ERROR")
                    .errorMessage("Error parseando tipo de persona: " + e.getMessage())
                    .errorType(ImportErrorDetail.ErrorType.FORMAT_ERROR)
                    .build());
            return null;
        }
    }

    /**
     * Parsea el género desde String.
     */
    private eThirdGender parseGender(String value, int rowNumber, List<ImportErrorDetail> errors) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            String normalizedValue = value.trim().toUpperCase();
            switch (normalizedValue) {
                case "MASCULINO":
                case "M":
                    return eThirdGender.Masculino;
                case "FEMENINO":
                case "F":
                    return eThirdGender.Femenino;
                case "OTRO":
                case "O":
                    return eThirdGender.Otro;
                default:
                errors.add(ImportErrorDetail.builder()
                        .rowNumber(rowNumber)
                        .columnName("Género")
                        .fieldValue(value)
                        .errorCode("INVALID_GENDER")
                        .errorMessage("Género inválido: " + value)
                        .errorType(ImportErrorDetail.ErrorType.VALIDATION_ERROR)
                        .build());
                    return null;
            }
        } catch (Exception e) {
            errors.add(ImportErrorDetail.builder()
                    .rowNumber(rowNumber)
                    .columnName("Género")
                    .fieldValue(value)
                    .errorCode("GENDER_PARSING_ERROR")
                    .errorMessage("Error parseando género: " + e.getMessage())
                    .errorType(ImportErrorDetail.ErrorType.FORMAT_ERROR)
                    .build());
            return null;
        }
    }

    /**
     * Parsea el estado desde String.
     */
    private Boolean parseState(String value, int rowNumber, List<ImportErrorDetail> errors) {
        if (value == null || value.trim().isEmpty()) {
            return true; // Por defecto activo
        }

        try {
            String normalizedValue = value.trim().toUpperCase();
            switch (normalizedValue) {
                case "ACTIVO":
                case "ACTIVE":
                case "TRUE":
                case "1":
                    return true;
                case "INACTIVO":
                case "INACTIVE":
                case "FALSE":
                case "0":
                    return false;
                default:
                errors.add(ImportErrorDetail.builder()
                        .rowNumber(rowNumber)
                        .columnName("Estado")
                        .fieldValue(value)
                        .errorCode("INVALID_STATE")
                        .errorMessage("Estado inválido: " + value)
                        .errorType(ImportErrorDetail.ErrorType.VALIDATION_ERROR)
                        .build());
                    return true; // Por defecto activo en caso de error
            }
        } catch (Exception e) {
            errors.add(ImportErrorDetail.builder()
                    .rowNumber(rowNumber)
                    .columnName("Estado")
                    .fieldValue(value)
                    .errorCode("STATE_PARSING_ERROR")
                    .errorMessage("Error parseando estado: " + e.getMessage())
                    .errorType(ImportErrorDetail.ErrorType.FORMAT_ERROR)
                    .build());
            return true;
        }
    }

    /**
     * Parsea los tipos de tercero desde String (separados por coma).
     */
    private Set<String> parseThirdTypes(String value) {
        if (value == null || value.trim().isEmpty()) {
            return new HashSet<>();
        }

        Set<String> types = new HashSet<>();
        String[] parts = value.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                types.add(trimmed);
            }
        }
        return types;
    }

    /**
     * Verifica si una fila está vacía.
     */
    private boolean isEmptyRow(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(row, i);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Clase que representa el resultado del parseo de Excel.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ExcelParsingResult {
        private List<ThirdExcelData> thirdsData;
        private List<ImportErrorDetail> errors;
        private int totalRows;
        private Map<String, Integer> columnMap;
    }
}
