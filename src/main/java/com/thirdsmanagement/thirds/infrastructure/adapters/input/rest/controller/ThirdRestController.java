package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.thirdsmanagement.thirds.application.ports.input.BulkChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.input.DeleteThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ExportThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.GetThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ImportThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ListThirdsUseCase;
import com.thirdsmanagement.thirds.application.ports.output.PdfRUTContentOutput;
import com.thirdsmanagement.thirds.application.service.importExport.PdfRUTService;
import com.thirdsmanagement.thirds.application.service.third.CreateThirdService;
import com.thirdsmanagement.thirds.application.service.third.UpdateThirdService;
import com.thirdsmanagement.thirds.domain.enums.ExportableField;
import com.thirdsmanagement.thirds.domain.enums.ImportStatus;
import com.thirdsmanagement.thirds.domain.model.ExportJobStatus;
import com.thirdsmanagement.thirds.domain.model.ImportJobStatus;
import com.thirdsmanagement.thirds.domain.model.PdfRUTContent;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.domain.utils.ValidationUtils;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdExportRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdImportRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.BulkStateChangeResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ChangeThirdStateResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.ThirdRestMapper;
import com.thirdsmanagement.thirds.infrastructure.utils.ExcelFileNameGenerator;
import com.thirdsmanagement.thirds.infrastructure.utils.PaginationHelper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * @brief Controlador REST principal para gestión completa de terceros
 *
 *        Adaptador de entrada que expone la API completa para gestión de
 *        terceros:
 *        CRUD completo, importación/exportación, cambios de estado masivos,
 *        validación de RUT, y consultas avanzadas con filtros y paginación.
 */
@RestController
@RequestMapping("/api/thirds")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('admin_client') or hasRole('super_client')")
public class ThirdRestController {

    private final ListThirdsUseCase listThirdsUseCase;
    private final GetThirdUseCase getThirdUseCase;
    private final ChangeThirdStateUseCase changeThirdStateUseCase;
    private final BulkChangeThirdStateUseCase bulkChangeThirdStateUseCase;
    private final DeleteThirdUseCase deleteThirdUseCase;
    private final ExportThirdUseCase exportThirdUseCase;
    private final ImportThirdUseCase importThirdUseCase;
    private final ThirdRestMapper thirdRestMapper;
    private final PdfRUTService pdfRUTService;
    private final CreateThirdService createThirdService;
    private final UpdateThirdService updateThirdService;
    private final ExcelFileNameGenerator fileNameGenerator;

    /**
     * @brief Crea un nuevo tercero en el sistema
     *
     *        Endpoint para registrar un nuevo tercero con validación completa
     *        de datos, jerarquía geográfica y reglas de negocio.
     * @param thirdCreateRequest datos completos del tercero a crear
     * @return tercero creado con código HTTP 201
     */
    @PostMapping("/")
    public ResponseEntity<ThirdResponse> createThird(@RequestBody @Valid ThirdCreateRequest thirdCreateRequest) {

        Third third = thirdRestMapper.toThird(thirdCreateRequest);

        // Use geography validation service for proper geography integration
        third = createThirdService.createThird(third,
                thirdCreateRequest.getCountryCode(),
                thirdCreateRequest.getStateCode(),
                thirdCreateRequest.getCityCode());

        return new ResponseEntity<>(thirdRestMapper.toThirdCreateResponse(third), HttpStatus.CREATED);
    }

    /**
     * @brief Actualiza datos de un tercero existente
     *
     *        Endpoint para modificar la información de un tercero existente
     *        con validación completa de jerarquía geográfica.
     * @param thirdUpdateRequest datos actualizados del tercero
     * @return tercero actualizado
     */
    @PostMapping("/update")
    public ResponseEntity<ThirdResponse> updateThird(@RequestBody @Valid ThirdUpdateRequest thirdUpdateRequest) {

        Third third = thirdRestMapper.toThird(thirdUpdateRequest);

        // Use geography validation service for proper geography integration
        third = updateThirdService.updateThirdWithGeography(third,
                thirdUpdateRequest.getCountryCode(),
                thirdUpdateRequest.getStateCode(),
                thirdUpdateRequest.getCityCode());

        return new ResponseEntity<>(thirdRestMapper.toThirdCreateResponse(third), HttpStatus.OK);
    }

