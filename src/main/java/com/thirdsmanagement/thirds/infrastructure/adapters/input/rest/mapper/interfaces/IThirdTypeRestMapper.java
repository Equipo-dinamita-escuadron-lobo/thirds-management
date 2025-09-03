package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper.interfaces;

import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdTypeCreateRequest;

import java.util.List;

import org.mapstruct.Mapper;

/**
 * Interfaz que define métodos para mapear entre entidades de tipo de tercero y sus representaciones REST.
 */
@Mapper(componentModel = "spring")
public interface IThirdTypeRestMapper {

    /**
     * Convierte una solicitud de creación de tipo de tercero ({@link ThirdTypeCreateRequest}) en una entidad de tipo de tercero ({@link ThirdType}).
     *
     * @param thirdTypeCreateRequest la solicitud de creación de tipo de tercero.
     * @return la entidad de tipo de tercero convertida.
     */
    ThirdType toThirdType(ThirdTypeCreateRequest thirdTypeCreateRequest);

    /**
     * Convierte una entidad de tipo de tercero ({@link ThirdType}) en una respuesta de tipo de tercero ({@link ThirdType}).
     *
     * @param thirdType la entidad de tipo de tercero.
     * @return la respuesta de tipo de tercero convertida.
     */
    ThirdType toThirdTypeResponse(ThirdType thirdType);

    /**
     * Convierte una lista de entidades de tipo de tercero ({@link ThirdType}) en una lista de respuestas de tipo de tercero ({@link ThirdType}).
     *
     * @param thirdTypeList la lista de entidades de tipo de tercero.
     * @return la lista de respuestas de tipo de tercero convertida.
     */
    List<ThirdType> toThirdTypeResponseList(List<ThirdType> thirdTypeList);
}
