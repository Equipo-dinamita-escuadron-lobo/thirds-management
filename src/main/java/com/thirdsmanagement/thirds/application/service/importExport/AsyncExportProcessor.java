package com.thirdsmanagement.thirds.application.service.importExport;

import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdExportRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @brief Servicio para procesar exportaciones de terceros de forma asíncrona
 *
 * Este servicio ejecuta la exportación en un hilo separado para no bloquear
 * la petición HTTP, permitiendo exportaciones de grandes volúmenes de datos.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncExportProcessor {

    private final ThirdOutputPort thirdOutputPort;
    private final ExportJobTracker jobTracker;
    private final ExcelValidationService excelValidationService;
    
    private static final int EXPORT_PAGE_SIZE = 5000;

    /**
     * @brief Procesa la exportación de forma asíncrona
     * @param exportRequest solicitud de exportación con filtros
     * @param jobId identificador del trabajo
     */
    @Async
    public void processExportAsync(ThirdExportRequest exportRequest, String jobId) {
        log.info("JobId {}: Iniciando procesamiento ASÍNCRONO de exportación en thread: {}",
                jobId, Thread.currentThread().getName());

        long totalStartTime = System.currentTimeMillis();
        java.util.Map<String, Long> phaseTimes = new java.util.LinkedHashMap<>();
        int totalRecords = 0;

        try {
            jobTracker.updateJobStatus(jobId, ImportStatus.PROCESSING);
            jobTracker.updateProgress(jobId, 10);

            // FASE 1: Obtener datos filtrados con paginación
            log.info("JobId {}: Fase 1 - Obtención de datos", jobId);
            long phase1Start = System.currentTimeMillis();
            List<Third> thirds = getFilteredThirds(exportRequest, jobId);
            long phase1Time = System.currentTimeMillis() - phase1Start;
            phaseTimes.put("1. Obtención de Datos", phase1Time);
            log.info("JobId {}: Fase 1 completada en {} ms - {} registros obtenidos", 
                    jobId, phase1Time, thirds.size());
            
            totalRecords = thirds.size();
            jobTracker.updateTotalRecords(jobId, totalRecords);
            jobTracker.updateProgress(jobId, 50);

            // Validar que existan datos
            if (thirds == null || thirds.isEmpty()) {
                handleNoDataError(exportRequest, jobId);
                return;
            }

            // FASE 2: Generar archivo Excel
            log.info("JobId {}: Fase 2 - Generación de archivo Excel", jobId);
            long phase2Start = System.currentTimeMillis();
            byte[] excelData = generateExcelFile(thirds, exportRequest, jobId);
            long phase2Time = System.currentTimeMillis() - phase2Start;
            phaseTimes.put("2. Generación Excel", phase2Time);
            log.info("JobId {}: Fase 2 completada en {} ms - Archivo generado ({} bytes)", 
                    jobId, phase2Time, excelData.length);

            jobTracker.updateProgress(jobId, 90);

            // FASE 3: Almacenar archivo en memoria
            log.info("JobId {}: Fase 3 - Almacenamiento del archivo", jobId);
            long phase3Start = System.currentTimeMillis();
            jobTracker.setFileData(jobId, excelData);
            long phase3Time = System.currentTimeMillis() - phase3Start;
            phaseTimes.put("3. Almacenamiento", phase3Time);
            log.info("JobId {}: Fase 3 completada en {} ms", jobId, phase3Time);

            // Completar
            jobTracker.updateJobStatus(jobId, ImportStatus.COMPLETED);
            jobTracker.updateProgress(jobId, 100);

            long totalTime = System.currentTimeMillis() - totalStartTime;

            // Imprimir tabla de tiempos
            printPhaseTimesTable(jobId, phaseTimes, totalTime, totalRecords);

            log.info("JobId {}: Exportación completada exitosamente en {} ms", jobId, totalTime);

        } catch (Exception e) {
            log.error("JobId {}: Error durante la exportación asíncrona", jobId, e);
            handleAsyncError(jobId, e);
        }
    }
    
    /**
     * @brief Imprime una tabla formateada con los tiempos de cada fase
     * @param jobId identificador del job
     * @param phaseTimes mapa con los tiempos de cada fase
     * @param totalTime tiempo total de procesamiento
     * @param totalRecords total de registros procesados
     */
    private void printPhaseTimesTable(String jobId, java.util.Map<String, Long> phaseTimes, 
                                      long totalTime, int totalRecords) {
        StringBuilder table = new StringBuilder("\n");
        table.append("╔════════════════════════════════════════════════════════════════════════╗\n");
        table.append(String.format("║  RESUMEN DE TIEMPOS - JobId: %-40s ║\n", jobId));
        table.append("╠════════════════════════════════════════════════════════════════════════╣\n");
        table.append("║  Fase                          │ Tiempo (ms) │ Tiempo (s) │ Porcentaje ║\n");
        table.append("╠════════════════════════════════════════════════════════════════════════╣\n");
        
        for (java.util.Map.Entry<String, Long> entry : phaseTimes.entrySet()) {
            String phaseName = entry.getKey();
            long phaseTime = entry.getValue();
            double seconds = phaseTime / 1000.0;
            double percentage = (phaseTime * 100.0) / totalTime;
            
            table.append(String.format("║  %-30s│ %,11d │ %10.2f │   %6.2f%% ║\n",
                    phaseName, phaseTime, seconds, percentage));
        }
        
        table.append("╠════════════════════════════════════════════════════════════════════════╣\n");
        table.append(String.format("║  TOTAL                         │ %,11d │ %10.2f │  100.00%% ║\n",
                totalTime, totalTime / 1000.0));
        table.append("╠════════════════════════════════════════════════════════════════════════╣\n");
        
        // Calcular métricas de rendimiento
        double recordsPerSecond = totalRecords > 0 ? (totalRecords * 1000.0) / totalTime : 0;
        double msPerRecord = totalRecords > 0 ? totalTime / (double) totalRecords : 0;
        
        table.append(String.format("║  Total Registros: %-15d                                   ║\n", totalRecords));
        table.append(String.format("║  Rendimiento: %,.2f registros/seg                                ║\n", recordsPerSecond));
        table.append(String.format("║  Tiempo por registro: %.2f ms                                    ║\n", msPerRecord));
        table.append("╚════════════════════════════════════════════════════════════════════════╝");
        
        log.info("JobId {}: {}", jobId, table.toString());
    }

    /**
     * @brief Obtiene terceros filtrados con paginación optimizada para exportación
     * @details Utiliza métodos optimizados que eliminan el problema N+1 queries mediante
     * carga batch de todas las relaciones. Reduce drásticamente el tiempo de obtención de datos.
     * @param request solicitud de exportación con filtros
     * @param jobId identificador del trabajo (para logging)
     * @return lista completa de terceros filtrados con todas sus relaciones pre-cargadas
     */
    private List<Third> getFilteredThirds(ThirdExportRequest request, String jobId) {
        List<Third> allThirds = new ArrayList<>();
        int currentPage = 0;
        Page<Third> page;

        do {
            Pageable pageable = PageRequest.of(currentPage, EXPORT_PAGE_SIZE);
            
            // Usar métodos optimizados específicos para exportación
            if (request.getStatus() != null) {
                page = thirdOutputPort.getAllThirdsByStateForExport(request.getEntId(), request.getStatus(), pageable);
            } else {
                page = thirdOutputPort.getAllThirdsForExport(request.getEntId(), pageable);
            }
            
            if (page != null && page.hasContent()) {
                allThirds.addAll(page.getContent());
                log.debug("JobId {}: Página {} procesada - {} registros acumulados", 
                        jobId, currentPage, allThirds.size());
            }
            
            currentPage++;
            
        } while (page != null && page.hasNext());
        
        return allThirds;
    }

    /**
     * @brief Genera el archivo Excel con los datos de terceros
     * @param thirds lista de terceros a exportar
     * @param request solicitud de exportación con configuración
     * @param jobId identificador del trabajo
     * @return arreglo de bytes con el contenido del archivo Excel
     * @throws IOException si ocurre un error al escribir el archivo
     */
    private byte[] generateExcelFile(List<Third> thirds, ThirdExportRequest request, String jobId) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            log.debug("JobId {}: Creando workbook y sheet", jobId);
            Sheet sheet = workbook.createSheet("Terceros");

            // Importar métodos de estilo y creación desde ExportThirdService
            // (estos métodos deberían estar en una clase utilitaria, pero por ahora
            // los replicamos aquí para mantener la lógica contenida)
            
            ExcelStyleHelper styleHelper = new ExcelStyleHelper(workbook);
            ExcelDataWriter dataWriter = new ExcelDataWriter(styleHelper);

            log.debug("JobId {}: Creando encabezados", jobId);
            dataWriter.createHeaders(sheet, request);

            log.debug("JobId {}: Escribiendo {} registros", jobId, thirds.size());
            dataWriter.fillData(sheet, thirds, request);

            log.debug("JobId {}: Creando hoja de datos de referencia", jobId);
            excelValidationService.createReferenceDataSheet(workbook, request.getEntId());

            log.debug("JobId {}: Aplicando validaciones", jobId);
            dataWriter.applyValidations(sheet, request, thirds.size(), excelValidationService);

            log.debug("JobId {}: Ajustando columnas", jobId);
            dataWriter.autoSizeColumns(sheet, request);

            log.debug("JobId {}: Escribiendo archivo a stream", jobId);
            workbook.write(outputStream);
            
            return outputStream.toByteArray();
        }
    }

    /**
     * @brief Maneja el error cuando no hay datos para exportar
     * @param exportRequest solicitud de exportación
     * @param jobId identificador del trabajo
     */
    private void handleNoDataError(ThirdExportRequest exportRequest, String jobId) {
        String errorMessage;
        
        if (exportRequest.getStatus() != null) {
            errorMessage = "No hay terceros " + (exportRequest.getStatus() ? "activos" : "inactivos") + " para exportar";
        } else {
            errorMessage = "No hay terceros para exportar";
        }
        
        log.warn("JobId {}: {}", jobId, errorMessage);
        jobTracker.updateJobStatus(jobId, ImportStatus.FAILED);
        jobTracker.setErrorMessage(jobId, errorMessage);
        jobTracker.updateProgress(jobId, 100);
    }

    /**
     * @brief Maneja errores durante la exportación asíncrona
     * @param jobId identificador del trabajo
     * @param e excepción ocurrida
     */
    private void handleAsyncError(String jobId, Exception e) {
        String errorMessage = e.getMessage() != null ? e.getMessage() : "Error desconocido durante la exportación";
        jobTracker.updateJobStatus(jobId, ImportStatus.FAILED);
        jobTracker.setErrorMessage(jobId, errorMessage);
        jobTracker.updateProgress(jobId, 100);
        log.error("JobId {}: Exportación fallida - {}", jobId, errorMessage);
    }
}

