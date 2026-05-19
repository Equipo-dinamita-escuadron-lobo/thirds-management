package com.thirdsmanagement.thirds.copy.application.services;

import com.thirdsmanagement.thirds.copy.application.input.ICancelThirdsCopyPort;
import com.thirdsmanagement.thirds.copy.application.output.ICopyJobLogRepositoryPort;
import com.thirdsmanagement.thirds.copy.domain.enums.CopyEstado;
import com.thirdsmanagement.thirds.copy.domain.exceptions.DuplicateCopyJobException;
import com.thirdsmanagement.thirds.copy.domain.models.CopyJobLog;
import com.thirdsmanagement.thirds.copy.infrastructure.adapters.input.rest.dto.CopyCancelResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio de aplicación para cancelar un proceso de copia de terceros en curso.
 */
@Service
@RequiredArgsConstructor
public class CancelThirdsCopyService implements ICancelThirdsCopyPort {

    private final ICopyJobLogRepositoryPort logRepo;

    /**
     * Cancela el proceso de copia marcando su estado como CANCELADO.
     *
     * @param idProceso identificador UUID del proceso
     * @return respuesta con nuevo estado CANCELADO
     */
    @Override
    public CopyCancelResponseDto cancelar(String idProceso) {
        CopyJobLog log = logRepo.buscarPorIdProceso(idProceso)
                .orElseThrow(() -> new DuplicateCopyJobException(idProceso));

        log.setEstado(CopyEstado.CANCELADO);
        logRepo.guardar(log);

        return CopyCancelResponseDto.builder()
                .idProceso(idProceso)
                .estado("CANCELADO")
                .mensaje("Proceso de copia cancelado")
                .build();
    }
}
