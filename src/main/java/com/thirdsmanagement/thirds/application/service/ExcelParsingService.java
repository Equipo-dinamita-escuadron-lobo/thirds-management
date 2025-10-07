package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.domain.enums.ImportErrorType;
import com.thirdsmanagement.thirds.domain.enums.ePersonType;
import com.thirdsmanagement.thirds.domain.enums.eThirdGender;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdImportException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdsErrorCode;
import com.thirdsmanagement.thirds.domain.model.ImportErrorDetail;
import com.thirdsmanagement.thirds.domain.model.ThirdExcelData;
import com.thirdsmanagement.thirds.domain.utils.ErrorMappingUtils;
import com.thirdsmanagement.thirds.domain.utils.ImportConstants;
import com.thirdsmanagement.thirds.domain.utils.StringNormalizer;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.validation.FileValidator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Servicio especializado en el parseo de archivos Excel para importación de
 * terceros.
 * Maneja la lectura, validación de formato y conversión de datos desde Excel.
 */
@Service
public class ExcelParsingService {

    private static final int HEADER_ROW_INDEX = 0;
    private static final int DATA_START_ROW_INDEX = 1;
    
    private final FileValidator fileValidator;
    
    public ExcelParsingService(@Qualifier("excelFileValidator") FileValidator fileValidator) {
        this.fileValidator = fileValidator;
    }

