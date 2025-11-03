package com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.thirdsmanagement.thirds.domain.model.City;
import com.thirdsmanagement.thirds.domain.model.Country;
import com.thirdsmanagement.thirds.domain.model.State;
import com.thirdsmanagement.thirds.domain.model.Third;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdCreateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.request.ThirdUpdateRequest;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ChangeThirdStateResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.GetThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ThirdResponse;
import com.thirdsmanagement.thirds.infrastructure.adapters.input.rest.dto.response.ThirdsEnterpriseListResponse;

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
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "province", ignore = true) 
    @Mapping(target = "city", ignore = true)
    Third toThird(ThirdCreateRequest thirdCreateRequest);
    
    /**
     * Método para mapear un objeto de tipo {@link ThirdUpdateRequest} a un objeto de tipo {@link Third}.
     * @param thirdUpdateRequest
     * @return Objeto de tipo {@link Third}.
     */
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "province", ignore = true)
    @Mapping(target = "city", ignore = true)
    Third toThird(ThirdUpdateRequest thirdUpdateRequest);
    
    /**
     * Método para mapear un objeto de tipo {@link Third} a un objeto de tipo {@link ThirdResponse}.
     * @param third
     * @return Objeto de tipo {@link ThirdResponse}.
     */
    @Mapping(source = "thId", target = "id")
    @Mapping(target = "name", expression = "java(getThirdName(third))")
    @Mapping(target = "description", expression = "java(getThirdDescription(third))")
    ThirdResponse toThirdCreateResponse(Third third);
    
    /**
     * Obtiene el nombre completo del tercero según su tipo de persona.
     * @param third el tercero
     * @return el nombre completo
     */
    default String getThirdName(Third third) {
        if (third == null) {
            return null;
        }
        
        if (third.isLegalEntity()) {
            // Para personas jurídicas, usar la razón social
            return third.getSocialReason();
        } else {
            // Para personas naturales, concatenar nombres y apellidos
            StringBuilder name = new StringBuilder();
            if (third.getNames() != null && !third.getNames().trim().isEmpty()) {
                name.append(third.getNames());
            }
            if (third.getLastNames() != null && !third.getLastNames().trim().isEmpty()) {
                if (name.length() > 0) {
                    name.append(" ");
                }
                name.append(third.getLastNames());
            }
            return name.length() > 0 ? name.toString() : null;
        }
    }
    
    /**
     * Obtiene la descripción del tercero con información útil.
     * @param third el tercero
     * @return la descripción
     */
    default String getThirdDescription(Third third) {
        if (third == null) {
            return null;
        }
        
        StringBuilder description = new StringBuilder();
        
        // Tipo de persona
        if (third.getPersonType() != null) {
            description.append("Persona ").append(third.getPersonType().name());
        }
        
        // Tipo de identificación y número
        if (third.getTypeId() != null && third.getTypeId().getTypeIdname() != null) {
            if (description.length() > 0) {
                description.append(" - ");
            }
            description.append(third.getTypeId().getTypeIdname())
                      .append(": ").append(third.getIdNumber());
        }
        
        // Estado
        if (description.length() > 0) {
            description.append(" - ");
        }
        description.append("Estado: ").append(third.isActive() ? "Activo" : "Inactivo");
        
        return description.toString();
    }

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
    @Mapping(target = "country", expression = "java(mapCountryToString(third.getCountry()))")
    @Mapping(target = "province", expression = "java(mapStateToString(third.getProvince()))")
    @Mapping(target = "city", expression = "java(mapCityToString(third.getCity()))")
    GetThirdResponse toGetThirdResponse(Third third);

    /**
     * Convierte una lista de entidades de tercero ({@link Third}) en una lista de respuestas de tercero ({@link GetThirdResponse}).
     *
     * @param thirdList la lista de entidades de tercero.
     * @return la lista de respuestas de tercero convertida.
     */
    java.util.List<GetThirdResponse> toGetThirdResponseList(java.util.List<Third> thirdList);
    
    /**
     * Mapea un objeto Country a su código de país como String.
     * @param country el objeto Country
     * @return el código del país o null si el país es null
     */
    default String mapCountryToString(Country country) {
        return country != null ? country.getCountryCode() : null;
    }
    
    /**
     * Mapea un objeto State a su código de estado como String.
     * @param state el objeto State
     * @return el código del estado o null si el estado es null
     */
    default String mapStateToString(State state) {
        return state != null ? state.getStateCode() : null;
    }
    
    /**
     * Mapea un objeto City a su código de ciudad como String.
     * @param city el objeto City
     * @return el código de la ciudad o null si la ciudad es null
     */
    default String mapCityToString(City city) {
        return city != null ? city.getCityCode() : null;
    }
}
