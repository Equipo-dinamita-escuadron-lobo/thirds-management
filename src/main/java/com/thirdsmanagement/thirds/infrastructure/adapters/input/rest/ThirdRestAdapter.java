package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.thirdsmanagement.thirds.application.ports.input.ChangeThirdStateUseCase;
import com.thirdsmanagement.thirds.application.ports.input.CreateThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.GetThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.input.ListThirdsUseCase;
import com.thirdsmanagement.thirds.application.ports.input.PdfRUTContent;
import com.thirdsmanagement.thirds.application.ports.input.UpdateThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.PdfRUTContentOutput;
import com.thirdsmanagement.thirds.application.service.PdfRUTService;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ChangeThirdStateResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.ThirdRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador REST para la gestión de terceros.
 * Este controlador expone endpoints para la creación, actualización, inactivación y listado de terceros.
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api/thirds")
@RequiredArgsConstructor
// @PreAuthorize("hasRole('admin_client') or hasRole('super_client')")
public class ThirdRestAdapter {

    private final CreateThirdUseCase createThirdUseCase;
    private final ListThirdsUseCase listThirdsUseCase;
    private final GetThirdUseCase getThirdUseCase;
    private final ChangeThirdStateUseCase changeThirdStateUseCase;
    private final UpdateThirdUseCase updateThirdUseCase;

    private final ThirdRestMapper thirdRestMapper;

