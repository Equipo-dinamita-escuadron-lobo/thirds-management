package com.thirdsmanagement.thirds.application.service;

import com.thirdsmanagement.thirds.application.ports.input.GetThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.exception.ThirdNotFound;
import com.thirdsmanagement.thirds.domain.model.Third;

import lombok.AllArgsConstructor;

/**
 * Clase de servicio para obtener un tercero por su ID.
 * Implementa la interfaz {@link GetThirdUseCase}.
 * Utiliza {@link ThirdOutputPort} para las operaciones de persistencia.
 * Este servicio proporciona un método para obtener un tercero por su ID.
 * Si el tercero no existe, lanza una excepción de tipo {@link ThirdNotFound}.
 * 
 * @see GetThirdUseCase
 * @see ThirdOutputPort
 */
@AllArgsConstructor
public class GetThirdService implements GetThirdUseCase{

    private final ThirdOutputPort thirdOutputPort;

    @Override
    public Third getThirdById(Long id) {
        return thirdOutputPort.getThirdById(id).orElseThrow(()-> new ThirdNotFound("Third not found with id " + id));
    }
    @Override
    public boolean existThirdById(long id, String entId) {
        return thirdOutputPort.existThirdById(id, entId);
    }
}
