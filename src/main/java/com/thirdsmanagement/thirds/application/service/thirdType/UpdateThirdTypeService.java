package com.thirdsmanagement.thirds.application.service.thirdType;

import com.thirdsmanagement.thirds.application.ports.input.UpdateThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @brief Servicio para la actualización de tipos de tercero
 *
 * Implementa el caso de uso UpdateThirdTypeUseCase.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateThirdTypeService implements UpdateThirdTypeUseCase {

    private final IdOutputPort idOutputPort;

    @Override
    public ThirdType updateThirdType(ThirdType thirdType) {
        ThirdType updatedThirdType = idOutputPort.updateThirdType(thirdType);

        return updatedThirdType;
    }
}
