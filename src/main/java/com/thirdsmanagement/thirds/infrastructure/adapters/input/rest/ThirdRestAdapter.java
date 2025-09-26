package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

import com.thirdsmanagement.thirds.application.ports.input.ChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.input.DeleteThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ExportThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.GetThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ImportThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ListThirdsUseCase;
import com.thirdsmanagement.thirds.application.ports.input.PdfRUTContent;
import com.thirdsmanagement.thirds.application.ports.output.PdfRUTContentOutput;
import com.thirdsmanagement.thirds.application.service.CreateThirdService;
import com.thirdsmanagement.thirds.domain.utils.ExcelFileNameGenerator;
import com.thirdsmanagement.thirds.domain.utils.PaginationHelper;
import com.thirdsmanagement.thirds.application.service.PdfRUTService;
import com.thirdsmanagement.thirds.application.service.UpdateThirdService;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdExportRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdImportRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ChangeThirdStateResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdImportResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.ThirdRestMapper;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Controlador REST para la gestión de terceros.
 * Este controlador expone endpoints para la creación, actualización, inactivación y listado de terceros.
 */
@Slf4j
@RestController
@RequestMapping("/api/thirds")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('admin_client') or hasRole('super_client')")
public class ThirdRestAdapter {

    private final ListThirdsUseCase listThirdsUseCase;
    private final GetThirdUseCase getThirdUseCase;
    private final ChangeThirdStateUseCase changeThirdStateUseCase;
    private final DeleteThirdUseCase deleteThirdUseCase;
    private final ExportThirdUseCase exportThirdUseCase;
    private final ImportThirdUseCase importThirdUseCase;
    private final ThirdRestMapper thirdRestMapper;
    private final PdfRUTService pdfRUTService;
    private final CreateThirdService createThirdService;
    private final UpdateThirdService updateThirdService;
    private final ExcelFileNameGenerator fileNameGenerator;
    private final PaginationHelper paginationHelper;

    /**
     * Crea un tercero.
     * @param thirdCreateRequest Datos del tercero a crear.
     * @return Respuesta con los datos del tercero creado.
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
     * Actualiza un tercero.
     * @param thirdUpdateRequest Datos del tercero a actualizar.
     * @return Respuesta con los datos del tercero actualizado.
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
     * Cambia el estado de un tercero.
     * @param thId ID del tercero.
     * @param entId ID de la empresa.
     * @return Respuesta con el resultado de la operación.
     */
    @PutMapping("/")
    public ResponseEntity<ChangeThirdStateResponse> changeThirdState(
            @NotNull(message = "thId es requerido") @RequestParam Long thId,
            @NotNull(message = "entId es requerido") @RequestParam String entId) {
        Boolean result = changeThirdStateUseCase.changeThirdState(thId, entId);

        return new ResponseEntity<>(thirdRestMapper.toChangeThirdStateResponse(result), HttpStatus.OK);
    }

    /**
     * Obtiene un tercero por ID y empresa.
     * @param thId ID del tercero.
     * @param entId ID de la empresa.
     * @return Respuesta con los datos del tercero.
     */
    @GetMapping("/third")
    public ResponseEntity<Third> getThirdById(
            @NotNull(message = "thId es requerido") @RequestParam Long thId,
            @NotNull(message = "entId es requerido") @RequestParam String entId) {

        Third third = getThirdUseCase.getThirdById(thId, entId);

        return new ResponseEntity<>(third, HttpStatus.OK);
    }

    /**
     * Verifica si existe un tercero.
     * @param idNumber ID del tercero.
     * @param entId ID de la empresa.
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
     * Obtiene una lista de terceros con paginación flexible.
     * Si no se especifican parámetros de paginación, retorna todos los terceros.
     * @param entId Id de la empresa.
     * @param numPage Número de página (opcional).
     * @param size Tamaño de página (opcional).
     * @return Respuesta con la lista de terceros.
     */
    @GetMapping("/")
    public ResponseEntity<Page<Third>> getThirdsList(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @RequestParam(required = false) Optional<Integer> numPage,
            @RequestParam(required = false) Optional<Integer> size) {

        long totalRecords = listThirdsUseCase.countAllThirdsByEntId(entId);
        Pageable pageable = paginationHelper.createFlexiblePageable(numPage, size, totalRecords);

        Page<Third> page = listThirdsUseCase.getAllThirdsBy(entId, pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);

    }

    /**
     * Obtiene una lista de terceros filtrados por estado con paginación flexible.
     * Si no se especifican parámetros de paginación, retorna todos los terceros del estado especificado.
     * @param entId Id de la empresa.
     * @param numPage Número de página (opcional).
     * @param size Tamaño de página (opcional).
     * @param isActive Estado del tercero (true para activos, false para inactivos).
     * @return Respuesta con la lista de terceros filtrados por estado.
     */
    @GetMapping("/inactive")
    public ResponseEntity<Page<Third>> getThirdsByStatus(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @RequestParam(required = false) Optional<Integer> numPage,
            @RequestParam(required = false) Optional<Integer> size,
            @NotNull(message = "isActive es requerido") @RequestParam boolean isActive) {

        long totalRecords = listThirdsUseCase.countAllThirdsByStatus(entId, isActive);
        Pageable pageable = paginationHelper.createFlexiblePageable(numPage, size, totalRecords);

        Page<Third> page = listThirdsUseCase.getAllThirdsByStatus(entId, pageable, isActive);

        return new ResponseEntity<>(page, HttpStatus.OK);

    }