    /**
     * Crea un tercero.
     * @param thirdCreateRequest Datos del tercero a crear.
     * @return Respuesta con los datos del tercero creado.
     */
    @Operation(summary = "Crear Tercero", description = "Crea Un Tercero Recibiendo Todos Los Campos Obligatorios De Este", responses = {
            @ApiResponse(responseCode = "201", description = "Tercero creado exitosamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos del tercero no válidos o incompletos", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/")
    public ResponseEntity<ThirdResponse> createThird(@RequestBody @Valid ThirdCreateRequest thirdCreateRequest) {

        Third third = thirdRestMapper.toThird(thirdCreateRequest);

        third = createThirdUseCase.createThird(third);

        return new ResponseEntity<>(thirdRestMapper.toThirdCreateResponse(third), HttpStatus.CREATED);
    }

    /**
     * Actualiza un tercero.
     * @param thirdCreateRequest Datos del tercero a actualizar.
     * @return Respuesta con los datos del tercero actualizado.
     */
    @Operation(summary = "Actualiza Un Tercero", description = "Actualiza Un Tercero Recibiendo Todos Los Campos Obligatorios De Este", responses = {
            @ApiResponse(responseCode = "200", description = "Tercero actualizado exitosamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos del tercero no válidos o incompletos", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "El tercero no fue encontrado", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/update")
    public ResponseEntity<ThirdResponse> updateThird(@RequestBody @Valid ThirdCreateRequest thirdCreateRequest) {

        Third third = thirdRestMapper.toThird(thirdCreateRequest);

        third = updateThirdUseCase.updateThird(third);

        return new ResponseEntity<>(thirdRestMapper.toThirdCreateResponse(third), HttpStatus.OK);
    }

    /**
     * Cambia el estado de un tercero.
     * @param thId ID del tercero.
     * @return Respuesta con el resultado de la operación.
     */
    @Operation(summary = "Cambia El Estado De Un Tercero", description = "Cambia el estado de un tercero en función de su ID.", responses = {
            @ApiResponse(responseCode = "200", description = "Estado del tercero cambiado exitosamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "El ID del tercero no puede estar vacío o es inválido", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Tercero no encontrado con el ID proporcionado", content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/")
    public ResponseEntity<ChangeThirdStateResponse> changeThirdState(
            @NotNull(message = "Third ID not be empty") @RequestParam("thId") Long thId) {
        Boolean result = changeThirdStateUseCase.changeThirdState(thId);

        return new ResponseEntity<>(thirdRestMapper.toChangeThirdStateResponse(result), HttpStatus.OK);
    }

    /**
     * Obtiene un tercero.
     * @param thId ID del tercero.
     * @return Respuesta con los datos del tercero.
     */
    @Operation(summary = "Obtiene Un Tercero", description = "Obtiene un tercero a partir de su ID.", responses = {
            @ApiResponse(responseCode = "200", description = "Tercero encontrado exitosamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "El ID del tercero no puede estar vacío o es inválido", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Tercero no encontrado con el ID proporcionado", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/third")
    public ResponseEntity<Third> getThirdById(
            @NotNull(message = "Third Id not be empty") @RequestParam("thId") Long thId) {

        Third third = getThirdUseCase.getThirdById(thId);

        return new ResponseEntity<>(third, HttpStatus.OK);
    }

    /**
     * Verifica si existe un tercero.
     * @param idNumber ID del tercero.
     * @param entId ID de la empresa.
     * @return Respuesta con el resultado de la verificación.
     */
    @Operation(summary = "Verifica Si Existe Un Tercero", description = "Verifica si existe un tercero utilizando el ID del tercero y el ID de la empresa (entId).", responses = {
            @ApiResponse(responseCode = "200", description = "Verificación exitosa. Retorna true si el tercero existe, false si no.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Uno o ambos parámetros (idNumber, entId) son inválidos o vacíos.", content = @Content(mediaType = "application/json"))
    })
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
     * @return Respuesta con la lista de terceros.
     */
    @Operation(summary = "Obtiene Una Lista de Terceros", description = "Obtiene una lista de terceros asociados a una empresa, con paginación basada en el número de página.", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de terceros obtenida exitosamente.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de entrada inválidos o vacíos.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/")
    public ResponseEntity<Page<Third>> getThirdsList(
            @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId,
            @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage) {

        Pageable pageable = PageRequest.of(numPage, 100);

        Page<Third> page = listThirdsUseCase.getAllThirdsBy(entId, pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);

    }

    /**
     * Obtiene una lista de terceros inactivos.
     * @param entId Id de la empresa.
     * @param numPage Número de página.
     * @return Respuesta con la lista de terceros inactivos.
     */
    @Operation(summary = "Inactiva Un Tercero", description = "Obtiene una lista de terceros inactivos asociados a una empresa, con paginación basada en el número de página.", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de terceros inactivos obtenida exitosamente.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de entrada inválidos o vacíos.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/inactive")
    public ResponseEntity<Page<Third>> getInactiveThirdsList(
            @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId,
            @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage) {

        Pageable pageable = PageRequest.of(numPage, 10);

        Page<Third> page = listThirdsUseCase.getAllInactiveThirdsBy(entId, pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);

    }

    /**
     * Obtiene una lista de terceros que son proveedores.
     * @param entId Id de la empresa.
     * @param numPage Número de página.
     * @return Respuesta con la lista de proveedores.
     */
    @Operation(summary = "Obtiene Una Lista de Terceros Que Son Proveedores", description = "Obtiene una lista de terceros que son proveedores asociados a una empresa, con paginación basada en el número de página.", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida exitosamente.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de entrada inválidos o vacíos.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/providers")
    public ResponseEntity<Page<Third>> getProvidersList(
            @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId,
            @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage) {

        Pageable pageable = PageRequest.of(numPage, 10);

        Page<Third> page = listThirdsUseCase.getAllProvidersBy(entId, pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    /**
     * Obtiene una lista de terceros que son clientes.
     * @param entId Id de la empresa.
     * @param numPage Número de página.
     * @return Respuesta con la lista de clientes.
     */
    @Operation(summary = "Obtiene Una Lista de Clientes", description = "Obtiene una lista de terceros que son clientes asociados a una empresa, con paginación basada en el número de página.", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida exitosamente.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de entrada inválidos o vacíos.", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/customers")
    public ResponseEntity<Page<Third>> getCustomersList(
            @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId,
            @NotNull(message = "Number page not be empty") @RequestParam("numPage") int numPage) {

        Pageable pageable = PageRequest.of(numPage, 10);

        Page<Third> page = listThirdsUseCase.getAllCustomersBy(entId, pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Autowired
    private PdfRUTService pdfRUTService;

    /**
     * Cargar un archivo PDF y extraer su contenido RUT.
     * @param file el archivo PDF que se va a cargar.
     * @return ResponseEntity con el contenido extraído del PDF en caso de éxito,
     *         o un ResponseEntity con un estado de error en caso de fallo.
     */
    @Operation(
        summary = "Carga un archivo PDF y extrae su contenido",
        description = "Permite cargar un archivo PDF (RUT) y extraer su contenido.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Contenido del PDF extraído exitosamente.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PdfRUTContentOutput.class))),
            @ApiResponse(responseCode = "400", description = "El archivo está vacío o es inválido.", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar el archivo.", content = @Content(mediaType = "application/json"))
        }
    )
    @PostMapping("/content-PDF-RUT")
    public ResponseEntity<PdfRUTContentOutput> uploadPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            PdfRUTContent request = new PdfRUTContent(file);
            PdfRUTContentOutput response = pdfRUTService.extractContent(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Obtiene una lista de terceros.
     * @param entId Id de la empresa.
     * @return Respuesta con la lista de terceros.
     */
    @Operation(
        summary = "Obtiene Una Lista de Terceros",
        description = "Obtiene Una Lista de Terceros, se debe mandar el Id de la empresa",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de terceros obtenida exitosamente", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Third.class)))),
            @ApiResponse(responseCode = "400", description = "El ID de la empresa no puede estar vacío", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No se encontraron terceros para la empresa proporcionada", content = @Content(mediaType = "application/json"))
        }
    )
    @GetMapping("/list")
        public ResponseEntity<List<Third>> getAllThirds(
                @NotNull(message = "Enterprise ID not be empty") @RequestParam("entId") String entId) {

            List<Third> thirds = listThirdsUseCase.getAllThirds(entId);

            return new ResponseEntity<>(thirds, HttpStatus.OK);
        }

}
