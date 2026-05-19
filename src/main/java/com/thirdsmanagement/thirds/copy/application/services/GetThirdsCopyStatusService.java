package com.thirdsmanagement.thirds.copy.application.services;

import com.thirdsmanagement.thirds.copy.application.input.IGetThirdsCopyStatusPort;
import com.thirdsmanagement.thirds.copy.application.output.ICopyJobLogRepositoryPort;
import com.thirdsmanagement.thirds.copy.domain.exceptions.DuplicateCopyJobException;
import com.thirdsmanagement.thirds.copy.domain.models.CopyJobLog;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyStatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación para consultar el estado de un proceso de copia de terceros.
 */
@Service
@RequiredArgsConstructor
public class GetThirdsCopyStatusService implements IGetThirdsCopyStatusPort {

    private final ICopyJobLogRepositoryPort logRepo;

    /**
     * Obtiene el estado actual del proceso de copia más reciente.
     *
     * @param idProceso identificador UUID del proceso
     * @return estado del proceso
     */
    @Override
    public CopyStatusResponseDto obtenerEstado(String idProceso) {
        CopyJobLog log = logRepo.buscarPorIdProceso(idProceso)
                .orElseThrow(() -> new DuplicateCopyJobException(idProceso));

        return CopyStatusResponseDto.builder()
                .idProceso(idProceso)
                .estado(log.getEstado().name())
                .fase(log.getFase())
                .modulo(log.getModulo())
                .registrosProcesados(log.getRegistrosProcesados())
                .equivalenciasGeneradas(log.getEquivalenciasGeneradas())
                .build();
    }
}
