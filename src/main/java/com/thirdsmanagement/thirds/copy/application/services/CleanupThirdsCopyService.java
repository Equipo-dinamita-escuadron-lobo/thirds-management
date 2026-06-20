package com.thirdsmanagement.thirds.copy.application.services;

import com.thirdsmanagement.thirds.copy.application.input.ICleanupThirdsCopyPort;
import com.thirdsmanagement.thirds.copy.application.output.ICopyJobLogRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación para limpiar los registros de log de un proceso de copia de terceros.
 */
@Service
@RequiredArgsConstructor
public class CleanupThirdsCopyService implements ICleanupThirdsCopyPort {

    private final ICopyJobLogRepositoryPort logRepo;

    /**
     * Elimina todos los registros de log asociados al proceso indicado.
     *
     * @param idProceso identificador UUID del proceso a limpiar
     */
    @Override
    public void limpiar(String idProceso) {
        logRepo.eliminarPorIdProceso(idProceso);
    }
}
