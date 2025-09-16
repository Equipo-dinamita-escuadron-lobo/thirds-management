package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.thirdsmanagement.thirds.application.ports.input.ChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ExportThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.GetThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ListThirdsUseCase;
import com.thirdsmanagement.thirds.application.ports.input.PdfRUTContent;
import com.thirdsmanagement.thirds.application.ports.output.PdfRUTContentOutput;
import com.thirdsmanagement.thirds.application.service.CreateThirdService;
import com.thirdsmanagement.thirds.application.service.PdfRUTService;
import com.thirdsmanagement.thirds.application.service.UpdateThirdService;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdExportRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ChangeThirdStateResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.ThirdRestMapper;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
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
    private final ExportThirdUseCase exportThirdUseCase;
    private final ThirdRestMapper thirdRestMapper;
    private final PdfRUTService pdfRUTService;
    private final CreateThirdService createThirdService;
    private final UpdateThirdService updateThirdService;

    /**
     * Crea un tercero.
     * @param thirdCreateRequest Datos del tercero a crear.
     * @return Respuesta con los datos del tercero creado.
     */
    @PostMapping("/")
    public ResponseEntity<ThirdResponse> createThird(@RequestBody @Valid ThirdCreateRequest thirdCreateRequest) {

        Third third = thirdRestMapper.toThird(thirdCreateRequest);

        // Use geography validation service for proper geography integration
        third = createThirdService.createThirdWithGeography(third, 
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
     * @return Respuesta con el resultado de la operación.
     */
    @PutMapping("/")
    public ResponseEntity<ChangeThirdStateResponse> changeThirdState(
            @NotNull(message = "Third ID not be empty") @RequestParam("thId") Long thId) {
        Boolean result = changeThirdStateUseCase.changeThirdState(thId);

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
            @NotNull(message = "Third Id not be empty") @RequestParam("thId") Long thId,
            @NotNull(message = "Enterprise Id not be empty") @RequestParam("entId") String entId) {

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
            @NotNull(message = "ID Number not be empty") @RequestParam("idNumber") Long idNumber,
            @NotNull(message = "Third Id not be empty") @RequestParam("entId") String entId) {

        boolean exists = getThirdUseCase.existThirdById(idNumber, entId);

        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    /**
     * Obtiene una lista de terceros.
     * @param entId Id de la empresa.
     * @param numPage Número de página.
     * @param size Tamaño de página (opcional, por defecto 30).
     * @return Respuesta con la lista de terceros.
     */
    @GetMapping("/")
    public ResponseEntity<Page<Third>> getThirdsList(
            @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId,
            @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage,
            @RequestParam(value = "size", defaultValue = "30") int size) {

        Pageable pageable = PageRequest.of(numPage, size);

        Page<Third> page = listThirdsUseCase.getAllThirdsBy(entId, pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);

    }

    /**
     * Obtiene una lista de terceros filtrados por estado.
     * @param entId Id de la empresa.
     * @param numPage Número de página.
     * @param isActive Estado del tercero (true para activos, false para inactivos).
     * @return Respuesta con la lista de terceros filtrados por estado.
     */
    @GetMapping("/inactive")
    public ResponseEntity<Page<Third>> getThirdsByStatus(
            @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId,
            @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage,
            @NotNull(message = "Status parameter not be empty") @RequestParam("isActive") boolean isActive) {

        Pageable pageable = PageRequest.of(numPage, 10);

        Page<Third> page = listThirdsUseCase.getAllThirdsByStatus(entId, pageable, isActive);

        return new ResponseEntity<>(page, HttpStatus.OK);

    }

    /**
     * Obtiene una lista de terceros filtrados por tipo de tercero.
     * @param entId Id de la empresa.
     * @param numPage Número de página.
     * @param thirdType Tipo de tercero (ej: "Proveedor", "Cliente").
     * @return Respuesta con la lista de terceros filtrados por tipo.
     */
    @GetMapping("/by-type")
    public ResponseEntity<Page<Third>> getThirdsByType(
            @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId,
            @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage,
            @NotNull(message = "Third type not be empty") @RequestParam("thirdType") String thirdType) {

        Pageable pageable = PageRequest.of(numPage, 10);

        Page<Third> page = listThirdsUseCase.getAllThirdsByType(entId, pageable, thirdType);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    /**
     * Cargar un archivo PDF y extraer su contenido RUT.
     * @param file el archivo PDF que se va a cargar.
     * @return ResponseEntity con el contenido extraído del PDF en caso de éxito,
     *         o un ResponseEntity con un estado de error en caso de fallo.
     */
    @PostMapping("/content-PDF-RUT")
    public ResponseEntity<PdfRUTContentOutput> uploadPdf(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        PdfRUTContent request = new PdfRUTContent(file);
        PdfRUTContentOutput response = pdfRUTService.extractContent(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene una lista de terceros.
     * @param entId Id de la empresa.
     * @return Respuesta con la lista de terceros.
     */
    @GetMapping("/list")
        public ResponseEntity<List<Third>> getAllThirds(
                @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId) {

            List<Third> thirds = listThirdsUseCase.getAllThirds(entId);

            return new ResponseEntity<>(thirds, HttpStatus.OK);
        }

    /**
     * Exporta terceros en formato Excel (.xlsx).
     * @param entId ID de la empresa.
     * @return Archivo Excel con los terceros exportados.
     */
    @GetMapping("/export/excel")
    public ResponseEntity<Resource> exportThirdsToExcel(
            @NotNull(message = "Enterprise ID no puede estar vacio") @RequestParam("entId") String entId) {
        
        // Crear request con solo entId requerido
        ThirdExportRequest exportRequest = ThirdExportRequest.builder()
                .entId(entId)
                .includeTypes(true)
                .includeCities(true)
                .build();
        
        Resource excelFile = exportThirdUseCase.exportThirdsToExcel(exportRequest);
        
        String filename = "Informacion_Terceros.xlsx";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelFile);
    }

    /**
     * Exporta una plantilla de terceros con validaciones de datos.
     */
    @GetMapping("/template/excel")
    public ResponseEntity<Resource> exportThirdTemplate(
            @RequestParam("entId") String entId) {
                
        Resource templateFile = exportThirdUseCase.exportThirdTemplateWithValidations(entId);
        
        // Generar nombre de archivo con timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "Plantilla_Terceros_" + timestamp + ".xlsx";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(templateFile);
    }


}
