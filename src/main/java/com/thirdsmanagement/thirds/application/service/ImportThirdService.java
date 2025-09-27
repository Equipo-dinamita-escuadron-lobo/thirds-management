package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.ImportThirdUseCase;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdImportRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdImportResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Esta clase se mantiene para compatibilidad, pero delega toda la lógica
 * al nuevo orquestador que sigue principios SOLID.
 * 
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Deprecated
public class ImportThirdService implements ImportThirdUseCase {

    private final ImportOrchestrator importOrchestrator;
    
    @Override
    public ThirdImportResponse importThirdsFromExcel(ThirdImportRequest importRequest) {
        return importOrchestrator.importThirdsFromExcel(importRequest);
    }

}
