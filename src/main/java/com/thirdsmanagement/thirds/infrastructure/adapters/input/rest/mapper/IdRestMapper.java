package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper;

import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdTypeCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.ThirdTypeUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.TypeIdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.request.TypeIdUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.data.response.ThirdTypeResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interfaz Mapper para mapear los objetos de entrada y salida de los tipos de terceros y los tipos de identificación.
 * Utiliza MapStruct para generar automáticamente las implementaciones de mapeo.
 */
@Mapper(componentModel = "spring")
public interface IdRestMapper {
    /**
     * Método para mapear un objeto de tipo {@link ThirdType} a un objeto de tipo {@link ThirdTypeCreateRequest}.
     * @param thirdType Objeto de tipo {@link ThirdType}.
     * @return Objeto de tipo {@link ThirdTypeCreateRequest}.
     */
    ThirdTypeCreateRequest toThirdTypeCreateRequest(ThirdType thirdType);

    /**
     * Método para mapear un objeto de tipo {@link ThirdTypeCreateRequest} a un objeto de tipo {@link ThirdType}.
     * @param thirdTypeCreateRequest Objeto de tipo {@link ThirdTypeCreateRequest}.
     * @return Objeto de tipo {@link ThirdType}.
     */
    @Mapping(target = "status", defaultValue = "true")
    ThirdType toThirdType(ThirdTypeCreateRequest thirdTypeCreateRequest);

    /**
     * Método para mapear un objeto de tipo {@link TypeId} a un objeto de tipo {@link TypeIdCreateRequest}.
     * @param typeId Objeto de tipo {@link TypeId}.
     * @return Objeto de tipo {@link TypeIdCreateRequest}.
     */
    TypeIdCreateRequest toTypeIdCreateRequest(TypeId typeId);

    /**
     * Método para mapear un objeto de tipo {@link TypeIdCreateRequest} a un objeto de tipo {@link TypeId}.
     * @param typeIdCreateRequest Objeto de tipo {@link TypeIdCreateRequest}.
     * @return Objeto de tipo {@link TypeId}.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "typeIdname", source = "typeIdname")
    @Mapping(target = "entId", source = "entId")
    @Mapping(target = "typeId", source = "typeId")
    @Mapping(target = "classification", source = "classification")
    TypeId toTypeId(TypeIdCreateRequest typeIdCreateRequest);

    /**
     * Método para mapear un objeto de tipo {@link TypeIdUpdateRequest} a un objeto de tipo {@link TypeId}.
     * @param typeIdUpdateRequest Objeto de tipo {@link TypeIdUpdateRequest}.
     * @return Objeto de tipo {@link TypeId}.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "typeIdname", source = "typeIdname")
    @Mapping(target = "entId", source = "entId")
    @Mapping(target = "typeId", source = "typeId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "classification", source = "classification")
    TypeId toTypeId(TypeIdUpdateRequest typeIdUpdateRequest);

    /**
     * Método para mapear un objeto de tipo {@link ThirdTypeUpdateRequest} a un objeto de tipo {@link ThirdType}.
     * @param thirdTypeUpdateRequest Objeto de tipo {@link ThirdTypeUpdateRequest}.
     * @return Objeto de tipo {@link ThirdType}.
     */
    @Mapping(target = "thirdTypeName", source = "thirdTypeName")
    @Mapping(target = "entId", source = "entId")
    @Mapping(target = "thirdTypeId", source = "thirdTypeId")
    @Mapping(target = "status", source = "status")
    ThirdType toThirdType(ThirdTypeUpdateRequest thirdTypeUpdateRequest);

    /**
     * Método para mapear un objeto de tipo {@link ThirdType} a un objeto de tipo {@link ThirdTypeResponse}.
     * @param thirdType Objeto de tipo {@link ThirdType}.
     * @return Objeto de tipo {@link ThirdTypeResponse}.
     */
    ThirdTypeResponse toThirdTypeResponse(ThirdType thirdType);

    /**
     * Método para mapear una lista de objetos de tipo {@link ThirdType} a una lista de objetos de tipo {@link ThirdTypeResponse}.
     * @param thirdTypes Lista de objetos de tipo {@link ThirdType}.
     * @return Lista de objetos de tipo {@link ThirdTypeResponse}.
     */
    List<ThirdTypeResponse> toThirdTypeResponseList(List<ThirdType> thirdTypes);
} 