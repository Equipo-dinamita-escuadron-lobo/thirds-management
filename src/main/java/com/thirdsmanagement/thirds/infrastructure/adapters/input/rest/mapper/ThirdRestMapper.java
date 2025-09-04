package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ChangeThirdStateResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.GetThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdsEnterpriseListResponse;

/**
 * Interfaz Mapper para mapear los objetos de entrada y salida de los terceros.
 */
@Mapper(componentModel = "spring")
public interface ThirdRestMapper {
    /**
     * Método para mapear un objeto de tipo {@link ThirdCreateRequest} a un objeto de tipo {@link Third}.
     * @param thirdCreateRequest
     * @return Objeto de tipo {@link Third}.
     */
    Third toThird(ThirdCreateRequest thirdCreateRequest);
    
    /**
     * Método para mapear un objeto de tipo {@link Third} a un objeto de tipo {@link ThirdResponse}.
     * @param third
     * @return Objeto de tipo {@link ThirdResponse}.
     */
    @Mapping(source = "thId", target = "id")
    @Mapping(source = "names", target = "name")
    @Mapping(source = "state", target = "description")
    ThirdResponse toThirdCreateResponse(Third third);

    /**
     * Método para mapear un objeto de tipo {@link Boolean} a un objeto de tipo {@link ChangeThirdStateResponse}.
     * @param result
     * @return Objeto de tipo {@link ChangeThirdStateResponse}.
     */
    ChangeThirdStateResponse toChangeThirdStateResponse(Boolean result);

    /**
     * Método para mapear un objeto de tipo {@link Page<Third>} a un objeto de tipo {@link ThirdsEnterpriseListResponse}.
     * @param page
     * @return Objeto de tipo {@link ThirdsEnterpriseListResponse}.
     */
    @Mapping(source = "page", target = "results")
    ThirdsEnterpriseListResponse toListThirdsResponse(Page<Third> page);

    /**
     * Método para mapear un objeto de tipo {@link Third} a un objeto de tipo {@link GetThirdResponse}.
     * @param third
     * @return Objeto de tipo {@link GetThirdResponse}.
     */
    GetThirdResponse toGetThirdResponse(Third third);

    /**
     * Convierte una lista de entidades de tercero ({@link Third}) en una lista de respuestas de tercero ({@link GetThirdResponse}).
     *
     * @param thirdList la lista de entidades de tercero.
     * @return la lista de respuestas de tercero convertida.
     */
    java.util.List<GetThirdResponse> toGetThirdResponseList(java.util.List<Third> thirdList);
}
