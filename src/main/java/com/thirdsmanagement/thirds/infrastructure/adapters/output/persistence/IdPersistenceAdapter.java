package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence;
import java.util.List;

import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.IdPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Clase adaptador de persistencia para la entidad Id.
 * Implementa la interfaz {@link IdOutputPort}.
 * Utiliza {@link ThirdRepository}, {@link ThirdTypeRepository} y {@link TypeIdRepository} para las operaciones de persistencia.
 * Utiliza {@link IdPersistenceMapper} para mapear las entidades y los modelos.
 * Proporciona métodos para guardar y obtener los tipos de terceros y los tipos de identificación.
 */
@Component
@RequiredArgsConstructor
public class IdPersistenceAdapter implements IdOutputPort {
    /**
     * Entity manager.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Repositorio de terceros.
     */
    private final ThirdRepository thirdRepository;

    /**
     * Repositorio de tipos de terceros.
     */
    private final ThirdTypeRepository thirdTypeRepository;

    /**
     * Repositorio de tipos de identificación.
     */
    private final TypeIdRepository typeIdRepository;

    /**
     * Mapeador de persistencia de Id.
     */
    private final IdPersistenceMapper idPersistenceMapper;

    /**
     * Guarda un tipo de tercero.
     * @param thirdType Tipo de tercero a guardar.
     * @return Tipo de tercero guardado.
     */
    @Override
    public ThirdType saveThirdType(ThirdType thirdType) {
        ThirdTypeEntity thirdTypeEntity = idPersistenceMapper.toThirdTypeEntity(thirdType);
        thirdTypeRepository.save(thirdTypeEntity);
        ThirdType result = idPersistenceMapper.toThirdType(thirdTypeEntity);
        return result;
    }

    /**
     * Obtiene todos los tipos de terceros.
     * @param entId Id de la entidad.
     * @return Lista de tipos de terceros.
     */
    @Override
    public List<ThirdType> getALLThirdTypes(String entId) {
        return idPersistenceMapper.toThirdTypeList(thirdTypeRepository.findAllByTtentId(entId));
    }

    /**
     * Guarda un tipo de identificación.
     * @param typeId Tipo de identificación a guardar.
     * @return Tipo de identificación guardado.
     */
    @Override
    public TypeId saveTypeId(TypeId typeId) {
       TypeIdEntity typeIdEntity = idPersistenceMapper.toTypeIdEntity(typeId);
       typeIdRepository.save(typeIdEntity);
       TypeId result = idPersistenceMapper.toTypeId(typeIdEntity);
       return result;
    }

    /**
     * Obtiene todos los tipos de identificación.
     * @param entId Id de la entidad.
     * @return Lista de tipos de identificación.
     */
    @Override
    public List<TypeId> getAllTypeIds(String entId) {
        return idPersistenceMapper.toTypeIdList(typeIdRepository.findAllByTientId(entId));
    }
    
}
