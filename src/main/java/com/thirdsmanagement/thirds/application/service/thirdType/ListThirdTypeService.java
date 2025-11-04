package com.thirdsmanagement.thirds.application.service.thirdType;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.thirdsmanagement.thirds.application.ports.input.ListThirdTypeUseCase;
import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.ThirdType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListThirdTypeService implements ListThirdTypeUseCase {

    private final IdOutputPort idOutputPort;

    /**
     * @brief Obtiene todos los tipos de terceros para una empresa específica
     * @param entId identificador de la empresa
     * @return lista de tipos de terceros disponibles
     */
    @Override
    public List<ThirdType> getAllThirdTypes(String entId) {
        return idOutputPort.getALLThirdTypes(entId);
    }

    /**
     * @brief Obtiene todos los tipos de tercero con paginación y ordenamiento personalizado
     * @param entId identificador de la empresa
     * @param page número de página (0-based)
     * @param size tamaño de página
     * @param sortField campo de ordenamiento
     * @param sortOrder orden (ascendente/descendente)
     * @return página de tipos de tercero
     */
    @Override
    public Page<ThirdType> getAllThirdTypesWithSort(String entId, int page, int size, String sortField, String sortOrder) {
        return idOutputPort.getAllThirdTypesWithSort(entId, page, size, sortField, sortOrder);
    }

    /**
     * @brief Busca tipos de tercero por empresa y término de búsqueda
     * @param entId identificador de la empresa
     * @param search término de búsqueda
     * @param page número de página (0-based)
     * @param size tamaño de página
     * @param sortField campo de ordenamiento
     * @param sortOrder orden (ascendente/descendente)
     * @return página de tipos de tercero que coinciden con la búsqueda
     */
    @Override
    public Page<ThirdType> findThirdTypesByEntIdAndSearch(String entId, String search, int page, int size, String sortField, String sortOrder) {
        return idOutputPort.findThirdTypesByEntIdAndSearch(entId, search, page, size, sortField, sortOrder);
    }

    /**
     * @brief Cuenta tipos de tercero por empresa
     * @param entId identificador de la empresa
     * @return cantidad total de tipos de tercero
     */
    @Override
    public long countThirdTypesByEntId(String entId) {
        return idOutputPort.countThirdTypesByEntId(entId);
    }

    /**
     * @brief Cuenta tipos de tercero por empresa y término de búsqueda
     * @param entId identificador de la empresa
     * @param search término de búsqueda
     * @return cantidad de tipos de tercero que coinciden con la búsqueda
     */
    @Override
    public long countThirdTypesByEntIdAndSearch(String entId, String search) {
        return idOutputPort.countThirdTypesByEntIdAndSearch(entId, search);
    }
    
    /**
     * @brief Cuenta tipos de tercero activos por empresa
     * @param entId identificador de la empresa
     * @return cantidad de tipos de tercero activos
     */
    @Override
    public long countActiveThirdTypesByEntId(String entId) {
        return idOutputPort.countActiveThirdTypesByEntId(entId);
    }
    
    /**
     * @brief Obtiene todos los tipos de tercero activos con paginación
     * @param entId identificador de la empresa
     * @param page número de página (0-based)
     * @param size tamaño de página
     * @return página de tipos de tercero activos ordenados por nombre
     */
    @Override
    public Page<ThirdType> getAllActiveThirdTypes(String entId, int page, int size) {
        return idOutputPort.getAllActiveThirdTypes(entId, page, size);
    }

}
