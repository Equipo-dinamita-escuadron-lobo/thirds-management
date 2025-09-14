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
    

    @Override
    public Resource exportThirdsToExcel(ThirdExportRequest exportRequest) {
        log.info("Iniciando exportación de terceros para entidad: {}", exportRequest.getEntId());
        
        try {
            // Obtener datos según filtros
            List<Third> thirds = getFilteredThirds(exportRequest);
            
            // Validar que existan terceros para exportar
            if (thirds == null || thirds.isEmpty()) {
                throw new ThirdExportException(ThirdsErrorCode.THIRD_EXPORT_NO_DATA);
            }
            
            // Generar archivo Excel
            byte[] excelData = generateExcelFile(thirds, exportRequest);
            
            return new ByteArrayResource(excelData);
            
        } catch (ThirdExportException e) {
            // Re-lanzar excepciones de negocio sin modificar
            throw e;
        } catch (Exception e) {
            throw new ThirdExportException(ThirdsErrorCode.THIRD_EXPORT_ERROR, "Error al generar archivo de exportación", e);
        }
    }

    private List<Third> getFilteredThirds(ThirdExportRequest request) {
        // Exportar todos los terceros sin aplicar filtros
        return thirdOutputPort.getAllThirds(request.getEntId());
    }


    private byte[] generateExcelFile(List<Third> thirds, ThirdExportRequest request) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Terceros");
            
            // Crear estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            // Crear encabezados
            createHeaders(sheet, headerStyle, request);

            // Llenar datos
            fillData(sheet, thirds, dataStyle, dateStyle, request);

            // Ajustar ancho de columnas
            autoSizeColumns(sheet, getColumnCount(request));

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
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
            createDataCell(row, colIndex++, third.getPersonType() != null ? third.getPersonType().toString() : "", dataStyle);
            createDataCell(row, colIndex++, third.getNames(), dataStyle);
            createDataCell(row, colIndex++, third.getLastNames(), dataStyle);
            createDataCell(row, colIndex++, third.getSocialReason(), dataStyle);
            createDataCell(row, colIndex++, third.getGender() != null ? third.getGender().toString() : "", dataStyle);

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
        int count = 8; // Columnas básicas
        
        if (Boolean.TRUE.equals(request.getIncludeTypes())) {
            count++;
        }
        
        if (Boolean.TRUE.equals(request.getIncludeCities())) {
            count += 3;
        }
        
        return count + 3; // Dirección, teléfono, email
    }
}
