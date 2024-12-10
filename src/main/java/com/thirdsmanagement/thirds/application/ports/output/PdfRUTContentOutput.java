package com.thirdsmanagement.thirds.application.ports.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa la salida que contiene el contenido extraído de un archivo PDF
 * correspondiente a un RUT (Registro Único Tributario).
 * 
 * <p>Esta clase se utiliza para encapsular el contenido textual extraído 
 * de un archivo PDF después de ser procesado. Incluye anotaciones de Lombok 
 * para reducir el código repetitivo.</p>
 * 
 * <ul>
 * <li>{@code @Builder} - Permite utilizar el patrón builder para crear instancias de esta clase.</li>
 * <li>{@code @Getter} - Genera los métodos getter para todos los campos.</li>
 * <li>{@code @Setter} - Genera los métodos setter para todos los campos.</li>
 * <li>{@code @AllArgsConstructor} - Genera un constructor con parámetros para todos los campos.</li>
 * <li>{@code @NoArgsConstructor} - Genera un constructor sin argumentos.</li>
 * </ul>
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PdfRUTContentOutput {
    /**
     * El contenido textual extraído del archivo PDF.
     * 
     * Este campo contiene el texto en bruto extraído del PDF, que puede incluir 
     * información como el RUT, el nombre de la empresa, la dirección u otros datos 
     * relevantes, dependiendo de la estructura del PDF.
     */
    private String content;
}
