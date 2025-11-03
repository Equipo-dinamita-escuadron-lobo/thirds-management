package com.thirdsmanagement.thirds.application.service.third;

import com.thirdsmanagement.thirds.application.ports.input.GetThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdNotFound;
import com.thirdsmanagement.thirds.domain.model.Third;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetThirdService implements GetThirdUseCase {

    private final ThirdOutputPort thirdOutputPort;

    /**
     * Obtiene un tercero por su ID y empresa.
     * 
     * @param id    el ID del tercero a buscar
     * @param entId el ID de la empresa
     * @return el tercero encontrado
     * @throws ThirdNotFound si el tercero no existe
     */
    @Override
    public Third getThirdById(Long id, String entId) {
        return thirdOutputPort.getThirdById(id, entId)
                .orElseThrow(
                        () -> new ThirdNotFound("Tercero no encontrado con ID: " + id + " para la empresa: " + entId));
    }

    /**
     * Verifica si existe un tercero por su ID en una empresa específica.
     * 
     * @param id    el ID del tercero a verificar
     * @param entId el ID de la empresa
     * @return true si el tercero existe, false en caso contrario
     */
    @Override
    public boolean existThirdById(long id, String entId) {
        return thirdOutputPort.existThirdById(id, entId);
    }
}
