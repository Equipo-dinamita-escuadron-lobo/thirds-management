package com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence;
import java.util.List;

import com.thirdsmanagement.thirds.application.ports.output.IdOutputPort;
import com.thirdsmanagement.thirds.domain.model.ThirdType;
import com.thirdsmanagement.thirds.domain.model.TypeId;
import com.thirdsmanagement.thirds.domain.utils.StringNormalizer;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdAlreadyExists;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdInvalidDataException;
import com.thirdsmanagement.thirds.domain.exceptions.typeId.TypeIdNameAlreadyExistsException;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.ThirdTypeEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.entity.TypeIdEntity;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.mapper.IdPersistenceMapper;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.ThirdTypeRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.repository.TypeIdRepository;
import com.thirdsmanagement.thirds.infrastructure.adapters.output.persistence.multitenancy.utils.TenantContext;

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
    
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final ThirdRepository thirdRepository;
    private final ThirdTypeRepository thirdTypeRepository;
    private final TypeIdRepository typeIdRepository;
    private final IdPersistenceMapper idPersistenceMapper;
    

    /**
     * Guarda un tipo de tercero.
     * @param thirdType Tipo de tercero a guardar.
     * @return Tipo de tercero guardado.
     */
    @Override
    public ThirdType saveThirdType(ThirdType thirdType) {
        ThirdTypeEntity thirdTypeEntity = idPersistenceMapper.toThirdTypeEntity(thirdType);

        // Asignar tenant ID del contexto actual
        thirdTypeEntity.setTenantId(TenantContext.getTenantId());

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
        if (typeId == null) {
            throw new TypeIdInvalidDataException("El tipo de identificación no puede ser null");
        }

        if (typeId.getTypeId() == null || typeId.getTypeId().trim().isEmpty()) {
            throw new TypeIdInvalidDataException("El código del tipo de identificación no puede estar vacío");
        }

        if (typeId.getTypeIdname() == null || typeId.getTypeIdname().trim().isEmpty()) {
            throw new TypeIdInvalidDataException("El nombre del tipo de identificación no puede estar vacío");
        }

        // Normalizar valores para validaciones
        String normalizedTypeId = StringNormalizer.normalizeCode(typeId.getTypeId());
        String normalizedTypeIdName = StringNormalizer.normalizePreservingCase(typeId.getTypeIdname());
        
        // Validar que no exista un typeId duplicado
        if (typeIdRepository.existsByTiIdAndTientId(normalizedTypeId, typeId.getEntId())) {
            throw new TypeIdAlreadyExists("Ya existe un tipo de identificación con el código '" + normalizedTypeId + "'");
        }

        // Validar que no exista un typeIdname similar (case-insensitive) usando el nombre normalizado
        if (typeIdRepository.existsByTiNameIgnoreCaseAndTientId(normalizedTypeIdName, typeId.getEntId())) {
            throw new TypeIdNameAlreadyExistsException(typeId.getTypeIdname());
        }
        
        TypeId normalizedTypeIdModel = TypeId.builder()
                .typeId(normalizedTypeId)
                .typeIdname(normalizedTypeIdName)
                .entId(typeId.getEntId())
                .build();

        TypeIdEntity typeIdEntity = idPersistenceMapper.toTypeIdEntity(normalizedTypeIdModel);

        // Asignar tenant ID del contexto actual
        typeIdEntity.setTenantId(TenantContext.getTenantId());

        if (typeIdEntity.getTiId() == null) {
            typeIdEntity.setTiId(normalizedTypeId);
        }

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