    /**
     * @brief Cambia el estado activo/inactivo de un tercero
     *
     *        Endpoint para alternar el estado de un tercero específico entre activo
     *        e inactivo.
     * @param thId  identificador único del tercero
     * @param entId identificador de la empresa
     * @return resultado del cambio de estado
     */
    @PutMapping("/")
    public ResponseEntity<ChangeThirdStateResponse> changeThirdState(
            @NotNull(message = "thId es requerido") @RequestParam Long thId,
            @NotNull(message = "entId es requerido") @RequestParam String entId) {
        Boolean result = changeThirdStateUseCase.changeThirdState(thId, entId);

        return new ResponseEntity<>(thirdRestMapper.toChangeThirdStateResponse(result), HttpStatus.OK);
    }

    /**
     * @brief Cambia el estado de todos los terceros de una empresa de forma masiva
     *
     *        Endpoint para cambiar el estado (activo/inactivo) de todos los
     *        terceros
     *        pertenecientes a una empresa en una sola operación.
     * @param entId    identificador de la empresa
     * @param newState nuevo estado para aplicar a todos los terceros
     * @return cantidad de terceros actualizados
     */
    @PatchMapping("/allState")
    public ResponseEntity<BulkStateChangeResponse> changeAllThirdsState(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @NotNull(message = "newState es requerido") @RequestParam Boolean newState) {

        int updatedCount = bulkChangeThirdStateUseCase.changeAllThirdsState(entId, newState);

        String message = String.format("Se actualizaron %d terceros al estado %s",
                updatedCount, newState ? "activo" : "inactivo");

        BulkStateChangeResponse response = BulkStateChangeResponse.builder()
                .updatedCount(updatedCount)
                .newState(newState)
                .message(message)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * @brief Obtiene un tercero específico por ID y empresa
     *
     *        Endpoint para consultar los datos completos de un tercero específico.
     * @param thId  identificador único del tercero
     * @param entId identificador de la empresa
     * @return datos completos del tercero solicitado
     */
    @GetMapping("/third")
    public ResponseEntity<Third> getThirdById(
            @NotNull(message = "thId es requerido") @RequestParam Long thId,
            @NotNull(message = "entId es requerido") @RequestParam String entId) {

        Third third = getThirdUseCase.getThirdById(thId, entId);

        return new ResponseEntity<>(third, HttpStatus.OK);
    }

    /**
     * @brief Verifica si existe un tercero.
     * @param idNumber ID del tercero.
     * @param entId    ID de la empresa.
     * @return Respuesta con el resultado de la verificación.
     */
    @GetMapping("/existBy")
    public ResponseEntity<Boolean> existThirdById(
            @NotNull(message = "idNumber es requerido") @RequestParam Long idNumber,
            @NotNull(message = "entId es requerido") @RequestParam String entId) {

        boolean exists = getThirdUseCase.existThirdById(idNumber, entId);

        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    /**
     * @brief Obtiene una lista de terceros con paginación flexible, búsqueda y
     *        ordenamiento.
     *        Si no se especifican parámetros de paginación, retorna todos los
     *        terceros.
     * 
     * @param entId     Id de la empresa
     * @param numPage   Número de página (opcional)
     * @param size      Tamaño de página (opcional)
     * @param sortField Campo de ordenamiento (opcional, default: "names")
     * @param sortOrder Orden asc/desc (opcional, default: "asc")
     * @param search    Término de búsqueda (opcional)
     * @return Respuesta con la lista de terceros
     */
    @GetMapping("/")
    public ResponseEntity<Page<Third>> getThirdsList(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @RequestParam(required = false) Optional<Integer> numPage,
            @RequestParam(required = false) Optional<Integer> size,
            @RequestParam(defaultValue = "names") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String search) {

        // Contar total de registros (con o sin filtro)
        long totalRecords = (search != null && !search.trim().isEmpty())
                ? listThirdsUseCase.countByEntIdAndSearch(entId, search)
                : listThirdsUseCase.countAllThirdsByEntId(entId);

        // Crear Pageable flexible
        Pageable pageable = PaginationHelper.createFlexiblePageable(numPage, size, totalRecords);

        // Obtener página de datos (con o sin filtro)
        Page<Third> page = (search != null && !search.trim().isEmpty())
                ? listThirdsUseCase.findByEntIdAndSearch(entId, search, pageable.getPageNumber(),
                        pageable.getPageSize(), sortField, sortOrder)
                : listThirdsUseCase.getAllThirdsByWithSort(entId, pageable.getPageNumber(),
                        pageable.getPageSize(), sortField, sortOrder);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    /**
     * @brief Obtiene una lista de terceros activos con paginación flexible y
     *        ordenamiento.
     *
     * @param entId     Id de la empresa
     * @param numPage   Número de página (opcional)
     * @param size      Tamaño de página (opcional)
     * @param sortField Campo de ordenamiento (opcional, default: "names")
     * @param sortOrder Orden asc/desc (opcional, default: "asc")
     * @return Respuesta con la lista de terceros activos
     */
    @GetMapping("/findAllActive")
    public ResponseEntity<Page<Third>> getActiveThirds(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @RequestParam(required = false) Optional<Integer> numPage,
            @RequestParam(required = false) Optional<Integer> size,
            @RequestParam(defaultValue = "names") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        // Contar total de registros activos
        long totalRecords = listThirdsUseCase.countActiveThirdsByEntId(entId);

        // Crear Pageable flexible
        Pageable pageable = PaginationHelper.createFlexiblePageable(numPage, size, totalRecords);

        // Obtener página de datos activos
        Page<Third> page = listThirdsUseCase.getAllActiveThirdsByWithSort(entId, pageable.getPageNumber(),
                pageable.getPageSize(), sortField, sortOrder);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    /**
     * @brief Cargar un archivo PDF y extraer su contenido RUT.
     * @param file el archivo PDF que se va a cargar.
     * @return ResponseEntity con el contenido extraído del PDF en caso de éxito,
     *         o un ResponseEntity con un estado de error en caso de fallo.
     */
    @PostMapping("/content-PDF-RUT")
    public ResponseEntity<PdfRUTContentOutput> uploadPdf(@RequestParam MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        PdfRUTContent request = new PdfRUTContent(file);
        PdfRUTContentOutput response = pdfRUTService.extractContent(request);
        return ResponseEntity.ok(response);
    }

    /**
     * @brief Exporta una plantilla de terceros.
     * @param entId identificador de la empresa
     * @return ResponseEntity con la plantilla de terceros
     */
    @GetMapping("/template/excel")
    public ResponseEntity<Resource> exportThirdTemplate(
            @RequestParam String entId) {

        Resource templateFile = exportThirdUseCase.exportThirdTemplateWithValidations(entId);
        String filename = fileNameGenerator.generateTemplateFileName();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(templateFile);
    }

    /**
     * @brief Inicia exportación asíncrona de terceros a formato Excel.
     *        Utiliza configuración flexible de campos opcionales mediante
     *        ExportableField.
     * 
     * @param entId          Identificador de la entidad (requerido)
     * @param status         Estado de los terceros (true=activos, false=inactivos,
     *                       null=todos)
     * @param companyName    Nombre de la empresa para el nombre del archivo
     * @param optionalFields Conjunto de campos opcionales a incluir (GENDER,
     *                       COUNTRY, STATE, CITY)
     * @return ResponseEntity con jobId y mensaje de confirmación
     */
    @GetMapping("/export/excel")
    public ResponseEntity<Map<String, String>> exportThirdsWithValidationsAsync(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) Set<ExportableField> optionalFields) {

        // Convertir status de String a Boolean, manejando strings vacíos
        Boolean statusBoolean = ValidationUtils.parseOptionalBoolean(status);

        // Si no se especifican campos opcionales, usar conjunto vacío
        Set<ExportableField> fields = optionalFields != null ? optionalFields : Set.of();

        ThirdExportRequest exportRequest = ThirdExportRequest.builder()
                .entId(entId)
                .status(statusBoolean)
                .companyName(companyName)
                .optionalFields(fields)
                .build();

        String jobId = exportThirdUseCase.exportThirdsAsync(exportRequest);

        Map<String, String> response = Map.of(
                "jobId", jobId,
                "message", "Exportación iniciada correctamente",
                "downloadUrl", "/api/thirds/export/download/" + jobId);

        return ResponseEntity.accepted().body(response);
    }

    /**
     * @brief Consulta el estado de una exportación asíncrona
     * 
     * @param jobId identificador único del job de exportación
     * @return ResponseEntity con el estado del job
     */
    @GetMapping("/export/status/{jobId}")
    public ResponseEntity<ExportJobStatus> getExportStatus(@PathVariable String jobId) {
        Optional<com.thirdsmanagement.thirds.domain.model.ExportJobStatus> jobStatus = exportThirdUseCase
                .getExportStatus(jobId);
        return jobStatus.map(status -> new ResponseEntity<>(status, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * @brief Descarga el archivo Excel generado por una exportación asíncrona
     * 
     * @param jobId identificador único del job de exportación
     * @return ResponseEntity con el archivo Excel si está disponible
     */
    @GetMapping("/export/download/{jobId}")
    public ResponseEntity<Resource> downloadExportedFile(@PathVariable String jobId) {
        Optional<ExportJobStatus> jobStatusOpt = exportThirdUseCase.getExportStatus(jobId);

        if (jobStatusOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ExportJobStatus jobStatus = jobStatusOpt.get();

        // Verificar que el job esté completado
        if (jobStatus.getStatus() != ImportStatus.COMPLETED) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(null); // Indicar que aún no está listo
        }

        // Verificar que los datos del archivo existan
        if (jobStatus.getFileData() == null || jobStatus.getFileData().length == 0) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Crear recurso con los datos del archivo
        ByteArrayResource resource = new ByteArrayResource(jobStatus.getFileData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + jobStatus.getFileName() + "\"")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    /**
     * @brief Inicia una importación asíncrona de terceros masivamente desde archivo
     *        Excel
     *
     *        Endpoint para carga masiva asíncrona de terceros desde archivo Excel.
     *        La importación
     *        se ejecuta en segundo plano y retorna inmediatamente un ID de job para
     *        consultar el estado.
     *        Este método retorna de forma inmediata después de crear el job, sin
     *        esperar el procesamiento.
     * 
     * @param entId identificador de la empresa
     * @param file  archivo Excel con los datos de terceros a importar
     * @return ResponseEntity con jobId único para consultar el estado de la
     *         importación y mensaje de confirmación
     */
    @PostMapping("/import/excel")
    public ResponseEntity<Map<String, String>> importThirdsFromExcel(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @NotNull(message = "El archivo Excel es obligatorio") @RequestParam("file") MultipartFile file) {

        ThirdImportRequest importRequest = ThirdImportRequest.builder()
                .entId(entId)
                .excelFile(file)
                .fileName(file.getOriginalFilename())
                .build();

        // Crea el job y lanza el procesamiento asíncrono (retorna inmediatamente)
        String jobId = importThirdUseCase.importThirdsFromExcel(importRequest);

        Map<String, String> response = Map.of(
                "jobId", jobId,
                "message", "Importación iniciada correctamente",
                "statusUrl", "/api/thirds/import/status/" + jobId);
        return ResponseEntity.accepted().body(response);
    }

    /**
     * @brief Consulta el estado de una importación asíncrona de terceros
     *
     *        Endpoint para consultar el progreso y resultados de una importación en
     *        ejecución o completada.
     *        Incluye métricas detalladas, porcentaje de progreso y errores
     *        acumulados.
     * @param jobId identificador único del job de importación
     * @return estado completo del job con métricas y errores, o 404 si no se
     *         encuentra
     */
    @GetMapping("/import/status/{jobId}")
    public ResponseEntity<ImportJobStatus> getImportStatus(@PathVariable String jobId) {
        return importThirdUseCase.getImportStatus(jobId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * @brief Elimina un tercero del sistema
     *
     *        Endpoint para eliminación lógica de un tercero, validando que no tenga
     *        dependencias activas antes de proceder con la eliminación.
     * @param thirdId identificador único del tercero a eliminar
     * @param entId   identificador de la empresa
     * @return resultado de la operación de eliminación
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteThird(
            @NotNull(message = "thirdId es requerido") @RequestParam Long thirdId,
            @NotNull(message = "entId es requerido") @RequestParam String entId) {

        boolean deleted = deleteThirdUseCase.deleteThird(thirdId, entId);

        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    /**
     * @brief Obtiene una lista de terceros filtrados por nombre de tipo activo con
     *        paginación inteligente.
     *
     *        Endpoint que lista terceros filtrados por un tipo específico de
     *        tercero activo,
     *        con paginación inteligente, ordenamiento por defecto ASC y búsqueda
     *        case insensitive.
     *
     * @param entId         Id de la empresa
     * @param thirdTypeName Nombre del tipo de tercero activo para filtrar (case
     *                      insensitive)
     * @param numPage       Número de página (opcional)
     * @param size          Tamaño de página (opcional)
     * @return Respuesta con la lista de terceros filtrados por tipo activo
     */
    @GetMapping("/by-type")
    public ResponseEntity<Page<Third>> getThirdsByType(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @NotNull(message = "thirdTypeName es requerido") @RequestParam String thirdTypeName,
            @RequestParam(required = false) Optional<Integer> numPage,
            @RequestParam(required = false) Optional<Integer> size) {

        long totalRecords = listThirdsUseCase.countThirdsByEntIdAndThirdTypeName(entId, thirdTypeName);

        Pageable pageable = PaginationHelper.createFlexiblePageable(numPage, size, totalRecords);
        Page<Third> page = listThirdsUseCase.getThirdsByEntIdAndThirdTypeName(entId, thirdTypeName,
                pageable.getPageNumber(), pageable.getPageSize());

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

}