    /**
     * Parsea el archivo Excel y extrae los datos de terceros.
     * 
     * @param file  archivo Excel a procesar
     * @param entId identificador de la entidad
     * @return lista de terceros parseados y lista de errores encontrados
     */
    public ExcelParsingResult parseExcelFile(MultipartFile file, String entId) {
        fileValidator.validate(file);

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

            // Procesar filas de datos (empezar después de headers)
            for (int rowIndex = DATA_START_ROW_INDEX; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
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
                    e);
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
        Row headerRow = sheet.getRow(HEADER_ROW_INDEX);
        if (headerRow == null) {
            errors.add(ImportErrorDetail.builder()
                    .rowNumber(1)
                    .errorCode("MISSING_HEADERS")
                    .errorMessage("El archivo no contiene encabezados")
                    .errorType(ImportErrorType.FORMAT_ERROR)
                    .build());
            return new HashMap<>();
        }

        Map<String, Integer> columnMap = new HashMap<>();
        Set<String> foundHeaders = new HashSet<>();

        // Mapear todas las columnas encontradas con protección contra NPE
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                String headerValue = cell.getStringCellValue();
                if (headerValue != null) {
                    String header = headerValue.trim();
                    if (!header.isEmpty()) {
                        // Normalizar el encabezado: extraer solo el nombre del campo
                        // eliminando el texto entre paréntesis y saltos de línea
                        String normalizedHeader = StringNormalizer.normalizeHeaderName(header);
                        columnMap.put(normalizedHeader, i);
                        foundHeaders.add(normalizedHeader);
                    }
                }
            }
        }

        // Validar que existan los encabezados básicos requeridos
        for (String requiredHeader : ImportConstants.REQUIRED_HEADERS) {
            if (!foundHeaders.contains(requiredHeader)) {
                errors.add(ImportErrorDetail.builder()
                        .rowNumber(1)
                        .columnName(requiredHeader)
                        .errorCode("MISSING_REQUIRED_HEADER")
                        .errorMessage("Falta el encabezado requerido: " + requiredHeader)
                        .errorType(ImportErrorType.FORMAT_ERROR)
                        .build());
            }
        }
        
        return columnMap;
    }

    /**
     * Parsea una fila individual del Excel usando el mapa de columnas detectado.
     * REFACTORIZADO: Dividido en sub-métodos para mejor mantenibilidad.
     */
    private ThirdExcelData parseRow(Row row, int rowNumber, String entId, Map<String, Integer> columnMap,
            List<ImportErrorDetail> errors) {
        try {
            ThirdExcelData.ThirdExcelDataBuilder builder = ThirdExcelData.builder()
                    .rowNumber(rowNumber)
                    .entId(entId);

            // Parsear campos básicos usando sub-método especializado
            parseBasicFields(row, builder, columnMap, rowNumber, errors);

            // Parsear campos opcionales usando sub-método especializado
            parseOptionalFields(row, builder, columnMap);

            return builder.build();

        } catch (Exception e) {
            errors.add(ImportErrorDetail.builder()
                    .rowNumber(rowNumber)
                    .errorCode("ROW_PARSING_ERROR")
                    .errorMessage("Error parseando fila: " + e.getMessage())
                    .errorType(ImportErrorType.FORMAT_ERROR)
                    .build());
            return null;
        }
    }

    /**
     * Parsea los campos básicos requeridos de una fila.
     */
    private void parseBasicFields(Row row, ThirdExcelData.ThirdExcelDataBuilder builder,
            Map<String, Integer> columnMap, int rowNumber, List<ImportErrorDetail> errors) {
        builder.typeIdName(getCellValueAsString(row, columnMap.get("Tipo Identificación")));
        builder.idNumber(getCellValueAsLong(row, columnMap.get("Número Identificación"), rowNumber,
                "Número Identificación", errors, columnMap));
        builder.verificationNumber(getCellValueAsLong(row, columnMap.get("Dígito Verificación"), rowNumber,
                "Dígito Verificación", errors, columnMap));
        builder.personType(parseEnum(getCellValueAsString(row, columnMap.get("Tipo Persona")),
                ePersonType.class, "Tipo Persona", rowNumber, errors, this::mapPersonType, columnMap));
        builder.names(getCellValueAsString(row, columnMap.get("Nombres")));
        builder.lastNames(getCellValueAsString(row, columnMap.get("Apellidos")));
        builder.socialReason(getCellValueAsString(row, columnMap.get("Razón Social")));
        builder.gender(parseEnum(getCellValueAsString(row, columnMap.get("Género")),
                eThirdGender.class, "Género", rowNumber, errors, this::mapGender, columnMap));
        builder.state(parseState(getCellValueAsString(row, columnMap.get("Estado")), rowNumber, errors, columnMap));
        
        // Campos de contacto ahora requeridos
        builder.address(getCellValueAsString(row, columnMap.get("Dirección")));
        builder.phoneNumber(parsePhoneNumber(getCellValueAsString(row, columnMap.get("Teléfono")), 
                rowNumber, errors, columnMap));
        builder.email(getCellValueAsString(row, columnMap.get("Email")));
    }

    /**
     * Parsea los campos adicionales de una fila.
     */
    private void parseOptionalFields(Row row, ThirdExcelData.ThirdExcelDataBuilder builder,
            Map<String, Integer> columnMap) {
        // Tipos de tercero
        Integer typesColumn = columnMap.get(ImportConstants.TYPES_COLUMN);
        if (typesColumn != null) {
            builder.thirdTypesNames(parseThirdTypes(getCellValueAsString(row, typesColumn)));
        }

        // Geografía
        parseGeographyFields(row, builder, columnMap);
    }

    /**
     * Parsea los campos geográficos (ahora obligatorios).
     */
    private void parseGeographyFields(Row row, ThirdExcelData.ThirdExcelDataBuilder builder,
            Map<String, Integer> columnMap) {
        Integer countryColumn = columnMap.get(ImportConstants.COUNTRY_COLUMN);
        if (countryColumn != null) {
            builder.countryName(getCellValueAsString(row, countryColumn));
        }

        Integer stateColumn = columnMap.get(ImportConstants.STATE_COLUMN);
        if (stateColumn != null) {
            builder.stateName(getCellValueAsString(row, stateColumn));
        }

        Integer cityColumn = columnMap.get(ImportConstants.CITY_COLUMN);
        if (cityColumn != null) {
            builder.cityName(getCellValueAsString(row, cityColumn));
        }

        Integer addressColumn = columnMap.get(ImportConstants.ADDRESS_COLUMN);
        if (addressColumn != null) {
            builder.address(getCellValueAsString(row, addressColumn));
        }
    }


    /**
     * Elimina duplicación de código entre parsePersonType, parseGender, etc.
     */
    private <T extends Enum<T>> T parseEnum(String value, Class<T> enumClass, String fieldName,
            int rowNumber, List<ImportErrorDetail> errors,
            java.util.function.Function<String, T> mapper, Map<String, Integer> columnMap) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            T result = mapper.apply(value.trim().toUpperCase());
            if (result == null) {
                errors.add(ErrorMappingUtils.createError(
                        rowNumber, 
                        fieldName, 
                        value,
                        ErrorMappingUtils.generateInvalidFieldErrorCode(fieldName),
                        fieldName + " inválido: " + value,
                        ImportErrorType.VALIDATION_ERROR,
                        columnMap));
            }
            return result;
        } catch (Exception e) {
            errors.add(ErrorMappingUtils.createError(
                    rowNumber,
                    fieldName,
                    value,
                    ErrorMappingUtils.generateParsingErrorCode(fieldName),
                    "Error parseando " + fieldName.toLowerCase() + ": " + e.getMessage(),
                    ImportErrorType.FORMAT_ERROR,
                    columnMap));
            return null;
        }
    }

    /**
     * Mapea valores de string a ePersonType.
     */
    private ePersonType mapPersonType(String normalizedValue) {
        switch (normalizedValue) {
            case "NATURAL":
                return ePersonType.Natural;
            case "JURIDICA":
            case "JURÍDICA":
                return ePersonType.Juridica;
            default:
                return null;
        }
    }

    /**
     * Mapea valores de string a eThirdGender.
     */
    private eThirdGender mapGender(String normalizedValue) {
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
    private Long getCellValueAsLong(Row row, Integer columnIndex, int rowNumber, String fieldName,
            List<ImportErrorDetail> errors, Map<String, Integer> columnMap) {
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
            errors.add(ErrorMappingUtils.createError(
                    rowNumber,
                    fieldName,
                    cell.toString(),
                    "INVALID_NUMBER_FORMAT",
                    "Formato numérico inválido en " + fieldName,
                    ImportErrorType.FORMAT_ERROR,
                    columnMap));
            return null;
        }
    }

    /**
     * Parsea el estado desde String.
     */
    private Boolean parseState(String value, int rowNumber, List<ImportErrorDetail> errors, 
            Map<String, Integer> columnMap) {
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
                    errors.add(ErrorMappingUtils.createError(
                            rowNumber,
                            "Estado",
                            value,
                            "INVALID_STATE",
                            "Estado inválido: " + value,
                            ImportErrorType.VALIDATION_ERROR,
                            columnMap));
                    return true; // Por defecto activo en caso de error
            }
        } catch (Exception e) {
            errors.add(ErrorMappingUtils.createError(
                    rowNumber,
                    "Estado",
                    value,
                    "STATE_PARSING_ERROR",
                    "Error parseando estado: " + e.getMessage(),
                    ImportErrorType.FORMAT_ERROR,
                    columnMap));
            return true;
        }
    }

    /**
     * Parsea y normaliza el número de teléfono.
     * Elimina espacios automáticamente y retorna el número limpio.
     * La validación de formato se realiza en BatchValidationService.
     */
    private String parsePhoneNumber(String value, int rowNumber, List<ImportErrorDetail> errors, 
            Map<String, Integer> columnMap) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        // Limpiar espacios automáticamente para mejorar UX
        String cleanValue = value.trim().replaceAll("\\s+", "");
        
        return cleanValue;
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
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExcelParsingResult {
        private List<ThirdExcelData> thirdsData;
        private List<ImportErrorDetail> errors;
        private int totalRows;
        private Map<String, Integer> columnMap;
    }
}
