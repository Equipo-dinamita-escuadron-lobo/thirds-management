package com.thirdsmanagement.thirds.domain.validation;

import com.thirdsmanagement.thirds.domain.exceptions.third.FileValidationException;
import com.thirdsmanagement.thirds.infrastructure.config.FileUploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Validador específico para archivos Excel.
 * Implementa validaciones de tamaño, extensión y tipo MIME para archivos Excel.
 */
@Component
@RequiredArgsConstructor
public class ExcelFileValidator implements FileValidator {
    
    private final FileUploadProperties fileProperties;
    
    @Override
    public void validate(MultipartFile file) {
        validateNotNull(file);
        validateNotEmpty(file);
        validateSize(file);
        validateExtension(file);
    }
    
    private void validateNotNull(MultipartFile file) {
        if (file == null) {
            throw FileValidationException.forNullFile();
        }
    }
    
    private void validateNotEmpty(MultipartFile file) {
        if (file.isEmpty() || file.getSize() == 0) {
            throw FileValidationException.forEmptyFile(file.getOriginalFilename());
        }
    }
    
    private void validateSize(MultipartFile file) {
        if (file.getSize() > fileProperties.getMaxSize()) {
            throw FileValidationException.forSizeExceeded(
                file.getOriginalFilename(), 
                file.getSize(), 
                fileProperties.getMaxSize()
            );
        }
    }
    
    private void validateExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw FileValidationException.forInvalidExtension("archivo sin nombre", getSupportedExtensions());
        }
        
        String lowerFilename = filename.toLowerCase();
        boolean validExtension = false;
        
        for (String ext : getSupportedExtensions()) {
            if (lowerFilename.endsWith(ext)) {
                validExtension = true;
                break;
            }
        }
        
        if (!validExtension) {
            throw FileValidationException.forInvalidExtension(filename, getSupportedExtensions());
        }
    }
    
    @Override
    public String[] getSupportedMimeTypes() {
        List<String> mimeTypes = fileProperties.getAllowedMimeTypes().get("excel");
        return mimeTypes != null ? mimeTypes.toArray(new String[0]) : new String[0];
    }
    
    @Override
    public String[] getSupportedExtensions() {
        List<String> extensions = fileProperties.getAllowedExtensions().get("excel");
        return extensions != null ? extensions.toArray(new String[0]) : new String[0];
    }
}
