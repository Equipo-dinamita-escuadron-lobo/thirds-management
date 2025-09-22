package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.ExportThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdExportException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdsErrorCode;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdExportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para exportar terceros en formato Excel.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExportThirdService implements ExportThirdUseCase {

    private final ThirdOutputPort thirdOutputPort;
    private final ExcelValidationService excelValidationService;
    


    private List<Third> getFilteredThirds(ThirdExportRequest request) {
        // Crear un Pageable que obtenga todos los registros (tamaño grande)
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        
        // Si se especifica un estado específico (activos o inactivos)
        if (request.getStatus() != null) {
            Page<Third> page = thirdOutputPort.getAllThirdsByStatus(request.getEntId(), pageable, request.getStatus());
            List<Third> thirds = page.getContent();
            
            // Si también se especifica un ID de tipo de tercero, filtrar adicionalmente
            if (request.getThirdTypeId() != null) {
                return thirds.stream()
                    .filter(third -> third.getThirdTypes().stream()
                        .anyMatch(type -> type.getThirdTypeId().equals(request.getThirdTypeId())))
                    .collect(Collectors.toList());
            }
            
            return thirds;
        }
        
        // Si se especifica un ID de tipo de tercero pero no estado, usar método sin filtro de estado
        if (request.getThirdTypeId() != null) {
            Page<Third> page = thirdOutputPort.getAllThirdsByTypeIdWithoutStateFilter(request.getEntId(), pageable, request.getThirdTypeId());
            return page.getContent();
        }
        
        // Si no se especifica filtro, obtener explícitamente activos e inactivos
        Page<Third> activePage = thirdOutputPort.getAllThirdsByStatus(request.getEntId(), pageable, true);
        Page<Third> inactivePage = thirdOutputPort.getAllThirdsByStatus(request.getEntId(), pageable, false);
        
        List<Third> allThirds = activePage.getContent();
        allThirds.addAll(inactivePage.getContent());
        
        return allThirds;
    }



    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
        return style;
    }

    private void createHeaders(Sheet sheet, CellStyle headerStyle, ThirdExportRequest request) {
        Row headerRow = sheet.createRow(0);
        int colIndex = 0;

        // Encabezados básicos
        createHeaderCell(headerRow, colIndex++, "Tipo Identificación", headerStyle);
        createHeaderCell(headerRow, colIndex++, "Número Identificación", headerStyle);
        createHeaderCell(headerRow, colIndex++, "Dígito Verificación", headerStyle);
        createHeaderCell(headerRow, colIndex++, "Tipo Persona", headerStyle);
        createHeaderCell(headerRow, colIndex++, "Nombres", headerStyle);
        createHeaderCell(headerRow, colIndex++, "Apellidos", headerStyle);
        createHeaderCell(headerRow, colIndex++, "Razón Social", headerStyle);
        createHeaderCell(headerRow, colIndex++, "Género", headerStyle);
        createHeaderCell(headerRow, colIndex++, "Estado", headerStyle);
        
        // Encabezados opcionales
        if (Boolean.TRUE.equals(request.getIncludeTypes())) {
            createHeaderCell(headerRow, colIndex++, "Tipos de Tercero", headerStyle);
        }
        
        if (Boolean.TRUE.equals(request.getIncludeCities())) {
            createHeaderCell(headerRow, colIndex++, "País", headerStyle);
            createHeaderCell(headerRow, colIndex++, "Departamento", headerStyle);
            createHeaderCell(headerRow, colIndex++, "Ciudad", headerStyle);
        }
        
        createHeaderCell(headerRow, colIndex++, "Dirección", headerStyle);
        createHeaderCell(headerRow, colIndex++, "Teléfono", headerStyle);
        createHeaderCell(headerRow, colIndex++, "Email", headerStyle);
    }

    private void createHeaderCell(Row row, int colIndex, String value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void fillData(Sheet sheet, List<Third> thirds, CellStyle dataStyle, CellStyle dateStyle, ThirdExportRequest request) {
        int rowIndex = 1;
        
        for (Third third : thirds) {
            Row row = sheet.createRow(rowIndex++);
            int colIndex = 0;

            // Datos básicos
            createDataCell(row, colIndex++, third.getTypeId() != null ? third.getTypeId().getTypeIdname() : "", dataStyle);
            createDataCell(row, colIndex++, third.getIdNumber(), dataStyle);
            createDataCell(row, colIndex++, third.getVerificationNumber(), dataStyle);
            createDataCell(row, colIndex++, third.getPersonType() != null ? third.getPersonType().getCode() : "", dataStyle);
            createDataCell(row, colIndex++, third.getNames(), dataStyle);
            createDataCell(row, colIndex++, third.getLastNames(), dataStyle);
            createDataCell(row, colIndex++, third.getSocialReason(), dataStyle);
            createDataCell(row, colIndex++, third.getGender() != null ? third.getGender().toString() : "", dataStyle);
            createDataCell(row, colIndex++, third.getState() != null && third.getState() ? "ACTIVO" : "INACTIVO", dataStyle);

            // Datos opcionales
            if (Boolean.TRUE.equals(request.getIncludeTypes())) {
                String types = third.getThirdTypes().stream()
                        .map(ThirdType::getThirdTypeName)
                        .collect(Collectors.joining(", "));
                createDataCell(row, colIndex++, types, dataStyle);
            }

            if (Boolean.TRUE.equals(request.getIncludeCities())) {
                createDataCell(row, colIndex++, third.getCountry() != null ? third.getCountry().getCountryName() : "", dataStyle);
                createDataCell(row, colIndex++, third.getProvince() != null ? third.getProvince().getStateName() : "", dataStyle);
                createDataCell(row, colIndex++, third.getCity() != null ? third.getCity().getCityName() : "", dataStyle);
            }

            createDataCell(row, colIndex++, third.getAddress(), dataStyle);
            createDataCell(row, colIndex++, third.getPhoneNumber(), dataStyle);
            createDataCell(row, colIndex++, third.getEmail(), dataStyle);
        }
    }

    private void createDataCell(Row row, int colIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
        
        cell.setCellStyle(style);
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            // Primero aplicar el auto-sizing basado en el contenido
            sheet.autoSizeColumn(i);
            
            // Obtener el ancho calculado automáticamente
            int autoWidth = sheet.getColumnWidth(i);
            
            // Establecer límites razonables
            int minWidth = 1500;  // Ancho mínimo más pequeño
            int maxWidth = 25000; // Ancho máximo más generoso
            
            // Aplicar los límites
            if (autoWidth < minWidth) {
                sheet.setColumnWidth(i, minWidth);
            } else if (autoWidth > maxWidth) {
                sheet.setColumnWidth(i, maxWidth);
            }
            
            // Agregar un pequeño padding al ancho calculado para mejor legibilidad
            int finalWidth = Math.min(Math.max(autoWidth + 500, minWidth), maxWidth);
            sheet.setColumnWidth(i, finalWidth);
        }
    }

    private int getColumnCount(ThirdExportRequest request) {
        int count = 9; // Columnas básicas (incluye Estado)
        
        if (Boolean.TRUE.equals(request.getIncludeTypes())) {
            count++;
        }
        
        if (Boolean.TRUE.equals(request.getIncludeCities())) {
            count += 3;
        }
        
        return count + 3; // Dirección, teléfono, email
    }

    /**
     * Exporta una plantilla de terceros con validaciones de datos (listas desplegables).
     */
    @Override
    public Resource exportThirdTemplateWithValidations(String entId) {
        
        try {
            byte[] templateData = generateTemplateWithValidations(entId);
            return new ByteArrayResource(templateData);
            
        } catch (Exception e) {
            throw new ThirdExportException(ThirdsErrorCode.THIRD_EXPORT_ERROR, 
                "Error al generar plantilla con validaciones", e);
        }
    }

    /**
     * Exporta terceros existentes con validaciones de datos (listas desplegables).
     * Combina los datos reales con las validaciones de la plantilla.
     */
    @Override
    public Resource exportThirdsWithValidations(ThirdExportRequest exportRequest) {
        
        try {
            // Obtener datos según filtros
            List<Third> thirds = getFilteredThirds(exportRequest);
            
            // Validar que existan terceros para exportar
            if (thirds == null || thirds.isEmpty()) {
                throw new ThirdExportException(ThirdsErrorCode.THIRD_EXPORT_NO_DATA);
            }
            
            // Generar archivo Excel con datos y validaciones
            byte[] excelData = generateExcelFileWithValidations(thirds, exportRequest);
            
            return new ByteArrayResource(excelData);
            
        } catch (ThirdExportException e) {
            // Re-lanzar excepciones de negocio sin modificar
            throw e;
        } catch (Exception e) {
            throw new ThirdExportException(ThirdsErrorCode.THIRD_EXPORT_ERROR, 
                "Error al generar archivo de exportación con validaciones", e);
        }
    }


    private byte[] generateTemplateWithValidations(String entId) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Plantilla_Terceros");
            
            // Crear estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle templateStyle = createTemplateStyle(workbook);

            // Crear encabezados
            ThirdExportRequest templateRequest = ThirdExportRequest.builder()
                .entId(entId)
                .includeTypes(true)
                .includeCities(true)
                .build();
            
            createHeaders(sheet, headerStyle, templateRequest);

            // Crear filas de ejemplo con estilos
            createTemplateRows(sheet, templateStyle, templateRequest, 10); // 10 filas de ejemplo

            // Crear hoja de datos de referencia
            excelValidationService.createReferenceDataSheet(workbook, entId);

            // Aplicar validaciones de datos
            applyValidationsToTemplate(sheet, entId, templateRequest);

            // Ajustar ancho de columnas
            autoSizeColumns(sheet, getColumnCount(templateRequest));

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }


    private CellStyle createTemplateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setItalic(true);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private void createTemplateRows(Sheet sheet, CellStyle templateStyle, ThirdExportRequest request, int numberOfRows) {
        for (int i = 1; i <= numberOfRows; i++) {
            Row row = sheet.createRow(i);
            int colIndex = 0;

            if (i == 1) {
                // Primera fila con indicadores (estilo normal)
                createTemplateCell(row, colIndex++, "Seleccionar...", templateStyle);
                createTemplateCell(row, colIndex++, "123456789", templateStyle);
                createTemplateCell(row, colIndex++, "1", templateStyle);
                createTemplateCell(row, colIndex++, "Seleccionar...", templateStyle);
                createTemplateCell(row, colIndex++, "Nombres", templateStyle);
                createTemplateCell(row, colIndex++, "Apellidos", templateStyle);
                createTemplateCell(row, colIndex++, "Razón Social", templateStyle);
                createTemplateCell(row, colIndex++, "Seleccionar...", templateStyle);
                createTemplateCell(row, colIndex++, "ACTIVO", templateStyle);

                if (Boolean.TRUE.equals(request.getIncludeTypes())) {
                    createTemplateCell(row, colIndex++, "Seleccionar...", templateStyle);
                }

                if (Boolean.TRUE.equals(request.getIncludeCities())) {
                    createTemplateCell(row, colIndex++, "Seleccionar...", templateStyle);
                    createTemplateCell(row, colIndex++, "Seleccionar...", templateStyle);
                    createTemplateCell(row, colIndex++, "Seleccionar...", templateStyle);
                }

                createTemplateCell(row, colIndex++, "Dirección ejemplo", templateStyle);
                createTemplateCell(row, colIndex++, "3001234567", templateStyle);
                createTemplateCell(row, colIndex++, "ejemplo@correo.com", templateStyle);
            } else {
                // Filas adicionales vacías con el mismo estilo
                createEmptyTemplateRow(row, request, templateStyle);
            }
        }
    }

    private void createEmptyTemplateRow(Row row, ThirdExportRequest request, CellStyle templateStyle) {
        int colIndex = 0;
        
        // Crear celdas vacías con el mismo estilo
        int basicColumns = 9; // Columnas básicas incluye Estado
        for (int i = 0; i < basicColumns; i++) {
            createTemplateCell(row, colIndex++, "", templateStyle);
        }

        if (Boolean.TRUE.equals(request.getIncludeTypes())) {
            createTemplateCell(row, colIndex++, "", templateStyle);
        }

        if (Boolean.TRUE.equals(request.getIncludeCities())) {
            createTemplateCell(row, colIndex++, "", templateStyle);
            createTemplateCell(row, colIndex++, "", templateStyle);
            createTemplateCell(row, colIndex++, "", templateStyle);
        }

        // Columnas finales: Dirección, Teléfono, Email
        createTemplateCell(row, colIndex++, "", templateStyle);
        createTemplateCell(row, colIndex++, "", templateStyle);
        createTemplateCell(row, colIndex++, "", templateStyle);
    }

    private void createTemplateCell(Row row, int colIndex, String value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void applyValidationsToTemplate(Sheet sheet, String entId, ThirdExportRequest request) {
        int startRow = 1; // Después del encabezado
        int endRow = 1000; // Permitir muchas filas para la plantilla
        
        // Aplicar validaciones básicas
        excelValidationService.applyThirdValidations(sheet, entId, startRow, endRow);
        
        // Aplicar validaciones de tipos si están incluidos
        if (Boolean.TRUE.equals(request.getIncludeTypes())) {
            int typesColumnIndex = getTypesColumnIndex(request);
            excelValidationService.applyThirdValidationsWithTypes(sheet, entId, startRow, endRow, typesColumnIndex);
        }
        
        // Aplicar validaciones geográficas si están incluidas
        if (Boolean.TRUE.equals(request.getIncludeCities())) {
            int[] geoColumns = getGeographyColumnIndexes(request);
            excelValidationService.applyGeographyValidations(sheet, startRow, endRow, 
                geoColumns[0], geoColumns[1], geoColumns[2]);
        }
    }


    private int getTypesColumnIndex(ThirdExportRequest request) {
        // Los tipos de tercero aparecen después de las columnas básicas (9 columnas incluye Estado)
        return 9;
    }

    private int[] getGeographyColumnIndexes(ThirdExportRequest request) {
        int baseIndex = 9; // Columnas básicas (incluye Estado)
        
        if (Boolean.TRUE.equals(request.getIncludeTypes())) {
            baseIndex++; // Agregar columna de tipos
        }
        
        return new int[]{baseIndex, baseIndex + 1, baseIndex + 2}; // País, Departamento, Ciudad
    }

    /**
     * Genera archivo Excel con datos reales y validaciones aplicadas.
     */
    private byte[] generateExcelFileWithValidations(List<Third> thirds, ThirdExportRequest request) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Terceros");
            
            // Crear estilos (reutilizando métodos existentes)
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            // Crear encabezados (reutilizando método existente)
            createHeaders(sheet, headerStyle, request);

            fillData(sheet, thirds, dataStyle, dateStyle, request);

            // Crear hoja de datos de referencia para validaciones
            excelValidationService.createReferenceDataSheet(workbook, request.getEntId());

            // Aplicar validaciones de datos (reutilizando método existente)
            applyValidationsToDataSheet(sheet, request.getEntId(), request, thirds.size());

            // Ajustar ancho de columnas (reutilizando método existente)
            autoSizeColumns(sheet, getColumnCount(request));

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * Aplica validaciones de datos a una hoja con datos existentes.
     * Reutiliza la lógica de applyValidationsToTemplate pero ajustada para datos reales.
     */
    private void applyValidationsToDataSheet(Sheet sheet, String entId, ThirdExportRequest request, int dataRowCount) {
        int startRow = 1; // Después del encabezado
        int endRow = Math.max(dataRowCount + 100, 1000); // Datos existentes + filas adicionales para edición
        
        // Aplicar validaciones básicas (reutilizando método existente)
        excelValidationService.applyThirdValidations(sheet, entId, startRow, endRow);
        
        // Aplicar validaciones de tipos si están incluidos 
        if (Boolean.TRUE.equals(request.getIncludeTypes())) {
            int typesColumnIndex = getTypesColumnIndex(request);
            excelValidationService.applyThirdValidationsWithTypes(sheet, entId, startRow, endRow, typesColumnIndex);
        }
        
        // Aplicar validaciones geográficas si están incluidas
        if (Boolean.TRUE.equals(request.getIncludeCities())) {
            int[] geoColumns = getGeographyColumnIndexes(request);
            excelValidationService.applyGeographyValidations(sheet, startRow, endRow, 
                geoColumns[0], geoColumns[1], geoColumns[2]);
        }
    }
}
