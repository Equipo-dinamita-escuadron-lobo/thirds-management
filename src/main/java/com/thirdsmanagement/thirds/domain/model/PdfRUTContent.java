package com.thirdsmanagement.thirds.domain.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @brief Representa el contenido de un archivo PDF que contiene el RUT de un tercero
 *
 * Esta clase se utiliza para manejar cargas de archivos PDF que contienen
 * certificados RUT (Registro Único Tributario) de la DIAN a través de MultipartFile.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PdfRUTContent {

    private MultipartFile file ;
}