    /**
     * Obtiene una lista de terceros filtrados por tipo de tercero con paginación flexible.
     * Si no se especifican parámetros de paginación, retorna todos los terceros del tipo especificado.
     * @param entId Id de la empresa.
     * @param numPage Número de página (opcional).
     * @param size Tamaño de página (opcional).
     * @param thirdTypeId ID del tipo de tercero.
     * @return Respuesta con la lista de terceros filtrados por tipo.
     */
    @GetMapping("/by-type")
    public ResponseEntity<Page<Third>> getThirdsByType(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @RequestParam(required = false) Optional<Integer> numPage,
            @RequestParam(required = false) Optional<Integer> size,
            @NotNull(message = "thirdTypeId es requerido") @RequestParam Long thirdTypeId) {

        long totalRecords = listThirdsUseCase.countAllThirdsByType(entId, thirdTypeId);
        Pageable pageable = paginationHelper.createFlexiblePageable(numPage, size, totalRecords);

        Page<Third> page = listThirdsUseCase.getAllThirdsByType(entId, pageable, thirdTypeId);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    /**
     * Cargar un archivo PDF y extraer su contenido RUT.
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
     * Exporta una plantilla de terceros.
     */
    @GetMapping("/template/excel")
    public ResponseEntity<Resource> exportThirdTemplate(
            @RequestParam String entId) {
                
        Resource templateFile = exportThirdUseCase.exportThirdTemplateWithValidations(entId);
        String filename = fileNameGenerator.generateTemplateFileName();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(templateFile);
    }

    /**
     * Exporta terceros existentes.
     * Permite filtrar opcionalmente por ID de tipo de tercero, estado (activos/inactivos) e incluye toda la información.
     * El nombre de la empresa se puede incluir en el nombre del archivo.
     */
    @GetMapping("/export/excel")
    public ResponseEntity<Resource> exportThirdsWithValidations(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @RequestParam(required = false) Long thirdTypeId,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) String companyName) {
        
        ThirdExportRequest exportRequest = ThirdExportRequest.builder()
                .entId(entId)
                .thirdTypeId(thirdTypeId)
                .status(status)  // filtro por estado: true=activos, false=inactivos, null=todos
                .includeTypes(true)  // incluir tipos
                .includeCities(true) // incluir geografía
                .build();
        
        Resource excelFile = exportThirdUseCase.exportThirdsWithValidations(exportRequest);
        String filename = fileNameGenerator.generateExportFileName(entId, companyName, thirdTypeId);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelFile);
    }

    /**
     * Importa terceros masivamente desde un archivo Excel.
     * Procesa el archivo, valida datos y crea los terceros en el sistema.
     * 
     * Comportamiento:
     * - Los duplicados se omiten automáticamente (usabilidad)
     * - Se procesan archivos grandes sin límite de lote específico
     * - La importación se detiene al primer error crítico de validación
     * - Permite reimportar el mismo archivo omitiendo registros existentes
     * 
     * @param entId ID de la empresa
     * @param file Archivo Excel con los terceros a importar
     * @return Respuesta con estadísticas detalladas incluyendo duplicados omitidos
     */
    @PostMapping("/import/excel")
    public ResponseEntity<ThirdImportResponse> importThirdsFromExcel(
            @NotNull(message = "entId es requerido") @RequestParam String entId,
            @NotNull(message = "El archivo Excel es obligatorio") @RequestParam("file") MultipartFile file) {

        ThirdImportRequest importRequest = ThirdImportRequest.builder()
                .entId(entId)
                .excelFile(file)
                .fileName(file.getOriginalFilename())
                .build();

        ThirdImportResponse response = importThirdUseCase.importThirdsFromExcel(importRequest);

        // Determinar código de respuesta HTTP basado en el estado
        HttpStatus status = switch (response.getStatus()) {
            case COMPLETED -> HttpStatus.OK;
            case COMPLETED_WITH_ERRORS -> HttpStatus.ACCEPTED;
            case FAILED -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return new ResponseEntity<>(response, status);
    }


    /**
     * Elimina un tercero del sistema.
     * Valida que el tercero no tenga dependencias antes de eliminarlo.
     * @param thirdId ID del tercero a eliminar
     * @param entId ID de la empresa
     * @return Respuesta con el resultado de la eliminación
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteThird(
            @NotNull(message = "thirdId es requerido") @RequestParam Long thirdId,
            @NotNull(message = "entId es requerido") @RequestParam String entId) {
        
        boolean deleted = deleteThirdUseCase.deleteThird(thirdId, entId);
                
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

}
