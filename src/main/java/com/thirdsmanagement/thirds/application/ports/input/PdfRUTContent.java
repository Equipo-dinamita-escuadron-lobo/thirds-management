package com.thirdsmanagement.thirds.application.ports.input;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa el contenido de un archivo PDF que contiene el RUT de un tercero.
 * Esta clase se utiliza para manejar cargas de archivos a través de un {@link MultipartFile}. 
 * Incluye anotaciones de Lombok para generar constructores, getters y setters, y un constructor sin argumentos.
 * 
 * <ul>
 * <li>{@code @Builder} - Proporciona una API fluida para construir instancias de esta clase.</li>
 * <li>{@code @Getter} - Genera métodos getter para todos los campos.</li>
 * <li>{@code @Setter} - Genera métodos setter para todos los campos.</li>
 * <li>{@code @AllArgsConstructor} - Genera un constructor con parámetros para todos los campos.</li>
 * <li>{@code @NoArgsConstructor} - Genera un constructor sin argumentos.</li>
 * </UL>
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PdfRUTContent {
    /**
     * El archivo PDF cargado que contiene el RUT.
     * Este archivo se maneja como un {@link MultipartFile}.
     */
    private MultipartFile file ;
}
