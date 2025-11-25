package com.thirdsmanagement.thirds.application.service.third;

import com.thirdsmanagement.thirds.application.ports.input.DeleteThirdUseCase;
import com.thirdsmanagement.thirds.application.ports.output.ThirdOutputPort;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdInUseException;
import com.thirdsmanagement.thirds.domain.exceptions.third.ThirdNotFound;
import com.thirdsmanagement.thirds.domain.model.Third;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @brief Servicio para eliminar terceros
 *
 * Implementa las validaciones de negocio necesarias antes de la eliminación.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteThirdService implements DeleteThirdUseCase {

    private final ThirdOutputPort thirdOutputPort;

  
    @Override
    @Transactional
    public boolean deleteThird(Long thirdId, String entId) {
        // Verificar que el tercero existe (validación de negocio)
        validateThirdExists(thirdId, entId);

        // Validar que el tercero no tenga movimientos contables registrados
        Third existingThird = thirdOutputPort.getThirdById(thirdId, entId)
                .orElseThrow(() -> new ThirdNotFound("El tercero con ID " + thirdId + " no existe"));
        if (existingThird.isInUse()) {
            throw new ThirdInUseException(existingThird.getIdNumber().toString(), false); // false indica operación de eliminación
        }

        // Proceder con la eliminación directa (el tercero es la entidad raíz)
        return thirdOutputPort.deleteThird(thirdId, entId);
    }


    private void validateThirdExists(Long thirdId, String entId) {
        if (!thirdOutputPort.existThirdById(thirdId, entId)) {
            throw new ThirdNotFound("El tercero con ID " + thirdId + " no existe para la empresa " + entId);
        }
    }

}
